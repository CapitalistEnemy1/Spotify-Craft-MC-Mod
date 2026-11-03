/*
 * Decompiled with CFR 0.152.
 */
package dev.neonlyrics.sky;

import java.util.Random;

public final class SkyPainter {
    public static final float EL_TOP = 90.0f;
    public static final float EL_BOTTOM = -8.0f;
    public static final int SPRITE = 64;

    private SkyPainter() {
    }

    private static int c255(float v) {
        return Math.max(0, Math.min(255, Math.round(v * 255.0f)));
    }

    public static int[] base(Spec s, int w, int h) {
        Buf b = new Buf(w, h);
        Random rnd = new Random((long)s.kind().hashCode() * 7919L + 13L);
        String k = s.kind();
        boolean day = k.equals("CLOUDS") || k.equals("WINTER");
        int a1 = s.a1();
        int a2 = s.a2();
        switch (k) {
            case "NEBULA": {
                SkyPainter.nebula(b, a1, a2, 1.0f, 11);
                SkyPainter.nebula(b, a2, 5930239, 0.35f, 29);
                SkyPainter.horizonGlow(b, a1, 0.3f, 16.0f);
                break;
            }
            case "GALAXY": {
                SkyPainter.milkyWay(b, a1, a2, 1.15f);
                SkyPainter.nebula(b, a1, a2, 0.45f, 5);
                SkyPainter.horizonGlow(b, a2, 0.25f, 14.0f);
                break;
            }
            case "STARRY": {
                SkyPainter.milkyWay(b, 10466559, 16771280, 0.75f);
                SkyPainter.horizonGlow(b, 3890088, 0.3f, 14.0f);
                break;
            }
            case "MIDNIGHT": {
                SkyPainter.milkyWay(b, 8162559, 14213375, 0.95f);
                SkyPainter.nebula(b, 3819720, 8162559, 0.25f, 41);
                SkyPainter.horizonGlow(b, 2767504, 0.35f, 12.0f);
                break;
            }
            case "PURPLE": 
            case "NEON": 
            case "ROSE": {
                SkyPainter.nebula(b, a1, a2, 0.45f, 3);
                SkyPainter.milkyWay(b, a1, 0xFFFFFF, 0.35f);
                SkyPainter.horizonGlow(b, a1, 0.35f, 12.0f);
                break;
            }
            case "NORTHERN": 
            case "OCEAN": 
            case "ICE": 
            case "CUSTOM": {
                SkyPainter.milkyWay(b, 10466559, a2, 0.4f);
                SkyPainter.horizonGlow(b, a1, 0.3f, 10.0f);
                break;
            }
            case "PILLARS": {
                SkyPainter.nebula(b, a1, a2, 0.4f, 17);
                SkyPainter.horizonGlow(b, a1, 0.7f, 14.0f);
                break;
            }
            case "LAGOON": {
                SkyPainter.nebula(b, a1, a2, 0.25f, 37);
                SkyPainter.horizonGlow(b, a2, 0.4f, 18.0f);
                break;
            }
            case "BLOOD_MOON": {
                SkyPainter.nebula(b, 11540522, 16734780, 0.5f, 23);
                SkyPainter.horizonGlow(b, 16726826, 0.45f, 12.0f);
                break;
            }
            case "ECLIPSE": {
                SkyPainter.milkyWay(b, 9207039, 16766632, 0.45f);
                SkyPainter.nebula(b, a2, a1, 0.25f, 31);
                break;
            }
            case "SUNSET": {
                SkyPainter.rays(b, 16756848, 200.0f, 3.0f, 0.4f);
                SkyPainter.sun(b, 16761466, 200.0f, 3.0f, 3.8f);
                SkyPainter.horizonGlow(b, 16742972, 0.55f, 12.0f);
                SkyPainter.clouds(b, 16752250, 0x5A2A5A, 0.55f, 0.6f);
                break;
            }
            case "SAKURA": {
                SkyPainter.sun(b, 16765158, 60.0f, 4.0f, 3.2f);
                SkyPainter.rays(b, 16762592, 60.0f, 4.0f, 0.35f);
                SkyPainter.horizonGlow(b, 16751819, 0.5f, 14.0f);
                SkyPainter.nebula(b, 16757721, 13144319, 0.25f, 13);
                break;
            }
            case "STORM": 
            case "RAIN": {
                SkyPainter.horizonGlow(b, 4876954, 0.35f, 10.0f);
                break;
            }
            case "WINTER": {
                SkyPainter.sun(b, 16771272, 150.0f, 14.0f, 2.4f);
                SkyPainter.rays(b, 0xFFF0E0, 150.0f, 14.0f, 0.1f);
                break;
            }
            case "CLOUDS": {
                SkyPainter.sun(b, 16774352, 120.0f, 40.0f, 3.0f);
                SkyPainter.horizonGlow(b, 0xFFFFFF, 0.3f, 16.0f);
                break;
            }
        }
        if (!day) {
            SkyPainter.stars(b, rnd, s.stars() * (k.equals("SUNSET") || k.equals("SAKURA") ? 0.6f : 1.25f));
        }
        if (k.equals("ECLIPSE")) {
            SkyPainter.eclipse(b, a1, a2);
        }
        if (s.moon() != 0) {
            SkyPainter.moon(b, s.moon(), 200.0f, 30.0f, k.equals("BLOOD_MOON") ? 4.2f : 3.0f);
        }
        SkyPainter.fadeBelowHorizon(b);
        return b.toEmissive();
    }

    private static void rays(Buf b, int col, float azD, float elD, float strength) {
        double a = Math.toRadians(azD);
        double e = Math.toRadians(elD);
        float cx = (float)(-Math.sin(a) * Math.cos(e));
        float cy = (float)Math.sin(e);
        float cz = (float)(Math.cos(a) * Math.cos(e));
        float tx = (float)(-Math.cos(a));
        float tz = (float)(-Math.sin(a));
        float ux = (float)(Math.sin(a) * Math.sin(e));
        float uy = (float)Math.cos(e);
        float uz = (float)(-Math.cos(a) * Math.sin(e));
        for (int y = 0; y < b.h; ++y) {
            if (b.el[y] < -2.0f) continue;
            for (int x = 0; x < b.w; ++x) {
                float fall;
                float dz;
                float dy;
                float dx = b.dx(x, y);
                float dot = dx * cx + (dy = b.dy(y)) * cy + (dz = b.dz(x, y)) * cz;
                if (dot < 0.2f) continue;
                float th = (float)Math.toDegrees(Math.acos(Math.min(1.0f, dot)));
                float phi = (float)Math.atan2(dx * ux + dy * uy + dz * uz, dx * tx + dz * tz);
                float r = Noise.noise((float)Math.cos(phi) * 9.0f, (float)Math.sin(phi) * 9.0f, 3.3f);
                float ray = SkyPainter.smooth(0.48f, 0.78f, r);
                if (ray * (fall = (float)Math.exp(-th / 34.0f) * SkyPainter.smooth(1.5f, 6.0f, th)) < 0.002f) continue;
                b.add(y * b.w + x, col, ray * fall * strength);
            }
        }
    }

