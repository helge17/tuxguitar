package org.herac.tuxguitar.app.printer;

import org.herac.tuxguitar.graphics.TGDimension;
import org.herac.tuxguitar.graphics.TGMargins;
import org.herac.tuxguitar.graphics.TGPainter;

public interface PrintDocument{
	
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
