package mapmakingtools.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ProPercivalalb
 */
public class SlotFakeArmor extends SlotFake {

	public EntityPlayer player;
	public EntityEquipmentSlot armorType;
	
    public SlotFakeArmor(EntityPlayer player, IInventory par2IInventory, int par3, int par4, int par5, EntityEquipmentSlot par6) {
        super(par2IInventory, par3, par4, par5);
        this.player = player;
        this.armorType = par6;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack == null) return false;
        return stack.getItem().isValidArmor(stack, this.armorType, this.player);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public String getSlotTexture() {
        return ItemArmor.EMPTY_SLOT_NAMES[this.armorType.getIndex()];
    }
}
