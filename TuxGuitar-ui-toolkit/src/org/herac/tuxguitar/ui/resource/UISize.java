package org.herac.tuxguitar.ui.resource;

public class UISize {
	
	private float width;
	private float height;
	
	public UISize() {
		this(0, 0);
	}
	
	public UISize(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof UISize ) {
			UISize uiSize = (UISize) obj;
			
			return (this.getWidth() == uiSize.getWidth() && this.getHeight() == uiSize.getHeight());
		}
		return super.equals(obj);
	}
}
