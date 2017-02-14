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
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandPaste extends CommandBase {

	@Override
	public String getCommandName() {
		return "/paste";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.paste.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.getActionStorage().hasSomethingToPaste())
			throw new CommandException("mapmakingtools.commands.build.nothingtopaste", new Object[0]);

		int blocksChanged = data.getActionStorage().paste();
			
		if(blocksChanged > 0) {
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.paste.complete", "" + blocksChanged);
			chatComponent.getStyle().setItalic(true);
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
