package mapmakingtools.tools.filter;

import java.util.List;

import mapmakingtools.api.interfaces.IFilterClientSpawner;
import mapmakingtools.api.interfaces.IGuiFilter;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.client.gui.button.GuiButtonData;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.TextHelper;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketBabyMonster;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

/**
 * @author ProPercivalalb
 */
public class BabyMonsterClientFilter extends IFilterClientSpawner {

	public GuiButtonData btn_covert;
	
	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.filter.babymonster.name";
	}

	@Override
	public String getIconPath() {
		return "mapmakingtools:textures/filter/baby_monster.png";
	}

	@Override
	public void initGui(IGuiFilter gui) {
		super.initGui(gui);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
        this.btn_covert = new GuiButtonData(0, topX + 20, topY + 37, 200, 20, "Turn into Baby");
        TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(tile instanceof TileEntityMobSpawner) {
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			if(SpawnerUtil.isBabyMonster(spawner.getSpawnerBaseLogic(), this.minecartIndex)) {
				this.btn_covert.displayString = "Turn into Adult";
				this.btn_covert.setData(1);
			}
		}
				
        gui.getButtonList().add(this.btn_covert);
        this.addMinecartButtons(gui, topX, topY);
	}
	
	@Override
	public void actionPerformed(IGuiFilter gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if (button.enabled) {
            switch (button.id) {
                case 0:
                	PacketDispatcher.sendToServer(new PacketBabyMonster(gui.getBlockPos(), this.btn_covert.getData() == 0 ? false : true, this.minecartIndex));
            		ClientHelper.mc.thePlayer.closeScreen();
                	break;
            }
        }
	}
	
	@Override
	public void mouseClicked(IGuiFilter gui, int xMouse, int yMouse, int mouseButton) {
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
		this.removeMinecartButtons(gui, xMouse, yMouse, mouseButton, topX, topY);
	}
	
	@Override
	public List<String> getFilterInfo(IGuiFilter gui) {
		return TextHelper.splitInto(140, gui.getFont(), TextFormatting.GREEN + this.getFilterName(), I18n.translateToLocal("mapmakingtools.filter.babymonster.info"));
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(IGuiFilter gui, float partialTicks, int xMouse, int yMouse) {
		super.drawGuiContainerBackgroundLayer(gui, partialTicks, xMouse, yMouse);
		int topX = (gui.getWidth() - gui.xFakeSize()) / 2;
        int topY = (gui.getHeight() - gui.yFakeSize()) / 2;
        gui.getFont().drawString(getFilterName(), topX - gui.getFont().getStringWidth(getFilterName()) / 2 + gui.xFakeSize() / 2, topY + 10, 0);
	}
	
	@Override
	public boolean showErrorIcon(IGuiFilter gui) {
		TileEntity tile = FakeWorldManager.getTileEntity(gui.getWorld(), gui.getBlockPos());
		if(!(tile instanceof TileEntityMobSpawner))
			return true;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
		
		List<WeightedSpawnerEntity> minecarts = SpawnerUtil.getRandomMinecarts(spawner.getSpawnerBaseLogic());
		WeightedSpawnerEntity randomMinecart = minecarts.get(minecartIndex);
		String mobId = SpawnerUtil.getMinecartType(randomMinecart);
		if(mobId.equals("Zombie") || mobId.equals("PigZombie"))
			return false;
		
		return true; 
	}
	
	@Override
	public String getErrorMessage(IGuiFilter gui) { 
		return TextFormatting.RED + I18n.translateToLocal("mapmakingtools.filter.babymonster.error");
	}
}
