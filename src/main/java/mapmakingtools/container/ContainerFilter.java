package mapmakingtools.container;

import java.util.List;

import mapmakingtools.api.enums.TargetType;
import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.api.interfaces.IFilterServer;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.tools.filter.packet.PacketPhantomInfinity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

/**
 * @author ProPercivalalb
 */
public class ContainerFilter extends Container implements IContainerFilter {

	//Extra data the class holds
	public EntityPlayer player;
	public BlockPos pos;
	public int entityId;
	public TargetType mode;
	
	public List<IFilterServer> filterList;
	public IFilterServer filterCurrent;
	public int selected;
	
	public ContainerFilter(List<IFilterServer> filterList, EntityPlayer player) {
		//this.inventorySlots = new ArrayListSlot();
		this.filterList = filterList;
		this.player = player;
	}
	
	public ContainerFilter setEntityId(int entityId) {
		this.entityId = entityId;
		this.mode = TargetType.ENTITY;
		return this;
	}
	
	public ContainerFilter setBlockPos(BlockPos pos) {
		this.pos = pos;
		this.mode = TargetType.BLOCK;
		return this;
	}
	
	public void setSelected(int selected) {
		this.selected = selected;
	    this.inventorySlots.clear();
	    this.inventoryItemStacks.clear();
	    this.filterCurrent = this.filterList.get(selected);
	    if(this.filterCurrent != null)
	    	this.filterCurrent.addSlots(this);
	}
	
	@Override
	public void addSlot(Slot slot) {
		this.addSlotToContainer(slot);
	}
	
	@Override
	public Slot addSlotToContainer(Slot slot) {
		slot.xDisplayPosition += 62 / 2;
        slot.slotNumber = this.inventorySlots.size();
        this.inventorySlots.add(slot);
        this.inventoryItemStacks.add((ItemStack)null);
        if(slot instanceof IPhantomSlot && ((IPhantomSlot)slot).canBeUnlimited())
        	PacketDispatcher.sendTo(new PacketPhantomInfinity(slot.getSlotIndex(), ((IPhantomSlot)slot).isUnlimited()), this.player);
        return slot;
    }
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
    	ItemStack stack = null;
    	if(filterCurrent != null && (stack = filterCurrent.transferStackInSlot(this, par1EntityPlayer, par2)) != null) {
    		return stack;
    	}
    	else {
    		return null;
    	}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public EntityPlayer getPlayer() {
		return this.player;
	}

	@Override
	public Slot getSlot(int par1) {
		if(par1 >= 0 && par1 < inventorySlots.size()) {
			return (Slot)this.inventorySlots.get(par1);
		}
		return null;
    }
	
	@Override
	public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack) {
        for (int i = 0; i < par1ArrayOfItemStack.length; ++i) {
            Slot slot = this.getSlot(i);
            if(slot != null) {
            	slot.putStack(par1ArrayOfItemStack[i]);
            }
        }
    }
	
	@Override
	public void putStackInSlot(int par1, ItemStack par2ItemStack) {
		Slot slot = this.getSlot(par1);
        if(slot != null) {
        	slot.putStack(par2ItemStack);
        }
	}

	//Phantom Slot
	@Nullable
	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		Slot slot = slotId < 0 || slotId >= inventorySlots.size() ? null : (Slot) this.inventorySlots.get(slotId);
		if (slot instanceof IPhantomSlot) {
			if(!this.getWorld().isRemote)
				return slotClickPhantom(slot, dragType, clickTypeIn, player);
			return null;
		}

		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	
	private ItemStack slotClickPhantom(Slot slot, int mouseButton, ClickType clickType, EntityPlayer player) {
		ItemStack stack = null;
		IPhantomSlot phantomSlot = (IPhantomSlot)slot;
		InventoryPlayer playerInv = player.inventory;
		ItemStack stackHeld = playerInv.getItemStack();
		ItemStack slotStack = slot.getStack();
		
		if(stackHeld != null) {
			ItemStack phantomStack = stackHeld.copy();
			phantomStack.stackSize = slotStack != null ? slotStack.stackSize : 1;
			slot.putStack(phantomStack);
		}
		else if(slotStack != null) {
			
			if(mouseButton == 2) {
				slot.putStack(null);
				if(slot.inventory instanceof IUnlimitedInventory)
					((IUnlimitedInventory)slot.inventory).setSlotUnlimited(slot.getSlotIndex(), false);
				phantomSlot.setIsUnlimited(false);
				PacketDispatcher.sendTo(new PacketPhantomInfinity(slot.getSlotIndex(), false), player);
			}
			else {
				int stackSize = mouseButton == 1 ? slotStack.stackSize + 1: slotStack.stackSize - 1;
			
				if(stackSize > 1 && phantomSlot.canBeUnlimited() && phantomSlot.isUnlimited()) {
					stackSize = 1;
					if(slot.inventory instanceof IUnlimitedInventory)
						((IUnlimitedInventory)slot.inventory).setSlotUnlimited(slot.getSlotIndex(), false);
					phantomSlot.setIsUnlimited(false);
					PacketDispatcher.sendTo(new PacketPhantomInfinity(slot.getSlotIndex(), false), player);
				}
				else if(stackSize < 1 && phantomSlot.canBeUnlimited() && !phantomSlot.isUnlimited()) {
					stackSize = 1;
					if(slot.inventory instanceof IUnlimitedInventory)
						((IUnlimitedInventory)slot.inventory).setSlotUnlimited(slot.getSlotIndex(), true);
					phantomSlot.setIsUnlimited(true);
					PacketDispatcher.sendTo(new PacketPhantomInfinity(slot.getSlotIndex(), true), player);
				}
				else if(stackSize < 1) {
					slot.putStack(null);
					if(slot.inventory instanceof IUnlimitedInventory)
						((IUnlimitedInventory)slot.inventory).setSlotUnlimited(slot.getSlotIndex(), false);
					phantomSlot.setIsUnlimited(false);
					PacketDispatcher.sendTo(new PacketPhantomInfinity(slot.getSlotIndex(), false), player);
				}
				slotStack.stackSize = stackSize;
			}
		}
		this.detectAndSendChanges();
		return stack;
	}
	
	@Override
	public List<Slot> getInventorySlots() {
		return this.inventorySlots;
	}

	@Override
	public boolean mergeItemStacks(ItemStack itemstack1, int j, int i, boolean b) {
		return this.mergeItemStack(itemstack1, j, i, b);
	}

	@Override
	public BlockPos getBlockPos() {
		return this.pos;
	}
	
	@Override
	public int getEntityId() {
		return this.entityId;
	}

	@Override
	public World getWorld() {
		return this.getPlayer().worldObj;
	}

	@Override
	public IFilterServer getCurrentFilter() {
		return this.filterCurrent;
	}

	@Override
	public Entity getEntity() {
		return this.getWorld().getEntityByID(this.getEntityId());
	}
}
