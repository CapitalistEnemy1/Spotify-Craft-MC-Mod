/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
 *  net.minecraft.class_12249
 *  net.minecraft.class_1937
 *  net.minecraft.class_243
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_327$class_6415
 *  net.minecraft.class_4184
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
 *  net.minecraft.class_746
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 */
package dev.neonlyrics.sky;

import dev.neonlyrics.License;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.cosmetic.Accessories;
import dev.neonlyrics.cosmetic.DeathFx;
import dev.neonlyrics.cosmetic.PhoneMenu;
import dev.neonlyrics.hud.Anim;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.hud.Theme;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.lyrics.SpotifyClient;
import dev.neonlyrics.sky.FloatWords;
import dev.neonlyrics.sky.LiveSky;
import dev.neonlyrics.sky.LiveSkyShader;
import dev.neonlyrics.sky.SkyPreset;
import dev.neonlyrics.sky.SkyTextures;
import dev.neonlyrics.util.AutoPerf;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.class_12249;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

public final class WorldSky {
    private static final Random RND = new Random();
    private static float[][] stars;
    private static final List<float[]> shooting;
    private static long nextShooting;
    private static final long T0;
    private static double[][] parts;
    private static SkyPreset.Weather partType;
    private static long lastNanos;
    private static final Anim visible;
    private static final Anim outside;
    private static final Anim musicCalm;
    private static double fx;
    private static double fy;
    private static double fz;
    private static double rx;
    private static double ry;
    private static double rz;
    private static double ux;
    private static double uy;
    private static double uz;
    private static float camYaw;
    private static float camPitch;
    private static double dist;
    private static double texDist;
    private static double skyD;
    private static boolean skyOpen;
    private static Matrix4f base;
    private static double skyTime;
    private static double breathT;
    private static final float[] Q_STARS;
    private static final double[] Q_AURORA_STEP;
    private static final float[] Q_WEATHER;
    private static final int[] Q_DOME;
    private static final int[] Q_MOON;
    private static final int[] Q_GLOW;
    private static class_4588 quads;
    private static final double[][] RING;
    private static int ringPos;
    private static final float[] DAY_AL;
    private static final double[] DOME_EL;
    private static final float[] DOME_AL;
    private static final float[] DOME_A;
    private static final int[] DOME_C;
    private static double[][][] domeGridCache;
    private static int domeGridStep;
    private static double flashNext;
    private static double flashT0;
    private static double[][] bolt;
    public static final int[][] PALETTES;
    public static final String[] PALETTE_KEYS;
    private static final double[][][] CONST;
    private static String introTrack;
    private static long introStart;
    private static double introAz;
    private static double introEl;
    private static final int PF = 11;
    private static final double[] TEX_EL;
    private static final float MESH_EL0 = -4.0f;
    private static final float MESH_EL1 = 90.0f;
    private static int meshVer;
    private static int meshNA;
    private static int meshNE;
    private static double meshStep;
    private static float[] mX;
    private static float[] mY;
    private static float[] mZ;
    private static float[] mAz;
    private static float[] mEl;
    private static int[] mBase;
    private static int[] mFrame;
    private static int[] mStamp;
    private static byte[] mDetail;
    private static int frameNo;
    private static double liveStep;
    private static int liveNA;
    private static int liveNE;
    private static float[] lX;
    private static float[] lY;
    private static float[] lZ;
    private static float[] lEl;
    private static int[] lCol;
    private static int[] lStamp;
    private static SkyPreset lastLive;
    private static long liveSince;
    private static float fogR;
    private static float fogG;
    private static float fogB;
    private static float fogMix;
    private static final int[] SUB_COL;
    private static final float[][] SUB_P;
    private static final ArrayDeque<double[]> TRAIL;
    private static long lastTrail;
    private static double lastTx;
    private static double lastTz;
    private static double lastSide;
    private static SkyLine active;
    private static final List<SkyLine> exiting;
    private static int activeIdx;
    private static String activeTrack;
    private static final Matrix4f M0;
    private static final Matrix4f TMP;
    private static final Matrix4f WM;
    private static final Matrix4f SCR;
    private static final Map<String, class_2561> COMP;
    private static int compFont;
    public static final int[] LYRIC_COLORS;

    private WorldSky() {
    }

    private static int q() {
        return AutoPerf.quality();
    }

    public static void render(WorldRenderContext ctx) {
        if (!License.ok()) {
            return;
        }
        AutoPerf.frame();
        AutoPerf.begin();
        try {
            WorldSky.renderSky(ctx);
        }
        finally {
            AutoPerf.end();
        }
    }

    private static void renderSky(WorldRenderContext ctx) {
        int lyricCol;
        long tod;
        fogMix = 0.0f;
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        Config c = NeonLyrics.cfg();
        SkyPreset p = c.customSkyboxEnabled ? SkyPreset.get(c.sky) : null;
        long nowNs = System.nanoTime();
        float dt = Math.min(0.1f, (float)(nowNs - lastNanos) / 1.0E9f);
        lastNanos = nowNs;
        float pt = mc.method_61966().method_60637(false);
        class_4184 mainCam = mc.field_1773.method_19418();
        camYaw = mainCam.method_19330();
        camPitch = mainCam.method_19329();
        double y = Math.toRadians(camYaw);
        double pi = Math.toRadians(camPitch);
        fx = -Math.sin(y) * Math.cos(pi);
        fy = -Math.sin(pi);
        fz = Math.cos(y) * Math.cos(pi);
        rx = -Math.cos(y);
        ry = 0.0;
        rz = -Math.sin(y);
        ux = ry * fz - rz * fy;
        uy = rz * fx - rx * fz;
        uz = rx * fy - ry * fx;
        dist = Math.max(40.0, Math.min(180.0, (double)(mc.field_1690.method_38521() * 16) * 0.7));
        texDist = Math.max(dist * 0.975, (double)(mc.field_1690.method_38521() * 16) * 0.8);
        skyD = Math.max(dist, (double)(mc.field_1690.method_38521() * 16) * 1.04 + 8.0);
        boolean overworld = mc.field_1687.method_27983() == class_1937.field_25179;
        float vis = visible.target(p != null && overworld ? 1.0 : 0.0).get();
        skyOpen = mc.field_1687.method_8311(mc.field_1724.method_24515().method_10084());
        float out = outside.target(skyOpen ? 1.0 : 0.0).get();
        breathT += (double)(dt * c.skyBreath) * 1.3;
        double t = skyTime += (double)(dt * Math.max(0.1f, Math.min(3.0f, c.skySpeed)));
        class_4587 ps = ctx.matrices();
        base = new Matrix4f((Matrix4fc)ps.method_23760().method_23761());
        class_4597 buffers = ctx.consumers();
        NowPlaying npm = SpotifyClient.now();
        float beat = 0.0f;
        float calm = 1.0f;
        if (c.fxSkyMusic && npm.hasTrack() && npm.playing) {
            long pr = npm.progressNow(c.lyricsOffsetMs);
            beat = npm.beatPulse(pr);
            int li = npm.lineIndex(pr);
            boolean singing = li >= 0 && pr <= npm.lineEnd(li) + 300L;
            calm = musicCalm.target(singing ? 1.0 : (double)0.7f).get();
        }
        float nightF = (tod = Math.floorMod(mc.field_1687.method_8532(), 24000L)) >= 13000L && tod <= 23000L ? 1.0f : (tod > 12000L && tod < 13000L ? (float)(tod - 12000L) / 1000.0f : (tod > 23000L ? 1.0f - (float)(tod - 23000L) / 1000.0f : 0.0f));
        float cover = 0.0f;
        float night = 1.0f;
        if (c.skyDay) {
            cover = 1.0f;
        } else if (c.fxDayNight) {
            night = 0.35f + 0.65f * nightF;
        }
        if (p != null && vis > 0.01f) {
            SkyTextures.update();
            float strength = p.strength * c.skyStrength * vis * night;
            quads = buffers.method_73477(class_12249.method_76023());
            if (c.fxSkyColor || cover > 0.01f) {
                WorldSky.drawDome(p, strength, cover * vis, mc);
            }
            LiveSky.MAIN.prepare(p, WorldSky.aurora1(p), WorldSky.aurora2(p), t, c);
            float liveA = vis * WorldSky.liveFade(p);
            float liveB = night * (0.92f + 0.08f * calm) * (1.0f + 0.12f * beat);
            WorldSky.updateFog(liveA, liveB);
            if (!WorldSky.drawLiveShader(p, liveA, liveB, t, c, buffers, mc)) {
                WorldSky.drawLiveSky(liveA, liveB, mc);
            }
            quads = null;
            quads = buffers.method_73477(class_12249.method_76023());
            if (p.stars > 0.0f && c.fxStars && AutoPerf.allow(5)) {
                WorldSky.drawStars(p, vis * night * (0.85f + 0.3f * beat), t);
            }
            if (p.stars > 0.0f && c.fxStars && c.fxConstellations && AutoPerf.allow(2)) {
                WorldSky.drawConstellations(vis * night);
            }
            if (p == SkyPreset.STORM && AutoPerf.allow(2)) {
                WorldSky.drawStorm(vis, t);
            }
            if ((p.shootingStars || p.stars >= 0.8f) && AutoPerf.allow(3)) {
                WorldSky.drawShooting(vis, dt);
            }
            WorldSky.drawBody(p, vis, t);
            quads = null;
            if (c.fxWeather && SkyTextures.spritesReady() && AutoPerf.allow(4) && overworld && c.weatherAmount > 0.01f && p.weather != SkyPreset.Weather.NONE && out > 0.01f) {
                class_243 cam = ctx.worldState().field_63082.field_63078;
                class_4588 ts = buffers.method_73477(class_12249.method_76022((class_2960)SkyTextures.SPRITES));
                WorldSky.drawWeather(ts, p.weather, c.weatherAmount * (AutoPerf.allow(3) ? 1.0f : 0.5f), out, dt, t, cam);
            }
        }
        lyricCol = c.lyricsColor >= 0 ? LYRIC_COLORS[Math.floorMod(c.lyricsColor, LYRIC_COLORS.length)] : (p == null ? Theme.accent() : p.lyricColor);
        if (c.lyricsSky && c.lyricsStyle == 0) {
            WorldSky.drawLyrics(mc, c, buffers, lyricCol, dt);
        }
        if (c.lyricsStyle == 1) {
            FloatWords.renderWorld(mc, c, buffers, base, ctx.worldState().field_63082.field_63078, pt, !skyOpen || c.lyricsUnderground);
        }
        if (c.fxSongIntro) {
            WorldSky.drawSongIntro(mc, c, buffers, lyricCol);
        }
        class_243 camV = ctx.worldState().field_63082.field_63078;
        if (c.trail > 0 || c.aura) {
            SkyTextures.update();
            WorldSky.drawPlayerFx(mc, c, buffers, camV, pt, t, p);
        }
        Accessories.render(mc, buffers, base, camV, pt);
        DeathFx.render(mc, buffers, base, camV, pt);
        PhoneMenu.render(mc, buffers, base, pt);
        base = null;
    }

    private static double[] p(double x, double y, double z) {
        double[] r = RING[ringPos];
        ringPos = ringPos + 1 & 0xF;
        r[0] = x;
        r[1] = y;
        r[2] = z;
        return r;
    }

    private static double[] dir(double azDeg, double elDeg) {
        double a = Math.toRadians(azDeg);
        double e = Math.toRadians(elDeg);
        double ce = Math.cos(e);
        return WorldSky.p(-Math.sin(a) * ce, Math.sin(e), Math.cos(a) * ce);
    }

    private static void v(double x, double y, double z, int rgb, float a) {
        quads.method_22918((Matrix4fc)base, (float)x, (float)y, (float)z).method_39415(Gfx.argbRaw(rgb, a));
    }

    private static void quad(double[] p0, double[] p1, double[] p2, double[] p3, int c0, float a0, int c1, float a1, int c2, float a2, int c3, float a3) {
        if (a0 <= 0.003f && a1 <= 0.003f && a2 <= 0.003f && a3 <= 0.003f) {
            return;
        }
        WorldSky.v(p0[0], p0[1], p0[2], c0, a0);
        WorldSky.v(p1[0], p1[1], p1[2], c1, a1);
        WorldSky.v(p2[0], p2[1], p2[2], c2, a2);
        WorldSky.v(p3[0], p3[1], p3[2], c3, a3);
        WorldSky.v(p3[0], p3[1], p3[2], c3, a3);
        WorldSky.v(p2[0], p2[1], p2[2], c2, a2);
        WorldSky.v(p1[0], p1[1], p1[2], c1, a1);
        WorldSky.v(p0[0], p0[1], p0[2], c0, a0);
    }

