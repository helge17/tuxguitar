package org.herac.tuxguitar.ui.jfx.resource;

import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import org.herac.tuxguitar.ui.resource.UIPainter;

public class JFXAWTImage extends JFXAbstractImage<BufferedImage> {
	
	private WritableImage jfxHandle;
	
	public JFXAWTImage(float width, float height) {
		super(new BufferedImage(Math.max(1, Math.round(width)), Math.max(1, Math.round(height)), BufferedImage.TYPE_INT_RGB));
		
		this.jfxHandle = new WritableImage(this.getControl().getWidth(), this.getControl().getHeight());
	}

	@Override
	public float getWidth() {
		return this.getControl().getWidth();
	}

	@Override
	public float getHeight() {
		return this.getControl().getHeight();
	}

	@Override
	public UIPainter createPainter() {
		return new JFXAWTImagePainter(this);
	}
	
	@Override
	public void paint(JFXAbstractPainter<?> painter, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		painter.drawNativeImage(this.getHandle(), srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight);
	}

	@Override
	public void paint(JFXAbstractPainter<?> painter, float x, float y) {
		painter.drawNativeImage(this.getHandle(), x, y);
	}
	
	@Override
	public Image getHandle() {
		return this.jfxHandle;
	}
	
	public void refreshHandle() {
		this.jfxHandle = SwingFXUtils.toFXImage(this.getControl(), this.jfxHandle);
	}
}
