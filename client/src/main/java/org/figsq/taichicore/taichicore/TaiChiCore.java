package org.figsq.taichicore.taichicore;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.figsq.taichicore.taichicore.comm.ForgeCommManager;
import org.figsq.taichicore.taichicore.common.comm.config.GuiConfig;

import java.util.ArrayList;
import java.util.List;

@Mod(
        modid = TaiChiCore.MODID,
        name = TaiChiCore.MODNAME,
        version = TaiChiCore.VERSION,
        dependencies = "required-after:mcef@[1.11,)"
)
public class TaiChiCore {
    public static final String MODID = "taichicore";
    public static final String MODNAME = "TaiChiCore";
    public static final String VERSION = "1.0-SNAPSHOT";


    public static List<GuiConfig> guiConfigs = new ArrayList<>();

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        ForgeCommManager.INSTANCE.init();
    }
}
