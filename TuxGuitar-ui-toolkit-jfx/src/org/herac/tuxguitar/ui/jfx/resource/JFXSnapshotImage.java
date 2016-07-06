package org.herac.tuxguitar.ui.jfx.resource;

import java.io.InputStream;

import javafx.scene.image.Image;

import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

public class JFXSnapshotImage extends JFXAbstractImage<JFXSnapshotImageHandle> implements UIImage {
	
	public JFXSnapshotImage(JFXSnapshotImageHandle component){
		super(component);
	}
	
	public JFXSnapshotImage(float width, float height){
		this(new JFXSnapshotImageHandle(width, height));
	}
	
	public JFXSnapshotImage(Image handle){
		this((float)handle.getWidth(), (float)handle.getHeight());
		
		this.getControl().setHandle(handle);
	}
	
	public JFXSnapshotImage(InputStream inputStream){
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
	
	@Override
	public void paint(JFXAbstractPainter<?> painter, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		painter.drawNativeImage(this.getHandle(), srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight);
	}

	@Override
	public void paint(JFXAbstractPainter<?> painter, float x, float y) {
		painter.drawNativeImage(this.getHandle(), x, y);
	}
	
	public JFXSnapshotImage clone() {
		return new JFXSnapshotImage(this.getControl());
	}
	
	public UIPainter createPainter() {
		return new JFXSnapshotImagePainter(this.getControl());
	}
}
