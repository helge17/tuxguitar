package org.herac.tuxguitar.ui.jfx.resource;

import javafx.scene.image.Image;

import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.resource.UIImage;

public abstract class JFXAbstractImage<T> extends JFXComponent<T> implements UIImage {

	public JFXAbstractImage(T component) {
		super(component);
	}
	
	public abstract void paint(JFXAbstractPainter<?> painter, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight);
	
	public abstract void paint(JFXAbstractPainter<?> painter, float x, float y);
	
	public abstract JFXAbstractImage<T> clone();
	
	public abstract Image getHandle();
}
