package mapmakingtools.handler;

import mapmakingtools.lib.Constants;
import net.minecraftforge.common.config.Configuration;

/**
 * @author ProPercivalalb
 */
public class ConfigurationHandler {

    public static Configuration config;
	public static void initConfig(Configuration conf) {
	    config = conf;
		config.load();

		loadConfig();

		config.save();
	 }

	 public static void loadConfig(){
         Constants.SHOULD_SHOW_BLOCK_ID_HELPER = config.get("general", "shouldShowBlockIdHelper", true, "Defines whether the block in the chat menu should show").getBoolean(true);
         Constants.RENDER_FILLED_CUBOID = config.get("general", "renderFilledBox", true, "Defines whether the preview selection box should be filled").getBoolean(false);
         Constants.RENDER_SELECTED_POSITION = config.get("general", "renderSelectedPosition", true, "Defines whether to highlight the selected positions").getBoolean(false);
         Constants.RENDER_ALL_BLOCKS = config.get("general", "renderAllBlocks", true, "Defines whether to highlight all blocks inside the selection").getBoolean(false);

         // TODO: Maybe change these two to allow swapping. Like either op or creative, op and creative, op, creative.
         Constants.HAS_TO_BE_OPPED = config.get("general", "opOnly", true, "Defines whether the quick build is only for ops").getBoolean(true);
         Constants.HAS_TO_BE_CREATIVE = config.get("general", "creativeOnly", true, "Defines whether the quick build is only available in creative").getBoolean(true);
     }
}
