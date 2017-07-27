package org.herac.tuxguitar.ui.resource;

public class UIRectangle {
	
	private UIPosition position;
	private UISize size;
	
	public UIRectangle() {
		this(new UIPosition(), new UISize());
	}
	
	public UIRectangle(UISize size) {
		this(new UIPosition(), size);
	}
	
	public UIRectangle(float x, float y, float width, float height) {
		this(new UIPosition(x, y), new UISize(width, height));
	}
	
	public UIRectangle(UIPosition position, UISize size) {
		this.position = position;
		this.size = size;
	}

	public UIPosition getPosition() {
		return position;
	}

	public void setPosition(UIPosition position) {
		this.position = position;
	}

	public UISize getSize() {
		return size;
	}

	public void setSize(UISize size) {
		this.size = size;
	}
	
	public float getX() {
		return (this.position != null ? this.position.getX() : 0);
	}

	public float getY() {
		return (this.position != null ? this.position.getY() : 0);
	}
	
	public float getWidth() {
		return (this.size != null ? this.size.getWidth() : 0);
	}
	
	public float getHeight() {
		return (this.size != null ? this.size.getHeight() : 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof UIRectangle ) {
			UIRectangle uiRectangle = (UIRectangle) obj;
			
			return (this.getPosition().equals(uiRectangle.getPosition()) && this.getSize().equals(uiRectangle.getSize()));
		}
		return super.equals(obj);
	}
}
