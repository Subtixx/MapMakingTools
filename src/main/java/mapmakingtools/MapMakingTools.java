package mapmakingtools;

import mapmakingtools.handler.*;
import mapmakingtools.lib.Reference;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION,
		dependencies = Reference.MOD_DEPENDENCIES, acceptedMinecraftVersions = "1.10",
		updateJSON = "https://raw.githubusercontent.com/Subtixx/MapMakingTools/master/version.json",
		guiFactory = Reference.MOD_GUIFACTORY)
public class MapMakingTools {

	@Instance(value = Reference.MOD_ID)
	public static MapMakingTools instance;
	
	@SidedProxy(clientSide = Reference.SP_CLIENT, serverSide = Reference.SP_SERVER)
    public static CommonProxy proxy;
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	ConfigurationHandler.loadConfig(event.getSuggestedConfigurationFile());

    	proxy.onPreLoad();
    	PacketDispatcher.registerPackets();
    }
    
    @EventHandler
    public void onInit(FMLInitializationEvent event) {
    	NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
    	MinecraftForge.EVENT_BUS.register(new ActionHandler());
    	MinecraftForge.EVENT_BUS.register(new WorldSaveHandler());
    	MinecraftForge.EVENT_BUS.register(new EntityJoinWorldHandler());
    	MinecraftForge.EVENT_BUS.register(new PlayerTrackerHandler());

    	ModItems.init();

    	proxy.registerFilters();  
    	proxy.registerRotation();
    	proxy.registerItemAttribute();
    	proxy.registerForceKill();
    	proxy.registerHandlers();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	proxy.onPostLoad();
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        //Initialize the custom commands
        CommandHandler.initCommands(event);
    }
	
}
