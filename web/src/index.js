// The game server: it serves public/ and owns every online game. Browsers
// send moves, the Worker plays them with the same rules engine the browser uses, and only legal results
// are stored. Games on one device and against the computer stay in the browser's local storage.
//   POST   /api/rooms               { name, rules }    -> { code, seat: 0, token }
//   POST   /api/rooms/:code/join    { name }           -> { code, seat: 1, token }, or a "full" error with both names
//   POST   /api/rooms/:code/claim   { seat }           -> a new token for that seat, for a player who lost theirs
//   GET    /api/rooms/:code?token=                     -> the game as that seat sees it
//   POST   /api/rooms/:code/move    { token, rev, pit } -> the game after the move
//   POST   /api/rooms/:code/first   { token, player }   -> the host chooses who goes first
//   POST   /api/rooms/:code/resign  { token }           -> that seat resigns
//   DELETE /api/rooms/:code         { token }          -> the host cancels a game nobody has joined
//   POST   /api/quick { name }, GET /api/quick/:ticket, DELETE /api/quick/:ticket -> quick match
import { chooseFirst, cleanName, createGame, play, resign, sameName } from "../public/engine.js";

const WEEK = 7 * 24 * 60 * 60 * 1000;
const QUEUE_WAIT = 70 * 1000; // a waiting quick match ticket that stops polling is dropped after this

const json = (data, status = 200) =>
  new Response(JSON.stringify(data), { status, headers: { "Content-Type": "application/json", "Cache-Control": "no-store" } });
const fail = (status, reason, error, extra = {}) => json({ reason, error, ...extra }, status);
const hex = (bytes) => [...crypto.getRandomValues(new Uint8Array(bytes))].map((b) => b.toString(16).padStart(2, "0")).join("");

const getRoom = (db, code) => db.prepare("SELECT * FROM rooms WHERE code = ?").bind(code).first();
const seatOf = (row, token) => (token && row.seat0 === token ? 0 : token && row.seat1 && row.seat1 === token ? 1 : null);
const view = (row, seat, state = JSON.parse(row.state)) => ({ code: row.code, rev: row.rev, seat, seated: !!row.seat1, ...state });

async function createRoom(db, rules, names, paired) {
  for (let tries = 0; tries < 60; tries++) {
    const code = String(Math.floor(Math.random() * 100000)).padStart(5, "0");
    const tokens = [hex(16), paired ? hex(16) : null];
    const now = Date.now();
    const state = createGame(rules, [names[0], names[1] || ""]);
    try {
      await db.prepare("INSERT INTO rooms (code, rules, rev, state, seat0, seat1, active0, active1, created, last_move) VALUES (?, ?, 0, ?, ?, ?, ?, ?, ?, ?)")
        .bind(code, state.rules, JSON.stringify(state), tokens[0], tokens[1], now, paired ? now : 0, now, now).run();
      return { code, tokens };
    } catch (e) {
      if (!/UNIQUE|constraint/i.test(String(e?.message ?? e))) throw e;
    }
  }
  throw new Error("no free game code");
}

