/*
 * Decompiled with CFR 0.152.
 */
package dev.neonlyrics.sky;

import dev.neonlyrics.config.Config;
import dev.neonlyrics.sky.SkyPreset;
import java.util.Random;

public final class LiveSky {
    public static final LiveSky MAIN = new LiveSky();
    private SkyPreset P;
    private float tr;
    private float tg;
    private float tb;
    private float hr;
    private float hg;
    private float hb;
    private float ar1;
    private float ag1;
    private float ab1;
    private float ar2;
    private float ag2;
    private float ab2;
    private double T;
    private float pattern = 1.0f;
    private float lively = 0.6f;
    private float breath = 1.0f;
    private float auroraOn = 1.0f;
    private double sunX;
    private double sunY;
    private double sunZ;
    private Body body;
    private float R;
    private float G;
    private float B;
    private static final double[][] PILLAR = new double[16][];

    public static Body body(SkyPreset p) {
        return switch (p) {
            case SkyPreset.SYNTHWAVE -> new Body(180.0, 12.0, 12.0, 16765803, 3);
            case SkyPreset.GOLDEN_HOUR -> new Body(200.0, 4.0, 3.6, 16761466, 0);
            case SkyPreset.COTTON_CANDY -> new Body(120.0, 38.0, 2.4, 16774368, 0);
            case SkyPreset.SAKURA -> new Body(60.0, 9.0, 2.6, 0xFFE2EE, 0);
            case SkyPreset.COSMOS -> new Body(60.0, 28.0, 5.0, 12166911, 1);
            case SkyPreset.BLACK_HOLE -> new Body(165.0, 24.0, 5.2, 16757850, 2);
            case SkyPreset.BLOOD_MOON -> new Body(200.0, 22.0, 4.4, 16728120, 1);
            case SkyPreset.FROST -> new Body(210.0, 30.0, 2.6, p.moon, 1);
            default -> p.moon != 0 ? new Body(200.0, 26.0, 2.4, p.moon, 1) : null;
        };
    }

    public static boolean synthGap(double v, double t) {
        if (v > 0.45) {
            return false;
        }
        double s = -v * 6.0 + t * 0.12;
        return (s -= Math.floor(s)) < 0.06 + 0.46 * Math.max(0.0, Math.min(1.0, (0.45 - v) / 1.45));
    }

    public void prepare(SkyPreset p, int a1, int a2, double t, Config c) {
        this.P = p;
        this.tr = LiveSky.r(p.top);
        this.tg = LiveSky.g(p.top);
        this.tb = LiveSky.b(p.top);
        this.hr = LiveSky.r(p.horizon);
        this.hg = LiveSky.g(p.horizon);
        this.hb = LiveSky.b(p.horizon);
        this.ar1 = LiveSky.r(a1);
        this.ag1 = LiveSky.g(a1);
        this.ab1 = LiveSky.b(a1);
        this.ar2 = LiveSky.r(a2);
        this.ag2 = LiveSky.g(a2);
        this.ab2 = LiveSky.b(a2);
        this.T = t * 2.2;
        if (c != null) {
            this.pattern = Math.max(0.2f, Math.min(1.5f, c.skyPattern));
            this.lively = Math.max(0.0f, Math.min(1.0f, c.skyLively));
            this.breath = c.skyBreath <= 0.01f ? 1.0f : 0.85f + 0.15f * (float)Math.sin(t * 0.45 * (double)Math.max(0.2f, c.skyBreath));
            this.auroraOn = c.fxAurora ? 1.0f : 0.0f;
        }
        this.body = LiveSky.body(p);
        if (this.body != null) {
            double a = Math.toRadians(this.body.az());
            double e = Math.toRadians(this.body.el());
            this.sunX = -Math.sin(a) * Math.cos(e);
            this.sunY = Math.sin(e);
            this.sunZ = Math.cos(a) * Math.cos(e);
        }
    }

    public static int synthColor(double v) {
        float k = (float)Math.max(0.0, Math.min(1.0, (v + 1.0) / 2.0));
        int lo = 16727450;
        int mid = 16747100;
        int hi = 16769658;
        return k < 0.5f ? LiveSky.mix(lo, mid, k * 2.0f) : LiveSky.mix(mid, hi, (k - 0.5f) * 2.0f);
    }

    private static int mix(int a, int b, float t) {
        int r = Math.round((float)(a >> 16 & 0xFF) + (float)((b >> 16 & 0xFF) - (a >> 16 & 0xFF)) * t);
        int g = Math.round((float)(a >> 8 & 0xFF) + (float)((b >> 8 & 0xFF) - (a >> 8 & 0xFF)) * t);
        int bl = Math.round((float)(a & 0xFF) + (float)((b & 0xFF) - (a & 0xFF)) * t);
        return r << 16 | g << 8 | bl;
    }

    private static float r(int c) {
        return (float)(c >> 16 & 0xFF) / 255.0f;
    }

    private static float g(int c) {
        return (float)(c >> 8 & 0xFF) / 255.0f;
    }

    private static float b(int c) {
        return (float)(c & 0xFF) / 255.0f;
    }

    private void add(float r, float g, float b, float k) {
        this.R += r * k;
        this.G += g * k;
        this.B += b * k;
    }

    private void over(float r, float g, float b, float k) {
        k = Math.max(0.0f, Math.min(1.0f, k));
        this.R += (r - this.R) * k;
        this.G += (g - this.G) * k;
        this.B += (b - this.B) * k;
    }

