package mapmakingtools.handler;

import mapmakingtools.ModItems;
import mapmakingtools.client.gui.GuiConfigMapMakingTools;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.network.packet.PacketUpdateBlock;
import mapmakingtools.network.packet.PacketUpdateEntity;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ProPercivalalb
 */
public class ActionHandler {
	@SubscribeEvent
	public void rightClick(PlayerInteractEvent event) {
		
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		World world = player.worldObj;
		BlockPos pos = event.getPos();
		EnumFacing side = event.getFace();
		
		//if(!world.isRemote) {
		//	player.addChatMessage(new TextComponentTranslation("%s", "" + world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos))));
			//event.setCanceled(true);
		//}

		if(event instanceof PlayerInteractEvent.LeftClickBlock) {
			if (PlayerAccess.canEdit(player) && !world.isRemote) {

				//Quick Build - Left Click
				if (stack != null && stack.getItem() == ModItems.editItem && stack.getMetadata() == 0) {
					PlayerData playerData = WorldData.getPlayerData(player);

					BlockPos movedPos = pos;
					if (player.isSneaking())
						movedPos = pos.add(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());


					if (playerData.setFirstPoint(movedPos)) {
						if (playerData.hasSelectedPoints()) {
							TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.positive", "" + playerData.getBlockCount());
							chatComponent.getStyle().setColor(TextFormatting.GREEN);
							player.addChatMessage(chatComponent);
						} else {
							TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.negative");
							chatComponent.getStyle().setColor(TextFormatting.RED);
							player.addChatMessage(chatComponent);
						}
						playerData.sendUpdateToClient();
						event.setCanceled(true);
					}
				}
			}
		}
		if(event instanceof PlayerInteractEvent.RightClickBlock) {
			if (PlayerAccess.canEdit(player) && !world.isRemote) {

				//Quick Build - Right Click
				if (stack != null && stack.getItem() == ModItems.editItem && stack.getMetadata() == 0) {
					PlayerData playerData = WorldData.getPlayerData(player);

					BlockPos movedPos = pos;
					if (player.isSneaking())
						movedPos = pos.add(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());

					if (playerData.setSecondPoint(movedPos)) {
						if (playerData.hasSelectedPoints()) {
							TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.positive", "" + playerData.getBlockCount());
							chatComponent.getStyle().setColor(TextFormatting.GREEN);
							player.addChatMessage(chatComponent);
						} else {
							TextComponentTranslation chatComponent = new TextComponentTranslation("mapmakingtools.chat.quickbuild.blocks.count.negative");
							chatComponent.getStyle().setColor(TextFormatting.RED);
							player.addChatMessage(chatComponent);
						}
						playerData.sendUpdateToClient();
						event.setCanceled(true);
					}
				} else if (stack != null && stack.getItem() == ModItems.editItem && stack.getMetadata() == 1) {
					if (!world.isRemote) {
						TileEntity tileEntity = world.getTileEntity(pos);
						if (tileEntity != null) {
							if (tileEntity instanceof TileEntityMobSpawner)
								SpawnerUtil.confirmHasRandomMinecart(((TileEntityMobSpawner) tileEntity).getSpawnerBaseLogic());


							PacketDispatcher.sendTo(new PacketUpdateBlock(tileEntity, pos), player);
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void entityInteract(PlayerInteractEvent event) {
		if(event instanceof PlayerInteractEvent.EntityInteract){
			EntityPlayer player = event.getEntityPlayer();
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
			World world = player.worldObj;
			Entity entity = ((PlayerInteractEvent.EntityInteract) event).getTarget();
		
			if(stack != null && stack.getItem() == ModItems.editItem && stack.getMetadata() == 1) {
				if(!world.isRemote) {
					PacketDispatcher.sendTo(new PacketUpdateEntity(entity), player);
					event.setCanceled(true);
				}
			}
		}
	}

}
