/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_7172
 */
package dev.neonlyrics.util;

import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import net.minecraft.class_310;
import net.minecraft.class_7172;

public final class FpsBoost {
    private FpsBoost() {
    }

    public static void apply(boolean on) {
        class_310 mc = class_310.method_1551();
        Config c = NeonLyrics.cfg();
        try {
            if (on) {
                c.boostBefore = FpsBoost.save(mc.field_1690.method_42435()) + ";" + FpsBoost.save(mc.field_1690.method_42475()) + ";" + FpsBoost.save(mc.field_1690.method_41805());
                FpsBoost.set(mc.field_1690.method_42435(), "false");
                FpsBoost.set(mc.field_1690.method_42475(), "MINIMAL");
                FpsBoost.set(mc.field_1690.method_41805(), "0");
                c.quality = 0;
            } else if (c.boostBefore != null && !c.boostBefore.isEmpty()) {
                String[] v = c.boostBefore.split(";", -1);
                if (v.length >= 3) {
                    FpsBoost.set(mc.field_1690.method_42435(), v[0]);
                    FpsBoost.set(mc.field_1690.method_42475(), v[1]);
                    FpsBoost.set(mc.field_1690.method_41805(), v[2]);
                }
                c.boostBefore = "";
                if (c.quality == 0) {
                    c.quality = 1;
                }
            }
            mc.field_1690.method_1640();
        }
        catch (Exception e) {
            NeonLyrics.LOG.warn("FPS-Boost konnte nicht alles umstellen", (Throwable)e);
        }
        c.fpsBoost = on;
        NeonLyrics.saveConfig();
    }

    private static String save(class_7172<?> o) {
        String string;
        Object v = o.method_41753();
        if (v instanceof Enum) {
            Enum e = (Enum)v;
            string = e.name();
        } else {
            string = String.valueOf(v);
        }
        return string;
    }

    private static void set(class_7172 o, String value) {
        Object nv;
        Object cur = o.method_41753();
        if (cur instanceof Boolean) {
            nv = Boolean.parseBoolean(value);
        } else if (cur instanceof Integer) {
            nv = Integer.parseInt(value);
        } else if (cur instanceof Enum) {
            Enum e = (Enum)cur;
            nv = Enum.valueOf(e.getDeclaringClass(), value);
        } else {
            return;
        }
        o.method_41748((Object)nv);
    }
}