    private static double[] at(double azDeg, double elDeg, double d) {
        double[] q = WorldSky.dir(azDeg, elDeg);
        q[0] = q[0] * d;
        q[1] = q[1] * d;
        q[2] = q[2] * d;
        return q;
    }

    private static void billboard(double cx, double cy, double cz, double size, int rgb, float a) {
        double s = size / 2.0;
        double[] p0 = WorldSky.p(cx - rx * s - ux * s, cy - ry * s - uy * s, cz - rz * s - uz * s);
        double[] p1 = WorldSky.p(cx + rx * s - ux * s, cy + ry * s - uy * s, cz + rz * s - uz * s);
        double[] p2 = WorldSky.p(cx + rx * s + ux * s, cy + ry * s + uy * s, cz + rz * s + uz * s);
        double[] p3 = WorldSky.p(cx - rx * s + ux * s, cy - ry * s + uy * s, cz - rz * s + uz * s);
        WorldSky.cquad(p0, p1, p2, p3, rgb, a, rgb, a, rgb, a, rgb, a);
    }

    private static void streak(double ax, double ay, double az, double bx, double by, double bz, double w, int rgb, float a0, float a1) {
        double dy = by - ay;
        double vz = (az + bz) / 2.0;
        double dz = bz - az;
        double vy = (ay + by) / 2.0;
        double sx = dy * vz - dz * vy;
        double vx = (ax + bx) / 2.0;
        double dx = bx - ax;
        double sy = dz * vx - dx * vz;
        double sz = dx * vy - dy * vx;
        double l = Math.sqrt(sx * sx + sy * sy + sz * sz);
        if (l < 1.0E-6) {
            return;
        }
        double k = w / 2.0 / l;
        WorldSky.quad(WorldSky.p(ax - (sx *= k), ay - (sy *= k), az - (sz *= k)), WorldSky.p(ax + sx, ay + sy, az + sz), WorldSky.p(bx + sx, by + sy, bz + sz), WorldSky.p(bx - sx, by - sy, bz - sz), rgb, a0, rgb, a0, rgb, a1, rgb, a1);
    }

    private static double[][][] domeGrid(int step) {
        if (domeGridCache != null && domeGridStep == step) {
            return domeGridCache;
        }
        int cols = 360 / step;
        double[][][] g = new double[cols + 1][DOME_EL.length][3];
        for (int j = 0; j <= cols; ++j) {
            double a = Math.toRadians(j * step);
            for (int i = 0; i < DOME_EL.length; ++i) {
                double e = Math.toRadians(DOME_EL[i]);
                g[j][i][0] = -Math.sin(a) * Math.cos(e);
                g[j][i][1] = Math.sin(e);
                g[j][i][2] = Math.cos(a) * Math.cos(e);
            }
        }
        domeGridCache = g;
        domeGridStep = step;
        return g;
    }

    private static void drawDome(SkyPreset p, float s, float cover, class_310 mc) {
        double d = skyD * 1.25;
        int step = Q_DOME[WorldSky.q()];
        double[][][] grid = WorldSky.domeGrid(step);
        float[] a = DOME_A;
        int[] col = DOME_C;
        for (int i = 0; i < DOME_EL.length; ++i) {
            a[i] = DOME_AL[i] * s + (DAY_AL[i] - DOME_AL[i] * s) * cover;
            col[i] = Gfx.lerpColor(p.horizon, p.top, (float)Math.max(0.0, DOME_EL[i] / 45.0));
        }
        int n = grid.length - 1;
        for (int j = 0; j < n; ++j) {
            double[] mid = grid[j][3];
            if (mid[0] * fx + mid[2] * fz < -0.55 && fy > -0.6 && fy < 0.6) continue;
            for (int i = 0; i < DOME_EL.length - 1; ++i) {
                double[] g00 = grid[j][i];
                double[] g10 = grid[j + 1][i];
                double[] g11 = grid[j + 1][i + 1];
                double[] g01 = grid[j][i + 1];
                WorldSky.quad(WorldSky.p(g00[0] * d, g00[1] * d, g00[2] * d), WorldSky.p(g10[0] * d, g10[1] * d, g10[2] * d), WorldSky.p(g11[0] * d, g11[1] * d, g11[2] * d), WorldSky.p(g01[0] * d, g01[1] * d, g01[2] * d), col[i], a[i], col[i], a[i], col[i + 1], a[i + 1], col[i + 1], a[i + 1]);
            }
        }
    }

    private static void initStars() {
        Random r = new Random(1337L);
        for (float[] s : stars = new float[1300][8]) {
            s[0] = r.nextFloat() * 360.0f;
            s[1] = (float)Math.toDegrees(Math.asin(0.04 + (double)r.nextFloat() * 0.96));
            s[2] = r.nextFloat() < 0.1f ? 1.8f : 1.0f;
            s[3] = 0.35f + r.nextFloat() * 0.65f;
            s[4] = r.nextFloat() * 6.28f;
            double a = Math.toRadians(s[0]);
            double e = Math.toRadians(s[1]);
            s[5] = (float)(-Math.sin(a) * Math.cos(e));
            s[6] = (float)Math.sin(e);
            s[7] = (float)(Math.cos(a) * Math.cos(e));
        }
    }

    private static void drawStars(SkyPreset p, float vis, double t) {
        if (stars == null) {
            WorldSky.initStars();
        }
        int count = (int)Math.min((float)stars.length, 520.0f * p.stars * Q_STARS[WorldSky.q()]);
        double d = skyD * 0.98;
        double px = d * 0.0016;
        for (int i = 0; i < count; ++i) {
            float[] s = stars[i];
            if ((double)s[5] * fx + (double)s[6] * fy + (double)s[7] * fz < 0.05) continue;
            float tw = (float)(0.75 + 0.25 * Math.sin(t * (0.8 + (double)s[4] * 0.2) + (double)s[4]));
            float a = s[3] * tw * vis * Math.min(1.0f, s[1] / 10.0f);
            float live = NeonLyrics.cfg().skyLively;
            float tw2 = live * (float)Math.pow(0.5 + 0.5 * Math.sin(t * (1.3 + (double)s[4] * 0.4) + (double)(s[4] * 3.0f)), 3.0) + (1.0f - live) * 0.5f;
            WorldSky.billboard((double)s[5] * d, (double)s[6] * d, (double)s[7] * d, px * 1.7 * (double)s[2], 0xF4F0FF, a * (0.25f + 0.75f * tw2));
        }
    }

    private static float flashLevel(double t) {
        double age;
        if (t >= flashNext) {
            flashT0 = t;
            flashNext = t + 0.5 + RND.nextDouble() * 2.0;
            bolt = WorldSky.makeBolt();
        }
        if ((age = t - flashT0) < 0.05) {
            return 1.0f;
        }
        if (age < 0.12) {
            return 0.25f;
        }
        if (age < 0.18) {
            return 0.9f;
        }
        if (age < 0.6) {
            return (float)(0.8 * Math.exp(-(age - 0.18) * 9.0));
        }
        return 0.0f;
    }

    private static double[][] makeBolt() {
        int n = 10;
        double[][] b = new double[n][2];
        double az = RND.nextDouble() * 360.0;
        for (int i = 0; i < n; ++i) {
            double f = (double)i / (double)(n - 1);
            b[i][0] = az + (RND.nextDouble() - 0.5) * 9.0;
            b[i][1] = 58.0 - f * 48.0 + (RND.nextDouble() - 0.5) * 4.0;
            az += (RND.nextDouble() - 0.5) * 6.0;
        }
        return b;
    }

    private static void drawStorm(float vis, double t) {
        float fl = WorldSky.flashLevel(t);
        double d = skyD * 0.97;
        if (fl > 0.02f && bolt.length > 1) {
            double[] z = WorldSky.at(0.0, 70.0, d);
            WorldSky.billboard(z[0], z[1], z[2], d * 1.2, 13161727, 0.35f * fl * vis);
            int i = 0;
            while (i + 1 < bolt.length) {
                double[] p0 = WorldSky.at(bolt[i][0], bolt[i][1], d);
                double[] p1 = WorldSky.at(bolt[i + 1][0], bolt[i + 1][1], d);
                WorldSky.streak(p0[0], p0[1], p0[2], p1[0], p1[1], p1[2], d * 0.006, 12110079, fl * vis, fl * vis);
                WorldSky.streak(p0[0], p0[1], p0[2], p1[0], p1[1], p1[2], d * 0.0025, 0xFFFFFF, fl * vis, fl * vis);
                ++i;
            }
        }
    }

    public static int aurora1(SkyPreset p) {
        int pal = NeonLyrics.cfg().skyPalette;
        if (pal >= 0 && pal < PALETTES.length) {
            return PALETTES[pal][0];
        }
        return p.aurora1 != 0 ? p.aurora1 : p.lyricColor;
    }

    public static int aurora2(SkyPreset p) {
        int pal = NeonLyrics.cfg().skyPalette;
        if (pal >= 0 && pal < PALETTES.length) {
            return PALETTES[pal][1];
        }
        return p.aurora2 != 0 ? p.aurora2 : 0xFFFFFF;
    }

    private static void drawConstellations(float vis) {
        double d = skyD * 0.975;
        double px = d * 0.0016;
        for (double[][] cst : CONST) {
            for (int i = 0; i < cst.length; ++i) {
                double[] a = WorldSky.at(cst[i][0], cst[i][1], d);
                double ax = a[0];
                double ay = a[1];
                double az = a[2];
                WorldSky.billboard(ax, ay, az, px * 2.6, 0xFFFFFF, 0.9f * vis);
                if (i + 1 >= cst.length) continue;
                double[] b = WorldSky.at(cst[i + 1][0], cst[i + 1][1], d);
                WorldSky.streak(ax, ay, az, b[0], b[1], b[2], px * 0.7, 14208255, 0.22f * vis, 0.22f * vis);
            }
        }
    }

    private static void drawSongIntro(class_310 mc, Config c, class_4597 buffers, int color) {
        long age;
        NowPlaying np = SpotifyClient.now();
        if (!np.hasTrack()) {
            return;
        }
        String id = String.valueOf(np.trackId);
        long now = System.currentTimeMillis();
        if (!id.equals(introTrack)) {
            introTrack = id;
            introStart = now;
            introAz = camYaw;
            introEl = Math.max(8.0f, Math.min(50.0f, -camPitch + 10.0f));
        }
        if ((age = now - introStart) > 3800L) {
            return;
        }
        float a = Math.min(1.0f, (float)age / 500.0f) * (age > 3000L ? 1.0f - (float)(age - 3000L) / 800.0f : 1.0f) * c.lyricsOpacity;
        if (a <= 0.03f) {
            return;
        }
        double d = dist * 0.88;
        double dpp = WorldSky.degPerPx(mc, c);
        float k = (float)(Math.toRadians(dpp) * d);
        class_327 font = mc.field_1772;
        String title = np.title.length() > 32 ? np.title.substring(0, 31) + "\u2026" : np.title;
        WorldSky.drawFlat(font, buffers, title, introAz, introEl + 2.0 + (double)((1.0f - Math.min(1.0f, (float)age / 700.0f)) * 2.0f), d, k * 1.6f, color, a);
        if (!np.artist.isBlank()) {
            WorldSky.drawFlat(font, buffers, np.artist, introAz, introEl - 4.0, d, k * 0.9f, 0xFFFFFF, a * 0.75f);
        }
    }

