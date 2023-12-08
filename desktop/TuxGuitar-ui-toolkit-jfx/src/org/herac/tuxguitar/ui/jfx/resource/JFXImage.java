package org.herac.tuxguitar.ui.jfx.resource;

import java.io.InputStream;

import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

import javafx.scene.image.Image;

public class JFXImage extends JFXComponent<JFXImageHandle> implements UIImage {
	
	public JFXImage(JFXImageHandle component){
		super(component);
	}
	
	public JFXImage(float width, float height){
		this(new JFXImageHandle(width, height));
	}
	
	public JFXImage(Image handle){
		this((float)handle.getWidth(), (float)handle.getHeight());
		
		this.getControl().setHandle(handle);
	}
	
	public JFXImage(InputStream inputStream){
		this(new Image(inputStream));
	}
	
	public float getWidth() {
		return this.getControl().getWidth();
	}
	
	public float getHeight() {
		return this.getControl().getHeight();
	}
	
	public Image getHandle() {
		if( this.getControl().getHandle() == null ) {
			this.createPainter().dispose();
		}
		return this.getControl().getHandle();
	}
	
	public UIPainter createPainter() {
		return new JFXImagePainter(this.getControl());
	}
}
