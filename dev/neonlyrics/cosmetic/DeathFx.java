/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_12249
 *  net.minecraft.class_1297
 *  net.minecraft.class_1922
 *  net.minecraft.class_1944
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 *  net.minecraft.class_2680
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4608
 *  net.minecraft.class_742
 *  net.minecraft.class_765
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package dev.neonlyrics.cosmetic;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.cosmetic.Accessories;
import dev.neonlyrics.cosmetic.DeathCards;
import dev.neonlyrics.cosmetic.DeathHard;
import dev.neonlyrics.sky.SkyTextures;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.class_12249;
import net.minecraft.class_1297;
import net.minecraft.class_1922;
import net.minecraft.class_1944;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_742;
import net.minecraft.class_765;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public final class DeathFx {
    public static final int COUNT = 4;
    static final float TE = 1.62f;
    static final float TI = 1.9f;
    static final float TF = 2.06f;
    static final float TP = 2.32f;
    static final float T_END = 4.75f;
    static final float[] TT;
    static final float HX = 1.7f;
    static final float HZ = 0.85f;
    static final float BX = 0.0f;
    static final float BY = 3.3f;
    static final float BZ = 0.0f;
    static final float STAND = 1.055f;
    static final float[] PX;
    static final float[] PY;
    static final float[] PZ;
    static final float[] PS;
    static final float SB;
    static final float SH;
    static final float SEND;
    static final float DX;
    static final float DY;
    static final float DZ;
    static final float TC;
    static final float[] TA;
    static final float[] TB;
    static final List<Fx> ACTIVE;
    static final Set<Integer> TRIGGERED;
    static final Set<Integer> SEEN;
    static final Map<Integer, Long> HITS;
    static final long KILL_WINDOW_MS = 6000L;
    static final float PXS = 0.05859375f;
    static final Pose POSE;
    static final float[] Q;
    static final float[] Q2;
    static final float[] AX;
    static final float[] GP;

    private DeathFx() {
    }

    static void pathPoint(float s, float[] out) {
        int n = PS.length;
        if (s <= 0.0f) {
            out[0] = PX[0];
            out[1] = PY[0] + s;
            out[2] = PZ[0];
            return;
        }
        if (s >= PS[n - 1]) {
            float e = s - PS[n - 1];
            out[0] = PX[n - 1] + DX * e;
            out[1] = PY[n - 1] + DY * e;
            out[2] = PZ[n - 1] + DZ * e;
            return;
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            int mid = lo + hi >>> 1;
            if (PS[mid] <= s) {
                lo = mid;
                continue;
            }
            hi = mid;
        }
        float k = (s - PS[lo]) / Math.max(1.0E-6f, PS[hi] - PS[lo]);
        out[0] = PX[lo] + (PX[hi] - PX[lo]) * k;
        out[1] = PY[lo] + (PY[hi] - PY[lo]) * k;
        out[2] = PZ[lo] + (PZ[hi] - PZ[lo]) * k;
    }

    static float sTip(float t) {
        if (t < 1.62f) {
            return SH - 1.3f;
        }
        if (t < 1.9f) {
            return DeathFx.lerp(SH - 1.3f, SB - 0.95f, (float)Math.pow((t - 1.62f) / 0.27999997f, 0.85));
        }
        if (t < 2.06f) {
            return SB - 0.95f + 1.35f * DeathFx.eo((t - 1.9f) / 0.15999997f);
        }
        if (t < 2.32f) {
            return SEND + 0.025f * (float)Math.sin(t * 70.0f) * (1.0f - DeathFx.sm(2.1f, 2.32f, t));
        }
        return SEND - DeathFx.ei((t - 2.32f) / 0.75f) * (SEND - (SH - 7.0f));
    }

    static float sBody(float t) {
        return DeathFx.sTip(t) - (SEND - SB);
    }

    static float wave(float s, float t) {
        float st = DeathFx.sTip(t);
        float a = 0.42f * (1.0f - DeathFx.sm(1.62f, 2.02f, t)) + 0.04f * DeathFx.sm(1.9f, 2.0f, t) * (1.0f - DeathFx.sm(2.25f, 2.32f, t)) + 0.13f * DeathFx.sm(2.32f, 2.5f, t) * (1.0f - DeathFx.sm(2.95f, 3.05f, t));
        a *= DeathFx.sm(SH - 0.2f, SH + 1.3f, s) * DeathFx.sm(0.0f, 1.6f, st - s + 0.6f);
        if (t > 1.9f) {
            a *= DeathFx.sm(0.0f, 1.1f, Math.abs(s - DeathFx.sBody(t)));
        }
        return a;
    }

    static void chainPoint(float s, float t, float[] out) {
        DeathFx.pathPoint(s, out);
        float a = DeathFx.wave(s, t);
        if (a < 1.0E-4f) {
            return;
        }
        DeathFx.pathPoint(s - 0.05f, TA);
        DeathFx.pathPoint(s + 0.05f, TB);
        float tx = TB[0] - TA[0];
        float ty = TB[1] - TA[1];
        float tz = TB[2] - TA[2];
        float tl = (float)Math.sqrt(tx * tx + ty * ty + tz * tz);
        if (tl < 1.0E-6f) {
            return;
        }
        tx /= tl;
        ty /= tl;
        float n1x = -(tz /= tl);
        float n1y = 0.0f;
        float n1z = tx;
        float nl = (float)Math.sqrt(n1x * n1x + n1z * n1z);
        if (nl < 1.0E-4f) {
            n1x = 1.0f;
            n1z = 0.0f;
        } else {
            n1x /= nl;
            n1z /= nl;
        }
        float n2x = ty * n1z - tz * n1y;
        float n2y = tz * n1x - tx * n1z;
        float n2z = tx * n1y - ty * n1x;
        float w1 = a * (float)Math.sin(s * 2.3f - t * 34.0f);
        float w2 = a * 0.5f * (float)Math.cos(s * 1.7f - t * 27.0f);
        out[0] = out[0] + (n1x * w1 + n2x * w2);
        out[1] = out[1] + (n1y * w1 + n2y * w2);
        out[2] = out[2] + (n1z * w1 + n2z * w2);
    }

    static void pose(float t, Pose o) {
        if (t < 1.9f) {
            float k = 1.0f - (float)Math.pow(1.0f - DeathFx.sm(0.22f, 1.62f, t), 2.0);
            o.p[0] = DeathFx.lerp(0.0f, 0.0f, k);
            o.p[1] = DeathFx.lerp(1.055f, 3.3f, k) + (float)Math.sin(t * 2.6f) * 0.06f * DeathFx.sm(0.5f, 1.0f, t) * (1.0f - DeathFx.sm(1.45f, 1.62f, t));
            o.p[2] = DeathFx.lerp(0.0f, 0.0f, k) - 0.28f * (float)Math.sin((double)DeathFx.cl(t / 0.5f) * Math.PI) * (1.0f - k);
            o.rx = -1.5707964f * DeathFx.sm(0.25f, 1.35f, t);
            o.ry = 0.55f * DeathFx.sm(0.2f, 1.6f, t);
            o.rz = 0.12f * (float)Math.sin(t * 1.8f) * DeathFx.sm(0.4f, 1.0f, t) * (1.0f - DeathFx.sm(1.5f, 1.62f, t));
            o.arm = -0.9f * (float)Math.sin((double)DeathFx.cl(t / 0.4f) * Math.PI) + 1.35f * DeathFx.sm(0.35f, 1.2f, t) + 0.12f * (float)Math.sin(t * 2.2f);
            o.leg = 0.55f * DeathFx.sm(0.3f, 1.2f, t);
            o.head = -0.75f * DeathFx.sm(0.4f, 1.3f, t);
        } else {
            DeathFx.chainPoint(DeathFx.sBody(t), t, o.p);
            float j = (float)Math.sin((double)DeathFx.cl((t - 1.9f) / 0.3f) * Math.PI);
            float pl = DeathFx.ei((t - 2.32f) / 0.6f);
            o.rx = -1.5707964f - 0.38f * j + 0.25f * pl;
            o.ry = 0.55f;
            o.rz = 0.05f * (float)Math.sin(t * 40.0f) * DeathFx.sm(2.06f, 2.1f, t) * (1.0f - DeathFx.sm(2.25f, 2.32f, t));
            o.arm = DeathFx.lerp(1.35f - 1.0f * j, -1.3f, pl);
            o.leg = DeathFx.lerp(0.55f - 0.3f * j, -0.9f, pl);
            o.head = DeathFx.lerp(-0.75f + 0.5f * j, -1.1f, pl);
        }
        o.e = (float)(0.9 * Math.exp(-t * 12.0f) + (t >= 1.9f ? 0.8 * Math.exp(-(t - 1.9f) * 7.0f) : 0.0));
    }

    public static void tick(class_310 mc) {
        Config c = NeonLyrics.cfg();
        if (mc.field_1687 == null || mc.field_1724 == null) {
            ACTIVE.clear();
            TRIGGERED.clear();
            return;
        }
        if (c.deathFx <= 0) {
            ACTIVE.clear();
            TRIGGERED.clear();
            return;
        }
        SEEN.clear();
        for (class_742 p : mc.field_1687.method_18456()) {
            int id = p.method_5628();
            SEEN.add(id);
            boolean dead = p.method_29504();
            if (!dead) {
                TRIGGERED.remove(id);
                continue;
            }
            if (TRIGGERED.contains(id)) continue;
            TRIGGERED.add(id);
            if (p == mc.field_1724 || p.method_5858((class_1297)mc.field_1724) > 6400.0 || !c.deathOthers && !DeathFx.killedByMe(id)) continue;
            DeathFx.spawn(mc, p, false, false);
        }
        TRIGGERED.retainAll(SEEN);
        long now = System.currentTimeMillis();
        HITS.values().removeIf(at -> now - at > 12000L);
    }

    public static void onAttack(class_1297 target) {
        if (target instanceof class_742) {
            HITS.put(target.method_5628(), System.currentTimeMillis());
        }
    }

    static boolean killedByMe(int id) {
        Long at = HITS.get(id);
        return at != null && System.currentTimeMillis() - at <= 6000L;
    }

    public static void test() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        DeathFx.spawn(mc, (class_742)mc.field_1724, true, true);
    }

    public static boolean hides(class_1297 e) {
        if (NeonLyrics.cfg().deathFx <= 0 || e == null) {
            return false;
        }
        int id = e.method_5628();
        if (TRIGGERED.contains(id) && e instanceof class_742) {
            return true;
        }
        for (Fx f : ACTIVE) {
            if (!f.test || f.id != id) continue;
            return true;
        }
        return false;
    }

    static void spawn(class_310 mc, class_742 p, boolean self, boolean test) {
        int i;
        int i2;
        if (ACTIVE.size() >= 6) {
            ACTIVE.remove(0);
        }
        Fx f = new Fx();
        f.id = p.method_5628();
        f.self = self;
        f.test = test;
        f.kind = Math.max(1, NeonLyrics.cfg().deathFx);
        class_243 pos = p.method_73189();
        f.yaw = (float)Math.toRadians(p.field_6283);
        f.start = System.nanoTime();
        try {
            f.skin = p.method_52814().comp_1626().comp_3627();
        }
        catch (Throwable t) {
            f.skin = null;
        }
        double gy = pos.field_1351;
        int top = 5999164;
        int under = 8017460;
        try {
            for (int dy = 0; dy <= 8; ++dy) {
                class_2338 bp = class_2338.method_49637((double)pos.field_1352, (double)(pos.field_1351 - 0.01 - (double)dy), (double)pos.field_1350);
                class_2680 st = mc.field_1687.method_8320(bp);
                if (st.method_26220((class_1922)mc.field_1687, bp).method_1110()) continue;
                gy = (double)bp.method_10264() + 1.0;
                int c1 = st.method_26205((class_1922)mc.field_1687, (class_2338)bp).field_16011;
                class_2338 b2 = bp.method_10074();
                int c2 = mc.field_1687.method_8320((class_2338)b2).method_26205((class_1922)mc.field_1687, (class_2338)b2).field_16011;
                if (c1 != 0) {
                    top = c1;
                }
                under = c2 != 0 ? c2 : top;
                break;
            }
        }
        catch (Throwable dy) {
            // empty catch block
        }
        f.ox = pos.field_1352;
        f.oy = gy;
        f.oz = pos.field_1350;
        f.groundCol = top;
        f.dirtCol = under;
        Random r = new Random(f.start ^ 31L * (long)f.id);
        ArrayList<float[]> cr = new ArrayList<float[]>();
        for (int k = 0; k < 13; ++k) {
            double a = (double)k / 13.0 * Math.PI * 2.0 + r.nextDouble() * 0.4;
            double rr = 0.15;
            double x = (double)1.7f + Math.cos(a) * rr;
            double z = (double)0.85f + Math.sin(a) * rr;
            double w = 0.085;
            double maxR = 2.4 + r.nextDouble() * 1.9;
            while (rr < maxR) {
                double len = 0.17 + r.nextDouble() * 0.24;
                double nx = x + Math.cos(a += (r.nextDouble() - 0.5) * 0.9) * len;
                double nz = z + Math.sin(a) * len;
                double mx = (x + nx) / 2.0;
                double mz = (z + nz) / 2.0;
                cr.add(new float[]{(float)x, (float)z, (float)nx, (float)nz, (float)w, (float)rr, (float)Math.hypot(mx - (double)1.7f, mz - (double)0.85f), r.nextFloat() * 9.0f});
                x = nx;
                z = nz;
                rr = Math.hypot(x - (double)1.7f, z - (double)0.85f);
                w *= 0.94;
            }
        }
        f.cracks = cr.toArray(new float[0][]);
        f.chunks = new float[42][];
        for (i2 = 0; i2 < f.chunks.length; ++i2) {
            boolean big = i2 < 12;
            double a = r.nextDouble() * Math.PI * 2.0;
            double rr = r.nextDouble() * (big ? 0.7 : 0.4);
            double sp = big ? 1.2 + r.nextDouble() * 1.8 : 1.0 + r.nextDouble() * 3.0;
            f.chunks[i2] = new float[]{(float)((double)1.7f + Math.cos(a) * rr), -0.1f, (float)((double)0.85f + Math.sin(a) * rr), (float)(Math.cos(a) * sp), (float)(big ? 4.5 + r.nextDouble() * 2.5 : 4.0 + r.nextDouble() * 5.0), (float)(Math.sin(a) * sp), (float)(big ? 0.24 + r.nextDouble() * 0.2 : 0.07 + r.nextDouble() * 0.12), r.nextFloat() * 10.0f - 5.0f, r.nextFloat() * 10.0f - 5.0f, r.nextFloat() * 10.0f - 5.0f, DeathFx.shade(r.nextBoolean() ? top : under, 0.8f + r.nextFloat() * 0.3f)};
        }
        f.ring = new float[18][];
        for (i2 = 0; i2 < f.ring.length; ++i2) {
            f.ring[i2] = new float[]{(float)((double)i2 / 18.0 * Math.PI * 2.0 + r.nextDouble() * 0.2), 1.0f + r.nextFloat() * 0.7f, 0.28f + r.nextFloat() * 0.16f, r.nextFloat() * 0.12f, DeathFx.shade(top, 0.75f + r.nextFloat() * 0.3f)};
        }
        f.burst = new float[70][];
        DeathFx.pathPoint(SB - 0.05f, TA);
        DeathFx.pathPoint(SB + 0.05f, TB);
        float tx = (TB[0] - TA[0]) * 10.0f;
        float ty = (TB[1] - TA[1]) * 10.0f;
        float tz = (TB[2] - TA[2]) * 10.0f;
        for (i = 0; i < f.burst.length; ++i) {
            double vx = r.nextGaussian();
            double vy = r.nextGaussian();
            double vz = r.nextGaussian();
            double vl = Math.max(0.01, Math.sqrt(vx * vx + vy * vy + vz * vz));
            double sp = 1.2 + r.nextDouble() * 4.0;
            double along = 1.5 + r.nextDouble() * 3.5;
            f.burst[i] = new float[]{(float)(vx / vl * sp + (double)tx * along), (float)(vy / vl * sp + (double)ty * along), (float)(vz / vl * sp + (double)tz * along), 0.035f + r.nextFloat() * 0.08f, r.nextFloat() * 12.0f, r.nextFloat() < 0.25f ? 1.0f : 0.0f, 1.9f + r.nextFloat() * 0.04f};
        }
        f.embers = new float[56][];
        for (i = 0; i < f.embers.length; ++i) {
            f.embers[i] = new float[]{0.95f + r.nextFloat() * 3.4f, 1.2f + r.nextFloat() * 0.9f, (float)Math.sqrt(r.nextDouble()) * 1.5f, r.nextFloat() * 6.283f, 0.8f + r.nextFloat() * 1.6f, 0.06f + r.nextFloat() * 0.08f, r.nextFloat() < 0.7f ? 1.672096E7f : 1.6747088E7f};
        }
        f.wisps = new float[24][];
        for (i = 0; i < f.wisps.length; ++i) {
            f.wisps[i] = new float[]{0.3f + r.nextFloat() * 1.1f, (r.nextFloat() - 0.5f) * 0.7f, (r.nextFloat() - 0.5f) * 0.7f, (r.nextFloat() - 0.5f) * 0.7f, r.nextFloat() - 0.5f, r.nextFloat() - 0.5f};
        }
        f.dust = new float[56][];
        for (i = 0; i < f.dust.length; ++i) {
            boolean first = i < 22;
            f.dust[i] = new float[]{(first ? 1.62f : TC) + r.nextFloat() * 0.08f, r.nextFloat() * 6.283f, (first ? 0.5f : 1.3f) + r.nextFloat() * 0.5f, (first ? 2.0f : 2.8f) * (0.6f + r.nextFloat() * 0.6f), 0.2f + r.nextFloat() * 0.8f, 1.5f + r.nextFloat() * 1.3f, 0.8f + r.nextFloat() * 1.0f};
        }
        if (f.kind == 2) {
            f.extra = DeathCards.create(f, r);
        } else if (f.kind >= 3) {
            f.extra = DeathHard.create(f, r);
        }
        ACTIVE.add(f);
    }

    public static void render(class_310 mc, class_4597 buffers, Matrix4f base, class_243 cam, float pt) {
        if (ACTIVE.isEmpty() || mc.field_1687 == null) {
            return;
        }
        long now = System.nanoTime();
        DeathFx.cameraAxes(mc, base, pt);
        Iterator<Fx> it = ACTIVE.iterator();
        while (it.hasNext()) {
            boolean hard;
            Fx f = it.next();
            int ms = (int)((now - f.start) / 1000000L);
            boolean bl = hard = f.kind >= 3;
            float[] table = f.kind == 2 ? DeathCards.TT : (hard ? DeathHard.TT[Math.min(f.kind - 3, DeathHard.TT.length - 1)] : TT);
            float t = table[Math.min(table.length - 1, Math.max(0, ms))];
            float tEnd = f.kind == 2 ? 4.2f : (hard ? DeathHard.END[Math.min(f.kind - 3, DeathHard.END.length - 1)] : 4.75f);
            if (t > tEnd || ms >= table.length - 1) {
                it.remove();
                continue;
            }
            try {
                if (f.kind == 2) {
                    DeathCards.draw(mc, buffers, base, cam, f, t);
                    continue;
                }
                if (hard) {
                    DeathHard.draw(mc, buffers, base, cam, f, t);
                    continue;
                }
                DeathFx.draw(mc, buffers, base, cam, f, t);
            }
            catch (Throwable e) {
                NeonLyrics.LOG.warn("Todes-Animation abgebrochen", e);
                it.remove();
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    static void draw(class_310 mc, class_4597 buffers, Matrix4f base, class_243 cam, Fx f, float t) {
        boolean bl;
        float sz;
        float z;
        float y;
        float ag;
        float ai;
        if (!Accessories.whiteTex()) {
            return;
        }
        Matrix4f m = new Matrix4f((Matrix4fc)base).translate((float)(f.ox - cam.field_1352), (float)(f.oy - cam.field_1351), (float)(f.oz - cam.field_1350)).rotateY(-f.yaw);
        int light = DeathFx.light(mc, f.ox, f.oy + 0.5, f.oz);
        float fade = 1.0f - DeathFx.sm(3.7f, 4.7f, t);
        boolean firstPerson = f.self && mc.field_1690.method_31044().method_31034();
        class_4588 g = buffers.method_73477(class_12249.method_76023());
        float glow = DeathFx.sm(0.95f, 1.6f, t) * (1.0f + 0.2f * (float)Math.sin(t * 19.0f));
        float y0 = 0.02f;
        float sheen = (t < 1.62f ? 0.35f * glow : 0.42f * (float)Math.exp((double)(-(t - 1.62f)) * 1.2) + 0.22f) * fade * DeathFx.sm(0.95f, 1.3f, t);
        DeathFx.disc(g, m, 1.7f, y0 - 0.005f, 0.85f, 0.0f, 2.6f, 16718390, sheen, 16718390, 0.0f, 28);
        float spread = 4.2f * DeathFx.eo((t - 0.92f) / 0.75f);
        float holeR = t < 1.62f ? 0.0f : (t < TC ? 0.95f : 1.75f);
        for (float[] c : f.cracks) {
            float op = DeathFx.sm(c[5], c[5] + 0.35f, spread) * (0.75f + 0.25f * (float)Math.sin(t * 23.0f + c[7])) * (1.0f - 0.7f * DeathFx.sm(3.4f, 4.6f, t));
            if (op < 0.01f || c[6] < holeR) continue;
            DeathFx.strip(g, m, c[0], c[1], c[2], c[3], y0 + 0.004f, c[4] * 3.2f, 16718904, op * 0.22f);
            DeathFx.strip(g, m, c[0], c[1], c[2], c[3], y0 + 0.008f, c[4], 16725328, op);
        }
        if (t >= 1.62f) {
            float R = (t < TC ? 0.95f * DeathFx.eo((t - 1.62f) / 0.12f) : 0.95f + 0.8f * DeathFx.eo((t - TC) / 0.25f)) * (1.0f - 0.85f * DeathFx.sm(3.9f, 4.7f, t));
            float a = 1.0f - DeathFx.sm(4.2f, 4.75f, t);
            float core = 0.85f + 0.15f * (float)Math.sin(t * 9.0f);
            DeathFx.disc(g, m, 1.7f, y0 + 0.012f, 0.85f, 0.0f, R * 0.45f, 16726613, a * core, 6947864, a, 24);
            DeathFx.disc(g, m, 1.7f, y0 + 0.012f, 0.85f, R * 0.45f, R, 6947864, a, 393476, a, 24);
            DeathFx.disc(g, m, 1.7f, y0 + 0.01f, 0.85f, R, R * 1.18f, 16722504, 0.9f * a, 16722504, 0.0f, 24);
        }
        if ((ai = (t - 1.9f) / 0.32f) > 0.0f && ai < 1.0f) {
            DeathFx.pathPoint(SB - 0.05f, TA);
            DeathFx.pathPoint(SB + 0.05f, TB);
            DeathFx.ringAround(g, m, 0.0f, 3.3f, 0.0f, TB[0] - TA[0], TB[1] - TA[1], TB[2] - TA[2], 0.25f + DeathFx.eo(ai) * 1.7f, 0.06f + 0.04f * (1.0f - ai), 16730726, 0.8f * (1.0f - ai));
        }
        if ((ag = (t - TC) / 0.9f) > 0.0f && ag < 1.0f) {
            float rr = 0.5f + DeathFx.eo(ag) * 6.0f;
            DeathFx.disc(g, m, 1.7f, y0 + 0.02f, 0.85f, rr * 0.9f, rr, 16722500, 0.0f, 16722500, 0.9f * (1.0f - ag), 40);
            DeathFx.disc(g, m, 1.7f, y0 + 0.02f, 0.85f, rr, rr * 1.04f, 16751272, 0.6f * (1.0f - ag), 16751272, 0.0f, 40);
        }
        class_4588 w = buffers.method_73477(class_12249.method_75994((class_2960)Accessories.WHITE));
        boolean on = t >= 1.62f;
        float st = DeathFx.sTip(t);
        if (on) {
            float s;
            int var21_30 = 0;
            while (var21_30 < 44 && !((s = st - 0.26f - (float)var21_30 * 0.33f) <= SH - 2.5f)) {
                DeathFx.chainPoint(s, t, Q);
                DeathFx.chainPoint(s + 0.06f, t, Q2);
                float e = 0.0f;
                if (t < 2.1599998f) {
                    e += 0.9f * (float)Math.exp(-(st - s) / 0.45f) * DeathFx.sm(1.62f, 1.67f, t) * (1.0f - DeathFx.sm(2.0f, 2.15f, t));
                }
                if (t > 2.06f && t < 2.52f) {
                    float pp = SB - (t - 2.06f) * 30.0f;
                    e += 1.6f * (float)Math.exp(-(s - pp) * (s - pp) / 0.25f);
                }
                if (t > 2.32f) {
                    e += 0.05f + 0.3f * Math.max(0.0f, (float)Math.sin(s * 2.5f + t * 45.0f));
                }
                int col = DeathFx.mix(0x2A2A32, 16722504, Math.min(1.0f, e));
                int li = e > 0.35f ? 0xF000F0 : light;
                Matrix4f lm = new Matrix4f((Matrix4fc)m).translate(Q[0], Q[1], Q[2]).rotate((Quaternionfc)DeathFx.toward(Q2[0] - Q[0], Q2[1] - Q[1], Q2[2] - Q[2]));
                if ((var21_30 & 1) == 1) {
                    lm.rotateY(1.5707964f);
                }
                DeathFx.link(w, lm, col, li);
                ++var21_30;
            }
            DeathFx.chainPoint(st, t, Q);
            DeathFx.chainPoint(st + 0.06f, t, Q2);
            Matrix4f matrix4f = new Matrix4f((Matrix4fc)m).translate(Q[0], Q[1], Q[2]).rotate((Quaternionfc)DeathFx.toward(Q2[0] - Q[0], Q2[1] - Q[1], Q2[2] - Q[2]));
            float hot = t > 1.9f ? (float)Math.exp(-(t - 1.9f) * 4.0f) : 0.0f;
            DeathFx.spear(w, matrix4f, light, DeathFx.mix(11540526, 16734832, hot));
        }
        for (float[] c : f.chunks) {
            float a = t - 1.62f;
            if (a <= 0.0f || a > 2.5f) continue;
            float x = c[0] + c[3] * a;
            y = c[1] + c[4] * a - 8.0f * a * a;
            z = c[2] + c[5] * a;
            if (y < -1.5f) continue;
            sz = c[6];
            Matrix4f cm = new Matrix4f((Matrix4fc)m).translate(x, y, z).rotateXYZ(c[7] * a, c[8] * a, c[9] * a);
            DeathFx.cube(w, cm, -sz / 2.0f, -sz / 2.0f, -sz / 2.0f, sz / 2.0f, sz / 2.0f, sz / 2.0f, 0xFF000000 | Float.floatToRawIntBits(c[10]), light);
        }
        for (float[] c : f.ring) {
            float a = t - TC - c[3];
            if (t < TC || a > 1.4f) continue;
            float aa = Math.max(0.0f, a);
            float rad = c[1] - aa * 0.8f;
            float x = 1.7f + (float)Math.cos(c[0]) * rad;
            float z2 = 0.85f + (float)Math.sin(c[0]) * rad;
            float y2 = -c[2] * 0.4f - 9.0f * aa * aa - aa * 0.5f;
            if (y2 < -2.5f) continue;
            float sz2 = c[2];
            Matrix4f cm = new Matrix4f((Matrix4fc)m).translate(x, y2, z2).rotateY(-c[0]).rotateZ(aa * 3.0f);
            DeathFx.cube(w, cm, -sz2 / 2.0f, -sz2 / 2.0f, -sz2 / 2.0f, sz2 / 2.0f, sz2 / 2.0f, sz2 / 2.0f, 0xFF000000 | Float.floatToRawIntBits(c[4]), light);
        }
        for (float[] b : f.burst) {
            float a = t - b[6];
            if (a <= 0.0f || a > 1.6f) continue;
            float x = 0.0f + b[0] * a;
            y = 3.3f + b[1] * a - 6.0f * a * a;
            z = 0.0f + b[2] * a;
            sz = b[3] * (1.0f - a / 1.6f * 0.6f);
            Matrix4f cm = new Matrix4f((Matrix4fc)m).translate(x, y, z).rotateXYZ(b[4] * a, b[4] * a * 0.7f, 0.0f);
            boolean hotP = b[5] > 0.5f;
            DeathFx.cube(w, cm, -sz / 2.0f, -sz / 2.0f, -sz / 2.0f, sz / 2.0f, sz / 2.0f, sz / 2.0f, hotP ? -54715 : -12974576, hotP ? 0xF000F0 : light);
        }
        DeathFx.pose(t, POSE);
        boolean bl3 = bl = DeathFx.POSE.p[1] > -1.4f && !firstPerson;
        if (bl && f.skin != null) {
            class_4588 sk = buffers.method_73477(class_12249.method_75994((class_2960)f.skin));
            int bl2 = DeathFx.light(mc, f.ox + (double)DeathFx.POSE.p[0], f.oy + (double)DeathFx.POSE.p[1], f.oz + (double)DeathFx.POSE.p[2]);
            float e = Math.min(1.0f, DeathFx.POSE.e);
            int tint = 0xFFFF0000 | Math.round(255.0f * (1.0f - 0.6f * e)) << 8 | Math.round(255.0f * (1.0f - 0.6f * e));
            DeathFx.body(sk, new Matrix4f((Matrix4fc)m).translate(DeathFx.POSE.p[0], DeathFx.POSE.p[1], DeathFx.POSE.p[2]).rotateY(DeathFx.POSE.ry).rotateX(DeathFx.POSE.rx).rotateZ(DeathFx.POSE.rz), POSE, tint, bl2);
        }
        if (SkyTextures.spritesReady()) {
            float a;
            class_4588 sp = buffers.method_73477(class_12249.method_76022((class_2960)SkyTextures.SPRITES));
            float rx = AX[0];
            float ry = AX[1];
            float rz = AX[2];
            float ux = AX[3];
            float uy = AX[4];
            float uz = AX[5];
            if (!firstPerson) {
                for (float[] wp : f.wisps) {
                    a = (t - wp[0]) / 1.0f;
                    if (a <= 0.0f || a >= 1.0f) continue;
                    DeathFx.pose(Math.min(wp[0], 1.8989999f), POSE);
                    float x = DeathFx.POSE.p[0] + wp[1] + wp[4] * a;
                    float y3 = DeathFx.POSE.p[1] + wp[2] + a * 1.2f;
                    float z3 = DeathFx.POSE.p[2] + wp[3] + wp[5] * a;
                    DeathFx.sprite(sp, base, f, cam, x, y3, z3, 0.5f + a * 1.3f, 1, 1707298, 0.55f * (float)Math.sin((double)a * Math.PI), rx, ry, rz, ux, uy, uz);
                }
            }
            for (float[] e : f.embers) {
                a = (t - e[0]) / e[1];
                if (a <= 0.0f || a >= 1.0f) continue;
                float an = e[3] + a * 1.5f;
                float rad = e[2] * (1.0f + a * 0.3f);
                DeathFx.sprite(sp, base, f, cam, 1.7f + (float)Math.cos(an) * rad, 0.05f + a * e[4] * 1.6f, 0.85f + (float)Math.sin(an) * rad, e[5] * 2.2f * (1.2f - a * 0.6f), 2, (int)e[6], (float)Math.sin((double)a * Math.PI) * fade, rx, ry, rz, ux, uy, uz);
            }
            for (float[] d : f.dust) {
                a = (t - d[0]) / d[5];
                if (a <= 0.0f || a >= 1.0f) continue;
                float k = DeathFx.eo(a * 1.6f);
                float x = 1.7f + (float)Math.cos(d[1]) * (d[2] + d[3] * k);
                float z2 = 0.85f + (float)Math.sin(d[1]) * (d[2] + d[3] * k);
                DeathFx.sprite(sp, base, f, cam, x, 0.25f + d[4] * a * 1.5f, z2, d[6] * (1.0f + a * 2.2f), 1, DeathFx.mix(f.dirtCol, 0x2A2226, 0.55f), 0.5f * (float)Math.pow(1.0f - a, 1.4) * DeathFx.cl(a * 8.0f), rx, ry, rz, ux, uy, uz);
            }
        }
    }

    static void cameraAxes(class_310 mc, Matrix4f base, float pt) {
        boolean identity;
        boolean bl = identity = Math.abs(base.m00() - 1.0f) + Math.abs(base.m11() - 1.0f) + Math.abs(base.m22() - 1.0f) < 1.0E-4f;
        if (!identity) {
            DeathFx.AX[0] = base.m00();
            DeathFx.AX[1] = base.m10();
            DeathFx.AX[2] = base.m20();
            DeathFx.AX[3] = base.m01();
            DeathFx.AX[4] = base.m11();
            DeathFx.AX[5] = base.m21();
            return;
        }
        double y = Math.toRadians(mc.field_1724.method_5705(pt));
        double p = Math.toRadians(mc.field_1724.method_5695(pt));
        if (mc.field_1690.method_31044().method_31035()) {
            y += Math.PI;
            p = -p;
        }
        double fx = -Math.sin(y) * Math.cos(p);
        double fy = -Math.sin(p);
        double fz = Math.cos(y) * Math.cos(p);
        double rx = -Math.cos(y);
        double ry = 0.0;
        double rz = -Math.sin(y);
        DeathFx.AX[0] = (float)rx;
        DeathFx.AX[1] = (float)ry;
        DeathFx.AX[2] = (float)rz;
        DeathFx.AX[3] = (float)(ry * fz - rz * fy);
        DeathFx.AX[4] = (float)(rz * fx - rx * fz);
        DeathFx.AX[5] = (float)(rx * fy - ry * fx);
    }

    static Quaternionf toward(float x, float y, float z) {
        float l = (float)Math.sqrt(x * x + y * y + z * z);
        if (l < 1.0E-6f) {
            return new Quaternionf();
        }
        return new Quaternionf().rotationTo(0.0f, 1.0f, 0.0f, x / l, y / l, z / l);
    }

    static void link(class_4588 w, Matrix4f m, int col, int light) {
        float L = 0.25f;
        float W = 0.13f;
        float T2 = 0.034f;
        DeathFx.cube(w, m, -W, -L, -T2, -W + 2.0f * T2, L, T2, col, light);
        DeathFx.cube(w, m, W - 2.0f * T2, -L, -T2, W, L, T2, col, light);
        DeathFx.cube(w, m, -W + 2.0f * T2, L - 2.0f * T2, -T2, W - 2.0f * T2, L, T2, col, light);
        DeathFx.cube(w, m, -W + 2.0f * T2, -L, -T2, W - 2.0f * T2, -L + 2.0f * T2, T2, col, light);
    }

    static void spear(class_4588 w, Matrix4f m, int light, int edge) {
        int dark = -14408660;
        DeathFx.cube(w, m, -0.09f, -0.12f, -0.09f, 0.09f, 0.1f, 0.09f, dark, light);
        float by = 0.12f;
        float ty = 1.0f;
        float bx = 0.17f;
        float bz = 0.07f;
        float[][] ring = new float[][]{{bx, by, 0.0f}, {0.0f, by, bz}, {-bx, by, 0.0f}, {0.0f, by, -bz}};
        for (int i = 0; i < 4; ++i) {
            float[] a = ring[i];
            float[] b = ring[i + 1 & 3];
            DeathFx.tri(w, m, a[0], a[1], a[2], b[0], b[1], b[2], 0.0f, ty, 0.0f, dark, light);
            DeathFx.tri(w, m, a[0], a[1], a[2], b[0], b[1], b[2], 0.0f, 0.06f, 0.0f, dark, light);
        }
        for (int sgn = -1; sgn <= 1; sgn += 2) {
            float ex = (float)sgn * (bx + 0.012f);
            DeathFx.quadW(w, m, ex, by, -0.012f, ex, by, 0.012f, 0.0f, ty + 0.02f, 0.006f, 0.0f, ty + 0.02f, -0.006f, edge, 0xF000F0);
            DeathFx.tri(w, m, (float)sgn * 0.1f, 0.3f, -0.035f, (float)sgn * 0.1f, 0.3f, 0.035f, (float)sgn * 0.33f, -0.1f, 0.0f, dark, light);
            DeathFx.tri(w, m, (float)sgn * 0.1f, 0.3f, 0.035f, (float)sgn * 0.13f, 0.18f, 0.0f, (float)sgn * 0.33f, -0.1f, 0.0f, dark, light);
            DeathFx.tri(w, m, (float)sgn * 0.1f, 0.3f, -0.035f, (float)sgn * 0.13f, 0.18f, 0.0f, (float)sgn * 0.33f, -0.1f, 0.0f, dark, light);
        }
    }

    static void body(class_4588 v, Matrix4f mb, Pose o, int tint, int light) {
        DeathFx.skinBox(v, mb, 8.0f, 12.0f, 4.0f, 0.0f, 16, 16, tint, light);
        DeathFx.skinBox(v, mb, 8.0f, 12.0f, 4.0f, 0.25f, 16, 32, tint, light);
        Matrix4f mh = new Matrix4f((Matrix4fc)mb).translate(0.0f, 0.3515625f, 0.0f).rotateX(o.head).translate(0.0f, 0.234375f, 0.0f);
        DeathFx.skinBox(v, mh, 8.0f, 8.0f, 8.0f, 0.0f, 0, 0, tint, light);
        DeathFx.skinBox(v, mh, 8.0f, 8.0f, 8.0f, 0.5f, 32, 0, tint, light);
        Matrix4f ar = new Matrix4f((Matrix4fc)mb).translate(-0.3515625f, 0.234375f, 0.0f).rotateX(o.arm * 0.9f + 0.1f).rotateZ(0.12f).translate(0.0f, -0.234375f, 0.0f);
        DeathFx.skinBox(v, ar, 4.0f, 12.0f, 4.0f, 0.0f, 40, 16, tint, light);
        DeathFx.skinBox(v, ar, 4.0f, 12.0f, 4.0f, 0.25f, 40, 32, tint, light);
        Matrix4f al = new Matrix4f((Matrix4fc)mb).translate(0.3515625f, 0.234375f, 0.0f).rotateX(o.arm).rotateZ(-0.12f).translate(0.0f, -0.234375f, 0.0f);
        DeathFx.skinBox(v, al, 4.0f, 12.0f, 4.0f, 0.0f, 32, 48, tint, light);
        DeathFx.skinBox(v, al, 4.0f, 12.0f, 4.0f, 0.25f, 48, 48, tint, light);
        Matrix4f lr = new Matrix4f((Matrix4fc)mb).translate(-0.1171875f, -0.3515625f, 0.0f).rotateX(o.leg * 0.75f).translate(0.0f, -0.3515625f, 0.0f);
        DeathFx.skinBox(v, lr, 4.0f, 12.0f, 4.0f, 0.0f, 0, 16, tint, light);
        DeathFx.skinBox(v, lr, 4.0f, 12.0f, 4.0f, 0.25f, 0, 32, tint, light);
        Matrix4f ll = new Matrix4f((Matrix4fc)mb).translate(0.1171875f, -0.3515625f, 0.0f).rotateX(o.leg).translate(0.0f, -0.3515625f, 0.0f);
        DeathFx.skinBox(v, ll, 4.0f, 12.0f, 4.0f, 0.0f, 16, 48, tint, light);
        DeathFx.skinBox(v, ll, 4.0f, 12.0f, 4.0f, 0.25f, 0, 48, tint, light);
    }

    static void skinBox(class_4588 vc, Matrix4f m, float w, float h, float d, float infl, int u, int v, int col, int light) {
        float hx = (w / 2.0f + infl) * 0.05859375f;
        float hy = (h / 2.0f + infl) * 0.05859375f;
        float hz = (d / 2.0f + infl) * 0.05859375f;
        float k = 0.015625f;
        DeathFx.face(vc, m, -hx, hy, hz, hx, hy, hz, hx, -hy, hz, -hx, -hy, hz, ((float)u + d) * k, ((float)v + d) * k, ((float)u + d + w) * k, ((float)v + d + h) * k, 0.0f, 0.0f, 1.0f, col, light);
        DeathFx.face(vc, m, hx, hy, -hz, -hx, hy, -hz, -hx, -hy, -hz, hx, -hy, -hz, ((float)u + 2.0f * d + w) * k, ((float)v + d) * k, ((float)u + 2.0f * d + 2.0f * w) * k, ((float)v + d + h) * k, 0.0f, 0.0f, -1.0f, col, light);
        DeathFx.face(vc, m, -hx, hy, -hz, -hx, hy, hz, -hx, -hy, hz, -hx, -hy, -hz, (float)u * k, ((float)v + d) * k, ((float)u + d) * k, ((float)v + d + h) * k, -1.0f, 0.0f, 0.0f, col, light);
        DeathFx.face(vc, m, hx, hy, hz, hx, hy, -hz, hx, -hy, -hz, hx, -hy, hz, ((float)u + d + w) * k, ((float)v + d) * k, ((float)u + 2.0f * d + w) * k, ((float)v + d + h) * k, 1.0f, 0.0f, 0.0f, col, light);
        DeathFx.face(vc, m, -hx, hy, -hz, hx, hy, -hz, hx, hy, hz, -hx, hy, hz, ((float)u + d) * k, (float)v * k, ((float)u + d + w) * k, ((float)v + d) * k, 0.0f, 1.0f, 0.0f, col, light);
        DeathFx.face(vc, m, -hx, -hy, hz, hx, -hy, hz, hx, -hy, -hz, -hx, -hy, -hz, ((float)u + d + w) * k, (float)v * k, ((float)u + d + 2.0f * w) * k, ((float)v + d) * k, 0.0f, -1.0f, 0.0f, col, light);
    }

    static void face(class_4588 vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float u0, float v0, float u1, float v1, float nx, float ny, float nz, int col, int light) {
        Vector3f n = m.transformDirection(new Vector3f(nx, ny, nz)).normalize();
        DeathFx.vert(vc, m, x0, y0, z0, u0, v0, col, light, n);
        DeathFx.vert(vc, m, x1, y1, z1, u1, v0, col, light, n);
        DeathFx.vert(vc, m, x2, y2, z2, u1, v1, col, light, n);
        DeathFx.vert(vc, m, x3, y3, z3, u0, v1, col, light, n);
    }

    static void vert(class_4588 vc, Matrix4f m, float x, float y, float z, float u, float v, int col, int light, Vector3f n) {
        vc.method_22918((Matrix4fc)m, x, y, z).method_39415(col).method_22913(u, v).method_22922(class_4608.field_21444).method_60803(light).method_22914(n.x, n.y, n.z);
    }

    static void cube(class_4588 vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, int col, int light) {
        float u = 0.5f;
        DeathFx.face(vc, m, x0, y1, z1, x1, y1, z1, x1, y0, z1, x0, y0, z1, u, u, u, u, 0.0f, 0.0f, 1.0f, col, light);
        DeathFx.face(vc, m, x1, y1, z0, x0, y1, z0, x0, y0, z0, x1, y0, z0, u, u, u, u, 0.0f, 0.0f, -1.0f, col, light);
        DeathFx.face(vc, m, x0, y1, z0, x0, y1, z1, x0, y0, z1, x0, y0, z0, u, u, u, u, -1.0f, 0.0f, 0.0f, col, light);
        DeathFx.face(vc, m, x1, y1, z1, x1, y1, z0, x1, y0, z0, x1, y0, z1, u, u, u, u, 1.0f, 0.0f, 0.0f, col, light);
        DeathFx.face(vc, m, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1, u, u, u, u, 0.0f, 1.0f, 0.0f, col, light);
        DeathFx.face(vc, m, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, u, u, u, u, 0.0f, -1.0f, 0.0f, col, light);
    }

    static void tri(class_4588 vc, Matrix4f m, float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz, int col, int light) {
        float e1x = bx - ax;
        float e1y = by - ay;
        float e1z = bz - az;
        float e2x = cx - ax;
        float e2y = cy - ay;
        float e2z = cz - az;
        Vector3f n = m.transformDirection(new Vector3f(e1y * e2z - e1z * e2y, e1z * e2x - e1x * e2z, e1x * e2y - e1y * e2x)).normalize();
        DeathFx.vert(vc, m, ax, ay, az, 0.5f, 0.5f, col, light, n);
        DeathFx.vert(vc, m, bx, by, bz, 0.5f, 0.5f, col, light, n);
        DeathFx.vert(vc, m, cx, cy, cz, 0.5f, 0.5f, col, light, n);
        DeathFx.vert(vc, m, cx, cy, cz, 0.5f, 0.5f, col, light, n);
    }

    static void quadW(class_4588 vc, Matrix4f m, float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz, float dx, float dy, float dz, int col, int light) {
        Vector3f n = m.transformDirection(new Vector3f(0.0f, 0.0f, 1.0f)).normalize();
        DeathFx.vert(vc, m, ax, ay, az, 0.5f, 0.5f, col, light, n);
        DeathFx.vert(vc, m, bx, by, bz, 0.5f, 0.5f, col, light, n);
        DeathFx.vert(vc, m, cx, cy, cz, 0.5f, 0.5f, col, light, n);
        DeathFx.vert(vc, m, dx, dy, dz, 0.5f, 0.5f, col, light, n);
    }

    static void gv(class_4588 g, Matrix4f m, float x, float y, float z, int argb) {
        g.method_22918((Matrix4fc)m, x, y, z).method_39415(argb);
    }

    static void gquad(class_4588 g, Matrix4f m, float[] p, int c0, int c1, int c2, int c3) {
        DeathFx.gv(g, m, p[0], p[1], p[2], c0);
        DeathFx.gv(g, m, p[3], p[4], p[5], c1);
        DeathFx.gv(g, m, p[6], p[7], p[8], c2);
        DeathFx.gv(g, m, p[9], p[10], p[11], c3);
        DeathFx.gv(g, m, p[9], p[10], p[11], c3);
        DeathFx.gv(g, m, p[6], p[7], p[8], c2);
        DeathFx.gv(g, m, p[3], p[4], p[5], c1);
        DeathFx.gv(g, m, p[0], p[1], p[2], c0);
    }

    static void disc(class_4588 g, Matrix4f m, float cx, float y, float cz, float r0, float r1, int cIn, float aIn, int cOut, float aOut, int seg) {
        if (aIn <= 0.003f && aOut <= 0.003f) {
            return;
        }
        int ci = DeathFx.argb(cIn, aIn);
        int co = DeathFx.argb(cOut, aOut);
        for (int i = 0; i < seg; ++i) {
            double a0 = (double)i * Math.PI * 2.0 / (double)seg;
            double a1 = (double)(i + 1) * Math.PI * 2.0 / (double)seg;
            float c0 = (float)Math.cos(a0);
            float s0 = (float)Math.sin(a0);
            float c1 = (float)Math.cos(a1);
            float s1 = (float)Math.sin(a1);
            DeathFx.GP[0] = cx + c0 * r0;
            DeathFx.GP[1] = y;
            DeathFx.GP[2] = cz + s0 * r0;
            DeathFx.GP[3] = cx + c1 * r0;
            DeathFx.GP[4] = y;
            DeathFx.GP[5] = cz + s1 * r0;
            DeathFx.GP[6] = cx + c1 * r1;
            DeathFx.GP[7] = y;
            DeathFx.GP[8] = cz + s1 * r1;
            DeathFx.GP[9] = cx + c0 * r1;
            DeathFx.GP[10] = y;
            DeathFx.GP[11] = cz + s0 * r1;
            DeathFx.gquad(g, m, GP, ci, ci, co, co);
        }
    }

    static void strip(class_4588 g, Matrix4f m, float x0, float z0, float x1, float z1, float y, float w, int rgb, float a) {
        float dx = x1 - x0;
        float dz = z1 - z0;
        float l = (float)Math.sqrt(dx * dx + dz * dz);
        if (l < 1.0E-5f) {
            return;
        }
        float nx = -dz / l * w / 2.0f;
        float nz = dx / l * w / 2.0f;
        int c = DeathFx.argb(rgb, a);
        DeathFx.GP[0] = x0 + nx;
        DeathFx.GP[1] = y;
        DeathFx.GP[2] = z0 + nz;
        DeathFx.GP[3] = x1 + nx;
        DeathFx.GP[4] = y;
        DeathFx.GP[5] = z1 + nz;
        DeathFx.GP[6] = x1 - nx;
        DeathFx.GP[7] = y;
        DeathFx.GP[8] = z1 - nz;
        DeathFx.GP[9] = x0 - nx;
        DeathFx.GP[10] = y;
        DeathFx.GP[11] = z0 - nz;
        DeathFx.gquad(g, m, GP, c, c, c, c);
    }

    static void ringAround(class_4588 g, Matrix4f m, float cx, float cy, float cz, float dx, float dy, float dz, float r, float width, int rgb, float a) {
        float l = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (l < 1.0E-6f) {
            return;
        }
        dx /= l;
        dy /= l;
        float e1x = -(dz /= l);
        float e1y = 0.0f;
        float e1z = dx;
        float el = (float)Math.sqrt(e1x * e1x + e1z * e1z);
        if (el < 1.0E-4f) {
            e1x = 1.0f;
            e1z = 0.0f;
        } else {
            e1x /= el;
            e1z /= el;
        }
        float e2x = dy * e1z - dz * e1y;
        float e2y = dz * e1x - dx * e1z;
        float e2z = dx * e1y - dy * e1x;
        int ci = DeathFx.argb(rgb, 0.0f);
        int cm = DeathFx.argb(rgb, a);
        int seg = 40;
        float r0 = r - width;
        float r1 = r;
        for (int i = 0; i < seg; ++i) {
            double a0 = (double)i * Math.PI * 2.0 / (double)seg;
            double a1 = (double)(i + 1) * Math.PI * 2.0 / (double)seg;
            float c0 = (float)Math.cos(a0);
            float s0 = (float)Math.sin(a0);
            float c1 = (float)Math.cos(a1);
            float s1 = (float)Math.sin(a1);
            float ox0 = e1x * c0 + e2x * s0;
            float oy0 = e1y * c0 + e2y * s0;
            float oz0 = e1z * c0 + e2z * s0;
            float ox1 = e1x * c1 + e2x * s1;
            float oy1 = e1y * c1 + e2y * s1;
            float oz1 = e1z * c1 + e2z * s1;
            DeathFx.GP[0] = cx + ox0 * r0;
            DeathFx.GP[1] = cy + oy0 * r0;
            DeathFx.GP[2] = cz + oz0 * r0;
            DeathFx.GP[3] = cx + ox1 * r0;
            DeathFx.GP[4] = cy + oy1 * r0;
            DeathFx.GP[5] = cz + oz1 * r0;
            DeathFx.GP[6] = cx + ox1 * r1;
            DeathFx.GP[7] = cy + oy1 * r1;
            DeathFx.GP[8] = cz + oz1 * r1;
            DeathFx.GP[9] = cx + ox0 * r1;
            DeathFx.GP[10] = cy + oy0 * r1;
            DeathFx.GP[11] = cz + oz0 * r1;
            DeathFx.gquad(g, m, GP, ci, ci, cm, cm);
        }
    }

    static void sprite(class_4588 vc, Matrix4f base, Fx f, class_243 cam, float lx, float ly, float lz, float size, int k, int rgb, float a, float rx, float ry, float rz, float ux, float uy, float uz) {
        int i;
        if (a <= 0.02f) {
            return;
        }
        float c = (float)Math.cos(-f.yaw);
        float s = (float)Math.sin(-f.yaw);
        float wx = (float)(f.ox - cam.field_1352) + lx * c + lz * s;
        float wy = (float)(f.oy - cam.field_1351) + ly;
        float wz = (float)(f.oz - cam.field_1350) - lx * s + lz * c;
        float h = size / 2.0f;
        int col = DeathFx.argb(rgb, a);
        float u0 = (float)k * 0.25f;
        float u1 = u0 + 0.25f;
        float[] p = new float[]{wx - rx * h - ux * h, wy - ry * h - uy * h, wz - rz * h - uz * h, wx + rx * h - ux * h, wy + ry * h - uy * h, wz + rz * h - uz * h, wx + rx * h + ux * h, wy + ry * h + uy * h, wz + rz * h + uz * h, wx - rx * h + ux * h, wy - ry * h + uy * h, wz - rz * h + uz * h};
        float[][] uv = new float[][]{{u0, 1.0f}, {u1, 1.0f}, {u1, 0.0f}, {u0, 0.0f}};
        for (i = 0; i < 4; ++i) {
            DeathFx.sv(vc, base, p, i, uv, col);
        }
        for (i = 3; i >= 0; --i) {
            DeathFx.sv(vc, base, p, i, uv, col);
        }
    }

    static void sv(class_4588 vc, Matrix4f base, float[] p, int i, float[][] uv, int col) {
        vc.method_22918((Matrix4fc)base, p[i * 3], p[i * 3 + 1], p[i * 3 + 2]).method_39415(col).method_22913(uv[i][0], uv[i][1]).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 1.0f, 0.0f);
    }

    static int light(class_310 mc, double x, double y, double z) {
        try {
            class_2338 bp = class_2338.method_49637((double)x, (double)y, (double)z);
            return class_765.method_23687((int)mc.field_1687.method_8314(class_1944.field_9282, bp), (int)mc.field_1687.method_8314(class_1944.field_9284, bp));
        }
        catch (Throwable t) {
            return 0xF000F0;
        }
    }

    static float shade(int rgb, float k) {
        int r = Math.min(255, Math.round((float)(rgb >> 16 & 0xFF) * k));
        int g = Math.min(255, Math.round((float)(rgb >> 8 & 0xFF) * k));
        int b = Math.min(255, Math.round((float)(rgb & 0xFF) * k));
        return Float.intBitsToFloat(r << 16 | g << 8 | b);
    }

    static int mix(int a, int b, float k) {
        k = DeathFx.cl(k);
        int r = Math.round((float)(a >> 16 & 0xFF) + (float)((b >> 16 & 0xFF) - (a >> 16 & 0xFF)) * k);
        int g = Math.round((float)(a >> 8 & 0xFF) + (float)((b >> 8 & 0xFF) - (a >> 8 & 0xFF)) * k);
        int bl = Math.round((float)(a & 0xFF) + (float)((b & 0xFF) - (a & 0xFF)) * k);
        return 0xFF000000 | r << 16 | g << 8 | bl;
    }

    static int argb(int rgb, float a) {
        return Math.round(DeathFx.cl(a) * 255.0f) << 24 | rgb & 0xFFFFFF;
    }

    static float cl(float x) {
        return Math.max(0.0f, Math.min(1.0f, x));
    }

    static float sm(float a, float b, float x) {
        float t = DeathFx.cl((x - a) / (b - a));
        return t * t * (3.0f - 2.0f * t);
    }

    static float eo(float x) {
        float t = 1.0f - DeathFx.cl(x);
        return 1.0f - t * t * t;
    }

    static float ei(float x) {
        float t = DeathFx.cl(x);
        return t * t * t;
    }

    static float lerp(float a, float b, float k) {
        return a + (b - a) * k;
    }

    static {
        int i;
        TT = new float[9001];
        float t = 0.0f;
        for (i = 0; i < TT.length; ++i) {
            DeathFx.TT[i] = t;
            float r = (float)i / 1000.0f;
            t += (1.0f - 0.83f * DeathFx.sm(1.8f, 1.97f, r) * (1.0f - DeathFx.sm(2.85f, 3.1f, r))) / 1000.0f;
        }
        ArrayList<float[]> pts = new ArrayList<float[]>();
        for (i = 0; i <= 24; ++i) {
            pts.add(new float[]{1.7f, -7.0f + 7.0f * (float)i / 24.0f, 0.85f});
        }
        float cx = 1.5f;
        float cy = 1.45f;
        float cz = 0.75f;
        for (int i2 = 1; i2 <= 80; ++i2) {
            float u = (float)i2 / 80.0f;
            float a = (1.0f - u) * (1.0f - u);
            float b = 2.0f * u * (1.0f - u);
            float c = u * u;
            pts.add(new float[]{a * 1.7f + b * cx + c * 0.0f, a * 0.0f + b * cy + c * 3.3f, a * 0.85f + b * cz + c * 0.0f});
        }
        float dx = 0.0f - cx;
        float dy = 3.3f - cy;
        float dz = 0.0f - cz;
        float dl = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
        DX = dx / dl;
        DY = dy / dl;
        DZ = dz / dl;
        for (int i3 = 1; i3 <= 30; ++i3) {
            float k = 4.0f * (float)i3 / 30.0f;
            pts.add(new float[]{0.0f + DX * k, 3.3f + DY * k, 0.0f + DZ * k});
        }
        int n = pts.size();
        PX = new float[n];
        PY = new float[n];
        PZ = new float[n];
        PS = new float[n];
        float s = 0.0f;
        float sb = 0.0f;
        float sh = 0.0f;
        for (int i4 = 0; i4 < n; ++i4) {
            float[] p = (float[])pts.get(i4);
            if (i4 > 0) {
                float ex = p[0] - PX[i4 - 1];
                float ey = p[1] - PY[i4 - 1];
                float ez = p[2] - PZ[i4 - 1];
                s += (float)Math.sqrt(ex * ex + ey * ey + ez * ez);
            }
            DeathFx.PX[i4] = p[0];
            DeathFx.PY[i4] = p[1];
            DeathFx.PZ[i4] = p[2];
            DeathFx.PS[i4] = s;
            if (i4 == 24) {
                sh = s;
            }
            if (i4 != 104) continue;
            sb = s;
        }
        SB = sb;
        SH = sh;
        SEND = SB + 0.4f;
        float tc = 3.0f;
        float[] o = new float[3];
        for (float t2 = 2.32f; t2 < 4.0f; t2 += 0.002f) {
            DeathFx.pathPoint(DeathFx.sBody(t2), o);
            if (!(o[1] < 0.35f)) continue;
            tc = t2;
            break;
        }
        TC = tc;
        TA = new float[3];
        TB = new float[3];
        ACTIVE = new ArrayList<Fx>();
        TRIGGERED = new HashSet<Integer>();
        SEEN = new HashSet<Integer>();
        HITS = new HashMap<Integer, Long>();
        POSE = new Pose();
        Q = new float[3];
        Q2 = new float[3];
        AX = new float[6];
        GP = new float[12];
    }

    static final class Pose {
        final float[] p = new float[3];
        float rx;
        float ry;
        float rz;
        float arm;
        float leg;
        float head;
        float e;

        Pose() {
        }
    }

    static final class Fx {
        int id;
        boolean self;
        boolean test;
        double ox;
        double oy;
        double oz;
        float yaw;
        class_2960 skin;
        int kind = 1;
        Object extra;
        long start;
        int groundCol;
        int dirtCol;
        float[][] cracks;
        float[][] chunks;
        float[][] ring;
        float[][] burst;
        float[][] embers;
        float[][] wisps;
        float[][] dust;

        Fx() {
        }
    }
}

