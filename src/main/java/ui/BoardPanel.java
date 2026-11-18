package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;
import mancala.AyoRules;
import mancala.GameRules;
import mancala.InvalidMoveException;
import mancala.KalahRules;
import mancala.MancalaDataStructure;
import mancala.MancalaGame;
import mancala.Player;

/**
 * The game page: a custom-painted Mancala board that animates every stone the game rules move.
 *
 * Slots 1-12 are pits, 13 is player 1's store (left) and 14 is player 2's store (right).
 * Player 1 owns the top row (pits 1-6) and player 2 the bottom row (pits 7-12), so stones
 * travel counter-clockwise, matching the text UI's layout.
 */
public class BoardPanel extends JPanel {
    private static final int STORE_1 = 13;
    private static final int STORE_2 = 14;

    private enum Overlay { NONE, LEAVE, GAME_OVER }

    private final List<List<Color>> slots = new ArrayList<>();
    private final Random random = new Random();
    private final Timer timer = new Timer(15, e -> tick());

    private final boolean kalah;
    private final String name1;
    private final String name2;
    private final Runnable onExit;
    private MancalaGame game;
    private Player player1;
    private Player player2;
    private int turn = 1;
    private boolean gameOver;
    private Overlay overlay = Overlay.NONE;
    private long overlayStart;
    private String resultTitle;
    private String resultScore;

    // Animation state: events recorded from the real rules are replayed one step at a time.
    private final Deque<int[]> events = new ArrayDeque<>();
    private final List<Color> hand = new ArrayList<>();
    private int handSlot = -1;
    private Step step;
    private Runnable afterPlayback;
    private double speed = 1;
    private boolean fastForward;
    private final List<Effect> ripples = new ArrayList<>();
    private final List<Effect> toasts = new ArrayList<>();

    // Layout, recomputed from the panel size.
    private final Point2D.Double[] centers = new Point2D.Double[15];
    private final RoundRectangle2D.Double[] stores = new RoundRectangle2D.Double[3];
    private RoundRectangle2D.Double board;
    private RoundRectangle2D.Double leaveButton;
    private RoundRectangle2D.Double card;
    private RoundRectangle2D.Double secondaryButton;
    private RoundRectangle2D.Double primaryButton;
    private double pitR;
    private double stoneR;
    private BufferedImage staticLayer;
    private int staticW;
    private int staticH;
    private double staticScale;

    private int hoverSlot = -1;
    private Shape hoverButton;

    /** A single animated move of the "hand" holding stones. */
    private static final class Step {
        final int from;
        final int to;
        final int drop;
        final boolean pickup;
        final double start;
        final double duration;

        Step(int from, int to, int drop, boolean pickup, double duration, double start) {
            this.from = from;
            this.to = to;
            this.drop = drop;
            this.pickup = pickup;
            this.duration = duration;
            this.start = start;
        }
    }

    /** A short-lived visual effect: a ripple on a slot or a floating message. */
    private static final class Effect {
        final int slot;
        final String text;
        final double x;
        final double y;
        final long start = System.currentTimeMillis();

