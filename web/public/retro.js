// The two looks from the original Java version, with the same set / highlight / animate calls as board.js.
// Slots: 0-5 player 1's pits, 6 player 1's store, 7-12 player 2's pits, 13 player 2's store.

// Classic: the unfinished Swing GUI, a 2 x 7 grid of plain buttons that show each pit's stone count.
export function createClassicBoard(root, { onPit }) {
  root.className = "swing";
  root.innerHTML = `<div class="swing-title">Mancala Game</div><div class="swing-grid"></div>`;
  const grid = root.querySelector(".swing-grid");
  // Top row: player 2's store, then their pits from left to right. Bottom row: player 1's pits, then their store.
  const order = [13, 12, 11, 10, 9, 8, 7, 0, 1, 2, 3, 4, 5, 6];
  const buttons = [];
  for (const slot of order) {
    const b = document.createElement("button");
    b.type = "button";
    b.disabled = true;
    if (slot !== 6 && slot !== 13) b.addEventListener("click", () => onPit(slot));
    grid.appendChild(b);
    buttons[slot] = b;
  }
  return {
    set(board) { board.forEach((n, slot) => { buttons[slot].textContent = n; }); },
    highlight(pits) { buttons.forEach((b, slot) => { if (slot !== 6 && slot !== 13) b.disabled = !pits.includes(slot); }); },
    async animate() {},
  };
}

// Text: the console version. It prints the board the way its toString did and reads a pit number,
// with pits 1-6 for player 1 and 7-12 for player 2.
export function createTextBoard(root, { onPit, game, canPlay }) {
  root.className = "console";
  root.innerHTML = `<pre class="console-log"></pre><label class="console-prompt"><span></span><input inputmode="numeric" autocomplete="off" spellcheck="false"></label>`;
  const log = root.querySelector(".console-log");
  const promptText = root.querySelector(".console-prompt span");
  const input = root.querySelector("input");
  const toSlot = (pit) => (pit <= 6 ? pit - 1 : pit);
  let printed = null, lastTurn = null;

  const print = (text) => {
    log.textContent += text + "\n";
    log.scrollTop = log.scrollHeight;
  };
  const boardText = (b) => {
    let s = "\n";
    for (let i = 1; i <= 6; i++) s += `Pit${i}: ${b[toSlot(i)]}\t\t`;
    s += `\nPlayer1 Store: ${b[6]}\t\t\t\t\t\tPlayer2 Store: ${b[13]}\n`;
    for (let i = 7; i <= 12; i++) s += `Pit${i}: ${b[toSlot(i)]}${i >= 9 ? "\t" : "\t\t"}`;
    return s;
  };

  input.addEventListener("keydown", (e) => {
    if (e.key !== "Enter") return;
    const text = input.value.trim();
    input.value = "";
    if (!canPlay()) return;
    const S = game();
    print(`${promptText.textContent}\n${text}`);
    const pit = /^-?\d+$/.test(text) ? Number(text) : NaN;
    const mine = S.turn === 0 ? pit >= 1 && pit <= 6 : pit >= 7 && pit <= 12;
    if (Number.isNaN(pit)) print("Invalid input. Please enter a number.");
    else if (!mine) print("Invalid pit number. Please try again.");
    else if (S.board[toSlot(pit)] === 0) print("The pit you chose is empty. Please choose another pit.");
    else onPit(toSlot(pit));
  });
  root.addEventListener("click", () => { if (!input.disabled && !getSelection().toString()) input.focus(); });

  return {
    set(board) {
      const S = game();
      const key = board.join(",") + "|" + S.turn;
      if (key === printed) return;
      if (printed !== null && S.turn === lastTurn && !S.over) print("another turn");
      printed = key;
      lastTurn = S.turn;
      print(boardText(board));
    },
    highlight() {
      const S = game();
      const playing = canPlay();
      promptText.textContent = S.over ? "" : playing ? `${S.players[S.turn]}, please enter the pit number:` : `Waiting for ${S.players[S.turn] || "a second player"}…`;
      input.disabled = !playing;
      if (playing) input.focus({ preventScroll: true });
    },
    async animate() {},
  };
}
