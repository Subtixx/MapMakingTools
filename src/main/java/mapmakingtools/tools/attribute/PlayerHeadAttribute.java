package mapmakingtools.tools.attribute;

import com.google.common.base.Strings;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import mapmakingtools.api.interfaces.IGuiItemEditor;
import mapmakingtools.api.interfaces.IItemAttribute;
import mapmakingtools.helper.NumberParse;
import mapmakingtools.tools.item.nbt.SkullNBT;

/**
 * @author ProPercivalalb
 */
public class PlayerHeadAttribute extends IItemAttribute {

	private GuiTextField fld_name;
	private String name;
	
	@Override
	public boolean isApplicable(EntityPlayer player, ItemStack stack) {
		return stack.getItem() == Items.skull && stack.getItemDamage() == 3;
	}

	@Override
	public void onItemCreation(ItemStack stack, int data) {		
		SkullNBT.setSkullName(stack, this.name);
	}

	@Override
	public String getUnlocalizedName() {
		return "mapmakingtools.itemattribute.playerhead.name";
	}
	
	@Override
	public void populateFromItem(IGuiItemEditor itemEditor, ItemStack stack, boolean first) {
		if(first)
			this.fld_name.setText(SkullNBT.getSkullName(stack));
	}

	@Override
	public void drawInterface(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		itemEditor.getFontRenderer().drawString(this.getAttributeName(), x + 2, y + 2, 1);
	}

	@Override
	public void initGui(IGuiItemEditor itemEditor, int x, int y, int width, int height) {
		this.fld_name = new GuiTextField(itemEditor.getFontRenderer(), x + 2, y + 15, 80, 13);
		itemEditor.getTextBoxList().add(this.fld_name);
	}

	@Override
	public void textboxKeyTyped(IGuiItemEditor itemEditor, char character, int keyId, GuiTextField textbox) {
		if(textbox == this.fld_name) {
			this.name = this.fld_name.getText();
			itemEditor.sendUpdateToServer(-1);
		}
	}
}