    public static int[] fx(Spec s, int w, int h) {
        Buf b = new Buf(w, h);
        String k = s.kind();
        boolean any = true;
        switch (k) {
            case "PILLARS": {
                SkyPainter.pillars(b, s.a1(), s.a2());
                break;
            }
            case "LAGOON": {
                SkyPainter.caustics(b, s.a1(), s.a2());
                break;
            }
            case "NEBULA": {
                SkyPainter.wisps(b, s.a1(), s.a2());
                break;
            }
            case "CLOUDS": {
                SkyPainter.clouds(b, 0xFFFFFF, 12175842, 0.95f, 0.46f);
                break;
            }
            case "WINTER": {
                SkyPainter.clouds(b, 0xFFFFFF, 13161702, 0.85f, 0.5f);
                break;
            }
            case "SAKURA": {
                SkyPainter.clouds(b, 16769776, 13144248, 0.55f, 0.55f);
                break;
            }
            case "SUNSET": {
                SkyPainter.clouds(b, 16756874, 8010346, 0.6f, 0.56f);
                break;
            }
            case "STORM": {
                SkyPainter.clouds(b, 5923960, 1711656, 0.95f, 0.38f);
                break;
            }
            case "RAIN": {
                SkyPainter.clouds(b, 4871270, 1843760, 0.85f, 0.42f);
                break;
            }
            default: {
                boolean bl = any = s.aurora() && !k.equals("GALAXY");
            }
        }
        if (s.aurora() && !k.equals("GALAXY")) {
            SkyPainter.aurora(b, s.a1(), s.a2(), k.hashCode());
        }
        if (!any) {
            return null;
        }
        SkyPainter.fadeBelowHorizon(b);
        return b.toEmissive();
    }

    private static void stars(Buf b, Random rnd, float density) {
        if (density <= 0.0f) {
            return;
        }
        int n = Math.round(density * 1700.0f);
        for (int i = 0; i < n; ++i) {
            float sinEl = -0.05f + rnd.nextFloat() * 1.05f;
            float elD = (float)Math.toDegrees(Math.asin(Math.min(1.0f, sinEl)));
            float azD = rnd.nextFloat() * 360.0f;
            float mag = (float)Math.pow(rnd.nextFloat(), 3.2);
            int col = rnd.nextFloat() < 0.15f ? 16770760 : (rnd.nextFloat() < 0.3f ? 13162751 : 0xFFFFFF);
            float core = Math.max(252.0f / (float)b.w, 0.045f + 0.06f * mag);
            float bright = 0.22f + 0.6f * mag;
            SkyPainter.star(b, azD, elD, core, col, bright);
            if (!(mag > 0.82f)) continue;
            SkyPainter.star(b, azD, elD, core * 5.5f, col, 0.12f * mag);
        }
    }

    private static void star(Buf b, float azD, float elD, float sigma, int col, float bright) {
        double ca = Math.cos(Math.toRadians(azD));
        double sa = Math.sin(Math.toRadians(azD));
        double ce = Math.cos(Math.toRadians(elD));
        double se = Math.sin(Math.toRadians(elD));
        float sx = (float)(-sa * ce);
        float sy = (float)se;
        float sz = (float)(ca * ce);
        float pxPerDegX = (float)b.w / 360.0f;
        float pxPerDegY = (float)b.h / 98.0f;
        float rad = sigma * 3.0f;
        int cy = Math.round((90.0f - elD) * pxPerDegY);
        int ry = (int)Math.ceil(rad * pxPerDegY) + 1;
        float cosEl = (float)Math.max(0.05, ce);
        int cx = Math.round(azD * pxPerDegX);
        int rx = (int)Math.ceil(rad * pxPerDegX / cosEl) + 1;
        if (rx > b.w / 2) {
            rx = b.w / 2;
        }
        double sig2 = Math.toRadians(sigma);
        sig2 *= sig2;
        for (int y = cy - ry; y <= cy + ry; ++y) {
            if (y < 0 || y >= b.h) continue;
            for (int x = cx - rx; x <= cx + rx; ++x) {
                int xx = Math.floorMod(x, b.w);
                float d = sx * b.dx(xx, y) + sy * b.dy(y) + sz * b.dz(xx, y);
                double ang2 = 2.0f * (1.0f - d);
                float f = (float)Math.exp(-ang2 / (2.0 * sig2));
                if (!(f > 0.003f)) continue;
                b.add(y * b.w + xx, col, f * bright);
            }
        }
    }

    private static void milkyWay(Buf b, int c1, int c2, float strength) {
        float nx = 0.32f;
        float ny = 0.58f;
        float nz = 0.75f;
        float l = (float)Math.sqrt(nx * nx + ny * ny + nz * nz);
        Low f1 = new Low(b, 3, (x, y, z) -> Noise.fbm(x * 3.2f + 11.0f, y * 3.2f, z * 3.2f, 5));
        Low f2 = new Low(b, 2, (x, y, z) -> Noise.fbm(x * 7.0f, y * 7.0f + 5.0f, z * 7.0f, 4));
        float fnx = nx /= l;
        float fny = ny /= l;
        float fnz = nz /= l;
        for (int y2 = 0; y2 < b.h; ++y2) {
            for (int x2 = 0; x2 < b.w; ++x2) {
                float dz;
                float dy;
                float dx = b.dx(x2, y2);
                float d = dx * fnx + (dy = b.dy(y2)) * fny + (dz = b.dz(x2, y2)) * fnz;
                float band = (float)Math.exp(-(d * d) / 0.045f);
                if (band < 0.01f) continue;
                float n1 = f1.get(x2, y2);
                float n2 = f2.get(x2, y2);
                float glow = band * (0.25f + 0.75f * n1);
                float dust = SkyPainter.smooth(0.52f, 0.72f, n2) * band;
                float core = (float)Math.exp(-(d * d) / 0.008f) * SkyPainter.smooth(0.45f, 0.8f, n1);
                int col = SkyPainter.lerp(c1, c2, SkyPainter.smooth(0.3f, 0.85f, n1));
                int i = y2 * b.w + x2;
                b.add(i, col, glow * 0.42f * strength * (1.0f - 0.75f * dust));
                b.add(i, 16774374, core * 0.25f * strength * (1.0f - dust));
                float h = Noise.hash(x2 * 7919 + y2 * 104729);
                if (!(h > 0.985f - band * 0.03f)) continue;
                b.add(i, 0xFFFFFF, 0.5f * band * strength);
            }
        }
    }

