package org.figsq.taichicore.taichicore.config;

import lombok.Getter;
import lombok.val;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.comm.PluginCommManager;
import org.figsq.taichicore.taichicore.common.comm.packets.client.NavigatePacket;
import org.figsq.taichicore.taichicore.common.comm.packets.client.OpenGuiConfigPacket;
import org.figsq.taichicore.taichicore.common.comm.packets.client.OpenUrlPacket;
import org.figsq.taichicore.taichicore.common.comm.packets.common.CustomPacket;
import org.openjdk.nashorn.api.scripting.AbstractJSObject;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomPacketHandlerManager {
    public static final File FOLDER = new File(TaiChiCore.getInstance().getDataFolder(), "handlers");
    private static final Map<String, Handler> handlers = new HashMap<>();

    public static void load() {
        handlers.clear();
        val plugin = TaiChiCore.getInstance();
        if (!FOLDER.exists()) {
            FOLDER.mkdirs();
            plugin.saveResource("handlers/example.yml", false);
        }
        val files = FOLDER.listFiles();
        if (files != null) for (File file : files)
            try {
                val handler = new Handler(YamlConfiguration.loadConfiguration(file));
                handlers.put(handler.getIdentifier(), handler);
            } catch (ScriptException e) {
                plugin.getLogger().warning("Failed to load handler " + file.getName());
                throw new RuntimeException(e);
            }
    }

    public static boolean handle(CustomPacket packet, Player player) {
        val identifier = packet.identifier;
        val handler = handlers.get(identifier);
        if (handler == null) return false;
        try {
            handler.handle(packet, player);
        } catch (ScriptException e) {
            val frames = ExceptionUtils.getStackFrames(e);
            val logger = TaiChiCore.getInstance().getLogger();
            logger.warning("Error while handling packet " + identifier);
            for (String frame : frames) logger.warning(frame);
        }
        return true;
    }

    @Getter
    public static class Handler {
        private final String identifier;
        private final YamlConfiguration con;
        private final CompiledScript script;

        public Handler(YamlConfiguration con) throws ScriptException {
            if (!con.contains("identifier")) throw new IllegalArgumentException("Missing identifier");
            this.con = con;
            this.identifier = this.con.getString("identifier");
            this.script = TaiChiCore.getScriptEngine().compile(getHandler());
        }

        public String getHandler() {
            return this.con.getString("handler");
        }

        public void handle(CustomPacket packet, Player player) throws ScriptException {
            this.script.eval(createScriptContext(packet, player));
        }

        public ScriptContext createScriptContext(CustomPacket packet, Player player) {
            val context = new SimpleScriptContext();
            context.setAttribute("data", packet.data, ScriptContext.ENGINE_SCOPE);
            context.setAttribute("player", player, ScriptContext.ENGINE_SCOPE);
            context.setAttribute("Bukkit", Bukkit.class, ScriptContext.ENGINE_SCOPE);
            context.setAttribute("handler", this, ScriptContext.ENGINE_SCOPE);

            context.setAttribute(
                    "papi",
                    (Function<String, String>) s ->
                            PlaceholderAPI.setPlaceholders(player, s),
                    ScriptContext.ENGINE_SCOPE
            );
            context.setAttribute(
                    "packet",
                    new AbstractJSObject() {
                        @Override
                        public Object call(Object thiz, Object... args) {
                            if (args.length < 2) throw new IllegalArgumentException("Missing arguments");
                            val identifier = args[0].toString();
                            val data = args[1].toString();
                            val packet = new CustomPacket(identifier, data);
                            val target = args.length > 2 ? ((Player) args[2]) : player;
                            PluginCommManager.INSTANCE.sendTo(target, packet);
                            return null;
                        }
                    },
                    ScriptContext.ENGINE_SCOPE
            );
            context.setAttribute(
                    "tell",
                    new AbstractJSObject() {
                        @Override
                        public Object call(Object thiz, Object... args) {
                            for (Object arg : args) player.sendMessage(arg.toString());
                            return null;
                        }
                    },
                    ScriptContext.ENGINE_SCOPE
            );
            context.setAttribute(
                    "bc",
                    new AbstractJSObject() {
                        @Override
                        public Object call(Object thiz, Object... args) {
                            for (Object arg : args) Bukkit.broadcastMessage(arg.toString());
                            return null;
                        }
                    },
                    ScriptContext.ENGINE_SCOPE
            );
            context.setAttribute(
                    "title",
                    (Consumer<String>) s ->
                            player.sendTitle(s,"", 7, 20, 7),
                    ScriptContext.ENGINE_SCOPE
            );
            context.setAttribute(
                    "close",
                    (Runnable) player::closeInventory,
                    ScriptContext.ENGINE_SCOPE
            );
            context.setAttribute(
                    "open",
                    (Consumer<String>) s ->
                            PluginCommManager.INSTANCE.sendTo(player, new OpenUrlPacket(s)),
                    ScriptContext.ENGINE_SCOPE
            );
            context.setAttribute(
                    "navigate",
                    (BiConsumer<String, Boolean>) (url,force) ->
                            PluginCommManager.INSTANCE.sendTo(player, new NavigatePacket(url, force)),
                    ScriptContext.ENGINE_SCOPE
            );
            context.setAttribute(
                    "gui",
                    (Consumer<String>) (url) ->
                            PluginCommManager.INSTANCE.sendTo(player, new OpenGuiConfigPacket(url)),
                    ScriptContext.ENGINE_SCOPE
            );
            context.setAttribute(
                    "command",
                    new AbstractJSObject() {
                        @Override
                        public Object call(Object thiz, Object... args) {
                            for (Object arg : args) Bukkit.dispatchCommand(player, arg.toString());
                            return null;
                        }
                    },
                    ScriptContext.ENGINE_SCOPE
            );
            context.setAttribute(
                    "console",
                    new AbstractJSObject() {
                        @Override
                        public Object call(Object thiz, Object... args) {
                            for (Object arg : args) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), arg.toString());
                            return null;
                        }
                    },
                    ScriptContext.ENGINE_SCOPE
            );
            context.setAttribute(
                    "op",
                    new AbstractJSObject() {
                        @Override
                        public Object call(Object thiz, Object... args) {
                            val op = player.isOp();
                            try {
                                player.setOp(true);
                                for (Object arg : args) Bukkit.dispatchCommand(player, arg.toString());
                            } catch (Exception e){
                                throw new RuntimeException(e);
                            } finally {
                                player.setOp(op);
                            }
                            return null;
                        }
                    },
                    ScriptContext.ENGINE_SCOPE
            );
            return context;
        }
    }
}
