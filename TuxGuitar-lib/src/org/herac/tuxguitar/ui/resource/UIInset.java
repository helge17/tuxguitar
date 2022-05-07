package org.herac.tuxguitar.ui.resource;

public class UIInset {
	
	private float top;
	private float left;
	private float right;
	private float bottom;
	
	public UIInset(float top, float left, float right, float bottom) {
		this.top = top;
		this.left = left;
		this.right = right;
		this.bottom = bottom;
	}
	
	public UIInset() {
		this(0, 0, 0, 0);
	}

	public float getTop() {
		return top;
	}

	public void setTop(float top) {
		this.top = top;
	}

	public float getLeft() {
		return left;
	}

	public void setLeft(float left) {
		this.left = left;
	}

	public float getRight() {
		return right;
	}

	public void setRight(float right) {
		this.right = right;
	}

	public float getBottom() {
		return bottom;
	}

	public void setBottom(float bottom) {
		this.bottom = bottom;
	}
	
	public void copyFrom(UIInset inset) {
		this.setTop(inset.getTop());
		this.setLeft(inset.getLeft());
		this.setRight(inset.getRight());
		this.setBottom(inset.getBottom());
	}
	
	public UIInset clone() {
		UIInset uiInset = new UIInset();
		uiInset.copyFrom(this);
		
		return uiInset;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof UIInset ) {
			UIInset uiInset = (UIInset) obj;
			
			return (this.getTop() == uiInset.getTop() && this.getLeft() == uiInset.getLeft() && this.getRight() == uiInset.getRight() && this.getBottom() == uiInset.getBottom());
		}
		return super.equals(obj);
	}
}
