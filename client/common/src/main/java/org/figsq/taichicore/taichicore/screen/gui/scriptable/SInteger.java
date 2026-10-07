package org.figsq.taichicore.taichicore.screen.gui.scriptable;

public class SInteger extends ScriptableValue<Integer> {
    public static final SInteger ZERO = new SInteger(0);

    public SInteger(Object obj) {
        super(obj);
    }

    @Override
    public Class<Integer> type() {
        return Integer.class;
    }
}