    private static void nebula(Buf b, int c1, int c2, float strength, int seed) {
        float o = (float)seed * 3.7f;
        Low f1 = new Low(b, 3, (dx, dy, dz) -> {
            float wx = Noise.fbm(dx * 1.4f + o, dy * 1.4f, dz * 1.4f, 3) - 0.5f;
            float wy = Noise.fbm(dx * 1.4f, dy * 1.4f + o, dz * 1.4f, 3) - 0.5f;
            return Noise.fbm(dx * 1.9f + wx * 1.3f + o, dy * 1.9f + wy * 1.3f, dz * 1.9f, 6);
        });
        Low f2 = new Low(b, 3, (dx, dy, dz) -> Noise.fbm(dx * 4.2f, dy * 4.2f + o, dz * 4.2f + 3.0f, 4));
        for (int y = 0; y < b.h; ++y) {
            if (b.el[y] < -4.0f) continue;
            for (int x = 0; x < b.w; ++x) {
                float n1 = f1.get(x, y);
                float dens = SkyPainter.smooth(0.46f, 0.82f, n1);
                if (dens <= 0.002f) continue;
                float n2 = f2.get(x, y);
                int col = SkyPainter.lerp(c1, c2, SkyPainter.smooth(0.25f, 0.8f, n2));
                int i = y * b.w + x;
                b.over(i, col, dens * 0.62f * strength);
                float core = SkyPainter.smooth(0.72f, 0.95f, n1);
                b.add(i, SkyPainter.lerp(col, 0xFFFFFF, 0.55f), core * 0.35f * strength);
            }
        }
    }

    private static void wisps(Buf b, int c1, int c2) {
        Low f = new Low(b, 2, (dx, dy, dz) -> Noise.fbm(dx * 3.4f + 40.0f, dy * 3.4f, dz * 3.4f, 5));
        for (int y = 0; y < b.h; ++y) {
            if (b.el[y] < -4.0f) continue;
            for (int x = 0; x < b.w; ++x) {
                float n = f.get(x, y);
                float k = SkyPainter.smooth(0.55f, 0.85f, n);
                if (k <= 0.002f) continue;
                b.over(y * b.w + x, SkyPainter.lerp(c2, 0xFFFFFF, 0.25f), k * 0.38f);
            }
        }
    }

    private static void aurora(Buf b, int c1, int c2, int seed) {
        float s = (float)(seed & 0x3FF) * 0.37f;
        float[] base = new float[b.w];
        float[] height = new float[b.w];
        float[] rays = new float[b.w];
        float[] mask = new float[b.w];
        for (int x = 0; x < b.w; ++x) {
            float ca = b.cosA[x];
            float sa = b.sinA[x];
            base[x] = 8.0f + 10.0f * Noise.fbm(ca * 1.6f + s, sa * 1.6f, 0.3f, 3) + 3.5f * (float)Math.sin((double)x * 6.2832 / (double)b.w * 7.0 + (double)s) + 1.5f * (float)Math.sin((double)x * 6.2832 / (double)b.w * 19.0 + (double)(s * 2.0f));
            height[x] = 14.0f + 26.0f * Noise.fbm(ca * 2.3f, sa * 2.3f + s, 1.7f, 3);
            float r1 = Noise.noise(ca * 60.0f + s, sa * 60.0f, 0.5f);
            float r2 = Noise.noise(ca * 160.0f, sa * 160.0f + s, 2.5f);
            rays[x] = (float)Math.pow(0.55f * r1 + 0.45f * r2, 2.2) * 1.8f;
            mask[x] = SkyPainter.smooth(0.3f, 0.72f, Noise.fbm(ca * 1.7f + s * 2.0f, sa * 1.7f, 4.1f, 4));
        }
        for (int y = 0; y < b.h; ++y) {
            float el = b.el[y];
            if (el < -2.0f || el > 75.0f) continue;
            for (int x = 0; x < b.w; ++x) {
                if (mask[x] <= 0.01f) continue;
                float rel = el - base[x];
                float I = rel < 0.0f ? (float)Math.exp(rel / 1.4f) : (float)Math.exp(-rel / height[x]) * (0.35f + 0.65f * Math.min(1.0f, rays[x]));
                if ((I *= mask[x]) <= 0.004f) continue;
                float t = Math.max(0.0f, Math.min(1.0f, rel / (height[x] * 1.4f)));
                int col = SkyPainter.lerp(c1, c2, t);
                int i = y * b.w + x;
                b.over(i, col, Math.min(0.85f, I * 0.75f));
                if (!(rel > -1.0f) || !(rel < 3.0f)) continue;
                b.add(i, SkyPainter.lerp(c1, 0xFFFFFF, 0.5f), I * 0.18f);
            }
        }
    }

    private static void pillars(Buf b, int c1, int c2) {
        Random r = new Random(77L);
        int n = 46;
        float[] az = new float[n];
        float[] wd = new float[n];
        float[] top = new float[n];
        float[] in = new float[n];
        int[] col = new int[n];
        for (int i = 0; i < n; ++i) {
            az[i] = r.nextFloat() * 360.0f;
            wd[i] = 0.18f + r.nextFloat() * 0.7f;
            top[i] = 25.0f + r.nextFloat() * 55.0f;
            in[i] = 0.35f + r.nextFloat() * 0.65f;
            col[i] = SkyPainter.lerp(c1, c2, r.nextFloat());
        }
        float pxPerDeg = (float)b.w / 360.0f;
        for (int i = 0; i < n; ++i) {
            for (int y = 0; y < b.h; ++y) {
                float el = b.el[y];
                if (el < -3.0f || el > top[i]) continue;
                float v = SkyPainter.smooth(-3.0f, 2.0f, el) * (float)Math.pow(1.0f - el / top[i], 1.6);
                float cosEl = Math.max(0.1f, b.cosE[y]);
                float w = wd[i] / cosEl;
                int x0 = (int)((az[i] - w * 6.0f) * pxPerDeg);
                int x1 = (int)((az[i] + w * 6.0f) * pxPerDeg);
                for (int x = x0; x <= x1; ++x) {
                    int xx = Math.floorMod(x, b.w);
                    float dAz = ((float)x / pxPerDeg - az[i]) / w;
                    float core = (float)Math.exp(-dAz * dAz);
                    float glow = (float)Math.exp(-dAz * dAz / 16.0f);
                    int idx = y * b.w + xx;
                    b.add(idx, SkyPainter.lerp(col[i], 0xFFFFFF, 0.55f * core), (core * 0.55f + glow * 0.12f) * v * in[i]);
                }
            }
        }
    }

