package mapmakingtools.handler;

import mapmakingtools.ModItems;
import mapmakingtools.tools.ClientData;
import mapmakingtools.tools.PlayerAccess;
import mapmakingtools.tools.PlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ProPercivalalb
 */
public class WorldOverlayHandler {
	
	private static Minecraft mc = Minecraft.getMinecraft();

    private double playerX;
    private double playerY;
    private double playerZ;
	
	@SubscribeEvent
	public void onWorldRenderLast(RenderWorldLastEvent event) {
		if(!PlayerAccess.canEdit(mc.thePlayer) || !ClientData.playerData.hasSelectedPoints() ||
                !(mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND) != null &&
                mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND).getItem() == ModItems.editItem &&
                mc.thePlayer.getHeldItem(EnumHand.MAIN_HAND).getMetadata() == 0))
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

	private void drawSelectionBox(EntityPlayer player, float particleTicks, AxisAlignedBB boundingBox) {
        playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)particleTicks;
        playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)particleTicks;
        playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)particleTicks;

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        if(!ConfigurationHandler.renderAllBlock)
            renderCuboid(boundingBox, new Color(255, 255, 255, 255), ConfigurationHandler.renderFilledBox && !ConfigurationHandler.renderSelectedPosition);
        else{
            // TODO: Make render all blocks!
        }

        if(ConfigurationHandler.renderSelectedPosition){
            renderRectangle(new AxisAlignedBB(ClientData.playerData.getFirstPoint(), ClientData.playerData.getFirstPoint().add(1, 1, 1)), ClientData.playerData.getFirstPoint().getY(), ClientData.playerData.getFirstPoint().getY()+1, new Color(0, 255, 0, 255), true);
            renderRectangle(new AxisAlignedBB(ClientData.playerData.getSecondPoint(), ClientData.playerData.getSecondPoint().add(1,1,1)), ClientData.playerData.getSecondPoint().getY(), ClientData.playerData.getSecondPoint().getY()+1, new Color(255, 0, 0, 255), true);
        }

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void renderRectangle(AxisAlignedBB aaBB, double minY, double maxY, Color color, Boolean fill) {
        aaBB = new AxisAlignedBB(aaBB.minX, minY, aaBB.minZ, aaBB.maxX, maxY, aaBB.maxZ);
        renderCuboid(aaBB, color, fill);
    }

    private void renderSphere(BlockPos center, double radius, Color color) {
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glPointSize(2f);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldRenderer = tessellator.getBuffer();
        worldRenderer.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
        for (OffsetPoint point : buildPoints(center, radius)) {
            worldRenderer.pos(point.getX(), point.getY(), point.getZ())
                    .color(color.getRed(), color.getGreen(), color.getBlue(), 255)
                    .endVertex();
        }
        tessellator.draw();
    }

    private AxisAlignedBB offsetAxisAlignedBB(AxisAlignedBB axisAlignedBB) {
        double expandXZ = 0.001F;
        double expandY = 0;
        if (axisAlignedBB.minY != axisAlignedBB.maxY) {
            expandY = expandXZ;
        }
        return axisAlignedBB
                .expand(expandXZ, expandY, expandXZ)
                .offset(-playerX, -playerY, -playerZ);
    }

    private void renderCuboid(AxisAlignedBB aaBB, Color color, boolean fill) {
        aaBB = offsetAxisAlignedBB(aaBB);
        if (fill) {
            renderFilledCuboid(aaBB, color);
        }
        renderUnfilledCuboid(aaBB, color);
    }

    private void renderFilledCuboid(AxisAlignedBB aaBB, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_BLEND);
        renderCuboid(aaBB, 30, color);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(-1.f, -1.f);
    }

    private void renderUnfilledCuboid(AxisAlignedBB aaBB, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        renderCuboid(aaBB, 255, color);
    }

    private void renderCuboid(AxisAlignedBB bb, int alphaChannel, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldRenderer = tessellator.getBuffer();

        int colorR = color.getRed();
        int colorG = color.getGreen();
        int colorB = color.getBlue();

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(bb.minX, bb.minY, bb.minZ)
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ)
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ)
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ)
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();

        if (bb.minY != bb.maxY) {

            worldRenderer.pos(bb.minX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(bb.minX, bb.minY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(bb.minX, bb.minY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.minY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(bb.minX, bb.minY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.minY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(bb.maxX, bb.minY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
        }
        tessellator.draw();
    }

    private class OffsetPoint {
        private final double x;
        private final double y;
        private final double z;

        public OffsetPoint(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public OffsetPoint(BlockPos blockPos) {
            x = blockPos.getX();
            y = blockPos.getY();
            z = blockPos.getZ();
        }

        public double getX() {
            return x - playerX;
        }

        public double getY() {
            return y - playerY;
        }

        public double getZ() {
            return z - playerZ;
        }

        public OffsetPoint add(double x, double y, double z) {
            return new OffsetPoint(this.x + x, this.y + y, this.z + z);
        }
    }

    private Set<OffsetPoint> buildPoints(BlockPos center, double radius) {
        Set<OffsetPoint> points = new HashSet<OffsetPoint>(1200);

        double tau = 6.283185307179586D;
        double pi = tau / 2D;
        double segment = tau / 48D;
        OffsetPoint centerPoint = new OffsetPoint(center);

        for (double t = 0.0D; t < tau; t += segment)
            for (double theta = 0.0D; theta < pi; theta += segment) {
                double dx = radius * Math.sin(t) * Math.cos(theta);
                double dz = radius * Math.sin(t) * Math.sin(theta);
                double dy = radius * Math.cos(t);

                points.add(centerPoint.add(dx, dy, dz));
            }
        return points;
    }
}
