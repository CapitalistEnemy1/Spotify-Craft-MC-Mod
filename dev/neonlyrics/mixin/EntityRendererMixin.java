/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_4604
 *  net.minecraft.class_897
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.neonlyrics.mixin;

import dev.neonlyrics.cosmetic.DeathFx;
import net.minecraft.class_1297;
import net.minecraft.class_4604;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_897.class})
public abstract class EntityRendererMixin {
    @Inject(method={"method_3933(Lnet/minecraft/class_1297;Lnet/minecraft/class_4604;DDD)Z"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void neonlyrics$hideForDeathFx(class_1297 entity, class_4604 frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (DeathFx.hides(entity)) {
            cir.setReturnValue(false);
        }
    }
}

