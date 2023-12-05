package org.herac.tuxguitar.graphics.control.print;

import org.herac.tuxguitar.ui.resource.UIInset;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UISize;

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
