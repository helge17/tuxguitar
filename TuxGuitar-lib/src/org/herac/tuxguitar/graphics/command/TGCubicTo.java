package org.herac.tuxguitar.graphics.command;

import org.herac.tuxguitar.ui.resource.UIPainter;

public class TGCubicTo implements TGPaintCommand {
	
	private float xc1;
	private float yc1;
	private float xc2;
	private float yc2;
	private float x1;
	private float y1;
	
	public TGCubicTo(float xc1, float yc1, float xc2, float yc2, float x1, float y1) {
		this.xc1 = xc1;
		this.yc1 = yc1;
		this.xc2 = xc2;
		this.yc2 = yc2;
		this.x1 = x1;
		this.y1 = y1;
	}

	public void paint(UIPainter painter, float x, float y, float scale) {
		painter.cubicTo((x + (this.xc1 * scale)), (y + (this.yc1 * scale)), (x + (this.xc2 * scale)), (y + (this.yc2 * scale)), (x + (this.x1 * scale)), (y + (this.y1 * scale)));
	}
	
	public float getMaximumX() {
		return Math.max(this.x1, Math.max(this.xc1, this.xc2));
	}
	
	public float getMaximumY() {
		return Math.max(this.y1, Math.max(this.yc1, this.yc2));
	}
	
	public float getMinimumX() {
		return Math.min(this.x1, Math.min(this.xc1, this.xc2));
	}
	
	public float getMinimumY() {
		return Math.min(this.y1, Math.min(this.yc1, this.yc2));
	}
}
