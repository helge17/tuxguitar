package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIFontModel;

public class SVGFont implements UIFont {
	
	private UIFontModel handle;
	
	public SVGFont(String name, float height, boolean bold, boolean italic){
		this.handle = new UIFontModel(name, height, bold, italic);
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
