package org.figsq.taichicore.taichicore.cef.scheme.actions;

import lombok.val;
import org.figsq.taichicore.taichicore.cef.scheme.ActionHandler;
import org.figsq.taichicore.taichicore.cef.scheme.TaiChiRequest;
import org.figsq.taichicore.taichicore.cef.scheme.TaiChiResponse;
import org.figsq.taichicore.taichicore.screen.RenderHelper;

public class RenderActionHandler implements ActionHandler {
    public static final RenderActionHandler INSTANCE = new RenderActionHandler();

    @Override
    public TaiChiResponse handle(TaiChiRequest request) {
        val argument = request.getArguments()[0].toLowerCase();
        val type = ".png";
        switch (argument) {
            case "player", "entity": {
                val width = Integer.parseInt(request.getParam("width", "256"));
                val height = Integer.parseInt(request.getParam("height", "256"));
                val scale = Float.parseFloat(request.getParam("scale", "1.0"));
                val followMouse = Boolean.parseBoolean(request.getParam("follow_mouse", "false"));
                val data = argument.equals("entity") ?
                        RenderHelper.renderEntity(
                                Integer.parseInt(request.getParam("entity_id", "-1")),
                                width,
                                height,
                                scale,
                                true,
                                followMouse
                        ) :
                        RenderHelper.renderPlayer(
                                width,
                                height,
                                scale,
                                true,
                                followMouse
                        );
                return TaiChiResponse.binary(data, type);
            }
            case "item": {
                val slot = request.getParam("slot", "0");
                val size = request.getParam("size", "64");
                return TaiChiResponse.binary(
                        RenderHelper.renderInventorySlot(Integer.parseInt(slot), Integer.parseInt(size), true), type);
            }
        }
        return TaiChiResponse.noContent();
    }
}
