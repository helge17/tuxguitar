package org.herac.tuxguitar.android.view.dialog.tremoloBar;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class TGTremoloBarEditorGestureDetector extends GestureDetector.SimpleOnGestureListener {

	private GestureDetectorCompat gestureDetector;
	private TGTremoloBarEditor tremoloBarEditor;
	
	public TGTremoloBarEditorGestureDetector(Context context, TGTremoloBarEditor tremoloBarEditor) {
		this.gestureDetector = new GestureDetectorCompat(context, this);
		this.tremoloBarEditor = tremoloBarEditor;
	}
	
	public boolean processTouchEvent(MotionEvent event) {
		return this.gestureDetector.onTouchEvent(event);
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		this.tremoloBarEditor.editPoint(Math.round(e.getX()), Math.round(e.getY()));
		
		return true;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}
}
