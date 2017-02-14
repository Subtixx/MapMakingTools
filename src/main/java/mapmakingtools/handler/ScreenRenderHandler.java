package mapmakingtools.handler;

import java.lang.reflect.Field;
import java.util.*;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import mapmakingtools.helper.*;
import mapmakingtools.lib.Reference;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.UsernameCache;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mapmakingtools.ModItems;
import mapmakingtools.tools.ClientData;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.datareader.BlockList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static mapmakingtools.helper.ClientHelper.mc;

/**
 * @author ProPercivalalb
 **/
public class ScreenRenderHandler {
	
	private int INDEX_HISTORY = 1;
	private boolean hasButtonBeenUp = true;
	public boolean isHelperOpen = false;
	public RenderItem renderer = mc.getRenderItem();
	public Field chatField = ReflectionHelper.getField(GuiChat.class, 4);
	
	@SubscribeEvent
	public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		//Event variables
		float partialTicks = event.getPartialTicks();
	    ScaledResolution resolution = event.getResolution();
	    ElementType type = event.getType();
		
	    EntityPlayer player = mc.thePlayer;
	    World world = player.worldObj;
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
	    
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        int mouseX = Mouse.getX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getY() * height / mc.displayHeight - 1;
		
		if(type == ElementType.HELMET && stack != null && stack.getItem() == ModItems.editItem && stack.getMetadata() == 0 && PlayerAccess.canEdit(mc.thePlayer)) {
    		
    		PlayerData data = ClientData.playerData;
    		FontRenderer font = mc.fontRendererObj;
    		GL11.glPushMatrix();
    		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            
            if(data.hasSelectedPoints()) {
        		int[] size = data.getSelectionSize();
            	font.drawStringWithShadow(String.format("Selection Size: %d * %d * %d = %d", size[0], size[1], size[2], data.getBlockCount()), 4, 4, -1);
            }
            else
            	font.drawStringWithShadow("Nothing Selected", 4, 4, -1);
            	
    		GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    		GL11.glPopMatrix();
		}

		if(type == ElementType.HELMET && mc.currentScreen == null && stack != null && stack.getItem() == ModItems.editItem && stack.getMetadata() == 1 && PlayerAccess.canEdit(player)) {

			RayTraceResult objectMouseOver = mc.objectMouseOver;
			
			if(objectMouseOver != null) {
	        	List<String> list = new ArrayList<String>();
				
				AddBlockInfo(list, objectMouseOver);
				AddEntityInfo(list, objectMouseOver);
				
            	drawHoveringText(list, 0, 25, 1000, 200, mc.fontRendererObj, false);

                if(objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY)
                    mapmakingtools.helper.RenderHelper.renderEntity(objectMouseOver.entityHit, 15, 15, 10);
                else if(objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                    IBlockState state = world.getBlockState(objectMouseOver.getBlockPos());
                    mapmakingtools.helper.RenderHelper.renderObject(mc, 15, 15, state.getBlock(), false);
                }
			}
		}
		