        Effect(int slot, String text, double x, double y) {
            this.slot = slot;
            this.text = text;
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Creates the game page and starts a game.
     *
     * @param kalah  true for Kalah rules, false for Ayo
     * @param name1  player 1's name
     * @param name2  player 2's name
     * @param onExit called when the players go back to the home page
     */
    public BoardPanel(boolean kalah, String name1, String name2, Runnable onExit) {
        this.kalah = kalah;
        this.name1 = name1;
        this.name2 = name2;
        this.onExit = onExit;
        for (int i = 0; i <= STORE_2; i++) {
            slots.add(new ArrayList<>());
        }

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateHover(e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        startGame();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        timer.start();
    }

    @Override
    public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }

    // ---------------------------------------------------------------- game flow

    private void startGame() {
        GameRules rules = kalah ? new KalahRules() : new AyoRules();
        game = new MancalaGame(kalah ? 0 : 1);
        player1 = new Player(name1);
        player2 = new Player(name2);
        game.setPlayers(player1, player2);
        rules.getDataStructure().setUpPits();
        game.setBoard(rules);
        turn = 1;
        gameOver = false;
        overlay = Overlay.NONE;
        events.clear();
        hand.clear();
        step = null;
        afterPlayback = null;
        for (List<Color> slot : slots) {
            slot.clear();
        }
        syncDisplay();
    }

    private void play(int pitNumber) {
        Player currentPlayer = game.getCurrentPlayer();

        // Extra-turn detection mirrors TextUI so both front ends play identically.
        int p1orP2 = currentPlayer.equals(player1) ? 1 : 2;
        int playerOG = stones().getStoreCount(p1orP2);
        int sum = pitNumber - 1 + stones().getNumStones(pitNumber);
        int afterStore = (turn == 1) ? 7 : 6;
        int beforeStore = (turn == 1) ? 1 : 12;
        int afterStoreOG = stones().getNumStones(afterStore);
        int beforeStoreOG = stones().getNumStones(beforeStore);

        List<int[]> recorded = new ArrayList<>();
        stones().setStoneListener((slot, delta) -> recorded.add(new int[] {slot, delta}));
        try {
            game.move(pitNumber);
        } catch (InvalidMoveException ex) {
            return;
        } finally {
            stones().setStoneListener(null);
        }

        int storeIncrease = ((sum % 12) == 0) ? (sum / 12) : (sum / 12) + 1;
        int beforeStoreIncrease = (pitNumber == beforeStore) ? beforeStoreOG
                : stones().getNumStones(beforeStore) - beforeStoreOG;
        int afterStoreIncrease = stones().getNumStones(afterStore) - afterStoreOG;
        boolean newTurn = storeIncrease != 0 && storeIncrease > afterStoreIncrease
                && beforeStoreIncrease == storeIncrease;
        boolean extraTurn = stones().getStoreCount(p1orP2) == playerOG + storeIncrease && newTurn && kalah;

        if (!extraTurn) {
            game.setCurrentPlayer(currentPlayer == player1 ? player2 : player1);
            turn *= -1;
        }

        startPlayback(recorded, () -> {
            syncDisplay();
            if (game.isGameOver()) {
                endGame();
            } else if (extraTurn) {
                toast(currentPlayer.getName() + " goes again!", board.getCenterX(), board.getCenterY());
            }
        });
    }

    private void endGame() {
        gameOver = true;
        // Sweep each side's leftover stones into its owner's store, then score from the board.
        List<int[]> sweep = new ArrayList<>();
        for (int pit = 1; pit <= 12; pit++) {
            int count = slots.get(pit).size();
            if (count > 0) {
                sweep.add(new int[] {pit, -count});
                sweep.add(new int[] {pit <= 6 ? STORE_1 : STORE_2, count});
            }
        }
        startPlayback(sweep, () -> {
            int score1 = slots.get(STORE_1).size();
            int score2 = slots.get(STORE_2).size();
            resultTitle = score1 == score2 ? "It's a tie!" : (score1 > score2 ? name1 : name2) + " wins!";
            resultScore = name1 + "  " + score1 + "  -  " + score2 + "  " + name2;
            openOverlay(Overlay.GAME_OVER);
        });
    }

    /** Makes the drawn stones match the real game state. */
    private void syncDisplay() {
        for (int slot = 1; slot <= STORE_2; slot++) {
            int real = slot <= 12 ? stones().getNumStones(slot) : stones().getStoreCount(slot - 12);
            List<Color> drawn = slots.get(slot);
            while (drawn.size() < real) {
                drawn.add(Theme.STONE_COLORS[random.nextInt(Theme.STONE_COLORS.length)]);
            }
            while (drawn.size() > real) {
                drawn.remove(drawn.size() - 1);
            }
        }
    }

    private MancalaDataStructure stones() {
        return game.getBoard().getDataStructure();
    }

    private boolean animating() {
        return step != null || !events.isEmpty() || afterPlayback != null;
    }

    private boolean playable(int pit) {
        return overlay == Overlay.NONE && !gameOver && !animating()
                && (turn == 1 ? pit <= 6 : pit >= 7) && slots.get(pit).size() > 0;
    }

    private void openOverlay(Overlay which) {
        overlay = which;
        overlayStart = System.currentTimeMillis();
    }

    // ---------------------------------------------------------------- animation

    private void startPlayback(List<int[]> recorded, Runnable then) {
        events.addAll(recorded);
        afterPlayback = then;
        handSlot = -1;
        fastForward = false;
        speed = recorded.size() > 40 ? 0.5 : recorded.size() > 20 ? 0.75 : 1;
    }

    private void tick() {
        // On a slow display (such as the in-browser Java runtime) frames arrive late, so finish every step
        // whose time has already passed rather than one per frame. Each step starts when the previous one ended.
        double clock = System.currentTimeMillis();
        for (int guard = 0; guard < 200; guard++) {
            if (step != null && progress(step) >= 1) {
                clock = Math.min(System.currentTimeMillis(), step.start + effectiveDuration(step));
                completeStep();
            }
            while (step == null && !events.isEmpty()) {
                nextStep(clock);
            }
            if (step == null || progress(step) < 1) {
                break;
            }
        }
        if (step == null && events.isEmpty() && afterPlayback != null) {
            Runnable then = afterPlayback;
            afterPlayback = null;
            then.run();
        }
        long now = System.currentTimeMillis();
        ripples.removeIf(r -> now - r.start > 500);
        toasts.removeIf(t -> now - t.start > 1400);
        repaint();
    }

    private void nextStep(double start) {
        int[] event = events.peek();
        int slot = event[0];
        int delta = event[1];
        if (delta < 0) {
            if (!hand.isEmpty() && handSlot != slot) {
                // Carry what is already in hand over to the next pit before picking up.
                step = new Step(handSlot, slot, 0, false, flightTime(handSlot, slot), start);
                return;
            }
            events.poll();
            List<Color> source = slots.get(slot);
            for (int i = 0; i < -delta && !source.isEmpty(); i++) {
                hand.add(source.remove(source.size() - 1));
            }
            handSlot = slot;
            step = new Step(slot, slot, 0, true, 170 * speed, start);
        } else {
            events.poll();
            int from = handSlot == -1 ? slot : handSlot;
            step = new Step(from, slot, delta, false, flightTime(from, slot), start);
        }
    }

    private void completeStep() {
        Step done = step;
        step = null;
        handSlot = done.to;
        if (done.drop > 0) {
            for (int i = 0; i < done.drop; i++) {
                slots.get(done.to).add(hand.isEmpty()
                        ? Theme.STONE_COLORS[random.nextInt(Theme.STONE_COLORS.length)] : hand.remove(0));
            }
            ripples.add(new Effect(done.to, null, 0, 0));
            if (done.to >= STORE_1 && done.drop > 1 && !gameOver) {
                toast("Capture +" + done.drop, centers[done.to].x, stores[done.to - 12].y + 30);
            }
        }
    }

    private double flightTime(int from, int to) {
        double distance = centers[from].distance(centers[to]);
        return Math.max(170, Math.min(560, 150 + distance * 0.55)) * speed;
    }

    private double effectiveDuration(Step s) {
        return s.duration / (fastForward ? 5 : 1);
    }

    private double progress(Step s) {
        return Math.max(0, Math.min(1, (System.currentTimeMillis() - s.start) / effectiveDuration(s)));
    }

    private void toast(String text, double x, double y) {
        toasts.add(new Effect(0, text, x, y));
    }

    // ---------------------------------------------------------------- input

    private void updateHover(int x, int y) {
        hoverSlot = -1;
        hoverButton = null;
        if (overlay != Overlay.NONE) {
            if (primaryButton != null && primaryButton.contains(x, y)) {
                hoverButton = primaryButton;
            } else if (secondaryButton != null && secondaryButton.contains(x, y)) {
                hoverButton = secondaryButton;
            }
        } else if (leaveButton != null && leaveButton.contains(x, y)) {
            hoverButton = leaveButton;
        } else {
            int pit = pitAt(x, y);
            if (pit != -1 && playable(pit)) {
                hoverSlot = pit;
            }
        }
        boolean clickable = hoverButton != null || hoverSlot != -1;
        setCursor(Cursor.getPredefinedCursor(clickable ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    private void handleClick(int x, int y) {
        if (overlay == Overlay.LEAVE) {
            if (primaryButton.contains(x, y)) {
                onExit.run();
                return;
            }
            if (secondaryButton.contains(x, y) || !card.contains(x, y)) {
                overlay = Overlay.NONE;
            }
        } else if (overlay == Overlay.GAME_OVER) {
            if (primaryButton.contains(x, y)) {
                startGame();
            } else if (secondaryButton.contains(x, y)) {
                onExit.run();
                return;
            }
        } else if (leaveButton.contains(x, y)) {
            if (gameOver) {
                onExit.run();
                return;
            }
            openOverlay(Overlay.LEAVE);
        } else if (animating()) {
            fastForward = true;
        } else {
            int pit = pitAt(x, y);
            if (pit != -1 && playable(pit)) {
                play(pit);
            }
        }
        updateHover(x, y);
    }

    private int pitAt(int x, int y) {
        for (int pit = 1; pit <= 12; pit++) {
            if (centers[pit] != null && centers[pit].distance(x, y) <= pitR) {
                return pit;
            }
        }
        return -1;
    }

    // ---------------------------------------------------------------- layout

    private void computeLayout() {
        double w = getWidth();
        double h = getHeight();
        double top = 120;
        double bottom = 70;
        double bw = w - 96;
        double bh = Math.max(200, Math.min(h - top - bottom, bw * 0.42));
        double bx = 48;
        double by = top + Math.max(0, (h - top - bottom - bh) / 2);
        board = new RoundRectangle2D.Double(bx, by, bw, bh, bh * 0.35, bh * 0.35);

        double pad = bh * 0.08;
        double storeW = bw * 0.105;
        double gap = bw * 0.02;
        double pitsX = bx + pad + storeW + gap;
        double colW = (bw - 2 * (pad + storeW + gap)) / 6;
        pitR = Math.min(colW * 0.42, bh * 0.19);
        stoneR = pitR * 0.19;
        for (int i = 0; i < 6; i++) {
            double cx = pitsX + colW * (i + 0.5);
            centers[1 + i] = new Point2D.Double(cx, by + bh * 0.285);
            centers[7 + i] = new Point2D.Double(cx, by + bh * 0.715);
        }
        stores[1] = new RoundRectangle2D.Double(bx + pad, by + pad, storeW, bh - 2 * pad, storeW, storeW);
        stores[2] = new RoundRectangle2D.Double(bx + bw - pad - storeW, by + pad, storeW, bh - 2 * pad,
                storeW, storeW);
        centers[STORE_1] = new Point2D.Double(stores[1].getCenterX(), stores[1].getCenterY());
        centers[STORE_2] = new Point2D.Double(stores[2].getCenterX(), stores[2].getCenterY());

        leaveButton = new RoundRectangle2D.Double(w - 48 - 140, 38, 140, 44, 44, 44);

        double cw = Math.min(520, w - 80);
        double ch = 210;
        card = new RoundRectangle2D.Double((w - cw) / 2, (h - ch) / 2, cw, ch, 28, 28);
        double bwide = 150;
        double by2 = card.y + ch - 28 - 46;
        primaryButton = new RoundRectangle2D.Double(card.x + cw - 30 - bwide, by2, bwide, 46, 46, 46);
        secondaryButton = new RoundRectangle2D.Double(primaryButton.x - 12 - bwide, by2, bwide, 46, 46, 46);
    }

    // ---------------------------------------------------------------- painting

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        Theme.smooth(g);
        computeLayout();

        paintStaticLayer(g);
        paintPlayableGlows(g);
        paintHeader(g);
        paintPlayerLabel(g, name1, board.y - 18, 1);
        paintPlayerLabel(g, name2, board.y + board.height + 34, -1);
        for (int slot = 1; slot <= STORE_2; slot++) {
            List<Color> colors = slots.get(slot);
            for (int i = 0; i < colors.size(); i++) {
                Point2D.Double p = stonePosition(slot, i);
                Theme.drawStone(g, p.x, p.y, stoneR, colors.get(i), 1);
            }
        }
        paintCounts(g);
        paintRipples(g);
        paintHand(g);
        paintToasts(g);
        if (overlay == Overlay.LEAVE) {
            paintDialog(g, "Leave this game?", "This game is not saved, so it will be lost.", "Cancel", "Leave");
        } else if (overlay == Overlay.GAME_OVER) {
            paintDialog(g, resultTitle, resultScore, "Back to home", "Play again");
        }
        g.dispose();
    }

    private void paintHeader(Graphics2D g) {
        g.setFont(Theme.font(Font.BOLD, 34));
        g.setColor(Theme.CREAM);
        g.drawString("Mancala", 48, 72);
        int titleWidth = g.getFontMetrics().stringWidth("Mancala");

        String chip = kalah ? "Kalah rules" : "Ayo rules";
        g.setFont(Theme.font(Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        double cx = 48 + titleWidth + 16;
        g.setColor(new Color(255, 205, 110, 40));
        g.fill(new RoundRectangle2D.Double(cx, 50, fm.stringWidth(chip) + 24, 26, 26, 26));
        g.setColor(Theme.GOLD);
        g.drawString(chip, (float) cx + 12, 68);

        Theme.paintButton(g, leaveButton, gameOver ? "Back to home" : "Leave game", false,
                hoverButton == leaveButton, true, 1);
    }

    private void paintPlayerLabel(Graphics2D g, String name, double y, int side) {
        boolean active = !gameOver && overlay == Overlay.NONE && turn == side;
        g.setFont(Theme.font(Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        String text = active ? name + "  -  your turn" : name;
        double x = board.getCenterX() - fm.stringWidth(text) / 2.0;
        if (active) {
            double pulse = 0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 220.0);
            g.setColor(new Color(255, 205, 110, (int) (90 + 120 * pulse)));
            g.fill(Theme.circle(x - 14, y - 5, 5));
        }
        g.setColor(active ? Theme.GOLD : new Color(250, 236, 210, 110));
        g.drawString(text, (float) x, (float) y);
    }

    /**
     * Draws the background, wood and empty wells from a cached image, rebuilt only when the size or display
     * scale changes. They never change between frames, and redrawing them every frame is slow in the browser.
     */
    private void paintStaticLayer(Graphics2D g) {
        double scale = g.getTransform().getScaleX();
        int w = getWidth();
        int h = getHeight();
        if (staticLayer == null || staticW != w || staticH != h || staticScale != scale) {
            staticLayer = new BufferedImage((int) Math.ceil(w * scale), (int) Math.ceil(h * scale),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D layer = staticLayer.createGraphics();
            Theme.smooth(layer);
            layer.scale(scale, scale);
            Theme.paintBackground(layer, w, h);
            paintBoard(layer);
            layer.dispose();
            staticW = w;
            staticH = h;
            staticScale = scale;
        }
        g.drawImage(staticLayer, 0, 0, w, h, null);
    }

    private void paintPlayableGlows(Graphics2D g) {
        long now = System.currentTimeMillis();
        for (int pit = 1; pit <= 12; pit++) {
            if (!playable(pit)) {
                continue;
            }
            Point2D.Double c = centers[pit];
            boolean hover = pit == hoverSlot;
            double pulse = 0.5 + 0.5 * Math.sin(now / 300.0 + pit);
            int alpha = hover ? 170 : (int) (35 + 35 * pulse);
            double glowR = pitR * (hover ? 1.32 : 1.18);
            g.setPaint(new RadialGradientPaint(c, (float) glowR, new float[] {0f, 0.7f, 1f},
                    new Color[] {new Color(255, 205, 110, alpha), new Color(255, 205, 110, alpha),
                        new Color(255, 205, 110, 0)}));
            g.fill(Theme.circle(c.x, c.y, glowR));
            paintWell(g, Theme.circle(c.x, c.y, pitR), c, pitR);
        }
    }

    private void paintBoard(Graphics2D g) {
        // Drop shadow.
        for (int i = 1; i <= 6; i++) {
            g.setColor(new Color(0, 0, 0, 18));
            g.fill(new RoundRectangle2D.Double(board.x - i * 2, board.y + i * 3, board.width + i * 4,
                    board.height + i * 2, board.arcwidth, board.archeight));
        }

        g.setPaint(new GradientPaint(0, (float) board.y, new Color(150, 92, 50), 0,
                (float) (board.y + board.height), new Color(96, 54, 28)));
        g.fill(board);

        // Wood grain, seeded so it stays still between frames.
        Shape oldClip = g.getClip();
        g.clip(board);
        Random grain = new Random(7);
        g.setStroke(new BasicStroke(1.4f));
        for (int i = 0; i < 46; i++) {
            double y = board.y + board.height * i / 46.0 + grain.nextDouble() * 6;
            Path2D.Double line = new Path2D.Double();
            line.moveTo(board.x, y);
            double amp = 2 + grain.nextDouble() * 6;
            double phase = grain.nextDouble() * 6;
            for (double x = board.x; x <= board.x + board.width; x += 12) {
                line.lineTo(x, y + Math.sin(x / 90.0 + phase) * amp);
            }
            g.setColor(new Color(40, 20, 8, 22 + grain.nextInt(22)));
            g.draw(line);
        }
        g.setClip(oldClip);

        // Bevel highlight along the top edge.
        g.setStroke(new BasicStroke(2f));
        g.setPaint(new GradientPaint(0, (float) board.y, new Color(255, 220, 170, 90), 0,
                (float) (board.y + board.height), new Color(0, 0, 0, 60)));
        g.draw(board);

        for (int store = 1; store <= 2; store++) {
            paintWell(g, stores[store], centers[12 + store], stores[store].width / 2);
        }
        for (int pit = 1; pit <= 12; pit++) {
            Point2D.Double c = centers[pit];
            paintWell(g, Theme.circle(c.x, c.y, pitR), c, pitR);
        }
    }

    private void paintWell(Graphics2D g, Shape shape, Point2D.Double c, double r) {
        double h = shape.getBounds2D().getHeight() / 2;
        g.setPaint(new RadialGradientPaint(new Point2D.Double(c.x, c.y + h * 0.25), (float) Math.max(r, h) * 1.1f,
                new float[] {0f, 1f}, new Color[] {new Color(78, 44, 22), new Color(36, 20, 10)}));
        g.fill(shape);
        g.setStroke(new BasicStroke((float) Math.max(2, r * 0.07)));
        g.setPaint(new GradientPaint((float) c.x, (float) (c.y - h), new Color(20, 10, 4, 210),
                (float) c.x, (float) (c.y + h), new Color(255, 214, 160, 110)));
        g.draw(shape);
    }

    private Point2D.Double stonePosition(int slot, int i) {
        Point2D.Double c = centers[slot];
        Random jitter = new Random(slot * 1000L + i);
        double angle = i * 2.39996 + slot;
        double r = stoneR * 1.15 * Math.sqrt(i) + jitter.nextDouble() * stoneR * 0.3;
        if (slot <= 12) {
            r = Math.min(r, pitR - stoneR * 1.6);
            return new Point2D.Double(c.x + Math.cos(angle) * r, c.y + Math.sin(angle) * r);
        }
        // Keep stones above the score drawn at the bottom of the store.
        RoundRectangle2D.Double store = stores[slot - 12];
        double usable = store.height - stoneR * 6;
        double dx = Math.cos(angle) * r;
        double dy = Math.sin(angle) * r * 2.2;
        double maxX = store.width / 2 - stoneR * 1.6;
        double maxY = usable / 2 - stoneR * 1.8;
        dx = Math.max(-maxX, Math.min(maxX, dx));
        dy = Math.max(-maxY, Math.min(maxY, dy));
        return new Point2D.Double(c.x + dx, store.y + usable / 2 + dy);
    }

    private void paintCounts(Graphics2D g) {
        g.setFont(Theme.font(Font.BOLD, (float) Math.max(11, pitR * 0.24)));
        for (int pit = 1; pit <= 12; pit++) {
            Point2D.Double c = centers[pit];
            double bx = c.x + pitR * 0.74;
            double by = c.y + (pit <= 6 ? -pitR * 0.74 : pitR * 0.74);
            double br = Math.max(10, pitR * 0.22);
            g.setColor(new Color(24, 14, 8, 230));
            g.fill(Theme.circle(bx, by, br));
            g.setColor(new Color(255, 214, 160, 120));
            g.setStroke(new BasicStroke(1.2f));
            g.draw(Theme.circle(bx, by, br));
            g.setColor(Theme.CREAM);
            Theme.drawCentered(g, String.valueOf(slots.get(pit).size()), bx, by);
        }
        g.setFont(Theme.font(Font.BOLD, (float) Math.max(18, pitR * 0.42)));
        for (int store = 1; store <= 2; store++) {
            RoundRectangle2D.Double s = stores[store];
            g.setColor(Theme.CREAM);
            Theme.drawCentered(g, String.valueOf(slots.get(12 + store).size()), s.getCenterX(),
                    s.y + s.height - stoneR * 3.2);
        }
    }

    private void paintRipples(Graphics2D g) {
        long now = System.currentTimeMillis();
        for (Effect ripple : ripples) {
            double t = (now - ripple.start) / 500.0;
            Point2D.Double c = centers[ripple.slot];
            double base = ripple.slot <= 12 ? pitR : stores[ripple.slot - 12].width / 2;
            g.setStroke(new BasicStroke(3f));
            g.setColor(new Color(255, 205, 110, (int) Math.max(0, 180 * (1 - t))));
            g.draw(Theme.circle(c.x, c.y, base * (0.9 + 0.35 * t)));
        }
    }

    private void paintHand(Graphics2D g) {
        if (step == null || hand.isEmpty()) {
            return;
        }
        double t = progress(step);
        double e = t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
        Point2D.Double a = centers[step.from];
        Point2D.Double b = centers[step.to];
        double x = a.x + (b.x - a.x) * e;
        double ground = a.y + (b.y - a.y) * e;
        double hover = stoneR * 2.2;
        double lift;
        if (step.pickup) {
            lift = hover * Math.sin(t * Math.PI / 2);
        } else {
            double endLift = step.drop >= hand.size() ? 0 : hover;
            lift = hover + (endLift - hover) * e + Math.sin(t * Math.PI) * (20 + a.distance(b) * 0.18);
        }

        g.setColor(new Color(0, 0, 0, (int) Math.max(20, 90 - lift)));
        double spread = stoneR * (1.4 + Math.sqrt(hand.size()) * 0.8);
        g.fill(new Ellipse2D.Double(x - spread, ground - spread * 0.35, spread * 2, spread * 0.9));
        for (int i = 0; i < hand.size(); i++) {
            double angle = i * 2.39996;
            double r = stoneR * 0.9 * Math.sqrt(i);
            Theme.drawStone(g, x + Math.cos(angle) * r, ground - lift + Math.sin(angle) * r, stoneR * 1.08,
                    hand.get(i), 0.4);
        }
    }

    private void paintToasts(Graphics2D g) {
        long now = System.currentTimeMillis();
        g.setFont(Theme.font(Font.BOLD, 26));
        for (Effect toast : toasts) {
            double t = (now - toast.start) / 1400.0;
            double alpha = t < 0.15 ? t / 0.15 : t > 0.6 ? (1 - t) / 0.4 : 1;
            double y = toast.y - 40 * t;
            g.setColor(new Color(0, 0, 0, (int) (150 * alpha)));
            Theme.drawCentered(g, toast.text, toast.x + 2, y + 2);
            g.setColor(new Color(255, 205, 110, (int) (255 * alpha)));
            Theme.drawCentered(g, toast.text, toast.x, y);
        }
    }

    private void paintDialog(Graphics2D g, String title, String message, String secondary, String primary) {
        double fade = Math.min(1, (System.currentTimeMillis() - overlayStart) / 250.0);
        g.setColor(new Color(10, 6, 12, (int) (170 * fade)));
        g.fillRect(0, 0, getWidth(), getHeight());

        double rise = (1 - fade) * 16;
        g.translate(0, rise);
        g.setColor(Theme.alpha(Theme.CARD, fade));
        g.fill(card);
        g.setStroke(new BasicStroke(1.5f));
        g.setColor(Theme.alpha(Theme.BORDER, fade));
        g.draw(card);

        g.setFont(Theme.font(Font.BOLD, 28));
        g.setColor(Theme.alpha(Theme.CREAM, fade));
        g.drawString(title, (float) card.x + 30, (float) card.y + 62);
        g.setFont(Theme.font(Font.PLAIN, 16));
        g.setColor(Theme.alpha(Theme.MUTED, fade));
        g.drawString(message, (float) card.x + 30, (float) card.y + 102);

        Theme.paintButton(g, secondaryButton, secondary, false, hoverButton == secondaryButton, true, fade);
        Theme.paintButton(g, primaryButton, primary, true, hoverButton == primaryButton, true, fade);
        g.translate(0, -rise);
    }
}
