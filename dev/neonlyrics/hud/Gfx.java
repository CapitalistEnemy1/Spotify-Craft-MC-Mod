/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.minecraft.class_10799
 *  net.minecraft.class_11719
 *  net.minecraft.class_11719$class_11721
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_5250
 *  net.minecraft.class_5348
 */
package dev.neonlyrics.hud;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.hud.Shapes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_10799;
import net.minecraft.class_11719;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5250;
import net.minecraft.class_5348;

public final class Gfx {
    public static float alpha = 1.0f;
    private static long revealStart = 0L;
    private static int revealTop = 0;
    private static int curY = Integer.MIN_VALUE;
    public static boolean revealOn = false;
    public static final Role UI = Role.UI;
    public static final Role BOLD = Role.BOLD;
    public static final Role LYRICS = Role.LYRICS;
    public static final String[] FONT_IDS = new String[]{"poppins", "nunito", "montserrat", "inter", "outfit", "spacegrotesk", "manrope", "dmsans", "plusjakartasans", "sora", "lexend", "rubik", "urbanist", "syne", "kanit", "fredoka", "baloo2", "unbounded", "orbitron", "bebasneue", "pacifico", "caveat"};
    public static final String[] FONT_NAMES = new String[]{"Poppins", "Nunito", "Montserrat", "Inter", "Outfit", "Space Grotesk", "Manrope", "DM Sans", "Jakarta Sans", "Sora", "Lexend", "Rubik", "Urbanist", "Syne", "Kanit", "Fredoka", "Baloo", "Unbounded", "Orbitron", "Bebas Neue", "Pacifico", "Caveat", "Minecraft"};
    private static final class_11719[][] FD = new class_11719[FONT_IDS.length][3];
    private static Role cur;
    private static final Map<class_11719, Map<String, Integer>> WIDTHS;

    private Gfx() {
    }

    public static void startReveal(int top, long start) {
        revealTop = top;
        revealStart = start;
    }

    private static float reveal(int y) {
        if (!revealOn || revealStart == 0L || !NeonLyrics.cfg().fxMenuAnim || y == Integer.MIN_VALUE) {
            return 1.0f;
        }
        long el = System.currentTimeMillis() - revealStart;
        if (el > 900L) {
            revealStart = 0L;
            return 1.0f;
        }
        float front = (float)el * 0.55f;
        float d = front - (float)(y - revealTop);
        return Math.max(0.0f, Math.min(1.0f, d / 40.0f));
    }

    public static int argb(int rgb, float a) {
        int al = Math.round(Math.max(0.0f, Math.min(1.0f, a * alpha * Gfx.reveal(curY))) * 255.0f);
        return al << 24 | rgb & 0xFFFFFF;
    }

    public static int argbRaw(int rgb, float a) {
        int al = Math.round(Math.max(0.0f, Math.min(1.0f, a)) * 255.0f);
        return al << 24 | rgb & 0xFFFFFF;
    }

    public static int lerpColor(int c1, int c2, float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        int r = (int)((float)(c1 >> 16 & 0xFF) + (float)((c2 >> 16 & 0xFF) - (c1 >> 16 & 0xFF)) * t);
        int g = (int)((float)(c1 >> 8 & 0xFF) + (float)((c2 >> 8 & 0xFF) - (c1 >> 8 & 0xFF)) * t);
        int b = (int)((float)(c1 & 0xFF) + (float)((c2 & 0xFF) - (c1 & 0xFF)) * t);
        return r << 16 | g << 8 | b;
    }

    public static int brighten(int c, float t) {
        return Gfx.lerpColor(c, 0xFFFFFF, t);
    }

    public static int hsv(float h, float s, float v) {
        float g;
        float r;
        h -= (float)Math.floor(h);
        int i = (int)(h * 6.0f);
        float f = h * 6.0f - (float)i;
        float p = v * (1.0f - s);
        float q = v * (1.0f - f * s);
        float t = v * (1.0f - (1.0f - f) * s);
        float b = switch (i % 6) {
            case 0 -> {
                r = v;
                g = t;
                yield p;
            }
            case 1 -> {
                r = q;
                g = v;
                yield p;
            }
            case 2 -> {
                r = p;
                g = v;
                yield t;
            }
            case 3 -> {
                r = p;
                g = q;
                yield v;
            }
            case 4 -> {
                r = t;
                g = p;
                yield v;
            }
            default -> {
                r = v;
                g = p;
                yield q;
            }
        };
        return (int)(r * 255.0f) << 16 | (int)(g * 255.0f) << 8 | (int)(b * 255.0f);
    }

