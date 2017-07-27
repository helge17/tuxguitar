package org.herac.tuxguitar.ui.resource;

public class UIPosition {
	
	private float x;
	private float y;
	
	public UIPosition() {
		this(0, 0);
	}
	
	public UIPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof UIPosition ) {
			UIPosition uiPosition = (UIPosition) obj;
			
			return (this.getX() == uiPosition.getX() && this.getY() == uiPosition.getY());
		}
		return super.equals(obj);
	}
}
