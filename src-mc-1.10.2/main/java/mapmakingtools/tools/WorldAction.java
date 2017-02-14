package mapmakingtools.tools;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class WorldAction {

	 public static boolean setBlock(World world, BlockPos pos, IBlockState blockState, boolean notifyAndLight) {
	    Chunk chunk = world.getChunkFromBlockCoords(pos);

	    IBlockState lastState = chunk.setBlockState(pos, blockState);
	    world.notifyBlockUpdate(pos, blockState, blockState, 0);
	    if(notifyAndLight) {
			world.notifyBlockUpdate(pos, blockState, blockState, 0);

	         world.notifyNeighborsRespectDebug(pos, lastState.getBlock());

	         if(blockState.getBlock().hasComparatorInputOverride(blockState))
	        	 world.updateComparatorOutputLevel(pos, blockState.getBlock());
	    	
	    }

	    return lastState != null;
	}

	public static boolean setBlockToAir(World world, BlockPos pos, boolean notifyAndLight) {
		return setBlock(world, pos, Blocks.AIR.getDefaultState(), notifyAndLight);
	}
}