    public static void rect(class_332 g, int x, int y, int w, int h, int rgb, float a) {
        if (w <= 0 || h <= 0 || a * alpha <= 0.004f) {
            return;
        }
        curY = y;
        g.method_25294(x, y, x + w, y + h, Gfx.argb(rgb, a));
    }

    private static int guiScale() {
        if (!revealOn && NeonLyrics.cfg().quality == 0) {
            return 1;
        }
        try {
            return Math.max(1, Math.min(2, Math.round(class_310.method_1551().method_22683().method_4495())));
        }
        catch (Exception e) {
            return 1;
        }
    }

    public static void round(class_332 g, int x, int y, int w, int h, int r, int rgb, float a) {
        if (w <= 0 || h <= 0 || a * alpha <= 0.004f) {
            return;
        }
        curY = y;
        r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
        int col = Gfx.argb(rgb, a);
        if (r == 0) {
            g.method_25294(x, y, x + w, y + h, col);
            return;
        }
        if (r >= 2 && Shapes.usable()) {
            Gfx.aaRound(g, x, y, w, h, r, rgb, a);
            return;
        }
        int s = Gfx.guiScale();
        if (s == 1) {
            Gfx.roundRows(g, x, y, w, h, r, col);
            return;
        }
        g.method_25294(x, y + r, x + w, y + h - r, col);
        g.method_51448().pushMatrix();
        g.method_51448().scale(1.0f / (float)s, 1.0f / (float)s);
        int X = x * s;
        int Y = y * s;
        int W = w * s;
        int H = h * s;
        int R = r * s;
        for (int i = 0; i < R; ++i) {
            double dy = (double)(R - i) - 0.5;
            int dx = (int)Math.round((double)R - Math.sqrt(Math.max(0.0, (double)(R * R) - dy * dy)));
            g.method_25294(X + dx, Y + i, X + W - dx, Y + i + 1, col);
            g.method_25294(X + dx, Y + H - i - 1, X + W - dx, Y + H - i, col);
        }
        g.method_51448().popMatrix();
    }

    private static int fullScale() {
        try {
            return Math.max(1, Math.min(6, Math.round(class_310.method_1551().method_22683().method_4495())));
        }
        catch (Exception e) {
            return 2;
        }
    }

