package mapmakingtools.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mapmakingtools.api.enums.MovementType;
import mapmakingtools.tools.BlockCache;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
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
public class CommandFlip extends CommandBase {

	@Override
	public String getCommandName() {
		return "/flip";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.flip.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected");
		
		
		if(args.length < 1)
			throw new WrongUsageException(this.getCommandUsage(sender));
		else {
			MovementType movementType = null;
			String flipMode = args[0];
			
			for(String str : getModeNames())
	    		if(str.equalsIgnoreCase(flipMode))
	    			movementType = MovementType.getRotation(str);

			if(movementType == null)
				throw new CommandException("mapmakingtools.commands.build.flipmodeerror", flipMode);
				
			
			data.getActionStorage().setFlipping(movementType);

			ArrayList<BlockCache> list = new ArrayList<BlockCache>();
			
			Iterable<BlockPos> positions = BlockPos.getAllInBox(data.getFirstPoint(), data.getSecondPoint());
			
			for(BlockPos pos : positions) {
				list.add(BlockCache.createCache(player, world, pos));
			}

			int blocksChanged = data.getActionStorage().flip(list);
			
			if(blocksChanged > 0) {
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.flip.complete", "" + blocksChanged);
				chatComponent.getStyle().setItalic(true);
				player.addChatMessage(chatComponent);
			}
		}
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, getModeNames()) : null;
    }
	
	public static List<String> getModeNames() {
		return Arrays.asList("x", "y", "z");
	}

    @Override
    public boolean isUsernameIndex(String[] param, int index) {
        return false;
    }
}
