package org.herac.tuxguitar.graphics.command;

import org.herac.tuxguitar.graphics.TGPainter;

public class TGLineTo implements TGPaintCommand {
	
	private float x;
	private float y;
	
	public TGLineTo(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void paint(TGPainter painter, float x, float y, float scale) {
		painter.lineTo((x + (this.x * scale)), (y + (this.y * scale)));
	}
}
