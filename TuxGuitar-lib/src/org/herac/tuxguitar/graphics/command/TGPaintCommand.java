package org.herac.tuxguitar.graphics.command;

import org.herac.tuxguitar.ui.resource.UIPainter;

public interface TGPaintCommand {
	
	void paint(UIPainter painter, float x, float y, float scale);
	
	float getMaximumX();
	
	float getMaximumY();
	
	float getMinimumX();
	
	float getMinimumY();
}