    public static void aaRound(class_332 g, float x, float y, float w, float h, float r, int rgb, float a) {
        class_2960 id;
        if (w <= 0.0f || h <= 0.0f || a * alpha <= 0.004f) {
            return;
        }
        int s = Gfx.fullScale();
        float X = x * (float)s;
        float Y = y * (float)s;
        float W = w * (float)s;
        float H = h * (float)s;
        float R = Math.max(0.0f, Math.min(r * (float)s, Math.min(W, H) / 2.0f));
        g.method_51448().pushMatrix();
        g.method_51448().scale(1.0f / (float)s, 1.0f / (float)s);
        int x0 = Math.round(X);
        int y0 = Math.round(Y);
        int x1 = Math.round(X + W);
        int y1 = Math.round(Y + H);
        int iw = x1 - x0;
        int ih = y1 - y0;
        int ir = (int)Math.ceil(R);
        int rq = Math.min(Math.round(R), Math.min(iw, ih) / 2);
        if (rq > 96) {
            rq = Math.min(512, (rq + 4) / 8 * 8);
        }
        if (rq * 2 > Math.min(iw, ih)) {
            int n0 = rq * 2;
            if (iw == ih) {
                int c0x = x0 + iw / 2;
                int c0y = y0 + ih / 2;
                x0 = c0x - rq;
                y0 = c0y - rq;
                x1 = x0 + n0;
                y1 = y0 + n0;
                iw = ih = n0;
            } else {
                rq = Math.min(iw, ih) / 2;
            }
        }
        if (rq >= 2 && Shapes.usable() && (id = Shapes.circle(rq)) != null) {
            try {
                int col = Gfx.argb(rgb, a);
                int n = rq * 2;
                RenderPipeline pipe = class_10799.field_56883;
                if (iw == n && ih == n) {
                    g.method_25291(pipe, id, x0, y0, 0.0f, 0.0f, n, n, n, n, col);
                } else {
                    g.method_25291(pipe, id, x0, y0, 0.0f, 0.0f, rq, rq, n, n, col);
                    g.method_25291(pipe, id, x1 - rq, y0, (float)rq, 0.0f, rq, rq, n, n, col);
                    g.method_25291(pipe, id, x0, y1 - rq, 0.0f, (float)rq, rq, rq, n, n, col);
                    g.method_25291(pipe, id, x1 - rq, y1 - rq, (float)rq, (float)rq, rq, rq, n, n, col);
                    if (iw > n) {
                        g.method_25294(x0 + rq, y0, x1 - rq, y0 + rq, col);
                        g.method_25294(x0 + rq, y1 - rq, x1 - rq, y1, col);
                    }
                    if (ih > n) {
                        g.method_25294(x0, y0 + rq, x1, y1 - rq, col);
                    }
                }
                g.method_51448().popMatrix();
                return;
            }
            catch (Throwable t) {
                Shapes.fail();
            }
        }
        if (ih - 2 * ir > 0) {
            g.method_25294(x0, y0 + ir, x1, y1 - ir, Gfx.argb(rgb, a));
        }
        float[] e = new float[4];
        for (int i = 0; i < ir; ++i) {
            float mn = Float.MAX_VALUE;
            float mx = -1.0f;
            for (int k = 0; k < 4; ++k) {
                float ex;
                float dy = R - ((float)i + 0.125f + (float)k * 0.25f);
                e[k] = ex = dy <= 0.0f ? 0.0f : R - (float)Math.sqrt(Math.max(0.0f, R * R - dy * dy));
                mn = Math.min(mn, ex);
                mx = Math.max(mx, ex);
            }
            int full = (int)Math.ceil(mx);
            int yt = y0 + i;
            int yb = y1 - 1 - i;
            if (iw - 2 * full > 0) {
                g.method_25294(x0 + full, yt, x1 - full, yt + 1, Gfx.argb(rgb, a));
                if (yb != yt) {
                    g.method_25294(x0 + full, yb, x1 - full, yb + 1, Gfx.argb(rgb, a));
                }
            }
            for (int c = (int)Math.floor(mn); c < full; ++c) {
                float cov = 0.0f;
                for (int k = 0; k < 4; ++k) {
                    cov += Math.max(0.0f, Math.min(1.0f, (float)(c + 1) - e[k]));
                }
                if ((cov /= 4.0f) <= 0.01f) continue;
                int col = Gfx.argb(rgb, a * cov);
                g.method_25294(x0 + c, yt, x0 + c + 1, yt + 1, col);
                g.method_25294(x1 - c - 1, yt, x1 - c, yt + 1, col);
                if (yb == yt) continue;
                g.method_25294(x0 + c, yb, x0 + c + 1, yb + 1, col);
                g.method_25294(x1 - c - 1, yb, x1 - c, yb + 1, col);
            }
        }
        g.method_51448().popMatrix();
    }

    public static void aaCircle(class_332 g, float cx, float cy, float r, int rgb, float a) {
        Gfx.aaRound(g, cx - r, cy - r, r * 2.0f, r * 2.0f, r, rgb, a);
    }

    public static void aaStroke(class_332 g, float[] xs, float[] ys, float width, int rgb, float a) {
        float step = Math.max(0.5f, width * 0.45f);
        int i = 0;
        while (i + 1 < xs.length) {
            float dx = xs[i + 1] - xs[i];
            float dy = ys[i + 1] - ys[i];
            float len = (float)Math.sqrt(dx * dx + dy * dy);
            int n = Math.max(1, (int)(len / step));
            for (int k = 0; k <= n; ++k) {
                float t = (float)k / (float)n;
                Gfx.aaCircle(g, xs[i] + dx * t, ys[i] + dy * t, width / 2.0f, rgb, a);
            }
            ++i;
        }
    }

    private static void roundRows(class_332 g, int x, int y, int w, int h, int r, int col) {
        g.method_25294(x, y + r, x + w, y + h - r, col);
        for (int i = 0; i < r; ++i) {
            double dy = (double)(r - i) - 0.5;
            int dx = (int)Math.round((double)r - Math.sqrt(Math.max(0.0, (double)(r * r) - dy * dy)));
            g.method_25294(x + dx, y + i, x + w - dx, y + i + 1, col);
            g.method_25294(x + dx, y + h - i - 1, x + w - dx, y + h - i, col);
        }
    }

