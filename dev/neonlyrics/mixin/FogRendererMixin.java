/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4184
 *  net.minecraft.class_5636
 *  net.minecraft.class_638
 *  net.minecraft.class_758
 *  org.joml.Vector4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.neonlyrics.mixin;

import dev.neonlyrics.sky.WorldSky;
import net.minecraft.class_4184;
import net.minecraft.class_5636;
import net.minecraft.class_638;
import net.minecraft.class_758;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_758.class})
public abstract class FogRendererMixin {
    @Inject(method={"method_62185(Lnet/minecraft/class_4184;FLnet/minecraft/class_638;IF)Lorg/joml/Vector4f;"}, at={@At(value="RETURN")}, cancellable=true, require=0)
    private void neonlyrics$skyFog(class_4184 camera, float pt, class_638 level, int renderDistance, float darken, CallbackInfoReturnable<Vector4f> cir) {
        if (camera.method_19334() != class_5636.field_27888) {
            return;
        }
        Vector4f v = (Vector4f)cir.getReturnValue();
        if (v != null && WorldSky.tintFog(v)) {
            cir.setReturnValue(v);
        }
    }
}

