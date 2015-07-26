package org.herac.tuxguitar.app.graphics;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;

public class TGImageImpl implements TGImage {
	
	private Image handle;
	
	public TGImageImpl( Image handle ){
		this.handle = handle;
	}
	
	public TGImageImpl( Device device, float width, float height ){
		this( new Image(device, Math.round(width), Math.round(height)) );
	}
	
	public TGImageImpl(Device device, ImageData source, ImageData mask){
		this( new Image(device,source,mask) );
	}
	
	public TGPainter createPainter() {
		return new TGPainterImpl(this.handle);
	}
	
	public float getWidth() {
		return this.handle.getBounds().width;
	}
	
	public float getHeight() {
		return this.handle.getBounds().height;
	}
	
	public Image getHandle(){
		return this.handle;
	}
	
	public boolean isDisposed(){
		return this.handle.isDisposed();
	}
	
	public void dispose(){
		this.handle.dispose();
	}
	
	public void applyTransparency( TGColor background ){
		RGB alpha = new RGB( background.getRed(), background.getGreen(), background.getBlue() );
		RGB none = new RGB((0xff ^ alpha.red),(0xff ^ alpha.green),(0xff ^ alpha.blue));
		
		Image srcImage = this.handle;
		ImageData srcData = srcImage.getImageData();
		ImageData maskData = new ImageData(srcData.width,srcData.height,1,new PaletteData(new RGB[]{ none,alpha }  ));
		for(int x = 0; x< maskData.width; x++) {
			for(int y = 0; y < maskData.height; y++) {
				RGB rgb = srcData.palette.getRGB(srcData.getPixel(x, y));
				if(rgb.red == alpha.red && rgb.green == alpha.green && rgb.blue == alpha.blue){
					maskData.setPixel(x, y, maskData.palette.getPixel(none));
				}else{
					maskData.setPixel(x, y, maskData.palette.getPixel(alpha));
				}
			}
		}
		this.handle = new Image(srcImage.getDevice(),srcData,maskData);
		
		srcImage.dispose();
	}
}
