package org.herac.tuxguitar.awt.graphics;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;

public class AWTFontMetrics {
	
	private Font font;
	private FontMetrics handle;
	
	public AWTFontMetrics(Font font) {
		this.font = font;
		this.handle = new Canvas().getFontMetrics(font);
	}
	
	public float getTopLine() {
		return -(this.handle.getDescent() + this.handle.getLeading());
	}
	
	public float getMiddleLine() {
		return (this.getTopLine() + ((this.getBaseLine() - this.getTopLine()) / 2f));
	}
	
	public float getBaseLine() {
		return -(this.font.getSize2D());
	}
	
	public float getHeight() {
		return this.handle.getHeight();
	}
	
	public float getWidth(String text) {
		return this.handle.stringWidth(text);
	}
}
