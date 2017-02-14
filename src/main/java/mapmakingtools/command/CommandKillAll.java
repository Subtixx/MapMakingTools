package mapmakingtools.command;

import java.util.List;

import mapmakingtools.api.manager.ForceKillManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

/**
 * @author ProPercivalalb
 **/
public class CommandKillAll extends CommandBase {
	
	@Override
    public String getCommandName() {
        return "/killentities";
    }

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "mapmakingtools.commands.killentities.usage";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    	if(sender instanceof EntityPlayerMP) {
    		EntityPlayerMP player = (EntityPlayerMP)sender;
    		WorldServer worldServer = (WorldServer)player.worldObj;
    		if(args.length == 1) {
    			if(ForceKillManager.isRealName(args[0])) {
    				for(int i = 0; i < worldServer.loadedEntityList.size(); ++i) {
    					Entity listEntity = (Entity)worldServer.loadedEntityList.get(i);
    					ForceKillManager.killGiven(args[0], listEntity, player);
    				}
    			}
    			else {
    				throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
    			}
    		}
    		else {
    			throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
    	    }
    	}
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
    	switch(args.length) {
		case 1: 
			return getListOfStringsMatchingLastWord(args, ForceKillManager.getNameList());
    	}
    	return null;
	}

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
}
