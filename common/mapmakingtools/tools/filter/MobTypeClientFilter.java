package mapmakingtools.tools.filter;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import mapmakingtools.api.IFilterClient;
import mapmakingtools.api.IFilterClientSpawner;
import mapmakingtools.api.IGuiFilter;
import mapmakingtools.api.MobSpawnerType;
import mapmakingtools.api.ScrollMenu;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.ChannelOutBoundHandler;
import mapmakingtools.tools.filter.packet.PacketMobType;

/**
 * @author ProPercivalalb
 */
public class MobTypeClientFilter extends IFilterClientSpawner {

	public GuiButton btnOk;
	public ScrollMenu menu;
	public static int selected = MobSpawnerType.getSpawnerMobs().indexOf("Pig");
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.mobType.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:mobType";
	}

	@Override
	public void initGui(final IGuiFilter gui) {
		super.initGui(gui);
		gui.setYSize(135);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 135) / 2;
        MobSpawnerType.sort();
        this.menu = new ScrollMenu((GuiScreen)gui, topX + 8, topY + 19, 208, 108, 2, MobSpawnerType.getSpawnerMobs()) {

			@Override
			public String getDisplayString(String unlocalised) {
				return StatCollector.translateToLocal(unlocalised);
			}

			@Override
			public void onSetButton() {
				MobTypeClientFilter.selected = this.selected;
				ChannelOutBoundHandler.sendPacketToServer(new PacketMobType(gui.getX(), gui.getY(), gui.getZ(), this.strRefrence.get(this.selected), IFilterClientSpawner.minecartIndex));
            	ClientHelper.mc.func_147108_a((GuiScreen)null);
        		ClientHelper.mc.setIngameFocus();
			}
        	
        };
        this.menu.initGui();
        this.menu.selected = selected;
        
        this.btnOk = new GuiButton(0, topX + 12, topY + 63, 20, 20, "OK");
        //gui.getButtonList().add(this.btnOk);
        this.addMinecartButtons(gui, topX, topY);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
	    int topY = (gui.getHeight() - 135) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 6, 0);
        this.menu.drawGuiContainerBackgroundLayer(partialTicks, xMouse, yMouse);
	}
	
	@Override
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {
		super.mouseClicked(gui, xMouse, yMouse, mouseButton);
		this.menu.mouseClicked(xMouse, yMouse, mouseButton);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.field_146124_l) {
            switch (button.field_146127_k) {
                case 0:
                	//PacketTypeHandler.populatePacketAndSendToServer(new PacketMobArmor(gui.x, gui.y, gui.z));
                	ClientHelper.mc.func_147108_a((GuiScreen)null);
            		ClientHelper.mc.setIngameFocus();
                    break;
            }
        }
	}
	
	@Override
	public boolean drawBackground(IGuiFilter gui) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.mc.getTextureManager().bindTexture(ResourceReference.screenScroll);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 135) / 2;
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 135);
		return true;
	}
}