    public static void glow(class_332 g, int x, int y, int w, int h, int r, int rgb, float strength, int size) {
        if (size < 7 || strength <= 0.05f || alpha <= 0.01f) {
            return;
        }
        float k = Math.min(1.0f, strength);
        Gfx.aaRound(g, x - 3, y - 1, w + 6, h + 6, r + 3, 0, 0.14f * k);
    }

    public static void flowBorder(class_332 g, int x, int y, int w, int h, int c1, int c2, float a, float phase) {
        Gfx.flowBorder(g, x, y, w, h, c1, c2, a, phase, 1.0f);
    }

    public static void circle(class_332 g, float cx, float cy, float r, int rgb, float a) {
        int ri = Math.max(1, Math.round(r));
        Gfx.round(g, Math.round(cx) - ri, Math.round(cy) - ri, ri * 2, ri * 2, ri, rgb, a);
    }

    public static void flowBorder(class_332 g, int x, int y, int w, int h, int c1, int c2, float a, float phase, float portion) {
        float k = Math.max(0.0f, Math.min(1.0f, a)) * Math.max(0.0f, Math.min(1.0f, portion));
        if (k <= 0.01f) {
            return;
        }
        Gfx.rect(g, x, y, w, 1, 0xFFFFFF, 0.1f * k);
        Gfx.rect(g, x, y + h - 1, w, 1, 0xFFFFFF, 0.04f * k);
    }

    public static void hGradient(class_332 g, int x, int y, int w, int h, int c1, int c2, float a) {
        if (w <= 0 || h <= 0) {
            return;
        }
        int step = 2;
        for (int i = 0; i < w; i += step) {
            g.method_25294(x + i, y, x + Math.min(w, i + step), y + h, Gfx.argb(Gfx.lerpColor(c1, c2, (float)i / (float)w), a));
        }
    }

    public static void vGradient(class_332 g, int x, int y, int w, int h, int top, int bottom, float aTop, float aBottom) {
        g.method_25296(x, y, x + w, y + h, Gfx.argb(top, aTop), Gfx.argb(bottom, aBottom));
    }

    public static class_327 font() {
        return class_310.method_1551().field_1772;
    }

    private static int uiIndex() {
        return NeonLyrics.cfg().uiFont;
    }

    public static boolean clean() {
        int i = Gfx.uiIndex();
        return i >= 0 && i < FONT_IDS.length;
    }

    private static class_11719 resolve(Role r) {
        if (r == Role.LYRICS) {
            int i = NeonLyrics.cfg().lyricsFont;
            return i >= 0 && i < FONT_IDS.length ? FD[i][2] : null;
        }
        if (!Gfx.clean()) {
            return null;
        }
        return FD[Gfx.uiIndex()][r == Role.BOLD ? 1 : 0];
    }

    public static void textIn(class_332 g, String s, int x, int y, int rgb, float a, int fontIdx, boolean bold) {
        if (a * alpha < 0.03f) {
            return;
        }
        class_5250 c = fontIdx >= 0 && fontIdx < FONT_IDS.length ? class_2561.method_43470((String)s).method_27696(class_2583.field_24360.method_27704(FD[fontIdx][bold ? 1 : 0])) : class_2561.method_43470((String)s);
        g.method_51439(Gfx.font(), (class_2561)c, x, y, Gfx.argb(rgb, a), false);
    }

    public static int wIn(String s, int fontIdx, boolean bold) {
        if (fontIdx < 0 || fontIdx >= FONT_IDS.length) {
            return Gfx.font().method_1727(s);
        }
        return Gfx.font().method_27525((class_5348)class_2561.method_43470((String)s).method_27696(class_2583.field_24360.method_27704(FD[fontIdx][bold ? 1 : 0])));
    }

    public static Role useFont(Role f) {
        Role old = cur;
        cur = f;
        return old;
    }

    public static class_2561 comp(String s, Role r) {
        class_11719 f = Gfx.resolve(r);
        return f != null ? class_2561.method_43470((String)s).method_27696(class_2583.field_24360.method_27704(f)) : class_2561.method_43470((String)s);
    }

