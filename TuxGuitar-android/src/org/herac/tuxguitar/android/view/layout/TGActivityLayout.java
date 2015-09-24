package org.herac.tuxguitar.android.view.layout;

import org.herac.tuxguitar.android.activity.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class TGActivityLayout extends ViewGroup {
	
	private int bottomSpacing;
	
	public TGActivityLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		this.layoutBottomView(l, t, r, b);
		this.layoutMainView(l, t, r, b - this.bottomSpacing);
	}
	
	public void layoutBottomView(int l, int t, int r, int b) {
		View childEditor = findViewById(R.id.tg_activity_layout_bottom);
		if( childEditor != null && childEditor.getVisibility() != GONE ) {
			childEditor.measure(MeasureSpec.makeMeasureSpec((r - l), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec((b - t), MeasureSpec.AT_MOST) );
			
			this.bottomSpacing = childEditor.getMeasuredHeight();
			
			childEditor.layout(l, b - this.bottomSpacing, r, b);
		}
	}
	
	public void layoutMainView(int l, int t, int r, int b) {
		View childView = findViewById(R.id.tg_activity_layout_main);
		if( childView.getVisibility() != GONE ) {
			childView.measure(MeasureSpec.makeMeasureSpec((r - l), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec((b - t), MeasureSpec.AT_MOST) );
			childView.layout(l, t, r, b);
		}
	}
}
