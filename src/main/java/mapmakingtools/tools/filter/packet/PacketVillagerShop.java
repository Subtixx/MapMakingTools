package mapmakingtools.tools.filter.packet;

import java.io.IOException;

import mapmakingtools.api.interfaces.IContainerFilter;
import mapmakingtools.helper.LogHelper;
import mapmakingtools.helper.ReflectionHelper;
import mapmakingtools.network.AbstractMessage.AbstractServerMessage;
import mapmakingtools.tools.filter.VillagerShopServerFilter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class PacketVillagerShop extends AbstractServerMessage {

	public int entityId;
	public int[] recipeUses;
	
	public PacketVillagerShop() {}
	public PacketVillagerShop(int entityId, int[] recipeUses) {
		this.entityId = entityId;
		this.recipeUses = recipeUses;
	}

	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.entityId = packetbuffer.readInt();
		this.recipeUses = new int[9];
		for(int i = 0; i < this.recipeUses.length; i++)
			this.recipeUses[i] = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(this.entityId);
		for(int i = 0; i < this.recipeUses.length; i++)
			packetbuffer.writeInt(this.recipeUses[i]);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		LogHelper.info("dawe " + this.entityId);
		if(player.openContainer instanceof IContainerFilter) {
			LogHelper.info("filter " + this.entityId);
			IContainerFilter container = (IContainerFilter)player.openContainer;
			if(container.getCurrentFilter() instanceof VillagerShopServerFilter) {
				LogHelper.info("shop " + this.entityId);
				VillagerShopServerFilter shop = (VillagerShopServerFilter)container.getCurrentFilter();
		        World world = player.worldObj;
		        Entity entity = world.getEntityByID(this.entityId);
		        if(entity instanceof EntityVillager) {
		        	LogHelper.info("villager " + this.entityId);
		        	EntityVillager villager = (EntityVillager)entity;
		        	MerchantRecipeList recipeList = new MerchantRecipeList();
		        	
		        	for(int i = 0; i < shop.getAmountRecipes(container.getPlayer()); ++i) {
			        	LogHelper.info("amount " + i);
		        		ItemStack input1 = shop.getInventory(container).getStackInSlot(i * 3);
		        		ItemStack input2 = shop.getInventory(container).getStackInSlot(i * 3 + 1);
		        		ItemStack output = shop.getInventory(container).getStackInSlot(i * 3 + 2);
		        		if(input1 != null)
		        			LogHelper.info("input1 " + input1.toString());
		        		if(input2 != null)
		        			LogHelper.info("input2 " + input2.toString());
		        		if(output != null)
		        			LogHelper.info("output " + output.toString());
		        		
		        		if(input1 == null && input2 != null) {
		        			input1 = input2.copy();
		        			input2 = null;
		        		}
		        		if(input1 == null) {
		        			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.villagershop.inputnull");
							chatComponent.getStyle().setItalic(true);
							chatComponent.getStyle().setColor(TextFormatting.RED);
							player.addChatMessage(chatComponent);
		        			return;
		        		}
		        		if(output == null) {
		        			TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.villagershop.outputnull");
							chatComponent.getStyle().setItalic(true);
							chatComponent.getStyle().setColor(TextFormatting.RED);
							player.addChatMessage(chatComponent);
		        			return;
		        		}
		        		MerchantRecipe recipe = new MerchantRecipe(input1, input2, output, 0, this.recipeUses[i]);
		        		recipeList.add(recipe);
		        	}
		        	ReflectionHelper.setField(EntityVillager.class, villager, 7, recipeList);
		        	
		        	TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.filter.villagershop.complete");
					chatComponent.getStyle().setItalic(true);
					player.addChatMessage(chatComponent);
		        }
		    }
		}
	}
}
