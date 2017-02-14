package mapmakingtools.command;

import java.util.Arrays;
import java.util.List;

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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author ProPercivalalb
 */
public class CommandMove extends CommandBase {

	@Override
	public String getCommandName() {
		return "/move";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.move.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		BlockPos firstPoint = data.getFirstPoint();
		BlockPos secondPoint = data.getSecondPoint();
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected");
		
		if(args.length < 2)
			throw new WrongUsageException(this.getCommandUsage(sender));
		
		String direction = args[0].toLowerCase();
		int amount = parseInt(args[1]);
		
		if("x".equals(direction)) {
			data.setFirstPoint(firstPoint.north(amount));
			data.setSecondPoint(secondPoint.north(amount));
		}
		else if("y".equals(direction)) {
			data.setFirstPoint(firstPoint.up(amount));
			data.setSecondPoint(secondPoint.up(amount));
		}
		else if("z".equals(direction)) {
			data.setFirstPoint(firstPoint.east(amount));
			data.setSecondPoint(secondPoint.east(amount));
		}
		
		data.sendUpdateToClient();
		
		TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.move.complete", direction, amount);
		chatComponent.getStyle().setColor(TextFormatting.GREEN);
		player.addChatMessage(chatComponent);
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, getDirections()) : null;
    }
	
	public static List<String> getDirections() {
		return Arrays.asList("x", "y", "z");
	}
	
    @Override
    public boolean isUsernameIndex(String[] param, int index) {
        return false;
    }
}