async function rooms(db, method, parts, url, body) {
  if (method === "POST" && parts.length === 2) {
    const name = cleanName(body.name);
    if (!name) return fail(400, "needname", "Choose a name to start.");
    const { code, tokens } = await createRoom(db, body.rules, [name], false);
    return json({ code, seat: 0, token: tokens[0] }, 201);
  }

  const code = parts[2];
  if (!/^\d{5}$/.test(code || "")) return fail(404, "notfound", "No game has that code.");
  const row = await getRoom(db, code);
  if (!row && method !== "DELETE") return fail(404, "notfound", "No game has that code.");

  if (method === "POST" && parts[3] === "join") {
    const S = JSON.parse(row.state);
    // A full game cannot be joined, but a player who lost their seat can take it back (see claim).
    if (row.seat1) return fail(409, "full", "That game is already full.", { names: S.players });
    const name = cleanName(body.name);
    if (!name) return fail(400, "needname", "Choose a name to join.");
    if (sameName(name, S.players[0])) return fail(409, "taken", "That name is taken, pick another.");
    const token = hex(16);
    S.players[1] = name;
    const res = await db.prepare("UPDATE rooms SET seat1 = ?, active1 = ?, state = ? WHERE code = ? AND seat1 IS NULL")
      .bind(token, Date.now(), JSON.stringify(S), code).run();
    if (res.meta.changes !== 1) return fail(409, "full", "That game is already full.", { names: S.players });
    return json({ code, seat: 1, token });
  }

  // Take over a seat in a full game. The old holder's token stops working, so it is whoever asked last.
  if (method === "POST" && parts[3] === "claim") {
    if (!row.seat1) return fail(409, "open", "That game is still waiting for a second player.");
    if (body.seat !== 0 && body.seat !== 1) return fail(400, "bad", "Pick a player.");
    const token = hex(16);
    await db.prepare(`UPDATE rooms SET seat${body.seat} = ?, active${body.seat} = ? WHERE code = ?`).bind(token, Date.now(), code).run();
    return json({ code, seat: body.seat, token });
  }

  if (method === "GET" && parts.length === 3) {
    const seat = seatOf(row, url.searchParams.get("token"));
    if (seat === null) return fail(403, "forbidden", "You are not in that game.");
    await db.prepare(`UPDATE rooms SET active${seat} = ? WHERE code = ?`).bind(Date.now(), code).run();
    return json(view(row, seat));
  }

  if (method === "POST" && parts[3] === "move") {
    const seat = seatOf(row, body.token);
    if (seat === null) return fail(403, "forbidden", "You are not in that game.");
    const S = JSON.parse(row.state);
    if (!row.seat1) return fail(409, "waiting", "Wait for the second player to join.", { game: view(row, seat, S) });
    if (body.rev !== row.rev) return fail(409, "stale", "The game moved on. Showing the latest board.", { game: view(row, seat, S) });
    if (S.turn !== seat) return fail(409, "turn", "It is not your turn.", { game: view(row, seat, S) });
    const result = play(S, body.pit);
    if (!result) return fail(409, "illegal", "That pit cannot be played.", { game: view(row, seat, S) });
    const now = Date.now();
    const res = await db.prepare(`UPDATE rooms SET state = ?, rev = rev + 1, last_move = ?, active${seat} = ? WHERE code = ? AND rev = ?`)
      .bind(JSON.stringify(result.state), now, now, code, row.rev).run();
    if (res.meta.changes !== 1) return fail(409, "stale", "The game moved on. Showing the latest board.", { game: view(row, seat, S) });
    await db.prepare("INSERT INTO moves (room, rev, seat, pit, at) VALUES (?, ?, ?, ?, ?)").bind(code, row.rev + 1, seat, body.pit, now).run();
    return json(view({ ...row, rev: row.rev + 1 }, seat, result.state));
  }

  if (method === "POST" && (parts[3] === "first" || parts[3] === "resign")) {
    const seat = seatOf(row, body.token);
    if (seat === null) return fail(403, "forbidden", "You are not in that game.");
    const S = JSON.parse(row.state);
    if (parts[3] === "first" && seat !== 0) return fail(403, "host", "Player 1 chooses who goes first.", { game: view(row, seat, S) });
    const next = parts[3] === "first" ? chooseFirst(S, body.player) : resign(S, seat);
    if (!next) return fail(409, "illegal", "That can't be done now.", { game: view(row, seat, S) });
    const now = Date.now();
    const res = await db.prepare("UPDATE rooms SET state = ?, rev = rev + 1, last_move = ? WHERE code = ? AND rev = ?")
      .bind(JSON.stringify(next), now, code, row.rev).run();
    if (res.meta.changes !== 1) return fail(409, "stale", "The game moved on. Showing the latest board.", { game: view(row, seat, S) });
    return json(view({ ...row, rev: row.rev + 1 }, seat, next));
  }

  if (method === "DELETE" && parts.length === 3) {
    if (!row) return json({ ok: true });
    if (seatOf(row, body.token) !== 0) return fail(403, "forbidden", "Only the host can cancel a game.");
    if (row.seat1) return fail(409, "started", "Someone has already joined.");
    await db.prepare("DELETE FROM rooms WHERE code = ?").bind(code).run();
    return json({ ok: true });
  }
  return fail(404, "notfound", "Not found.");
}

