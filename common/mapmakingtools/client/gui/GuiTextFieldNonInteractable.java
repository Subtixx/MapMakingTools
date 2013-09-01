package mapmakingtools.client.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

/**
 * @author ProPercivalalb
 */
public class GuiTextFieldNonInteractable extends GuiTextField {

	public boolean missMouseClick;
	
	public GuiTextFieldNonInteractable(FontRenderer fontRenderer, int xPos, int yPos, int width, int height) {
		super(fontRenderer, xPos, yPos, width, height);
	}

	@Override
	public void setCursorPosition(int index) {
		super.setCursorPosition(this.getMaxStringLength());
	}
	
	@Override
    public void mouseClicked(int par1, int par2, int par3) {
		if(!missMouseClick) {
			super.mouseClicked(par1, par2, par3);
		}
		else {
			missMouseClick = false;
		}
	}
}
