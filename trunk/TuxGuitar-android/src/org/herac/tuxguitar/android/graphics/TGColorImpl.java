package org.herac.tuxguitar.android.graphics;

import android.graphics.Color;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;

public class TGColorImpl implements UIColor {
	
	private UIColorModel model;
	
	public TGColorImpl(UIColorModel model){
		this.model = model;
	}
	
	public void dispose() {
		this.model = null;
	}
	
	public boolean isDisposed() {
		return this.model == null;
	}
	
	public int getBlue() {
		return model.getBlue();
	}
	
	public int getGreen() {
		return model.getGreen();
	}
	
	public int getRed() {
		return this.model.getRed();
	}
	
	public int getHandle(int alpha){
		return Color.argb(alpha, this.model.getRed(), this.model.getGreen(), this.model.getBlue());
	}
}
