package org.herac.tuxguitar.graphics.command;

import org.herac.tuxguitar.graphics.TGPainter;

public interface TGPaintCommand {
	
	void paint(TGPainter painter, float x, float y, float scale);
}