    public int color(double x, double y, double z, float elDeg) {
        SkyPreset p = this.P;
        float el = elDeg;
        double az = Math.toDegrees(Math.atan2(-x, z));
        if (az < 0.0) {
            az += 360.0;
        }
        double t = this.T;
        float k = (float)Math.pow(LiveSky.clamp(el / 72.0f), 0.55);
        this.R = this.hr + (this.tr - this.hr) * k;
        this.G = this.hg + (this.tg - this.hg) * k;
        this.B = this.hb + (this.tb - this.hb) * k;
        if (el < 0.0f) {
            float d = LiveSky.clamp(-el / 6.0f) * 0.55f;
            this.R *= 1.0f - d;
            this.G *= 1.0f - d;
            this.B *= 1.0f - d;
        }
        float lowGlow = (float)Math.exp((double)(-Math.max(0.0f, el)) / 9.0);
        switch (p) {
            case SYNTHWAVE: {
                this.synth(el);
                break;
            }
            case AURORA: 
            case CUSTOM: {
                this.milkyWay(x, y, z, 0.16f);
                this.curtains(az, el, 1.0f);
                this.add(this.ar1, this.ag1, this.ab1, 0.06f * lowGlow);
                break;
            }
            case NEBULA: {
                this.swirl(x, y, z);
                this.add(this.ar1, this.ag1, this.ab1, 0.1f * lowGlow);
                break;
            }
            case BLACK_HOLE: {
                this.milkyWay(x, y, z, 0.22f);
                this.accretion(az, el);
                break;
            }
            case COSMOS: {
                this.galaxyBand(x, y, z);
                this.add(this.ar2, this.ag2, this.ab2, 0.08f * lowGlow);
                break;
            }
            case NEON_CITY: {
                this.cityGlow(az, el);
                this.clouds(x, y, z, 0.62f, 0.55f, 6958202, 789022, 0.03);
                break;
            }
            case GOLDEN_HOUR: {
                this.add(1.0f, 0.55f, 0.28f, 0.5f * lowGlow);
                this.clouds(x, y, z, 0.54f, 0.85f, 16761994, 5909598, 0.02);
                break;
            }
            case COTTON_CANDY: {
                this.add(1.0f, 0.8f, 0.92f, 0.14f * lowGlow);
                this.clouds(x, y, z, 0.6f, 0.92f, 0xFFFFFF, 15907032, 0.025);
                break;
            }
            case SAKURA: {
                this.add(1.0f, 0.61f, 0.8f, 0.4f * lowGlow);
                this.clouds(x, y, z, 0.57f, 0.6f, 16771314, 13144248, 0.02);
                break;
            }
            case FROST: {
                this.iceBands(az, el);
                this.curtains(az, el, 0.45f);
                this.milkyWay(x, y, z, 0.2f);
                break;
            }
            case MIDNIGHT: {
                this.milkyWay(x, y, z, 0.5f);
                this.add(0.16f, 0.23f, 0.56f, 0.18f * lowGlow);
                break;
            }
            case DEEP_SEA: {
                this.caustics(x, y, z, el);
                this.add(this.ar2, this.ag2, this.ab2, 0.16f * lowGlow);
                break;
            }
            case BLOOD_MOON: {
                this.add(1.0f, 0.23f, 0.16f, 0.25f * lowGlow);
                this.clouds(x, y, z, 0.6f, 0.75f, 12070970, 1704968, 0.03);
                break;
            }
            case STORM: {
                this.clouds(x, y, z, 0.3f, 0.97f, 5923960, 1185311, 0.11);
                break;
            }
            case INFERNO: {
                this.lava(x, y, z, el);
                this.add(1.0f, 0.35f, 0.08f, 0.35f * lowGlow);
                break;
            }
            case MATRIX: {
                this.code(az, el);
                break;
            }
            case PRISM: {
                this.prism(az, el);
                break;
            }
            case PULSAR: {
                this.milkyWay(x, y, z, 0.25f);
                this.pulsar(x, y, z);
                break;
            }
            case AQUARELL: {
                this.paint(x, y, z, el);
            }
        }
        if (this.body != null) {
            this.bodyGlow(x, y, z, az, el);
        }
        int ri = Math.round(LiveSky.soft(this.R) * 255.0f);
        int gi = Math.round(LiveSky.soft(this.G) * 255.0f);
        int bi = Math.round(LiveSky.soft(this.B) * 255.0f);
        return ri << 16 | gi << 8 | bi;
    }

    private static float soft(float v) {
        if (v <= 0.8f) {
            return Math.max(0.0f, v);
        }
        float o = v - 0.8f;
        return 0.8f + 0.2f * (o / (o + 0.2f));
    }

    private void synth(float el) {
        float glow = (float)Math.exp((double)(-Math.max(0.0f, el)) / 9.0);
        this.add(this.ar1, this.ag1, this.ab1, 0.5f * glow);
        double line = Math.exp(-Math.pow(((double)el - 0.7) / 0.6, 2.0));
        float pulse = 0.75f + 0.25f * (float)Math.sin(this.T * 1.6);
        this.add(this.ar2, this.ag2, this.ab2, (float)(line * 0.85 * (double)pulse));
        this.add(this.ar2, this.ag2, this.ab2, 0.08f * (float)Math.exp((double)(-Math.max(0.0f, el)) / 3.0));
        if (el > 20.0f) {
            double sl = 0.5 + 0.5 * Math.sin((double)el * 1.3 - this.T * 0.5);
            this.add(this.ar1, this.ag1, this.ab1, (float)(0.03 * sl * LiveSky.smooth(20.0, 40.0, el) * (double)this.pattern));
        }
    }

