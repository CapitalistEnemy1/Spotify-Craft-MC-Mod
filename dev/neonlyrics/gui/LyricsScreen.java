/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.minecraft.class_10799
 *  net.minecraft.class_11719
 *  net.minecraft.class_11719$class_11721
 *  net.minecraft.class_11908
 *  net.minecraft.class_11909
 *  net.minecraft.class_124
 *  net.minecraft.class_156
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_2960
 *  net.minecraft.class_304
 *  net.minecraft.class_332
 *  net.minecraft.class_342
 *  net.minecraft.class_364
 *  net.minecraft.class_437
 *  net.minecraft.class_5348
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fc
 *  org.joml.Vector2f
 */
package dev.neonlyrics.gui;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.cosmetic.Accessories;
import dev.neonlyrics.cosmetic.DeathFx;
import dev.neonlyrics.cosmetic.PhoneMenu;
import dev.neonlyrics.gui.SpotifyTutorialScreen;
import dev.neonlyrics.hud.Anim;
import dev.neonlyrics.hud.Cover;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.hud.Icons;
import dev.neonlyrics.hud.Island;
import dev.neonlyrics.hud.SkyThumbs;
import dev.neonlyrics.hud.Theme;
import dev.neonlyrics.lyrics.Favorites;
import dev.neonlyrics.lyrics.LocalMedia;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import dev.neonlyrics.sky.SkyPreset;
import dev.neonlyrics.sky.WorldSky;
import dev.neonlyrics.util.AutoPerf;
import dev.neonlyrics.util.FpsBoost;
import dev.neonlyrics.util.Fx;
import dev.neonlyrics.util.Profiles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.class_10799;
import net.minecraft.class_11719;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_437;
import net.minecraft.class_5348;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

