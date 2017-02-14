package mapmakingtools.tools.filter.packet;

import java.io.IOException;
import java.util.List;

import mapmakingtools.container.ContainerFilter;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.util.PacketUtil;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PacketMobArmorRemoveIndex extends AbstractServerMessage {

	public BlockPos pos;
	public int minecartIndex;
	
	public PacketMobArmorRemoveIndex() {}
	public PacketMobArmorRemoveIndex(BlockPos pos, int minecartIndex) {
		this.pos = pos;
		this.minecartIndex = minecartIndex;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.pos = packetbuffer.readBlockPos();
		this.minecartIndex = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.pos);
		packetbuffer.writeInt(this.minecartIndex);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(!PlayerAccess.canEdit(player))
			return;
		TileEntity tile = player.worldObj.getTileEntity(this.pos);
		if(player.openContainer instanceof ContainerFilter) {
			
			ContainerFilter container = (ContainerFilter)player.openContainer;
			if(tile instanceof TileEntityMobSpawner) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;

				List<WeightedSpawnerEntity> minecarts = SpawnerUtil.getRandomMinecarts(spawner.getSpawnerBaseLogic());
				WeightedSpawnerEntity minecart = minecarts.get(this.minecartIndex);
				minecarts.remove(this.minecartIndex);
				if(SpawnerUtil.getRandomMinecart(spawner.getSpawnerBaseLogic()) == minecart)
					spawner.getSpawnerBaseLogic().setNextSpawnData(WeightedRandom.getRandomItem(spawner.getSpawnerBaseLogic().getSpawnerWorld().rand, minecarts));
				PacketUtil.sendTileEntityUpdateToWatching(spawner);
					
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.mobArmor.removeIndex");
				chatComponent.getStyle().setItalic(true);
				player.addChatMessage(chatComponent);
			}
		}
	}

}
