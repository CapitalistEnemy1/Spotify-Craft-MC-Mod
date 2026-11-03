/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11908
 *  net.minecraft.class_11909
 *  net.minecraft.class_124
 *  net.minecraft.class_156
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_342
 *  net.minecraft.class_364
 *  net.minecraft.class_437
 */
package dev.neonlyrics.gui;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.hud.Anim;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.hud.Island;
import dev.neonlyrics.hud.Theme;
import dev.neonlyrics.lyrics.LocalMedia;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import dev.neonlyrics.util.Fx;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_156;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_437;

public class SpotifyTutorialScreen
extends class_437 {
    private static final int STEPS = 6;
    private final class_437 parent;
    private final long opened = System.currentTimeMillis();
    private long stepTime = System.currentTimeMillis();
    private int step = 0;
    private int prevStep = -1;
    private int dir = 1;
    private final List<Hit> hits = new ArrayList<Hit>();
    private final Anim.Pool pool = new Anim.Pool(14.0);
    private final Anim dots = new Anim(0.0, 14.0);
    private int mx;
    private int my;
    private int px;
    private int py;
    private int pw;
    private int ph;
    private class_342 idBox;

    public SpotifyTutorialScreen(class_437 parent) {
        super((class_2561)class_2561.method_43470((String)"Spotify Tutorial"));
        this.parent = parent;
    }

    protected void method_25426() {
        this.pw = Math.min(430, this.field_22789 - 16);
        this.ph = Math.min(250, this.field_22790 - 16);
        this.px = (this.field_22789 - this.pw) / 2;
        this.py = (this.field_22790 - this.ph) / 2;
        this.idBox = new class_342(this.field_22793, this.px + 22, this.py + 118, 170, 12, (class_2561)class_2561.method_43470((String)""));
        this.idBox.method_1858(false);
        this.idBox.method_1880(64);
        this.idBox.method_1868(-790529);
        this.idBox.method_1852(NeonLyrics.cfg().spotifyClientId);
        this.idBox.method_47404((class_2561)class_2561.method_43470((String)Fx.t("neonlyrics.hint.clientid", new Object[0])).method_27692(class_124.field_1063));
        this.method_37063(this.idBox);
    }

    private void go(int s) {
        if ((s = Math.max(0, Math.min(5, s))) == this.step) {
            return;
        }
        this.dir = s > this.step ? 1 : -1;
        this.prevStep = this.step;
        this.step = s;
        this.stepTime = System.currentTimeMillis();
        this.method_25395(null);
        Fx.sound(Fx.Sfx.CLICK);
    }

    public void method_25420(class_332 g, int mouseX, int mouseY, float delta) {
        if (NeonLyrics.cfg().blur && this.field_22787 != null && this.field_22787.field_1687 != null) {
            super.method_25420(g, mouseX, mouseY, delta);
        }
        float a = Anim.outCubic(Anim.since(this.opened, 250L));
        Gfx.alpha = 1.0f;
        Gfx.vGradient(g, 0, 0, this.field_22789, this.field_22790, 264711, 528910, 0.55f * a, 0.75f * a);
    }

    public void method_25394(class_332 g, int mouseX, int mouseY, float delta) {
        float open;
        this.mx = mouseX;
        this.my = mouseY;
        this.hits.clear();
        Config c = NeonLyrics.cfg();
        int acc = Theme.accent();
        Gfx.alpha = open = Anim.outCubic(Anim.since(this.opened, 300L));
        g.method_51448().pushMatrix();
        float sc = 0.94f + 0.06f * Anim.outBack(Anim.since(this.opened, 380L));
        g.method_51448().translate((float)this.px + (float)this.pw / 2.0f, (float)this.py + (float)this.ph / 2.0f);
        g.method_51448().scale(sc, sc);
        g.method_51448().translate(-((float)this.px + (float)this.pw / 2.0f), -((float)this.py + (float)this.ph / 2.0f));
        Gfx.glow(g, this.px, this.py, this.pw, this.ph, 10, acc, 0.7f, 10);
        Gfx.round(g, this.px, this.py, this.pw, this.ph, 10, 528910, 0.97f);
        Gfx.vGradient(g, this.px + 1, this.py + 1, this.pw - 2, 50, acc, 528910, 0.08f, 0.0f);
        Gfx.flowBorder(g, this.px + 4, this.py, this.pw - 8, this.ph, acc, Theme.accent2(), 0.8f, 0.0f, Anim.outCubic(Anim.since(this.opened, 700L)));
        g.method_51448().pushMatrix();
        g.method_51448().translate((float)(this.px + 22), (float)(this.py + 18));
        g.method_51448().scale(1.2f, 1.2f);
        g.method_51427(new class_1799((class_1935)class_1802.field_8565), -8, -8);
        g.method_51448().popMatrix();
        Gfx.text(g, Fx.t("neonlyrics.tut.title", new Object[0]), this.px + 36, this.py + 10, 0xFFFFFF, 1.0f, true);
        Gfx.text(g, Fx.t("neonlyrics.tut.stepof", this.step + 1, 6), this.px + 36, this.py + 21, 8826011, 1.0f, false);
        float d = this.dots.target(this.step).get();
        int dw = 72;
        int dx = this.px + this.pw - 16 - dw;
        for (int i = 0; i < 6; ++i) {
            boolean done = i < this.step;
            Gfx.round(g, dx + i * 12, this.py + 15, 8, 4, 2, done ? acc : 1980974, done ? 0.7f : 1.0f);
            this.hit(dx + i * 12 - 2, this.py + 11, 12, 12, () -> {}, i);
        }
        Gfx.round(g, dx + Math.round(d * 12.0f), this.py + 14, 8, 6, 3, acc, 1.0f);
        Gfx.rect(g, this.px + 14, this.py + 34, this.pw - 28, 1, 3036742, 0.8f);
        float e = Anim.outCubic(Anim.since(this.stepTime, 300L));
        g.method_44379(this.px + 4, this.py + 36, this.px + this.pw - 4, this.py + this.ph - 30);
        if (this.prevStep >= 0 && e < 1.0f) {
            float oa = Gfx.alpha;
            Gfx.alpha = open * (1.0f - e);
            g.method_51448().pushMatrix();
            g.method_51448().translate((float)(-this.dir) * e * 30.0f, 0.0f);
            this.content(g, this.prevStep, false);
            g.method_51448().popMatrix();
            Gfx.alpha = oa;
        }
        float oa = Gfx.alpha;
        Gfx.alpha = open * e;
        g.method_51448().pushMatrix();
        g.method_51448().translate(this.prevStep >= 0 ? (float)this.dir * (1.0f - e) * 30.0f : 0.0f, this.prevStep < 0 ? (1.0f - e) * 8.0f : 0.0f);
        this.content(g, this.step, true);
        g.method_51448().popMatrix();
        Gfx.alpha = oa;
        g.method_44380();
        int by = this.py + this.ph - 24;
        Gfx.rect(g, this.px + 14, by - 6, this.pw - 28, 1, 3036742, 0.8f);
        this.button(g, "close", this.px + 14, by, 80, 16, Fx.t("neonlyrics.tut.close", new Object[0]), 8826011, false, this::method_25419);
        if (this.step > 0) {
            this.button(g, "back", this.px + this.pw - 14 - 80 - 6 - 80, by, 80, 16, Fx.t("neonlyrics.tut.back", new Object[0]), 8826011, false, () -> this.go(this.step - 1));
        }
        boolean last = this.step == 5;
        this.button(g, "next", this.px + this.pw - 14 - 80, by, 80, 16, last ? Fx.t("neonlyrics.tut.finish", new Object[0]) : Fx.t("neonlyrics.tut.next", new Object[0]), acc, true, () -> {
            if (last) {
                this.method_25419();
            } else {
                this.go(this.step + 1);
            }
        });
        g.method_51448().popMatrix();
        this.idBox.field_22763 = this.idBox.field_22764 = this.step == 4 && e > 0.9f;
        if (!this.idBox.field_22764 && this.idBox.method_25370()) {
            this.method_25395(null);
        }
        if (this.step == 4) {
            c.spotifyClientId = this.idBox.method_1882().trim();
        }
        Gfx.alpha = 1.0f;
        super.method_25394(g, mouseX, mouseY, delta);
    }

    private void content(class_332 g, int s, boolean live) {
        int acc = Theme.accent();
        int lx = this.px + 18;
        int ly = this.py + 44;
        int lw = s == 5 ? this.pw - 36 : 196;
        int rx = this.px + 224;
        int ry = this.py + 44;
        int rw = this.pw - 224 - 16;
        int rh = this.ph - 44 - 34;
        String title = Fx.t("neonlyrics.tut.s" + s + ".title", new Object[0]);
        Gfx.text(g, title, lx, ly, 0xFFFFFF, 1.0f, true);
        int ty = ly + 14;
        for (String string : Gfx.wrap(Fx.t("neonlyrics.tut.s" + s + ".text", new Object[0]), lw)) {
            Gfx.text(g, string, lx, ty, 14939884, 0.88f, false);
            ty += 10;
        }
        ty += 6;
        switch (s) {
            case 0: {
                String[] need;
                for (String n : need = new String[]{Fx.t("neonlyrics.tut.need1", new Object[0]), Fx.t("neonlyrics.tut.need2", new Object[0]), Fx.t("neonlyrics.tut.need3", new Object[0])}) {
                    Gfx.circle(g, lx + 3, ty + 3, 2.0f, acc, 1.0f);
                    Gfx.text(g, n, lx + 10, ty, 14939884, 0.9f, false);
                    ty += 11;
                }
                if (LocalMedia.WINDOWS) {
                    this.button(g, "useauto" + live, lx, ty + 6, 150, 16, Fx.t("neonlyrics.tut.useauto", new Object[0]), 6022299, true, () -> {
                        SpotifyClient.setMode(0);
                        this.method_25419();
                    }, live);
                }
                this.drawWelcome(g, rx, ry, rw, rh);
                break;
            }
            case 1: {
                this.button(g, "dash" + live, lx, ty, 120, 16, Fx.t("neonlyrics.spotify.dashboard", new Object[0]), 6022299, true, () -> class_156.method_668().method_670("https://developer.spotify.com/dashboard"), live);
                this.drawBrowser(g, rx, ry, rw, rh, 1);
                break;
            }
            case 2: {
                Gfx.round(g, lx, ty - 2, lw, 13, 4, 1319965, 0.95f);
                Gfx.text(g, "http://127.0.0.1:8789/callback", lx + 5, ty + 1, 16762460, 1.0f, false);
                this.button(g, "copy" + live, lx, ty += 16, 120, 16, Fx.t("neonlyrics.tut.copyuri", new Object[0]), 6022299, true, () -> {
                    Fx.clipboard("http://127.0.0.1:8789/callback");
                    Island.notify(Fx.t("neonlyrics.toast.copiedres", new Object[0]), true);
                }, live);
                this.drawBrowser(g, rx, ry, rw, rh, 2);
                break;
            }
            case 3: {
                this.drawBrowser(g, rx, ry, rw, rh, 3);
                break;
            }
            case 4: {
                int fy = this.py + 114;
                Gfx.text(g, Fx.t("neonlyrics.spotify.clientid", new Object[0]).toUpperCase(), lx, fy - 12, 8826011, 1.0f, false);
                boolean bl = this.idBox.method_25370();
                Gfx.glow(g, lx, fy, 180, 18, 5, acc, bl ? 0.8f : 0.0f, 4);
                Gfx.round(g, lx, fy, 180, 18, 5, bl ? 1980974 : 1319965, 0.95f);
                SpotifyClient.Status st = SpotifyClient.status();
                boolean ok = st == SpotifyClient.Status.CONNECTED;
                this.button(g, "connect" + live, lx, fy + 24, 120, 16, ok ? Fx.t("neonlyrics.spotify.st.on", new Object[0]) : Fx.t("neonlyrics.spotify.connect", new Object[0]), ok ? 6022299 : acc, !ok, () -> {
                    NeonLyrics.cfg().spotifyClientId = this.idBox.method_1882().trim();
                    NeonLyrics.saveConfig();
                    SpotifyClient.setMode(1);
                    if (SpotifyClient.status() != SpotifyClient.Status.CONNECTED) {
                        SpotifyClient.login();
                    }
                }, live);
                this.drawStatus(g, rx, ry, rw, rh);
                break;
            }
            default: {
                this.drawHelp(g, lx, ty, this.pw - 36, this.py + this.ph - 34 - ty);
            }
        }
    }

    private void drawWelcome(class_332 g, int x, int y, int w, int h) {
        int i;
        int acc = Theme.accent();
        Gfx.round(g, x, y, w, h, 8, 529167, 0.95f);
        Gfx.vGradient(g, x + 6, y + 6, w - 12, h - 12, 529167, 1784365, 1.0f, 1.0f);
        Random r = new Random(4L);
        double t = (double)System.currentTimeMillis() / 1000.0;
        for (i = 0; i < 40; ++i) {
            float tw = (float)(0.5 + 0.5 * Math.sin(t * 2.0 + (double)i));
            Gfx.rect(g, x + 8 + r.nextInt(w - 16), y + 8 + r.nextInt(h - 40), 1, 1, 0xFFFFFF, 0.3f + 0.6f * tw);
        }
        for (i = 0; i < w - 12; i += 2) {
            float hh = (float)(14.0 + 8.0 * Math.sin((double)i * 0.12 + t));
            int col = Gfx.lerpColor(6085030, 14086883, (float)(0.5 + 0.5 * Math.sin((double)i * 0.1 + t)));
            g.method_25296(x + 6 + i, y + 26, x + 8 + i, (int)((float)(y + 26) + hh), Gfx.argb(col, 0.0f), Gfx.argb(col, 0.4f));
        }
        Gfx.round(g, x + w / 2 - 44, y + 10, 88, 11, 5, 726803, 0.9f);
        Gfx.round(g, x + w / 2 - 20, y + 12, 22, 7, 2, acc, 0.6f);
        g.method_51448().pushMatrix();
        g.method_51448().translate((float)x + (float)w / 2.0f, (float)y + (float)h / 2.0f + 8.0f);
        g.method_51448().rotate(-0.08f);
        g.method_51448().scale(1.8f, 1.8f);
        String demo = Fx.t("neonlyrics.tut.preview", new Object[0]);
        Gfx.text(g, demo, -Gfx.w(demo) / 2, 0, 10479300, 0.95f, true);
        g.method_51448().popMatrix();
        Gfx.rect(g, x + 6, y + h - 14, w - 12, 8, 3037738, 0.95f);
    }

    private void drawBrowser(class_332 g, int x, int y, int w, int h, int which) {
        int acc = Theme.accent();
        float pulse = Anim.pulse(1100.0);
        Gfx.round(g, x, y, w, h, 8, 15592946, 0.98f);
        Gfx.round(g, x, y, w, 14, 8, 0xD6D6DE, 1.0f);
        Gfx.rect(g, x, y + 8, w, 6, 0xD6D6DE, 1.0f);
        for (int i = 0; i < 3; ++i) {
            Gfx.circle(g, x + 8 + i * 6, y + 7, 2.0f, (new int[]{0xFF5F57, 16694318, 2672704})[i], 1.0f);
        }
        Gfx.round(g, x + 30, y + 3, w - 38, 8, 3, 0xFFFFFF, 1.0f);
        Gfx.text(g, Gfx.ellipsize("developer.spotify.com/dashboard", w - 44), x + 34, y + 3, 4877919, 0.9f, false);
        int cy = y + 20;
        int dark = 0x12211B;
        int gray = 8826011;
        int green = 1947988;
        switch (which) {
            case 1: {
                Gfx.text(g, "Dashboard", x + 8, cy, dark, 1.0f, false);
                cy += 16;
                for (int i = 0; i < 3; ++i) {
                    Gfx.round(g, x + 8, cy + i * 14, w - 16, 10, 3, 14474468, 1.0f);
                }
                int bx = x + w - 66;
                int by = y + 18;
                Gfx.glow(g, bx, by, 58, 13, 6, green, 0.6f + 0.6f * pulse, 5);
                Gfx.round(g, bx, by, 58, 13, 6, green, 1.0f);
                Gfx.center(g, "Create app", bx + 29, by + 3, 0xFFFFFF, 1.0f, false);
                this.arrow(g, bx - 14, by + 6, pulse);
                break;
            }
            case 2: {
                Gfx.text(g, "Create app", x + 8, cy, dark, 1.0f, false);
                cy += 13;
                String[][] rows = new String[][]{{"App name", "Minecraft Lyrics"}, {"App description", "Lyrics im Spiel"}, {"Redirect URIs", "http://127.0.0.1:8789/\u2026"}};
                for (int i = 0; i < rows.length; ++i) {
                    boolean hl = i == 2;
                    Gfx.text(g, rows[i][0], x + 8, cy, gray, 1.0f, false);
                    cy += 9;
                    if (hl) {
                        Gfx.glow(g, x + 8, cy, w - 16, 11, 3, acc, 0.6f + 0.6f * pulse, 4);
                    }
                    Gfx.round(g, x + 8, cy, w - 16, 11, 3, 0xFFFFFF, 1.0f);
                    Gfx.round(g, x + 8, cy, w - 16, 1, 1, hl ? acc : 13158610, 1.0f);
                    Gfx.text(g, Gfx.ellipsize(rows[i][1], w - 24), x + 11, cy + 2, hl ? acc : dark, 1.0f, false);
                    if (hl) {
                        Gfx.round(g, x + w - 30, cy + 1, 20, 9, 3, 0xE4E4EC, 1.0f);
                    }
                    if (hl) {
                        Gfx.center(g, "Add", x + w - 20, cy + 2, dark, 0.8f, false);
                    }
                    cy += 14;
                }
                Gfx.round(g, x + 8, cy, 8, 8, 2, green, 1.0f);
                Gfx.rect(g, x + 10, cy + 4, 2, 1, 0xFFFFFF, 1.0f);
                Gfx.rect(g, x + 11, cy + 5, 1, 1, 0xFFFFFF, 1.0f);
                Gfx.rect(g, x + 12, cy + 3, 1, 2, 0xFFFFFF, 1.0f);
                Gfx.rect(g, x + 13, cy + 2, 1, 1, 0xFFFFFF, 1.0f);
                Gfx.text(g, "Web API", x + 20, cy, dark, 1.0f, false);
                Gfx.round(g, x + w - 44, cy - 2, 36, 12, 6, green, 1.0f);
                Gfx.center(g, "Save", x + w - 26, cy + 1, 0xFFFFFF, 1.0f, false);
                break;
            }
            case 3: {
                Gfx.text(g, "Minecraft Lyrics", x + 8, cy, dark, 1.0f, false);
                Gfx.round(g, x + w - 52, cy - 2, 44, 12, 6, 14474468, 1.0f);
                Gfx.center(g, "Settings", x + w - 30, cy + 1, dark, 1.0f, false);
                Gfx.text(g, "Basic Information", x + 8, cy += 16, gray, 1.0f, false);
                Gfx.text(g, "Client ID", x + 8, cy += 12, dark, 1.0f, false);
                Gfx.glow(g, x + 8, cy += 10, w - 16, 12, 3, acc, 0.6f + 0.6f * pulse, 4);
                Gfx.round(g, x + 8, cy, w - 16, 12, 3, 0xFFFFFF, 1.0f);
                Gfx.text(g, Gfx.ellipsize("3f9a1c7e2b8d4e60a5c1\u2026", w - 40), x + 11, cy + 2, dark, 1.0f, false);
                Gfx.round(g, x + w - 20, cy + 2, 8, 8, 2, acc, 1.0f);
                Gfx.round(g, x + w - 18, cy + 4, 4, 4, 1, 0xFFFFFF, 1.0f);
                Gfx.text(g, "Client secret", x + 8, cy += 18, gray, 1.0f, false);
                Gfx.text(g, Fx.t("neonlyrics.tut.nosecret", new Object[0]), x + 8, cy + 10, 12597547, 1.0f, false);
                break;
            }
        }
    }

    private void arrow(class_332 g, int x, int y, float p) {
        int o = (int)(p * 3.0f);
        int col = Theme.accent();
        Gfx.rect(g, x - 8 + o, y, 8, 2, col, 1.0f);
        Gfx.rect(g, x - 2 + o, y - 2, 2, 6, col, 1.0f);
        Gfx.rect(g, x + o, y - 1, 2, 4, col, 1.0f);
    }

    private void drawStatus(class_332 g, int x, int y, int w, int h) {
        int acc = Theme.accent();
        SpotifyClient.Status st = SpotifyClient.status();
        NowPlaying np = SpotifyClient.now();
        Gfx.round(g, x, y, w, h, 8, 1319965, 0.95f);
        int col = switch (st) {
            case SpotifyClient.Status.CONNECTED -> 6022299;
            case SpotifyClient.Status.ERROR -> 16735354;
            case SpotifyClient.Status.WAITING_LOGIN -> 16762460;
            default -> 8826011;
        };
        Gfx.text(g, Fx.t("neonlyrics.tut.status", new Object[0]), x + 10, y + 8, 8826011, 1.0f, false);
        float p = st == SpotifyClient.Status.WAITING_LOGIN ? Anim.pulse(700.0) : 1.0f;
        Gfx.circle(g, x + 13, y + 26, 4.0f, col, 0.4f + 0.6f * p);
        String s = switch (st) {
            default -> throw new MatchException(null, null);
            case SpotifyClient.Status.NOT_CONFIGURED -> Fx.t("neonlyrics.spotify.st.setup", new Object[0]);
            case SpotifyClient.Status.DISCONNECTED -> Fx.t("neonlyrics.spotify.st.off", new Object[0]);
            case SpotifyClient.Status.WAITING_LOGIN -> Fx.t("neonlyrics.spotify.st.wait", new Object[0]);
            case SpotifyClient.Status.CONNECTED -> Fx.t("neonlyrics.spotify.st.on", new Object[0]);
            case SpotifyClient.Status.ERROR -> Fx.t("neonlyrics.spotify.st.error", SpotifyClient.lastError());
        };
        int ty = y + 22;
        for (String string : Gfx.wrap(s, w - 30)) {
            Gfx.text(g, string, x + 22, ty, col, 1.0f, false);
            ty += 10;
        }
        ty += 6;
        if (st == SpotifyClient.Status.CONNECTED) {
            Object song = np.hasTrack() ? "\u266a " + np.title : Fx.t("neonlyrics.tut.playsong", new Object[0]);
            for (String line : Gfx.wrap((String)song, w - 20)) {
                Gfx.text(g, line, x + 10, ty, 14939884, 0.9f, false);
                ty += 10;
            }
        } else if (st == SpotifyClient.Status.WAITING_LOGIN) {
            for (String string : Gfx.wrap(Fx.t("neonlyrics.tut.browserhint", new Object[0]), w - 20)) {
                Gfx.text(g, string, x + 10, ty, 8826011, 0.9f, false);
                ty += 10;
            }
        }
        Gfx.rect(g, x + 10, y + h - 20, w - 20, 1, 3036742, 0.8f);
        Gfx.text(g, Gfx.ellipsize(Fx.t("neonlyrics.tut.local", new Object[0]), w - 20), x + 10, y + h - 14, 8826011, 0.85f, false);
        if (st == SpotifyClient.Status.CONNECTED) {
            Gfx.round(g, x, y, 3, h, 2, acc, 1.0f);
        }
    }

    private void drawHelp(class_332 g, int x, int y, int w, int h) {
        int cw = (w - 6) / 2;
        int chh = (h - 6) / 2;
        block0: for (int i = 0; i < 4; ++i) {
            int bx = x + i % 2 * (cw + 6);
            int by = y + i / 2 * (chh + 6);
            Gfx.round(g, bx, by, cw, chh, 6, 1319965, 0.95f);
            Gfx.round(g, bx, by + 5, 2, chh - 10, 1, 16762460, 1.0f);
            Gfx.text(g, Gfx.ellipsize(Fx.t("neonlyrics.tut.err" + (i + 1), new Object[0]), cw - 14), bx + 8, by + 5, 16762460, 1.0f, false);
            int ty = by + 16;
            for (String line : Gfx.wrap(Fx.t("neonlyrics.tut.fix" + (i + 1), new Object[0]), cw - 14)) {
                if (ty > by + chh - 9) continue block0;
                Gfx.text(g, line, bx + 8, ty, 14939884, 0.85f, false);
                ty += 9;
            }
        }
    }

    private void button(class_332 g, String id, int x, int y, int w, int h, String label, int color, boolean filled, Runnable r) {
        this.button(g, id, x, y, w, h, label, color, filled, r, true);
    }

    private void button(class_332 g, String id, int x, int y, int w, int h, String label, int color, boolean filled, Runnable r, boolean clickable) {
        boolean hover = clickable && this.mx >= x && this.my >= y && this.mx < x + w && this.my < y + h;
        float hv = this.pool.to("b" + id, hover ? 1.0 : 0.0);
        Gfx.glow(g, x, y, w, h, 5, color, hv * 0.8f, 4);
        Gfx.round(g, x, y, w, h, 5, filled ? color : Gfx.lerpColor(1319965, color, 0.12f + 0.2f * hv), filled ? 0.3f + 0.25f * hv : 0.95f);
        Gfx.center(g, Gfx.ellipsize(label, w - 6), x + w / 2, y + (h - 8) / 2, Gfx.lerpColor(Gfx.lerpColor(14939884, color, 0.3f), 0xFFFFFF, hv * 0.6f), 1.0f, false);
        if (clickable) {
            this.hits.add(new Hit(x, y, w, h, () -> {
                Fx.sound(Fx.Sfx.CLICK);
                r.run();
            }));
        }
    }

    private void hit(int x, int y, int w, int h, Runnable r, int stepIndex) {
        this.hits.add(new Hit(x, y, w, h, () -> this.go(stepIndex)));
    }

    public boolean method_25402(class_11909 event, boolean doubleClick) {
        if (super.method_25402(event, doubleClick)) {
            return true;
        }
        if (event.method_74245() != 0) {
            return false;
        }
        for (int i = this.hits.size() - 1; i >= 0; --i) {
            Hit h = this.hits.get(i);
            if (!(event.comp_4798() >= (double)h.x()) || !(event.comp_4799() >= (double)h.y()) || !(event.comp_4798() < (double)(h.x() + h.w())) || !(event.comp_4799() < (double)(h.y() + h.h()))) continue;
            this.method_25395(null);
            h.r().run();
            return true;
        }
        this.method_25395(null);
        return false;
    }

    public boolean method_25404(class_11908 event) {
        int k = event.comp_4795();
        if (this.method_25399() == null) {
            if (k == 262 || k == 257 || k == 335) {
                if (this.step == 5) {
                    this.method_25419();
                } else {
                    this.go(this.step + 1);
                }
                return true;
            }
            if (k == 263) {
                this.go(this.step - 1);
                return true;
            }
        }
        return super.method_25404(event);
    }

    public void method_25419() {
        Config c = NeonLyrics.cfg();
        c.spotifyClientId = this.idBox.method_1882().trim();
        c.spotifyTutorialSeen = true;
        NeonLyrics.saveConfig();
        this.field_22787.method_1507(this.parent);
    }

    public boolean method_25421() {
        return false;
    }

    private record Hit(int x, int y, int w, int h, Runnable r) {
    }
}

