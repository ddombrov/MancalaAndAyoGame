// Draws the board from the artwork's layout (board.svg is 1200 x 440) and moves every stone on its own,
// so sowing, relays, captures and the final sweep can be animated stone by stone.
import { STONE_SETS } from "./app.js";

const W = 1200, H = 440, STONE = 34;
// Slots counter-clockwise: 0-5 player 1's pits (bottom, left to right), 6 player 1's store (right),
// 7-12 player 2's pits (top, right to left), 13 player 2's store (left).
const PIT_X = [266, 399.6, 533.2, 666.8, 800.4, 934];
export const SLOTS = [
  ...PIT_X.map((cx) => ({ cx, cy: 312, kind: "pit" })),
  { cx: 1098, cy: 220, kind: "store" },
  ...[...PIT_X].reverse().map((cx) => ({ cx, cy: 128, kind: "pit" })),
  { cx: 102, cy: 220, kind: "store" },
];

const pct = (v, total) => `${(v / total) * 100}%`;
const jitter = (id, k) => (((id * 37 + k * 11) % 9) - 4) * 1.2;

// Where the k-th stone in a slot sits: a spiral in a pit, a stack of rows in a store.
function spot(slot, k, id) {
  const s = SLOTS[slot];
  if (s.kind === "pit") {
    const r = Math.min(36, 10 * Math.sqrt(k));
    const a = k * 2.39996 + slot;
    return [s.cx + r * Math.cos(a) + jitter(id, 1), s.cy + r * Math.sin(a) + jitter(id, 2)];
  }
  const row = Math.floor(k / 3) % 12, col = k % 3, layer = Math.floor(k / 36);
  return [s.cx + (col - 1) * 30 + jitter(id, 3) + layer * 8, s.cy + 118 - row * 21 + jitter(id, 4) - layer * 6];
}

const wait = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

export function createBoard(root, { boardLook, stoneSet, onPit }) {
  root.className = `board board-${boardLook}`;
  root.innerHTML = `<img class="art" src="/img/board.svg" alt="" draggable="false">`;
  const counts = [];
  SLOTS.forEach((s, slot) => {
    const w = s.kind === "pit" ? 128 : 136, h = s.kind === "pit" ? 128 : 312;
    const hole = document.createElement(onPit && s.kind === "pit" ? "button" : "img");
    if (hole.tagName === "BUTTON") {
      hole.type = "button";
      hole.className = "hole pit-button";
      hole.innerHTML = `<img src="/img/pit.svg" alt="" draggable="false">`;
      hole.addEventListener("click", () => hole.classList.contains("playable") && onPit(slot));
      hole.setAttribute("aria-label", `Pit ${slot < 7 ? slot + 1 : slot - 6}`);
    } else {
      hole.className = "hole";
      hole.src = s.kind === "pit" ? "/img/pit.svg" : "/img/store.svg";
      hole.alt = "";
      hole.draggable = false;
    }
    Object.assign(hole.style, { left: pct(s.cx - w / 2, W), top: pct(s.cy - h / 2, H), width: pct(w, W), height: pct(h, H) });
    root.appendChild(hole);
    const count = document.createElement("span");
    count.className = `count count-${s.kind}`;
    const [x, y] = s.kind === "pit" ? [s.cx + 46, s.cy + (s.cy > H / 2 ? 46 : -46)] : [s.cx, s.cy + (slot === 6 ? 178 : -178)];
    Object.assign(count.style, { left: pct(x, W), top: pct(y, H) });
    root.appendChild(count);
    counts[slot] = count;
  });

  const colors = STONE_SETS[stoneSet];
  const stones = []; // one <img> per stone, by id
  for (let id = 0; id < 48; id++) {
    const img = document.createElement("img");
    img.className = "stone";
    img.alt = "";
    img.draggable = false;
    img.src = `/img/stones/stone-${colors[id % colors.length]}-${(id % 3) + 1}.svg`;
    img.style.width = pct(STONE, W);
    root.appendChild(img);
    stones.push(img);
  }
  let slots = Array.from({ length: 14 }, () => []);

  const place = (id, slot, k, lift = 0) => {
    const [x, y] = spot(slot, k, id);
    Object.assign(stones[id].style, { left: pct(x - STONE / 2, W), top: pct(y - STONE / 2 - lift, H) });
  };
  const layout = () => {
    slots.forEach((ids, slot) => ids.forEach((id, k) => place(id, slot, k)));
    slots.forEach((ids, slot) => { counts[slot].textContent = ids.length; });
  };

  return {
    // Puts the stones where a board array says, keeping each stone in its pit where the counts already match.
    set(board) {
      const same = slots.every((ids, slot) => ids.length === board[slot]);
      if (!same) {
        const pool = slots.flat();
        for (let id = 0; id < 48; id++) if (!pool.includes(id)) pool.push(id);
        slots = board.map((n) => pool.splice(0, n));
      }
      root.classList.add("instant");
      layout();
      void root.offsetWidth;
      root.classList.remove("instant");
    },

    // Shows which pits can be played.
    highlight(pits) {
      root.querySelectorAll(".pit-button").forEach((b, i) => {
        const slot = i < 6 ? i : i + 1;
        b.classList.toggle("playable", pits.includes(slot));
      });
      root.querySelectorAll(".pit-button").forEach((b) => (b.disabled = !b.classList.contains("playable")));
    },

    // Plays the engine's steps on screen. `speed` scales every pause.
    async animate(steps, speed = 1) {
      let hand = [];
      for (const step of steps) {
        if (step.t === "pick") {
          hand = slots[step.slot].splice(0);
          hand.forEach((id, k) => { stones[id].classList.add("moving"); place(id, step.slot, k, 18); });
          counts[step.slot].textContent = 0;
          await wait(260 * speed);
        } else if (step.t === "drop") {
          const id = hand.shift();
          slots[step.slot].push(id);
          place(id, step.slot, slots[step.slot].length - 1);
          counts[step.slot].textContent = slots[step.slot].length;
          setTimeout(() => stones[id].classList.remove("moving"), 300);
          await wait(220 * speed);
        } else if (step.t === "capture" || step.t === "sweep") {
          const from = step.t === "capture" ? step.slots : [0, 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 12];
          for (const slot of from) {
            const to = step.t === "capture" ? step.to : slot < 7 ? 6 : 13;
            for (const id of slots[slot].splice(0)) {
              slots[to].push(id);
              place(id, to, slots[to].length - 1);
            }
            counts[slot].textContent = 0;
            counts[to].textContent = slots[to].length;
          }
          await wait(520 * speed);
        }
      }
      hand.forEach((id) => stones[id].classList.remove("moving"));
    },
  };
}
