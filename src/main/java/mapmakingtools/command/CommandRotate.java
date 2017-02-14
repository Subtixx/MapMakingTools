package mapmakingtools.command;

import java.util.Arrays;
import java.util.List;

import mapmakingtools.api.enums.MovementType;
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

import javax.annotation.Nullable;

/**
 * @author ProPercivalalb
 */
public class CommandRotate extends CommandBase {

	@Override
	public String getCommandName() {
		return "/rotate";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.rotate.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);

		if(!data.getActionStorage().hasSomethingToPaste())
			throw new CommandException("mapmakingtools.commands.build.nothingtorotate");


		if(args.length < 1)
			throw new WrongUsageException(this.getCommandUsage(sender));
		else {
			MovementType rotation = null;
			for(String str : getModeNames())
				if(str.equalsIgnoreCase(args[0]))
					rotation = MovementType.getRotation(args[0]);

			if(rotation == null)
				throw new CommandException("mapmakingtools.commands.build.rotatemodeerror");

			boolean didChange = data.getActionStorage().setRotation(rotation);

			if(didChange) {
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.rotate.complete", args[0]);
				chatComponent.getStyle().setItalic(true);
				player.addChatMessage(chatComponent);
			}
			else {
				TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.rotationnot90");
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
		return Arrays.asList("90", "180", "270");
	}

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
}
