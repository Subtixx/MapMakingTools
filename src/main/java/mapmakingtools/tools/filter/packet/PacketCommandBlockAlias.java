package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.helper.LogHelper;
import mapmakingtools.helper.ServerHelper;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketCommandBlockAlias extends AbstractServerMessage {

	public BlockPos pos;
	public String name;
	
	public PacketCommandBlockAlias() {}
	public PacketCommandBlockAlias(BlockPos pos, String name) {
		this.pos = pos;
		this.name = name;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = packetbuffer.readBlockPos();
		this.name = packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4);
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.pos);
		packetbuffer.writeString(this.name);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;

		TileEntity tile = player.worldObj.getTileEntity(this.pos);
		if(tile instanceof TileEntityCommandBlock) {
			LogHelper.info("YES");
			TileEntityCommandBlock commandBlock = (TileEntityCommandBlock)tile;
			commandBlock.getCommandBlockLogic().setName(this.name);
				//player.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("filter.commandBlockName.complete", name));
				if(ServerHelper.isServer()) {
	    			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
	    			server.getPlayerList().sendPacketToAllPlayersInDimension(commandBlock.getUpdatePacket(), commandBlock.getWorld().provider.getDimension());
    			}
    			
    			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.commandblockalias.complete", this.name);
				chatComponent.getStyle().setItalic(true);
				player.addChatMessage(chatComponent);
			
		}
	}
}