    private static void drawFlat(class_327 font, class_4597 buffers, String text, double azDeg, double elDeg, double d, float k, int col, float a) {
        double az = Math.toRadians(azDeg);
        double el = Math.toRadians(elDeg);
        double dx = -Math.sin(az) * Math.cos(el);
        double dy = Math.sin(el);
        double dz = Math.cos(az) * Math.cos(el);
        double r0x = -Math.cos(az);
        double r0z = -Math.sin(az);
        double ux0 = -r0z * dy;
        double uy0 = r0z * dx - r0x * dz;
        double uz0 = r0x * dy;
        Matrix4f m = new Matrix4f((Matrix4fc)base).mul((Matrix4fc)new Matrix4f((float)r0x * k, 0.0f, (float)r0z * k, 0.0f, (float)(-ux0) * k, (float)(-uy0) * k, (float)(-uz0) * k, 0.0f, (float)(-dx) * k, (float)(-dy) * k, (float)(-dz) * k, 0.0f, (float)(dx * d), (float)(dy * d), (float)(dz * d), 1.0f));
        int w = Gfx.w(text, Gfx.LYRICS);
        m.translate((float)(-w) / 2.0f, -4.5f, 0.0f);
        WorldSky.word(font, buffers, m, text, 0.0f, 1.0f, 0.0f, Gfx.lerpColor(col, 656916, 0.7f), a * 0.6f, false);
        WorldSky.word(font, buffers, m, text, 0.0f, 0.0f, 0.1f, col, a, false);
    }

    private static void drawShooting(float vis, float dt) {
        long now = System.currentTimeMillis();
        if (now > nextShooting) {
            nextShooting = now + 1200L + (long)RND.nextInt(3500);
            shooting.add(new float[]{camYaw + (RND.nextFloat() - 0.5f) * 80.0f, 30.0f + RND.nextFloat() * 30.0f, now - T0, RND.nextBoolean() ? 1.0f : -1.0f});
        }
        shooting.removeIf(s -> now - (T0 + (long)s[2]) > 900L || now < T0 + (long)s[2] - 1000L);
        double d = skyD * 0.97;
        for (float[] s2 : shooting) {
            float life = Anim.clamp((float)(now - (T0 + (long)s2[2])) / 900.0f);
            double az = s2[0] + s2[3] * life * 30.0f;
            double el = s2[1] - life * 12.0f;
            double az2 = az - (double)(s2[3] * 6.0f);
            double el2 = el + 2.4;
            double[] h = WorldSky.at(az, el, d);
            double hx = h[0];
            double hy = h[1];
            double hz = h[2];
            double[] tl = WorldSky.at(az2, el2, d);
            float a = vis * (1.0f - life);
            WorldSky.streak(tl[0], tl[1], tl[2], hx, hy, hz, d * 0.004, 0xFFFFFF, 0.0f, a);
        }
    }

    private static void drawWeather(class_4588 vc, SkyPreset.Weather w, float amount, float out, float dt, double t, class_243 eye) {
        int n = switch (w) {
            case SkyPreset.Weather.BLIZZARD -> 240;
            case SkyPreset.Weather.SNOW -> 200;
            case SkyPreset.Weather.RAIN, SkyPreset.Weather.THUNDER -> 220;
            case SkyPreset.Weather.PETALS -> 150;
            case SkyPreset.Weather.EMBERS -> 80;
            case SkyPreset.Weather.FIREFLIES -> 45;
            default -> 0;
        };
        n = (int)((float)n * amount * Q_WEATHER[WorldSky.q()]);
        double box = 14.0;
        double hb = 9.0;
        if (partType != w || parts.length != n) {
            partType = w;
            for (double[] q : parts = new double[n][11]) {
                q[0] = eye.field_1352 + (RND.nextDouble() * 2.0 - 1.0) * box;
                q[1] = eye.field_1351 + (RND.nextDouble() * 2.0 - 1.0) * hb;
                q[2] = eye.field_1350 + (RND.nextDouble() * 2.0 - 1.0) * box;
                q[3] = RND.nextDouble() * 6.28;
                q[4] = 0.6 + RND.nextDouble() * 0.8;
                double ax = RND.nextGaussian();
                double ay = RND.nextGaussian() * 0.6;
                double az = RND.nextGaussian();
                double l = Math.sqrt(ax * ax + ay * ay + az * az) + 1.0E-9;
                q[5] = ax / l;
                q[6] = ay / l;
                q[7] = az / l;
                q[8] = (double)(RND.nextBoolean() ? 1 : -1) * (1.2 + RND.nextDouble() * 2.6);
                q[9] = 0.75 + RND.nextDouble() * 0.6;
                q[10] = RND.nextDouble();
            }
        }
        block23: for (double[] q : parts) {
            double vy;
            double vx;
            double ph = q[3];
            double sp = q[4];
            double vz = switch (w) {
                case SkyPreset.Weather.BLIZZARD -> {
                    vx = -8.5 * sp;
                    vy = -4.0 * sp;
                    yield 2.8 * sp;
                }
                case SkyPreset.Weather.SNOW -> {
                    vx = 0.4 + 0.5 * Math.sin(t * 0.8 + ph);
                    vy = -1.3 * sp;
                    yield 0.3 * Math.cos(t * 0.7 + ph);
                }
                case SkyPreset.Weather.RAIN, SkyPreset.Weather.THUNDER -> {
                    vx = 0.6;
                    vy = -17.0 * sp;
                    yield 0.2;
                }
                case SkyPreset.Weather.PETALS -> {
                    vx = 0.7 + 0.8 * Math.sin(t * 0.9 + ph);
                    vy = -0.55 * sp - 0.25 * Math.sin(t * 2.1 + ph * 2.0);
                    yield 0.45 * Math.cos(t * 0.7 + ph);
                }
                case SkyPreset.Weather.EMBERS -> {
                    vx = 0.3 * Math.sin(t * 1.3 + ph);
                    vy = 0.9 * sp;
                    yield 0.3 * Math.cos(t + ph);
                }
                default -> {
                    vx = 0.4 * Math.sin(t * 0.5 + ph);
                    vy = 0.25 * Math.sin(t * 0.7 + ph * 2.0);
                    yield 0.4 * Math.cos(t * 0.45 + ph);
                }
            };
            q[0] = q[0] + vx * (double)dt;
            q[1] = q[1] + vy * (double)dt;
            q[2] = q[2] + vz * (double)dt;
            double dx = WorldSky.wrap(q[0] - eye.field_1352, box);
            double dy = WorldSky.wrap(q[1] - eye.field_1351, hb);
            double dz = WorldSky.wrap(q[2] - eye.field_1350, box);
            q[0] = eye.field_1352 + dx;
            q[1] = eye.field_1351 + dy;
            q[2] = eye.field_1350 + dz;
            if (dx * fx + dy * fy + dz * fz < 0.3) continue;
            double dd = Math.sqrt(dx * dx + dy * dy + dz * dz);
            float near = (float)Math.min(1.0, 5.0 / Math.max(0.5, dd));
            float far = (float)(1.0 - Math.max(0.0, Math.min(1.0, (dd - 9.0) / 6.0)));
            switch (w) {
                case PETALS: {
                    double ang = q[3] + t * q[8];
                    int col = Gfx.lerpColor(0xFFFFFF, 16765670, (float)q[10]);
                    WorldSky.petal(vc, dx, dy, dz, 0.16 * q[9], q[5], q[6], q[7], ang, col, out * far * 0.95f);
                    continue block23;
                }
                case SNOW: {
                    WorldSky.sprite(vc, dx, dy, dz, 0.11 * q[9], 1, 0xFFFFFF, out * far * (0.55f + 0.4f * near));
                    continue block23;
                }
                case BLIZZARD: {
                    WorldSky.spriteStreak(vc, dx - vx * 0.06, dy - vy * 0.06, dz - vz * 0.06, dx, dy, dz, 0.07, 0xF4F2FF, out * far * (0.45f + 0.4f * near));
                    continue block23;
                }
                case RAIN: 
                case THUNDER: {
                    WorldSky.spriteStreak(vc, dx - vx * 0.035, dy - vy * 0.035, dz - vz * 0.035, dx, dy, dz, 0.05, 12767468, out * far * (0.3f + 0.35f * near));
                    continue block23;
                }
                case EMBERS: {
                    float fl = (float)(0.5 + 0.5 * Math.sin(t * 9.0 + ph * 3.0));
                    WorldSky.sprite(vc, dx, dy, dz, 0.22 * q[9], 2, Gfx.lerpColor(16726815, 16760906, fl), out * far * (0.5f + 0.5f * fl));
                    continue block23;
                }
                default: {
                    float pulse = (float)Math.pow(0.5 + 0.5 * Math.sin(t * 2.2 + ph * 4.0), 2.0);
                    WorldSky.sprite(vc, dx, dy, dz, 0.6, 2, 14090090, out * far * (0.15f + 0.85f * pulse));
                }
            }
        }
    }

