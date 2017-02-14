package mapmakingtools.tools.filter;

import java.util.List;

import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

import mapmakingtools.api.interfaces.IFilterClientSpawner;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketMobArmor;
import mapmakingtools.tools.filter.packet.PacketMobArmorUpdate;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.text.TextFormatting;


/**
 * @author ProPercivalalb
 */
public class MobArmorClientFilter extends IFilterClientSpawner {

	public GuiButton btnOk;
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.mobArmor.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/mobArmor.png";
	}
	
	@Override
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		gui.setYSize(151);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 151) / 2;
        //this.btnOk = new GuiButton(0, topX + 12, topY + 63, 20, 20, "OK");
        this.btnOk = new GuiButton(0, topX + 12, topY + 95, 20, 20, "OK");
        gui.getButtonList().add(this.btnOk);
        this.addMinecartButtons(gui, topX, topY);
        this.onMinecartIndexChange(gui);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
	    int topY = (gui.getHeight() - 151) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
        
        gui.getFont().drawString("Mob Armor", topX + 40, topY + 30, 4210752);
        gui.getFont().drawString("Player Armor", topX + 129, topY + 30, 4210752);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	PacketDispatcher.sendToServer(new PacketMobArmor(gui.getBlockPos(), IFilterClientSpawner.minecartIndex));
            		ClientHelper.mc.thePlayer.closeScreen();
                    break;
            }
        }
	}
	
	@Override
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 151) / 2;
		this.removeMinecartButtons(gui, xMouse, yMouse, mouseButton, topX, topY);
	}
	
	@Override
	public void onMinecartIndexChange(IGuiFilter gui) {
		if(this.showErrorIcon(gui))
			this.btnOk.enabled = false;
		else
			this.btnOk.enabled = true;
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.mobArmor.info"));
	}
	
	@Override
	public boolean hasUpdateButton(IGuiFilter gui) {
		return true;
	}
	
	@Override
	public void updateButtonClicked(IGuiFilter gui) {
		if(!showErrorIcon(gui))
			PacketDispatcher.sendToServer(new PacketMobArmorUpdate(gui.getBlockPos(), IFilterClientSpawner.minecartIndex));
	}
	
	@Override
	public boolean showErrorIcon(IGuiFilter gui) { 
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(!(tile instanceof TileEntityMobSpawner))
			return true;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		/*String mobId = SpawnerUtil.getMobId(spawner.getSpawnerBaseLogic(), -1);*/
		String mobId = SpawnerUtil.getMobId(spawner.getSpawnerBaseLogic(), -1);
		if(mobId.equals("Zombie") || mobId.equals("PigZombie") || mobId.equals("Skeleton")) {
			return false;
		}
		
		return true; 
	}
	
	@Override
	public String getErrorMessage(IGuiFilter gui) { 
		return TextFormatting.RED + I18n.translateToLocal("mapmakingtools.filter.mobArmor.error");
	}
	
	@Override
	public boolean drawBackground(IGuiFilter gui) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientHelper.mc.getTextureManager().bindTexture(ResourceReference.screenMobArmor);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 151) / 2;
		gui.drawTexturedModalRectangle(topX, topY, 0, 0, gui.xFakeSize(), 151);
		return true;
	}
}
