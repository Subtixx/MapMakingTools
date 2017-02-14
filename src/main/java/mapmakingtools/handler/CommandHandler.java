package mapmakingtools.handler;

import mapmakingtools.command.*;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * @author ProPercivalalb
 */
public class CommandHandler {

    public static void initCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandClearPoints());
        event.registerServerCommand(new CommandSet());
        event.registerServerCommand(new CommandRoof());
        event.registerServerCommand(new CommandFloor());
        event.registerServerCommand(new CommandWall());
        event.registerServerCommand(new CommandRotate());
        event.registerServerCommand(new CommandFlip());
        event.registerServerCommand(new CommandReplace());
        event.registerServerCommand(new CommandCopy());
        event.registerServerCommand(new CommandPaste());
        event.registerServerCommand(new CommandUndo());
        event.registerServerCommand(new CommandRedo());
        event.registerServerCommand(new CommandPos1());
        event.registerServerCommand(new CommandPos2());
        event.registerServerCommand(new CommandSetBiome());
        event.registerServerCommand(new CommandPlayerStatue());
        event.registerServerCommand(new CommandSelectionSize());
        event.registerServerCommand(new CommandExpand());
        event.registerServerCommand(new CommandShrink());
        event.registerServerCommand(new CommandMove());
        event.registerServerCommand(new CommandMaze());
        
        event.registerServerCommand(new CommandWorldTransfer());
        event.registerServerCommand(new CommandKillAll());
        
        event.registerServerCommand(new CommandDebug());
        event.registerServerCommand(new CommandCopyInventory());
        
    }
}
