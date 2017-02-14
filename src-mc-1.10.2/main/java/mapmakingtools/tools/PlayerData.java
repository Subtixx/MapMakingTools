package mapmakingtools.tools;

import java.util.Hashtable;
import java.util.UUID;

import mapmakingtools.MapMakingTools;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketSetPoint1;
import mapmakingtools.network.packet.PacketSetPoint2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class PlayerData {

	private Hashtable<Integer, BlockPos> pos1 = new Hashtable<Integer, BlockPos>();
	private Hashtable<Integer, BlockPos> pos2 = new Hashtable<Integer, BlockPos>();
	private ActionStorage actionStorage = new ActionStorage(this);
	
	public UUID uuid;
	
	public PlayerData(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}
	public PlayerData(UUID uuid) {
		this.uuid = uuid;
	}
	
	public EntityPlayer getPlayer() {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			return MapMakingTools.proxy.getPlayerEntity();	
		
		System.out.println(uuid);
		for(Object obj : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList()) {
			EntityPlayerMP player = (EntityPlayerMP)obj;
			if(player.getUniqueID().equals(this.uuid)) {
				System.out.println("player");
				return player;
			}
		}
		
		return null;
	}
	
	public World getPlayerWorld() {
		return this.getPlayer().worldObj;
	}
	
	public BlockPos getFirstPoint() {	
		return this.pos1.get(this.getPlayer().worldObj.provider.getDimension());
	}
	
	public BlockPos getSecondPoint() {
		return this.pos2.get(this.getPlayer().worldObj.provider.getDimension());
	}
	
	public int[] getSelectionSize() {
		return new int[] {this.getMaxX() - this.getMinX() + 1, this.getMaxY() - this.getMinY() + 1, this.getMaxZ() - this.getMinZ() + 1};
	}
	
	public int getBlockCount() {
		return (this.getMaxX() - this.getMinX() + 1) * (this.getMaxZ() - this.getMinZ() + 1) * (this.getMaxY() - this.getMinY() + 1);
	}
	
	public boolean hasSelectedPoints() {
		return this.getFirstPoint() != null && this.getSecondPoint() != null;
	}
	
	public boolean setFirstPoint(BlockPos pos) {
		int dimId = this.getPlayer().worldObj.provider.getDimension();
		if(pos != null)
			this.pos1.put(dimId, pos);
		else
			this.pos1.remove(dimId);
		return true;
	}
	
	public boolean setPoints(BlockPos pos1, BlockPos pos2) {
		this.setFirstPoint(pos1);
		this.setSecondPoint(pos2);
		return true;
	}
	
	public boolean setSecondPoint(BlockPos pos) {
		int dimId = this.getPlayer().worldObj.provider.getDimension();
		if(pos != null)
			this.pos2.put(dimId, pos);
		else
			this.pos2.remove(dimId);
		return true;
	}
	
	public void sendUpdateToClient() {
		PacketDispatcher.sendTo(new PacketSetPoint1(this.getFirstPoint()), this.getPlayer());
		PacketDispatcher.sendTo(new PacketSetPoint2(this.getSecondPoint()), this.getPlayer());
	}
	
	public ActionStorage getActionStorage() {
		return this.actionStorage;
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList list1 = new NBTTagList();
		NBTTagList list2 = new NBTTagList();
		
		for(int key : this.pos1.keySet()) {
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.setInteger("dim", key);
			tag1.setLong("pos", pos1.get(key).toLong());
			list1.appendTag(tag1);
		}
		
		for(int key : this.pos2.keySet()) {
			NBTTagCompound tag2 = new NBTTagCompound();
			tag2.setInteger("dim", key);
			tag2.setLong("pos", pos2.get(key).toLong());
			list2.appendTag(tag2);
		}
		
		tag.setString("uuid", this.uuid.toString());
		tag.setTag("firstPoint", list1);
		tag.setTag("secondPoint", list2);
		
		return tag;
	}
	
	public PlayerData readFromNBT(NBTTagCompound tag) {
		NBTTagList list1 = (NBTTagList)tag.getTag("firstPoint");
		NBTTagList list2 = (NBTTagList)tag.getTag("secondPoint");
		
		for(int i = 0; i < list1.tagCount(); ++i) {
			NBTTagCompound tag1 = list1.getCompoundTagAt(i);
			int dim = tag1.getInteger("dim");
			pos1.put(dim, BlockPos.fromLong(tag1.getLong("pos")));
		}
		
		for(int i = 0; i < list2.tagCount(); ++i) {
			NBTTagCompound tag2 = list2.getCompoundTagAt(i);
			int dim = tag2.getInteger("dim");
			pos2.put(dim, BlockPos.fromLong(tag2.getLong("pos")));
		}
		
		this.uuid = UUID.fromString(tag.getString("uuid"));
		return this;
	}
	
	public BlockPos getMinPos() {
		return new BlockPos(this.getMinX(), this.getMinY(), this.getMinZ());
	}
	
	public BlockPos getMaxPos() {
		return new BlockPos(this.getMaxX(), this.getMaxY(), this.getMaxZ());
	}
	
	public int getMinX() {
		return Math.min(this.getFirstPoint().getX(), this.getSecondPoint().getX());
	}
	public int getMinY() {
		return Math.min(this.getFirstPoint().getY(), this.getSecondPoint().getY());
	}
	public int getMinZ() {
		return Math.min(this.getFirstPoint().getZ(), this.getSecondPoint().getZ());
	}
	public int getMaxX() {
		return Math.max(this.getFirstPoint().getX(), this.getSecondPoint().getX());
	}
	public int getMaxY() {
		return Math.max(this.getFirstPoint().getY(), this.getSecondPoint().getY());
	}
	public int getMaxZ() {
		return Math.max(this.getFirstPoint().getZ(), this.getSecondPoint().getZ());
	}
	
	@Override
	public String toString() {
		return "PlayerData=[UUID=" + this.uuid +"]";
	}
}
