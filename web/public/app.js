// Things the home and game pages share: what this browser remembers, and the server connection.

export const $ = (id) => document.getElementById(id);

const read = (store, key) => { try { return store.getItem(key); } catch { return null; } };
const write = (store, key, value) => { try { value === null ? store.removeItem(key) : store.setItem(key, value); } catch { /* storage may be blocked */ } };

// The name this browser last played under, so it does not have to be typed again.
export const savedName = () => read(localStorage, "mancala:name") || "";
export const rememberName = (name) => { if (name && name.trim()) write(localStorage, "mancala:name", name.trim()); };

// A tab remembers its seat in each online game, so two tabs of one browser can be the two players.
export const readSeat = (code) => { try { return JSON.parse(read(sessionStorage, `mancala:seat:${code}`)); } catch { return null; } };
export const saveSeat = (code, seat, token) => write(sessionStorage, `mancala:seat:${code}`, JSON.stringify({ seat, token }));

// Games on this device and against the computer are kept in local storage, one slot per kind, like Splendor Duel's.
export function loadSave(mode) {
  try {
    const save = JSON.parse(read(localStorage, `mancala:save:${mode}`));
    return save && save.S && !save.S.over ? save : null;
  } catch { return null; }
}
export const writeSave = (mode, save) => write(localStorage, `mancala:save:${mode}`, JSON.stringify({ ...save, at: Date.now() }));
export const clearSave = (mode) => write(localStorage, `mancala:save:${mode}`, null);

// The look, chosen in Settings: the modern board, or the Java version's Swing grid or text console.
export const LOOKS = { modern: "Modern", classic: "Classic", text: "Text" };
export const BOARDS = { walnut: "Walnut", oak: "Oak", ebony: "Ebony" };
export const STONE_SETS = {
  mixed: ["amber", "blue", "green", "red", "purple", "teal", "white", "black"],
  gems: ["red", "blue", "green", "purple"],
  ocean: ["blue", "teal", "white"],
  amber: ["amber", "black", "white"],
};
export const STONE_SET_NAMES = { mixed: "Mixed", gems: "Gems", ocean: "Ocean", amber: "Amber" };
const pref = (key, options, fallback) => { const v = read(localStorage, key); return v in options ? v : fallback; };
export const getBoardLook = () => pref("mancala:board", BOARDS, "walnut");
export const setBoardLook = (v) => write(localStorage, "mancala:board", v);
export const getStoneSet = () => pref("mancala:stones", STONE_SETS, "mixed");
export const setStoneSet = (v) => write(localStorage, "mancala:stones", v);
export const getLook = () => pref("mancala:look", LOOKS, "modern");
export const setLook = (v) => write(localStorage, "mancala:look", v);

// Fills the Settings dialog, which the home and game pages both have. The board and stone choices only
// apply to the modern look. `onChange` runs after any choice.
export function showSettings(onChange = () => {}) {
  const choices = (container, options, get, set) => {
    container.innerHTML = "";
    for (const [value, label] of Object.entries(options)) {
      const b = document.createElement("button");
      b.type = "button";
      b.textContent = label;
      b.classList.toggle("on", value === get());
      b.addEventListener("click", () => { set(value); showSettings(onChange); onChange(); });
      container.appendChild(b);
    }
  };
  choices($("looks"), LOOKS, getLook, setLook);
  choices($("boardLooks"), BOARDS, getBoardLook, setBoardLook);
  choices($("stoneSets"), STONE_SET_NAMES, getStoneSet, setStoneSet);
  $("modernChoices").hidden = getLook() !== "modern";
  $("settingsDialog").hidden = false;
}

// Calls the Worker. Throws an Error carrying the server's message, reason, names and latest game.
export async function api(method, path, body) {
  const response = await fetch("/api" + path, {
    method,
    headers: body ? { "Content-Type": "application/json" } : {},
    body: body ? JSON.stringify(body) : undefined,
  });
  const data = await response.json().catch(() => ({}));
  if (!response.ok) throw Object.assign(new Error(data.error || "The server could not be reached."), data);
  return data;
}

export function celebrate() {
  if (typeof confetti === "function") {
    confetti({ particleCount: 160, spread: 85, origin: { y: 0.6 }, colors: ["#e7a93a", "#3f6fd8", "#3aa86b", "#d8283a", "#ffffff"] });
  }
}

export const RULES_HTML = `
  <h3>Both games</h3>
  <ul class="rules">
    <li>Each player has six pits on their side and a store on their right. Every pit starts with four stones.</li>
    <li>On your turn, pick up all the stones in one of your pits and sow them one by one, counter-clockwise, into the following pits and your own store. Your opponent's store is skipped.</li>
    <li>When either side runs out of stones, each player banks the stones left on their side. The bigger store wins.</li>
  </ul>
  <h3>Kalah</h3>
  <ul class="rules">
    <li>If your last stone lands in your store, you go again.</li>
    <li>If it lands in an empty pit on your side, you capture it and every stone in the pit opposite.</li>
  </ul>
  <h3>Ayo</h3>
  <ul class="rules">
    <li>Sowing skips the pit you started from. There are no extra turns.</li>
    <li>If your last stone lands in a pit that already had stones, pick them all up and keep sowing.</li>
    <li>If it lands in an empty pit on your side, you capture the stones in the pit opposite.</li>
  </ul>`;
