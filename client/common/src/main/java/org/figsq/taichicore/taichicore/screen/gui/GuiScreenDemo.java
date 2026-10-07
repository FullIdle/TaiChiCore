package org.figsq.taichicore.taichicore.screen.gui;

import lombok.val;
import org.figsq.taichicore.taichicore.screen.gui.scriptable.SBoolean;
import org.figsq.taichicore.taichicore.screen.gui.scriptable.SInteger;
import org.figsq.taichicore.taichicore.screen.gui.widget.GuiWidget;
import org.figsq.taichicore.taichicore.screen.gui.widget.InputWidget;
import org.figsq.taichicore.taichicore.screen.gui.widget.TextWidget;
import org.figsq.taichicore.taichicore.screen.gui.widget.TextureWidget;

import java.util.ArrayList;

public class GuiScreenDemo extends GuiScreen {
    public GuiScreenDemo() {
        super(demoConfig());
    }

    public static GuiConfig demoConfig() {
        val guiWidgets = new ArrayList<GuiWidget>();
        val textureWidget = new TextureWidget(null);
        val SInt100 = new SInteger(100);
        textureWidget.x = SInt100;
        textureWidget.y = SInt100;
        textureWidget.width = SInt100;
        textureWidget.height = SInt100;


        val helloWorld = new TextWidget("Hello world");
        helloWorld.center = SBoolean.TRUE;
        helloWorld.x = SInt100;
        helloWorld.y = SInt100;
        helloWorld.width = SInt100;
        helloWorld.height = SInt100;
        val SInt1 = new SInteger(1);
        helloWorld.z = SInt1;

        val input = new InputWidget("请输入XXX");
        input.x = SInt100;
        input.y = SInt100;
        input.width = SInt100;
        input.height = new SInteger(20);
        input.z = SInt1;
        input.bordered = SBoolean.FALSE;

        guiWidgets.add(helloWorld);
        guiWidgets.add(input);
        guiWidgets.add(textureWidget);
        val config = new GuiConfig("Demo",guiWidgets);
        return config;
    }
}
