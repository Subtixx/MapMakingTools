package mapmakingtools.command;

import java.util.List;

import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandSelectionSize extends CommandBase {

	@Override
	public String getCommandName() {
		return "/selectionsize";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.selectionsize.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(data.hasSelectedPoints()) {
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.selectionsize.complete", data.getSelectionSize()[0], data.getSelectionSize()[1], data.getSelectionSize()[2]);
			chatComponent.getStyle().setColor(TextFormatting.GREEN);
			player.addChatMessage(chatComponent);
		}
		else {
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.selectionsize.nothingselected", data.getSelectionSize()[0], data.getSelectionSize()[1], data.getSelectionSize()[2]);
			chatComponent.getStyle().setColor(TextFormatting.RED);
			player.addChatMessage(chatComponent);
		}
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }
	
    @Override
    public boolean isUsernameIndex(String[] param, int index) {
        return false;
    }
}