public class LyricsScreen
extends class_437 {
    private static final int COL_BG = 726803;
    private static final int GROUP = 0x12211B;
    private static final int GROUP_HI = 1716263;
    private static final int LINE = 2376247;
    private static final int TEXT = 14939884;
    private static final int SUB = 8826011;
    private static final int DIM = 4877919;
    private static final int TRACK = 2771517;
    private static final int KNOB_OFF = 7905424;
    private static final int INK = 726803;
    private static final Map<T, class_11719> FONTS = new EnumMap<T, class_11719>(T.class);
    private final Map<T, Map<String, class_2561>> comps = new EnumMap<T, Map<String, class_2561>>(T.class);
    private final Map<T, Map<String, Integer>> widths = new EnumMap<T, Map<String, Integer>>(T.class);
    private static final SkyPreset[] SKY_ORDER;
    private static final int[] DARK_COLS;
    private static final int[] BRIGHT;
    private static final String[] LANGS;
    private static final int[] TARGETS;
    private static final int RAIL = 56;
    private static final int ROW = 38;
    private static Page lastPage;
    private Page page;
    private long pageTime;
    private final long openTime = System.currentTimeMillis();
    private long closeTime = -1L;
    private int dir = 1;
    private final Anim.Pool pool = new Anim.Pool(12.0);
    private final Map<String, Long> pressed = new HashMap<String, Long>();
    private final List<Hit> hits = new ArrayList<Hit>();
    private final List<Slider> sliders = new ArrayList<Slider>();
    private Slider activeSlider;
    private int mx;
    private int my;
    private float S = 1.0f;
    private int W;
    private int H;
    private int colX;
    private int colY;
    private int colW;
    private int colH;
    private int vx;
    private int vy;
    private int vw;
    private int vh;
    private float stageK = 0.0f;
    private float scroll = 0.0f;
    private float scrollTarget = 0.0f;
    private int contentH = 0;
    private int ly;
    private int rowIdx;
    private float rowA = 1.0f;
    private boolean inContent;
    private boolean firstInGroup;
    private long lastNs = System.nanoTime();
    private class_332 gfx;
    private class_342 idBox;
    private int idX;
    private int idY;
    private int idW;
    private boolean idWanted;
    private String lastTrack = "";
    private long trackAt = 0L;
    private float spin = 0.0f;
    private float vizLevel = 0.0f;
    private float frameDt = 0.016f;
    private final Map<String, float[]> springs = new HashMap<String, float[]>();
    private final Map<String, Ripple> ripples = new HashMap<String, Ripple>();
    private final Map<Integer, Long> seen = new HashMap<Integer, Long>();
    private String curRid;
    private boolean floating;
    private final Matrix3x2f floatInv = new Matrix3x2f();

    private static class_11719 fd(String name) {
        return new class_11719.class_11721(class_2960.method_60655((String)"neonlyrics", (String)name));
    }

    public static SkyPreset[] skyOrder() {
        return (SkyPreset[])SKY_ORDER.clone();
    }

    public LyricsScreen() {
        this(lastPage);
    }

    public LyricsScreen(boolean floating) {
        this(lastPage);
        this.floating = floating;
    }

    public LyricsScreen(Page page) {
        super((class_2561)class_2561.method_43470((String)"SpotifyCraft"));
        this.page = page;
        this.pageTime = System.currentTimeMillis() + 520L;
    }

    private static Config c() {
        return NeonLyrics.cfg();
    }

    private static String t(String key, Object ... args) {
        return Fx.t("neonlyrics." + key, args);
    }

    protected void method_25426() {
        this.layout(SpotifyClient.now().hasTrack() ? 1.0f : 0.0f);
        this.stageK = SpotifyClient.now().hasTrack() ? 1.0f : 0.0f;
        this.idBox = new class_342(this.field_22793, 0, -200, 100, 12, (class_2561)class_2561.method_43470((String)""));
        this.idBox.method_1858(false);
        this.idBox.method_1880(64);
        this.idBox.method_1868(-1118479);
        this.idBox.method_1852(LyricsScreen.c().spotifyClientId);
        this.idBox.method_47404((class_2561)class_2561.method_43470((String)LyricsScreen.t("hint.clientid", new Object[0])).method_27692(class_124.field_1063));
        this.method_37063(this.idBox);
        LyricsScreen.show(this.idBox, false);
    }

    private void layout(float k) {
        this.S = Math.min(1.0f, Math.min((float)this.field_22789 / 640.0f, (float)this.field_22790 / 350.0f));
        this.W = Math.round((float)this.field_22789 / this.S);
        this.H = Math.round((float)this.field_22790 / this.S);
        int wMusic = Math.max(330, Math.min(470, Math.round((float)this.W * 0.46f)));
        int wAlone = Math.min(this.W - 60, 640);
        this.colW = Math.round((float)wAlone + (float)(wMusic - wAlone) * k);
        int xMusic = this.W - wMusic - 16;
        int xAlone = (this.W - wAlone) / 2;
        this.colX = Math.round((float)xAlone + (float)(xMusic - xAlone) * k);
        this.colY = 12;
        this.colH = this.H - 24;
        this.vx = this.colX + 56 + 18;
        this.vw = this.colW - 56 - 18 - 16;
        this.vy = this.colY + 50;
        this.vh = this.colH - 50 - 10;
    }

    private static void show(class_342 b, boolean v) {
        b.field_22764 = v;
        b.field_22763 = v;
        if (!v && b.method_25370()) {
            b.method_25365(false);
        }
    }

    private void go(Page p) {
        if (p == this.page) {
            return;
        }
        this.dir = p.ordinal() >= this.page.ordinal() ? 1 : -1;
        this.page = p;
        lastPage = p;
        this.pageTime = System.currentTimeMillis();
        this.scrollTarget = 0.0f;
        this.scroll = 0.0f;
        this.seen.clear();
        this.method_25395(null);
        Fx.sound(Fx.Sfx.CLICK);
    }

    private boolean anim() {
        return LyricsScreen.c().fxMenuAnim;
    }

    private float since() {
        return this.anim() ? (float)(System.currentTimeMillis() - this.openTime) / 1000.0f : 9.0f;
    }

    private float closeK() {
        if (this.closeTime < 0L) {
            return 1.0f;
        }
        return this.anim() ? 1.0f - Anim.outCubic(Anim.since(this.closeTime, 160L)) : 0.0f;
    }

    private static float oc(float x) {
        return Anim.outCubic(Anim.clamp(x));
    }

    private static float ob(float x) {
        return Anim.outBack(Anim.clamp(x));
    }

    public void method_25420(class_332 g, int mouseX, int mouseY, float delta) {
        if (this.floating) {
            return;
        }
        float a = LyricsScreen.oc(this.since() / 0.3f) * this.closeK();
        boolean blur = LyricsScreen.c().blur;
        if (blur && this.field_22787 != null && this.field_22787.field_1687 != null) {
            super.method_25420(g, mouseX, mouseY, delta);
        }
        Gfx.alpha = 1.0f;
        int steps = 24;
        for (int i = 0; i < steps; ++i) {
            float al;
            float f = (float)i / ((float)steps - 1.0f);
            float f2 = al = f < 0.55f ? 0.86f + -0.31f * (f / 0.55f) : 0.55f + 0.25f * ((f - 0.55f) / 0.45f);
            if (this.stageK < 0.99f) {
                al += (0.62f - al) * (1.0f - this.stageK);
            }
            int x0 = Math.round((float)(this.field_22789 * i) / (float)steps);
            int x1 = Math.round((float)(this.field_22789 * (i + 1)) / (float)steps);
            Gfx.rect(g, x0, 0, x1 - x0, this.field_22790, 199175, al * a);
        }
        float fl = Math.max(0.0f, 1.0f - Math.abs(this.since() - 0.12f) / 0.12f);
        if (fl > 0.01f) {
            Gfx.rect(g, 0, 0, this.field_22789, this.field_22790, 6085030, 0.1f * fl);
        }
    }

    public void method_25394(class_332 g, int mouseX, int mouseY, float delta) {
        if (!this.floating) {
            this.renderBody(g, mouseX, mouseY, delta);
            return;
        }
        float t = this.since();
        float ck = this.closeK();
        float wk = this.anim() ? LyricsScreen.oc(t / 0.22f) : 1.0f;
        float hk = this.anim() ? LyricsScreen.oc((t - 0.12f) / 0.38f) : 1.0f;
        float sx = 0.08f + 0.92f * wk;
        float sy = (0.03f + 0.97f * hk) * (0.03f + 0.97f * ck);
        float s0 = 0.84f;
        float bob = (float)Math.sin((double)System.currentTimeMillis() / 900.0) * (float)this.field_22790 * 0.005f;
        Matrix3x2f m = new Matrix3x2f().translate((float)this.field_22789 / 2.0f, (float)this.field_22790 / 2.0f + bob).mul((Matrix3x2fc)new Matrix3x2f(1.0f, -0.035f, 0.0f, 1.0f, 0.0f, 0.0f)).scale(s0 * sx, s0 * sy).translate((float)(-this.field_22789) / 2.0f, (float)(-this.field_22790) / 2.0f);
        m.invert(this.floatInv);
        Vector2f mp = this.floatInv.transformPosition((float)mouseX, (float)mouseY, new Vector2f());
        g.method_51448().pushMatrix();
        g.method_51448().mul((Matrix3x2fc)m);
        float a = Math.min(1.0f, t / 0.15f) * ck;
        Gfx.glow(g, 0, 0, this.field_22789, this.field_22790, 22, 3528347, 0.55f * a, 18);
        Gfx.aaRound(g, 0.0f, 0.0f, this.field_22789, this.field_22790, 22.0f, 662036, 0.86f * a);
        Gfx.aaRound(g, 1.0f, 1.0f, this.field_22789 - 2, (float)this.field_22790 / 2.0f, 21.0f, 6085030, 0.025f * a);
        this.renderBody(g, Math.round(mp.x), Math.round(mp.y), delta);
        g.method_51448().popMatrix();
    }

    private class_11909 fm(class_11909 e) {
        if (!this.floating) {
            return e;
        }
        Vector2f p = this.floatInv.transformPosition((float)e.comp_4798(), (float)e.comp_4799(), new Vector2f());
        return new class_11909((double)p.x, (double)p.y, e.comp_4800());
    }

    public void method_25432() {
        super.method_25432();
        if (this.floating) {
            PhoneMenu.menuClosed();
        }
    }

    private void renderBody(class_332 g, int mouseX, int mouseY, float delta) {
        if (!(this.closeTime < 0L || this.anim() && System.currentTimeMillis() - this.closeTime <= 170L)) {
            super.method_25419();
            return;
        }
        long ns = System.nanoTime();
        float dt = Math.min(0.1f, (float)(ns - this.lastNs) / 1.0E9f);
        this.lastNs = ns;
        this.frameDt = dt;
        NowPlaying np = SpotifyClient.now();
        this.stageK = this.pool.to("stage", np.hasTrack() ? 1.0 : 0.0);
        if (!this.anim()) {
            this.stageK = np.hasTrack() ? 1.0f : 0.0f;
        }
        this.layout(this.stageK);
        this.mx = Math.round((float)mouseX / this.S);
        this.my = Math.round((float)mouseY / this.S);
        this.gfx = g;
        Gfx.revealOn = false;
        if (this.activeSlider != null) {
            if (!this.field_22787.field_1729.method_1608()) {
                this.activeSlider = null;
                NeonLyrics.saveConfig();
            } else {
                this.activeSlider.set().accept(Float.valueOf(Anim.clamp((float)(this.mx - this.activeSlider.x()) / (float)this.activeSlider.w())));
            }
        }
        this.hits.clear();
        this.sliders.clear();
        this.idWanted = false;
        float t = this.since();
        float ck = this.closeK();
        g.method_51448().pushMatrix();
        g.method_51448().scale(this.S, this.S);
        Gfx.alpha = ck;
        if (this.stageK > 0.01f) {
            this.stage(g, np, t, dt);
        }
        float co = this.anim() ? LyricsScreen.ob((t - 0.3f) / 0.5f) : 1.0f;
        float slide = (1.0f - co) * (float)(this.W - this.colX + 20) + (1.0f - ck) * 30.0f;
        g.method_51448().pushMatrix();
        g.method_51448().translate(slide, 0.0f);
        if (co < 0.999f || ck < 0.999f) {
            float sc = 0.92f + 0.08f * Math.min(co, 1.02f) - 0.04f * (1.0f - ck);
            float ccx = (float)this.colX + (float)this.colW / 2.0f;
            float ccy = (float)this.colY + (float)this.colH / 2.0f;
            g.method_51448().translate(ccx, ccy);
            g.method_51448().rotate((1.0f - Anim.clamp(co)) * 0.06f);
            g.method_51448().scale(sc, sc);
            g.method_51448().translate(-ccx, -ccy);
        }
        this.column(g, dt);
        g.method_51448().popMatrix();
        g.method_51448().popMatrix();
        Gfx.alpha = 1.0f;
        float pa = this.anim() ? Anim.outCubic(Anim.since(this.pageTime, 260L)) : 1.0f;
        boolean idVis = this.idWanted && this.closeTime < 0L && co > 0.98f && pa > 0.95f && this.idY >= this.vy && this.idY + 12 <= this.vy + this.vh;
        LyricsScreen.show(this.idBox, idVis);
        if (idVis) {
            this.idBox.method_46421(Math.round((float)this.idX * this.S));
            this.idBox.method_46419(Math.round((float)this.idY * this.S));
            this.idBox.method_25358(Math.max(20, Math.round((float)this.idW * this.S)));
            LyricsScreen.c().spotifyClientId = this.idBox.method_1882().trim();
        }
        super.method_25394(g, mouseX, mouseY, delta);
    }

    private void stage(class_332 g, NowPlaying np, float t, float dt) {
        Config c = LyricsScreen.c();
        long now = System.currentTimeMillis();
        String track = String.valueOf(np.trackId);
        if (!track.equals(this.lastTrack)) {
            this.lastTrack = track;
            this.trackAt = now;
        }
        float base = Gfx.alpha;
        float sk = this.stageK;
        int shift = Math.round((1.0f - sk) * -40.0f);
        int x0 = 40 + shift;
        int x1 = this.colX - 34;
        int areaW = Math.max(120, x1 - x0);
        int cs = Math.round(Math.min(150.0f, Math.min((float)this.H * 0.38f, (float)areaW * 0.46f)));
        int cy = Math.round((float)this.H * 0.12f);
        if (np.playing) {
            this.spin += dt * 70.0f;
        }
        float vin = this.anim() ? LyricsScreen.oc((t - 0.4f) / 0.5f) : 1.0f;
        float vr = (float)cs * 0.47f;
        float vcx = (float)x0 + (float)cs / 2.0f + vin * (float)cs * 0.6f;
        float vcy = (float)cy + (float)cs / 2.0f;
        if (vin > 0.01f) {
            Gfx.alpha = base * sk * Anim.clamp((t - 0.4f) / 0.1f);
            Gfx.aaCircle(g, vcx, vcy, vr, 528910, 1.0f);
            for (int i = 1; i <= 7; ++i) {
                Gfx.aaCircle(g, vcx, vcy, vr * (1.0f - 0.075f * (float)i), (i & 1) == 1 ? 1056794 : 528910, 1.0f);
            }
            float ls = vr * 0.62f;
            g.method_51448().pushMatrix();
            g.method_51448().translate(vcx, vcy);
            g.method_51448().rotate((float)Math.toRadians(this.spin));
            Gfx.aaRound(g, -vr * 0.92f, -0.75f, vr * 1.84f, 1.5f, 0.75f, 6085030, 0.05f);
            Gfx.aaRound(g, -0.75f, -vr * 0.92f, 1.5f, vr * 1.84f, 0.75f, 6085030, 0.03f);
            if (!Cover.draw(g, Math.round(-ls / 2.0f), Math.round(-ls / 2.0f), Math.round(ls), 1.0f)) {
                Gfx.aaCircle(g, 0.0f, 0.0f, ls / 2.0f, 1980974, 1.0f);
                Gfx.aaCircle(g, ls * 0.18f, -ls * 0.1f, 2.0f, 6085030, 0.6f);
            }
            Gfx.aaCircle(g, 0.0f, 0.0f, 2.2f, 528910, 1.0f);
            g.method_51448().popMatrix();
        }
        float sl = this.anim() ? LyricsScreen.ob((t - 0.05f) / 0.45f) : 1.0f;
        float trackIn = this.anim() ? LyricsScreen.oc((float)(now - this.trackAt) / 450.0f) : 1.0f;
        Gfx.alpha = base * sk * Anim.clamp((t - 0.05f) / 0.2f);
        g.method_51448().pushMatrix();
        g.method_51448().translate((float)x0 + (float)cs / 2.0f, (float)cy + (float)cs / 2.0f + (1.0f - sl) * 60.0f);
        g.method_51448().rotate((float)Math.toRadians((1.0f - sl) * -14.0f));
        float pop = (0.94f + 0.06f * trackIn) * (0.8f + 0.2f * sl);
        g.method_51448().scale(pop, pop);
        Gfx.aaRound(g, (float)(-cs) / 2.0f - 2.0f, (float)(-cs) / 2.0f + 4.0f, cs + 4, cs + 4, 10.0f, 0, 0.35f);
        if (!Cover.drawSquare(g, -cs / 2, -cs / 2, cs, 1.0f)) {
            Gfx.aaRound(g, (float)(-cs) / 2.0f, (float)(-cs) / 2.0f, cs, cs, 8.0f, 1518371, 1.0f);
            Icons.draw(g, Icons.Icon.LYRICS, 0.0f, 0.0f, (float)cs * 0.26f, 4089174, 1.0f);
        }
        g.method_51448().popMatrix();
        int ty = cy + cs + 20;
        float meta = this.anim() ? LyricsScreen.oc((t - 0.55f) / 0.4f) : 1.0f;
        Gfx.alpha = base * sk * meta * trackIn;
        int lift = Math.round((1.0f - meta * trackIn) * 14.0f);
        float twipe = this.anim() ? Anim.outCubic(Anim.clamp((t - 0.55f) / 0.5f)) * trackIn : 1.0f;
        String ttl = this.ell(np.title, areaW, T.XL);
        if (twipe < 0.999f) {
            g.method_44379(x0 - 2, ty - 4, x0 + Math.round((float)(this.tw(ttl, T.XL) + 6) * twipe), ty + 22);
        }
        this.txt(ttl, x0, ty + lift, 14939884, 1.0f, T.XL);
        if (twipe < 0.999f) {
            g.method_44380();
        }
        this.txt(this.ell(np.artist, areaW, T.M), x0, ty + 24 + lift, 8826011, 1.0f, T.M);
        float lyA = this.anim() ? LyricsScreen.oc((t - 0.7f) / 0.4f) : 1.0f;
        Gfx.alpha = base * sk * lyA;
        int lyY = ty + 46 + Math.round((1.0f - lyA) * 14.0f);
        if (np.lyricsState == NowPlaying.LyricsState.SYNCED) {
            long prog = np.progressNow(c.lyricsOffsetMs);
            int li = np.lineIndex(prog);
            if (li >= 0 && li < np.lines.size() && !np.lines.get(li).text().isBlank()) {
                NowPlaying.Line ln = np.lines.get(li);
                long end = li + 1 < np.lines.size() ? np.lines.get(li + 1).timeMs() : ln.timeMs() + 4000L;
                float frac = Anim.clamp((float)(prog - ln.timeMs()) / (float)Math.max(400L, Math.min(end - ln.timeMs(), 6000L) - 200L));
                String[] words = ln.text().split(" ");
                int sung = (int)Math.ceil(frac * (float)words.length);
                if (ln.wordTimes() != null && ln.wordTimes().length == words.length) {
                    sung = 0;
                    for (long wt : ln.wordTimes()) {
                        if (wt > prog) continue;
                        ++sung;
                    }
                }
                float la = Anim.clamp(this.pool.toFrom("ln" + li + track, 0.0, 1.0));
                int yy = lyY + Math.round((1.0f - la) * 8.0f);
                float xx = x0;
                int lines = 0;
                int space = this.tw(" ", T.TITLE);
                for (int k = 0; k < words.length && lines < 2; ++k) {
                    int ww = this.tw(words[k], T.TITLE);
                    if (xx + (float)ww > (float)(x0 + areaW) && xx > (float)x0) {
                        xx = x0;
                        yy += 15;
                        if (++lines >= 2) break;
                    }
                    this.txt(words[k], xx, yy, k < sung ? 14939884 : 5732714, la, T.TITLE);
                    xx += (float)(ww + space);
                }
                if (li + 1 < np.lines.size()) {
                    this.txt(this.ell(np.lines.get(li + 1).text(), areaW, T.M), x0, yy + 19, 5732714, la, T.M);
                }
            }
        } else {
            String st = switch (np.lyricsState) {
                case NowPlaying.LyricsState.LOADING -> LyricsScreen.t("spotify.ly.loading", new Object[0]);
                case NowPlaying.LyricsState.NOT_FOUND -> LyricsScreen.t("spotify.ly.none", new Object[0]);
                case NowPlaying.LyricsState.ERROR -> LyricsScreen.t("spotify.ly.error", new Object[0]);
                default -> "";
            };
            this.txt(this.ell(st, areaW, T.M), x0, lyY, 8826011, 1.0f, T.M);
        }
        int bars = 28;
        int bw = 3;
        int gap = 3;
        int bh = 34;
        int by = this.H - 26;
        long prog = np.progressNow(c.lyricsOffsetMs);
        float beat = np.playing ? np.beatPulse(prog) : 0.0f;
        this.vizLevel += ((np.playing ? 1.0f : 0.12f) - this.vizLevel) * (1.0f - (float)Math.exp(-6.0f * dt));
        double tt = (double)now / 1000.0;
        for (int i = 0; i < bars; ++i) {
            float k = this.anim() ? LyricsScreen.oc((t - 0.5f - (float)i * 0.012f) / 0.3f) : 1.0f;
            double wave = Math.abs(Math.sin((double)i * 0.7 + tt * 6.0) * Math.sin((double)i * 0.23 + tt * 3.1));
            float h = 3.0f + (float)(bh - 3) * (float)wave * this.vizLevel * (0.55f + 0.6f * beat) * k;
            Gfx.alpha = base * sk * (0.3f + 0.6f * k);
            Gfx.aaRound(g, x0 + i * (bw + gap), (float)by - h, bw, h, 1.5f, 6085030, 0.85f);
        }
        Gfx.alpha = base * sk * (this.anim() ? LyricsScreen.oc((t - 0.75f) / 0.3f) : 1.0f);
        int cx = x0 + bars * (bw + gap) + 24;
        int ccy = by - 14;
        this.iconBtn(g, "prev", cx, ccy - 10, 20, Icons.Icon.PREV, 8.0f, () -> SpotifyClient.control(SpotifyClient.Cmd.PREVIOUS));
        float hv = this.hover(cx + 28, ccy - 14, 28, 28, "play");
        float press = 1.0f - Anim.outCubic(Anim.since(this.pressed.getOrDefault("play", 0L), 220L));
        Gfx.aaCircle(g, cx + 42, ccy, 14.0f * (1.0f + 0.05f * hv) * (1.0f - 0.08f * press), 6085030, 1.0f);
        Icons.draw(g, np.playing ? Icons.Icon.PAUSE : Icons.Icon.PLAY, (float)(cx + 42) + (np.playing ? 0.0f : 0.8f), ccy, 10.0f, 726803, 1.0f);
        this.hit(cx + 28, ccy - 14, 28, 28, () -> {
            this.pressed.put("play", System.currentTimeMillis());
            SpotifyClient.control(SpotifyClient.Cmd.PLAYPAUSE);
        });
        this.iconBtn(g, "next", cx + 64, ccy - 10, 20, Icons.Icon.NEXT, 8.0f, () -> SpotifyClient.control(SpotifyClient.Cmd.NEXT));
        this.iconBtn(g, "fav", cx + 92, ccy - 10, 20, Icons.Icon.FAV, 8.0f, Favorites::saveCurrent);
        Gfx.alpha = base;
    }

    private Icons.Icon icon(Page p) {
        return switch (p.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> Icons.Icon.SKY;
            case 1 -> Icons.Icon.TEXT;
            case 2 -> Icons.Icon.HAT;
            case 3 -> Icons.Icon.SPOTIFY;
            case 4 -> Icons.Icon.DISPLAY;
            case 5 -> Icons.Icon.SETTINGS;
            case 6 -> Icons.Icon.PERF;
            case 7 -> Icons.Icon.FAV;
        };
    }

    private String title(Page p) {
        return LyricsScreen.t("ui.page." + p.name().toLowerCase(Locale.ROOT), new Object[0]);
    }

    private String subtitle(Page p) {
        if (p == Page.SKY) {
            return LyricsScreen.t("ui.page.sky.sub", SKY_ORDER.length);
        }
        return LyricsScreen.t("ui.page." + p.name().toLowerCase(Locale.ROOT) + ".sub", new Object[0]);
    }

    private void column(class_332 g, float dt) {
        Gfx.aaRound(g, this.colX - 1, this.colY - 1, this.colW + 2, this.colH + 2, 15.0f, 6085030, 0.1f);
        Gfx.aaRound(g, this.colX, this.colY, this.colW, this.colH, 14.0f, 726803, 0.94f);
        Gfx.vGradient(g, this.colX + 10, this.colY + 1, this.colW - 20, 40, 6085030, 6085030, 0.05f, 0.0f);
        Gfx.rect(g, this.colX + 56, this.colY + 12, 1, this.colH - 24, 2376247, 1.0f);
        int ry = this.colY + 10;
        int rh = Math.min(44, (this.colH - 20) / Page.values().length - 2);
        float sel = this.spring("railsel", this.page.ordinal());
        float vel = this.springs.get("railsel")[1];
        float stretch = Math.min(10.0f, Math.abs(vel) * 0.9f);
        Gfx.aaRound(g, this.colX + 6, (float)ry + sel * (float)(rh + 2) - stretch / 2.0f, 44.0f, (float)rh + stretch, 10.0f, 6085030, 1.0f);
        for (Page p : Page.values()) {
            int y = ry + p.ordinal() * (rh + 2);
            float on = Anim.clamp(1.0f - Math.abs(sel - (float)p.ordinal()));
            float hv = this.hover(this.colX + 6, y, 44, rh, "rail" + p.name());
            if (p != this.page && hv > 0.01f) {
                Gfx.aaRound(g, this.colX + 6, y, 44.0f, rh, 10.0f, 6085030, 0.05f * hv);
            }
            int fg = Gfx.lerpColor(Gfx.lerpColor(8826011, 11983047, hv), 726803, on);
            float intro = this.anim() ? Anim.outBack(Anim.clamp((this.since() - 0.55f - (float)p.ordinal() * 0.06f) / 0.35f)) : 1.0f;
            float bounce = (1.0f + 0.18f * (1.0f - Anim.outCubic(Anim.since(this.pressed.getOrDefault("rail" + p.name(), 0L), 380L))) + 0.08f * hv) * Math.max(0.01f, intro);
            Icons.draw(g, this.icon(p), (float)this.colX + 28.0f, (float)y + (float)rh / 2.0f - 5.0f - hv, 10.0f * bounce, fg, 1.0f);
            this.txtC(this.ell(this.title(p), 42, T.S), (float)this.colX + 28.0f, (float)y + (float)rh / 2.0f + 5.0f, fg, 1.0f, T.S);
            Page target = p;
            this.hit(this.colX + 6, y, 44, rh, () -> {
                this.pressed.put("rail" + target.name(), System.currentTimeMillis());
                this.go(target);
            });
        }
        int xx = this.colX + this.colW - 28;
        int xy = this.colY + 12;
        float hvx = this.hover(xx, xy, 18, 18, "close");
        Gfx.aaCircle(g, xx + 9, xy + 9, 9.0f, 6085030, 0.05f + 0.07f * hvx);
        g.method_51448().pushMatrix();
        g.method_51448().translate((float)(xx + 9), (float)(xy + 9));
        g.method_51448().rotate((float)((double)hvx * Math.PI / 2.0));
        Icons.draw(g, Icons.Icon.CLOSE, 0.0f, 0.0f, 6.5f, Gfx.lerpColor(8826011, 14939884, hvx), 1.0f);
        g.method_51448().popMatrix();
        this.hit(xx, xy, 18, 18, this::method_25419);
        float pa = this.anim() ? Anim.outCubic(Anim.since(this.pageTime, 260L)) : 1.0f;
        int hx = this.vx + Math.round((1.0f - pa) * 8.0f * (float)this.dir);
        float wipe = this.anim() ? Anim.outCubic(Anim.since(this.pageTime, 420L)) : 1.0f;
        int tw0 = this.tw(this.title(this.page), T.TITLE);
        if (wipe < 0.999f) {
            g.method_44379(hx - 2, this.colY + 8, hx + Math.round((float)(tw0 + 6) * wipe), this.colY + 30);
        }
        this.txt(this.title(this.page), hx, this.colY + 15, 14939884, pa, T.TITLE);
        if (wipe < 0.999f) {
            g.method_44380();
            Gfx.aaRound(g, (float)hx + (float)(tw0 + 4) * wipe, this.colY + 13, 1.5f, 12.0f, 0.75f, 6085030, 0.8f * (1.0f - wipe));
        }
        float sa = this.anim() ? Anim.outCubic(Anim.since(this.pageTime + 120L, 300L)) : 1.0f;
        this.txt(this.ell(this.subtitle(this.page), this.vw - 30, T.S), hx, this.colY + 31 + Math.round((1.0f - sa) * 5.0f), 8826011, sa, T.S);
        float maxScroll = Math.max(0, this.contentH - this.vh + 6);
        float over = 36.0f;
        this.scrollTarget = Math.max(-over, Math.min(maxScroll + over, this.scrollTarget));
        if (this.scrollTarget < 0.0f) {
            this.scrollTarget += (0.0f - this.scrollTarget) * (1.0f - (float)Math.exp(-9.0f * dt));
        }
        if (this.scrollTarget > maxScroll) {
            this.scrollTarget += (maxScroll - this.scrollTarget) * (1.0f - (float)Math.exp(-9.0f * dt));
        }
        this.scroll += (this.scrollTarget - this.scroll) * (1.0f - (float)Math.exp(-16.0f * dt));
        g.method_44379(this.vx - 4, this.vy, this.vx + this.vw + 4, this.vy + this.vh);
        this.ly = 0;
        this.rowIdx = 0;
        float baseA = Gfx.alpha;
        Gfx.alpha = baseA * Math.max(0.0f, pa);
        this.inContent = true;
        switch (this.page.ordinal()) {
            case 0: {
                this.sky();
                break;
            }
            case 1: {
                this.lyrics();
                break;
            }
            case 2: {
                this.style();
                break;
            }
            case 3: {
                this.spotify();
                break;
            }
            case 4: {
                this.display();
                break;
            }
            case 5: {
                this.menuPage();
                break;
            }
            case 6: {
                this.perf();
                break;
            }
            case 7: {
                this.fav();
            }
        }
        this.inContent = false;
        this.contentH = this.ly + 4;
        Gfx.alpha = baseA;
        g.method_44380();
        if (this.scroll > 1.0f) {
            Gfx.vGradient(g, this.vx - 4, this.vy, this.vw + 8, 10, 726803, 726803, 0.94f * baseA, 0.0f);
        }
        if (this.scroll < maxScroll - 1.0f) {
            Gfx.vGradient(g, this.vx - 4, this.vy + this.vh - 10, this.vw + 8, 10, 726803, 726803, 0.0f, 0.94f * baseA);
        }
        if (maxScroll > 0.0f) {
            int bh = Math.max(16, Math.round((float)this.vh * ((float)this.vh / (float)this.contentH)));
            int by = this.vy + Math.round((float)(this.vh - bh) * (this.scroll / maxScroll));
            float act = this.pool.to("scrollact", Math.abs(this.scrollTarget - this.scroll) > 0.5f ? 1.0 : 0.0);
            Gfx.aaRound(g, (float)(this.colX + this.colW - 7) - act, by, 2.0f + act * 1.5f, bh, 1.0f + act, 6085030, 0.12f + 0.25f * act);
        }
    }

    private void setSky(SkyPreset p) {
        LyricsScreen.c().sky = p.ordinal();
        LyricsScreen.c().lastSky = p.ordinal();
    }

    private void sky() {
        Config c = LyricsScreen.c();
        this.gStart(LyricsScreen.t("ui.group.general", new Object[0]), null);
        this.toggleRow("skybox", LyricsScreen.t("ui.sky.custom.enabled", new Object[0]), LyricsScreen.t("ui.sky.custom.enabled.sub", new Object[0]), c.customSkyboxEnabled, v -> {
            c.customSkyboxEnabled = v;
        });
        this.toggleRow("skyon", LyricsScreen.t("ui.sky.enabled", new Object[0]), LyricsScreen.t("ui.sky.enabled.sub", new Object[0]), c.sky >= 0, v -> {
            c.sky = v != false ? Math.max(0, c.lastSky) : -1;
        });
        this.toggleRow("shuf", LyricsScreen.t("ui.sky.shuffle", new Object[0]), LyricsScreen.t("ui.sky.shuffle.sub", new Object[0]), c.skyShuffle, v -> {
            c.skyShuffle = v;
        });
        this.linkRow(LyricsScreen.t("ui.sky.random", new Object[0]), null, SkyPreset::shuffle);
        this.gEnd();
        this.label(LyricsScreen.t("ui.sky.style", new Object[0]), LyricsScreen.t("ui.sky.count", SKY_ORDER.length));
        this.skyGrid();
        SkyPreset cur = SkyPreset.get(c.sky >= 0 ? c.sky : c.lastSky);
        if (cur != null) {
            this.note(LyricsScreen.t("skydesc." + cur.name().toLowerCase(Locale.ROOT), new Object[0]));
        }
        if (cur == SkyPreset.CUSTOM && c.sky >= 0) {
            this.gStart(LyricsScreen.t("skyed.title", new Object[0]), null);
            this.swatchRow("ctop", LyricsScreen.t("skyed.top", new Object[0]), DARK_COLS, c.customTop, false, v -> {
                c.customTop = v;
                SkyPreset.applyCustom();
            });
            this.swatchRow("chor", LyricsScreen.t("skyed.horizon", new Object[0]), DARK_COLS, c.customHorizon, false, v -> {
                c.customHorizon = v;
                SkyPreset.applyCustom();
            });
            this.swatchRow("ca1", LyricsScreen.t("skyed.aurora1", new Object[0]), BRIGHT, c.customAurora1, false, v -> {
                c.customAurora1 = v;
                SkyPreset.applyCustom();
            });
            this.swatchRow("ca2", LyricsScreen.t("skyed.aurora2", new Object[0]), BRIGHT, c.customAurora2, false, v -> {
                c.customAurora2 = v;
                SkyPreset.applyCustom();
            });
            this.toggleRow("cau", LyricsScreen.t("feat.aurora", new Object[0]), null, c.customAurora, v -> {
                c.customAurora = v;
                SkyPreset.applyCustom();
            });
            this.toggleRow("cst", LyricsScreen.t("feat.stars", new Object[0]), null, c.customStars, v -> {
                c.customStars = v;
                SkyPreset.applyCustom();
            });
            this.toggleRow("cmo", LyricsScreen.t("feat.moon", new Object[0]), null, c.customMoon, v -> {
                c.customMoon = v;
                SkyPreset.applyCustom();
            });
            this.toggleRow("csh", LyricsScreen.t("feat.shooting", new Object[0]), null, c.customShooting, v -> {
                c.customShooting = v;
                SkyPreset.applyCustom();
            });
            SkyPreset.Weather[] ws = SkyPreset.Weather.values();
            SkyPreset.Weather w = ws[Math.floorMod(c.customWeather, ws.length)];
            this.pickerRow("cwe", LyricsScreen.t("skyed.weather", new Object[0]), null, w == SkyPreset.Weather.NONE ? LyricsScreen.t("skyed.noweather", new Object[0]) : LyricsScreen.t("feat." + w.name().toLowerCase(Locale.ROOT), new Object[0]), -1, () -> {
                c.customWeather = Math.floorMod(c.customWeather - 1, ws.length);
                SkyPreset.applyCustom();
            }, () -> {
                c.customWeather = Math.floorMod(c.customWeather + 1, ws.length);
                SkyPreset.applyCustom();
            });
            this.gEnd();
        }
        this.gStart(LyricsScreen.t("ui.sky.tune", new Object[0]), null);
        String pal = c.skyPalette < 0 ? LyricsScreen.t("ui.palette.original", new Object[0]) : LyricsScreen.t("ui.palette." + WorldSky.PALETTE_KEYS[c.skyPalette], new Object[0]);
        int np = WorldSky.PALETTES.length + 1;
        this.pickerRow("pal", LyricsScreen.t("ui.sky.palette", new Object[0]), LyricsScreen.t("ui.sky.palette.sub", new Object[0]), pal, -1, () -> {
            c.skyPalette = Math.floorMod(c.skyPalette, np) - 1;
        }, () -> {
            c.skyPalette = Math.floorMod(c.skyPalette + 2, np) - 1;
        });
        this.sliderRow("spd", LyricsScreen.t("ui.sky.speed", new Object[0]), (c.skySpeed - 0.25f) / 1.75f, String.format(Locale.ROOT, "%.1f\u00d7", Float.valueOf(c.skySpeed)), v -> {
            c.skySpeed = 0.25f + v.floatValue() * 1.75f;
        });
        this.sliderRow("liv", LyricsScreen.t("ui.sky.lively", new Object[0]), c.skyLively, Math.round(c.skyLively * 100.0f) + " %", v -> {
            c.skyLively = v.floatValue();
        });
        this.sliderRow("brt", LyricsScreen.t("ui.sky.breath", new Object[0]), c.skyBreath / 2.0f, c.skyBreath < 0.02f ? LyricsScreen.t("ui.off", new Object[0]) : String.format(Locale.ROOT, "%.1f\u00d7", Float.valueOf(c.skyBreath)), v -> {
            c.skyBreath = v.floatValue() < 0.03f ? 0.0f : v.floatValue() * 2.0f;
        });
        this.sliderRow("pat", LyricsScreen.t("ui.sky.pattern", new Object[0]), (c.skyPattern - 0.2f) / 1.3f, Math.round(c.skyPattern * 100.0f) + " %", v -> {
            c.skyPattern = 0.2f + v.floatValue() * 1.3f;
        });
        this.sliderRow("str", LyricsScreen.t("opt.skystrength", new Object[0]), (c.skyStrength - 0.1f) / 0.9f, Math.round(c.skyStrength * 100.0f) + " %", v -> {
            c.skyStrength = 0.1f + v.floatValue() * 0.9f;
        });
        this.sliderRow("wea", LyricsScreen.t("opt.weatheramount", new Object[0]), c.weatherAmount / 2.0f, Math.round(c.weatherAmount * 100.0f) + " %", v -> {
            c.weatherAmount = v.floatValue() * 2.0f;
        });
        this.gEnd();
        this.gStart(LyricsScreen.t("ui.sky.behaviour", new Object[0]), null);
        this.toggleRow("rw", LyricsScreen.t("opt.realweather", new Object[0]), LyricsScreen.t("ui.sky.realweather.sub", new Object[0]), c.realWeather, v -> {
            c.realWeather = v;
        });
        this.toggleRow("hc", LyricsScreen.t("opt.hideclouds", new Object[0]), LyricsScreen.t("ui.sky.hideclouds.sub", new Object[0]), c.hideClouds, v -> {
            c.hideClouds = v;
        });
        this.toggleRow("sd", LyricsScreen.t("extras.skyday", new Object[0]), LyricsScreen.t("ui.sky.skyday.sub", new Object[0]), c.skyDay, v -> {
            c.skyDay = v;
            if (v.booleanValue()) {
                c.fxDayNight = false;
            }
        });
        this.toggleRow("dn", LyricsScreen.t("extras.daynight", new Object[0]), LyricsScreen.t("ui.sky.daynight.sub", new Object[0]), c.fxDayNight, v -> {
            c.fxDayNight = v;
            if (v.booleanValue()) {
                c.skyDay = false;
            }
        });
        this.toggleRow("con", LyricsScreen.t("extras.const", new Object[0]), LyricsScreen.t("ui.sky.const.sub", new Object[0]), c.fxConstellations, v -> {
            c.fxConstellations = v;
        });
        this.toggleRow("skm", LyricsScreen.t("extras.skymusic", new Object[0]), LyricsScreen.t("ui.sky.music.sub", new Object[0]), c.fxSkyMusic, v -> {
            c.fxSkyMusic = v;
        });
        this.gEnd();
    }

    private void lyrics() {
        Config c = LyricsScreen.c();
        this.gStart(LyricsScreen.t("ui.group.general", new Object[0]), null);
        this.toggleRow("lyron", LyricsScreen.t("ui.lyrics.show", new Object[0]), LyricsScreen.t("ui.lyrics.show.sub", new Object[0]), c.lyricsSky, v -> {
            c.lyricsSky = v;
        });
        this.segRow("kind", LyricsScreen.t("ui.lyrics.kind", new Object[0]), null, new String[]{LyricsScreen.t("ui.style.sky", new Object[0]), LyricsScreen.t("ui.style.float", new Object[0])}, c.lyricsStyle, i -> {
            c.lyricsStyle = i;
        });
        this.toggleRow("fol", LyricsScreen.t("opt.lyricsfollow", new Object[0]), LyricsScreen.t("ui.lyrics.follow.sub", new Object[0]), c.lyricsFollow, v -> {
            c.lyricsFollow = v;
        });
        this.toggleRow("lock", LyricsScreen.t("ui.lyrics.lock", new Object[0]), LyricsScreen.t("ui.lyrics.lock.sub", new Object[0]), c.lyricsViewLock, v -> {
            c.lyricsViewLock = v;
        });
        this.toggleRow("und", LyricsScreen.t("style.underground", new Object[0]), LyricsScreen.t("ui.lyrics.under.sub", new Object[0]), c.lyricsUnderground, v -> {
            c.lyricsUnderground = v;
        });
        this.gEnd();
        this.gStart(LyricsScreen.t("ui.lyrics.look", new Object[0]), null);
        this.swatchRow("lcol", LyricsScreen.t("ui.lyrics.color", new Object[0]), WorldSky.LYRIC_COLORS, c.lyricsColor < 0 ? -1 : WorldSky.LYRIC_COLORS[Math.floorMod(c.lyricsColor, WorldSky.LYRIC_COLORS.length)], true, v -> {
            c.lyricsColor = LyricsScreen.indexOf(WorldSky.LYRIC_COLORS, v);
        });
        this.glowRow();
        this.pairRow("look1", 34, this.sliderCell("size", LyricsScreen.t("opt.lyricssize", new Object[0]), (c.lyricsSize - 0.6f) / 3.4f, String.format(Locale.ROOT, "%.1f\u00d7", Float.valueOf(c.lyricsSize)), v -> {
            c.lyricsSize = 0.6f + v.floatValue() * 3.4f;
        }), this.sliderCell("opa", LyricsScreen.t("opt.lyricsopacity", new Object[0]), (c.lyricsOpacity - 0.2f) / 0.8f, Math.round(c.lyricsOpacity * 100.0f) + " %", v -> {
            c.lyricsOpacity = 0.2f + v.floatValue() * 0.8f;
        }));
        String[] anims = new String[]{LyricsScreen.t("style.anim0", new Object[0]), LyricsScreen.t("style.anim1", new Object[0]), LyricsScreen.t("style.anim2", new Object[0])};
        this.pairRow("look2", 34, c.lyricsGlow ? this.sliderCell("gstr", LyricsScreen.t("style.glowstrength", new Object[0]), (c.lyricsGlowStrength - 0.2f) / 2.3f, Math.round(c.lyricsGlowStrength * 100.0f) + " %", v -> {
            c.lyricsGlowStrength = 0.2f + v.floatValue() * 2.3f;
        }) : null, this.pickerCell("anim", LyricsScreen.t("style.anim", new Object[0]), anims[Math.floorMod(c.lyricsAnim, 3)], -1, () -> {
            c.lyricsAnim = Math.floorMod(c.lyricsAnim - 1, 3);
        }, () -> {
            c.lyricsAnim = Math.floorMod(c.lyricsAnim + 1, 3);
        }));
        int nf = Gfx.FONT_NAMES.length;
        this.pickerRow("font", LyricsScreen.t("opt.lyricsfont", new Object[0]), null, Gfx.FONT_NAMES[c.lyricsFont], c.lyricsFont, () -> {
            c.lyricsFont = Math.floorMod(c.lyricsFont - 1, nf);
            Gfx.clearWidths();
        }, () -> {
            c.lyricsFont = Math.floorMod(c.lyricsFont + 1, nf);
            Gfx.clearWidths();
        });
        this.gEnd();
        this.gStart(LyricsScreen.t("ui.lyrics.timing", new Object[0]), null);
        this.pickerRow("off", LyricsScreen.t("opt.lyricsoffset", new Object[0]), LyricsScreen.t("ui.lyrics.offset.sub", new Object[0]), (c.lyricsOffsetMs > 0 ? "+" : "") + c.lyricsOffsetMs + " ms", -1, () -> {
            c.lyricsOffsetMs = Math.max(-5000, c.lyricsOffsetMs - 100);
        }, () -> {
            c.lyricsOffsetMs = Math.min(5000, c.lyricsOffsetMs + 100);
        });
        this.toggleRow("beat", LyricsScreen.t("extras.beat", new Object[0]), LyricsScreen.t("ui.lyrics.beat.sub", new Object[0]), c.fxBeatPulse, v -> {
            c.fxBeatPulse = v;
        });
        this.toggleRow("intro", LyricsScreen.t("extras.intro", new Object[0]), LyricsScreen.t("ui.lyrics.intro.sub", new Object[0]), c.fxSongIntro, v -> {
            c.fxSongIntro = v;
        });
        this.gEnd();
        this.gStart(LyricsScreen.t("ui.lyrics.translate", new Object[0]), null);
        this.toggleRow("tr", LyricsScreen.t("extras.translate", new Object[0]), LyricsScreen.t("ui.lyrics.translate.sub", new Object[0]), c.translate, v -> {
            c.translate = v;
        });
        int li = Math.max(0, Arrays.asList(LANGS).indexOf(c.translateTo));
        this.pickerRow("lang", LyricsScreen.t("ui.lyrics.lang", new Object[0]), null, LANGS[li].toUpperCase(Locale.ROOT), -1, () -> {
            c.translateTo = LANGS[Math.floorMod(li - 1, LANGS.length)];
        }, () -> {
            c.translateTo = LANGS[Math.floorMod(li + 1, LANGS.length)];
        });
        this.gEnd();
    }

    private void style() {
        Config c = LyricsScreen.c();
        this.label(LyricsScreen.t("ui.style.hat", new Object[0]), null);
        this.hatGrid();
        this.note(LyricsScreen.t("ui.style.hat.note", new Object[0]));
        this.gStart(LyricsScreen.t("ui.style.hatopts", new Object[0]), null);
        this.toggleRow("hglow", LyricsScreen.t("ui.style.hatglow", new Object[0]), LyricsScreen.t("ui.style.hatglow.sub", new Object[0]), c.hatGlow, v -> {
            c.hatGlow = v;
        });
        this.gEnd();
        this.gStart(LyricsScreen.t("ui.style.crosshair", new Object[0]), null);
        String[] cr = new String[]{LyricsScreen.t("ui.cross.0", new Object[0]), LyricsScreen.t("ui.cross.1", new Object[0]), LyricsScreen.t("ui.cross.2", new Object[0]), LyricsScreen.t("ui.cross.3", new Object[0]), LyricsScreen.t("ui.cross.4", new Object[0])};
        this.pickerRow("cross", LyricsScreen.t("ui.style.crosshair", new Object[0]), LyricsScreen.t("ui.style.crosshair.sub", new Object[0]), cr[Math.floorMod(c.crosshair, cr.length)], -1, () -> {
            c.crosshair = Math.floorMod(c.crosshair - 1, cr.length);
        }, () -> {
            c.crosshair = Math.floorMod(c.crosshair + 1, cr.length);
        });
        this.gEnd();
        this.gStart(LyricsScreen.t("ui.style.effects", new Object[0]), null);
        String[] tr = new String[]{LyricsScreen.t("ui.trail.0", new Object[0]), LyricsScreen.t("ui.trail.1", new Object[0]), LyricsScreen.t("ui.trail.2", new Object[0]), LyricsScreen.t("ui.trail.3", new Object[0])};
        this.pickerRow("trail", LyricsScreen.t("ui.style.trail", new Object[0]), LyricsScreen.t("ui.style.trail.sub", new Object[0]), tr[Math.floorMod(c.trail, tr.length)], -1, () -> {
            c.trail = Math.floorMod(c.trail - 1, tr.length);
        }, () -> {
            c.trail = Math.floorMod(c.trail + 1, tr.length);
        });
        this.toggleRow("aura", LyricsScreen.t("ui.style.aura", new Object[0]), LyricsScreen.t("ui.style.aura.sub", new Object[0]), c.aura, v -> {
            c.aura = v;
        });
        this.toggleRow("bglow", LyricsScreen.t("ui.style.beatglow", new Object[0]), LyricsScreen.t("ui.style.beatglow.sub", new Object[0]), c.beatGlow, v -> {
            c.beatGlow = v;
        });
        this.gEnd();
        this.gStart(LyricsScreen.t("ui.style.death", new Object[0]), null);
        String[] de = new String[4];
        for (int i = 0; i < de.length; ++i) {
            de[i] = LyricsScreen.t("ui.death." + i, new Object[0]);
        }
        this.pickerRow("death", LyricsScreen.t("ui.style.death", new Object[0]), LyricsScreen.t("ui.style.death.sub", new Object[0]), de[Math.floorMod(c.deathFx, de.length)], -1, () -> {
            c.deathFx = Math.floorMod(c.deathFx - 1, de.length);
        }, () -> {
            c.deathFx = Math.floorMod(c.deathFx + 1, de.length);
        });
        if (c.deathFx > 0) {
            this.toggleRow("dothers", LyricsScreen.t("ui.style.deathothers", new Object[0]), LyricsScreen.t("ui.style.deathothers.sub", new Object[0]), c.deathOthers, v -> {
                c.deathOthers = v;
            });
            this.linkRow(LyricsScreen.t("ui.style.deathtest", new Object[0]), LyricsScreen.t("ui.style.deathtest.sub", new Object[0]), () -> {
                this.method_25419();
                DeathFx.test();
            });
        }
        this.gEnd();
    }

    private void hatGrid() {
        Config c = LyricsScreen.c();
        String[] hats = Accessories.HATS;
        int n = 3;
        int gap = 8;
        int cw = (this.vw - gap * (n - 1)) / n;
        int ch = Math.round((float)cw * 0.625f);
        int rows = (hats.length + n - 1) / n;
        for (int r = 0; r < rows; ++r) {
            int y0 = this.rowStart(ch + 24);
            if (y0 != Integer.MIN_VALUE) {
                int i;
                for (int k = 0; k < n && (i = r * n + k) < hats.length; ++k) {
                    int x = this.vx + k * (cw + gap);
                    int y = y0 + 2;
                    boolean on = c.hat == i;
                    float hv = this.hover(x, y, cw, ch + 16, "hat" + i);
                    float sv = this.pool.to("hatsel" + i, on ? 1.0 : 0.0);
                    float clickPop = 1.0f - Anim.outCubic(Anim.since(this.pressed.getOrDefault("hat" + i, 0L), 300L));
                    float sc = this.spring("hatsc" + i, 1.0f + 0.05f * hv) - 0.05f * clickPop;
                    this.gfx.method_51448().pushMatrix();
                    this.gfx.method_51448().translate((float)x + (float)cw / 2.0f, (float)y + (float)ch / 2.0f);
                    this.gfx.method_51448().scale(sc, sc);
                    this.gfx.method_51448().translate(-((float)x + (float)cw / 2.0f), -((float)y + (float)ch / 2.0f));
                    if (sv > 0.01f) {
                        float ring = 2.0f + 2.0f * (1.0f - Anim.outBack(sv));
                        Gfx.aaRound(this.gfx, (float)x - ring, (float)y - ring, (float)cw + ring * 2.0f, (float)ch + ring * 2.0f, 9.0f + ring, 6085030, sv);
                    }
                    Gfx.aaRound(this.gfx, x, y, cw, ch, 9.0f, Gfx.lerpColor(0x12211B, 1716263, hv), 1.0f);
                    if (i == 0) {
                        Icons.draw(this.gfx, Icons.Icon.CLOSE, (float)x + (float)cw / 2.0f, (float)y + (float)ch / 2.0f, 10.0f, Gfx.lerpColor(4877919, 14939884, Math.max(hv, sv)), 1.0f);
                    } else {
                        float bob = (float)Math.sin((double)System.currentTimeMillis() / 400.0 + (double)i) * 1.5f * Math.max(hv, sv);
                        class_2960 tex = class_2960.method_60655((String)"neonlyrics", (String)("textures/gui/hats/" + hats[i] + ".png"));
                        try {
                            this.gfx.method_25291(class_10799.field_56883, tex, x + 2, Math.round((float)(y + 2) + bob), 0.0f, 0.0f, cw - 4, ch - 4, cw - 4, ch - 4, Gfx.argb(-1118479, 1.0f));
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                    }
                    this.gfx.method_51448().popMatrix();
                    this.txt(this.ell(LyricsScreen.t("ui.hat." + hats[i], new Object[0]), cw, T.S), x, y + ch + 6, Gfx.lerpColor(Gfx.lerpColor(8826011, 11983047, hv), 14939884, sv), 1.0f, on ? T.B : T.S);
                    int idx = i;
                    this.hit(x, y, cw, ch + 16, () -> {
                        this.pressed.put("hat" + idx, System.currentTimeMillis());
                        c.hat = idx;
                        Fx.sound(Fx.Sfx.CLICK);
                        NeonLyrics.saveConfig();
                    });
                }
            }
            this.rowEnd();
        }
    }

    private void spotify() {
        Config c = LyricsScreen.c();
        this.gStart(LyricsScreen.t("ui.group.status", new Object[0]), null);
        this.statusRow();
        this.gEnd();
        this.gStart(LyricsScreen.t("ui.spotify.connection", new Object[0]), null);
        boolean auto = SpotifyClient.autoMode();
        this.segRow("mode", LyricsScreen.t("ui.spotify.mode", new Object[0]), null, new String[]{LyricsScreen.t("spotify.mode.auto", new Object[0]), LyricsScreen.t("spotify.mode.api", new Object[0])}, auto ? 0 : 1, i -> {
            SpotifyClient.setMode(i);
            this.idBox.method_1852(LyricsScreen.c().spotifyClientId);
        });
        if (auto) {
            this.linkRow(LyricsScreen.t("spotify.auto.retry", new Object[0]), null, () -> {
                LocalMedia.restart();
                Island.notify(LyricsScreen.t("spotify.auto.retrying", new Object[0]), true);
            });
        } else {
            this.fieldRow(LyricsScreen.t("spotify.clientid", new Object[0]), LyricsScreen.t("ui.spotify.clientid.sub", new Object[0]));
            boolean connected = SpotifyClient.status() == SpotifyClient.Status.CONNECTED;
            this.linkRow(connected ? LyricsScreen.t("spotify.disconnect", new Object[0]) : LyricsScreen.t("spotify.connect", new Object[0]), null, () -> {
                c.spotifyClientId = this.idBox.method_1882().trim();
                NeonLyrics.saveConfig();
                if (SpotifyClient.status() == SpotifyClient.Status.CONNECTED) {
                    SpotifyClient.logout();
                } else {
                    SpotifyClient.login();
                }
            });
            this.linkRow(LyricsScreen.t("tut.open", new Object[0]), null, this::openTutorial);
            this.copyRow(LyricsScreen.t("ui.spotify.redirect", new Object[0]), "http://127.0.0.1:8789/callback");
            this.linkRow(LyricsScreen.t("spotify.dashboard", new Object[0]), null, () -> class_156.method_668().method_670("https://developer.spotify.com/dashboard"));
        }
        this.gEnd();
        if (auto) {
            this.note(LyricsScreen.t("spotify.auto.p1", new Object[0]));
            this.note(LyricsScreen.t("spotify.auto.p2", new Object[0]));
            this.note(LyricsScreen.t("spotify.auto.p4", new Object[0]));
        }
        this.gStart(LyricsScreen.t("ui.spotify.playback", new Object[0]), null);
        this.toggleRow("demo", LyricsScreen.t("ui.spotify.demo", new Object[0]), LyricsScreen.t("ui.spotify.demo.sub", new Object[0]), c.demoLyrics, v -> {
            c.demoLyrics = v;
            SpotifyClient.restartDemo();
        });
        this.linkRow(LyricsScreen.t("ui.spotify.sync", new Object[0]), null, SpotifyClient::syncHere);
        this.gEnd();
    }

    private void display() {
        Config c = LyricsScreen.c();
        this.gStart(LyricsScreen.t("ui.display.bar", new Object[0]), null);
        this.toggleRow("isl", LyricsScreen.t("ui.display.island", new Object[0]), LyricsScreen.t("ui.display.island.sub", new Object[0]), c.spotifyHudEnabled, v -> {
            c.spotifyHudEnabled = v;
        });
        this.toggleRow("lisl", LyricsScreen.t("opt.lyricsisland", new Object[0]), LyricsScreen.t("ui.display.lyricsisland.sub", new Object[0]), c.lyricsIsland, v -> {
            c.lyricsIsland = v;
        });
        this.toggleRow("cov", LyricsScreen.t("extras.cover", new Object[0]), LyricsScreen.t("ui.display.cover.sub", new Object[0]), c.albumCover, v -> {
            c.albumCover = v;
        });
        this.toggleRow("clk", LyricsScreen.t("opt.clock", new Object[0]), null, c.showClock, v -> {
            c.showClock = v;
        });
        this.toggleRow("png", LyricsScreen.t("opt.ping", new Object[0]), null, c.showPing, v -> {
            c.showPing = v;
        });
        this.toggleRow("fps", LyricsScreen.t("opt.fps", new Object[0]), null, c.showFps, v -> {
            c.showFps = v;
        });
        int nf = Gfx.FONT_NAMES.length;
        this.pickerRow("ufont", LyricsScreen.t("ui.display.barfont", new Object[0]), LyricsScreen.t("ui.display.barfont.sub", new Object[0]), Gfx.FONT_NAMES[c.uiFont], c.uiFont < Gfx.FONT_IDS.length ? c.uiFont : -1, () -> {
            c.uiFont = Math.floorMod(c.uiFont - 1, nf);
            Gfx.clearWidths();
        }, () -> {
            c.uiFont = Math.floorMod(c.uiFont + 1, nf);
            Gfx.clearWidths();
        });
        this.gEnd();
        this.gStart(LyricsScreen.t("ui.display.hud", new Object[0]), null);
        this.toggleRow("hot", LyricsScreen.t("opt.glasshotbar", new Object[0]), LyricsScreen.t("ui.display.hotbar.sub", new Object[0]), c.glassHotbar, v -> {
            c.glassHotbar = v;
        });
        this.toggleRow("intro", LyricsScreen.t("extras.startup", new Object[0]), null, c.fxStartup, v -> {
            c.fxStartup = v;
        });
        this.gEnd();
    }

    private void menuPage() {
        Config c = LyricsScreen.c();
        this.gStart(LyricsScreen.t("ui.menu.mode", new Object[0]), null);
        this.segRow("mmode", LyricsScreen.t("ui.menu.open", new Object[0]), LyricsScreen.t(c.menuMode == 1 ? "ui.menu.world.sub" : "ui.menu.normal.sub", new Object[0]), new String[]{LyricsScreen.t("ui.menu.normal", new Object[0]), LyricsScreen.t("ui.menu.world", new Object[0])}, c.menuMode, i -> {
            c.menuMode = i;
        });
        this.linkRow(LyricsScreen.t("ui.menu.reset", new Object[0]), LyricsScreen.t("ui.menu.reset.sub", new Object[0]), () -> {
            c.menuMode = 0;
            c.accent = 0;
            c.blur = true;
            c.fxMenuAnim = true;
            c.sounds = true;
            PhoneMenu.closeNow();
            NeonLyrics.saveConfig();
        });
        this.gEnd();
        this.gStart(LyricsScreen.t("ui.display.menu", new Object[0]), null);
        this.accentRow();
        this.toggleRow("blur", LyricsScreen.t("opt.blur", new Object[0]), null, c.blur, v -> {
            c.blur = v;
        });
        this.toggleRow("anim", LyricsScreen.t("perf.menuanim", new Object[0]), null, c.fxMenuAnim, v -> {
            c.fxMenuAnim = v;
        });
        this.toggleRow("snd", LyricsScreen.t("opt.sounds", new Object[0]), null, c.sounds, v -> {
            c.sounds = v;
        });
        this.gEnd();
    }

    private void perf() {
        Config c = LyricsScreen.c();
        this.gStart(LyricsScreen.t("ui.group.now", new Object[0]), null);
        this.fpsRow();
        this.gEnd();
        this.gStart(LyricsScreen.t("ui.group.auto", new Object[0]), null);
        this.toggleRow("auto", LyricsScreen.t("perf.auto", new Object[0]), LyricsScreen.t("ui.perf.auto.sub", new Object[0]), c.autoPerf, v -> {
            c.autoPerf = v;
            AutoPerf.reset();
        });
        int ti = 0;
        for (int i2 = 0; i2 < TARGETS.length; ++i2) {
            if (TARGETS[i2] != c.targetFps) continue;
            ti = i2;
        }
        int tIdx = ti;
        this.pickerRow("target", LyricsScreen.t("perf.target", new Object[0]), LyricsScreen.t("ui.perf.target.sub", new Object[0]), (String)(c.targetFps <= 0 ? LyricsScreen.t("ui.perf.target.auto", AutoPerf.target()) : c.targetFps + " FPS"), -1, () -> {
            c.targetFps = TARGETS[Math.floorMod(tIdx - 1, TARGETS.length)];
            AutoPerf.reset();
        }, () -> {
            c.targetFps = TARGETS[Math.floorMod(tIdx + 1, TARGETS.length)];
            AutoPerf.reset();
        });
        this.segRow("qual", LyricsScreen.t("quality", new Object[0]), null, new String[]{LyricsScreen.t("quality.0", new Object[0]), LyricsScreen.t("quality.1", new Object[0]), LyricsScreen.t("quality.2", new Object[0])}, c.quality, i -> {
            c.quality = i;
        });
        this.segRow("prof", LyricsScreen.t("ui.perf.profile", new Object[0]), null, new String[]{"Chill", "PvP"}, c.profile, Profiles::set);
        this.toggleRow("boost", LyricsScreen.t("opt.fpsboost", new Object[0]), LyricsScreen.t("ui.perf.boost.sub", new Object[0]), c.fpsBoost, FpsBoost::apply);
        this.gEnd();
        this.gStart(LyricsScreen.t("ui.perf.effects", new Object[0]), null);
        this.toggleRow("e_col", LyricsScreen.t("perf.skycolor", new Object[0]), null, c.fxSkyColor, v -> {
            c.fxSkyColor = v;
        });
        this.toggleRow("e_wea", LyricsScreen.t("perf.weather", new Object[0]), null, c.fxWeather, v -> {
            c.fxWeather = v;
        });
        this.toggleRow("e_aur", LyricsScreen.t("perf.aurora", new Object[0]), null, c.fxAurora, v -> {
            c.fxAurora = v;
        });
        this.toggleRow("e_sta", LyricsScreen.t("perf.stars", new Object[0]), null, c.fxStars, v -> {
            c.fxStars = v;
        });
        this.toggleRow("e_glo", LyricsScreen.t("style.glow", new Object[0]), LyricsScreen.t("ui.perf.glow.sub", new Object[0]), c.lyricsGlow, v -> {
            c.lyricsGlow = v;
        });
        this.linkRow(LyricsScreen.t("perf.alloff", new Object[0]), null, () -> this.setAllEffects(false));
        this.linkRow(LyricsScreen.t("perf.allon", new Object[0]), null, () -> this.setAllEffects(true));
        this.gEnd();
    }

    private void fav() {
        List<Favorites.Entry> list = Favorites.all();
        if (list.isEmpty()) {
            this.label(LyricsScreen.t("ui.fav.lines", new Object[0]), null);
            this.note(LyricsScreen.t("fav.empty", new Object[0]));
        } else {
            this.gStart(LyricsScreen.t("ui.fav.lines", new Object[0]), Integer.toString(list.size()));
            for (int i = 0; i < list.size(); ++i) {
                Favorites.Entry e = list.get(i);
                int y = this.rowStart(38);
                if (y != Integer.MIN_VALUE) {
                    int x = this.vx + 12;
                    int w = this.vw - 24;
                    this.rowBg(y, 38, "fav" + i);
                    this.txt(this.ell("\u201e" + e.line + "\u201c", w - 26, T.B), x, y + 9, 14939884, 1.0f, T.B);
                    this.txt(this.ell(e.title + (String)(e.artist.isBlank() ? "" : " \u00b7 " + e.artist), w - 26, T.S), x, y + 21, 8826011, 1.0f, T.S);
                    this.iconBtn(this.gfx, "favdel" + i, x + w - 16, y + 10, 18, Icons.Icon.CLOSE, 6.0f, () -> Favorites.remove(e));
                }
                this.rowEnd();
            }
            this.gEnd();
        }
        List<SpotifyClient.Played> hist = SpotifyClient.history();
        if (hist.isEmpty()) {
            this.label(LyricsScreen.t("ui.fav.history", new Object[0]), null);
            this.note(LyricsScreen.t("ui.fav.history.empty", new Object[0]));
            return;
        }
        this.gStart(LyricsScreen.t("ui.fav.history", new Object[0]), Integer.toString(hist.size()));
        long now = System.currentTimeMillis();
        for (int i = 0; i < hist.size(); ++i) {
            SpotifyClient.Played e = hist.get(i);
            int y = this.rowStart(38);
            if (y != Integer.MIN_VALUE) {
                int x = this.vx + 12;
                int w = this.vw - 24;
                this.rowBg(y, 38, "his" + i);
                String ago = LyricsScreen.ago(now - e.at());
                this.txt(this.ell(e.title(), w - 14 - this.tw(ago, T.S), T.B), x, y + 9, 14939884, 1.0f, T.B);
                this.txt(this.ell(e.artist(), w, T.S), x, y + 21, 8826011, 1.0f, T.S);
                this.txtR(ago, x + w, y + 10, 8826011, 1.0f, T.S);
            }
            this.rowEnd();
        }
        this.gEnd();
    }

    private int rowStart(int h) {
        int sy = this.vy + this.ly - Math.round(this.scroll);
        int idx = this.rowIdx++;
        this.ly += h;
        this.rowA = Gfx.alpha;
        if (sy + h < this.vy - 2 || sy > this.vy + this.vh + 2) {
            return Integer.MIN_VALUE;
        }
        if (!this.anim()) {
            return sy;
        }
        long now = System.currentTimeMillis();
        long first = this.seen.computeIfAbsent(idx, k -> Math.max(now, this.pageTime + (long)Math.min(idx, 14) * 35L));
        float in = Anim.outCubic(Anim.since(first, 340L));
        float k2 = Anim.outBack(Anim.since(first, 420L));
        Gfx.alpha = this.rowA * in;
        return sy + Math.round((1.0f - k2) * 14.0f);
    }

    private void rowEnd() {
        Gfx.alpha = this.rowA;
    }

    private void gStart(String title, String meta) {
        this.label(title, meta);
        int y = this.rowStart(5);
        if (y != Integer.MIN_VALUE) {
            Gfx.aaRound(this.gfx, this.vx, y, this.vw, 16.0f, 9.0f, 0x12211B, 1.0f);
        }
        this.rowEnd();
        this.firstInGroup = true;
    }

    private void gEnd() {
        int y = this.rowStart(13);
        if (y != Integer.MIN_VALUE) {
            Gfx.aaRound(this.gfx, this.vx, y - 11, this.vw, 16.0f, 9.0f, 0x12211B, 1.0f);
        }
        this.rowEnd();
        this.firstInGroup = false;
    }

    private void label(String title, String meta) {
        int y = this.rowStart(20);
        if (y != Integer.MIN_VALUE) {
            this.txt(title.toUpperCase(Locale.ROOT), this.vx + 3, y + 8, 8826011, 1.0f, T.S);
            if (meta != null) {
                this.txtR(meta, this.vx + this.vw - 3, y + 8, 8826011, 1.0f, T.S);
            }
        }
        this.rowEnd();
    }

    private void rowBg(int y, int h, String id) {
        float hv = this.hover(this.vx, y, this.vw, h, id);
        Gfx.rect(this.gfx, this.vx, y, this.vw, h, Gfx.lerpColor(0x12211B, 1716263, hv), 1.0f);
        if (hv > 0.01f) {
            Gfx.aaRound(this.gfx, this.vx + 3, (float)y + (float)h / 2.0f - (float)(h - 14) * hv / 2.0f, 2.0f, (float)(h - 14) * hv, 1.0f, 6085030, 0.5f * hv);
        }
        if (!this.firstInGroup) {
            Gfx.rect(this.gfx, this.vx + 12, y, this.vw - 24, 1, 2376247, 1.0f);
        }
        this.firstInGroup = false;
        this.ripple(id, this.vx, y, this.vw, h);
        this.curRid = id;
    }

    private void labels(int y, int h, String title, String sub, int maxW) {
        int x = this.vx + 12;
        if (sub == null) {
            this.txt(this.ell(title, maxW, T.B), x, y + h / 2 - 4, 14939884, 1.0f, T.B);
        } else {
            this.txt(this.ell(title, maxW, T.B), x, y + h / 2 - 9, 14939884, 1.0f, T.B);
            this.txt(this.ell(sub, maxW, T.S), x, y + h / 2 + 3, 8826011, 1.0f, T.S);
        }
    }

    private int rowH(String sub) {
        return sub == null ? 30 : 38;
    }

    private void note(String text) {
        List<String> lines = this.wrap(text, this.vw - 6, T.S);
        int y = this.rowStart(6 + lines.size() * 9);
        if (y != Integer.MIN_VALUE) {
            for (int i = 0; i < lines.size(); ++i) {
                this.txt(lines.get(i), this.vx + 3, y + 3 + i * 9, 8826011, 1.0f, T.S);
            }
        }
        this.rowEnd();
    }

    private void toggleRow(String id, String title, String sub, boolean value, Consumer<Boolean> set) {
        int h = this.rowH(sub);
        int y = this.rowStart(h);
        if (y != Integer.MIN_VALUE) {
            this.rowBg(y, h, "tr" + id);
            this.labels(y, h, title, sub, this.vw - 70);
            int tw0 = 24;
            int th = 14;
            int tx = this.vx + this.vw - 12 - tw0;
            int ty = y + h / 2 - th / 2;
            float ks = this.spring("tg" + id, value ? 1.0f : 0.0f);
            float k = Anim.clamp(ks);
            float flash = 1.0f - Anim.outCubic(Anim.since(this.pressed.getOrDefault("tg" + id, 0L), 450L));
            if (flash > 0.01f) {
                Gfx.aaRound(this.gfx, (float)tx - 4.0f * (1.0f - flash) - 1.0f, (float)ty - 4.0f * (1.0f - flash) - 1.0f, (float)tw0 + 8.0f * (1.0f - flash) + 2.0f, (float)th + 8.0f * (1.0f - flash) + 2.0f, (float)th / 2.0f + 4.0f, 6085030, 0.25f * flash);
            }
            Gfx.aaRound(this.gfx, tx, ty, tw0, th, (float)th / 2.0f, Gfx.lerpColor(2771517, 6085030, k), 1.0f);
            float stretch = Math.min(5.0f, Math.abs(this.springs.get("tg" + id)[1]) * 0.35f);
            Gfx.aaRound(this.gfx, (float)(tx + 2) + ks * 10.0f - (value ? stretch : 0.0f), ty + 2, 10.0f + stretch, 10.0f, 5.0f, Gfx.lerpColor(7905424, 726803, k), 1.0f);
            this.hit(this.vx, y, this.vw, h, () -> {
                this.pressed.put("tg" + id, System.currentTimeMillis());
                set.accept(!value);
                Fx.sound(Fx.Sfx.CLICK);
                NeonLyrics.saveConfig();
            }, "tr" + id);
        }
        this.rowEnd();
    }

    private void sliderRow(String id, String title, float value, String valueText, Consumer<Float> set) {
        int h = 42;
        int y = this.rowStart(h);
        if (y != Integer.MIN_VALUE) {
            this.rowBg(y, h, "sr" + id);
            int x = this.vx + 12;
            int w = this.vw - 24;
            this.txt(this.ell(title, w - 50, T.B), x, y + 9, 14939884, 1.0f, T.B);
            value = Anim.clamp(value);
            boolean drag = this.activeSlider != null && this.activeSlider.id().equals(id);
            float dk = this.pool.to("sd" + id, drag ? 1.0 : 0.0);
            this.txtR(valueText, x + w, y + 9 - Math.round(dk * 1.5f), Gfx.lerpColor(11062715, 6085030, dk), 1.0f, drag ? T.B : T.S);
            float v = drag ? value : this.spring("sv" + id, value);
            int cy = y + 29;
            float hv = this.pool.to("sh" + id, drag || this.in(x - 6, cy - 8, w + 12, 16) ? 1.0 : 0.0);
            Gfx.aaRound(this.gfx, x, (float)cy - 1.5f, w, 3.0f, 1.5f, 2771517, 1.0f);
            Gfx.aaRound(this.gfx, x, (float)cy - 1.5f, Math.max(3.0f, (float)w * Anim.clamp(v)), 3.0f, 1.5f, 6085030, 1.0f);
            if (hv > 0.01f) {
                Gfx.aaCircle(this.gfx, (float)x + (float)w * Anim.clamp(v), cy, 8.0f + 3.0f * dk, 6085030, 0.12f * hv);
            }
            Gfx.aaCircle(this.gfx, (float)x + (float)w * Anim.clamp(v), cy, 4.5f + 0.8f * hv + 1.5f * dk, 6085030, 1.0f);
            if (this.closeTime < 0L) {
                int sy0 = Math.max(cy - 9, this.vy);
                int sy1 = Math.min(cy + 9, this.vy + this.vh);
                this.sliders.add(new Slider(id, x, sy0, w, sy1 - sy0, set));
            }
        }
        this.rowEnd();
    }

    private void pickerRow(String id, String title, String sub, String value, int fontIdx, Runnable prev, Runnable next) {
        int h = this.rowH(sub);
        int y = this.rowStart(h);
        if (y != Integer.MIN_VALUE) {
            int vw0;
            this.rowBg(y, h, "pr" + id);
            int area = Math.min(150, this.vw / 2);
            int ax = this.vx + this.vw - 12 - area;
            this.labels(y, h, title, sub, ax - this.vx - 20);
            int cy = y + h / 2;
            boolean hl = this.in(ax, y, area / 2, h);
            boolean hr = this.in(ax + area / 2, y, area - area / 2, h);
            float pulse = 1.0f - Anim.outCubic(Anim.since(this.pressed.getOrDefault("pk" + id, 0L), 240L));
            int right = this.vx + this.vw - 24;
            if (fontIdx >= 0) {
                vw0 = Math.min(area - 30, Gfx.wIn(value, fontIdx, false));
                Gfx.textIn(this.gfx, value, right - vw0, cy - 4 - Math.round(pulse * 1.5f), 13035221, 1.0f, fontIdx, false);
            } else {
                String vv = this.ell(value, area - 30, T.M);
                vw0 = this.tw(vv, T.M);
                this.txtR(vv, right, (float)(cy - 4) - pulse * 1.5f, 13035221, 1.0f, T.M);
            }
            float hlk = this.pool.to("hl" + id, hl ? 1.0 : 0.0);
            float hrk = this.pool.to("hr" + id, hr ? 1.0 : 0.0);
            Icons.draw(this.gfx, Icons.Icon.BACK, (float)(right - vw0 - 10) - hlk * 2.0f, cy, 6.0f + hlk, Gfx.lerpColor(4877919, 14939884, hlk), 1.0f);
            Icons.draw(this.gfx, Icons.Icon.CHEVRON, (float)(this.vx + this.vw - 15) + hrk * 2.0f, cy, 6.0f + hrk, Gfx.lerpColor(4877919, 14939884, hrk), 1.0f);
            this.hit(ax, y, area / 2, h, () -> {
                this.pressed.put("pk" + id, System.currentTimeMillis());
                prev.run();
                Fx.sound(Fx.Sfx.CLICK);
                NeonLyrics.saveConfig();
            }, "pr" + id);
            this.hit(ax + area / 2, y, area - area / 2, h, () -> {
                this.pressed.put("pk" + id, System.currentTimeMillis());
                next.run();
                Fx.sound(Fx.Sfx.CLICK);
                NeonLyrics.saveConfig();
            }, "pr" + id);
        }
        this.rowEnd();
    }

    private void segRow(String id, String title, String sub, String[] opts, int sel, IntConsumer set) {
        int h = this.rowH(sub);
        int y = this.rowStart(h);
        if (y != Integer.MIN_VALUE) {
            this.rowBg(y, h, "sg" + id);
            int n = opts.length;
            int ow = 0;
            for (String o : opts) {
                ow = Math.max(ow, Math.max(this.tw(o, T.S), this.tw(o, T.B)) + 18);
            }
            int sw0 = ow * n + 4;
            int sh = 18;
            int sx = this.vx + this.vw - 12 - sw0;
            int sy = y + h / 2 - sh / 2;
            this.labels(y, h, title, sub, sx - this.vx - 20);
            Gfx.aaRound(this.gfx, sx, sy, sw0, sh, 9.0f, 595215, 1.0f);
            float pos = this.spring("seg" + id, sel);
            Gfx.aaRound(this.gfx, (float)(sx + 2) + pos * (float)ow, sy + 2, ow, sh - 4, 7.0f, 6085030, 1.0f);
            for (int i = 0; i < n; ++i) {
                float on = Anim.clamp(1.0f - Math.abs(pos - (float)i));
                this.txtC(opts[i], (float)(sx + 2 + i * ow) + (float)ow / 2.0f, sy + 5, Gfx.lerpColor(8826011, 726803, on), 1.0f, on > 0.5f ? T.B : T.S);
                int idx = i;
                this.hit(sx + 2 + i * ow, sy, ow, sh, () -> {
                    if (idx == sel) {
                        return;
                    }
                    set.accept(idx);
                    Fx.sound(Fx.Sfx.CLICK);
                    NeonLyrics.saveConfig();
                });
            }
        }
        this.rowEnd();
    }

    private void swatchRow(String id, String title, int[] cols, int value, boolean auto, IntConsumer set) {
        int h = 30;
        int y = this.rowStart(h);
        if (y != Integer.MIN_VALUE) {
            this.rowBg(y, h, "sw" + id);
            int cy = y + h / 2;
            int n = cols.length + (auto ? 1 : 0);
            int step = 14;
            int startX = this.vx + this.vw - 17 - (n - 1) * step;
            this.labels(y, h, title, null, startX - this.vx - 24);
            for (int i = 0; i < n; ++i) {
                boolean isAuto = auto && i == 0;
                int col = isAuto ? 0 : cols[i - (auto ? 1 : 0)];
                boolean sel = isAuto ? value < 0 : value == col;
                float cx = startX + i * step;
                float sv = this.pool.to(id + "s" + i, sel ? 1.0 : 0.0);
                if (sv > 0.01f) {
                    Gfx.aaCircle(this.gfx, cx, cy, 6.5f, 6085030, sv);
                    Gfx.aaCircle(this.gfx, cx, cy, 5.2f, 0x12211B, 1.0f);
                }
                if (isAuto) {
                    Gfx.aaCircle(this.gfx, cx, cy, 4.2f, 2771517, 1.0f);
                    this.txtC("A", cx, cy - 3, 8826011, 1.0f, T.S);
                } else {
                    Gfx.aaCircle(this.gfx, cx, cy, 4.2f, col, 1.0f);
                }
                int v = isAuto ? -1 : col;
                this.hit(Math.round(cx - 7.0f), y, 14, h, () -> {
                    set.accept(v);
                    Fx.sound(Fx.Sfx.CLICK);
                    NeonLyrics.saveConfig();
                });
            }
        }
        this.rowEnd();
    }

    private void glowRow() {
        Config c = LyricsScreen.c();
        int h = 30;
        int y = this.rowStart(h);
        if (y != Integer.MIN_VALUE) {
            this.rowBg(y, h, "glowrow");
            int cy = y + h / 2;
            int[] cols = WorldSky.LYRIC_COLORS;
            int value = c.lyricsGlowColor < 0 ? -1 : cols[Math.floorMod(c.lyricsGlowColor, cols.length)];
            int tw0 = 24;
            int th = 14;
            int tx = this.vx + this.vw - 12 - tw0;
            int ty = cy - th / 2;
            int n = cols.length + 1;
            int step = 14;
            int startX = tx - 14 - (n - 1) * step;
            this.labels(y, h, LyricsScreen.t("style.glow", new Object[0]), null, startX - this.vx - 24);
            float on = this.pool.to("glowon", c.lyricsGlow ? 1.0 : 0.0);
            float old = Gfx.alpha;
            Gfx.alpha = old * (0.35f + 0.65f * on);
            for (int i = 0; i < n; ++i) {
                int col;
                boolean isAuto = i == 0;
                int n2 = col = isAuto ? 0 : cols[i - 1];
                boolean sel = isAuto ? value < 0 : value == col;
                float cx = startX + i * step;
                float sv = this.pool.to("gcs" + i, sel ? 1.0 : 0.0);
                if (sv > 0.01f) {
                    Gfx.aaCircle(this.gfx, cx, cy, 6.5f, 6085030, sv);
                    Gfx.aaCircle(this.gfx, cx, cy, 5.2f, 0x12211B, 1.0f);
                }
                if (isAuto) {
                    Gfx.aaCircle(this.gfx, cx, cy, 4.2f, 2771517, 1.0f);
                    this.txtC("A", cx, cy - 3, 8826011, 1.0f, T.S);
                } else {
                    Gfx.aaCircle(this.gfx, cx, cy, 4.2f, col, 1.0f);
                }
                int v = isAuto ? -1 : col;
                this.hit(Math.round(cx - 7.0f), y, 14, h, () -> {
                    c.lyricsGlowColor = LyricsScreen.indexOf(cols, v);
                    c.lyricsGlow = true;
                    Fx.sound(Fx.Sfx.CLICK);
                    NeonLyrics.saveConfig();
                });
            }
            Gfx.alpha = old;
            float ks = this.spring("tgglow", c.lyricsGlow ? 1.0f : 0.0f);
            float k = Anim.clamp(ks);
            Gfx.aaRound(this.gfx, tx, ty, tw0, th, (float)th / 2.0f, Gfx.lerpColor(2771517, 6085030, k), 1.0f);
            Gfx.aaRound(this.gfx, (float)(tx + 2) + ks * 10.0f, ty + 2, 10.0f, 10.0f, 5.0f, Gfx.lerpColor(7905424, 726803, k), 1.0f);
            this.hit(tx - 6, y, tw0 + 18, h, () -> {
                c.lyricsGlow = !c.lyricsGlow;
                Fx.sound(Fx.Sfx.CLICK);
                NeonLyrics.saveConfig();
            });
        }
        this.rowEnd();
    }

    private void pairRow(String id, int h, Cell left, Cell right) {
        int y = this.rowStart(h);
        if (y != Integer.MIN_VALUE) {
            this.rowBg(y, h, "pair" + id);
            int half = this.vw / 2;
            Gfx.rect(this.gfx, this.vx + half, y + 7, 1, h - 14, 2376247, 1.0f);
            if (left != null) {
                left.draw(this.vx, y, half, h);
            }
            if (right != null) {
                right.draw(this.vx + half, y, this.vw - half, h);
            }
        }
        this.rowEnd();
    }

    private Cell sliderCell(String id, String title, float value0, String valueText, Consumer<Float> set) {
        return (x0, y, w0, h) -> {
            int x = x0 + 12;
            int w = w0 - 24;
            float value = Anim.clamp(value0);
            boolean drag = this.activeSlider != null && this.activeSlider.id().equals(id);
            float dk = this.pool.to("sd" + id, drag ? 1.0 : 0.0);
            this.txt(this.ell(title, w - this.tw(valueText, T.S) - 6, T.S), x, y + 7, 14939884, 1.0f, T.S);
            this.txtR(valueText, x + w, y + 7, Gfx.lerpColor(11062715, 6085030, dk), 1.0f, drag ? T.B : T.S);
            float v = drag ? value : this.spring("sv" + id, value);
            int cy = y + h - 10;
            float hv = this.pool.to("sh" + id, drag || this.in(x - 6, cy - 8, w + 12, 16) ? 1.0 : 0.0);
            Gfx.aaRound(this.gfx, x, (float)cy - 1.5f, w, 3.0f, 1.5f, 2771517, 1.0f);
            Gfx.aaRound(this.gfx, x, (float)cy - 1.5f, Math.max(3.0f, (float)w * Anim.clamp(v)), 3.0f, 1.5f, 6085030, 1.0f);
            if (hv > 0.01f) {
                Gfx.aaCircle(this.gfx, (float)x + (float)w * Anim.clamp(v), cy, 7.0f + 2.0f * dk, 6085030, 0.12f * hv);
            }
            Gfx.aaCircle(this.gfx, (float)x + (float)w * Anim.clamp(v), cy, 4.0f + 0.7f * hv + 1.2f * dk, 6085030, 1.0f);
            if (this.closeTime < 0L) {
                int sy0 = Math.max(cy - 9, this.vy);
                int sy1 = Math.min(cy + 9, this.vy + this.vh);
                this.sliders.add(new Slider(id, x, sy0, w, sy1 - sy0, set));
            }
        };
    }

    private Cell pickerCell(String id, String title, String value, int fontIdx, Runnable prev, Runnable next) {
        return (x0, y, w0, h) -> {
            this.txt(this.ell(title, w0 - 24, T.S), x0 + 12, y + 7, 14939884, 1.0f, T.S);
            int cy = y + h - 10;
            float pulse = 1.0f - Anim.outCubic(Anim.since(this.pressed.getOrDefault("pk" + id, 0L), 240L));
            boolean hl = this.in(x0, y, w0 / 2, h);
            boolean hr = this.in(x0 + w0 / 2, y, w0 - w0 / 2, h);
            float hlk = this.pool.to("hl" + id, hl ? 1.0 : 0.0);
            float hrk = this.pool.to("hr" + id, hr ? 1.0 : 0.0);
            String vv = this.ell(value, w0 - 44, T.S);
            this.txtC(vv, (float)x0 + (float)w0 / 2.0f, (float)(cy - 4) - pulse * 1.5f, 13035221, 1.0f, T.S);
            Icons.draw(this.gfx, Icons.Icon.BACK, (float)(x0 + 16) - hlk * 2.0f, cy, 5.0f + hlk, Gfx.lerpColor(4877919, 14939884, hlk), 1.0f);
            Icons.draw(this.gfx, Icons.Icon.CHEVRON, (float)(x0 + w0 - 16) + hrk * 2.0f, cy, 5.0f + hrk, Gfx.lerpColor(4877919, 14939884, hrk), 1.0f);
            this.hit(x0, y, w0 / 2, h, () -> {
                this.pressed.put("pk" + id, System.currentTimeMillis());
                prev.run();
                Fx.sound(Fx.Sfx.CLICK);
                NeonLyrics.saveConfig();
            }, "pair" + id);
            this.hit(x0 + w0 / 2, y, w0 - w0 / 2, h, () -> {
                this.pressed.put("pk" + id, System.currentTimeMillis());
                next.run();
                Fx.sound(Fx.Sfx.CLICK);
                NeonLyrics.saveConfig();
            }, "pair" + id);
        };
    }

    private void accentRow() {
        Config c = LyricsScreen.c();
        int h = 30;
        int y = this.rowStart(h);
        if (y != Integer.MIN_VALUE) {
            this.rowBg(y, h, "accent");
            int cy = y + h / 2;
            int n = Theme.ACCENTS.length;
            int step = 14;
            int startX = this.vx + this.vw - 17 - (n - 1) * step;
            this.labels(y, h, LyricsScreen.t("ui.display.accent", new Object[0]), null, startX - this.vx - 24);
            int i = 0;
            while (i < n) {
                float cx = startX + i * step;
                float sv = this.pool.to("accs" + i, c.accent == i ? 1.0 : 0.0);
                if (sv > 0.01f) {
                    Gfx.aaCircle(this.gfx, cx, cy, 6.5f, 6085030, sv);
                    Gfx.aaCircle(this.gfx, cx, cy, 5.2f, 0x12211B, 1.0f);
                }
                Gfx.aaCircle(this.gfx, cx, cy, 4.2f, Theme.ACCENTS[i], 1.0f);
                int idx = i++;
                this.hit(Math.round(cx - 7.0f), y, 14, h, () -> {
                    c.accent = idx;
                    c.rgbAccent = false;
                    Fx.sound(Fx.Sfx.CLICK);
                    NeonLyrics.saveConfig();
                });
            }
        }
        this.rowEnd();
    }

    private void linkRow(String title, String sub, Runnable action) {
        int h = this.rowH(sub);
        int y = this.rowStart(h);
        if (y != Integer.MIN_VALUE) {
            String id = "lk" + title;
            this.rowBg(y, h, id);
            float hv = this.hover(this.vx, y, this.vw, h, id);
            float press = 1.0f - Anim.outCubic(Anim.since(this.pressed.getOrDefault(id, 0L), 260L));
            this.labels(y + Math.round(press), h, title, sub, this.vw - 40);
            Icons.draw(this.gfx, Icons.Icon.CHEVRON, (float)(this.vx + this.vw - 15) + hv * 3.0f + press * 3.0f, (float)y + (float)h / 2.0f, 6.0f + hv, Gfx.lerpColor(4877919, 14939884, hv), 1.0f);
            this.hit(this.vx, y, this.vw, h, () -> {
                this.pressed.put(id, System.currentTimeMillis());
                Fx.sound(Fx.Sfx.CLICK);
                action.run();
            }, id);
        }
        this.rowEnd();
    }

    private void fieldRow(String title, String sub) {
        int h = 58;
        int y = this.rowStart(h);
        if (y != Integer.MIN_VALUE) {
            this.rowBg(y, h, "field");
            int x = this.vx + 12;
            int w = this.vw - 24;
            this.txt(title, x, y + 8, 14939884, 1.0f, T.B);
            this.txt(this.ell(sub, w, T.S), x, y + 19, 8826011, 1.0f, T.S);
            float fk = this.pool.to("idfocus", this.idBox.method_25370() ? 1.0 : 0.0);
            Gfx.aaRound(this.gfx, x, y + 31, w, 20.0f, 6.0f, Gfx.lerpColor(2376247, 4165488, fk), 1.0f);
            Gfx.aaRound(this.gfx, x + 1, y + 32, w - 2, 18.0f, 5.0f, 595215, 1.0f);
            this.idWanted = true;
            this.idX = x + 7;
            this.idY = y + 37;
            this.idW = w - 14;
            this.hit(x, y + 31, w, 20, () -> this.method_25395((class_364)this.idBox));
        }
        this.rowEnd();
    }

    private void copyRow(String title, String value) {
        int h = 38;
        int y = this.rowStart(h);
        if (y != Integer.MIN_VALUE) {
            this.rowBg(y, h, "copy");
            String btn = LyricsScreen.t("btn.copy", new Object[0]);
            int bw = this.tw(btn, T.S) + 16;
            this.labels(y, h, title, value, this.vw - bw - 40);
            int bx = this.vx + this.vw - 12 - bw;
            int by = y + h / 2 - 9;
            float hv = this.hover(bx, by, bw, 18, "copyb");
            Gfx.aaRound(this.gfx, bx, by, bw, 18.0f, 9.0f, Gfx.lerpColor(2046512, 2839108, hv), 1.0f);
            this.txtC(btn, (float)bx + (float)bw / 2.0f, by + 5, 14939884, 1.0f, T.S);
            this.hit(bx, by, bw, 18, () -> {
                Fx.clipboard(value);
                Fx.sound(Fx.Sfx.CLICK);
                Island.notify(LyricsScreen.t("toast.copiedres", new Object[0]), true);
            });
        }
        this.rowEnd();
    }

    private void statusRow() {
        Config c = LyricsScreen.c();
        int h = 38;
        int y = this.rowStart(h);
        if (y != Integer.MIN_VALUE) {
            this.rowBg(y, h, "status");
            NowPlaying np = SpotifyClient.now();
            boolean ok = c.demoLyrics || (SpotifyClient.autoMode()
                ? LocalMedia.status() == LocalMedia.Status.PLAYING
                : SpotifyClient.status() == SpotifyClient.Status.CONNECTED);
            boolean bad = SpotifyClient.autoMode()
                ? LocalMedia.status() == LocalMedia.Status.FAILED || LocalMedia.status() == LocalMedia.Status.UNSUPPORTED
                : SpotifyClient.status() == SpotifyClient.Status.ERROR;
            int col = ok ? 6022299 : (bad ? 16735354 : 16762460);
            float pulse = ok ? 0.5f + 0.5f * Anim.pulse(1600.0) : 1.0f;
            Gfx.aaCircle(this.gfx, this.vx + this.vw - 20, (float)y + (float)h / 2.0f, 7.0f, col, 0.15f * pulse);
            Gfx.aaCircle(this.gfx, this.vx + this.vw - 20, (float)y + (float)h / 2.0f, 3.0f, col, 1.0f);
            String song = np.hasTrack() ? np.title + (String)(np.artist.isBlank() ? "" : " \u00b7 " + np.artist) : LyricsScreen.t("spotify.nothing", new Object[0]);
            this.labels(y, h, this.spotifyStatus(), song, this.vw - 50);
        }
        this.rowEnd();
    }

    private String spotifyStatus() {
        Config c = LyricsScreen.c();
        if (c.demoLyrics) {
            return LyricsScreen.t("spotify.st.demo", new Object[0]);
        }
        if (SpotifyClient.autoMode()) {
            return switch (LocalMedia.status()) {
                default -> throw new MatchException(null, null);
                case LocalMedia.Status.UNSUPPORTED -> LyricsScreen.t("spotify.auto.unsupported", new Object[0]);
                case LocalMedia.Status.STARTING -> LyricsScreen.t("spotify.auto.starting", new Object[0]);
                case LocalMedia.Status.NO_PLAYER -> LyricsScreen.t("spotify.auto.noplayer", new Object[0]);
                case LocalMedia.Status.PLAYING -> LyricsScreen.t("spotify.auto.on", LocalMedia.app());
                case LocalMedia.Status.FAILED -> LyricsScreen.t("spotify.auto.failed", new Object[0]);
            };
        }
        return switch (SpotifyClient.status()) {
            default -> throw new MatchException(null, null);
            case SpotifyClient.Status.NOT_CONFIGURED -> LyricsScreen.t("spotify.st.setup", new Object[0]);
            case SpotifyClient.Status.DISCONNECTED -> LyricsScreen.t("spotify.st.off", new Object[0]);
            case SpotifyClient.Status.WAITING_LOGIN -> LyricsScreen.t("spotify.st.wait", new Object[0]);
            case SpotifyClient.Status.CONNECTED -> LyricsScreen.t("spotify.st.on", new Object[0]);
            case SpotifyClient.Status.ERROR -> LyricsScreen.t("spotify.st.error", SpotifyClient.lastError());
        };
    }

    private void fpsRow() {
        Config c = LyricsScreen.c();
        int h = 50;
        int y = this.rowStart(h);
        if (y != Integer.MIN_VALUE) {
            this.rowBg(y, h, "fpsrow");
            int x = this.vx + 12;
            int w = this.vw - 24;
            String num = Integer.toString(Math.round(this.pool.to("fpsnum", AutoPerf.fps())));
            this.txt(num, x, y + 12, 14939884, 1.0f, T.XL);
            this.txt("FPS", x + this.tw(num, T.XL) + 5, y + 24, 8826011, 1.0f, T.M);
            int lv = AutoPerf.level();
            String st = !c.autoPerf ? LyricsScreen.t("perf.auto.off", new Object[0]) : (lv == 0 ? LyricsScreen.t("perf.auto.full", new Object[0]) : LyricsScreen.t("perf.auto.level", lv, 6));
            this.txtR(LyricsScreen.t("ui.perf.cost", String.format(Locale.ROOT, "%.1f", AutoPerf.modMs())), x + w, y + 15, 8826011, 1.0f, T.S);
            this.txtR(st, x + w, y + 26, 8826011, 1.0f, T.S);
        }
        this.rowEnd();
    }

    private void skyGrid() {
        Config c = LyricsScreen.c();
        int n = this.vw >= 330 ? 3 : 2;
        int gap = 8;
        int cw = (this.vw - gap * (n - 1)) / n;
        int ch = Math.round((float)cw * 0.5f);
        int rowH = ch + 22;
        int rows = (SKY_ORDER.length + n - 1) / n;
        int sel = c.sky;
        for (int r = 0; r < rows; ++r) {
            int y0 = this.rowStart(rowH);
            if (y0 != Integer.MIN_VALUE) {
                int i;
                for (int k = 0; k < n && (i = r * n + k) < SKY_ORDER.length; ++k) {
                    float a;
                    SkyPreset p = SKY_ORDER[i];
                    int x = this.vx + k * (cw + gap);
                    int y = y0 + 2;
                    boolean on = sel == p.ordinal();
                    float hv = this.hover(x, y, cw, ch + 16, "sky" + i);
                    float sv = this.pool.to("skysel" + i, on ? 1.0 : 0.0);
                    float clickPop = 1.0f - Anim.outCubic(Anim.since(this.pressed.getOrDefault("sky" + i, 0L), 300L));
                    float sc = this.spring("skysc" + i, 1.0f + 0.05f * hv) - 0.05f * clickPop;
                    int yy = Math.round((float)y - hv * 2.0f);
                    this.gfx.method_51448().pushMatrix();
                    this.gfx.method_51448().translate((float)x + (float)cw / 2.0f, (float)yy + (float)ch / 2.0f);
                    this.gfx.method_51448().scale(sc, sc);
                    this.gfx.method_51448().translate(-((float)x + (float)cw / 2.0f), -((float)yy + (float)ch / 2.0f));
                    if (hv > 0.01f) {
                        Gfx.aaRound(this.gfx, x, yy + 5, cw, ch, 8.0f, 0, 0.4f * hv);
                    }
                    if (sv > 0.01f) {
                        float ring = 2.0f + 2.0f * (1.0f - Anim.outBack(sv));
                        Gfx.aaRound(this.gfx, (float)x - ring, (float)yy - ring, (float)cw + ring * 2.0f, (float)ch + ring * 2.0f, 7.0f + ring, 6085030, sv);
                        Gfx.aaRound(this.gfx, (float)x - 0.75f, (float)yy - 0.75f, (float)cw + 1.5f, (float)ch + 1.5f, 7.5f, 726803, 1.0f);
                    }
                    float f = a = sel < 0 ? 0.45f : 0.8f + 0.2f * Math.max(hv, sv);
                    if (!SkyThumbs.draw(this.gfx, p, x, yy, cw, ch, a)) {
                        Gfx.aaRound(this.gfx, x, yy, cw, ch, 7.0f, p.top, a);
                    }
                    this.gfx.method_51448().popMatrix();
                    this.txt(this.ell(Fx.t(p.key(), new Object[0]), cw, T.S), x, y + ch + 6, Gfx.lerpColor(Gfx.lerpColor(8826011, 11983047, hv), 14939884, sv), 1.0f, on ? T.B : T.S);
                    SkyPreset target = p;
                    int ci = i;
                    this.hit(x, y, cw, ch + 16, () -> {
                        this.pressed.put("sky" + ci, System.currentTimeMillis());
                        this.setSky(target);
                        Fx.sound(Fx.Sfx.CLICK);
                        NeonLyrics.saveConfig();
                    });
                }
            }
            this.rowEnd();
        }
    }

    private class_2561 comp(String s, T t) {
        Map<String, class_2561> m = this.comps.computeIfAbsent(t, k -> new HashMap<>());
        if (m.size() > 600) {
            m.clear();
        }
        return m.computeIfAbsent(s, k -> class_2561.method_43470(k).method_27696(class_2583.field_24360.method_27704(FONTS.get(t))));
    }

    private int tw(String s, T t) {
        Integer v;
        if (s == null || s.isEmpty()) {
            return 0;
        }
        Map m = this.widths.computeIfAbsent(t, k -> new HashMap());
        if (m.size() > 600) {
            m.clear();
        }
        if ((v = (Integer)m.get(s)) == null) {
            v = this.field_22793.method_27525((class_5348)this.comp(s, t));
            m.put(s, v);
        }
        return v;
    }

    private void txt(String s, float x, float y, int col, float a, T t) {
        if (s == null || s.isEmpty() || a * Gfx.alpha < 0.02f) {
            return;
        }
        this.gfx.method_51439(this.field_22793, this.comp(s, t), Math.round(x), Math.round(y), Gfx.argb(col, a), false);
    }

    private void txtR(String s, float rx, float y, int col, float a, T t) {
        this.txt(s, rx - (float)this.tw(s, t), y, col, a, t);
    }

    private void txtC(String s, float cx, float y, int col, float a, T t) {
        this.txt(s, cx - (float)this.tw(s, t) / 2.0f, y, col, a, t);
    }

    private String ell(String s, int maxW, T t) {
        if (s == null) {
            return "";
        }
        if (this.tw(s, t) <= maxW) {
            return s;
        }
        while (s.length() > 1 && this.tw(s + "\u2026", t) > maxW) {
            s = s.substring(0, s.length() - 1);
        }
        return s + "\u2026";
    }

    private List<String> wrap(String s, int maxW, T t) {
        ArrayList<String> out = new ArrayList<String>();
        if (s == null || s.isEmpty()) {
            return out;
        }
        StringBuilder line = new StringBuilder();
        for (String w : s.split(" ")) {
            String test;
            String string = test = line.length() == 0 ? w : String.valueOf(line) + " " + w;
            if (this.tw(test, t) > maxW && line.length() > 0) {
                out.add(line.toString());
                line = new StringBuilder(w);
                continue;
            }
            line = new StringBuilder(test);
        }
        if (line.length() > 0) {
            out.add(line.toString());
        }
        return out;
    }

    private float spring(String id, float target) {
        float[] st = this.springs.computeIfAbsent(id, k -> new float[]{target, 0.0f});
        if (!this.anim()) {
            st[0] = target;
            st[1] = 0.0f;
            return target;
        }
        float h = Math.min(this.frameDt, 0.05f) / 3.0f;
        for (int i = 0; i < 3; ++i) {
            float acc = 320.0f * (target - st[0]) - 24.0f * st[1];
            st[1] = st[1] + acc * h;
            st[0] = st[0] + st[1] * h;
        }
        return st[0];
    }

    private void ripple(String id, int x, int y, int w, int h) {
        int y1;
        Ripple rp = this.ripples.get(id);
        if (rp == null) {
            return;
        }
        float age = (float)(System.currentTimeMillis() - rp.at()) / 520.0f;
        if (age >= 1.0f) {
            this.ripples.remove(id);
            return;
        }
        float e = Anim.outCubic(age);
        float cx = Math.max((float)x, Math.min((float)(x + w), rp.x()));
        float left = cx - (cx - (float)x) * e;
        float right = cx + ((float)(x + w) - cx) * e;
        float a = 0.16f * (1.0f - age);
        int y0 = this.inContent ? Math.max(y, this.vy) : y;
        int n = y1 = this.inContent ? Math.min(y + h, this.vy + this.vh) : y + h;
        if (y1 <= y0 || a < 0.005f) {
            return;
        }
        Gfx.aaRound(this.gfx, left, y0, Math.max(2.0f, right - left), y1 - y0, Math.min(6.0f, (float)(y1 - y0) / 2.0f), 6085030, a);
    }

    private void iconBtn(class_332 g, String id, int x, int y, int s, Icons.Icon icon, float iconSize, Runnable r) {
        float hv = this.hover(x, y, s, s, "ib" + id);
        float press = 1.0f - Anim.outCubic(Anim.since(this.pressed.getOrDefault(id, 0L), 220L));
        float cx = (float)x + (float)s / 2.0f;
        float cy = (float)y + (float)s / 2.0f;
        if (hv > 0.01f) {
            Gfx.aaCircle(g, cx, cy, (float)s / 2.0f * (0.8f + 0.2f * hv), 6085030, 0.07f * hv);
        }
        if (press > 0.01f) {
            Gfx.aaCircle(g, cx, cy, (float)s / 2.0f + 6.0f * (1.0f - press), 6085030, 0.14f * press);
        }
        Icons.draw(g, icon, cx, cy, iconSize * (1.0f - 0.15f * press + 0.08f * hv), Gfx.lerpColor(11983047, 6085030, hv), 1.0f);
        this.hit(x, y, s, s, () -> {
            this.pressed.put(id, System.currentTimeMillis());
            Fx.sound(Fx.Sfx.CLICK);
            r.run();
        });
    }

    private float hover(int x, int y, int w, int h, String id) {
        boolean on = this.closeTime < 0L && this.in(x, y, w, h) && (!this.inContent || this.my >= this.vy && this.my < this.vy + this.vh);
        return this.pool.to("hv" + id, on ? 1.0 : 0.0);
    }

    private boolean in(int x, int y, int w, int h) {
        return this.mx >= x && this.my >= y && this.mx < x + w && this.my < y + h;
    }

    private void hit(int x, int y, int w, int h, Runnable r) {
        this.hit(x, y, w, h, r, null);
    }

    private void hit(int x, int y, int w, int h, Runnable r, String rid) {
        if (this.closeTime >= 0L) {
            return;
        }
        if (this.inContent) {
            int y0 = Math.max(y, this.vy);
            int y1 = Math.min(y + h, this.vy + this.vh);
            if (y1 <= y0) {
                return;
            }
            this.hits.add(new Hit(x, y0, w, y1 - y0, r, rid, true));
            return;
        }
        this.hits.add(new Hit(x, y, w, h, r, rid, false));
    }

    private static String ago(long ms) {
        long m = ms / 60000L;
        if (m < 1L) {
            return LyricsScreen.t("ui.ago.now", new Object[0]);
        }
        if (m < 60L) {
            return LyricsScreen.t("ui.ago.min", m);
        }
        return LyricsScreen.t("ui.ago.h", m / 60L);
    }

    private static int indexOf(int[] a, int v) {
        for (int i = 0; i < a.length; ++i) {
            if (a[i] != v) continue;
            return i;
        }
        return -1;
    }

    private void setAllEffects(boolean on) {
        Config c = LyricsScreen.c();
        if (!on && c.sky >= 0) {
            c.lastSky = c.sky;
        }
        c.sky = on ? Math.max(0, c.lastSky) : -1;
        c.fxSkyColor = on;
        c.fxWeather = on;
        c.fxAurora = on;
        c.fxStars = on;
        c.fxBeams = on;
        c.lyricsGlow = on;
        c.glassHotbar = on;
        c.blur = on;
        c.fxMenuAnim = on;
        int n = c.quality = on ? 1 : 0;
        if (!on && !c.fpsBoost) {
            FpsBoost.apply(true);
        }
        if (on && c.fpsBoost) {
            FpsBoost.apply(false);
        }
        NeonLyrics.saveConfig();
        Island.notify(LyricsScreen.t(on ? "perf.allon.done" : "perf.alloff.done", new Object[0]), true);
    }

    private void openTutorial() {
        LyricsScreen.c().spotifyClientId = this.idBox.method_1882().trim();
        NeonLyrics.saveConfig();
        this.field_22787.method_1507((class_437)new SpotifyTutorialScreen(new LyricsScreen(Page.SPOTIFY)));
    }

    public boolean method_25402(class_11909 event, boolean doubleClick) {
        if (super.method_25402(event = this.fm(event), doubleClick)) {
            return true;
        }
        if (this.closeTime >= 0L || event.method_74245() != 0) {
            return false;
        }
        double ex = event.comp_4798() / (double)this.S;
        double ey = event.comp_4799() / (double)this.S;
        for (Slider s : this.sliders) {
            if (s.h() <= 0 || !(ex >= (double)(s.x() - 6)) || !(ex <= (double)(s.x() + s.w() + 6)) || !(ey >= (double)s.y()) || !(ey <= (double)(s.y() + s.h()))) continue;
            this.activeSlider = s;
            s.set().accept(Float.valueOf(Anim.clamp((float)((ex - (double)s.x()) / (double)s.w()))));
            return true;
        }
        for (int i = this.hits.size() - 1; i >= 0; --i) {
            Hit h = this.hits.get(i);
            if (!(ex >= (double)h.x()) || !(ey >= (double)h.y()) || !(ex < (double)(h.x() + h.w())) || !(ey < (double)(h.y() + h.h()))) continue;
            this.method_25395(null);
            if (h.rid() != null) {
                this.ripples.put(h.rid(), new Ripple((float)ex, (float)(h.content() ? ey - (double)this.vy + (double)this.scroll : ey), h.content(), System.currentTimeMillis()));
            }
            h.run().run();
            return true;
        }
        this.method_25395(null);
        return false;
    }

    public boolean method_25403(class_11909 event, double dx, double dy) {
        event = this.fm(event);
        if (this.activeSlider != null) {
            this.activeSlider.set().accept(Float.valueOf(Anim.clamp((float)((event.comp_4798() / (double)this.S - (double)this.activeSlider.x()) / (double)this.activeSlider.w()))));
            return true;
        }
        return super.method_25403(event, dx, dy);
    }

    public boolean method_25406(class_11909 event) {
        event = this.fm(event);
        if (this.activeSlider != null) {
            this.activeSlider = null;
            NeonLyrics.saveConfig();
            return true;
        }
        return super.method_25406(event);
    }

    public boolean method_25401(double x, double y, double h, double v) {
        this.scrollTarget -= (float)v * 30.0f;
        return true;
    }

    public boolean method_25404(class_11908 event) {
        boolean typing;
        int key = event.comp_4795();
        boolean bl = typing = this.method_25399() == this.idBox;
        if (!typing && key >= 49 && key < 49 + Page.values().length) {
            this.go(Page.values()[key - 49]);
            return true;
        }
        if (!typing && key == 258) {
            this.go(Page.values()[(this.page.ordinal() + 1) % Page.values().length]);
            return true;
        }
        if (!(typing || key != 264 && key != 267)) {
            this.scrollTarget += key == 264 ? 30.0f : (float)this.vh * 0.8f;
            return true;
        }
        if (!(typing || key != 265 && key != 266)) {
            this.scrollTarget -= key == 265 ? 30.0f : (float)this.vh * 0.8f;
            return true;
        }
        if (!typing && KeyBindingHelper.getBoundKeyOf((class_304)NeonLyrics.keyMenu).method_1444() == key) {
            this.method_25419();
            return true;
        }
        return super.method_25404(event);
    }

    public void method_25419() {
        if (this.closeTime < 0L) {
            this.closeTime = System.currentTimeMillis();
            LyricsScreen.c().spotifyClientId = this.idBox.method_1882().trim();
            NeonLyrics.saveConfig();
            LyricsScreen.show(this.idBox, false);
        }
    }

    public boolean method_25421() {
        return false;
    }

    static {
        FONTS.put(T.S, LyricsScreen.fd("menu_s"));
        FONTS.put(T.M, LyricsScreen.fd("menu_m"));
        FONTS.put(T.B, LyricsScreen.fd("menu_b"));
        FONTS.put(T.TITLE, LyricsScreen.fd("menu_t"));
        FONTS.put(T.XL, LyricsScreen.fd("menu_xl"));
        SKY_ORDER = new SkyPreset[]{SkyPreset.INFERNO, SkyPreset.MATRIX, SkyPreset.PRISM, SkyPreset.PULSAR, SkyPreset.AQUARELL, SkyPreset.SYNTHWAVE, SkyPreset.AURORA, SkyPreset.NEBULA, SkyPreset.BLACK_HOLE, SkyPreset.COSMOS, SkyPreset.NEON_CITY, SkyPreset.GOLDEN_HOUR, SkyPreset.COTTON_CANDY, SkyPreset.SAKURA, SkyPreset.FROST, SkyPreset.MIDNIGHT, SkyPreset.DEEP_SEA, SkyPreset.BLOOD_MOON, SkyPreset.STORM, SkyPreset.CUSTOM};
        DARK_COLS = new int[]{131848, 460058, 656920, 1378328, 201240, 1053982, 1839672, 2758216, 4068404, 668480, 1784412, 4852242};
        BRIGHT = new int[]{10719743, 13716479, 16743112, 0xFF6E6E, 16757596, 16769658, 4652976, 3073750, 8177919, 4942847, 0xF6F2FF, 6085030};
        LANGS = new String[]{"de", "en", "es", "fr", "tr", "pl", "it", "ru"};
        TARGETS = new int[]{0, 60, 90, 120, 144, 240};
        lastPage = Page.SKY;
    }

    public static enum Page {
        SKY,
        LYRICS,
        STYLE,
        SPOTIFY,
        DISPLAY,
        MENU,
        PERF,
        FAV;

    }

    private static enum T {
        S,
        M,
        B,
        TITLE,
        XL;

    }

    private record Slider(String id, int x, int y, int w, int h, Consumer<Float> set) {
    }

    private static interface Cell {
        public void draw(int var1, int var2, int var3, int var4);
    }

    private record Ripple(float x, float y, boolean content, long at) {
    }

    private record Hit(int x, int y, int w, int h, Runnable run, String rid, boolean content) {
    }
}

