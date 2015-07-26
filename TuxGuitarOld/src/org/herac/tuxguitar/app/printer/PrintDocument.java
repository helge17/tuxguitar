package org.herac.tuxguitar.app.printer;

import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;

public interface PrintDocument{
	
	public void start();
	
	public void finish();
	
	public void pageStart();
	
	public void pageFinish();
	
	public boolean isPaintable(int page);
	
	public TGPainter getPainter();
	
	public TGRectangle getBounds();
	
}
