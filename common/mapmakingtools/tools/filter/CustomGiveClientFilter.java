package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.FakeWorldManager;
import mapmakingtools.api.IContainerFilter;
import mapmakingtools.api.IFilterClient;
import mapmakingtools.api.IGuiFilter;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.tools.filter.packet.PacketCustomGive;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

/**
 * @author ProPercivalalb
 */
public class CustomGiveClientFilter extends IFilterClient {

	public GuiButton btnOk;
	public GuiTextField tempCommand;
	public ItemStack lastStack = null;
	public String lastText = "";
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.customgive.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:custom_give";
	}

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = FakeWorldManager.getTileEntity(world, x, y, z);
		if(tileEntity != null && tileEntity instanceof TileEntityCommandBlock)
			return true;
		return super.isApplicable(player, world, x, y, z);
	}

	@Override
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		gui.setYSize(104);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 104) / 2;
        this.btnOk = new GuiButton(0, topX + 20, topY + 61, 20, 20, "OK");
        this.tempCommand = new GuiTextField(gui.getFont(), gui.getWidth() / 2 - 150, topY + 110, 300, 20);
        this.tempCommand.setMaxStringLength(32767);
        this.tempCommand.setEnabled(false);
        this.tempCommand.setText(this.lastText);
        
        gui.getButtonList().add(this.btnOk);
        gui.getTextBoxList().add(this.tempCommand);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketCustomGive(gui.getX(), gui.getY(), gui.getZ()));
            		ClientHelper.mc.setIngameFocus();
                	break;
            }
        }
	}
	
	@Override
	public void updateScreen(IGuiFilter gui) {
		IContainerFilter container = gui.getFilterContainer();
		ItemStack stack = container.getInventorySlots().get(0).getStack();
		
		if(!(ItemStack.areItemStacksEqual(stack, this.lastStack) && ItemStack.areItemStackTagsEqual(stack, this.lastStack))) {
			
			if(stack != null) {
				String command = "/give @p";
				command += " " + Item.itemRegistry.getNameForObject(stack.getItem());
				command += " " + stack.stackSize;
				command += " " + stack.getItemDamage();
				
				if(stack.hasTagCompound())
					command += " " + String.valueOf(stack.stackTagCompound);
				this.tempCommand.setText(command);
				this.lastText = command;
				this.lastStack = stack.copy();
				this.tempCommand.setCursorPositionZero();
			}
			else {
				this.tempCommand.setText("");
				this.lastText = "";
				this.lastStack = null;
				this.tempCommand.setCursorPositionZero();
			}
			
		}
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), EnumChatFormatting.GREEN + this.getFilterName(), StatCollector.translateToLocal("mapmakingtools.filter.customgive.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 104) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
	}
	
	@Override
	public boolean drawBackground(IGuiFilter gui) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.mc.getTextureManager().bindTexture(ResourceReference.screenOneSlot);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 104) / 2;
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 104);
		return true;
	}
}
