package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGColorModel;

public class SVGColor implements TGColor{
	
	private TGColorModel handle;
	
	public SVGColor(int red, int green, int blue){
		this.handle = new TGColorModel(red, green, blue);
	}
	
	public void dispose() {
		this.handle = null;
	}
	
	public boolean isDisposed() {
		return (this.handle == null);
	}
	
	public TGColorModel getHandle(){
		return this.handle;
	}
	
	public int getRed() {
		return this.handle.getRed();
	}
	
	public int getGreen() {
		return this.handle.getGreen();
	}
	
	public int getBlue() {
		return this.handle.getBlue();
	}
	
	public String toHexString(){
		return ("#" + getHexValue(getRed()) + getHexValue(getGreen()) + getHexValue(getBlue()));
	}
	
	private String getHexValue( int value ){
		String hexValue =  Integer.toHexString( value );
		while( hexValue.length() < 2 ){
			hexValue = ("0" + hexValue);
		}
		return hexValue;
	}
}
