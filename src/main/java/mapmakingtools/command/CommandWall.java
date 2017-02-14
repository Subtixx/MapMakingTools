package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandWall extends CommandBase {

	@Override
	public String getCommandName() {
		return "/wall";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.wall.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected", new Object[0]);
		
		if(args.length < 1)
			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
		else {
			Block block = getBlockByText(sender, args[0]);
			int meta = 0;
			
			if(args.length == 2)
				meta = parseInt(args[1]);
			
			ArrayList<BlockCache> list = new ArrayList<BlockCache>();
			int blocks = 0;
			
			Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getFirstPoint(), data.getSecondPoint());
			
			for(BlockPos pos : positions) {
				boolean flag1 = pos.getX() == data.getMinX();
				boolean flag2 = pos.getX() == data.getMaxX();
				boolean flag3 = pos.getZ() == data.getMinZ();
				boolean flag4 = pos.getZ() == data.getMaxZ();
				if(flag1 || flag2 || flag3 || flag4) {
					list.add(BlockCache.createCache(player, world, pos));
					world.setBlockState(pos, block.getStateFromMeta(meta), 2);
					blocks += 1;
				}
			}

			data.getActionStorage().addUndo(list);

			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.wall.complete", "" + blocks, args[0]);
			chatComponent.getStyle().setItalic(true);
			player.addChatMessage(chatComponent);
			
		}
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys()) : null;
    }

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
}