    private static void caustics(Buf b, int c1, int c2) {
        Low wxF = new Low(b, 2, (dx, dy, dz) -> {
            float k = 3.2f / (Math.max(-0.2f, dy) + 0.3f);
            return (Noise.fbm(dx * k * 0.35f, dz * k * 0.35f, 1.3f, 3) - 0.5f) * 1.6f;
        });
        Low wzF = new Low(b, 2, (dx, dy, dz) -> {
            float k = 3.2f / (Math.max(-0.2f, dy) + 0.3f);
            return (Noise.fbm(dx * k * 0.35f + 9.0f, dz * k * 0.35f, 4.7f, 3) - 0.5f) * 1.6f;
        });
        for (int y = 0; y < b.h; ++y) {
            float el = b.el[y];
            if (el < 1.0f) continue;
            float fade = SkyPainter.smooth(1.0f, 14.0f, el);
            for (int x = 0; x < b.w; ++x) {
                float dx2 = b.dx(x, y);
                float dy2 = b.dy(y);
                float dz2 = b.dz(x, y);
                float k = 3.2f / (dy2 + 0.3f);
                float u = dx2 * k + wxF.get(x, y);
                float v = dz2 * k + wzF.get(x, y);
                float e1 = SkyPainter.cellEdge(u * 1.15f, v * 1.15f);
                float e2 = SkyPainter.cellEdge(u * 2.3f + 7.1f, v * 2.3f + 3.3f);
                float line = (float)Math.pow(1.0f - SkyPainter.smooth(0.0f, 0.16f, e1), 2.2) + 0.45f * (float)Math.pow(1.0f - SkyPainter.smooth(0.0f, 0.12f, e2), 2.5);
                float I = Math.min(1.2f, line) * fade;
                int i = y * b.w + x;
                b.add(i, c1, 0.1f * fade);
                if (!(I > 0.004f)) continue;
                b.add(i, SkyPainter.lerp(c1, c2, Math.min(1.0f, I)), I * 0.55f);
            }
        }
    }

    private static float cellEdge(float u, float v) {
        int xi = (int)Math.floor(u);
        int yi = (int)Math.floor(v);
        float f1 = 9.0f;
        float f2 = 9.0f;
        for (int j = -1; j <= 1; ++j) {
            for (int i = -1; i <= 1; ++i) {
                float py;
                int cx = xi + i;
                int cy = yi + j;
                float px = (float)cx + 0.15f + 0.7f * Noise.hash(cx * 374761 + cy * 668265);
                float d = (float)Math.sqrt((px - u) * (px - u) + ((py = (float)cy + 0.15f + 0.7f * Noise.hash(cx * 912367 + cy * 177823 + 5)) - v) * (py - v));
                if (d < f1) {
                    f2 = f1;
                    f1 = d;
                    continue;
                }
                if (!(d < f2)) continue;
                f2 = d;
            }
        }
        return f2 - f1;
    }

    private static void clouds(Buf b, int light, int shade, float cover, float thr) {
        Low fn = new Low(b, 2, (dx, dy, dz) -> {
            if (dy < -0.03f) {
                return 0.0f;
            }
            float k = 1.8f / (dy + 0.1f);
            return Noise.fbm(dx * k * 0.42f, dz * k * 0.42f, 0.7f, 6);
        });
        Low fl = new Low(b, 3, (dx, dy, dz) -> {
            if (dy < -0.03f) {
                return 0.0f;
            }
            float k = 1.8f / (dy + 0.1f);
            return Noise.fbm(dx * k * 0.42f + 0.18f, dz * k * 0.42f + 0.18f, 0.7f, 4);
        });
        for (int y = 0; y < b.h; ++y) {
            float dy2 = b.dy(y);
            if (dy2 < -0.02f) continue;
            float fade = SkyPainter.smooth(-0.02f, 0.12f, dy2);
            for (int x = 0; x < b.w; ++x) {
                float n = fn.get(x, y);
                float d = SkyPainter.smooth(thr, thr + 0.28f, n);
                if (d <= 0.002f) continue;
                float lit = fl.get(x, y);
                float sh = SkyPainter.smooth(-0.08f, 0.12f, n - lit);
                int col = SkyPainter.lerp(light, shade, 0.25f + 0.6f * sh);
                b.over(y * b.w + x, col, d * cover * fade);
            }
        }
    }

    private static void eclipse(Buf b, int c1, int c2) {
        float azC = 165.0f;
        float elC = 24.0f;
        float R = 7.5f;
        double a = Math.toRadians(azC);
        double e = Math.toRadians(elC);
        float cx = (float)(-Math.sin(a) * Math.cos(e));
        float cy = (float)Math.sin(e);
        float cz = (float)(Math.cos(a) * Math.cos(e));
        float tx = (float)(-Math.cos(a));
        float tz = (float)(-Math.sin(a));
        float ux = (float)(Math.sin(a) * Math.sin(e));
        float uy = (float)Math.cos(e);
        float uz = (float)(-Math.cos(a) * Math.sin(e));
        float tilt = (float)Math.toRadians(-11.0);
        float ct = (float)Math.cos(tilt);
        float st = (float)Math.sin(tilt);
        for (int y = 0; y < b.h; ++y) {
            if (Math.abs(b.el[y] - elC) > R * 6.0f) continue;
            for (int x = 0; x < b.w; ++x) {
                float dz;
                float dy;
                float dx = b.dx(x, y);
                float dot = dx * cx + (dy = b.dy(y)) * cy + (dz = b.dz(x, y)) * cz;
                if (dot < 0.75f) continue;
                float th = (float)Math.toDegrees(Math.acos(Math.min(1.0f, dot)));
                float lx = (float)Math.toDegrees(Math.asin(Math.max(-1.0f, Math.min(1.0f, dx * tx + dz * tz))));
                float lyy = (float)Math.toDegrees(Math.asin(Math.max(-1.0f, Math.min(1.0f, dx * ux + dy * uy + dz * uz))));
                float rx = lx * ct - lyy * st;
                float ry = lx * st + lyy * ct;
                float squash = 0.27f;
                float re = (float)Math.sqrt(rx * rx + ry / squash * (ry / squash));
                float ang = (float)Math.atan2(ry / squash, rx);
                float diskBand = SkyPainter.smooth(R * 1.25f, R * 1.6f, re) * (1.0f - SkyPainter.smooth(R * 2.4f, R * 3.8f, re));
                float streak = 0.6f + 0.4f * Noise.noise(re * 3.1f, ang * 3.0f, 1.1f);
                float doppler = 0.4f + 0.6f * (0.5f + 0.5f * (float)Math.cos(ang - 0.4f));
                float disk = diskBand * streak * doppler;
                int i = y * b.w + x;
                float glow = th > R ? (float)Math.exp(-(th - R) / 3.2f) : 1.0f;
                b.add(i, c1, glow * 0.22f);
                boolean front = ry < 0.0f;
                int dcol = SkyPainter.lerp(c2, SkyPainter.lerp(c1, 0xFFFFFF, 0.6f), doppler);
                if (!front) {
                    b.add(i, dcol, disk * 0.85f);
                }
                float arc = (float)Math.exp(-Math.pow((th - R * 1.32f) / 0.32f, 2.0)) * (0.55f + 0.45f * Math.max(0.0f, lyy / (th + 0.001f)));
                b.add(i, SkyPainter.lerp(c1, 0xFFFFFF, 0.5f), arc * 0.55f);
                float core = 1.0f - SkyPainter.smooth(R - 0.08f, R + 0.04f, th);
                if (core > 0.0f) {
                    b.over(i, 131332, core);
                }
                float ring = (float)Math.exp(-Math.pow((th - R * 1.03f) / 0.1f, 2.0));
                b.add(i, SkyPainter.lerp(c1, 0xFFFFFF, 0.8f), ring * 0.95f);
                if (!front) continue;
                b.add(i, dcol, disk * 0.95f);
            }
        }
    }