	    if(Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && PlayerAccess.canSeeBlockIdHelper(player)) {
	    	if(type == RenderGameOverlayEvent.ElementType.HELMET) {
	   
	    		if(mc.currentScreen instanceof GuiChat) {
	    			GuiChat chat = (GuiChat) mc.currentScreen;
	    			int chatPostion = ReflectionHelper.getField(chatField, GuiTextField.class, chat).getCursorPosition();
	    			boolean isHovering = false;

	    			float scale = 1F; //1.0F is normal size
	    			
	    			
	    			int totalWidth = MathHelper.floor_double((double)(width - 8) / 16.0D);
	    			FMLLog.info(BlockList.getListSize() + " " + totalWidth);
	    			int totalHeight = MathHelper.floor_double((double)BlockList.getListSize() / (double)totalWidth);
	    			int renderOffset = (width - 8 - totalWidth * 16) / 2;
	    			
	    			GL11.glPushMatrix();
	    			GL11.glEnable(GL11.GL_SCISSOR_TEST);
	    			GL11.glDisable(GL11.GL_TEXTURE_2D);
	    			this.clipToSize(2, 2, width - 4, height - 40, resolution);
	    			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
	    			drawTexturedModalRect(2, 2, 0, 0, width - 4, 4 + totalHeight * 16);
	    			GL11.glEnable(GL11.GL_TEXTURE_2D);
                	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            		GL11.glScalef(scale, scale, scale);
            		RenderHelper.enableGUIStandardItemLighting();
                	GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                	this.renderer.zLevel = 200F;
	    			
	    			int row = 0;
	    			int column = 0;
	    			for(int i = 0; i < BlockList.getListSize(); ++i) {
	    				ItemStack item = BlockList.getList().get(i);
	    				
	    				

	    				if(item == null || item.getItem() == null)
	    					continue;
	    				
	    				if(column >= totalWidth) {
	    					row += 1;
	    					column = 0;
	    				}


	                	if(mouseX > 4 + 16 * column + renderOffset && mouseX < 4 + 16 * (column + 1) + renderOffset && mouseY > 4 + 16 * row && mouseY < 4 + 16 * (row + 1)) {
	                		GL11.glDisable(GL11.GL_TEXTURE_2D);
	                		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	                		drawTexturedModalRect(4 + 16 * column + renderOffset, 4 + 16 * row, 0, 0, 16, 16);
	                		GL11.glEnable(GL11.GL_TEXTURE_2D);

		    				if(Mouse.isButtonDown(1) && hasButtonBeenUp) {
				    			String txtToInsert = String.format((ReflectionHelper.getField(chatField, GuiTextField.class, chat).getText().endsWith(" ") ? "" : " ") +"%s %s ", Block.REGISTRY.getNameForObject(Block.getBlockFromItem(item.getItem())), item.getItemDamage());
				    			LogHelper.info(txtToInsert);
				    			for(int s = 0; s < txtToInsert.length(); s++) {
				    				char ch = txtToInsert.charAt(s);
				    				ReflectionHelper.getField(chatField, GuiTextField.class, chat).textboxKeyTyped(ch, Integer.valueOf(ch));
				    			}
				    			ReflectionHelper.getField(chatField, GuiTextField.class, chat).setCursorPosition(chatPostion + txtToInsert.length());
				    			hasButtonBeenUp = false;
		    				}
	                	}
	                	if(!Mouse.isButtonDown(1)) {
		    				hasButtonBeenUp = true;
		    			}
	                	this.renderer.renderItemIntoGUI(item, 4 + 16 * column + renderOffset, 4 + 16 * row);

	    				column++;
	    			}
	    			
	    			row = 0;
	    			column = 0;

	    			GL11.glDisable(GL11.GL_SCISSOR_TEST);
	    			for(int i = 0; i < BlockList.getListSize(); ++i) {
	    				ItemStack item = BlockList.getList().get(i);
	    				if(item == null || item.getItem() == null)
	    					continue;
	    				if(column >= totalWidth) {
	    					row += 1;
	    					column = 0;
	    				}
	    				
	    				if(mouseX > 4 + 16 * column + renderOffset && mouseX < 4 + 16 * (column + 1) + renderOffset && mouseY > 4 + 16 * row && mouseY < 4 + 16 * (row + 1))
	    					drawHoveringText(Arrays.asList(TextFormatting.GREEN + item.getDisplayName(), TextFormatting.ITALIC + String.format("%s %s", Block.REGISTRY.getNameForObject(Block.getBlockFromItem(item.getItem())), item.getItemDamage())), mouseX, mouseY, width, height, mc.fontRendererObj, true);
	    				column++;
	    			}
	    			
	            	RenderHelper.disableStandardItemLighting();
	    			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    			GL11.glPopMatrix();
	            }
	    	}
	    }
	}

    private void AddBlockInfo(List<String> list, RayTraceResult objectMouseOver) {
        if(objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            IBlockState state = mc.theWorld.getBlockState(objectMouseOver.getBlockPos()).getActualState(mc.theWorld, objectMouseOver.getBlockPos());
            ResourceLocation resourceLocation = (ResourceLocation)Block.REGISTRY.getNameForObject(state.getBlock());
            String id = resourceLocation.toString();
            Block block = state.getBlock();
            int meta = block.getMetaFromState(state);
            int stateId = Block.getStateId(state);

            list.add(String.format(TextFormatting.WHITE+"%s (#%d/%d:%d)", block.getLocalizedName(), Block.REGISTRY.getIDForObject(block), meta, stateId));
            list.add(String.format(TextFormatting.DARK_GRAY+"(%s)", resourceLocation.toString(), stateId));

            if (block instanceof BlockLever) {
                Boolean powered = state.getValue(BlockLever.POWERED);
                list.add(String.format(TextFormatting.GRAY+"State: "+ TextFormatting.YELLOW+"%s", powered ? "On" : "Off"));
            } else if (block instanceof BlockRedstoneComparator) {
                BlockRedstoneComparator.Mode mode = state.getValue(BlockRedstoneComparator.MODE);
                list.add(String.format(TextFormatting.GRAY+"Mode: "+TextFormatting.YELLOW+"%s", mode.getName()));
            } else if (block instanceof BlockRedstoneRepeater) {
                Boolean locked = state.getValue(BlockRedstoneRepeater.LOCKED);
                Integer delay = state.getValue(BlockRedstoneRepeater.DELAY);
                list.add(String.format(TextFormatting.GRAY+"Delay: "+TextFormatting.YELLOW+"%d ticks", delay));
                if (locked) {
                    list.add(TextFormatting.RED + "Locked");
                }
            } else if (block instanceof BlockRedstoneWire) {
                int redstonePower;
                if (block instanceof BlockRedstoneWire) {
                    redstonePower = state.getValue(BlockRedstoneWire.POWER);
                } else {
                    redstonePower = mc.theWorld.getRedstonePower(objectMouseOver.getBlockPos(), objectMouseOver.sideHit.getOpposite());
                }
                list.add(String.format(TextFormatting.GRAY+"Power: "+TextFormatting.YELLOW+"%d", redstonePower));
            }

            if (block instanceof BlockCrops) {
                BlockCrops crops = (BlockCrops) block;
                int age = state.getValue(BlockCrops.AGE);
                int maxAge = crops.getMaxAge();
                if (crops.isMaxAge(state)) {
                    list.add(TextFormatting.GREEN + "Fully grown");
                } else {
                    list.add(String.format(TextFormatting.GRAY + "Growth: " + TextFormatting.YELLOW + "%d%%",(age * 100) / maxAge));
                }
            } else if (block instanceof BlockNetherWart) {
                int age = state.getValue(BlockNetherWart.AGE);
                int maxAge = 3;
                if (age == maxAge) {
                    list.add(TextFormatting.GREEN + "Fully grown");
                } else {
                    list.add(String.format(TextFormatting.GRAY + "Growth: " + TextFormatting.YELLOW + "%d%%",(age * 100) / maxAge));
                }
            }

            // Always display mod info last!
            ModContainer container = Loader.instance().getIndexedModList().get(resourceLocation.getResourceDomain());
            if(container != null)
                list.add(TextFormatting.ITALIC + "" + container.getName());
            else if(resourceLocation.getResourceDomain().equals("minecraft"))
                list.add(TextFormatting.ITALIC + "Minecraft");
            else
                list.add(TextFormatting.ITALIC + "" + TextFormatting.RED + "Unknown");
        }
    }

    public void AddEntityInfo(List<String> list, RayTraceResult objectMouseOver)
    {
        if(objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {

            Entity entity = objectMouseOver.entityHit;
            String id = EntityList.getEntityString(entity);

            if (id == null && entity instanceof EntityPlayer)
                id = "Player";
            else if (id == null && entity instanceof EntityLightningBolt)
                id = "LightningBolt";

            list.add(TextFormatting.YELLOW + id + " (ID: " + entity.getEntityId() + ")");

            // TODO: Missing sync?
            if (entity instanceof EntityLiving) {
                EntityLivingBase livingEntity = ((EntityLiving) entity);
                list.add(String.format("%.0f/%.0f", livingEntity.getHealth(), livingEntity.getMaxHealth()));

                Collection<net.minecraft.potion.PotionEffect> effects = livingEntity.getActivePotionEffects();
                if (!effects.isEmpty()) {
                    for (net.minecraft.potion.PotionEffect effect : effects) {
                        net.minecraft.potion.Potion potion = effect.getPotion();
                        String s1 = net.minecraft.util.text.translation.I18n.translateToLocal(effect.getEffectName()).trim();
                        if (effect.getAmplifier() > 0) {
                            s1 += String.format(" %s", net.minecraft.util.text.translation.I18n.translateToLocal("potion.potency." + effect.getAmplifier()).trim());
                        }

                        if (effect.getDuration() > 20) {
                            s1 += String.format(" (%s)", net.minecraft.potion.Potion.getPotionDurationString(effect, 1.0f));
                        }

                        if (potion.isBadEffect()) {
                            list.add(TextFormatting.RED + s1);
                        } else {
                            list.add(TextFormatting.GREEN + s1);
                        }
                    }
                }

                // Owner infos
                UUID ownerId = null;
                if (entity instanceof IEntityOwnable) {
                    ownerId = ((IEntityOwnable) entity).getOwnerId();
                } else if (entity instanceof EntityHorse) {
                    ownerId = ((EntityHorse) entity).getOwnerUniqueId();
                }

                if (ownerId != null) {
                    String username = UsernameCache.getLastKnownUsername(ownerId);
                    if (username == null) {
                        list.add(TextFormatting.RED + "Unknown owner");
                    } else {
                        list.add(TextFormatting.GRAY + "Owned by: " + TextFormatting.YELLOW + username);
                    }
                }

                // Horse infos
                if (entity instanceof EntityHorse) {
                    double jumpStrength = ((EntityHorse) entity).getHorseJumpStrength();
                    double jumpHeight = -0.1817584952 * jumpStrength * jumpStrength * jumpStrength + 3.689713992 * jumpStrength * jumpStrength + 2.128599134 * jumpStrength - 0.343930367;
                    list.add(String.format(TextFormatting.GRAY + "Jump height: " + TextFormatting.YELLOW + "%.2f", jumpHeight));
                    IAttributeInstance iattributeinstance = ((EntityHorse) entity).getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                    list.add(String.format(TextFormatting.GRAY + "Speed: " + TextFormatting.YELLOW + "%.2f", iattributeinstance.getAttributeValue()));
                }

                // Collar color
                if (entity instanceof EntityWolf) {
                    EnumDyeColor collarColor = ((EntityWolf) entity).getCollarColor();
                    list.add(TextFormatting.GRAY + "Speed: " + TextFormatting.YELLOW + collarColor.getName());
                }

                if(entity instanceof EntityVillager)
                {
                    EntityVillager villager = ((EntityVillager)entity);
                    list.add(String.format("Profession: "+TextFormatting.YELLOW+"%s", I18n.translateToLocal(villager.getDisplayName().getUnformattedText())));
                }

                if(entity instanceof EntityZombie){
                    EntityZombie zombie = ((EntityZombie)entity);
                    if(zombie.isChild())
                        list.add(TextFormatting.YELLOW+"Baby Zombie");
                    if(zombie.isVillager())
                        list.add(TextFormatting.YELLOW+"Zombie Villager");
                    if(zombie.isConverting())
                        list.add(TextFormatting.YELLOW+"Transforming into Villager...");
                }
            }

            int i = id.indexOf(".");
            if (i >= 0) {
                String domain = id.substring(0, i);
                ModContainer container = Loader.instance().getIndexedModList().get(domain);
                if (container != null)
                    list.add(TextFormatting.ITALIC + "" + container.getName());
                else
                    list.add(TextFormatting.ITALIC + "" + TextFormatting.RED + "Unknown");
            } else
                list.add(TextFormatting.ITALIC + "Minecraft");
        }
    }
	
	public int chatOffset = 0;
	
	@SubscribeEvent
	public void chatEventPre(RenderGameOverlayEvent.Chat event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.CHAT)
			return;
		
		//isHelperOpen = Mouse.isButtonDown(0);
		if(mc.currentScreen instanceof GuiChat) {
			ScaledResolution scaling = event.getResolution();
			if(this.isHelperOpen) {
				event.setPosY(event.getPosY() + 1000);
			}
		}
	}
	
	@SubscribeEvent
	public void chatEventPost(RenderGameOverlayEvent.Post event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.CHAT)
			return;
		
	}
	
	public void clipToSize(int xPosition, int yPosition, int width, int height, ScaledResolution scaling) {
		int scaleFactor = scaling.getScaleFactor();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(xPosition * scaleFactor, (scaling.getScaledHeight() - (yPosition + height)) * scaleFactor, width * scaleFactor, height * scaleFactor);
	}
	
	protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
		float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double)right, (double)top, (double)100).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)top, (double)100).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)bottom, (double)100).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos((double)right, (double)bottom, (double)100).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();

        // Forge fix. Forge fixed that on 1.11.. But is still buggy in 1.10.
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
	
	public void drawHoveringText(List textList, int mouseX, int mouseY, int width, int height, FontRenderer font, boolean titled) {
        if(!textList.isEmpty()) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;

			for (Object aTextList : textList) {
				String s = (String) aTextList;
				int l = font.getStringWidth(s);

				if (l > k)
					k = l;
			}

            int i1 = mouseX + 12;
            int j1 = mouseY - 12;
            int k1 = 8;

            if (textList.size() > 1)
                k1 += 2 + (textList.size() - 1) * 10;
            
            if (i1 + k > width)
                i1 -= 28 + k;

            if (j1 + k1 + 6 > height)
                j1 = height - k1 - 6;

            k += 50;

            int l1 = -267386864;
            this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
            this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
            int i2 = 1347420415;
            int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
            this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
            this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

            for(int k2 = 0; k2 < textList.size(); ++k2) {
                String s1 = (String)textList.get(k2);
                font.drawStringWithShadow(s1, i1 + 25, j1, -1);
                //font.drawStringWithShadow(s1, i1, j1, -1);

                if(k2 == 0 && titled)
                    j1 += 2;

                j1 += 10;
            }

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)(x), (double)(y + height), (double)100).tex((double)((float)(textureX) * f), (double)((float)(textureY + height) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + height), (double)100).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y), (double)100).tex((double)((float)(textureX + width) * f), (double)((float)(textureY) * f1)).endVertex();
        worldrenderer.pos((double)(x), (double)(y), (double)100).tex((double)((float)(textureX) * f), (double)((float)(textureY) * f1)).endVertex();
        tessellator.draw();
    }
	
	public static void drawRect(int left, int top, int right, int bottom, int color) {
		 if (left < right)
	        {
	            int i = left;
	            left = right;
	            right = i;
	        }

	        if (top < bottom)
	        {
	            int j = top;
	            top = bottom;
	            bottom = j;
	        }

	        float f3 = (float)(color >> 24 & 255) / 255.0F;
	        float f = (float)(color >> 16 & 255) / 255.0F;
	        float f1 = (float)(color >> 8 & 255) / 255.0F;
	        float f2 = (float)(color & 255) / 255.0F;
	        Tessellator tessellator = Tessellator.getInstance();
	        VertexBuffer worldrenderer = tessellator.getBuffer();
	        GlStateManager.enableBlend();
	        GlStateManager.disableTexture2D();
	        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	        GlStateManager.color(f, f1, f2, f3);
	        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
	        worldrenderer.pos((double)left, (double)bottom, 0.0D).endVertex();
	        worldrenderer.pos((double)right, (double)bottom, 0.0D).endVertex();
	        worldrenderer.pos((double)right, (double)top, 0.0D).endVertex();
	        worldrenderer.pos((double)left, (double)top, 0.0D).endVertex();
	        tessellator.draw();
	        GlStateManager.enableTexture2D();
	        GlStateManager.disableBlend();
	}
}
