package mapmakingtools.handler;

import mapmakingtools.lib.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

/**
 * @author ProPercivalalb
 */
public class ConfigurationHandler {
    public static Configuration config;

    public static boolean showBlockIdHelper = true;
    public static boolean renderFilledBox = true;
    public static boolean renderSelectedPosition = true;
    public static boolean renderAllBlock = false;

    public static boolean forceOp = true;
    public static boolean forceCreative = true;

    public static void loadConfig(File configFile) {
        config = new Configuration(configFile);

        config.load();
        load();

        MinecraftForge.EVENT_BUS.register(ChangeListener.class);
    }

	 public static void load(){
         showBlockIdHelper = config.get("general", "shouldShowBlockIdHelper", true, "Defines whether the block in the chat menu should show").getBoolean(true);
         renderFilledBox = config.get("general", "renderFilledBox", true, "Defines whether the preview selection box should be filled").getBoolean(false);
         renderSelectedPosition = config.get("general", "renderSelectedPosition", true, "Defines whether to highlight the selected positions").getBoolean(false);
         renderAllBlock = config.get("general", "renderAllBlocks", false, "Defines whether to highlight all blocks inside the selection").getBoolean(false);

         // TODO: Maybe change these two to allow swapping. Like either op or creative, op and creative, op, creative.
         forceOp = config.get("general", "opOnly", true, "Defines whether the quick build is only for ops").getBoolean(true);
         forceCreative = config.get("general", "creativeOnly", true, "Defines whether the quick build is only available in creative").getBoolean(true);

         if(config.hasChanged())
             config.save();
     }

    public static class ChangeListener {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
            if(eventArgs.getModID().equals(Reference.MOD_ID))
                load();
        }
    }
}
