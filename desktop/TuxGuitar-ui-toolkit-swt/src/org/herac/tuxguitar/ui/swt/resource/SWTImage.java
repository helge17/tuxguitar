package org.herac.tuxguitar.ui.swt.resource;

import java.io.InputStream;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.swt.SWTComponent;

public class SWTImage extends SWTComponent<Image> implements UIImage {
	
	public SWTImage( Image handle ){
		super(handle);
	}
	
	public SWTImage(Device device, float width, float height ){
		this(new Image(device, Math.round(width), Math.round(height)));
	}
	
	public SWTImage(Device device, InputStream inputStream){
		this(new Image(device, new ImageData(inputStream)));
	}
	
	public UIPainter createPainter() {
		return new SWTPainter(this.getControl());
	}
	
	public float getWidth() {
		return this.getControl().getBounds().width;
	}
	
	public float getHeight() {
		return this.getControl().getBounds().height;
	}
	
	public Image getHandle(){
		return this.getControl();
	}
	
	public boolean isDisposed(){
		return this.getControl().isDisposed();
	}
	
	public void dispose(){
		this.getControl().dispose();
	}
}
