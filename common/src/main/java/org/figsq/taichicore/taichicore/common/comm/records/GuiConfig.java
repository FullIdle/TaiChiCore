package org.figsq.taichicore.taichicore.common.comm.records;

import lombok.val;
import org.figsq.taichicore.taichicore.common.util.ScriptUtil;

import javax.annotation.Nullable;
import javax.script.ScriptContext;
import java.util.Objects;

/**
 * 用于，识别，是否长存浏览器于套用在哪些特殊标识的GUI上
 */
public class GuiConfig {
    //该配置的标识
    public String identifier;
    /**
     * 匹配的界面名
     * 额外的成员
     * 该成员可以为空
     * 当为空时，将不被算在套屏幕列表种
     * 只能通过服务端来帮忙打开
     * <pre>
     *     titleName    公式目标判断的名字   String类型
     *     title        屏幕原title        Component类型 不一定支持有些
     * </pre>
     */
    public String matchScript;
    //匹配后打开的TaiChiScreen使用的url
    public String url;
    /**
     * 该界面的浏览器是否可持续存活 这样打开过一次的界面，将不会自动清理下次打开就不会黑一下，且无缝更加丝滑，但如果配置的比较多的情况下，占用的是客户端内存
     */
    public boolean persistent = false;
    /**
     * 当 {@link #persistent} 配置为 {@code true} 时候该选项才有用
     * 当开启持久化后，在开启该项，则玩家在每次打开界面匹配成功后，会重加载一次url （这个改成也是无缝的感觉）
     */
    public boolean reload = false;

    public boolean match(String titleName, Object title) {
        val rs = ScriptUtil.eval(Objects.requireNonNull(this.matchScript, "GuiConfig#match(String titleName) match-script is null"), context -> {
            if (title != null) context.setAttribute("title", title, ScriptContext.ENGINE_SCOPE);
            if (titleName == null) throw new RuntimeException("GuiConfig#match(String titleName) titleName is null");
            context.setAttribute("titleName", titleName, ScriptContext.ENGINE_SCOPE);
        });
        if (rs == null) throw new RuntimeException("GuiConfig#match(String titleName) script return null");
        if (rs instanceof Boolean) return (Boolean) rs;
        if (rs instanceof String) return rs.equals(titleName);
        throw new RuntimeException("GuiConfig#match(String titleName) script return " + rs.getClass().getName());
    }

    public boolean match(String titleName) {
        return match(titleName, null);
    }
}
