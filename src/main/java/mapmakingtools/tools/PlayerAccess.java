package mapmakingtools.tools;

import mapmakingtools.lib.Constants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * @author ProPercivalalb
 */
public class PlayerAccess {

	public static boolean canEdit(EntityPlayer player) {
		if(player == null)
			return false;
		
		//boolean isCreativeMode = player.capabilities.isCreativeMode || !Constants.HAS_TO_BE_CREATIVE;
		boolean isCreativeMode = player.isCreative() || !Constants.HAS_TO_BE_CREATIVE;
        boolean isOp = (FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOppedPlayers().getGameProfileFromName(player.getName()) != null ||
                FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer()) || !Constants.HAS_TO_BE_OPPED;

		return isCreativeMode && isOp;
	}
	
	public static boolean canSeeBlockIdHelper(EntityPlayer player) {
		return canEdit(player) && Constants.SHOULD_SHOW_BLOCK_ID_HELPER;
	}
}