async function quick(db, method, parts, body) {
  if (method === "POST" && parts.length === 2) {
    const now = Date.now();
    await db.prepare("DELETE FROM queue WHERE code IS NULL AND at < ?").bind(now - QUEUE_WAIT).run();
    const ticket = hex(8);
    const name = cleanName(body.name) || "Player";
    const waiting = await db.prepare("SELECT ticket, name FROM queue WHERE code IS NULL ORDER BY at LIMIT 1").first();
    // Claim the waiting ticket so two newcomers cannot both pair with it.
    const claimed = waiting && (await db.prepare("UPDATE queue SET code = 'pending' WHERE ticket = ? AND code IS NULL").bind(waiting.ticket).run()).meta.changes === 1;
    if (claimed) {
      const second = sameName(name, waiting.name) ? `${name} (2)` : name;
      const { code, tokens } = await createRoom(db, "kalah", [waiting.name, second], true);
      await db.batch([
        db.prepare("UPDATE queue SET code = ?, seat = 0, token = ? WHERE ticket = ?").bind(code, tokens[0], waiting.ticket),
        db.prepare("INSERT INTO queue (ticket, at, code, seat, token) VALUES (?, ?, ?, 1, ?)").bind(ticket, now, code, tokens[1]),
      ]);
    } else {
      await db.prepare("INSERT INTO queue (ticket, at, name) VALUES (?, ?, ?)").bind(ticket, now, name).run();
    }
    return json({ ticket });
  }
  const ticket = parts[2];
  if (method === "GET" && ticket) {
    const row = await db.prepare("SELECT * FROM queue WHERE ticket = ?").bind(ticket).first();
    if (!row) return json({ gone: true });
    if (!row.code || row.code === "pending") {
      await db.prepare("UPDATE queue SET at = ? WHERE ticket = ? AND code IS NULL").bind(Date.now(), ticket).run();
      return json({ waiting: true });
    }
    await db.prepare("DELETE FROM queue WHERE ticket = ?").bind(ticket).run();
    return json({ code: row.code, seat: row.seat, token: row.token });
  }
  if (method === "DELETE" && ticket) {
    await db.prepare("DELETE FROM queue WHERE ticket = ? AND code IS NULL").bind(ticket).run();
    return json({ ok: true });
  }
  return fail(404, "notfound", "Not found.");
}

// The weekly job: delete games with no move for seven days, and old quick match tickets.
export async function cleanup(env, now = Date.now()) {
  await env.DB.prepare("DELETE FROM moves WHERE room IN (SELECT code FROM rooms WHERE last_move < ?)").bind(now - WEEK).run();
  await env.DB.prepare("DELETE FROM rooms WHERE last_move < ?").bind(now - WEEK).run();
  await env.DB.prepare("DELETE FROM queue WHERE at < ?").bind(now - 10 * 60 * 1000).run();
}

export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    const parts = url.pathname.split("/").filter(Boolean);
    if (parts[0] !== "api") return env.ASSETS.fetch(request);
    try {
      const body = ["POST", "DELETE"].includes(request.method) ? await request.json().catch(() => ({})) : {};
      if (parts[1] === "rooms") return await rooms(env.DB, request.method, parts, url, body);
      if (parts[1] === "quick") return await quick(env.DB, request.method, parts, body);
      return fail(404, "notfound", "Not found.");
    } catch (error) {
      console.error(error);
      return fail(500, "server", "Something went wrong. Try again.");
    }
  },
  async scheduled(event, env) {
    await cleanup(env);
  },
};
