package org.figsq.taichicore.taichicore.cef.scheme.actions;

import lombok.val;
import org.figsq.taichicore.taichicore.cef.scheme.ActionHandler;
import org.figsq.taichicore.taichicore.cef.scheme.TaiChiRequest;
import org.figsq.taichicore.taichicore.cef.scheme.TaiChiResponse;

public class RenderActionHandler implements ActionHandler {
    public static final RenderActionHandler INSTANCE = new RenderActionHandler();

    @Override
    public TaiChiResponse handle(TaiChiRequest request) {
        val argument = request.getArguments()[0].toLowerCase();
        val type = ".png";
        if (argument.equals("player")) {
            val width = request.getParam("width", "256");
            val height = request.getParam("height", "256");
            val scale = request.getParam("scale", "1.0");
            val followMouse = request.getParam("follow_mouse", "false");
            return TaiChiResponse.binary(
                    RenderHelper.renderPlayer(
                            Integer.parseInt(width),
                            Integer.parseInt(height),
                            Float.parseFloat(scale),
                            true,
                            Boolean.parseBoolean(followMouse)
                    ), type);
        }
        if (argument.equals("item")) {
            val slot = request.getParam("slot", "0");
            val size = request.getParam("size", "64");
            return TaiChiResponse.binary(
                    RenderHelper.renderInventorySlot(Integer.parseInt(slot), Integer.parseInt(size), true), type);
        }
        return TaiChiResponse.noContent();
    }
}
