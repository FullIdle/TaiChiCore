package org.figsq.taichicore.taichicore.common.comm.config;

/**
 * 数据结构
 */
public class GuiConfig {
    public String identity;
    public Type type;
    public String match;
    public String url;

    public boolean match(String str) {
        return match.equals(str);
    }

    public enum Type {
        OVERWRITE, INDEPENDENT;
    }
}
