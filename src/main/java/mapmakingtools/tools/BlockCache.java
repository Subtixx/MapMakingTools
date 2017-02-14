package mapmakingtools.tools;


import java.io.IOException;
import java.util.UUID;

import io.netty.buffer.Unpooled;
import mapmakingtools.api.enums.MovementType;
import mapmakingtools.helper.BlockPosHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author ProPercivalalb
 * This class is based off {@link net.minecraftforge.common.util.BlockSnapshot}
 */
public class BlockCache {

	public BlockPos playerPos;
    public  BlockPos pos;
    public int dimId;
    private NBTTagCompound nbt;
    public ResourceLocation blockIdentifier;
    public Block block;
    public int meta;
    
    public IBlockState replacedBlock;
    public World world;

    private BlockCache() {}
    
    private BlockCache(BlockPos playerPos, World world, BlockPos pos, IBlockState state) {
        this.playerPos = playerPos;
    	this.world = world;
        this.dimId = world.provider.getDimension();
        this.pos = pos;
        this.replacedBlock = state;
        this.block = state.getBlock();
        this.blockIdentifier = this.block.getRegistryName();
        this.meta = this.block.getMetaFromState(state);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null) {
            this.nbt = new NBTTagCompound();
            tileEntity.writeToNBT(this.nbt);
        }
        else 
        	this.nbt = null;
    }

    private BlockCache(BlockPos playerPos, int dimension, BlockPos pos, String modid, String blockName, int meta, NBTTagCompound nbt) {
    	this.playerPos = playerPos;
    	this.world = DimensionManager.getWorld(dimension);
    	this.dimId = dimension;
        this.pos = pos;
        this.block = Block.getBlockFromName(modid + ":" + blockName);
        this.replacedBlock = this.block.getStateFromMeta(meta);
        this.blockIdentifier = new ResourceLocation(modid, blockName);
        this.meta = meta;
        this.nbt = nbt;
    }
    
    private BlockCache(BlockPos playerPos, int dimension, BlockPos pos, ResourceLocation resource, int meta, NBTTagCompound nbt) {
    	this.playerPos = playerPos;
    	this.world = DimensionManager.getWorld(dimension);
    	this.dimId = dimension;
        this.pos = pos;
        this.block = Block.getBlockFromName(resource.toString());
        this.replacedBlock = this.block.getStateFromMeta(meta);
        this.blockIdentifier = resource;
        this.meta = meta;
        this.nbt = nbt;
    }

	public static BlockCache createCache(EntityPlayer player, World world, BlockPos pos) {
        return new BlockCache(new BlockPos(player), world, pos, world.getBlockState(pos));
    }
    
    public static BlockCache createCache(BlockPos playerPos, World world, BlockPos pos) {
        return new BlockCache(playerPos, world, pos, world.getBlockState(pos));
    }

    public static BlockCache readFromNBT(NBTTagCompound tag) {
        NBTTagCompound nbt = tag.getBoolean("hasTE") ? null : tag.getCompoundTag("tileEntity");

        return new BlockCache(
        		BlockPos.fromLong(tag.getLong("playerPos")),
        		tag.getInteger("dimension"),
                BlockPos.fromLong(tag.getLong("blockPos")),
                tag.getString("blockMod"),
                tag.getString("blockName"),
                tag.getByte("metadata"),
                nbt);
    }
    
    public static BlockCache readFromPacketBuffer(PacketBuffer packetbuffer) throws IOException {
        return new BlockCache(
        		packetbuffer.readBlockPos(),
        		packetbuffer.readInt(),
        		packetbuffer.readBlockPos(),
        		new ResourceLocation(packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4)),
                packetbuffer.readByte(),
                packetbuffer.readNBTTagCompoundFromBuffer());
    }
    
    public static BlockCache readFromPacketBufferCompact(PacketBuffer packetbuffer) throws IOException {
        BlockCache cache = new BlockCache();
        ResourceLocation resource = new ResourceLocation(packetbuffer.readStringFromBuffer(Integer.MAX_VALUE / 4));
        cache.block = Block.getBlockFromName(resource.toString());
        cache.replacedBlock = cache.block.getStateFromMeta(packetbuffer.readByte());
        cache.blockIdentifier = resource;
        cache.nbt = packetbuffer.readNBTTagCompoundFromBuffer();
        return cache;
    }

    public boolean restore(boolean applyPhysics) {
        return this.restoreToLocation(this.world, this.pos, applyPhysics);
    }

    public boolean restoreToLocation(World world, BlockPos pos, boolean applyPhysics) {

        world.setBlockState(pos, this.replacedBlock, applyPhysics ? 3 : 2);
        world.notifyBlockUpdate(pos, replacedBlock, replacedBlock, 0);
        if (this.nbt != null) {
        	TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity != null) {
                tileEntity.readFromNBT(this.nbt);
                tileEntity.setPos(pos);
            }
        }
        
        return true;
    }
    
    public BlockCache restoreRelativeToRotated(PlayerData data, MovementType movementType) { 
		BlockPos newPos = BlockPosHelper.subtract(this.pos, this.playerPos);
		BlockPos newPlayerPos = new BlockPos(data.getPlayer());
		
		if(movementType.equals(MovementType._090_))
			newPos = new BlockPos(-newPos.getZ(), newPos.getY(), newPos.getX());
		else if(movementType.equals(MovementType._180_))
			newPos = new BlockPos(-newPos.getX(), newPos.getY(), -newPos.getZ());
		else if(movementType.equals(MovementType._270_))
			newPos = new BlockPos(newPos.getZ(), newPos.getY(), -newPos.getX());
		
		newPos = newPos.add(newPlayerPos);
		
		BlockCache bse = BlockCache.createCache(data.getPlayer(), data.getPlayerWorld(), newPos);
		
		if(!RotationLoader.onRotation(data.getPlayerWorld(), newPos, this.blockIdentifier, this.block, this.meta, movementType))
			data.getPlayerWorld().setBlockState(newPos, this.replacedBlock, 2);
		
		data.getPlayerWorld().notifyBlockUpdate(newPos, replacedBlock, replacedBlock, 0);
	    if (this.nbt != null) {
	    	TileEntity tileEntity = data.getPlayerWorld().getTileEntity(newPos);
	        if (tileEntity != null) {
	            tileEntity.readFromNBT(this.nbt);
	            tileEntity.setPos(newPos);
	        }
	    }
	    
	    return bse;
	}
    
    public BlockCache restoreRelative(PlayerData data) { 
		BlockPos newPos = BlockPosHelper.subtract(this.pos, this.playerPos);
		BlockPos newPlayerPos = new BlockPos(data.getPlayer());
		newPos = newPos.add(newPlayerPos);

		BlockCache bse = BlockCache.createCache(data.getPlayer(), data.getPlayerWorld(), newPos);
		
		this.restoreToLocation(data.getPlayerWorld(), newPos, false);
		
		return bse;
	}
    
    public BlockCache restoreRelative(World world, BlockPos pos) { 
		BlockPos newPos = BlockPosHelper.subtract(this.pos, this.playerPos);
		BlockPos newPlayerPos = pos;
		newPos = newPos.add(newPlayerPos);

		BlockCache bse = BlockCache.createCache(pos, world, newPos);
		
		this.restoreToLocation(world, newPos, false);
		
		return bse;
	}
	
	public BlockCache restoreRelativeToFlipped(PlayerData data, MovementType movementType) { 
		BlockPos newPos = this.pos;
		
		if(movementType.equals(MovementType._X_))
			newPos = new BlockPos(data.getMaxX() - (this.pos.getX() - data.getMinX()), this.pos.getY(), this.pos.getZ());
		else if(movementType.equals(MovementType._Z_))
			newPos = new BlockPos(this.pos.getX(), this.pos.getY(), data.getMaxZ() - (this.pos.getZ() - data.getMinZ()));
		else if(movementType.equals(MovementType._Y_))
			newPos = new BlockPos(this.pos.getX(), data.getMaxY() - (this.pos.getY() - data.getMinY()), this.pos.getZ());
	
		BlockCache bse = BlockCache.createCache(data.getPlayer(), data.getPlayerWorld(), newPos);
		
		if(!RotationLoader.onRotation(data.getPlayerWorld(), newPos, this.blockIdentifier, this.block, this.meta, movementType))
			data.getPlayerWorld().setBlockState(newPos, this.replacedBlock, 2);
		
		data.getPlayerWorld().notifyBlockUpdate(newPos, replacedBlock, replacedBlock, 0);
	    if (this.nbt != null) {
	    	TileEntity tileEntity = data.getPlayerWorld().getTileEntity(newPos);
	        if (tileEntity != null) {
	            tileEntity.readFromNBT(this.nbt);
	            tileEntity.setPos(newPos);
	        }
	    }
	    
	    return bse;
	}

    public void writeToNBT(NBTTagCompound compound) {
    	compound.setLong("playerPos", this.playerPos.toLong());
        compound.setString("blockMod", this.blockIdentifier.getResourceDomain());
        compound.setString("blockName", this.blockIdentifier.getResourcePath());
        compound.setLong("blockPos", this.pos.toLong());
        compound.setInteger("dimension", this.dimId);
        compound.setByte("metadata", (byte)this.meta);

        compound.setBoolean("hasTE", this.nbt != null);

        if (this.nbt != null)
            compound.setTag("tileEntity", this.nbt);
    }
    
    public void writeToPacketBuffer(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBlockPos(this.playerPos);
		packetbuffer.writeInt(this.dimId);
		packetbuffer.writeBlockPos(this.pos);
		String id = this.blockIdentifier.toString();
	    int i = id.indexOf(58);

	    if(i >= 0 && id.substring(0, i).equals("minecraft"))
			packetbuffer.writeString(id.substring(i + 1, id.length()));
	    else
	    	packetbuffer.writeString(id);
	    
		packetbuffer.writeByte(this.meta);
		packetbuffer.writeNBTTagCompoundToBuffer(this.nbt);
	}
    
    private static PacketBuffer SIZE_BUFFER = new PacketBuffer(Unpooled.buffer());
    
    public int calculateSizeEverything() throws IOException {
    	SIZE_BUFFER.clear();
    	this.writeToPacketBuffer(SIZE_BUFFER);
    	return SIZE_BUFFER.writerIndex();
    }
    
    public int calculateSizeCompact() throws IOException {
    	SIZE_BUFFER.clear();
    	this.writeToPacketBufferCompact(SIZE_BUFFER);
    	return SIZE_BUFFER.writerIndex();
    }
    
    public void writeToPacketBufferCompact(PacketBuffer packetbuffer) throws IOException {
		String id = this.blockIdentifier.toString();
	    int i = id.indexOf(58);

	    if(i >= 0 && id.substring(0, i).equals("minecraft"))
			packetbuffer.writeString(id.substring(i + 1, id.length()));
	    else
	    	packetbuffer.writeString(id);
	    
		packetbuffer.writeByte(this.meta);
		packetbuffer.writeNBTTagCompoundToBuffer(this.nbt);
	}

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        BlockCache other = (BlockCache)obj;
        if (!this.pos.equals(other.pos))
            return false;
        if (this.meta != other.meta)
            return false;
        if (this.dimId != other.dimId)
            return false;
        if (this.nbt != other.nbt && (this.nbt == null || !this.nbt.equals(other.nbt)))
            return false;
        if (this.world != other.world && (this.world == null || !this.world.equals(other.world)))
            return false;
        if (this.blockIdentifier != other.blockIdentifier && (this.blockIdentifier == null || !this.blockIdentifier.equals(other.blockIdentifier)))
            return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.pos.getX();
        hash = 73 * hash + this.pos.getY();
        hash = 73 * hash + this.pos.getZ();
        hash = 73 * hash + this.meta;
        hash = 73 * hash + this.dimId;
        hash = 73 * hash + (this.nbt != null ? this.nbt.hashCode() : 0);
        hash = 73 * hash + (this.world != null ? this.world.hashCode() : 0);
        hash = 73 * hash + (this.blockIdentifier != null ? this.blockIdentifier.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
    	return "BlockCache=[Pos=" + this.pos.toString() + ", Block=" + this.blockIdentifier.toString() + "]";
    }
}