    private static void moon(Buf b, int col, float azD, float elD, float R) {
        double a = Math.toRadians(azD);
        double e = Math.toRadians(elD);
        float cx = (float)(-Math.sin(a) * Math.cos(e));
        float cy = (float)Math.sin(e);
        float cz = (float)(Math.cos(a) * Math.cos(e));
        for (int y = 0; y < b.h; ++y) {
            if (Math.abs(b.el[y] - elD) > R * 9.0f) continue;
            for (int x = 0; x < b.w; ++x) {
                float dz;
                float dy;
                float dx = b.dx(x, y);
                float dot = dx * cx + (dy = b.dy(y)) * cy + (dz = b.dz(x, y)) * cz;
                if (dot < 0.97f) continue;
                float th = (float)Math.toDegrees(Math.acos(Math.min(1.0f, dot)));
                int i = y * b.w + x;
                b.add(i, col, (float)Math.exp(-th / (R * 1.6f)) * 0.22f);
                float disc = 1.0f - SkyPainter.smooth(R - 0.06f, R + 0.06f, th);
                if (!(disc > 0.0f)) continue;
                float cr = Noise.fbm(dx * 90.0f, dy * 90.0f, dz * 90.0f, 4);
                int mc = SkyPainter.lerp(col, SkyPainter.lerp(col, 0, 0.35f), SkyPainter.smooth(0.5f, 0.75f, cr));
                b.over(i, mc, disc);
            }
        }
    }

    private static void sun(Buf b, int col, float azD, float elD, float R) {
        double a = Math.toRadians(azD);
        double e = Math.toRadians(elD);
        float cx = (float)(-Math.sin(a) * Math.cos(e));
        float cy = (float)Math.sin(e);
        float cz = (float)(Math.cos(a) * Math.cos(e));
        for (int y = 0; y < b.h; ++y) {
            for (int x = 0; x < b.w; ++x) {
                float dot = b.dx(x, y) * cx + b.dy(y) * cy + b.dz(x, y) * cz;
                if (dot < 0.5f) continue;
                float th = (float)Math.toDegrees(Math.acos(Math.min(1.0f, dot)));
                int i = y * b.w + x;
                b.add(i, col, (float)Math.exp(-th / 18.0f) * 0.35f);
                b.add(i, 16773848, (1.0f - SkyPainter.smooth(R - 0.2f, R + 0.2f, th)) * 0.95f);
            }
        }
    }

    private static void horizonGlow(Buf b, int col, float strength, float height) {
        for (int y = 0; y < b.h; ++y) {
            float el = b.el[y];
            if (el < -4.0f || el > height * 2.0f) continue;
            float f = SkyPainter.smooth(-4.0f, 0.0f, el) * (float)Math.exp(-Math.max(0.0f, el) / (height * 0.45f));
            for (int x = 0; x < b.w; ++x) {
                b.add(y * b.w + x, col, f * strength);
            }
        }
    }

    private static void fadeBelowHorizon(Buf b) {
        for (int y = 0; y < b.h; ++y) {
            float f = SkyPainter.smooth(-7.0f, 1.0f, b.el[y]);
            if (f >= 0.999f) continue;
            for (int x = 0; x < b.w; ++x) {
                int i;
                int n = i = y * b.w + x;
                b.r[n] = b.r[n] * f;
                int n2 = i;
                b.g[n2] = b.g[n2] * f;
                int n3 = i;
                b.b[n3] = b.b[n3] * f;
                int n4 = i;
                b.a[n4] = b.a[n4] * f;
            }
        }
    }

    public static int[] sprites() {
        int S = 64;
        int W = S * 4;
        int[] out = new int[W * S];
        for (int y = 0; y < S; ++y) {
            for (int x = 0; x < S; ++x) {
                float u = ((float)x + 0.5f) / (float)S * 2.0f - 1.0f;
                float v = ((float)y + 0.5f) / (float)S * 2.0f - 1.0f;
                float t = (v + 1.0f) / 2.0f;
                float width = (float)(0.78 * Math.pow(Math.sin(Math.PI * (double)Math.min(1.0f, t * 0.92f + 0.04f)), 0.55) * (1.0 - 0.62 * (double)t * (double)t));
                float edge = width - Math.abs(u);
                float notch = (0.11f - t) * 3.4f - Math.abs(u) * 3.6f;
                float a0 = SkyPainter.smooth(0.0f, 0.07f, edge) * (1.0f - SkyPainter.smooth(-0.05f, 0.08f, notch));
                float center = 1.0f - Math.min(1.0f, Math.abs(u) / Math.max(0.05f, width));
                float vein = (float)Math.exp(-u * u * 160.0f) * 0.25f * t;
                int c0 = SkyPainter.lerp(16095940, 0xFFF0F6, 0.25f + 0.35f * center + 0.3f * t + vein * 0.5f);
                out[y * W + x] = SkyPainter.argb(c0, a0 * 0.97f);
                float r = (float)Math.sqrt(u * u + v * v);
                out[y * W + S + x] = SkyPainter.argb(0xFFFFFF, 1.0f - SkyPainter.smooth(0.15f, 1.0f, r));
                float g = (float)Math.exp(-r * r * 9.0f) * 0.85f + (float)Math.exp(-r * r * 60.0f) * 0.6f;
                out[y * W + 2 * S + x] = SkyPainter.argb(0xFFFFFF, Math.min(1.0f, g));
                float sx = 1.0f - SkyPainter.smooth(0.05f, 0.35f, Math.abs(u));
                float sv = SkyPainter.smooth(-1.0f, 0.6f, v) * (1.0f - SkyPainter.smooth(0.85f, 1.0f, v));
                out[y * W + 3 * S + x] = SkyPainter.argb(0xFFFFFF, sx * sv);
            }
        }
        return out;
    }

