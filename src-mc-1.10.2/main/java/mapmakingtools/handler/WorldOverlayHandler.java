package mapmakingtools.handler;

import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import mapmakingtools.ModItems;
import mapmakingtools.lib.Constants;
import mapmakingtools.tools.ClientData;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author ProPercivalalb
 */
public class WorldOverlayHandler {
	
	private static Minecraft mc = Minecraft.getMinecraft();
	private static boolean hasCheckedVersion = false;
	
	@SubscribeEvent
	public void onWorldRenderLast(RenderWorldLastEvent event) {
		if(!PlayerAccess.canEdit(mc.thePlayer) || !ClientData.playerData.hasSelectedPoints() || !(mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND) != null && mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND).getItem() == ModItems.editItem && mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND).getMetadata() == 0))
			return;
		GL11.glPushMatrix();
		PlayerData data = ClientData.playerData;
		
		int minX = data.getMinX();
		int minY = data.getMinY();
		int minZ = data.getMinZ();
		int maxX = data.getMaxX() + 1;
		int maxY = data.getMaxY() + 1;
		int maxZ = data.getMaxZ() + 1;
		 
		AxisAlignedBB boundingBox = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
		this.drawSelectionBox(mc.thePlayer, event.getPartialTicks(), boundingBox);
        GL11.glPopMatrix();
	}

	public void drawSelectionBox(EntityPlayer player, float particleTicks, AxisAlignedBB boundingBox) {
		GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_LIGHTING); //Make the line see thought blocks
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST); //Make the line see thought blocks
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.7F);
        //TODO Used when drawing outline of bounding box 
        GL11.glLineWidth(2.0F);
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    	double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)particleTicks;
    	double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)particleTicks;
    	double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)particleTicks;
    	
        if(Constants.RENDER_ALL_BLOCKS) {
        	int minX = (int)boundingBox.minX;
        	int minY = (int)boundingBox.minY;
        	int minZ = (int)boundingBox.minZ;
        	int maxX = (int)boundingBox.maxX;
        	int maxY = (int)boundingBox.maxY;
   		 	int maxZ = (int)boundingBox.maxZ;
   		 	for(int x = minX; x < maxX; ++x) {
   		 		for(int y = minY; y < maxY; ++y) {
   		 			for(int z = minZ; z < maxZ; ++z) {
   		 				AxisAlignedBB smallBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
   		 				//RenderGlobal.drawOutlinedBoundingBox(smallBox.offset(-d0, -d1, -d2), 255, 255, 255, 255);
						RenderGlobal.func_189696_b(smallBox.offset(-d0, -d1, -d2), 255, 255, 255, 255);
   		 			} 
   		 		}
   		 	}
        } 
        else {
        	//RenderGlobal.drawOutlinedBoundingBox(boundingBox.offset(-d0, -d1, -d2), 255, 255, 255, 255);
        	RenderGlobal.func_189696_b(boundingBox.offset(-d0, -d1, -d2), 255, 255, 255, 255);
        	//if(Constants.RENDER_SELECTED_POSITION) {
	        //	PlayerData data = ClientData.playerData;
	        //	GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.1F);
	        	//this.drawBoundingBox(new AxisAlignedBB(data.getFirstPoint().getX(), data.getFirstPoint().getY(), data.getFirstPoint().getZ(), data.getFirstPoint().getX() + 1, data.getFirstPoint().getY() + 1, data.getFirstPoint().getZ() + 1).offset(-d0, -d1, -d2));
	       // 	//this.drawBoundingBox(new AxisAlignedBB(data.getSecondPoint().getX(), data.getSecondPoint().getY(), data.getSecondPoint().getZ(), data.getSecondPoint().getX() + 1, data.getSecondPoint().getY() + 1, data.getSecondPoint().getZ() + 1).offset(-d0, -d1, -d2));
        	//}
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST); //Make the line see thought blocks
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING); //Make the line see thought blocks
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }
	
	/**
	public void drawBoundingBox(AxisAlignedBB boundingBox) {
		Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.startDrawingQuads();
        worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
	    tessellator.draw();
	    worldrenderer.startDrawingQuads();
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
	    tessellator.draw();
	    worldrenderer.startDrawingQuads();
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
	    tessellator.draw();
	    worldrenderer.startDrawingQuads();
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
	    tessellator.draw();
	    worldrenderer.startDrawingQuads();
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
	    tessellator.draw();
	    worldrenderer.startDrawingQuads();
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
	    worldrenderer.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
	    tessellator.draw();
	}**/
}
