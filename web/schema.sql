-- One row per online game, like Splendor Duel's. The 5-digit code is the primary key.
CREATE TABLE IF NOT EXISTS rooms (
  code TEXT PRIMARY KEY,
  rules TEXT NOT NULL,           -- kalah or ayo
  rev INTEGER NOT NULL,          -- goes up by one on every stored move
  state TEXT NOT NULL,           -- the whole game as JSON
  seat0 TEXT NOT NULL,           -- private token of the first seat
  seat1 TEXT,                    -- private token of the second seat, empty until someone joins
  active0 INTEGER NOT NULL,      -- when each seat was last seen, in milliseconds
  active1 INTEGER NOT NULL,
  created INTEGER NOT NULL,
  last_move INTEGER NOT NULL     -- the weekly cleanup deletes rooms where this is over 7 days old
);
CREATE INDEX IF NOT EXISTS rooms_last_move ON rooms (last_move);

-- Every move ever stored for a game, so games can be replayed later.
CREATE TABLE IF NOT EXISTS moves (
  room TEXT NOT NULL,
  rev INTEGER NOT NULL,
  seat INTEGER NOT NULL,
  pit INTEGER NOT NULL,
  at INTEGER NOT NULL,
  PRIMARY KEY (room, rev)
);

-- Quick match. A waiting ticket has no code. Once paired, both tickets hold their room, seat and token.
CREATE TABLE IF NOT EXISTS queue (
  ticket TEXT PRIMARY KEY,
  at INTEGER NOT NULL,
  code TEXT,
  seat INTEGER,
  token TEXT,
  name TEXT
);
