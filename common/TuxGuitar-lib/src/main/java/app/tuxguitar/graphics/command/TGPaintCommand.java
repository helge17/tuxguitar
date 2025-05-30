package app.tuxguitar.graphics.command;

import app.tuxguitar.ui.resource.UIPainter;

public interface TGPaintCommand {

	void paint(UIPainter painter, float x, float y, float scale);

	float getMaximumX();

	float getMaximumY();

	float getMinimumX();

	float getMinimumY();
}
