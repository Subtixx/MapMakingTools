package mapmakingtools.command;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketBiomeUpdate;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

/**
 * @author ProPercivalalb
 */
public class CommandSetBiome extends CommandBase {

	@Override
	public String getCommandName() {
		return "/setbiome";
	}

	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "mapmakingtools.commands.build.setbiome.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayerMP))
			return;
		
		EntityPlayerMP player = (EntityPlayerMP)sender;
		WorldServer world = (WorldServer)player.worldObj;
		PlayerData data = WorldData.getPlayerData(player);
		
		if(!data.hasSelectedPoints())
			throw new CommandException("mapmakingtools.commands.build.postionsnotselected");
		
		if(args.length < 1)
			throw new WrongUsageException(this.getCommandUsage(sender));
		else {
			
			Biome biome = getBiomeByText(sender, args[0]);
			
			Iterable<BlockPos> positions = BlockPos.getAllInBox(new BlockPos(data.getFirstPoint().getX(), 0, data.getFirstPoint().getZ()), new BlockPos(data.getSecondPoint().getX(), 0, data.getSecondPoint().getZ()));
			
			for(BlockPos pos : positions) {
				Chunk chunk = world.getChunkFromBlockCoords(pos);
				byte[] biomes = chunk.getBiomeArray();
				biomes[((pos.getZ() & 0xF) << 4 | pos.getX() & 0xF)] = (byte)Biome.REGISTRY.getIDForObject(biome);
				chunk.setBiomeArray(biomes);
				chunk.setChunkModified();
			}
			
			PacketDispatcher.sendToDimension(new PacketBiomeUpdate(data.getFirstPoint(), data.getSecondPoint(), biome), world.provider.getDimension());
			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.commands.build.setbiome.complete", "" + biome.getBiomeName());
			chatComponent.getStyle().setItalic(true);
			player.addChatMessage(chatComponent);
		}
	}

	public Biome getBiomeByText(ICommandSender sender, String p_147180_1_) throws CommandException {
		int y = 0;
		for (Biome biome : Biome.REGISTRY) {
			if(biome == null) continue;
			String name = biome.getBiomeName();
			if(!Strings.isNullOrEmpty(name) && name.replaceAll(" ", "").equalsIgnoreCase(p_147180_1_))
				return biome;
			y++;
		}
		
		try {
			int i = parseInt(p_147180_1_, 0, y);
			Biome biome = Biome.getBiome(i);
                
            if (biome != null)
            	return biome;
         }
         catch (NumberFormatException numberformatexception) {}

         throw new NumberInvalidException("mapmakingtools.commands.build.setbiome.notfound", p_147180_1_);

	}
	
	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getBiomeKeys()) : null;
    }

    private List<String> getBiomeKeys() {
		List<String> list = new ArrayList<String>();
		for (Biome biome : Biome.REGISTRY) {
			if(biome == null) continue;
			String name = biome.getBiomeName().replaceAll(" ", "").toLowerCase();
			if(!Strings.isNullOrEmpty(name))
				list.add(name);
		}
		return list;
	}

	@Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
        return false;
    }
}