    private static int argb(int rgb, float a) {
        return SkyPainter.c255(a) << 24 | rgb & 0xFFFFFF;
    }

    public static int[] smooth(int[] em, int w, int h, boolean soft) {
        int[] a = (int[])em.clone();
        int[] tmp = new int[w];
        int[] r = new int[w];
        int[] g = new int[w];
        int[] b = new int[w];
        for (int y = 0; y < h; ++y) {
            float el = 90.0f - ((float)y + 0.5f) / (float)h * 98.0f;
            double ce = Math.max(0.015, Math.cos(Math.toRadians(el)));
            int rad = (int)Math.min(64.0, Math.floor(0.55 / ce)) + (soft ? 1 : 0);
            if (rad <= 0) continue;
            int off = y * w;
            for (int x = 0; x < w; ++x) {
                int c = a[off + x];
                r[x] = c >> 16 & 0xFF;
                g[x] = c >> 8 & 0xFF;
                b[x] = c & 0xFF;
            }
            int n = rad * 2 + 1;
            int sr = 0;
            int sg = 0;
            int sb = 0;
            for (int k = -rad; k <= rad; ++k) {
                int xx = Math.floorMod(k, w);
                sr += r[xx];
                sg += g[xx];
                sb += b[xx];
            }
            for (int x = 0; x < w; ++x) {
                tmp[x] = 0xFF000000 | sr / n << 16 | sg / n << 8 | sb / n;
                int out = Math.floorMod(x - rad, w);
                int in = Math.floorMod(x + rad + 1, w);
                sr += r[in] - r[out];
                sg += g[in] - g[out];
                sb += b[in] - b[out];
            }
            System.arraycopy(tmp, 0, a, off, w);
        }
        if (soft) {
            int[] o = (int[])a.clone();
            for (int y = 1; y < h - 1; ++y) {
                for (int x = 0; x < w; ++x) {
                    int c0 = o[(y - 1) * w + x];
                    int c1 = o[y * w + x];
                    int c2 = o[(y + 1) * w + x];
                    int rr = (c0 >> 16 & 0xFF) + 2 * (c1 >> 16 & 0xFF) + (c2 >> 16 & 0xFF) >> 2;
                    int gg = (c0 >> 8 & 0xFF) + 2 * (c1 >> 8 & 0xFF) + (c2 >> 8 & 0xFF) >> 2;
                    int bb = (c0 & 0xFF) + 2 * (c1 & 0xFF) + (c2 & 0xFF) >> 2;
                    a[y * w + x] = 0xFF000000 | rr << 16 | gg << 8 | bb;
                }
            }
        }
        return a;
    }

    private static int rowColor(Spec s, float el) {
        float t = Math.max(0.0f, Math.min(1.0f, (el + 2.0f) / 82.0f));
        t = (float)Math.pow(t, 0.65);
        int mid = SkyPainter.lerp(SkyPainter.lerp(s.horizon(), s.top(), 0.5f), s.a1(), 0.1f);
        int c = t < 0.45f ? SkyPainter.lerp(s.horizon(), mid, t / 0.45f) : SkyPainter.lerp(mid, s.top(), SkyPainter.smooth(0.0f, 1.0f, (t - 0.45f) / 0.55f));
        return c;
    }

    public static int[] opaqueBase(int[] em, Spec s, int w, int h) {
        int[] out = new int[em.length];
        for (int y = 0; y < h; ++y) {
            float el = 90.0f - ((float)y + 0.5f) / (float)h * 98.0f;
            int g = SkyPainter.rowColor(s, el);
            float al = SkyPainter.smooth(-3.0f, 3.0f, el);
            int A = Math.round(al * 255.0f);
            if (A == 0) continue;
            int gr = g >> 16 & 0xFF;
            int gg = g >> 8 & 0xFF;
            int gb = g & 0xFF;
            for (int x = 0; x < w; ++x) {
                int c = em[y * w + x];
                int r = Math.min(255, gr + (c >> 16 & 0xFF));
                int gn = Math.min(255, gg + (c >> 8 & 0xFF));
                int b = Math.min(255, gb + (c & 0xFF));
                out[y * w + x] = A << 24 | r << 16 | gn << 8 | b;
            }
        }
        return out;
    }

    public static int[] toBlendOver(int[] em, Spec s, int w, int h) {
        int[] out = new int[em.length];
        float MIN = 0.12f;
        for (int y = 0; y < h; ++y) {
            float el = 90.0f - ((float)y + 0.5f) / (float)h * 98.0f;
            int g = SkyPainter.rowColor(s, el);
            float gr = (float)(g >> 16 & 0xFF) / 255.0f;
            float gg = (float)(g >> 8 & 0xFF) / 255.0f;
            float gb = (float)(g & 0xFF) / 255.0f;
            for (int x = 0; x < w; ++x) {
                int i = y * w + x;
                int c = em[i];
                float r = (float)(c >> 16 & 0xFF) / 255.0f;
                float gn = (float)(c >> 8 & 0xFF) / 255.0f;
                float b = (float)(c & 0xFF) / 255.0f;
                float m = Math.max(r, Math.max(gn, b));
                if (m < 0.005882353f) continue;
                float a = Math.max(0.12f, m);
                a = Math.max(a, Math.max(r / Math.max(0.02f, 1.0f - gr), Math.max(gn / Math.max(0.02f, 1.0f - gg), b / Math.max(0.02f, 1.0f - gb))));
                a = Math.min(1.0f, a);
                int rr = SkyPainter.c255(r / a + gr);
                int g2 = SkyPainter.c255(gn / a + gg);
                int bb = SkyPainter.c255(b / a + gb);
                out[i] = SkyPainter.c255(a) << 24 | rr << 16 | g2 << 8 | bb;
            }
        }
        return out;
    }

