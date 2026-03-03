package org.figsq.taichicore.taichicore.common.util;

import lombok.val;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.util.function.Consumer;

public class ScriptUtil {
    public static final NashornScriptEngine SCRIPT_ENGINE = (NashornScriptEngine) new NashornScriptEngineFactory().getScriptEngine(ScriptUtil.class.getClassLoader());

    public static Object eval(String script) {
        return eval(script, null);
    }

    public static Object eval(String script, Consumer<ScriptContext> extras) {
        val context = addTaiChiAttributes(new SimpleScriptContext());
        if (extras != null) extras.accept(context);
        try {
            return SCRIPT_ENGINE.eval(script, context);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends ScriptContext> T addTaiChiAttributes(T context) {
        //TODO
        return context;
    }
}
