package org.figsq.taichicore.taichicore.screen.gui;

import org.figsq.taichicore.taichicore.screen.gui.widget.GuiWidget;

import java.util.ArrayList;
import java.util.List;

public class GuiConfig {
    public final String title;
    public final List<GuiWidget> widgets = new ArrayList<>();

    public GuiConfig(String title, List<GuiWidget> widgets) {
        this.title = title;
        this.widgets.addAll(widgets);
    }
}
