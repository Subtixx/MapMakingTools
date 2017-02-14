package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.proxy.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketEditBlock extends AbstractServerMessage {
	
	public BlockPos pos;
	
	public PacketEditBlock() {}
	public PacketEditBlock(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		this.pos = buffer.readBlockPos();
	}
	
	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeBlockPos(this.pos);
	}
	
	@Override
	public void process(EntityPlayer player, Side side) {
		player.openGui(MapMakingTools.instance, CommonProxy.ID_FILTER_BLOCK, player.worldObj, this.pos.getX(), this.pos.getY(), this.pos.getZ());
	}
}
