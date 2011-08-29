package org.herac.tuxguitar.app.editors;

import java.awt.Font;

import org.herac.tuxguitar.graphics.TGFont;

public class TGFontImpl implements TGFont {
	
	private Font handle;
	
	public TGFontImpl( Font handle ){
		this.handle = handle;
	}
	
	public TGFontImpl(String name, int height, boolean bold, boolean italic){
		this( new Font(name, ( (bold ? Font.BOLD : 0) | (italic ? Font.ITALIC : 0)) , height) );
	}
	
	public Font getHandle(){
		return this.handle;
	}
	
	public String getName() {
		return this.handle.getName();
	}
	
	public int getHeight() {
		return this.handle.getSize();
	}
	
	public boolean isBold() {
		return this.handle.isBold();
	}
	
	public boolean isItalic() {
		return this.handle.isItalic();
	}
	
	public boolean isDisposed(){
		return (this.handle == null); 
	}
	
	public void dispose(){
		this.handle = null;
	}
	
}