    private static void tv(class_4588 vc, double x, double y, double z, float u, float v, int argb) {
        vc.method_22918((Matrix4fc)base, (float)x, (float)y, (float)z).method_39415(argb).method_22913(u, v).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 1.0f, 0.0f);
    }

    private static void tquad(class_4588 vc, double[] p0, double[] p1, double[] p2, double[] p3, float u0, float v0, float u1, float v1, int col) {
        double ay = p1[1] - p0[1];
        double bz = p2[2] - p0[2];
        double az = p1[2] - p0[2];
        double by = p2[1] - p0[1];
        double nx = ay * bz - az * by;
        double bx = p2[0] - p0[0];
        double ax = p1[0] - p0[0];
        double ny = az * bx - ax * bz;
        double nz = ax * by - ay * bx;
        if (nx * p0[0] + ny * p0[1] + nz * p0[2] < 0.0) {
            WorldSky.tv(vc, p0[0], p0[1], p0[2], u0, v1, col);
            WorldSky.tv(vc, p1[0], p1[1], p1[2], u1, v1, col);
            WorldSky.tv(vc, p2[0], p2[1], p2[2], u1, v0, col);
            WorldSky.tv(vc, p3[0], p3[1], p3[2], u0, v0, col);
        } else {
            WorldSky.tv(vc, p3[0], p3[1], p3[2], u0, v0, col);
            WorldSky.tv(vc, p2[0], p2[1], p2[2], u1, v0, col);
            WorldSky.tv(vc, p1[0], p1[1], p1[2], u1, v1, col);
            WorldSky.tv(vc, p0[0], p0[1], p0[2], u0, v1, col);
        }
    }

    private static void sprite(class_4588 vc, double cx, double cy, double cz, double size, int k, int rgb, float a) {
        if (a <= 0.02f) {
            return;
        }
        double s = size / 2.0;
        int col = Gfx.argbRaw(rgb, a);
        float u0 = (float)k * 0.25f;
        float u1 = u0 + 0.25f;
        WorldSky.tquad(vc, WorldSky.p(cx - rx * s - ux * s, cy - ry * s - uy * s, cz - rz * s - uz * s), WorldSky.p(cx + rx * s - ux * s, cy + ry * s - uy * s, cz + rz * s - uz * s), WorldSky.p(cx + rx * s + ux * s, cy + ry * s + uy * s, cz + rz * s + uz * s), WorldSky.p(cx - rx * s + ux * s, cy - ry * s + uy * s, cz - rz * s + uz * s), u0, 0.0f, u1, 1.0f, col);
    }

    private static void spriteStreak(class_4588 vc, double ax, double ay, double az, double bx, double by, double bz, double w, int rgb, float a) {
        if (a <= 0.02f) {
            return;
        }
        double dy = by - ay;
        double vz = (az + bz) / 2.0;
        double dz = bz - az;
        double vy = (ay + by) / 2.0;
        double sx = dy * vz - dz * vy;
        double vx = (ax + bx) / 2.0;
        double dx = bx - ax;
        double sy = dz * vx - dx * vz;
        double sz = dx * vy - dy * vx;
        double l = Math.sqrt(sx * sx + sy * sy + sz * sz);
        if (l < 1.0E-6) {
            return;
        }
        double k = w / 2.0 / l;
        int col = Gfx.argbRaw(rgb, a);
        WorldSky.tquad(vc, WorldSky.p(bx - (sx *= k), by - (sy *= k), bz - (sz *= k)), WorldSky.p(bx + sx, by + sy, bz + sz), WorldSky.p(ax + sx, ay + sy, az + sz), WorldSky.p(ax - sx, ay - sy, az - sz), 0.75f, 0.0f, 1.0f, 1.0f, col);
    }

    private static void petal(class_4588 vc, double cx, double cy, double cz, double size, double kx, double ky, double kz, double ang, int rgb, float a) {
        if (a <= 0.02f) {
            return;
        }
        double c = Math.cos(ang);
        double s = Math.sin(ang);
        double oc = 1.0 - c;
        double e1x = c + kx * kx * oc;
        double e1y = ky * kx * oc + kz * s;
        double e1z = kz * kx * oc - ky * s;
        double e2x = kx * kz * oc + ky * s;
        double e2y = ky * kz * oc - kx * s;
        double e2z = c + kz * kz * oc;
        double h = size / 2.0;
        int col = Gfx.argbRaw(rgb, a);
        WorldSky.tquad(vc, WorldSky.p(cx - e1x * h - e2x * h, cy - e1y * h - e2y * h, cz - e1z * h - e2z * h), WorldSky.p(cx + e1x * h - e2x * h, cy + e1y * h - e2y * h, cz + e1z * h - e2z * h), WorldSky.p(cx + e1x * h + e2x * h, cy + e1y * h + e2y * h, cz + e1z * h + e2z * h), WorldSky.p(cx - e1x * h + e2x * h, cy - e1y * h + e2y * h, cz - e1z * h + e2z * h), 0.0f, 0.0f, 0.25f, 1.0f, col);
    }

    private static void texDome(class_4588 vc, float alpha, float uOff, float wave, double t) {
        WorldSky.texDome(vc, alpha, 1.0f, texDist, uOff, wave, t);
    }

    private static void texDome(class_4588 vc, float bright, float opacity, double d, float uOff, float wave, double t) {
        if (bright <= 0.01f || opacity <= 0.01f) {
            return;
        }
        int gray = Math.round(Math.min(1.0f, bright) * 255.0f);
        int col = Math.round(Math.min(1.0f, opacity) * 255.0f) << 24 | gray << 16 | gray << 8 | gray;
        int step = WorldSky.q() == 0 ? 12 : 8;
        for (int az = 0; az < 360; az += step) {
            double am = Math.toRadians((double)az + (double)step / 2.0);
            if (-Math.sin(am) * fx + Math.cos(am) * fz < -0.55 && fy > -0.6 && fy < 0.6) continue;
            for (int i = 0; i < TEX_EL.length - 1; ++i) {
                WorldSky.texQuad(vc, az, az + step, TEX_EL[i], TEX_EL[i + 1], d, uOff, wave, t, col);
            }
        }
    }

    private static void texQuad(class_4588 vc, double az0, double az1, double el0, double el1, double d, float uOff, float wave, double t, int col) {
        double u1;
        double u0 = az0 / 360.0 + (double)uOff;
        if ((u1 = (u0 -= Math.floor(u0)) + (az1 - az0) / 360.0) > 1.0000001) {
            double split = az0 + (1.0 - u0) * 360.0;
            WorldSky.texPart(vc, az0, split, u0, 1.0, el0, el1, d, wave, t, col);
            WorldSky.texPart(vc, split, az1, 0.0, u1 - 1.0, el0, el1, d, wave, t, col);
        } else {
            WorldSky.texPart(vc, az0, az1, u0, u1, el0, el1, d, wave, t, col);
        }
    }

    private static void texPart(class_4588 vc, double az0, double az1, double u0, double u1, double el0, double el1, double d, float wave, double t, int col) {
        float v0 = (float)((90.0 - el1) / 98.0);
        float v1 = (float)((90.0 - el0) / 98.0);
        double w00 = WorldSky.waveAt(az0, el0, t) * (double)wave;
        double w10 = WorldSky.waveAt(az1, el0, t) * (double)wave;
        double w11 = WorldSky.waveAt(az1, el1, t) * (double)wave;
        double w01 = WorldSky.waveAt(az0, el1, t) * (double)wave;
        double[] a0 = WorldSky.at(az0, el0 + w00, d);
        double ax = a0[0];
        double ay = a0[1];
        double az = a0[2];
        double[] b0 = WorldSky.at(az1, el0 + w10, d);
        double bx = b0[0];
        double by = b0[1];
        double bz = b0[2];
        double[] c1 = WorldSky.at(az1, el1 + w11, d);
        double cx = c1[0];
        double cy = c1[1];
        double cz = c1[2];
        double[] d1 = WorldSky.at(az0, el1 + w01, d);
        WorldSky.tquad(vc, WorldSky.p(ax, ay, az), WorldSky.p(bx, by, bz), WorldSky.p(cx, cy, cz), d1, (float)u0, v0, (float)u1, v1, col);
    }

    private static double waveAt(double azDeg, double el, double t) {
        double a = Math.toRadians(azDeg);
        double k = Math.max(0.0, Math.min(1.0, (el - 2.0) / 10.0)) * Math.max(0.0, Math.min(1.0, (80.0 - el) / 20.0));
        return (Math.sin(a * 5.0 + t * 0.55) * 0.6 + Math.sin(a * 11.0 - t * 0.8) * 0.4) * k;
    }

    private static double wrap(double v, double half) {
        double size = half * 2.0;
        if ((v = (v + half) % size) < 0.0) {
            v += size;
        }
        return v - half;
    }

    private static int mIdx(int ai, int ei) {
        return ei * meshNA + Math.floorMod(ai, meshNA);
    }

    private static int samplePano(int[] pix, double az, double el) {
        int w = 2048;
        int h = 512;
        double u = az / 360.0 * (double)w - 0.5;
        double v = (90.0 - el) / 98.0 * (double)h - 0.5;
        u -= Math.floor(u / (double)w) * (double)w;
        if (v < 0.0) {
            v = 0.0;
        }
        if (v > (double)(h - 1)) {
            v = h - 1;
        }
        int x0 = (int)u;
        int y0 = (int)v;
        int x1 = (x0 + 1) % w;
        int y1 = Math.min(h - 1, y0 + 1);
        float tx = (float)(u - (double)x0);
        float ty = (float)(v - (double)y0);
        int a = pix[y0 * w + x0];
        int b = pix[y0 * w + x1];
        int c = pix[y1 * w + x0];
        int d = pix[y1 * w + x1];
        int r = Math.round(WorldSky.bil(a >> 16 & 0xFF, b >> 16 & 0xFF, c >> 16 & 0xFF, d >> 16 & 0xFF, tx, ty));
        int g = Math.round(WorldSky.bil(a >> 8 & 0xFF, b >> 8 & 0xFF, c >> 8 & 0xFF, d >> 8 & 0xFF, tx, ty));
        int bl = Math.round(WorldSky.bil(a & 0xFF, b & 0xFF, c & 0xFF, d & 0xFF, tx, ty));
        return r << 16 | g << 8 | bl;
    }

    private static float bil(int a, int b, int c, int d, float tx, float ty) {
        float top = (float)a + (float)(b - a) * tx;
        float bot = (float)c + (float)(d - c) * tx;
        return top + (bot - top) * ty;
    }

    private static float lum(int c) {
        return ((float)(c >> 16 & 0xFF) * 0.3f + (float)(c >> 8 & 0xFF) * 0.55f + (float)(c & 0xFF) * 0.15f) / 255.0f;
    }

    private static void buildMesh(double step) {
        int ei;
        int[] base = SkyTextures.base();
        meshStep = step;
        meshVer = SkyTextures.version();
        meshNA = (int)Math.round(360.0 / step);
        meshNE = (int)Math.ceil(94.0 / step);
        int n = meshNA * (meshNE + 1);
        mX = new float[n];
        mY = new float[n];
        mZ = new float[n];
        mAz = new float[n];
        mEl = new float[n];
        mBase = new int[n];
        mFrame = new int[n];
        mStamp = new int[n];
        for (ei = 0; ei <= meshNE; ++ei) {
            float el = -4.0f + (float)ei * 94.0f / (float)meshNE;
            double e = Math.toRadians(el);
            double ce = Math.cos(e);
            for (int ai = 0; ai < meshNA; ++ai) {
                float az = (float)ai * 360.0f / (float)meshNA;
                double a = Math.toRadians(az);
                int i = ei * meshNA + ai;
                WorldSky.mX[i] = (float)(-Math.sin(a) * ce);
                WorldSky.mY[i] = (float)Math.sin(e);
                WorldSky.mZ[i] = (float)(Math.cos(a) * ce);
                WorldSky.mAz[i] = az;
                WorldSky.mEl[i] = el;
                WorldSky.mBase[i] = WorldSky.samplePano(base, az, el);
            }
        }
        mDetail = new byte[meshNA * meshNE];
        for (ei = 0; ei < meshNE; ++ei) {
            for (int ai = 0; ai < meshNA; ++ai) {
                int i00 = WorldSky.mIdx(ai, ei);
                float lo = 9.0f;
                float hi = -9.0f;
                float az0 = mAz[i00];
                float el0 = mEl[i00];
                for (int sy = 0; sy <= 1; ++sy) {
                    for (int sx = 0; sx <= 1; ++sx) {
                        float l = WorldSky.lum(WorldSky.samplePano(base, (double)az0 + ((double)sx + 0.5) * step / 2.0, (double)el0 + ((double)sy + 0.5) * step / 2.0));
                        lo = Math.min(lo, l);
                        hi = Math.max(hi, l);
                    }
                }
                WorldSky.mDetail[ei * WorldSky.meshNA + ai] = (byte)(hi - lo > 0.1f ? 1 : 0);
            }
        }
    }

    private static float horizonA(float el) {
        float t = Anim.clamp((el + 3.0f) / 6.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    private static int meshColor(int baseRgb, double az, double el, float night, int[] fxp, float fxA, double drift) {
        int r = baseRgb >> 16 & 0xFF;
        int g = baseRgb >> 8 & 0xFF;
        int b = baseRgb & 0xFF;
        if (night < 0.999f) {
            r = Math.round((float)r * night);
            g = Math.round((float)g * night);
            b = Math.round((float)b * night);
        }
        if (fxp != null && fxA > 0.01f) {
            int f = WorldSky.samplePano(fxp, az - drift, el);
            r = Math.min(255, r + Math.round((float)(f >> 16 & 0xFF) * fxA));
            g = Math.min(255, g + Math.round((float)(f >> 8 & 0xFF) * fxA));
            b = Math.min(255, b + Math.round((float)(f & 0xFF) * fxA));
        }
        return r << 16 | g << 8 | b;
    }

    private static int vertColor(int i, float night, int[] fxp, float fxA, double drift) {
        int c;
        if (mStamp[i] == frameNo) {
            return mFrame[i];
        }
        WorldSky.mFrame[i] = c = WorldSky.meshColor(mBase[i], mAz[i], mEl[i], night, fxp, fxA, drift);
        WorldSky.mStamp[i] = frameNo;
        return c;
    }

    private static void cquad(double[] p0, double[] p1, double[] p2, double[] p3, int c0, float a0, int c1, float a1, int c2, float a2, int c3, float a3) {
        double ay = p1[1] - p0[1];
        double bz = p2[2] - p0[2];
        double az = p1[2] - p0[2];
        double by = p2[1] - p0[1];
        double nx = ay * bz - az * by;
        double bx = p2[0] - p0[0];
        double ax = p1[0] - p0[0];
        double ny = az * bx - ax * bz;
        double nz = ax * by - ay * bx;
        if (nx * p0[0] + ny * p0[1] + nz * p0[2] < 0.0) {
            WorldSky.v(p0[0], p0[1], p0[2], c0, a0);
            WorldSky.v(p1[0], p1[1], p1[2], c1, a1);
            WorldSky.v(p2[0], p2[1], p2[2], c2, a2);
            WorldSky.v(p3[0], p3[1], p3[2], c3, a3);
        } else {
            WorldSky.v(p3[0], p3[1], p3[2], c3, a3);
            WorldSky.v(p2[0], p2[1], p2[2], c2, a2);
            WorldSky.v(p1[0], p1[1], p1[2], c1, a1);
            WorldSky.v(p0[0], p0[1], p0[2], c0, a0);
        }
    }

    private static void drawSkyMesh(float alpha, float night, float fxA, double drift, double d, class_310 mc) {
        if (alpha <= 0.01f) {
            return;
        }
        double step = WorldSky.q() == 0 || !AutoPerf.allow(3) ? 2.0 : (WorldSky.q() == 1 ? 1.25 : 1.0);
        if (meshVer != SkyTextures.version() || step != meshStep || mX == null) {
            WorldSky.buildMesh(step);
        }
        int[] fxp = (int[])(fxA > 0.01f ? SkyTextures.fx() : null);
        int[] basePix = SkyTextures.base();
        ++frameNo;
        double fov = 70.0;
        try {
            fov = ((Integer)mc.field_1690.method_41808().method_41753()).intValue();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        double aspect = (double)mc.method_22683().method_4489() / (double)Math.max(1, mc.method_22683().method_4506());
        double halfDiag = Math.toDegrees(Math.atan(Math.tan(Math.toRadians(fov / 2.0)) * Math.sqrt(1.0 + aspect * aspect)));
        double lim = Math.toRadians(Math.min(95.0, halfDiag + 4.0 + step));
        double cosLim = Math.cos(lim);
        boolean fine = WorldSky.q() > 0 && AutoPerf.allow(2);
        float fd = (float)d;
        int budget = WorldSky.q() >= 2 ? 1800 : 900;
        for (int ei = 0; ei < meshNE; ++ei) {
            for (int ai = 0; ai < meshNA; ++ai) {
                double cz;
                double cy;
                int i01;
                int i11;
                int i10;
                int i00 = WorldSky.mIdx(ai, ei);
                double cx = mX[i00] + mX[i10 = WorldSky.mIdx(ai + 1, ei)] + mX[i11 = WorldSky.mIdx(ai + 1, ei + 1)] + mX[i01 = WorldSky.mIdx(ai, ei + 1)];
                double cl = Math.sqrt(cx * cx + (cy = (double)(mY[i00] + mY[i10] + mY[i11] + mY[i01])) * cy + (cz = (double)(mZ[i00] + mZ[i10] + mZ[i11] + mZ[i01])) * cz);
                if (cl < 1.0E-6 || (cx * fx + cy * fy + cz * fz) / cl < cosLim) continue;
                float a0 = alpha * WorldSky.horizonA(mEl[i00]);
                float a1 = alpha * WorldSky.horizonA(mEl[i01]);
                if (a0 <= 0.003f && a1 <= 0.003f) continue;
                if (fine && budget > 0 && mDetail[ei * meshNA + Math.floorMod(ai, meshNA)] != 0) {
                    --budget;
                    float az0 = mAz[i00];
                    float el0 = mEl[i00];
                    float st = (float)(meshStep / 2.0);
                    for (int sy = 0; sy < 2; ++sy) {
                        for (int sx = 0; sx < 2; ++sx) {
                            double azA = az0 + (float)sx * st;
                            double azB = azA + (double)st;
                            double elA = el0 + (float)sy * st;
                            double elB = elA + (double)st;
                            int q00 = WorldSky.meshColor(WorldSky.samplePano(basePix, azA, elA), azA, elA, night, fxp, fxA, drift);
                            int q10 = WorldSky.meshColor(WorldSky.samplePano(basePix, azB, elA), azB, elA, night, fxp, fxA, drift);
                            int q11 = WorldSky.meshColor(WorldSky.samplePano(basePix, azB, elB), azB, elB, night, fxp, fxA, drift);
                            int q01 = WorldSky.meshColor(WorldSky.samplePano(basePix, azA, elB), azA, elB, night, fxp, fxA, drift);
                            float hA = alpha * WorldSky.horizonA((float)elA);
                            float hB = alpha * WorldSky.horizonA((float)elB);
                            double[] p0 = WorldSky.at(azA, elA, fd);
                            double x0 = p0[0];
                            double y0 = p0[1];
                            double z0 = p0[2];
                            double[] p1 = WorldSky.at(azB, elA, fd);
                            double x1 = p1[0];
                            double y1 = p1[1];
                            double z1 = p1[2];
                            double[] p2 = WorldSky.at(azB, elB, fd);
                            double x2 = p2[0];
                            double y2 = p2[1];
                            double z2 = p2[2];
                            double[] p3 = WorldSky.at(azA, elB, fd);
                            WorldSky.cquad(WorldSky.p(x0, y0, z0), WorldSky.p(x1, y1, z1), WorldSky.p(x2, y2, z2), p3, q00, hA, q10, hA, q11, hB, q01, hB);
                        }
                    }
                    continue;
                }
                int c00 = WorldSky.vertColor(i00, night, fxp, fxA, drift);
                int c10 = WorldSky.vertColor(i10, night, fxp, fxA, drift);
                int c11 = WorldSky.vertColor(i11, night, fxp, fxA, drift);
                int c01 = WorldSky.vertColor(i01, night, fxp, fxA, drift);
                WorldSky.cquad(WorldSky.p((double)mX[i00] * d, (double)mY[i00] * d, (double)mZ[i00] * d), WorldSky.p((double)mX[i10] * d, (double)mY[i10] * d, (double)mZ[i10] * d), WorldSky.p((double)mX[i11] * d, (double)mY[i11] * d, (double)mZ[i11] * d), WorldSky.p((double)mX[i01] * d, (double)mY[i01] * d, (double)mZ[i01] * d), c00, a0, c10, a0, c11, a1, c01, a1);
            }
        }
    }

    private static float liveFade(SkyPreset p) {
        if (p != lastLive) {
            lastLive = p;
            liveSince = System.currentTimeMillis();
        }
        return Anim.outCubic(Anim.since(liveSince, 900L));
    }

    private static void buildLive(double step) {
        liveStep = step;
        liveNA = (int)Math.round(360.0 / step);
        liveNE = (int)Math.ceil(94.0 / step);
        int n = liveNA * (liveNE + 1);
        lX = new float[n];
        lY = new float[n];
        lZ = new float[n];
        lEl = new float[n];
        lCol = new int[n];
        lStamp = new int[n];
        for (int ei = 0; ei <= liveNE; ++ei) {
            float el = -4.0f + (float)ei * 94.0f / (float)liveNE;
            double e = Math.toRadians(el);
            double ce = Math.cos(e);
            for (int ai = 0; ai < liveNA; ++ai) {
                double a = Math.toRadians((double)ai * 360.0 / (double)liveNA);
                int i = ei * liveNA + ai;
                WorldSky.lX[i] = (float)(-Math.sin(a) * ce);
                WorldSky.lY[i] = (float)Math.sin(e);
                WorldSky.lZ[i] = (float)(Math.cos(a) * ce);
                WorldSky.lEl[i] = el;
            }
        }
    }

    private static int liveCol(int i, float bright) {
        if (lStamp[i] == frameNo) {
            return lCol[i];
        }
        int c = LiveSky.MAIN.color(lX[i], lY[i], lZ[i], lEl[i]);
        if (bright < 0.999f || bright > 1.001f) {
            int r = Math.min(255, Math.round((float)(c >> 16 & 0xFF) * bright));
            int g = Math.min(255, Math.round((float)(c >> 8 & 0xFF) * bright));
            int b = Math.min(255, Math.round((float)(c & 0xFF) * bright));
            c = r << 16 | g << 8 | b;
        }
        WorldSky.lCol[i] = c;
        WorldSky.lStamp[i] = frameNo;
        return c;
    }

    private static void drawLiveSky(float alpha, float bright, class_310 mc) {
        if (alpha <= 0.01f) {
            return;
        }
        double step = WorldSky.q() == 0 || !AutoPerf.allow(3) ? 3.0 : (WorldSky.q() == 1 ? 2.0 : 1.5);
        if (lX == null || step != liveStep) {
            WorldSky.buildLive(step);
        }
        ++frameNo;
        double fov = 70.0;
        try {
            fov = ((Integer)mc.field_1690.method_41808().method_41753()).intValue();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        double aspect = (double)mc.method_22683().method_4489() / (double)Math.max(1, mc.method_22683().method_4506());
        double halfDiag = Math.toDegrees(Math.atan(Math.tan(Math.toRadians(fov / 2.0)) * Math.sqrt(1.0 + aspect * aspect)));
        double cosLim = Math.cos(Math.toRadians(Math.min(95.0, halfDiag + 4.0 + step)));
        double d2 = skyD;
        int budget = WorldSky.q() >= 2 ? 14000 : (WorldSky.q() == 1 ? 7000 : 3000);
        if (!AutoPerf.allow(5)) {
            budget = 0;
        }
        for (int ei = 0; ei < liveNE; ++ei) {
            float a0 = alpha * WorldSky.horizonA(lEl[ei * liveNA]);
            float a1 = alpha * WorldSky.horizonA(lEl[(ei + 1) * liveNA]);
            if (a0 <= 0.003f && a1 <= 0.003f) continue;
            for (int ai = 0; ai < liveNA; ++ai) {
                int i00 = ei * liveNA + ai;
                int i10 = ei * liveNA + (ai + 1) % liveNA;
                int i11 = i10 + liveNA;
                int i01 = i00 + liveNA;
                double cx = lX[i00] + lX[i10] + lX[i11] + lX[i01];
                double cy = lY[i00] + lY[i10] + lY[i11] + lY[i01];
                double cz = lZ[i00] + lZ[i10] + lZ[i11] + lZ[i01];
                double cl = Math.sqrt(cx * cx + cy * cy + cz * cz);
                if (cl < 1.0E-6 || (cx * fx + cy * fy + cz * fz) / cl < cosLim) continue;
                int c00 = WorldSky.liveCol(i00, bright);
                int c10 = WorldSky.liveCol(i10, bright);
                int c11 = WorldSky.liveCol(i11, bright);
                int c01 = WorldSky.liveCol(i01, bright);
                if (budget > 0) {
                    float lo = Math.min(Math.min(WorldSky.lum(c00), WorldSky.lum(c10)), Math.min(WorldSky.lum(c01), WorldSky.lum(c11)));
                    float hi = Math.max(Math.max(WorldSky.lum(c00), WorldSky.lum(c10)), Math.max(WorldSky.lum(c01), WorldSky.lum(c11)));
                    int n2 = hi - lo > 0.3f ? 3 : (hi - lo > 0.1f ? 2 : 1);
                    if (n2 > 1) {
                        budget -= (n2 + 1) * (n2 + 1) - 4;
                        WorldSky.liveSub(ai, ei, n2, c00, c10, c11, c01, alpha, bright, d2);
                        continue;
                    }
                }
                WorldSky.cquad(WorldSky.p((double)lX[i00] * d2, (double)lY[i00] * d2, (double)lZ[i00] * d2), WorldSky.p((double)lX[i10] * d2, (double)lY[i10] * d2, (double)lZ[i10] * d2), WorldSky.p((double)lX[i11] * d2, (double)lY[i11] * d2, (double)lZ[i11] * d2), WorldSky.p((double)lX[i01] * d2, (double)lY[i01] * d2, (double)lZ[i01] * d2), c00, a0, c10, a0, c11, a1, c01, a1);
            }
        }
    }

    public static boolean tintFog(Vector4f v) {
        if (!NeonLyrics.cfg().customSkyboxEnabled || fogMix <= 0.01f) {
            return false;
        }
        v.x += (fogR - v.x) * fogMix;
        v.y += (fogG - v.y) * fogMix;
        v.z += (fogB - v.z) * fogMix;
        return true;
    }

    private static void updateFog(float mix, float bright) {
        float r = 0.0f;
        float g = 0.0f;
        float b = 0.0f;
        for (int i = -2; i <= 2; ++i) {
            double[] q = WorldSky.dir(camYaw + (float)(i * 35), 1.5);
            int c = LiveSky.MAIN.color(q[0], q[1], q[2], 1.5f);
            r += (float)(c >> 16 & 0xFF);
            g += (float)(c >> 8 & 0xFF);
            b += (float)(c & 0xFF);
        }
        fogR = Math.min(1.0f, r / 5.0f / 255.0f * bright);
        fogG = Math.min(1.0f, g / 5.0f / 255.0f * bright);
        fogB = Math.min(1.0f, b / 5.0f / 255.0f * bright);
        fogMix = Math.max(0.0f, Math.min(1.0f, mix));
    }

    private static boolean drawLiveShader(SkyPreset p, float alpha, float bright, double t, Config c, class_4597 buffers, class_310 mc) {
        class_4597.class_4598 bs;
        block11: {
            block10: {
                if (!(buffers instanceof class_4597.class_4598)) break block10;
                bs = (class_4597.class_4598)buffers;
                if (LiveSkyShader.ready()) break block11;
            }
            return false;
        }
        if (alpha <= 0.01f) {
            return true;
        }
        try {
            bs.method_22994(class_12249.method_76023());
            LiveSkyShader.params(p, WorldSky.aurora1(p), WorldSky.aurora2(p), t, c, base);
            class_4588 vc = buffers.method_73477(LiveSkyShader.type());
            float st = (float)(t * 2.2);
            int br = Math.max(0, Math.min(255, Math.round(bright / 2.0f * 255.0f)));
            int al = Math.max(0, Math.min(255, Math.round(alpha * 255.0f)));
            double fov = 70.0;
            try {
                fov = ((Integer)mc.field_1690.method_41808().method_41753()).intValue();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            double aspect = (double)mc.method_22683().method_4489() / (double)Math.max(1, mc.method_22683().method_4506());
            double halfDiag = Math.toDegrees(Math.atan(Math.tan(Math.toRadians(fov / 2.0)) * Math.sqrt(1.0 + aspect * aspect)));
            double cosLim = Math.cos(Math.toRadians(Math.min(100.0, halfDiag + 14.0)));
            double d = skyD;
            int na = 36;
            int ne = 20;
            for (int ei = 0; ei < ne; ++ei) {
                double el0 = -30.0 + (double)ei * 120.0 / (double)ne;
                double el1 = -30.0 + (double)(ei + 1) * 120.0 / (double)ne;
                for (int ai = 0; ai < na; ++ai) {
                    double az0 = (double)ai * 360.0 / (double)na;
                    double az1 = (double)(ai + 1) * 360.0 / (double)na;
                    double[] m = WorldSky.dir((az0 + az1) / 2.0, (el0 + el1) / 2.0);
                    if (m[0] * fx + m[1] * fy + m[2] * fz < cosLim) continue;
                    double[] q0 = WorldSky.dir(az0, el0);
                    float x0 = (float)(q0[0] * d);
                    float y0 = (float)(q0[1] * d);
                    float z0 = (float)(q0[2] * d);
                    double[] q1 = WorldSky.dir(az1, el0);
                    float x1 = (float)(q1[0] * d);
                    float y1 = (float)(q1[1] * d);
                    float z1 = (float)(q1[2] * d);
                    double[] q2 = WorldSky.dir(az1, el1);
                    float x2 = (float)(q2[0] * d);
                    float y2 = (float)(q2[1] * d);
                    float z2 = (float)(q2[2] * d);
                    double[] q3 = WorldSky.dir(az0, el1);
                    float x3 = (float)(q3[0] * d);
                    float y3 = (float)(q3[1] * d);
                    float z3 = (float)(q3[2] * d);
                    vc.method_22918((Matrix4fc)base, x0, y0, z0).method_22913(st, 0.0f).method_1336(br, 0, 0, al);
                    vc.method_22918((Matrix4fc)base, x1, y1, z1).method_22913(st, 0.0f).method_1336(br, 0, 0, al);
                    vc.method_22918((Matrix4fc)base, x2, y2, z2).method_22913(st, 0.0f).method_1336(br, 0, 0, al);
                    vc.method_22918((Matrix4fc)base, x3, y3, z3).method_22913(st, 0.0f).method_1336(br, 0, 0, al);
                }
            }
            bs.method_22994(LiveSkyShader.type());
        }
        catch (Throwable e) {
            LiveSkyShader.disable(e);
            return false;
        }
        return true;
    }

    private static void liveSub(int ai, int ei, int n, int c00, int c10, int c11, int c01, float alpha, float bright, double d) {
        int j;
        double az0 = (double)ai * 360.0 / (double)liveNA;
        double az1 = (double)(ai + 1) * 360.0 / (double)liveNA;
        float el0 = lEl[ei * liveNA];
        float el1 = lEl[(ei + 1) * liveNA];
        for (j = 0; j <= n; ++j) {
            float el = el0 + (el1 - el0) * (float)j / (float)n;
            double e = Math.toRadians(el);
            double ce = Math.cos(e);
            double se = Math.sin(e);
            for (int i = 0; i <= n; ++i) {
                int c;
                int k = j * (n + 1) + i;
                double a = Math.toRadians(az0 + (az1 - az0) * (double)i / (double)n);
                float x = (float)(-Math.sin(a) * ce);
                float y = (float)se;
                float z = (float)(Math.cos(a) * ce);
                WorldSky.SUB_P[k][0] = x;
                WorldSky.SUB_P[k][1] = y;
                WorldSky.SUB_P[k][2] = z;
                if (i == 0 && j == 0) {
                    c = c00;
                } else if (i == n && j == 0) {
                    c = c10;
                } else if (i == n && j == n) {
                    c = c11;
                } else if (i == 0 && j == n) {
                    c = c01;
                } else {
                    c = LiveSky.MAIN.color(x, y, z, el);
                    if (bright < 0.999f || bright > 1.001f) {
                        int r = Math.min(255, Math.round((float)(c >> 16 & 0xFF) * bright));
                        int g = Math.min(255, Math.round((float)(c >> 8 & 0xFF) * bright));
                        int b = Math.min(255, Math.round((float)(c & 0xFF) * bright));
                        c = r << 16 | g << 8 | b;
                    }
                }
                WorldSky.SUB_COL[k] = c;
            }
        }
        for (j = 0; j < n; ++j) {
            float aA = alpha * WorldSky.horizonA(el0 + (el1 - el0) * (float)j / (float)n);
            float aB = alpha * WorldSky.horizonA(el0 + (el1 - el0) * (float)(j + 1) / (float)n);
            for (int i = 0; i < n; ++i) {
                int k00 = j * (n + 1) + i;
                int k10 = k00 + 1;
                int k01 = k00 + n + 1;
                int k11 = k01 + 1;
                WorldSky.cquad(WorldSky.p((double)SUB_P[k00][0] * d, (double)SUB_P[k00][1] * d, (double)SUB_P[k00][2] * d), WorldSky.p((double)SUB_P[k10][0] * d, (double)SUB_P[k10][1] * d, (double)SUB_P[k10][2] * d), WorldSky.p((double)SUB_P[k11][0] * d, (double)SUB_P[k11][1] * d, (double)SUB_P[k11][2] * d), WorldSky.p((double)SUB_P[k01][0] * d, (double)SUB_P[k01][1] * d, (double)SUB_P[k01][2] * d), SUB_COL[k00], aA, SUB_COL[k10], aA, SUB_COL[k11], aB, SUB_COL[k01], aB);
            }
        }
    }

    private static void drawBody(SkyPreset p, float vis, double t) {
        double cz;
        double cy;
        LiveSky.Body b = LiveSky.body(p);
        if (b == null || vis <= 0.01f) {
            return;
        }
        double d = skyD * 0.995;
        double a = Math.toRadians(b.az());
        double e = Math.toRadians(b.el());
        double cx = -Math.sin(a) * Math.cos(e);
        if (cx * fx + (cy = Math.sin(e)) * fy + (cz = Math.cos(a) * Math.cos(e)) * fz < 0.2) {
            return;
        }
        double e1x = Math.cos(a);
        double e1y = 0.0;
        double e1z = Math.sin(a);
        double e2x = cy * e1z - cz * e1y;
        double e2y = cz * e1x - cx * e1z;
        double e2z = cx * e1y - cy * e1x;
        double[] B = new double[]{cx, cy, cz, e1x, e1y, e1z, e2x, e2y, e2z};
        double r = Math.toRadians(b.radius());
        int col = b.color();
        if (b.kind() == 0) {
            WorldSky.bodyRing(B, d, 0.0, r * 0.8, 0xFFFFFF, vis, Gfx.lerpColor(col, 0xFFFFFF, 0.6f), vis, 40, 0.0, t);
            WorldSky.bodyRing(B, d, r * 0.8, r, Gfx.lerpColor(col, 0xFFFFFF, 0.6f), vis, col, vis, 40, 0.0, t);
            WorldSky.bodyRing(B, d, r, r * 1.25, col, vis, col, 0.0f, 40, 0.0, t);
            WorldSky.bodyRing(B, d, r * 1.25, r * 3.2, col, vis * 0.3f, col, 0.0f, 40, 0.0, t);
        } else if (b.kind() == 1) {
            double[][] spots;
            int lit = Gfx.lerpColor(col, 0xFFFFFF, 0.35f);
            int limb = Gfx.lerpColor(col, 0, 0.18f);
            WorldSky.bodyRing(B, d, 0.0, r * 0.7, lit, vis, col, vis, 40, 0.0, t);
            WorldSky.bodyRing(B, d, r * 0.7, r, col, vis, limb, vis, 40, 0.0, t);
            WorldSky.bodyRing(B, d, r, r * 1.08, limb, vis, limb, 0.0f, 40, 0.0, t);
            WorldSky.bodyRing(B, d, r * 1.08, r * 3.4, col, vis * 0.16f, col, 0.0f, 40, 0.0, t);
            int mare = Gfx.lerpColor(col, 0, 0.22f);
            for (double[] sp : spots = new double[][]{{-0.3, 0.25, 0.28}, {0.28, 0.05, 0.22}, {0.05, -0.35, 0.18}}) {
                double ox = sp[0] * Math.sin(r);
                double oy = sp[1] * Math.sin(r);
                double[] C = new double[]{cx * Math.cos(r * 0.3) + e1x * ox + e2x * oy, cy * Math.cos(r * 0.3) + e1y * ox + e2y * oy, cz * Math.cos(r * 0.3) + e1z * ox + e2z * oy, e1x, e1y, e1z, e2x, e2y, e2z};
                WorldSky.bodyRing(C, d * 0.999, 0.0, r * sp[2], mare, vis * 0.35f, mare, 0.0f, 20, 0.0, t);
            }
        } else if (b.kind() == 3) {
            int a1 = WorldSky.aurora1(p);
            WorldSky.bodyRing(B, d, r, r * 1.6, a1, vis * 0.45f, a1, 0.0f, 48, 0.0, t);
            int n = 64;
            for (int i = 0; i < n; ++i) {
                double v0 = -r + 2.0 * r * (double)i / (double)n;
                double v1 = v0 + 2.0 * r / (double)n;
                double vm = (v0 + v1) / 2.0 / r;
                if (LiveSky.synthGap(vm, t)) continue;
                double h0 = Math.sqrt(Math.max(0.0, r * r - v0 * v0));
                double h1 = Math.sqrt(Math.max(0.0, r * r - v1 * v1));
                int c0 = LiveSky.synthColor(v0 / r);
                int c1 = LiveSky.synthColor(v1 / r);
                WorldSky.quad(WorldSky.disc(B, -h0, v0, d), WorldSky.disc(B, h0, v0, d), WorldSky.disc(B, h1, v1, d), WorldSky.disc(B, -h1, v1, d), c0, vis, c0, vis, c1, vis, c1, vis);
            }
        } else {
            int a1 = WorldSky.aurora1(p);
            int a2 = WorldSky.aurora2(p);
            WorldSky.bodyRing(B, d, 0.0, r, 262664, vis, 262664, vis, 48, 0.0, t);
            WorldSky.bodyRing(B, d, r, r * 1.035, 16774364, vis, 16774364, vis, 64, 0.25, t);
            WorldSky.bodyRing(B, d, r * 1.035, r * 1.2, Gfx.lerpColor(a1, 0xFFFFFF, 0.4f), vis * 0.95f, a1, 0.0f, 64, 0.5, t);
            WorldSky.bodyRing(B, d, r * 1.2, r * 2.4, a2, vis * 0.25f, a2, 0.0f, 64, 0.8, t);
        }
    }

    private static double[] disc(double[] B, double u, double v, double d) {
        double x = B[0] + B[3] * u + B[6] * v;
        double y = B[1] + B[4] * u + B[7] * v;
        double z = B[2] + B[5] * u + B[8] * v;
        double l = Math.sqrt(x * x + y * y + z * z);
        return WorldSky.p(x / l * d, y / l * d, z / l * d);
    }

    private static void bodyRing(double[] B, double d, double r0, double r1, int c0, float a0, int c1, float a1, int seg, double flick, double t) {
        double s0 = Math.sin(r0);
        double k0 = Math.cos(r0);
        double s1 = Math.sin(r1);
        double k1 = Math.cos(r1);
        for (int i = 0; i < seg; ++i) {
            double th0 = (double)i * Math.PI * 2.0 / (double)seg;
            double th1 = (double)(i + 1) * Math.PI * 2.0 / (double)seg;
            float f0 = flick > 0.0 ? (float)(1.0 - flick + flick * (0.5 + 0.5 * Math.sin(th0 * 7.0 + t * 0.9) * Math.sin(th0 * 3.0 - t * 0.5))) : 1.0f;
            float f1 = flick > 0.0 ? (float)(1.0 - flick + flick * (0.5 + 0.5 * Math.sin(th1 * 7.0 + t * 0.9) * Math.sin(th1 * 3.0 - t * 0.5))) : 1.0f;
            double c0x = Math.cos(th0);
            double c0y = Math.sin(th0);
            double c1x = Math.cos(th1);
            double c1y = Math.sin(th1);
            WorldSky.quad(WorldSky.p((B[0] * k0 + (B[3] * c0x + B[6] * c0y) * s0) * d, (B[1] * k0 + (B[4] * c0x + B[7] * c0y) * s0) * d, (B[2] * k0 + (B[5] * c0x + B[8] * c0y) * s0) * d), WorldSky.p((B[0] * k0 + (B[3] * c1x + B[6] * c1y) * s0) * d, (B[1] * k0 + (B[4] * c1x + B[7] * c1y) * s0) * d, (B[2] * k0 + (B[5] * c1x + B[8] * c1y) * s0) * d), WorldSky.p((B[0] * k1 + (B[3] * c1x + B[6] * c1y) * s1) * d, (B[1] * k1 + (B[4] * c1x + B[7] * c1y) * s1) * d, (B[2] * k1 + (B[5] * c1x + B[8] * c1y) * s1) * d), WorldSky.p((B[0] * k1 + (B[3] * c0x + B[6] * c0y) * s1) * d, (B[1] * k1 + (B[4] * c0x + B[7] * c0y) * s1) * d, (B[2] * k1 + (B[5] * c0x + B[8] * c0y) * s1) * d), c0, a0 * f0, c0, a0 * f1, c1, a1 * f1, c1, a1 * f0);
        }
    }

    private static void drawPlayerFx(class_310 mc, Config c, class_4597 buffers, class_243 cam, float pt, double t, SkyPreset p) {
        int skyCol;
        class_746 pl = mc.field_1724;
        if (pl == null || pl.method_5767()) {
            return;
        }
        class_243 pos = pl.method_30950(pt);
        long now = System.currentTimeMillis();
        NowPlaying np = SpotifyClient.now();
        float beat = np.playing ? np.beatPulse(np.progressNow(c.lyricsOffsetMs)) : 0.0f;
        int acc = Theme.accent();
        int n = skyCol = p != null ? WorldSky.aurora1(p) : acc;
        if (c.trail > 0 && SkyTextures.spritesReady()) {
            double mvx = pos.field_1352 - lastTx;
            double mvz = pos.field_1350 - lastTz;
            if (now - lastTrail > 35L && mvx * mvx + mvz * mvz > 9.0E-4) {
                lastTrail = now;
                lastTx = pos.field_1352;
                lastTz = pos.field_1350;
                double jx = (Math.random() - 0.5) * 0.35;
                double jz = (Math.random() - 0.5) * 0.35;
                TRAIL.addLast(new double[]{pos.field_1352 + jx, pos.field_1351 + 0.12 + Math.random() * 0.25, pos.field_1350 + jz, now, (double)now / 1400.0 % 1.0});
            }
            while (!TRAIL.isEmpty() && (double)now - TRAIL.peekFirst()[3] > 1300.0) {
                TRAIL.pollFirst();
            }
            if (!TRAIL.isEmpty()) {
                class_4588 ts = buffers.method_73477(class_12249.method_76022((class_2960)SkyTextures.SPRITES));
                for (double[] q : TRAIL) {
                    float age = (float)(((double)now - q[3]) / 1300.0);
                    int col = c.trail == 2 ? Gfx.hsv((float)q[4], 0.65f, 1.0f) : (c.trail == 3 ? skyCol : acc);
                    double rise = (double)age * 0.35;
                    WorldSky.sprite(ts, q[0] - cam.field_1352, q[1] + rise - cam.field_1351, q[2] - cam.field_1350, 0.32 * (1.0 - (double)age * 0.6) * (1.0 + 0.4 * (double)beat), 2, col, (1.0f - age) * 0.9f);
                }
            }
        } else if (!TRAIL.isEmpty()) {
            TRAIL.clear();
        }
        if (c.aura) {
            quads = buffers.method_73477(class_12249.method_76023());
            double fx0 = pos.field_1352 - cam.field_1352;
            double fy0 = pos.field_1351 + 0.03 - cam.field_1351;
            double fz0 = pos.field_1350 - cam.field_1350;
            int seg = 40;
            float pulse = 0.35f + 0.65f * beat;
            double r0 = 0.55 + 0.08 * (double)beat;
            double r1 = r0 + 0.16;
            for (int i = 0; i < seg; ++i) {
                double a0 = (double)i * Math.PI * 2.0 / (double)seg + t * 0.6;
                double a1 = (double)(i + 1) * Math.PI * 2.0 / (double)seg + t * 0.6;
                float k = (float)(0.5 + 0.5 * Math.cos(a0 * 3.0 - t * 2.4));
                float al = (0.18f + 0.45f * k) * pulse;
                int col = c.trail == 2 ? Gfx.hsv((float)(((double)i / (double)seg + t * 0.1) % 1.0), 0.6f, 1.0f) : acc;
                WorldSky.quad(WorldSky.p(fx0 + Math.cos(a0) * r0, fy0, fz0 + Math.sin(a0) * r0), WorldSky.p(fx0 + Math.cos(a1) * r0, fy0, fz0 + Math.sin(a1) * r0), WorldSky.p(fx0 + Math.cos(a1) * r1, fy0, fz0 + Math.sin(a1) * r1), WorldSky.p(fx0 + Math.cos(a0) * r1, fy0, fz0 + Math.sin(a0) * r1), col, al, col, al, col, 0.0f, col, 0.0f);
            }
            quads = null;
        }
    }

    private static double skyEl() {
        double base = -camPitch + 3.0f;
        return NeonLyrics.cfg().lyricsUnderground && !skyOpen ? base : Math.max(-6.0, Math.min(60.0, base));
    }

    private static double degPerPx(class_310 mc, Config c) {
        double guiH = mc.method_22683().method_4502();
        double focalRef = Math.max(1.0, guiH) / 2.0 / Math.tan(Math.toRadians(35.0));
        return Math.toDegrees((double)c.lyricsSize / focalRef);
    }

    private static void drawLyrics(class_310 mc, Config c, class_4597 buffers, int color, float dt) {
        NowPlaying np = SpotifyClient.now();
        long now = System.currentTimeMillis();
        int li = -1;
        long prog = 0L;
        if (np.hasTrack() && np.lyricsState == NowPlaying.LyricsState.SYNCED && np.playing && (li = np.lineIndex(prog = np.progressNow(c.lyricsOffsetMs + 280))) >= 0 && prog > np.lineEnd(li) + 400L) {
            li = -1;
        }
        double dpp = WorldSky.degPerPx(mc, c);
        if (li != activeIdx || !String.valueOf(np.trackId).equals(activeTrack)) {
            if (active != null) {
                WorldSky.active.exitAt = now;
                exiting.add(active);
            }
            active = null;
            activeIdx = li;
            activeTrack = String.valueOf(np.trackId);
            if (li >= 0 && !np.lines.get(li).text().isBlank()) {
                active = new SkyLine(np.lines.get(li), camYaw, WorldSky.skyEl(), dpp);
            }
        }
        exiting.removeIf(s -> now - s.exitAt > 520L);
        while (exiting.size() > 1) {
            exiting.remove(0);
        }
        for (SkyLine s2 : exiting) {
            WorldSky.drawLine(mc, c, buffers, s2, color, -1.0f, now, dt, dpp);
        }
        if (active != null) {
            WorldSky.drawLine(mc, c, buffers, active, color, np.wordPosition(li, prog), now, dt, dpp);
        }
    }

    private static double angDiff(double a, double b) {
        return ((a - b) % 360.0 + 540.0) % 360.0 - 180.0;
    }

    private static void drawLine(class_310 mc, Config c, class_4597 buffers, SkyLine s, int color, float wp, long now, float dt, double dpp) {
        float exit;
        float lineA;
        if (c.lyricsFollow && !c.lyricsViewLock && s.exitAt == 0L) {
            double dEl;
            double dAz = WorldSky.angDiff(camYaw, s.baseAz);
            double kh = 1.0 - Math.exp(-3.0 * (double)dt);
            if (Math.abs(dAz) > 26.0) {
                s.baseAz += (dAz - Math.signum(dAz) * 26.0) * kh;
            }
            double kv = 1.0 - Math.exp(-(Math.abs(dEl = WorldSky.skyEl() - s.baseEl) > 6.0 ? 4.0 : 1.5) * (double)dt);
            s.baseEl += dEl * kv;
        }
        if ((lineA = (1.0f - Anim.outCubic(exit = s.exitAt == 0L ? 0.0f : Anim.clamp((float)(now - s.exitAt) / 480.0f))) * c.lyricsOpacity) <= 0.01f) {
            return;
        }
        String[] words = s.line.words();
        int cur = wp < 0.0f ? words.length : (int)Math.floor(wp);
        class_327 font = mc.field_1772;
        double d = dist * 0.9;
        float k = (float)(Math.toRadians(dpp) * d);
        int space = Gfx.w(" ", Gfx.LYRICS);
        int shadow = Gfx.lerpColor(color, 656916, 0.75f);
        long startDelay = exiting.isEmpty() || s.exitAt != 0L ? 0L : 420L;
        NowPlaying npB = SpotifyClient.now();
        float beat = c.fxBeatPulse ? npB.beatPulse(npB.progressNow(c.lyricsOffsetMs)) : 0.0f;
        boolean through = c.lyricsUnderground && !skyOpen;
        int glowCol = c.lyricsGlowColor < 0 ? color : LYRIC_COLORS[Math.floorMod(c.lyricsGlowColor, LYRIC_COLORS.length)];
        for (Chunk ch : s.chunks) {
            double Nz;
            double Rx;
            double Ux;
            double Ny;
            double Uy;
            double Rz;
            double Uz;
            double sr;
            if (s.exitAt == 0L && ch.from > cur) continue;
            float kk = k * ch.size;
            double az = Math.toRadians(s.baseAz + ch.offAz);
            double el = Math.toRadians(s.baseEl + ch.offEl + (double)(Anim.outCubic(exit) * 3.0f));
            double dx = -Math.sin(az) * Math.cos(el);
            double dy = Math.sin(el);
            double dz = Math.cos(az) * Math.cos(el);
            double r0x = -Math.cos(az);
            double r0z = -Math.sin(az);
            double u0x = -r0z * dy;
            double u0y = r0z * dx - r0x * dz;
            double u0z = r0x * dy;
            double ct = Math.cos(ch.yawTilt);
            double st = Math.sin(ch.yawTilt);
            double rX = r0x * ct + dx * st;
            double rY = dy * st;
            double rZ = r0z * ct + dz * st;
            double cr = Math.cos(ch.roll);
            double Ry = rY * cr + u0y * (sr = Math.sin(ch.roll));
            double Nx = Ry * (Uz = u0z * cr - rZ * sr) - (Rz = rZ * cr + u0z * sr) * (Uy = u0y * cr - rY * sr);
            if (Nx * dx + (Ny = Rz * (Ux = u0x * cr - rX * sr) - (Rx = rX * cr + u0x * sr) * Uz) * dy + (Nz = Rx * Uy - Ry * Ux) * dz > 0.0) {
                Nx = -Nx;
                Ny = -Ny;
                Nz = -Nz;
            }
            Matrix4f m0 = M0.set((Matrix4fc)base).mul((Matrix4fc)TMP.set((float)Rx * kk, (float)Ry * kk, (float)Rz * kk, 0.0f, (float)(-Ux) * kk, (float)(-Uy) * kk, (float)(-Uz) * kk, 0.0f, (float)Nx * kk, (float)Ny * kk, (float)Nz * kk, 0.0f, (float)(dx * d), (float)(dy * d), (float)(dz * d), 1.0f));
            float lx = (float)(-ch.widthPx) / 2.0f;
            for (int i = ch.from; i < ch.to; ++i) {
                boolean glowOn;
                boolean on;
                String word = words[i];
                int ww = Gfx.w(word, Gfx.LYRICS);
                boolean bl = on = s.exitAt != 0L || i <= cur;
                if (on && s.shown[i] == 0L) {
                    s.shown[i] = Math.max(now, s.born + startDelay);
                }
                if (!on) {
                    lx += (float)(ww + space);
                    continue;
                }
                float raw = Anim.since(s.shown[i], 450L);
                float e = Anim.outCubic(raw);
                float a = e * lineA;
                boolean current = s.exitAt == 0L && i == cur;
                Matrix4f m = WM.set((Matrix4fc)m0);
                if (current && beat > 0.01f) {
                    float bs = 1.0f + 0.07f * beat;
                    m.translate(lx + (float)ww / 2.0f, 0.0f, 0.0f).scale(bs, bs, 1.0f).translate(-(lx + (float)ww / 2.0f), 0.0f, 0.0f);
                }
                switch (c.lyricsAnim) {
                    case 1: {
                        float ps = 1.3f - 0.3f * Anim.outBack(raw);
                        m.translate(lx + (float)ww / 2.0f, 0.0f, 0.0f).scale(ps, ps, 1.0f).translate((float)(-ww) / 2.0f, -4.5f, 0.0f);
                        break;
                    }
                    case 2: {
                        m.translate(lx - (1.0f - e) * 10.0f, -4.5f, 0.0f);
                        break;
                    }
                    default: {
                        m.translate(lx, -4.5f + (1.0f - e) * 3.0f, 0.0f);
                    }
                }
                int glowN = Q_GLOW[WorldSky.q()];
                boolean bl2 = glowOn = c.lyricsGlow && AutoPerf.allow(3);
                if (glowOn && glowN > 0) {
                    float gs = c.lyricsGlowStrength * (current ? 1.25f : 1.0f) * (8.0f / (float)glowN) * 0.75f;
                    for (int qi = 0; qi < glowN; ++qi) {
                        double ang = (double)qi * Math.PI * 2.0 / (double)glowN + 0.39269908169872414;
                        float r = 1.0f;
                        WorldSky.word(font, buffers, m, word, (float)Math.cos(ang) * r, (float)Math.sin(ang) * r, 0.04f, glowCol, a * 0.16f * gs, through);
                    }
                } else if (glowOn) {
                    WorldSky.word(font, buffers, m, word, 0.0f, 0.6f, 0.04f, glowCol, a * 0.45f * c.lyricsGlowStrength, through);
                } else {
                    WorldSky.word(font, buffers, m, word, 0.0f, 0.9f, 0.0f, shadow, a * 0.55f, through);
                }
                int col = current ? Gfx.lerpColor(color, 0xFFFFFF, 0.6f) : Gfx.lerpColor(color, 0xFFFFFF, 0.2f);
                WorldSky.word(font, buffers, m, word, 0.0f, 0.0f, 0.12f, col, a, through);
                lx += (float)(ww + space);
            }
        }
    }

    private static class_2561 lyricComp(String w) {
        int f = NeonLyrics.cfg().lyricsFont;
        if (f != compFont || COMP.size() > 400) {
            COMP.clear();
            compFont = f;
        }
        return COMP.computeIfAbsent(w, k -> Gfx.comp(k, Gfx.LYRICS));
    }

    private static void word(class_327 font, class_4597 buffers, Matrix4f m, String w, float x, float y, float z, int rgb, float a, boolean through) {
        if (a <= 0.03f) {
            return;
        }
        Matrix4f mm = SCR.set((Matrix4fc)m).translate(x, y, z);
        font.method_27522(WorldSky.lyricComp(w), 0.0f, 0.0f, Gfx.argbRaw(rgb, a), false, mm, buffers, through ? class_327.class_6415.field_33994 : class_327.class_6415.field_33993, 0, 0xF000F0);
    }

    static {
        shooting = new ArrayList<float[]>();
        nextShooting = 0L;
        T0 = System.currentTimeMillis();
        parts = new double[0][];
        partType = null;
        lastNanos = System.nanoTime();
        visible = new Anim(0.0, 3.0);
        outside = new Anim(0.0, 2.0);
        musicCalm = new Anim(1.0, 1.5);
        skyOpen = true;
        skyTime = 1000.0;
        breathT = 0.0;
        Q_STARS = new float[]{0.4f, 0.65f, 1.0f};
        Q_AURORA_STEP = new double[]{7.0, 4.5, 3.0};
        Q_WEATHER = new float[]{0.3f, 0.55f, 0.85f};
        Q_DOME = new int[]{36, 30, 24};
        Q_MOON = new int[]{10, 14, 20};
        Q_GLOW = new int[]{0, 2, 4};
        RING = new double[16][3];
        ringPos = 0;
        DAY_AL = new float[]{0.0f, 0.55f, 0.9f, 0.97f, 0.985f, 0.99f, 0.99f};
        DOME_EL = new double[]{-6.0, 0.0, 8.0, 20.0, 38.0, 62.0, 90.0};
        DOME_AL = new float[]{0.0f, 0.3f, 0.42f, 0.52f, 0.6f, 0.64f, 0.64f};
        DOME_A = new float[DOME_EL.length];
        DOME_C = new int[DOME_EL.length];
        domeGridStep = -1;
        flashNext = 0.0;
        flashT0 = -10.0;
        bolt = new double[0][0];
        PALETTES = new int[][]{{10719743, 0xF6F2FF}, {16740036, 16769266}, {3073750, 10155775}, {4652976, 8186111}, {16757596, 0xFF6E6E}, {10475775, 0xFFFFFF}, {13716479, 4970751}};
        PALETTE_KEYS = new String[]{"purple", "pink", "ocean", "mint", "sunset", "ice", "neon"};
        CONST = new double[][][]{new double[][]{{30.0, 48.0}, {36.0, 50.0}, {42.0, 49.0}, {47.0, 46.0}, {50.0, 41.0}, {57.0, 42.0}, {56.0, 47.0}}, new double[][]{{160.0, 38.0}, {165.0, 44.0}, {171.0, 40.0}, {177.0, 46.0}, {183.0, 41.0}}, new double[][]{{250.0, 30.0}, {254.0, 36.0}, {258.0, 30.0}, {251.0, 25.0}, {257.0, 24.0}, {254.0, 36.0}, {254.0, 43.0}}, new double[][]{{320.0, 55.0}, {326.0, 58.0}, {331.0, 55.0}, {326.0, 52.0}, {320.0, 55.0}}};
        introTrack = "";
        introStart = 0L;
        introAz = 0.0;
        introEl = 0.0;
        TEX_EL = new double[]{-8.0, -3.0, 2.0, 8.0, 15.0, 24.0, 35.0, 48.0, 62.0, 76.0, 90.0};
        meshVer = -1;
        meshStep = 0.0;
        frameNo = 0;
        liveStep = 0.0;
        SUB_COL = new int[16];
        SUB_P = new float[16][3];
        TRAIL = new ArrayDeque();
        lastTrail = 0L;
        lastSide = 0.0;
        exiting = new ArrayList<SkyLine>();
        activeIdx = -2;
        activeTrack = "";
        M0 = new Matrix4f();
        TMP = new Matrix4f();
        WM = new Matrix4f();
        SCR = new Matrix4f();
        COMP = new HashMap<String, class_2561>();
        compFont = -1;
        LYRIC_COLORS = new int[]{13219327, 0xFFFFFF, 16747224, 9098495, 0x8CFFC8, 16765562, 0xFF7A7A, 11635967};
    }

    private static final class SkyLine {
        final NowPlaying.Line line;
        final long[] shown;
        final List<Chunk> chunks = new ArrayList<Chunk>();
        double baseAz;
        double baseEl;
        final long born = System.currentTimeMillis();
        long exitAt = 0L;

        SkyLine(NowPlaying.Line l, double az, double el, double degPerPx) {
            int rowsN;
            this.line = l;
            this.shown = new long[l.words().length];
            this.baseAz = az;
            this.baseEl = el;
            String[] w = l.words();
            int space = Gfx.w(" ", Gfx.LYRICS);
            int i = 0;
            while (i < w.length) {
                int j;
                int chars = w[i].length();
                for (j = i + 1; j < w.length && j - i < 3 && chars + w[j].length() <= (j - i == 1 ? 12 : 9); ++j) {
                    chars += w[j].length();
                }
                Chunk c = new Chunk();
                c.from = i;
                c.to = j;
                for (int k = i; k < j; ++k) {
                    c.widthPx = c.widthPx + (Gfx.w(w[k], Gfx.LYRICS) + (k > i ? space : 0));
                }
                this.chunks.add(c);
                i = j;
            }
            double gap = (double)space * 2.4 * degPerPx;
            int n = this.chunks.size();
            rowsN = n <= 2 ? 1 : (n >= 6 && RND.nextFloat() < 0.35f ? 3 : 2);
            if (n == 3 && RND.nextFloat() < 0.35f) {
                rowsN = 1;
            }
            ArrayList<ArrayList<Chunk>> rows = new ArrayList<ArrayList<Chunk>>();
            int idx = 0;
            for (int r = 0; r < rowsN; ++r) {
                int left = n - idx;
                int rowsLeft = rowsN - r;
                int take = rowsLeft == 1 ? left : Math.max(1, Math.min(left - (rowsLeft - 1), Math.round((float)left / (float)rowsLeft + (RND.nextFloat() - 0.5f) * 1.2f)));
                rows.add(new ArrayList<Chunk>(this.chunks.subList(idx, idx + take)));
                idx += take;
            }
            int big = RND.nextInt(n);
            for (int k = 0; k < n; ++k) {
                this.chunks.get((int)k).size = (k == big ? 1.22f : 0.86f) + RND.nextFloat() * 0.2f;
            }
            double rowH = 17.0 * degPerPx;
            double side = (RND.nextDouble() * 2.0 - 1.0) * 20.0;
            if (Math.signum(side) == lastSide && RND.nextFloat() < 0.65f) {
                side = -side;
            }
            lastSide = Math.signum(side);
            double lift = 2.5 + RND.nextDouble() * 6.0;
            double stair = (double)(RND.nextBoolean() ? 1 : -1) * (5.0 + RND.nextDouble() * 8.0);
            for (int r = 0; r < rows.size(); ++r) {
                List<Chunk> rr = rows.get(r);
                double total = -gap;
                for (Chunk c : rr) {
                    total += gap + (double)c.widthPx * degPerPx * (double)c.size;
                }
                if (total > 58.0) {
                    float f = (float)(58.0 / total);
                    for (Chunk c : rr) {
                        c.size *= f;
                    }
                    total = 58.0;
                }
                double rowShift = side + (double)r * stair + (RND.nextDouble() - 0.5) * 4.0;
                double maxShift = Math.max(0.0, 46.0 - total / 2.0);
                rowShift = Math.max(-maxShift, Math.min(maxShift, rowShift));
                double y = lift + (double)(rows.size() - 1 - r) * rowH + (RND.nextDouble() - 0.5) * rowH * 0.15;
                double x = rowShift - total / 2.0;
                for (int k = 0; k < rr.size(); ++k) {
                    Chunk c = (Chunk)rr.get(k);
                    double cw = (double)c.widthPx * degPerPx * (double)c.size;
                    c.offAz = x + cw / 2.0;
                    c.offEl = y + (k % 2 == 0 ? 0.8 : -0.6) + (RND.nextDouble() - 0.5) * 0.8;
                    x += cw + gap;
                    c.yawTilt = (float)Math.toRadians((float)(RND.nextFloat() < 0.5f ? -1 : 1) * (5.0f + RND.nextFloat() * 14.0f));
                    c.roll = (float)Math.toRadians((RND.nextFloat() - 0.5f) * 9.0f);
                }
            }
        }
    }

    private static final class Chunk {
        int from;
        int to;
        int widthPx;
        double offAz;
        double offEl;
        float yawTilt;
        float roll;
        float size = 1.0f;

        private Chunk() {
        }
    }
}

