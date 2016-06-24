package org.herac.tuxguitar.graphics.control;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGResourceFactory;

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
	
	public TGPainter createBuffer(TGResourceBuffer resourceBuffer, TGResourceFactory resourceFactory, float width, float height, TGColor background){
		TGImage buffer = resourceFactory.createImage(width, height);
		this.width = buffer.getWidth();
		this.height = buffer.getHeight();
		
		TGPainter bufferedPainter = buffer.createPainter();
		bufferedPainter.setBackground(background);
		bufferedPainter.initPath(TGPainter.PATH_FILL);
		bufferedPainter.addRectangle(0, 0, this.width, this.height);
		bufferedPainter.closePath();
		
		resourceBuffer.setResource(this.getRegistryKey(), buffer);
		
		return bufferedPainter;
	}
	
	public void paintBuffer(TGResourceBuffer resourceBuffer, TGPainter painter,float x,float y /*,float srcY*/){
		TGImage buffer = resourceBuffer.getResource(this.getRegistryKey());
//		painter.drawImage(buffer, 0, srcY, this.width, (this.height - srcY), x, (y + srcY), this.width, (this.height - srcY));
		painter.drawImage(buffer, x, y);
	}
	
	public boolean isDisposed(TGResourceBuffer resourceBuffer){
		return resourceBuffer.isResourceDisposed(this.getRegistryKey());
	}
}
