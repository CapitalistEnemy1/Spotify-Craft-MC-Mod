/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_4063
 *  net.minecraft.class_7172
 *  net.minecraft.class_9779
 */
package dev.neonlyrics.sky;

import dev.neonlyrics.License;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.hud.Anim;
import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.hud.Island;
import dev.neonlyrics.sky.SkyPreset;
import dev.neonlyrics.util.Fx;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.class_1937;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4063;
import net.minecraft.class_7172;
import net.minecraft.class_9779;

public final class SkyRenderer {
    private static float sw;
    private static float sh;
    private static float focal;
    private static double rx;
    private static double ry;
    private static double rz;
    private static double ux;
    private static double uy;
    private static double uz;
    private static double fx;
    private static double fy;
    private static double fz;
    private static float camYaw;
    private static float camPitch;
    private static final float[] P;
    private static final Anim visible;
    private static final Random RND;
    private static float[][] stars;
    private static final List<float[]> shooting;
    private static long nextShooting;
    private static double[][] parts;
    private static SkyPreset.Weather partType;
    private static long lastNanos;
    private static long flashAt;
    private static long nextFlash;
    private static boolean forcedRain;
    private static int lastSky;

    private SkyRenderer() {
    }

    private static void setupCamera(class_332 g, class_310 mc, float pt) {
        sw = g.method_51421();
        sh = g.method_51443();
        double fov = Math.toRadians(((Integer)mc.field_1690.method_41808().method_41753()).intValue());
        focal = (float)((double)sh / 2.0 / Math.tan(fov / 2.0));
        camYaw = mc.field_1724.method_5705(pt);
        camPitch = mc.field_1724.method_5695(pt);
        double y = Math.toRadians(camYaw);
        double p = Math.toRadians(camPitch);
        fx = -Math.sin(y) * Math.cos(p);
        fy = -Math.sin(p);
        fz = Math.cos(y) * Math.cos(p);
        rx = -Math.cos(y);
        ry = 0.0;
        rz = -Math.sin(y);
        ux = ry * fz - rz * fy;
        uy = rz * fx - rx * fz;
        uz = rx * fy - ry * fx;
    }

    private static boolean project(double dx, double dy, double dz) {
        double zc = dx * fx + dy * fy + dz * fz;
        if (zc < 0.02) {
            return false;
        }
        double xc = dx * rx + dy * ry + dz * rz;
        double yc = dx * ux + dy * uy + dz * uz;
        SkyRenderer.P[0] = (float)((double)(sw / 2.0f) + (double)focal * xc / zc);
        SkyRenderer.P[1] = (float)((double)(sh / 2.0f) - (double)focal * yc / zc);
        SkyRenderer.P[2] = (float)zc;
        return true;
    }

    private static boolean projectDir(double azDeg, double elDeg) {
        double a = Math.toRadians(azDeg);
        double e = Math.toRadians(elDeg);
        return SkyRenderer.project(-Math.sin(a) * Math.cos(e), Math.sin(e), Math.cos(a) * Math.cos(e));
    }

    private static void initStars() {
        Random r = new Random(1337L);
        stars = new float[520][5];
        for (int i = 0; i < stars.length; ++i) {
            float[] s = stars[i];
            s[0] = r.nextFloat() * 360.0f;
            s[1] = (float)Math.toDegrees(Math.asin(0.05 + (double)r.nextFloat() * 0.95));
            s[2] = r.nextFloat() < 0.12f ? 2.0f : 1.0f;
            s[3] = 0.35f + r.nextFloat() * 0.65f;
            s[4] = r.nextFloat() * 6.28f;
        }
    }

    public static void tick(class_310 mc) {
        boolean wantRain;
        SkyPreset p;
        Config c = NeonLyrics.cfg();
        if (c.sky != lastSky) {
            if (lastSky != -99 && mc.field_1724 != null) {
                p = c.customSkyboxEnabled ? SkyPreset.get(c.sky) : null;
                Island.notify(p == null ? Fx.t("neonlyrics.sky.off", new Object[0]) : Fx.t("neonlyrics.sky.enabled", Fx.t(p.key(), new Object[0])), p != null);
            }
            lastSky = c.sky;
        }
        if (mc.field_1687 == null) {
            forcedRain = false;
            return;
        }
        SkyRenderer.updateClouds(mc, c);
        p = c.customSkyboxEnabled ? SkyPreset.get(c.sky) : null;
        boolean bl = wantRain = p != null && c.realWeather && (p.weather == SkyPreset.Weather.RAIN || p.weather == SkyPreset.Weather.THUNDER) && mc.field_1687.method_27983() == class_1937.field_25179;
        if (wantRain) {
            mc.field_1687.method_8519(1.0f);
            mc.field_1687.method_8496(p.weather == SkyPreset.Weather.THUNDER ? 1.0f : 0.0f);
            forcedRain = true;
        } else if (forcedRain) {
            mc.field_1687.method_8519(0.0f);
            mc.field_1687.method_8496(0.0f);
            forcedRain = false;
        }
    }

    private static void updateClouds(class_310 mc, Config c) {
        boolean hide = c.customSkyboxEnabled && c.hideClouds && c.sky >= 0;
        class_7172 opt = mc.field_1690.method_42528();
        if (hide && opt.method_41753() != class_4063.field_18162) {
            c.cloudsBefore = ((class_4063)opt.method_41753()).name();
            opt.method_41748((Object)class_4063.field_18162);
            NeonLyrics.saveConfig();
        } else if (!hide && !c.cloudsBefore.isEmpty()) {
            try {
                opt.method_41748((Object)class_4063.valueOf((String)c.cloudsBefore));
            }
            catch (Exception exception) {
                // empty catch block
            }
            c.cloudsBefore = "";
            NeonLyrics.saveConfig();
        }
    }

    public static void render(class_332 g, class_9779 delta) {
        if (!License.ok() || !NeonLyrics.cfg().customSkyboxEnabled) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1690.field_1842 || mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        Config c = NeonLyrics.cfg();
        long nowNs = System.nanoTime();
        float dt = Math.min(0.1f, (float)(nowNs - lastNanos) / 1.0E9f);
        lastNanos = nowNs;
        float pt = delta.method_60637(false);
        SkyRenderer.setupCamera(g, mc, pt);
        SkyPreset p = c.customSkyboxEnabled ? SkyPreset.get(c.sky) : null;
        float old = Gfx.alpha;
        Gfx.alpha = 1.0f;
        if (p != null && p.weather == SkyPreset.Weather.THUNDER && mc.field_1687.method_27983() == class_1937.field_25179) {
            SkyRenderer.drawLightning(g);
        }
        Gfx.alpha = old;
    }

    private static void drawLightning(class_332 g) {
        long age;
        long now = System.currentTimeMillis();
        if (now > nextFlash) {
            flashAt = now;
            nextFlash = now + 5000L + (long)RND.nextInt(9000);
        }
        if ((age = now - flashAt) < 600L) {
            float a = age < 90L ? 0.32f : (age < 180L ? 0.05f : (age < 260L ? 0.22f : 0.22f * (1.0f - (float)(age - 260L) / 340.0f)));
            Gfx.rect(g, 0, 0, (int)sw, (int)sh, 15266047, Math.max(0.0f, a));
        }
    }

    static {
        P = new float[3];
        visible = new Anim(0.0, 4.0);
        RND = new Random();
        shooting = new ArrayList<float[]>();
        nextShooting = 0L;
        parts = new double[0][];
        partType = null;
        lastNanos = System.nanoTime();
        flashAt = 0L;
        nextFlash = 0L;
        forcedRain = false;
        lastSky = -99;
    }
}

