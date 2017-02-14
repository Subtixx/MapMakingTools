package mapmakingtools.tools;

import mapmakingtools.handler.ConfigurationHandler;
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

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if(ConfigurationHandler.forceCreative && !player.isCreative())
            return false;
        if(ConfigurationHandler.forceOp && (server.getPlayerList().getOppedPlayers().getGameProfileFromName(player.getName()) == null &&
                !server.isSinglePlayer()))
            return false;

        return true;
	}
	
	public static boolean canSeeBlockIdHelper(EntityPlayer player) {
		return canEdit(player) && ConfigurationHandler.showBlockIdHelper;
	}
}
