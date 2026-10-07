package org.figsq.taichicore.taichicore.screen.gui.scriptable;

import lombok.Getter;
import lombok.val;

import javax.script.CompiledScript;
import javax.script.ScriptException;

@Getter
public abstract class ScriptableValue<T> {
    private final Object obj;

    public abstract Class<T> type();

    public ScriptableValue(Object obj) {
        this.obj = obj;
    }

    public T get() throws ScriptException {
        if (type().isAssignableFrom(obj.getClass())) return (T) obj;
        if (obj instanceof CompiledScript) ((CompiledScript) obj).eval();
        return (T) obj;
    }

    public T getOrDefault(T defaultValue) throws ScriptException {
        try {
            val t = get();
            if (t == null) return defaultValue;
            return t;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }
}
