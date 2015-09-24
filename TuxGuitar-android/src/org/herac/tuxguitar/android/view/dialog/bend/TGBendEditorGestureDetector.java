package org.herac.tuxguitar.android.view.dialog.bend;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class TGBendEditorGestureDetector extends GestureDetector.SimpleOnGestureListener {

	private GestureDetectorCompat gestureDetector;
	private TGBendEditor bendEditor;
	
	public TGBendEditorGestureDetector(Context context, TGBendEditor bendEditor) {
		this.gestureDetector = new GestureDetectorCompat(context, this);
		this.bendEditor = bendEditor;
	}
	
	public boolean processTouchEvent(MotionEvent event) {
		return this.gestureDetector.onTouchEvent(event);
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		this.bendEditor.editPoint(Math.round(e.getX()), Math.round(e.getY()));
		
		return true;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}
}
