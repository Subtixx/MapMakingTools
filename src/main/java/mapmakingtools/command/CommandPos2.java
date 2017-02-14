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

import javax.annotation.Nullable;

/**
 * @author ProPercivalalb
 */
public class CommandPos2 extends CommandBase {

	@Override
	public String getCommandName() {
		return "/pos2";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.pos2.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer)sender;
		World world = player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);

		data.setSecondPoint(new BlockPos(player));
		data.sendUpdateToClient();

		if(data.hasSelectedPoints()) {
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.positive", "" + data.getBlockCount());
			chatComponent.getStyle().setColor(TextFormatting.GREEN);
			player.addChatMessage(chatComponent);
		}
		else {
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.negative");
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
