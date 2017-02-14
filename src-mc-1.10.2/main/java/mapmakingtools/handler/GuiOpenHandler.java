package mapmakingtools.handler;

import mapmakingtools.MapMakingTools;
import mapmakingtools.ModItems;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author ProPercivalalb
 */
public class GuiOpenHandler {

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		GuiScreen screen = event.getGui();
		if(!(screen instanceof GuiCommandBlock)) return;
		
		EntityPlayer player = MapMakingTools.proxy.getPlayerEntity();
		if(player == null) return;

		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		if(stack == null) return;
		
		if(stack.getItem() == ModItems.editItem)
			event.setCanceled(true);
	}
}
