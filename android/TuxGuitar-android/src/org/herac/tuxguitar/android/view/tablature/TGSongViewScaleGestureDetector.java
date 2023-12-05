package org.herac.tuxguitar.android.view.tablature;

import org.herac.tuxguitar.android.action.impl.layout.TGSetLayoutScaleAction;
import org.herac.tuxguitar.android.application.TGApplicationUtil;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class TGSongViewScaleGestureDetector implements ScaleGestureDetector.OnScaleGestureListener {
	
	private float scaleFactor;
	private ScaleGestureDetector gestureDetector;
	private TGSongView songView;
	
	public TGSongViewScaleGestureDetector(Context context, TGSongView songView) {
		this.gestureDetector = new ScaleGestureDetector(context, this);
		this.songView = songView;
	}
	
	public boolean processTouchEvent(MotionEvent event) {
		if( this.songView.getController().isScaleActionAvailable() ) {
			return this.gestureDetector.onTouchEvent(event);
		}
		return false;
	}
	
	public boolean isInProgress() {
		if( this.songView.getController().isScaleActionAvailable() ) {
			return this.gestureDetector.isInProgress();
		}
		return false;
	}
	
	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		this.scaleFactor = this.songView.getController().getLayout().getScale();
		return true;
	}
	
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
        this.scaleFactor = Math.max(this.songView.getMinimumScale(), Math.min(this.scaleFactor * detector.getScaleFactor(), this.songView.getMaximumScale()));
        this.previewScale();
		return true;
	}
	
	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		this.applyScale();
	}
	
	public void previewScale() {
		this.songView.getController().setScalePreview(this.scaleFactor);
	}
	
	public void applyScale() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(TGApplicationUtil.findContext(this.songView), TGSetLayoutScaleAction.NAME);
		tgActionProcessor.setAttribute(TGSetLayoutScaleAction.ATTRIBUTE_SCALE, this.scaleFactor);
		tgActionProcessor.processOnNewThread();
	}
}
