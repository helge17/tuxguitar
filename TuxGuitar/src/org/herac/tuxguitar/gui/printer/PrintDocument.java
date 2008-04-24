package org.herac.tuxguitar.gui.printer;

import org.eclipse.swt.graphics.Rectangle;
import org.herac.tuxguitar.gui.editors.TGPainter;

public interface PrintDocument{
	
	public void start();
	
	public void finish();
	
	public void pageStart();
	
	public void pageFinish();
	
	public boolean isPaintable(int page);
	
	public TGPainter getPainter();
	
	public Rectangle getBounds();
	
}
