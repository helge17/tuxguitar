package org.herac.tuxguitar.ui.resource;

public class UIFontAlignment {
	
	private float top;
	private float middle;
	private float bottom;
	
	public UIFontAlignment(float top, float middle, float bottom){
		this.top = top;
		this.middle = middle;
		this.bottom = bottom;
	}

	public UIFontAlignment(){
		this(0f, 0f, 0f);
	}
	
	public float getTop() {
		return top;
	}

	public void setTop(float top) {
		this.top = top;
	}

	public float getMiddle() {
		return middle;
	}

	public void setMiddle(float middle) {
		this.middle = middle;
	}

	public float getBottom() {
		return bottom;
	}

	public void setBottom(float bottom) {
		this.bottom = bottom;
	}
}
