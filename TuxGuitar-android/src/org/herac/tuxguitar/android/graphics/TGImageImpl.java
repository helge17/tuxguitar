package org.herac.tuxguitar.android.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

import java.io.InputStream;

public class TGImageImpl implements UIImage {
	
	private Bitmap handle;
	
	public TGImageImpl(float width, float height){
		this.handle = Bitmap.createBitmap(Math.round(width), Math.round(height), Bitmap.Config.ARGB_8888);
	}

	public TGImageImpl(InputStream stream){
		this.handle = BitmapFactory.decodeStream(stream);
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
	
	public UIPainter createPainter() {
		return new TGPainterImpl(new Canvas(this.handle));
	}

}