    private void curtains(double az, float el, float strength) {
        if (this.auroraOn <= 0.0f || el < -2.0f) {
            return;
        }
        double azr = Math.toRadians(az);
        double t = this.T * (0.3 + (double)this.lively * 0.45);
        for (int k = 0; k < 2; ++k) {
            double vert;
            double base = (double)(17 + k * 11) + 7.0 * Math.sin(azr * 2.0 + t * 0.22 + (double)k * 1.7) + 3.0 * Math.sin(azr * 5.0 - t * 0.35 + (double)k);
            double h = (double)el - base;
            double d = vert = h < 0.0 ? Math.exp(-h * h / 3.0) : Math.exp(-h / (15.0 + (double)(6 * k)));
            if (vert < 0.01) continue;
            double fold = 0.55 + 0.45 * Math.sin(azr * 9.0 + 2.5 * Math.sin(azr * 2.0 + t * 0.45) + t * 0.7 + (double)(k * 3));
            double where = LiveSky.smooth(0.25, 0.65, LiveSky.noise(Math.cos(azr) * 0.6876 + (double)(k * 5), Math.sin(azr) * 0.6876 + t * 0.04, (double)k + 0.3));
            float in = (float)(vert * (0.35 + 0.65 * fold) * (0.25 + 0.75 * where) * (k == 0 ? 1.0 : 0.6) * (double)strength * (double)this.pattern * (double)this.breath);
            float m = LiveSky.clamp((float)(h / 18.0));
            this.add(this.ar1 + (this.ar2 - this.ar1) * m, this.ag1 + (this.ag2 - this.ag1) * m, this.ab1 + (this.ab2 - this.ab1) * m, in * 0.85f);
        }
    }

    private void swirl(double x, double y, double z) {
        double t = this.T * 0.012 * (0.5 + (double)this.lively);
        double ca = Math.cos(t);
        double sa = Math.sin(t);
        double rx = x * ca - z * sa;
        double rz = x * sa + z * ca;
        double w1 = LiveSky.fbm(rx * 1.2 + 3.0, y * 1.2, rz * 1.2, 2);
        double w2 = LiveSky.fbm(rx * 1.2, y * 1.2 + 7.0, rz * 1.2 + 1.0, 2);
        double n = LiveSky.fbm(rx * 1.8 + w1 * 2.2, y * 1.8 + w2 * 2.2, rz * 1.8 - w1 * 1.5 + t * 2.0, 3);
        double cloud = LiveSky.smooth(0.36, 0.78, n);
        double core = LiveSky.smooth(0.58, 0.86, n);
        if (cloud < 0.01) {
            return;
        }
        float mm = (float)LiveSky.smooth(0.3, 0.7, w1);
        float k = (float)(cloud * 0.6 * (double)this.pattern * (double)this.breath);
        this.add(this.ar2 + (this.ar1 - this.ar2) * mm, this.ag2 + (this.ag1 - this.ag2) * mm, this.ab2 + (this.ab1 - this.ab2) * mm, k);
        this.add(1.0f, 0.92f, 1.0f, (float)(core * 0.22 * (double)this.pattern));
    }

    private void accretion(double az, float el) {
        Body bd = this.body;
        if (bd == null) {
            return;
        }
        double du = ((az - bd.az()) % 360.0 + 540.0) % 360.0 - 180.0;
        double dv = (double)el - bd.el();
        double R = bd.radius();
        double flat = 0.2;
        double rr = Math.sqrt((du *= Math.cos(Math.toRadians(el))) * du + Math.pow(dv / flat, 2.0));
        double dist = Math.sqrt(du * du + dv * dv);
        if (dist > R) {
            this.add(this.ar1, this.ag1, this.ab1, (float)(Math.exp(-(dist - R) / (R * 0.9)) * 0.35 * (double)this.pattern));
        }
        if (rr < R * 1.1 || rr > R * 6.5) {
            return;
        }
        double band = LiveSky.smooth(R * 1.1, R * 1.35, rr) * (1.0 - LiveSky.smooth(R * 3.0, R * 6.5, rr));
        double ang = Math.atan2(dv / flat, du);
        double a2 = ang - this.T * 0.3;
        double streak = 0.5 + 0.5 * LiveSky.noise(Math.cos(a2) * 3.0, Math.sin(a2) * 3.0 + rr * 0.3, 4.2);
        double doppler = 1.0 + 0.75 * (-du / rr);
        float in = (float)(band * streak * doppler * (double)this.pattern);
        float hot = LiveSky.clamp((float)(1.0 - (rr - R * 1.1) / (R * 3.0)));
        this.add(this.ar2 + (1.0f - this.ar2) * hot * 0.7f, this.ag2 + (this.ag1 - this.ag2) * hot + (1.0f - this.ag1) * hot * 0.3f, this.ab2 + (this.ab1 - this.ab2) * hot, in * 1.1f);
    }

    private void galaxyBand(double x, double y, double z) {
        double l;
        double nz;
        double ny;
        double t = this.T * 0.004;
        double nx = 0.36 * Math.cos(t) - 0.73 * Math.sin(t);
        double d = (x * nx + y * (ny = 0.58) + z * (nz = 0.36 * Math.sin(t) + 0.73 * Math.cos(t))) / (l = Math.sqrt(nx * nx + ny * ny + nz * nz));
        double band = Math.exp(-d * d / 0.04);
        if (band < 0.01) {
            return;
        }
        double core = Math.exp(-d * d / 0.005);
        double tint = LiveSky.fbm(x * 2.2 + 5.0, y * 2.2, z * 2.2, 2);
        this.add(this.ar1, this.ag1, this.ab1, (float)(band * LiveSky.smooth(0.45, 0.75, tint) * 0.22 * (double)this.pattern));
        double n = LiveSky.fbm(x * 5.0 + 2.0, y * 5.0, z * 5.0 - 1.0, 3);
        double dust = Math.exp(-d * d / 0.0025) * LiveSky.smooth(0.45, 0.7, LiveSky.fbm(x * 11.0, y * 11.0 + 3.0, z * 11.0, 2));
        float in = (float)(band * (0.4 + 0.8 * n) * (1.0 - 0.7 * dust) * (double)this.pattern);
        float m = (float)LiveSky.smooth(0.3, 0.8, n);
        this.add(this.ar2 + (this.ar1 - this.ar2) * m, this.ag2 + (this.ag1 - this.ag2) * m, this.ab2 + (this.ab1 - this.ab2) * m, in * 0.45f);
        this.add(1.0f, 0.9f, 0.8f, (float)(core * (1.0 - 0.7 * dust) * 0.35 * (double)this.pattern));
    }

