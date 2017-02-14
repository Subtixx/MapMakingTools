package mapmakingtools.tools.filter;

import java.util.Hashtable;
import java.util.Map;

import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.api.interfaces.IFilterServer;
import mapmakingtools.container.IUnlimitedInventory;
import mapmakingtools.container.SlotFake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;

/**
 * @author ProPercivalalb
 */
public class ItemSpawnerServerFilter extends IFilterServer {

	public static Map<String, FillInventory> invMap = new Hashtable<String, FillInventory>();
	
	@Override
	public void addSlots(IContainerFilter container) {
        container.addSlot(new SlotFake(getInventory(container), 0, 23, 37).setCantBeUnlimited());
		for (int i = 0; i < 3; ++i){
			for (int j = 0; j < 9; ++j) {
				container.addSlot(new Slot(container.getPlayer().inventory, j + i * 9 + 9, 59 + j * 18, 23 + i * 18));
	        }
		}	

	    for (int i = 0; i < 9; ++i) {
	    	container.addSlot(new Slot(container.getPlayer().inventory, i, 59 + i * 18, 81));
	    }
	}

	@Override
	public String getSaveId() { 
		return "changeItem"; 
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList list = (NBTTagList)tag.getTag("playerData");
		for(int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound data = list.getCompoundTagAt(i);
			String username = data.getString("username");
			boolean isUnlimited = data.getBoolean("isUnlimited");
			if(data.hasKey("item")) {
				ItemStack stack = ItemStack.loadItemStackFromNBT(data.getCompoundTag("item"));
				this.getInventory(username).contents[0] = stack;
				this.getInventory(username).setSlotUnlimited(0, isUnlimited);
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) { 
		NBTTagList list = new NBTTagList();
		for(String key : invMap.keySet()) {
			NBTTagCompound data = new NBTTagCompound();
			data.setString("username", key);
			data.setBoolean("isUnlimited", this.getInventory(key).isSlotUnlimited(0));
			if(this.getInventory(key).contents[0] != null)
				data.setTag("item", this.getInventory(key).contents[0].writeToNBT(new NBTTagCompound()));
			list.appendTag(data);
		}
		tag.setTag("playerData", list);
		return tag; 
	}
	
	public FillInventory getInventory(IContainerFilter containerFilter) {
		String username = containerFilter.getPlayer().getName().toLowerCase();
	    if(!invMap.containsKey(username))
	    	invMap.put(username, new FillInventory(1));
	    	
		return invMap.get(username);
	}
	
	public FillInventory getInventory(String username) {
	    if(!invMap.containsKey(username))
	    	invMap.put(username, new FillInventory(1));
	    	
		return invMap.get(username);
	}

	public class FillInventory implements IUnlimitedInventory {

		public ItemStack[] contents;
		
		public FillInventory(int inventorySize) {
			this.contents = new ItemStack[inventorySize];
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
			return 64;
		}

		@Override
		public void markDirty() {}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			return true;
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return false;
		}
/**
		@Override
		public void openInventory() {}

		@Override
		public void closeInventory() {}
		
		@Override
		public String getInventoryName() {
			return "Item Spawner";
		}

		@Override
		public boolean hasCustomInventoryName() {
			return true;
		}**/
		
		@Override
		public boolean isSlotUnlimited(int slotIndex) {
			return false;
		}
		
		@Override
		public void setSlotUnlimited(int slotIndex, boolean isUnlimited) {
			
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
	}
}
