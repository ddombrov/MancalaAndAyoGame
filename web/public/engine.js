// Kalah and Ayo rules, shared by the browser and the Worker (a port of the Java KalahRules and AyoRules).
// The board has 14 slots, counter-clockwise: 0-5 are player 1's pits (bottom, left to right), 6 is
// player 1's store, 7-12 are player 2's pits (top, right to left) and 13 is player 2's store.

export const STONES_PER_PIT = 4;
export const MAX_NAME_LENGTH = 20;
export const RULE_NAMES = { kalah: "Kalah", ayo: "Ayo" };

export const storeOf = (player) => player * 7 + 6;
export const ownsPit = (player, slot) => slot >= player * 7 && slot < player * 7 + 6;
const opposite = (slot) => 12 - slot;

export const cleanName = (n) => (typeof n === "string" ? n.replace(/\s+/g, " ").trim().slice(0, MAX_NAME_LENGTH) : "");
export const sameName = (a, b) => a.trim().toLowerCase() === b.trim().toLowerCase();

export function createGame(rules, players) {
  const board = Array(14).fill(STONES_PER_PIT);
  board[6] = board[13] = 0;
  return { rules: rules === "ayo" ? "ayo" : "kalah", board, players: [...players], turn: 0, moves: 0,
           over: false, winner: null, last: null, message: `${players[0] || "Player 1"} moves first.` };
}

export const isLegal = (S, pit) => !S.over && Number.isInteger(pit) && ownsPit(S.turn, pit) && S.board[pit] > 0;
export const legalMoves = (S) => [0, 1, 2, 3, 4, 5].map((i) => i + S.turn * 7).filter((pit) => isLegal(S, pit));

// Plays one move. Returns { state, steps }, where steps describe every stone that moved, in order, for
// the animation: { t: "pick", slot, n }, { t: "drop", slot }, { t: "capture", slots, to } and
// { t: "sweep" } at the end of the game. Returns null for an illegal move.
export function play(S, pit) {
  if (!isLegal(S, pit)) return null;
  const board = [...S.board];
  const me = S.turn, them = 1 - me, myStore = storeOf(me);
  const steps = [];
  let captured = 0;

  const sow = (from) => {
    let hand = board[from];
    board[from] = 0;
    steps.push({ t: "pick", slot: from, n: hand });
    let slot = from;
    while (hand > 0) {
      slot = (slot + 1) % 14;
      if (slot === storeOf(them)) continue;
      if (S.rules === "ayo" && slot === pit) continue; // Ayo never sows back into the pit the move started from
      board[slot]++;
      hand--;
      steps.push({ t: "drop", slot });
    }
    return slot;
  };

  let end = sow(pit);
  let again = false;
  if (S.rules === "kalah") {
    if (end === myStore) again = true;
    else if (ownsPit(me, end) && board[end] === 1) {
      captured = board[end] + board[opposite(end)];
      steps.push({ t: "capture", slots: [end, opposite(end)], to: myStore });
      board[myStore] += captured;
      board[end] = board[opposite(end)] = 0;
    }
  } else {
    // Relay sowing: ending in a pit that already had stones picks them all up and carries on.
    for (let relays = 0; end !== myStore && board[end] > 1 && relays < 500; relays++) end = sow(end);
    if (end !== myStore && ownsPit(me, end) && board[end] === 1 && board[opposite(end)] > 0) {
      captured = board[opposite(end)];
      steps.push({ t: "capture", slots: [opposite(end)], to: myStore });
      board[myStore] += captured;
      board[opposite(end)] = 0;
    }
  }

  const name = S.players[me], other = S.players[them];
  const next = { ...S, board, moves: S.moves + 1, last: { pit, player: me }, turn: again ? me : them };
  next.message = captured ? `${name} captured ${captured}. ` : "";
  next.message += again ? `${name} goes again.` : `${other}'s turn.`;

  const sideEmpty = (p) => board.slice(p * 7, p * 7 + 6).every((n) => n === 0);
  if (sideEmpty(0) || sideEmpty(1)) {
    // Each player banks the stones left on their own side.
    for (const p of [0, 1]) for (let i = p * 7; i < p * 7 + 6; i++) { board[storeOf(p)] += board[i]; board[i] = 0; }
    steps.push({ t: "sweep" });
    next.over = true;
    const [a, b] = [board[6], board[13]];
    next.winner = a > b ? 0 : b > a ? 1 : null;
    next.message = next.winner === null ? `It's a draw, ${a} to ${b}.` : `${S.players[next.winner]} wins, ${Math.max(a, b)} to ${Math.min(a, b)}!`;
  }
  return { state: next, steps };
}

// ---------- the computer opponent ----------

const DEPTH = { easy: 1, medium: 4, hard: 8 };

function search(S, depth, alpha, beta, player) {
  if (S.over || depth === 0) return S.board[storeOf(player)] - S.board[storeOf(1 - player)];
  const maximising = S.turn === player;
  let best = maximising ? -Infinity : Infinity;
  for (const pit of legalMoves(S)) {
    const { state } = play(S, pit);
    // An extra turn does not use up depth, so the computer sees its whole chain of moves.
    const score = search(state, state.turn === S.turn && !state.over ? depth : depth - 1, alpha, beta, player);
    if (maximising) { best = Math.max(best, score); alpha = Math.max(alpha, score); }
    else { best = Math.min(best, score); beta = Math.min(beta, score); }
    if (beta <= alpha) break;
  }
  return best;
}

// The computer's move for the player whose turn it is. Easy also picks a random move a third of the time.
export function computerMove(S, level = "medium", random = Math.random) {
  const moves = legalMoves(S);
  if (level === "easy" && random() < 1 / 3) return moves[Math.floor(random() * moves.length)];
  const depth = DEPTH[level] ?? DEPTH.medium;
  let best = moves[0], bestScore = -Infinity;
  for (const pit of moves) {
    const { state } = play(S, pit);
    const score = search(state, state.turn === S.turn && !state.over ? depth : depth - 1, -Infinity, Infinity, S.turn);
    if (score > bestScore) { bestScore = score; best = pit; }
  }
  return best;
}
