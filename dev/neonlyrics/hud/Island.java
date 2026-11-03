/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_640
 *  net.minecraft.class_9779
 */
package dev.neonlyrics.hud;

import dev.neonlyrics.License;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.gui.LyricsScreen;
import dev.neonlyrics.hud.Anim;
import dev.neonlyrics.hud.Cover;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.hud.Theme;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import dev.neonlyrics.lyrics.Translator;
import dev.neonlyrics.util.AutoPerf;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_640;
import net.minecraft.class_9779;

public final class Island {
    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");
    private static final Spring W = new Spring(60.0f, 170.0f, 19.0f);
    private static final Spring H = new Spring(14.0f, 170.0f, 19.0f);
    private static final Spring HL_X = new Spring(0.0f, 260.0f, 30.0f);
    private static final Spring HL_W = new Spring(0.0f, 260.0f, 30.0f);
    private static final Spring SCROLL = new Spring(0.0f, 90.0f, 18.0f);
    private static final Spring SHOW = new Spring(0.0f, 120.0f, 20.0f);
    private static String note = null;
    private static boolean noteOk = true;
    private static long noteTime = 0L;
    private static String curKey = "";
    private static long switchTime = 0L;
    private static Kind prevKind = Kind.IDLE;
    private static String prevText = "";
    private static Kind curKind = Kind.IDLE;
    private static String curText = "";
    private static long lastNanos = System.nanoTime();

    private Island() {
    }

