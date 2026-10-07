package org.figsq.taichicore.taichicore.screen.gui.helper;

public class FunHelper {
    public static <T> T nullOr(T obj, T defaultValue) {
        return obj == null ? defaultValue : obj;
    }
}
