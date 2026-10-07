package org.figsq.taichicore.taichicore.screen.gui.helper;

import lombok.val;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.figsq.taichicore.taichicore.TaiChiCore;

import javax.script.ScriptException;

public class LoggerHelper {
    public static void error(ScriptException e) {
        val message = e.getMessage();
        String builder = "ScriptException: " +
                message +
                "   FileName: " + e.getFileName() +
                "   LineNumber: " + e.getLineNumber() +
                "   ColumnNumber: " + e.getColumnNumber();
        error(builder);
    }

    public static void error(String message) {
        val minecraft = Minecraft.getInstance();
        val player = minecraft.player;
        if (player != null) {
            player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.RED));
            return;
        }
        TaiChiCore.LOGGER.error(message);
    }
}
