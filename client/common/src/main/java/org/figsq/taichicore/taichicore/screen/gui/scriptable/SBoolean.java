package org.figsq.taichicore.taichicore.screen.gui.scriptable;

public class SBoolean extends ScriptableValue<Boolean> {
    public static final SBoolean TRUE = new SBoolean(true);
    public static final SBoolean FALSE = new SBoolean(false);


    public SBoolean(Object obj) {
        super(obj);
    }

    @Override
    public Class<Boolean> type() {
        return Boolean.class;
    }
}
