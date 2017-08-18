package org.herac.tuxguitar.ui.jfx.resource;

import javafx.scene.image.Image;

public class JFXImageHandle {
	
	private float width;
	private float height;
	private Image handle;
	
	public JFXImageHandle(float width, float height) {
		this.width = width;
		this.height = height;
	}
	
	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public Image getHandle() {
		return handle;
	}

	public void setHandle(Image handle) {
		this.handle = handle;
	}
}