    private void cityGlow(double az, float el) {
        float low = (float)Math.exp((double)(-Math.max(0.0f, el)) / 10.0);
        this.add(this.ar1, this.ag1, this.ab1, 0.42f * low);
        this.add(this.ar2, this.ag2, this.ab2, 0.22f * (float)Math.exp((double)(-Math.max(0.0f, el)) / 3.5));
        double azc = Math.toRadians(az);
        double city = LiveSky.smooth(0.4, 0.8, LiveSky.noise(Math.cos(azc) * 2.865, 0.5 + Math.sin(azc) * 2.865, 1.3));
        this.add(this.ar1, this.ag1, this.ab1, (float)(0.22 * city * Math.exp((double)(-Math.max(0.0f, el)) / 5.0)));
        if (el < -1.0f) {
            return;
        }
        for (int i = 0; i < 3; ++i) {
            double w;
            double tilt;
            double center = (double)(60 + i * 120) + 28.0 * Math.sin(this.T * 0.25 + (double)i * 2.1);
            double daz = ((az - center - (tilt = 18.0 * Math.sin(this.T * 0.18 + (double)i)) * (double)el / 60.0) % 360.0 + 540.0) % 360.0 - 180.0;
            double beam = Math.exp(-(daz * daz) / ((w = 1.8 + (double)el * 0.06) * w)) * Math.exp((double)(-Math.max(0.0f, el)) / 50.0) * LiveSky.smooth(-1.0, 3.0, el);
            if (beam < 0.005) continue;
            float br = (float)(beam * 0.5 * (double)this.pattern);
            if (i == 1) {
                this.add(this.ar2, this.ag2, this.ab2, br);
                continue;
            }
            if (i == 0) {
                this.add(this.ar1, this.ag1, this.ab1, br);
                continue;
            }
            this.add(0.9f, 0.9f, 1.0f, br * 0.8f);
        }
    }

    private void iceBands(double az, float el) {
        if (el < 2.0f) {
            return;
        }
        double azi = Math.toRadians(az);
        double n = LiveSky.noise(Math.cos(azi) * 1.719, this.T * 0.05 + Math.sin(azi) * 1.719, 2.2);
        double band = Math.pow(0.5 + 0.5 * Math.sin((double)el * 0.35 + n * 3.0 - this.T * 0.15), 6.0);
        this.add(this.ar1, this.ag1, this.ab1, (float)(0.16 * band * LiveSky.smooth(5.0, 25.0, el) * (double)this.pattern));
    }

    private void lava(double x, double y, double z, float el) {
        double t = this.T * 0.03 * (0.6 + (double)this.lively);
        double w = LiveSky.fbm(x * 1.6 + 3.0, y * 1.6 - t, z * 1.6, 2);
        double n = LiveSky.fbm(x * 2.4 + w * 1.6 + t, y * 2.4 - t * 0.7, z * 2.4 - w * 1.2, 3);
        double vein = Math.pow(1.0 - Math.abs(2.0 * n - 1.0), 6.0);
        double pulse = 0.75 + 0.25 * Math.sin(this.T * 1.3 + n * 12.0);
        float k = (float)(vein * pulse * (double)this.pattern * (double)this.breath * (0.4 + 0.6 * LiveSky.smooth(-2.0, 20.0, el)));
        this.add(this.ar1, this.ag1, this.ab1, k * 1.1f);
        this.add(this.ar2, this.ag2, this.ab2, (float)(Math.pow(vein, 3.0) * 0.8 * (double)this.pattern));
        this.add(0.5f, 0.06f, 0.02f, (float)(LiveSky.smooth(0.45, 0.8, n) * 0.18));
    }

    private void code(double az, float el) {
        double edge;
        if (el < -2.0f) {
            return;
        }
        double colW = 4.0;
        double col = Math.floor(az / colW);
        double h1 = LiveSky.hash3((int)col, 7, 3);
        double h2 = LiveSky.hash3((int)col, 11, 5);
        double speed = 0.25 + h1 * 0.5 * (0.5 + (double)this.lively);
        double head = 90.0 - (this.T * speed * 0.35 + h2) % 1.0 * 110.0;
        double d = head - (double)el;
        double cell = Math.floor((double)el / 3.0);
        double flick = LiveSky.hash3((int)col, (int)cell, (int)Math.floor(this.T * 3.0 + h1 * 7.0)) > 0.25 ? 1.0 : 0.35;
        double d2 = edge = Math.abs(az / colW - col - 0.5) < 0.38 ? 1.0 : 0.2;
        if (d < 0.0) {
            double trail = Math.exp(d / 14.0) * flick * edge;
            this.add(this.ar1, this.ag1, this.ab1, (float)(trail * 0.75 * (double)this.pattern));
        }
        if (Math.abs(d) < 2.2) {
            this.add(this.ar2, this.ag2, this.ab2, (float)((1.0 - Math.abs(d) / 2.2) * edge * (double)this.pattern));
        }
        this.add(this.ar1, this.ag1, this.ab1, (float)(0.05 * flick * edge * (0.5 + 0.5 * Math.sin(this.T + col))));
    }

    private void prism(double az, float el) {
        double azr = Math.toRadians(az);
        double t = this.T * (0.15 + (double)this.lively * 0.2);
        double beams = Math.pow(0.5 + 0.5 * Math.sin(azr * 7.0 + t + (double)el * 0.035), 14.0);
        double beams2 = Math.pow(0.5 + 0.5 * Math.sin(azr * 11.0 - t * 1.3 + (double)el * 0.02 + 1.0), 18.0);
        double fall = LiveSky.smooth(0.0, 75.0, el);
        double hue = (az / 360.0 + this.T * 0.02) % 1.0;
        int c = LiveSky.hsv(hue, 0.6, 1.0);
        int c2 = LiveSky.hsv((hue + 0.33) % 1.0, 0.6, 1.0);
        this.add(LiveSky.r(c), LiveSky.g(c), LiveSky.b(c), (float)(beams * fall * 0.5 * (double)this.pattern * (double)this.breath));
        this.add(LiveSky.r(c2), LiveSky.g(c2), LiveSky.b(c2), (float)(beams2 * fall * 0.35 * (double)this.pattern));
        double top = Math.exp((double)(-(90.0f - el)) / 18.0);
        this.add(1.0f, 0.95f, 1.0f, (float)(top * 0.25 * (double)this.pattern));
    }

