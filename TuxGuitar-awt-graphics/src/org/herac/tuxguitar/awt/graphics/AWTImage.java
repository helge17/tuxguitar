package org.herac.tuxguitar.awt.graphics;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

public class AWTImage implements UIImage {
	
	private Image handle;
	
	public AWTImage( Image handle ){
		this.handle = handle;
	}
	
	public AWTImage(float width , float height ){
		this(new BufferedImage(Math.round(width), Math.round(height), BufferedImage.TYPE_INT_RGB));
	}
	
	public AWTImage(InputStream in) throws IOException{
		this(ImageIO.read(in));
	}
	
	public UIPainter createPainter() {
		return new AWTPainter(this.handle);
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
	
	public void applyTransparency( final UIColor background ){
		ImageFilter filter = new RGBImageFilter() {
			public int markerRGB = (((AWTColor)background).getHandle().getRGB() | 0xFF000000);
			
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