    public static int[] view(Spec s, int[] base, int[] fx, int pw, int ph, int w, int h, float yaw, float pitch, float fovDeg, int rad) {
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
        Random rr = new Random(s.kind().hashCode());
        double ph0 = rr.nextDouble() * 10.0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                double tree;
                double sx = (double)x + 0.5 - (double)w / 2.0;
                double sy = -((double)y + 0.5 - (double)h / 2.0);
                double dx = fx0 * f + rx * sx + ux * sy;
                double dy = fy0 * f + ry * sx + uy * sy;
                double dz = fz0 * f + rz * sx + uz * sy;
                double l = Math.sqrt(dx * dx + dy * dy + dz * dz);
                dx /= l;
                dz /= l;
                double el = Math.toDegrees(Math.asin(dy /= l));
                double az = Math.toDegrees(Math.atan2(-dx, dz));
                if (az < 0.0) {
                    az += 360.0;
                }
                int sky = SkyPainter.rowColor(s, (float)el);
                float r = (float)(sky >> 16 & 0xFF) / 255.0f;
                float g = (float)(sky >> 8 & 0xFF) / 255.0f;
                float bl = (float)(sky & 0xFF) / 255.0f;
                float u = (float)(az / 360.0 * (double)pw);
                float v = (float)((90.0 - el) / 98.0 * (double)ph);
                for (int[] layer : new int[][]{base, fx}) {
                    if (layer == null) continue;
                    int c = SkyPainter.sample(layer, pw, ph, u, v);
                    r = Math.min(1.0f, r + (float)(c >> 16 & 0xFF) / 255.0f);
                    g = Math.min(1.0f, g + (float)(c >> 8 & 0xFF) / 255.0f);
                    bl = Math.min(1.0f, bl + (float)(c & 0xFF) / 255.0f);
                }
                double uu = az / 360.0 * 6.2832;
                double hill = 1.5 + 2.2 * Math.sin(uu * 3.0 + ph0) + 1.2 * Math.sin(uu * 7.0 + ph0 * 2.0);
                if (el < hill + (tree = Math.max(0.0, Math.sin(uu * 95.0 + ph0)) * Math.max(0.0, Math.sin(uu * 37.0 + ph0 * 3.0)) * 4.5)) {
                    r = 0.024f;
                    g = 0.016f;
                    bl = 0.04f;
                }
                float dk = Math.max(0.0f, ((float)y - (float)h * 0.6f) / ((float)h * 0.4f));
                dk = dk * dk * 0.55f;
                float ca = SkyPainter.cornerAlpha(x, y, w, h, rad);
                out[y * w + x] = SkyPainter.c255(ca) << 24 | SkyPainter.c255(r *= 1.0f - dk) << 16 | SkyPainter.c255(g *= 1.0f - dk) << 8 | SkyPainter.c255(bl *= 1.0f - dk);
            }
        }
        return out;
    }

    private static int sample(int[] img, int w, int h, float u, float v) {
        int x0 = (int)Math.floor(u -= 0.5f);
        int y0 = (int)Math.floor(v -= 0.5f);
        float fx = u - (float)x0;
        float fy = v - (float)y0;
        int c00 = SkyPainter.px(img, w, h, x0, y0);
        int c10 = SkyPainter.px(img, w, h, x0 + 1, y0);
        int c01 = SkyPainter.px(img, w, h, x0, y0 + 1);
        int c11 = SkyPainter.px(img, w, h, x0 + 1, y0 + 1);
        int out = 0;
        for (int sh = 0; sh <= 24; sh += 8) {
            float a = (float)(c00 >>> sh & 0xFF) * (1.0f - fx) + (float)(c10 >>> sh & 0xFF) * fx;
            float b2 = (float)(c01 >>> sh & 0xFF) * (1.0f - fx) + (float)(c11 >>> sh & 0xFF) * fx;
            out |= Math.round(a * (1.0f - fy) + b2 * fy) << sh;
        }
        return out;
    }

    private static int px(int[] img, int w, int h, int x, int y) {
        x = Math.floorMod(x, w);
        y = Math.max(0, Math.min(h - 1, y));
        return img[y * w + x];
    }

    private static float cornerAlpha(int x, int y, int w, int h, int rad) {
        if (rad <= 0) {
            return 1.0f;
        }
        float cx = x < rad ? (float)rad : (x >= w - rad ? (float)(w - rad) : -1.0f);
        float cy = y < rad ? (float)rad : (y >= h - rad ? (float)(h - rad) : -1.0f);
        if (cx < 0.0f || cy < 0.0f) {
            return 1.0f;
        }
        float d = (float)Math.hypot((float)x + 0.5f - cx, (float)y + 0.5f - cy);
        return Math.max(0.0f, Math.min(1.0f, (float)rad - d + 0.5f));
    }

    static float smooth(float e0, float e1, float x) {
        float t = Math.max(0.0f, Math.min(1.0f, (x - e0) / (e1 - e0)));
        return t * t * (3.0f - 2.0f * t);
    }

    static int lerp(int c1, int c2, float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        int r = (int)((float)(c1 >> 16 & 0xFF) + (float)((c2 >> 16 & 0xFF) - (c1 >> 16 & 0xFF)) * t);
        int g = (int)((float)(c1 >> 8 & 0xFF) + (float)((c2 >> 8 & 0xFF) - (c1 >> 8 & 0xFF)) * t);
        int b = (int)((float)(c1 & 0xFF) + (float)((c2 & 0xFF) - (c1 & 0xFF)) * t);
        return r << 16 | g << 8 | b;
    }

    private static final class Buf {
        final int w;
        final int h;
        final float[] r;
        final float[] g;
        final float[] b;
        final float[] a;
        final float[] el;
        final float[] sinE;
        final float[] cosE;
        final float[] sinA;
        final float[] cosA;

        Buf(int w, int h) {
            this.w = w;
            this.h = h;
            this.r = new float[w * h];
            this.g = new float[w * h];
            this.b = new float[w * h];
            this.a = new float[w * h];
            this.el = new float[h];
            this.sinE = new float[h];
            this.cosE = new float[h];
            for (int y = 0; y < h; ++y) {
                this.el[y] = 90.0f - ((float)y + 0.5f) / (float)h * 98.0f;
                double e = Math.toRadians(this.el[y]);
                this.sinE[y] = (float)Math.sin(e);
                this.cosE[y] = (float)Math.cos(e);
            }
            this.sinA = new float[w];
            this.cosA = new float[w];
            for (int x = 0; x < w; ++x) {
                double az = Math.toRadians(((double)x + 0.5) / (double)w * 360.0);
                this.sinA[x] = (float)Math.sin(az);
                this.cosA[x] = (float)Math.cos(az);
            }
        }

        float dx(int x, int y) {
            return -this.sinA[x] * this.cosE[y];
        }

        float dy(int y) {
            return this.sinE[y];
        }

        float dz(int x, int y) {
            return this.cosA[x] * this.cosE[y];
        }

        void over(int i, int rgb, float al) {
            if (al <= 5.0E-4f) {
                return;
            }
            if (al > 1.0f) {
                al = 1.0f;
            }
            float k = 1.0f - al;
            this.r[i] = (float)(rgb >> 16 & 0xFF) / 255.0f * al + this.r[i] * k;
            this.g[i] = (float)(rgb >> 8 & 0xFF) / 255.0f * al + this.g[i] * k;
            this.b[i] = (float)(rgb & 0xFF) / 255.0f * al + this.b[i] * k;
            this.a[i] = al + this.a[i] * k;
        }

        void add(int i, int rgb, float al) {
            if (al <= 5.0E-4f) {
                return;
            }
            this.r[i] = Math.min(1.0f, this.r[i] + (float)(rgb >> 16 & 0xFF) / 255.0f * al);
            this.g[i] = Math.min(1.0f, this.g[i] + (float)(rgb >> 8 & 0xFF) / 255.0f * al);
            this.b[i] = Math.min(1.0f, this.b[i] + (float)(rgb & 0xFF) / 255.0f * al);
            this.a[i] = Math.min(1.0f, this.a[i] + al * 0.85f);
        }

        int[] toEmissive() {
            int[] out = new int[this.w * this.h];
            for (int i = 0; i < out.length; ++i) {
                out[i] = 0xFF000000 | SkyPainter.c255(this.r[i]) << 16 | SkyPainter.c255(this.g[i]) << 8 | SkyPainter.c255(this.b[i]);
            }
            return out;
        }

        int[] toArgb() {
            int[] out = new int[this.w * this.h];
            for (int i = 0; i < out.length; ++i) {
                float al = this.a[i];
                if (al <= 0.002f) {
                    out[i] = 0;
                    continue;
                }
                int rr = SkyPainter.c255(this.r[i] / al);
                int gg = SkyPainter.c255(this.g[i] / al);
                int bb = SkyPainter.c255(this.b[i] / al);
                out[i] = SkyPainter.c255(al) << 24 | rr << 16 | gg << 8 | bb;
            }
            return out;
        }
    }

    public record Spec(String kind, int top, int horizon, int a1, int a2, float stars, boolean aurora, int moon, String weather) {
    }

    static final class Noise {
        private Noise() {
        }

        static float hash(int n) {
            n = n << 13 ^ n;
            n = n * (n * n * 15731 + 789221) + 1376312589;
            return (float)(n & Integer.MAX_VALUE) / 2.1474836E9f;
        }

        private static float h3(int x, int y, int z) {
            return Noise.hash(x * 73856093 ^ y * 19349663 ^ z * 83492791);
        }

        static float noise(float x, float y, float z) {
            int xi = (int)Math.floor(x);
            int yi = (int)Math.floor(y);
            int zi = (int)Math.floor(z);
            float xf = x - (float)xi;
            float yf = y - (float)yi;
            float zf = z - (float)zi;
            float u = Noise.fade(xf);
            float v = Noise.fade(yf);
            float w = Noise.fade(zf);
            float c000 = Noise.h3(xi, yi, zi);
            float c100 = Noise.h3(xi + 1, yi, zi);
            float c010 = Noise.h3(xi, yi + 1, zi);
            float c110 = Noise.h3(xi + 1, yi + 1, zi);
            float c001 = Noise.h3(xi, yi, zi + 1);
            float c101 = Noise.h3(xi + 1, yi, zi + 1);
            float c011 = Noise.h3(xi, yi + 1, zi + 1);
            float c111 = Noise.h3(xi + 1, yi + 1, zi + 1);
            float x00 = c000 + (c100 - c000) * u;
            float x10 = c010 + (c110 - c010) * u;
            float x01 = c001 + (c101 - c001) * u;
            float x11 = c011 + (c111 - c011) * u;
            float y0 = x00 + (x10 - x00) * v;
            float y1 = x01 + (x11 - x01) * v;
            return y0 + (y1 - y0) * w;
        }

        static float fbm(float x, float y, float z, int oct) {
            float sum = 0.0f;
            float amp = 0.5f;
            float norm = 0.0f;
            for (int i = 0; i < oct; ++i) {
                sum += Noise.noise(x, y, z) * amp;
                norm += amp;
                x = x * 2.03f + 17.1f;
                y = y * 2.03f + 3.7f;
                z = z * 2.03f + 9.3f;
                amp *= 0.5f;
            }
            return sum / norm;
        }

        private static float fade(float t) {
            return t * t * t * (t * (t * 6.0f - 15.0f) + 10.0f);
        }
    }

    private static final class Low {
        final int lw;
        final int lh;
        final int div;
        final float[] v;
        final Buf b;

        Low(Buf b, int div, Field f) {
            this.b = b;
            this.div = div;
            this.lw = Math.max(4, b.w / div);
            this.lh = Math.max(2, b.h / div + 1);
            this.v = new float[this.lw * this.lh];
            for (int y = 0; y < this.lh; ++y) {
                float el = 90.0f - ((float)(y * div) + (float)div * 0.5f) / (float)b.h * 98.0f;
                double e = Math.toRadians(el);
                float ce = (float)Math.cos(e);
                float se = (float)Math.sin(e);
                for (int x = 0; x < this.lw; ++x) {
                    double az = Math.toRadians(((double)x + 0.5) / (double)this.lw * 360.0);
                    this.v[y * this.lw + x] = f.at((float)(-Math.sin(az) * (double)ce), se, (float)(Math.cos(az) * (double)ce));
                }
            }
        }

        float get(int x, int y) {
            float fx = ((float)x + 0.5f) / (float)this.div - 0.5f;
            float fy = ((float)y + 0.5f) / (float)this.div - 0.5f;
            int x0 = (int)Math.floor(fx);
            int y0 = (int)Math.floor(fy);
            float tx = fx - (float)x0;
            float ty = fy - (float)y0;
            int xa = Math.floorMod(x0, this.lw);
            int xb = Math.floorMod(x0 + 1, this.lw);
            int ya = Math.max(0, Math.min(this.lh - 1, y0));
            int yb = Math.max(0, Math.min(this.lh - 1, y0 + 1));
            float a = this.v[ya * this.lw + xa] + (this.v[ya * this.lw + xb] - this.v[ya * this.lw + xa]) * tx;
            float c = this.v[yb * this.lw + xa] + (this.v[yb * this.lw + xb] - this.v[yb * this.lw + xa]) * tx;
            return a + (c - a) * ty;
        }
    }

    private static interface Field {
        public float at(float var1, float var2, float var3);
    }
}

