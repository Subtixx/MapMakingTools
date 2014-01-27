package mapmakingtools.tools;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ProPercivalalb
 */
public class SelectedPoint {

	private int x;
	private int y;
	private int z;
	
	public SelectedPoint(int defaultValue) { this.setPoint(defaultValue, defaultValue, defaultValue); }
	public SelectedPoint(int x, int y, int z) { this.setPoint(x, y, z); }
	
	/**
	 * Sets the position to the given coordinates
	 * @param x The new x coordinate
	 * @param y The new y coordinate
	 * @param z The new z coordinate
	 * @return The current object
	 */
	public SelectedPoint setPoint(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getZ() {
		return this.z;
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("x", this.x);
		tag.setInteger("y", this.y);
		tag.setInteger("z", this.z);
		return tag;
	}
	
	public SelectedPoint readFromNBT(NBTTagCompound tag) {
		this.x = tag.getInteger("x");
		this.y = tag.getInteger("y");
		this.z = tag.getInteger("z");
		return this;
	}
}