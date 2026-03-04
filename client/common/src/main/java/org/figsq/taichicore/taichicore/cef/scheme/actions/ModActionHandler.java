package org.figsq.taichicore.taichicore.cef.scheme.actions;

import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.figsq.taichicore.taichicore.cef.scheme.ActionHandler;
import org.figsq.taichicore.taichicore.cef.scheme.TaiChiRequest;
import org.figsq.taichicore.taichicore.cef.scheme.TaiChiResponse;

import java.io.IOException;

public class ModActionHandler implements ActionHandler {
    public static final ModActionHandler INSTANCE = new ModActionHandler();

    @Override
    public TaiChiResponse handle(TaiChiRequest req) {
        val rawArgs = req.getRawArguments();
        val index = rawArgs.indexOf("/");
        if (index == -1) return TaiChiResponse.error("Invalid mod name");
        val modId = rawArgs.substring(0, index);
        val modPath = rawArgs.substring(index + 1);
        val location = ResourceLocation.tryBuild(modId, modPath);
        val minecraft = Minecraft.getInstance();
        val optionalResource = minecraft.getResourceManager().getResource(location);
        if (optionalResource.isEmpty()) return TaiChiResponse.noContent();
        val resource = optionalResource.get();
        try (
                val open = resource.open()
        ){
            return TaiChiResponse.binary(open.readAllBytes(), modPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