    public static int w(String s) {
        return Gfx.w(s, cur);
    }

    public static int w(String s, Role r) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        class_11719 f = Gfx.resolve(r);
        if (f == null) {
            return Gfx.font().method_1727(s);
        }
        Map m = WIDTHS.computeIfAbsent(f, k -> new HashMap());
        Integer v = (Integer)m.get(s);
        if (v == null) {
            if (m.size() > 3000) {
                m.clear();
            }
            v = Gfx.font().method_27525((class_5348)Gfx.comp(s, r));
            m.put(s, v);
        }
        return v;
    }

    public static void clearWidths() {
        WIDTHS.clear();
    }

    public static void text(class_332 g, String s, int x, int y, int rgb, float a, boolean shadow) {
        if (a * alpha < 0.03f || s == null) {
            return;
        }
        curY = y;
        if (Gfx.resolve(cur) == null) {
            g.method_51433(Gfx.font(), s, x, y, Gfx.argb(rgb, a), shadow);
            return;
        }
        class_2561 c = Gfx.comp(s, cur);
        if (shadow && NeonLyrics.cfg().quality > 0) {
            g.method_51448().pushMatrix();
            g.method_51448().translate(0.5f, 0.7f);
            g.method_51439(Gfx.font(), c, x, y, Gfx.argb(328458, a * 0.45f), false);
            g.method_51448().popMatrix();
        }
        g.method_51439(Gfx.font(), c, x, y, Gfx.argb(rgb, a), false);
    }

    public static void textBold(class_332 g, String s, int x, int y, int rgb, float a, boolean shadow) {
        Role o = Gfx.useFont(BOLD);
        Gfx.text(g, s, x, y, rgb, a, shadow);
        Gfx.useFont(o);
    }

    public static void text(class_332 g, String s, int x, int y, int rgb) {
        Gfx.text(g, s, x, y, rgb, 1.0f, false);
    }

    public static void center(class_332 g, String s, int cx, int y, int rgb, float a, boolean shadow) {
        Gfx.text(g, s, cx - Gfx.w(s) / 2, y, rgb, a, shadow);
    }

    public static void right(class_332 g, String s, int rx, int y, int rgb, float a, boolean shadow) {
        Gfx.text(g, s, rx - Gfx.w(s), y, rgb, a, shadow);
    }

    public static void glowText(class_332 g, String s, int x, int y, int rgb, float a) {
        Role o = Gfx.useFont(BOLD);
        Gfx.text(g, s, x, y, Gfx.brighten(rgb, 0.5f), a, false);
        Gfx.useFont(o);
    }

    public static void rainbowText(class_332 g, String s, int x, int y, int c1, int c2, float a, float phase) {
        Gfx.textBold(g, s, x, y, 0xFFFFFF, a, false);
    }

    public static String ellipsize(String s, int maxW) {
        if (s == null) {
            return "";
        }
        if (Gfx.w(s) <= maxW) {
            return s;
        }
        while (s.length() > 1 && Gfx.w(s + "\u2026") > maxW) {
            s = s.substring(0, s.length() - 1);
        }
        return s + "\u2026";
    }

    public static List<String> wrap(String s, int maxW) {
        ArrayList<String> out = new ArrayList<String>();
        for (String para : s.split("\n")) {
            StringBuilder line = new StringBuilder();
            for (String w : para.split(" ")) {
                String test;
                String string = test = line.length() == 0 ? w : String.valueOf(line) + " " + w;
                if (Gfx.w(test) > maxW && line.length() > 0) {
                    out.add(line.toString());
                    line = new StringBuilder(w);
                    continue;
                }
                line = new StringBuilder(test);
            }
            out.add(line.toString());
        }
        return out;
    }

    static {
        String[] kinds = new String[]{"_ui", "_bold", "_lyrics"};
        for (int i = 0; i < FONT_IDS.length; ++i) {
            for (int k = 0; k < 3; ++k) {
                Gfx.FD[i][k] = new class_11719.class_11721(class_2960.method_60655((String)"neonlyrics", (String)(FONT_IDS[i] + kinds[k])));
            }
        }
        cur = Role.UI;
        WIDTHS = new HashMap<class_11719, Map<String, Integer>>();
    }

    public static enum Role {
        UI,
        BOLD,
        LYRICS;

    }
}

