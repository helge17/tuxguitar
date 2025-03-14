package app.tuxguitar.graphics.control.print;

import app.tuxguitar.ui.resource.UIInset;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UISize;

public interface TGPrintDocument{

	void start();

	void finish();

	void pageStart();

	void pageFinish();

	boolean isPaintable(int page);

	boolean isTransparentBackground();

	UIPainter getPainter();

	UISize getSize();

	UIInset getMargins();
}
