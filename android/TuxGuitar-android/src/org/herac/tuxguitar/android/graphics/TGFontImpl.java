package org.herac.tuxguitar.android.graphics;

import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIFontModel;

public class TGFontImpl implements UIFont {

	private UIFontModel model;
	
	public TGFontImpl(UIFontModel model){
		this.model = model;
	}
	
	public void dispose() {
		this.model = null;
	}
	
	public boolean isDisposed() {
		return this.model == null;
	}
	
	public String getName() {
		return this.model.getName();
	}
	
	public float getHeight() {
		return this.model.getHeight();
	}
	
	public boolean isBold() {
		return this.model.isBold();
	}
	
	public boolean isItalic() {
		return this.model.isItalic();
	}
}
