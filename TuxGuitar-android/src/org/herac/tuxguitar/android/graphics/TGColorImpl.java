package org.herac.tuxguitar.android.graphics;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGColorModel;

import android.graphics.Color;

public class TGColorImpl implements TGColor {
	
	private TGColorModel model;
	
	public TGColorImpl(TGColorModel model){
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