    private void pulsar(double x, double y, double z) {
        double a = Math.toRadians(30.0);
        double e = Math.toRadians(50.0);
        double px = -Math.sin(a) * Math.cos(e);
        double py = Math.sin(e);
        double pz = Math.cos(a) * Math.cos(e);
        double dot = x * px + y * py + z * pz;
        double ang = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, dot))));
        this.add(1.0f, 1.0f, 1.0f, (float)(Math.exp(-ang / 1.2) * 0.9 + Math.exp(-ang / 6.0) * 0.25));
        this.add(this.ar1, this.ag1, this.ab1, (float)(Math.exp(-ang / 14.0) * 0.2));
        double t = this.T * (0.5 + (double)this.lively * 0.6);
        double ux = -Math.sin(a + 1.5707963267948966);
        double uz = Math.cos(a + 1.5707963267948966);
        double vx = py * uz;
        double vy = pz * ux - px * uz;
        double vz = -py * ux;
        double nx = Math.cos(t) * ux + Math.sin(t) * vx;
        double ny = Math.sin(t) * vy;
        double nz = Math.cos(t) * uz + Math.sin(t) * vz;
        double band = x * nx + y * ny + z * nz;
        double beam = Math.exp(-band * band / 0.0025) * Math.exp(-ang / 55.0);
        this.add(this.ar1, this.ag1, this.ab1, (float)(beam * 0.7 * (double)this.pattern));
        double ringR = this.T * 0.25 % 1.0 * 70.0;
        double ring = Math.exp(-Math.pow((ang - ringR) / 2.0, 2.0)) * (1.0 - ringR / 70.0);
        this.add(this.ar2, this.ag2, this.ab2, (float)(ring * 0.45 * (double)this.pattern * (double)this.breath));
    }

    private void paint(double x, double y, double z, float el) {
        double t = this.T * 0.02 * (0.5 + (double)this.lively);
        int[] cols = new int[]{16752329, 10475775, 16765850, 12186312, 13215487};
        for (int k = 0; k < 3; ++k) {
            double n = LiveSky.fbm(x * 1.3 + (double)(k * 7) + t, y * 1.3 - t * 0.6, z * 1.3 + (double)(k * 3), 3);
            double blob = LiveSky.smooth(0.44, 0.62, n);
            if (blob < 0.01) continue;
            double rim = Math.exp(-Math.pow((n - 0.5) / 0.05, 2.0));
            int c = cols[(k * 2 + (int)Math.floor(n * 5.0)) % cols.length];
            this.over(LiveSky.r(c), LiveSky.g(c), LiveSky.b(c), (float)(blob * 0.65 * (double)this.pattern));
            this.R *= 1.0f - 0.12f * (float)rim;
            this.G *= 1.0f - 0.12f * (float)rim;
            this.B *= 1.0f - 0.12f * (float)rim;
        }
    }

    private static int hsv(double h, double s, double v) {
        double gg;
        double rr;
        double hh = (h % 1.0 + 1.0) % 1.0 * 6.0;
        double f = hh - Math.floor(hh);
        double p = v * (1.0 - s);
        double q = v * (1.0 - s * f);
        double u = v * (1.0 - s * (1.0 - f));
        double bb = switch ((int)Math.floor(hh)) {
            case 0 -> {
                rr = v;
                gg = u;
                yield p;
            }
            case 1 -> {
                rr = q;
                gg = v;
                yield p;
            }
            case 2 -> {
                rr = p;
                gg = v;
                yield u;
            }
            case 3 -> {
                rr = p;
                gg = q;
                yield v;
            }
            case 4 -> {
                rr = u;
                gg = p;
                yield v;
            }
            default -> {
                rr = v;
                gg = p;
                yield q;
            }
        };
        return (int)Math.round(rr * 255.0) << 16 | (int)Math.round(gg * 255.0) << 8 | (int)Math.round(bb * 255.0);
    }

    private void aurora(double az, float el, float strength) {
        if (this.auroraOn <= 0.0f || el < -2.0f) {
            return;
        }
        double azr = Math.toRadians(az);
        double t = this.T * (0.6 + (double)this.lively * 0.8);
        for (int k = 0; k < 2; ++k) {
            double vert;
            double base = (double)(22 + k * 9) + 10.0 * Math.sin(azr * 2.0 + t * 0.3 + (double)k * 2.1) + 5.0 * Math.sin(azr * 5.0 - t * 0.55 + (double)k) + 2.5 * Math.sin(azr * 11.0 + t * 1.1);
            double h = (double)el - base;
            double d = vert = h < 0.0 ? Math.exp(-h * h / 5.0) : Math.exp(-h / (13.0 + (double)(5 * k)));
            if (vert < 0.01) continue;
            double rays = LiveSky.noise(az * 0.2 + (double)(k * 41) + t * 0.35, t * 0.9, (double)k * 7.3);
            rays = 0.35 + 1.1 * rays * rays;
            double band = LiveSky.smooth(0.28, 0.72, LiveSky.noise(az * 0.018 + (double)(k * 3), t * 0.06, 1.7 + (double)k));
            double pulse = 0.65 + 0.9 * Math.pow(0.5 + 0.5 * Math.sin(azr * 3.0 - t * 1.4 + (double)(k * 2)), 3.0);
            float in = (float)(vert * rays * pulse * (0.25 + 0.75 * band) * (k == 0 ? 1.0 : 0.6) * (double)strength * (double)this.pattern * (double)this.breath);
            float m = LiveSky.clamp((float)(h / 20.0));
            this.add(this.ar1 + (this.ar2 - this.ar1) * m, this.ag1 + (this.ag2 - this.ag1) * m, this.ab1 + (this.ab2 - this.ab1) * m, in * 0.9f);
        }
    }

    private void milkyWay(double x, double y, double z, float strength) {
        double nx = 0.36;
        double ny = 0.58;
        double nz = 0.73;
        double d = x * nx + y * ny + z * nz;
        double band = Math.exp(-d * d / 0.03);
        if (band < 0.01) {
            return;
        }
        double n = LiveSky.fbm(x * 4.0 + this.T * 0.03, y * 4.0 - this.T * 0.02, z * 4.0 + this.T * 0.015, 3);
        double dust = Math.exp(-d * d / 0.0035) * LiveSky.smooth(0.42, 0.7, LiveSky.fbm(x * 9.0, y * 9.0 + 3.0, z * 9.0, 2));
        float in = (float)(band * (0.35 + 0.9 * n) * (1.0 - 0.75 * dust) * (double)strength * (double)this.pattern);
        float m = (float)n;
        this.add(0.62f + 0.38f * m, 0.7f + 0.2f * m, 1.0f - 0.18f * m, in * 0.55f);
    }

    private void nebula(double x, double y, double z, float strength, double scale) {
        double wy;
        double t = this.T * (0.035 + (double)this.lively * 0.05);
        double wx = LiveSky.noise(x * 1.3 + t * 0.7, y * 1.3, z * 1.3 - t * 0.5) - 0.5;
        double n = LiveSky.fbm(x * scale + t + wx * 1.4, y * scale - t * 0.6 + (wy = LiveSky.noise(x * 1.3 + 7.0, y * 1.3 + t * 0.6, z * 1.3 + 3.0) - 0.5) * 1.4, z * scale + t * 0.4 - wx, 3);
        double cloud = LiveSky.smooth(0.42, 0.85, n);
        if (cloud < 0.01) {
            return;
        }
        double m = LiveSky.fbm(x * scale * 1.7 - t, y * scale * 1.7, z * scale * 1.7 + 5.0, 2);
        float in = (float)(cloud * (double)strength * (double)this.pattern * (double)this.breath);
        float mm = (float)LiveSky.smooth(0.3, 0.75, m);
        this.add(this.ar1 + (this.ar2 - this.ar1) * mm, this.ag1 + (this.ag2 - this.ag1) * mm, this.ab1 + (this.ab2 - this.ab1) * mm, in * 0.6f);
    }

    private void pillars(double az, float el) {
        if (el < -2.0f) {
            return;
        }
        double t = this.T * (0.5 + (double)this.lively);
        float sum = 0.0f;
        for (double[] q : PILLAR) {
            double da = Math.abs(((az - q[0] - 6.0 * Math.sin(t * 0.2 * q[3] + q[2])) % 360.0 + 540.0) % 360.0 - 180.0);
            if (da > q[1] * 4.0) continue;
            double w = Math.exp(-da * da / (q[1] * q[1]));
            double height = 18.0 + 22.0 * (0.5 + 0.5 * Math.sin(t * 0.5 * q[3] + q[2]));
            double vert = Math.exp((double)(-Math.max(0.0f, el)) / height) * LiveSky.smooth(-2.0, 1.0, el);
            sum += (float)(w * vert * (0.75 + 0.25 * Math.sin(t * 2.2 * q[3] + q[2] * 3.0)));
        }
        if (sum <= 0.003f) {
            return;
        }
        float m = LiveSky.clamp(el / 35.0f);
        this.add(this.ar1 + (this.ar2 - this.ar1) * m, this.ag1 + (this.ag2 - this.ag1) * m, this.ab1 + (this.ab2 - this.ab1) * m, sum * 0.85f * this.pattern * this.breath);
    }

    private void caustics(double x, double y, double z, float el) {
        double t = this.T * (0.45 + (double)this.lively * 0.5);
        double s = 3.2;
        double n1 = LiveSky.noise(x * s + t, y * s + t * 0.7, z * s - t * 0.4);
        double n2 = LiveSky.noise(x * s * 1.6 - t * 0.8, y * s * 1.6 + 4.0, z * s * 1.6 + t * 0.5);
        double w1 = 1.0 - Math.abs(2.0 * n1 - 1.0);
        double w2 = 1.0 - Math.abs(2.0 * n2 - 1.0);
        double c = Math.pow(w1, 7.0) + 0.7 * Math.pow(w2, 9.0);
        float high = LiveSky.smooth01((el - 5.0f) / 50.0f);
        this.add(this.ar1, this.ag1, this.ab1, (float)(c * 0.5 * (double)high * (double)this.pattern * (double)this.breath));
        double az = Math.atan2(-x, z) + t * 0.05;
        double sh = LiveSky.noise(Math.cos(az) * 3.0, 2.5 + Math.sin(az) * 3.0, 7.0);
        this.add(this.ar2, this.ag2, this.ab2, (float)(Math.pow(sh, 4.0) * 0.35 * (double)high * (double)this.pattern));
    }

    private void clouds(double x, double y, double z, float thr, float opacity, int light, int dark, double speed) {
        if (y < 0.01) {
            return;
        }
        double s = 1.0 / (y + 0.12);
        double px = x * s;
        double t = this.T * speed * (1.2 + 1.5 * (double)this.lively);
        double pz = z * s;
        double n = LiveSky.fbm(px * 0.85 + t, pz * 0.85 + t * 0.35, t * 0.6, 4);
        double cov = LiveSky.smooth(thr, (double)thr + 0.22, n) * LiveSky.smooth(0.0, 0.14, y);
        if (cov < 0.01) {
            return;
        }
        double lit = LiveSky.smooth(0.35, 0.95, n + 0.15 * y);
        if (this.body != null && this.body.kind() == 0) {
            lit = Math.min(1.0, lit + 0.35 * Math.max(0.0, x * this.sunX + y * this.sunY + z * this.sunZ));
        }
        float lr = LiveSky.r(light);
        float lg = LiveSky.g(light);
        float lb = LiveSky.b(light);
        float dr = LiveSky.r(dark);
        float dg = LiveSky.g(dark);
        float db = LiveSky.b(dark);
        float l = (float)lit;
        this.over(dr + (lr - dr) * l, dg + (lg - dg) * l, db + (lb - db) * l, (float)cov * opacity);
    }

    private void bodyGlow(double x, double y, double z, double az, float el) {
        double dot = x * this.sunX + y * this.sunY + z * this.sunZ;
        double ang = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, dot))));
        Body bd = this.body;
        float cr = LiveSky.r(bd.color());
        float cg = LiveSky.g(bd.color());
        float cb = LiveSky.b(bd.color());
        double rad = bd.radius();
        if (bd.kind() == 0) {
            this.add(cr, cg, cb, (float)(Math.exp(-ang / 14.0) * 0.55 + Math.exp(-ang / 4.0) * 0.45));
            if (this.P == SkyPreset.GOLDEN_HOUR || this.P == SkyPreset.SAKURA) {
                double around = Math.atan2((double)el - bd.el(), ((az - bd.az()) % 360.0 + 540.0) % 360.0 - 180.0);
                double ray = Math.pow(0.5 + 0.5 * Math.sin(around * 11.0 + this.T * 0.3 + LiveSky.noise(Math.cos(around) * 2.0, this.T * 0.25 + Math.sin(around) * 2.0, 3.0) * 3.0), 5.0);
                this.add(cr, cg, cb, (float)(ray * Math.exp(-ang / 26.0) * 0.28 * LiveSky.smooth(rad, rad * 2.5, ang) * (double)this.pattern));
            }
        } else if (bd.kind() == 1) {
            this.add(cr, cg, cb, (float)(Math.exp(-ang / 5.0) * 0.32 + Math.exp(-ang / 22.0) * 0.14));
        } else if (bd.kind() == 3) {
            double out = Math.max(0.0, ang - rad);
            this.add(this.ar1, this.ag1, this.ab1, (float)(Math.exp(-out / 10.0) * 0.45 + Math.exp(-out / 3.0) * 0.35));
        } else {
            if (ang < rad) {
                this.over(0.008f, 0.004f, 0.02f, 1.0f);
                return;
            }
            double around = Math.atan2((double)el - bd.el(), ((az - bd.az()) % 360.0 + 540.0) % 360.0 - 180.0);
            double fl = 0.6 + 0.5 * LiveSky.noise(Math.cos(around) * 3.0, this.T * 0.9 + Math.sin(around) * 3.0, 5.0);
            double a5 = around + this.T * 0.02;
            double streams = 0.5 + 1.0 * Math.pow(LiveSky.noise(Math.cos(a5) * 5.0 + 9.0, this.T * 0.35 + Math.sin(a5) * 5.0, 2.0), 2.0) * 2.0;
            double out = ang - rad;
            this.add(this.ar1, this.ag1, this.ab1, (float)(Math.exp(-out / 1.6) * 1.2 * fl));
            this.add(this.ar2, this.ag2, this.ab2, (float)(Math.exp(-out / 7.0) * 0.45 * streams * (double)this.pattern));
        }
    }

    public int[] view(SkyPreset p, int a1, int a2, double t, int w, int h, float yaw, float pitch, float fovDeg) {
        this.prepare(p, a1, a2, t, null);
        int[] out = new int[w * h];
        double f = (double)h / 2.0 / Math.tan(Math.toRadians(fovDeg / 2.0f));
        double ya = Math.toRadians(yaw);
        double pi = Math.toRadians(pitch);
        double fx0 = -Math.sin(ya) * Math.cos(pi);
        double fy0 = -Math.sin(pi);
        double fz0 = Math.cos(ya) * Math.cos(pi);
        double rx = -Math.cos(ya);
        double ry = 0.0;
        double rz = -Math.sin(ya);
        double ux = ry * fz0 - rz * fy0;
        double uy = rz * fx0 - rx * fz0;
        double uz = rx * fy0 - ry * fx0;
        Random rr = new Random(p.name().hashCode());
        double ph0 = rr.nextDouble() * 10.0;
        for (int yy = 0; yy < h; ++yy) {
            for (int xx = 0; xx < w; ++xx) {
                double tree;
                double az;
                double hill;
                Body bd;
                double hs;
                double sx = (double)xx + 0.5 - (double)w / 2.0;
                double sy = -((double)yy + 0.5 - (double)h / 2.0);
                double dx = fx0 * f + rx * sx + ux * sy;
                double dy = fy0 * f + ry * sx + uy * sy;
                double dz = fz0 * f + rz * sx + uz * sy;
                double l = Math.sqrt(dx * dx + dy * dy + dz * dz);
                float el = (float)Math.toDegrees(Math.asin(dy /= l));
                int c = this.color(dx /= l, dy, dz /= l, el);
                float cr = LiveSky.r(c);
                float cg = LiveSky.g(c);
                float cb = LiveSky.b(c);
                if (p.stars > 0.0f && el > 3.0f && (hs = LiveSky.hash3((int)Math.floor(dx * 260.0), (int)Math.floor(dy * 260.0), (int)Math.floor(dz * 260.0))) > 1.0 - 0.0045 * (double)p.stars) {
                    float st = (float)((hs - (1.0 - 0.0045 * (double)p.stars)) / (0.0045 * (double)p.stars)) * 0.9f;
                    cr = Math.min(1.0f, cr + st);
                    cg = Math.min(1.0f, cg + st);
                    cb = Math.min(1.0f, cb + st);
                }
                if ((bd = this.body) != null) {
                    double ang = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, dx * this.sunX + dy * this.sunY + dz * this.sunZ))));
                    double edge = LiveSky.clamp((float)((bd.radius() - ang) / 0.5 + 0.5));
                    if (bd.kind() == 3) {
                        double v = ((double)el - bd.el()) / bd.radius();
                        if (edge > 0.0 && !LiveSky.synthGap(v, this.T / 2.2)) {
                            int sc = LiveSky.synthColor(v);
                            cr = (float)((double)cr + (double)(LiveSky.r(sc) - cr) * edge);
                            cg = (float)((double)cg + (double)(LiveSky.g(sc) - cg) * edge);
                            cb = (float)((double)cb + (double)(LiveSky.b(sc) - cb) * edge);
                        }
                    } else if (bd.kind() == 2) {
                        double ring = Math.exp(-Math.pow((ang - bd.radius()) / 0.35, 2.0));
                        cr = (float)Math.min(1.0, (double)cr * (1.0 - edge) + ring);
                        cg = (float)Math.min(1.0, (double)cg * (1.0 - edge) + ring * 0.92);
                        cb = (float)Math.min(1.0, (double)cb * (1.0 - edge) + ring * 0.8);
                    } else if (edge > 0.0) {
                        float br = bd.kind() == 0 ? 1.0f : 0.92f;
                        cr = (float)((double)cr + (double)(Math.min(1.0f, LiveSky.r(bd.color()) * 0.3f + 0.7f * br) - cr) * edge);
                        cg = (float)((double)cg + (double)(Math.min(1.0f, LiveSky.g(bd.color()) * 0.6f + 0.4f * br) - cg) * edge);
                        cb = (float)((double)cb + (double)(Math.min(1.0f, LiveSky.b(bd.color()) * 0.6f + 0.4f * br) - cb) * edge);
                    }
                }
                if ((double)el < (hill = 1.5 + 2.2 * Math.sin((az = Math.atan2(-dx, dz)) * 3.0 + ph0) + 1.2 * Math.sin(az * 7.0 + ph0 * 2.0)) + (tree = Math.max(0.0, Math.sin(az * 95.0 + ph0)) * Math.max(0.0, Math.sin(az * 37.0 + ph0 * 3.0)) * 4.5)) {
                    cr = 0.024f;
                    cg = 0.016f;
                    cb = 0.04f;
                }
                float dk = Math.max(0.0f, ((float)yy - (float)h * 0.6f) / ((float)h * 0.4f));
                dk = dk * dk * 0.55f;
                out[yy * w + xx] = 0xFF000000 | Math.round(LiveSky.clamp(cr *= 1.0f - dk) * 255.0f) << 16 | Math.round(LiveSky.clamp(cg *= 1.0f - dk) * 255.0f) << 8 | Math.round(LiveSky.clamp(cb *= 1.0f - dk) * 255.0f);
            }
        }
        return out;
    }

    private static double hash3(int x, int y, int z) {
        int h = x * 374761393 + y * 668265263 + z * 1274126177;
        h = (h ^ h >>> 13) * 1274126177;
        h ^= h >>> 16;
        return (double)(h & 0xFFFFFF) / 1.6777215E7;
    }

    static double noise(double x, double y, double z) {
        int ix = (int)Math.floor(x);
        int iy = (int)Math.floor(y);
        int iz = (int)Math.floor(z);
        double fx = x - (double)ix;
        double fy = y - (double)iy;
        double fz = z - (double)iz;
        double ux = fx * fx * (3.0 - 2.0 * fx);
        double uy = fy * fy * (3.0 - 2.0 * fy);
        double uz = fz * fz * (3.0 - 2.0 * fz);
        double a = LiveSky.hash3(ix, iy, iz);
        double b = LiveSky.hash3(ix + 1, iy, iz);
        double c = LiveSky.hash3(ix, iy + 1, iz);
        double d = LiveSky.hash3(ix + 1, iy + 1, iz);
        double e = LiveSky.hash3(ix, iy, iz + 1);
        double f = LiveSky.hash3(ix + 1, iy, iz + 1);
        double g = LiveSky.hash3(ix, iy + 1, iz + 1);
        double h = LiveSky.hash3(ix + 1, iy + 1, iz + 1);
        double x00 = a + (b - a) * ux;
        double x10 = c + (d - c) * ux;
        double x01 = e + (f - e) * ux;
        double x11 = g + (h - g) * ux;
        double y0 = x00 + (x10 - x00) * uy;
        double y1 = x01 + (x11 - x01) * uy;
        return y0 + (y1 - y0) * uz;
    }

    static double fbm(double x, double y, double z, int oct) {
        double sum = 0.0;
        double amp = 0.5;
        double norm = 0.0;
        for (int i = 0; i < oct; ++i) {
            sum += LiveSky.noise(x, y, z) * amp;
            norm += amp;
            x = x * 2.03 + 17.1;
            y = y * 2.03 - 9.7;
            z = z * 2.03 + 3.3;
            amp *= 0.5;
        }
        return sum / norm;
    }

    private static double smooth(double a, double b, double x) {
        double t = Math.max(0.0, Math.min(1.0, (x - a) / (b - a)));
        return t * t * (3.0 - 2.0 * t);
    }

    private static float smooth01(float x) {
        float t = LiveSky.clamp(x);
        return t * t * (3.0f - 2.0f * t);
    }

    private static float clamp(float v) {
        return Math.max(0.0f, Math.min(1.0f, v));
    }

    private static double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    static {
        Random r = new Random(77L);
        for (int i = 0; i < PILLAR.length; ++i) {
            LiveSky.PILLAR[i] = new double[]{r.nextDouble() * 360.0, 1.2 + r.nextDouble() * 2.2, r.nextDouble() * 6.28, 0.6 + r.nextDouble() * 0.8};
        }
    }

    public record Body(double az, double el, double radius, int color, int kind) {
        public static final int SUN = 0;
        public static final int MOON = 1;
        public static final int ECLIPSE = 2;
        public static final int SYNTH = 3;
    }
}

