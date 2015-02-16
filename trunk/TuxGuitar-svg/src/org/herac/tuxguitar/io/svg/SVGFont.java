package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGFontModel;

public class SVGFont implements TGFont {
	
	private TGFontModel handle;
	
	public SVGFont(String name, float height, boolean bold, boolean italic){
		this.handle = new TGFontModel(name, height, bold, italic);
	}
	
	public void dispose() {
		this.handle = null;
	}
	
	public boolean isDisposed() {
		return (this.handle == null);
	}
	
	public String getName() {
		return this.handle.getName();
	}
	
	public float getHeight() {
		return this.handle.getHeight();
	}
	
	public boolean isBold() {
		return this.handle.isBold();
	}
	
	public boolean isItalic() {
		return this.handle.isItalic();
	}
}
