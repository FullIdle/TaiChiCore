package org.figsq.taichicore.taichicore.mixin;

import com.cinemamod.mcef.ModScheme;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(value = ModScheme.class, remap = false)
public class MixinModScheme {
    @ModifyArg(method = "processRequest", at = @At(value = "INVOKE", target = "Ljava/lang/ClassLoader;getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;"))
    public String processRequest(String name) {
        val classLoader = ModScheme.class.getClassLoader();
        if (name.startsWith("/") && classLoader.getResource(name) == null) return name.substring(1);
        return name;
    }
}
