package mapmakingtools.tools.filter;

import java.util.Hashtable;
import java.util.Map;

import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.api.interfaces.IFilterServer;
import mapmakingtools.container.IUnlimitedInventory;
import mapmakingtools.container.SlotArmor;
import mapmakingtools.container.SlotFake;
import mapmakingtools.container.SlotFakeArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;

/**
 * @author ProPercivalalb
 */
public class MobArmorServerFilter extends IFilterServer {

	public static Map<String, MobArmor> invMap = new Hashtable<String, MobArmor>();
	
	@Override
	public void addSlots(IContainerFilter container) {
		for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()){
            if(slot.getSlotType() != EntityEquipmentSlot.Type.HAND) {
                container.addSlot(new SlotArmor(container.getPlayer(), container.getPlayer().inventory, container.getPlayer().inventory.getSizeInventory() - 1 - slot.getIndex(), 130 + slot.getIndex() * 18, 40, slot));
            }
        }

        container.addSlot(new SlotFake(getInventory(container), 0, 14, 39)); // Mainhand
        container.addSlot(new SlotFake(getInventory(container), 1, 14, 67)); // Offhand

        for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()){
            if(slot == EntityEquipmentSlot.MAINHAND || slot == EntityEquipmentSlot.OFFHAND)
                continue;
        	container.addSlot(new SlotFakeArmor(container.getPlayer(), getInventory(container), getInventory(container).getSizeInventory() - 1 - slot.getIndex(), 40 + slot.getIndex() * 18, 40, slot));
        }

		for (int i = 0; i < 3; ++i){
			for (int j = 0; j < 9; ++j){
				container.addSlot(new Slot(container.getPlayer().inventory, j + i * 9 + 9, 40 + j * 18, 70 + i * 18));
	        }
		}	

	    for (int i = 0; i < 9; ++i) {
	    	container.addSlot(new Slot(container.getPlayer().inventory, i, 40 + i * 18, 128));
	    }
	}
	
	@Override
	public ItemStack transferStackInSlot(IContainerFilter container, EntityPlayer par1EntityPlayer, int par2) {
		ItemStack itemstack = null;
	    Slot slot = (Slot)container.getInventorySlots().get(par2);

	    if (slot != null && slot.getHasStack()) {
	        ItemStack itemstack1 = slot.getStack();
	        itemstack = itemstack1.copy();
	        boolean wasPhantomSlot = false;

	        if (itemstack.getItem() instanceof ItemArmor && !((Slot)container.getInventorySlots().get(5 + ((ItemArmor)itemstack.getItem()).armorType.getSlotIndex())).getHasStack() && (par2 >= 9 && par2 < 45 || par2 >= 0 && par2 < 4)) {
	        	wasPhantomSlot = true;
	        	int j = 5 + ((ItemArmor)itemstack.getItem()).armorType.getSlotIndex();
	                
	            if (!container.mergeItemStacks(itemstack1, j, j + 1, false))
	                return null;
	        }
	        else if(par2 >= 9 && par2 < 45 && !(itemstack.getItem() instanceof ItemArmor) && !((Slot)container.getInventorySlots().get(4)).getHasStack()) {
	        	wasPhantomSlot = true;
	        	if (!container.mergeItemStacks(itemstack1, 4, 4 + 1, false))
	                return null;
	        }
	        else if (par2 >= 36 && par2 < 45) {
	            if (!container.mergeItemStacks(itemstack1, 9, 36, false))
	                return null;
	        }
	        else if (!container.mergeItemStacks(itemstack1, 36, 45, false))
	            return null;
	          
	        if(!wasPhantomSlot) {
	        	if(itemstack1.stackSize == 0)
	        		slot.putStack((ItemStack)null);
	            else
	            	slot.onSlotChanged();
	        }
	        else
	        	itemstack1.stackSize = itemstack.stackSize;
	            
	        if (itemstack1.stackSize == itemstack.stackSize)
	            return null;

	        slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
	    }

	    return itemstack;
	}
	
	@Override
	public String getSaveId() {
		return "mobArmor";
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList list = (NBTTagList)tag.getTag("playerData");
		for(int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound data = list.getCompoundTagAt(i);
			String username = data.getString("username");
			NBTTagList nbttaglist = (NBTTagList)data.getTag("items");

	        for (int k = 0; k < nbttaglist.tagCount(); ++k) {
	            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(k);

	            this.getInventory(username).contents[k] = ItemStack.loadItemStackFromNBT(nbttagcompound1.getCompoundTag("item"));
	            this.getInventory(username).umlimited[k] = nbttagcompound1.getBoolean("isUnlimited");
	        }
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) { 
		NBTTagList list = new NBTTagList();
		for(String key : invMap.keySet()) {
			NBTTagCompound data = new NBTTagCompound();
			data.setString("username", key);
			
			NBTTagList nbttaglist = new NBTTagList();
			for(int i = 0; i < this.getInventory(key).getSizeInventory(); ++i) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setBoolean("isUnlimited", this.getInventory(key).isSlotUnlimited(i));
				if(this.getInventory(key).contents[i] != null)
					data.setTag("item", this.getInventory(key).contents[i].writeToNBT(new NBTTagCompound()));
				else
					data.setTag("item", new NBTTagCompound());
				nbttaglist.appendTag(nbttagcompound1);
			}
			data.setTag("items", nbttaglist);
			list.appendTag(data);
		}
		tag.setTag("playerData", list);
		return tag; 
	}
	
	public MobArmor getInventory(IContainerFilter container) {
		String username = container.getPlayer().getName().toLowerCase();
	    if(!invMap.containsKey(username))
	    	invMap.put(username, new MobArmor(4));
	    	
		return invMap.get(username);
	}
	
	public MobArmor getInventory(String username) {
	    if(!invMap.containsKey(username))
	    	invMap.put(username, new MobArmor(4));
	    	
		return invMap.get(username);
	}

	public class MobArmor implements IUnlimitedInventory {

		public ItemStack[] contents;
		
		public MobArmor(int inventorySize) {
			this.contents = new ItemStack[inventorySize];
			umlimited = new boolean[inventorySize];
		}
		
		public int getSizeInventory() {
		    return this.contents.length;
		}

		public ItemStack getStackInSlot(int par1) {
		    return this.contents[par1];
		}

		public ItemStack decrStackSize(int par1, int par2) {
		     if (this.contents[par1] != null) {
		        ItemStack itemstack;

		        if (this.contents[par1].stackSize <= par2) {
		            itemstack = this.contents[par1];
		            this.contents[par1] = null;
		            return itemstack;
		        }
		        else {
		            itemstack = this.contents[par1].splitStack(par2);

		            if (this.contents[par1].stackSize == 0) {
		                this.contents[par1] = null;
		            }

		            return itemstack;
		        }
		    }
		    else {
		        return null;
		    }
		}

		@Override
		public ItemStack removeStackFromSlot(int par1) {
		    if (this.contents[par1] != null) {
		        ItemStack itemstack = this.contents[par1];
		        this.contents[par1] = null;
		        return itemstack;
		    }
		    else {
		        return null;
		    }
		}

		public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
		    this.contents[par1] = par2ItemStack;

		    if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
		        par2ItemStack.stackSize = this.getInventoryStackLimit();
		    }
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public void markDirty() {
			
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			return true;
		}
/**
		@Override
		public void openInventory() {}

		@Override
		public void closeInventory() {}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return false;
		}

		@Override
		public String getInventoryName() {
			return "Mob Armor";
		}

		@Override
		public boolean hasCustomInventoryName() {
			return true;
		}**/

		public boolean[] umlimited;
		
		@Override
		public boolean isSlotUnlimited(int slotIndex) {
			return this.umlimited[slotIndex];
		}
		
		@Override
		public void setSlotUnlimited(int slotIndex, boolean isUnlimited) {
			this.umlimited[slotIndex] = isUnlimited;
		}

		@Override
		public void openInventory(EntityPlayer player) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void closeInventory(EntityPlayer player) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getField(int id) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void setField(int id, int value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getFieldCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasCustomName() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public ITextComponent getDisplayName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
