package mapmakingtools.tools.filter;

import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import mapmakingtools.api.FakeWorldManager;
import mapmakingtools.api.IFilterClient;
import mapmakingtools.api.IFilterClientSpawner;
import mapmakingtools.api.IGuiFilter;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.lib.ResourceReference;
import mapmakingtools.network.ChannelOutBoundHandler;
import mapmakingtools.tools.filter.packet.PacketMobArmor;
import mapmakingtools.tools.filter.packet.PacketMobArmorUpdate;
import mapmakingtools.tools.filter.packet.PacketMobType;
import mapmakingtools.util.SpawnerUtil;

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
		return "mapmakingtools:mobArmor";
	}
	
	@Override
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		gui.setYSize(151);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - 151) / 2;
        this.btnOk = new GuiButton(0, topX + 12, topY + 63, 20, 20, "OK");
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
		if (button.field_146124_l) {
            switch (button.field_146127_k) {
                case 0:
                	ChannelOutBoundHandler.sendPacketToServer(new PacketMobArmor(gui.getX(), gui.getY(), gui.getZ(), IFilterClientSpawner.minecartIndex));
                	ClientHelper.mc.func_147108_a((GuiScreen)null);
            		ClientHelper.mc.setIngameFocus();
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
			this.btnOk.field_146124_l = false;
		else
			this.btnOk.field_146124_l = true;
	}
	
	@Override
	public boolean hasUpdateButton(IGuiFilter gui) {
		return true;
	}
	
	@Override
	public void updateButtonClicked(IGuiFilter gui) {
		if(!showErrorIcon(gui))
			ChannelOutBoundHandler.sendPacketToServer(new PacketMobArmorUpdate(gui.getX(), gui.getY(), gui.getZ(), IFilterClientSpawner.minecartIndex));
	}
	
	@Override
	public boolean showErrorIcon(IGuiFilter gui) { 
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getX(), gui.getY(), gui.getZ());
		if(!(tile instanceof TileEntityMobSpawner))
			return true;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		List<WeightedRandomMinecart> minecarts = SpawnerUtil.getRandomMinecarts(spawner.func_145881_a());
		WeightedRandomMinecart randomMinecart = minecarts.get(minecartIndex);
		String mobId = randomMinecart.minecartName;
		if(mobId.equals("Zombie") || mobId.equals("PigZombie") || mobId.equals("Skeleton")) {
			return false;
		}
		
		return true; 
	}
	
	@Override
	public String getErrorMessage(IGuiFilter gui) { 
		return EnumChatFormatting.RED + StatCollector.translateToLocal("mapmakingtools.filter.mobArmor.error");
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
