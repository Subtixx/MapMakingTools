package mapmakingtools.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author ProPercivalalb
 */
public class CommandCopyInventory extends CommandBase {

	@Override
	public String getCommandName() {
		return "/copyinventory";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.copyinventory.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;

		if(args.length < 1)
			throw new WrongUsageException(this.getCommandUsage(sender));

		EntityPlayer copyPlayer = getPlayer(server, sender, args[0]);

		if(copyPlayer == null || player.equals(copyPlayer))
			throw new CommandException("mapmakingtools.commands.copyinventory.playererror");

		player.inventory.copyInventory(copyPlayer.inventory);

		TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.copyinventory.complete");
		chatComponent.getStyle().setItalic(true);
		player.addChatMessage(chatComponent);
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getAllUsernames()) : null;
	}
	
    @Override
    public boolean isUsernameIndex(String[] param, int index) {
        return index == 0;
    }
}