    public static void notify(String text, boolean ok) {
        note = text;
        noteOk = ok;
        noteTime = System.currentTimeMillis();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void render(class_332 g, class_9779 delta) {
        if (!License.ok()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        Config c = NeonLyrics.cfg();
        if (mc.field_1690.field_1842 || mc.field_1724 == null || !c.spotifyHudEnabled) {
            return;
        }
        if (mc.field_1755 instanceof LyricsScreen) {
            return;
        }
        AutoPerf.begin();
        try {
            Island.renderIsland(g, mc, c);
        }
        finally {
            AutoPerf.end();
        }
    }

    private static void renderIsland(class_332 g, class_310 mc, Config c) {
        float targetW;
        String key;
        String text;
        Kind kind;
        boolean hasLine;
        long nowNs = System.nanoTime();
        float dt = Math.min(0.05f, (float)(nowNs - lastNanos) / 1.0E9f);
        lastNanos = nowNs;
        long now = System.currentTimeMillis();
        int sw = g.method_51421();
        int cx = sw / 2;
        int top = 5;
        float old = Gfx.alpha;
        Gfx.alpha = 1.0f;
        int acc = Theme.accent();
        boolean noteActive = note != null && now - noteTime < 2400L;
        NowPlaying np = SpotifyClient.now();
        boolean hasSong = c.lyricsIsland && np.hasTrack();
        long prog = np.progressNow(c.lyricsOffsetMs + 120);
        int li = hasSong && np.lyricsState == NowPlaying.LyricsState.SYNCED && np.playing ? np.lineIndex(prog) : -1;
        NowPlaying.Line line = li >= 0 ? np.lines.get(li) : null;
        boolean bl = hasLine = line != null && !line.text().isBlank() && prog <= np.lineEnd(li) + 400L;
        if (noteActive) {
            kind = Kind.NOTE;
            text = note;
            key = "n" + noteTime;
        } else if (hasLine) {
            kind = Kind.LYRIC;
            text = line.text();
            key = "l" + np.trackId + li;
        } else if (hasSong) {
            kind = Kind.SONG;
            text = np.title + (String)(np.artist.isBlank() ? "" : "  \u00b7  " + np.artist);
            key = "s" + np.trackId;
        } else {
            kind = Kind.IDLE;
            text = "";
            key = "i";
        }
        if (!((String)key).equals(curKey)) {
            prevKind = curKind;
            prevText = curText;
            curKey = key;
            curKind = kind;
            curText = text;
            switchTime = now;
            if (kind == Kind.LYRIC) {
                SCROLL.snap(0.0f);
                HL_X.snap(0.0f);
                HL_W.snap(0.0f);
            }
        }
        int musicW = (int)Math.max(200.0f, Math.min(300.0f, (float)sw * 0.42f));
        float targetH = switch (kind.ordinal()) {
            case 1 -> {
                targetW = Math.min(musicW, Gfx.w((String)text) + 40);
                yield 18.0f;
            }
            case 2, 3 -> {
                targetW = Math.max(110, Math.min(musicW, Gfx.w((String)text) + 58));
                yield 18.0f;
            }
            default -> {
                targetW = 40.0f;
                yield 14.0f;
            }
        };
        boolean idleHidden = kind == Kind.IDLE && !c.showClock && !c.showPing;
        float show = SHOW.update(idleHidden ? 0.0f : 1.0f, dt);
        float w = W.update(targetW, dt);
        float h = H.update(targetH, dt);
        if (show < 0.02f) {
            Gfx.alpha = old;
            return;
        }
        int pw = Math.max(12, Math.round(w));
        int ph = Math.max(8, Math.round(h));
        int px = cx - pw / 2;
        int r = ph / 2;
        int midY = top + ph / 2;
        Gfx.alpha = show;
        if (c.showClock) {
            String t = LocalTime.now().format(HM);
            Gfx.textBold(g, t, px - 10 - Gfx.w(t, Gfx.BOLD), midY - 4, 0xFFFFFF, 0.95f, true);
        }
        int rx = px + pw + 10;
        if (c.showPing && mc.method_1562() != null) {
            int ping;
            class_640 pi = mc.method_1562().method_2871(mc.field_1724.method_5667());
            int n = ping = pi == null ? 0 : pi.method_2959();
            int bars = ping <= 0 ? 0 : (ping < 60 ? 4 : (ping < 120 ? 3 : (ping < 250 ? 2 : 1)));
            float[] ax = new float[9];
            float[] ay = new float[9];
            for (int i = 0; i < 9; ++i) {
                double an = Math.PI + (double)i * Math.PI / 8.0;
                ax[i] = (float)rx + 3.5f + (float)Math.cos(an) * 3.0f;
                ay[i] = (float)midY + 0.5f + (float)Math.sin(an) * 3.0f;
            }
            Gfx.aaStroke(g, ax, ay, 1.1f, 8826011, 0.9f);
            String ps = Integer.toString(ping);
            Gfx.textBold(g, ps, rx + 9, midY - 4, 0xFFFFFF, 0.95f, true);
            int bx = rx + 9 + Gfx.w(ps, Gfx.BOLD) + 5;
            for (int i = 0; i < 4; ++i) {
                int bh = 2 + i * 2;
                Gfx.aaRound(g, bx + i * 3, midY + 4 - bh, 2.0f, bh, 1.0f, 0xFFFFFF, i < bars ? 0.95f : 0.22f);
            }
            rx = bx + 16;
        }
        if (c.showFps) {
            Gfx.text(g, mc.method_47599() + " fps", rx, midY - 4, 8826011, 0.85f, true);
        }
        float fx = (float)cx - w / 2.0f;
        float fh = h;
        float fr = fh / 2.0f;
        if (AutoPerf.allow(2)) {
            for (int k = 3; k >= 1; --k) {
                Gfx.aaRound(g, fx - (float)k, (float)(top - k) + 1.5f, w + (float)(k * 2), fh + (float)(k * 2), fr + (float)k, 0, 0.05f);
            }
        }
        Gfx.aaRound(g, fx - 0.75f, (float)top - 0.75f, w + 1.5f, fh + 1.5f, fr + 0.75f, 5229464, 0.38f);
        Gfx.aaRound(g, fx, top, w, fh, fr, 726803, 0.94f);
        g.method_44379(px + 2, top, px + pw - 2, top + ph);
        float e = Anim.outCubic(Anim.since(switchTime, 380L));
        if (e < 1.0f && prevKind != Kind.IDLE && !prevText.isEmpty()) {
            float oa = show * (1.0f - e);
            Gfx.alpha = oa * 0.5f;
            Island.drawSimple(g, prevKind, prevText, px - Math.round(e * 3.0f), top, pw, ph, acc, np, now, false);
            Island.drawSimple(g, prevKind, prevText, px + Math.round(e * 3.0f), top, pw, ph, acc, np, now, false);
        }
        Gfx.alpha = show * e;
        switch (kind.ordinal()) {
            case 3: {
                Island.drawLyric(g, line, np, li, prog, px, top, pw, ph, acc, dt, e);
                break;
            }
            case 0: {
                float br = 0.5f + 0.5f * (float)Math.sin((double)now / 900.0);
                Gfx.aaCircle(g, cx, (float)top + h / 2.0f, 2.2f, acc, 0.55f + 0.35f * br);
                break;
            }
            default: {
                Island.drawSimple(g, kind, (String)text, px, top, pw, ph, acc, np, now, true);
            }
        }
        g.method_44380();
        if (kind == Kind.LYRIC && c.translate) {
            String tr = Translator.get(line.text());
            for (int k2 = 1; k2 <= 2 && li + k2 < np.lines.size(); ++k2) {
                Translator.prefetch(np.lines.get(li + k2).text());
            }
            if (tr != null) {
                Gfx.alpha = show * e;
                g.method_51448().pushMatrix();
                g.method_51448().translate((float)cx, (float)(top + ph + 4));
                g.method_51448().scale(0.85f, 0.85f);
                Gfx.center(g, Gfx.ellipsize(tr, Math.round((float)pw / 0.85f)), 0, 0, 13035221, 0.9f, true);
                g.method_51448().popMatrix();
            }
        }
        Gfx.alpha = old;
    }

    private static void cover(class_332 g, int cx, int cy, int acc, boolean playing, long now) {
        if (Cover.draw(g, cx - 6, cy - 6, 12, 1.0f)) {
            return;
        }
        Gfx.aaCircle(g, cx, cy, 6.0f, Gfx.lerpColor(acc, 2050616, 0.35f), 1.0f);
        Gfx.aaCircle(g, cx, cy, 4.6f, Gfx.lerpColor(acc, 0xFFFFFF, 0.25f), 0.9f);
        double an = playing ? (double)now / 450.0 : 0.8;
        Gfx.aaCircle(g, (float)((double)cx + Math.cos(an) * 3.0), (float)((double)cy + Math.sin(an) * 3.0), 1.1f, 0xFFFFFF, 0.75f);
        Gfx.aaCircle(g, cx, cy, 1.4f, 726803, 1.0f);
    }

    private static void drawSimple(class_332 g, Kind kind, String text, int px, int top, int pw, int ph, int acc, NowPlaying np, long now, boolean live) {
        int midY = top + ph / 2;
        int ty = midY - 4;
        if (kind == Kind.NOTE) {
            int col = noteOk ? 6022299 : 16735354;
            float pop = live ? Anim.outBack(Anim.since(noteTime, 380L)) : 1.0f;
            float ccx = px + 11;
            float ccy = (float)top + (float)ph / 2.0f;
            Gfx.aaCircle(g, ccx, ccy, 5.0f * pop, col, 1.0f);
            if (noteOk) {
                Gfx.aaStroke(g, new float[]{ccx - 2.3f, ccx - 0.6f, ccx + 2.4f}, new float[]{ccy + 0.1f, ccy + 1.8f, ccy - 1.8f}, 1.3f * pop, 0xFFFFFF, 1.0f);
            } else {
                Gfx.aaRound(g, ccx - 2.5f, ccy - 0.7f, 5.0f, 1.4f, 0.7f, 0xFFFFFF, 1.0f);
            }
            Gfx.text(g, Gfx.ellipsize(text, pw - 30), px + 21, ty, 0xFFFFFF, 1.0f, false);
        } else if (kind == Kind.SONG || kind == Kind.LYRIC) {
            Island.cover(g, px + 11, midY, acc, np.playing, now);
            Island.equalizer(g, px + pw - 20, midY, acc, np.playing, now);
            Gfx.text(g, Gfx.ellipsize(text, pw - 50), px + 22, ty, 0xFFFFFF, kind == Kind.SONG ? 0.95f : 0.45f, false);
        }
    }

    private static void equalizer(class_332 g, int x, int cy, int acc, boolean playing, long now) {
        if (!AutoPerf.allow(4)) {
            playing = false;
        }
        double t = (double)now / 1000.0;
        NowPlaying np = SpotifyClient.now();
        float beat = np.beatPulse(np.progressNow(NeonLyrics.cfg().lyricsOffsetMs));
        for (int i = 0; i < 4; ++i) {
            float hh = playing ? (float)(3.0 + 3.5 * Math.abs(Math.sin(t * (2.6 + (double)i * 1.1) + (double)i * 1.7)) + 3.5 * (double)beat * (i % 2 == 0 ? 1.0 : 0.7)) : 2.5f;
            int bh = Math.round(hh);
            Gfx.aaRound(g, x + i * 3, (float)cy - hh / 2.0f, 2.0f, hh, 1.0f, acc, 0.95f);
        }
    }

    private static void drawLyric(class_332 g, NowPlaying.Line line, NowPlaying np, int li, long prog, int px, int top, int pw, int ph, int acc, float dt, float in) {
        long now = System.currentTimeMillis();
        int midY = top + ph / 2;
        Island.cover(g, px + 11, midY, acc, np.playing, now);
        Island.equalizer(g, px + pw - 20, midY, acc, np.playing, now);
        int tx = px + 22;
        int tw = pw - 22 - 26;
        String[] words = line.words();
        float wp = np.wordPosition(li, prog);
        int cw = (int)Math.floor(wp);
        int space = Gfx.w(" ");
        int[] wx = new int[words.length];
        int[] ww = new int[words.length];
        int x = 0;
        for (int i = 0; i < words.length; ++i) {
            wx[i] = x;
            ww[i] = Gfx.w(words[i]);
            x += ww[i] + space;
        }
        int full = x - space;
        int a0 = Math.max(0, Math.min(words.length - 1, cw - 1));
        int a1 = Math.max(0, Math.min(words.length - 1, cw));
        int curX = cw >= 0 ? wx[a0] : 0;
        int curW = cw >= 0 ? wx[a1] + ww[a1] - wx[a0] : 0;
        float sx = full > tw ? Math.max(0.0f, Math.min((float)(full - tw), (float)wx[a1] + (float)ww[a1] / 2.0f - (float)tw * 0.55f)) : 0.0f;
        float scroll = SCROLL.update(sx, dt);
        float hx = HL_X.update(curX, dt);
        float hw = HL_W.update(curW, dt);
        int base = tx - Math.round(scroll);
        int ty = midY - 4;
        g.method_44379(tx - 2, top, tx + tw + 2, top + ph);
        if (cw >= 0 && cw < words.length) {
            Gfx.aaRound(g, (float)base + hx - 3.0f, top + 3, hw + 6.0f, ph - 6, 3.5f, acc, 0.85f);
        }
        for (int i = 0; i < words.length; ++i) {
            boolean sung = i <= cw;
            Gfx.text(g, words[i], base + wx[i], ty, 0xFFFFFF, sung ? 1.0f : 0.42f, false);
        }
        g.method_44380();
    }

    private static enum Kind {
        IDLE,
        NOTE,
        SONG,
        LYRIC;

    }

    private static final class Spring {
        float x;
        float v;
        final float k;
        final float d;

        Spring(float x, float stiffness, float damping) {
            this.x = x;
            this.k = stiffness;
            this.d = damping;
        }

        float update(float target, float dt) {
            int steps = Math.max(1, (int)Math.ceil(dt / 0.008f));
            float h = dt / (float)steps;
            for (int i = 0; i < steps; ++i) {
                float a = this.k * (target - this.x) - this.d * this.v;
                this.v += a * h;
                this.x += this.v * h;
            }
            return this.x;
        }

        void snap(float t) {
            this.x = t;
            this.v = 0.0f;
        }
    }
}

