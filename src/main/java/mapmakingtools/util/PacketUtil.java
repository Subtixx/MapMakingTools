package mapmakingtools.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.WorldServer;

public class PacketUtil {

	public static void sendTileEntityUpdateToWatching(TileEntity tileEntity) {
		if(tileEntity.getWorld() instanceof WorldServer) {

			for(Object obj : tileEntity.getWorld().getMinecraftServer().getPlayerList().getPlayerList()) {
				EntityPlayerMP player = (EntityPlayerMP)obj;
				if(((WorldServer)tileEntity.getWorld()).getPlayerChunkMap().isPlayerWatchingChunk(player, tileEntity.getPos().getX() >> 4, tileEntity.getPos().getZ() >> 4));
					player.connection.sendPacket(tileEntity.getUpdatePacket());
			}
		}
	}

}
