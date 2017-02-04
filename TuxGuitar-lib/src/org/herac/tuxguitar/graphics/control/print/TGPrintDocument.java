package org.herac.tuxguitar.graphics.control.print;

import org.herac.tuxguitar.graphics.TGDimension;
import org.herac.tuxguitar.graphics.TGMargins;
import org.herac.tuxguitar.graphics.TGPainter;

public interface TGPrintDocument{
	
	void start();
	
	void finish();
	
	void pageStart();
	
	void pageFinish();
	
	boolean isPaintable(int page);
	
	boolean isTransparentBackground();
	
	TGPainter getPainter();
	
	TGDimension getSize();
	
	TGMargins getMargins();
}
