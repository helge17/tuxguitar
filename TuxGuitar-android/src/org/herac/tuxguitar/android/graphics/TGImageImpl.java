package org.herac.tuxguitar.android.graphics;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class TGImageImpl implements TGImage{
	
	private Bitmap handle;
	
	public TGImageImpl(float width, float height){
		this.handle = Bitmap.createBitmap(Math.round(width), Math.round(height), Bitmap.Config.ARGB_8888);
	}
	
	public void dispose() {
		if(!this.isDisposed()) {
			this.handle.recycle();
			this.handle = null;
		}
	}
	
	public boolean isDisposed() {
		return (this.handle == null || this.handle.isRecycled());
	}
	
	public Bitmap getHandle(){
		return this.handle;
	}
	
	public float getWidth() {
		return this.handle.getWidth();
	}
	
	public float getHeight() {
		return this.handle.getHeight();
	}
	
	public TGPainter createPainter() {
		return new TGPainterImpl(new Canvas(this.handle));
	}
	
	public void applyTransparency(TGColor background) {
		// not implemented
	}
}
