package org.herac.tuxguitar.graphics.command;

import org.herac.tuxguitar.ui.resource.UIPainter;

public class TGMoveTo implements TGPaintCommand {
	
	private float x;
	private float y;
	
	public TGMoveTo(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void paint(UIPainter painter, float x, float y, float scale) {
		painter.moveTo((x + (this.x * scale)), (y + (this.y * scale)));
	}
	
	public float getMaximumX() {
		return this.x;
	}
	
	public float getMaximumY() {
		return this.y;
	}
	
	public float getMinimumX() {
		return this.x;
	}
	
	public float getMinimumY() {
		return this.y;
	}
}
