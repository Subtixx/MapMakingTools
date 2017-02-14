package mapmakingtools.lib;

/**
 * @author ProPercivalalb
 */
public class Reference {

	//Mod Related Constants 
	public static final String 		 MOD_ID 	     = "mapmakingtools";
	public static final String		 MOD_NAME        = "Map Making Tools";
	public static final String 		 MOD_VERSION     = "@GRADLEVERSION@-@BUILDERNUMBER@";
	public static final String 		 MOD_DEPENDENCIES = "required-after:Forge@[12,)";
	public static final String 		 SP_CLIENT 		 = "mapmakingtools.proxy.ClientProxy";
	public static final String 		 SP_SERVER		 = "mapmakingtools.proxy.CommonProxy";
	public static final String       CHANNEL_NAME    = "MMT";
	
	public static final boolean		 DEBUG 			 = false;
    public static final String MOD_GUIFACTORY = "mapmakingtools.client.gui.GuiFactoryMapMakingTools";
}
