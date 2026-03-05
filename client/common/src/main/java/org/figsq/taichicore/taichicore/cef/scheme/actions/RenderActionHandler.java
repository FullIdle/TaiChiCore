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
            val rotX = request.getParam("rotX", "0.0");
            val rotY = request.getParam("rotY", "0.0");
            val rotZ = request.getParam("rotZ", "0.0");
            return TaiChiResponse.binary(
                    RenderHelper.renderPlayerToPng(
                            Integer.parseInt(width),
                            Integer.parseInt(height),
                            Float.parseFloat(scale),
                            Float.parseFloat(rotX),
                            Float.parseFloat(rotY),
                            Float.parseFloat(rotZ)
                    ), type);
        }
        if (argument.equals("item")) {
            val slot = request.getParam("slot", "0");
            val size = request.getParam("size", "64");
            return TaiChiResponse.binary(
                    RenderHelper.renderInventorySlotToPng(Integer.parseInt(slot), Integer.parseInt(size)), type);
        }
        return TaiChiResponse.noContent();
    }
}
