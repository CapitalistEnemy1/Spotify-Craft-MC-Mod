/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_11909
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_342
 *  net.minecraft.class_364
 *  net.minecraft.class_437
 */
package dev.neonlyrics.gui;

import dev.neonlyrics.License;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.config.Storage;
import dev.neonlyrics.hud.Gfx;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.time.LocalDateTime;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_437;

public class LicenseScreen
extends class_437 {
    private static final int ACCENT = 3528347;
    private class_342 box;
    private String status = "";
    private int statusCol = 0xFFFFFF;
    private long okTime = -1L;
    private int bx;
    private int by;
    private int bw;
    private int bh;
    private int px;
    private int py;
    private int pw;
    private int ph;

    public LicenseScreen() {
        super((class_2561)class_2561.method_43470((String)"SpotifyCraft"));
    }

    private static String tr(String key) {
        return class_2561.method_43471((String)("neonlyrics.lic." + key)).getString();
    }

    protected void method_25426() {
        int w = Math.min(320, this.field_22789 - 40);
        this.box = new class_342(this.field_22793, this.field_22789 / 2 - w / 2 + 12, this.field_22790 / 2 + 4, w - 24, 16, (class_2561)class_2561.method_43470((String)""));
        this.box.method_1880(200);
        this.box.method_47404((class_2561)class_2561.method_43470((String)"LCORD-XXXX-XXXX-XXXX"));
        String old = NeonLyrics.cfg().licenseCode;
        this.box.method_1852(old.startsWith("R1:") ? "" : old);
        this.method_37063(this.box);
        this.method_48265((class_364)this.box);
    }

    public void method_25394(class_332 g, int mouseX, int mouseY, float delta) {
        int w = Math.min(320, this.field_22789 - 40);
        int h = 150;
        int x = this.field_22789 / 2 - w / 2;
        int y = this.field_22790 / 2 - 70;
        Gfx.aaRound(g, x, y, w, h, 12.0f, 726803, 0.96f);
        Gfx.aaRound(g, x, y, w, 2.0f, 1.0f, 3528347, 0.9f);
        Gfx.center(g, LicenseScreen.tr("title"), this.field_22789 / 2, y + 14, 0xFFFFFF, 1.0f, true);
        Gfx.center(g, LicenseScreen.tr("account") + " " + License.name(), this.field_22789 / 2, y + 32, 10479300, 1.0f, false);
        Gfx.center(g, LicenseScreen.tr("hint"), this.field_22789 / 2, y + 48, 0xAAAAB5, 1.0f, false);
        Gfx.aaRound(g, x + 10, this.field_22790 / 2, w - 20, 24.0f, 6.0f, 0, 0.5f);
        super.method_25394(g, mouseX, mouseY, delta);
        this.ph = 18;
        this.pw = (w - 30) / 2;
        this.px = x + 10;
        this.py = y + h - 34;
        this.bx = this.px + this.pw + 10;
        this.by = this.py;
        this.bw = this.pw;
        this.bh = this.ph;
        boolean ovP = mouseX >= this.px && mouseY >= this.py && mouseX < this.px + this.pw && mouseY < this.py + this.ph;
        boolean ovB = mouseX >= this.bx && mouseY >= this.by && mouseX < this.bx + this.bw && mouseY < this.by + this.bh;
        Gfx.aaRound(g, this.px, this.py, this.pw, this.ph, 9.0f, 0xFFFFFF, ovP ? 0.18f : 0.09f);
        Gfx.center(g, LicenseScreen.tr("paste"), this.px + this.pw / 2, this.py + 5, 0xFFFFFF, 1.0f, false);
        Gfx.aaRound(g, this.bx, this.by, this.bw, this.bh, 9.0f, 3528347, ovB ? 1.0f : 0.8f);
        Gfx.center(g, LicenseScreen.tr("unlock"), this.bx + this.bw / 2, this.by + 5, 0xFFFFFF, 1.0f, true);
        if (!this.status.isEmpty()) {
            Gfx.center(g, this.status, this.field_22789 / 2, y + h + 8, this.statusCol, 1.0f, true);
        }
        if (this.okTime > 0L && System.currentTimeMillis() - this.okTime > 900L) {
            this.method_25419();
        }
    }

    public boolean method_25402(class_11909 event, boolean doubleClick) {
        if (event.method_74245() == 0) {
            double mx = event.comp_4798();
            double my = event.comp_4799();
            if (mx >= (double)this.px && my >= (double)this.py && mx < (double)(this.px + this.pw) && my < (double)(this.py + this.ph)) {
                this.box.method_1852(this.field_22787.field_1774.method_1460().trim());
                return true;
            }
            if (mx >= (double)this.bx && my >= (double)this.by && mx < (double)(this.bx + this.bw) && my < (double)(this.by + this.bh)) {
                this.tryUnlock();
                return true;
            }
        }
        return super.method_25402(event, doubleClick);
    }

    private void unlocked() {
        NeonLyrics.saveConfig();
        this.status = LicenseScreen.tr("ok");
        this.statusCol = 8257434;
        this.okTime = System.currentTimeMillis();
    }

    private void tryUnlock() {
        String code = this.box.method_1882().trim();
        String u = License.uuid();
        Config cf = NeonLyrics.cfg();
        if (License.isRedeemCode(code)) {
            String n = License.normalize(code);
            String owner = cf.usedCodes.get(n);
            if (owner != null && !owner.equals(u)) {
                this.status = LicenseScreen.tr("used");
                this.statusCol = 0xFF6A6A;
                return;
            }
            cf.usedCodes.put(n, u);
            cf.licenseCode = "R1:" + n + ":" + u;
            this.unlocked();
        } else if (License.check(u, code)) {
            cf.licenseCode = code;
            this.unlocked();
        } else {
            String n = License.normalize(code);
            this.status = LicenseScreen.tr("bad") + (String)(n.isEmpty() ? "" : "  (" + (String)(n.length() > 22 ? n.substring(0, 22) + "..." : n) + ")");
            this.statusCol = 0xFF6A6A;
        }
        try {
            Files.writeString(Storage.dir().resolve("license-status.txt"), (CharSequence)(String.valueOf(LocalDateTime.now()) + "\nAccount: " + License.name() + " " + u + "\nEingabe-Laenge: " + code.length() + "\nGelesen: " + License.normalize(code) + "\nErgebnis: " + this.status + "\n"), new OpenOption[0]);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

