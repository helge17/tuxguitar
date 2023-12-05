package org.herac.tuxguitar.graphics.control;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;

public class TGMeasureBuffer {
	
	private float width;
	
	private float height;
	
	public TGMeasureBuffer(){
		super();
	}
	
	public Object getRegistryKey() {
		return this;
	}
	
	public void register(TGResourceBuffer resourceBuffer) {
		resourceBuffer.register(this.getRegistryKey());
	}
	
	public UIPainter createBuffer(TGResourceBuffer resourceBuffer, UIResourceFactory resourceFactory, float width, float height, UIColor background){
		UIImage buffer = resourceFactory.createImage(width, height);
		this.width = buffer.getWidth();
		this.height = buffer.getHeight();
		
		UIPainter bufferedPainter = buffer.createPainter();
		bufferedPainter.setBackground(background);
		bufferedPainter.initPath(UIPainter.PATH_FILL);
		bufferedPainter.addRectangle(0, 0, this.width, this.height);
		bufferedPainter.closePath();
		
		resourceBuffer.setResource(this.getRegistryKey(), buffer);
		
		return bufferedPainter;
	}
	
	public void paintBuffer(TGResourceBuffer resourceBuffer, UIPainter painter,float x,float y){
		UIImage buffer = resourceBuffer.getResource(this.getRegistryKey());
		painter.drawImage(buffer, x, y);
	}
	
	public boolean isDisposed(TGResourceBuffer resourceBuffer){
		return resourceBuffer.isResourceDisposed(this.getRegistryKey());
	}
}
