package org.herac.tuxguitar.awt.graphics;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;

public class TGImageImpl implements TGImage {
	
	private Image handle;
	
	public TGImageImpl( Image handle ){
		this.handle = handle;
	}
	
	public TGImageImpl(float width , float height ){
		this(new BufferedImage(Math.round(width), Math.round(height), BufferedImage.TYPE_INT_RGB));
	}
	
	public TGPainter createPainter() {
		return new TGPainterImpl(this.handle);
	}
	
	public float getWidth() {
		return this.handle.getWidth(null);
	}
	
	public float getHeight() {
		return this.handle.getHeight(null);
	}
	
	public Image getHandle(){
		return this.handle;
	}
	
	public boolean isDisposed(){
		return (this.handle == null); 
	}
	
	public void dispose(){
		this.handle = null;
	}
	
	public void applyTransparency( final TGColor background ){
		ImageFilter filter = new RGBImageFilter() {
			public int markerRGB = (((TGColorImpl)background).getHandle().getRGB() | 0xFF000000);
			
			public final int filterRGB(int x, int y, int rgb) {
				if ( ( rgb | 0xFF000000 ) == markerRGB ) {
					return 0x00FFFFFF & rgb;
				}
				return rgb;
			}
		};
		this.handle = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(this.handle.getSource(), filter));
	}
	
}
