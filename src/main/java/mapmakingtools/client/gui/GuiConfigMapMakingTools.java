package mapmakingtools.client.gui;

import mapmakingtools.handler.ConfigurationHandler;
import mapmakingtools.lib.Constants;
import mapmakingtools.lib.Reference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

import static mapmakingtools.handler.ConfigurationHandler.config;
import static mapmakingtools.handler.ConfigurationHandler.loadConfig;

public class GuiConfigMapMakingTools extends GuiConfig
{
    public GuiConfigMapMakingTools(GuiScreen parent)
    {
        super(parent,
                new ConfigElement(

                        config.getCategory(Configuration.CATEGORY_GENERAL))

                        .getChildElements(),
                Reference.MOD_ID,
                false,
                false,
                "Editing config for Map Making Tools located in:");
        titleLine2 = config.getConfigFile().getAbsolutePath();
    }

    @Override
    public void initGui()
    {
        // You can add buttons and initialize fields here
        super.initGui();
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        // You can do things like create animations, draw additional elements, etc. here
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if(button.id == 2000){
            loadConfig();
        }
        super.actionPerformed(button);
    }
}
