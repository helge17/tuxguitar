package org.herac.tuxguitar.ui.jfx.resource;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import org.herac.tuxguitar.ui.resource.UIPainter;

public class JFXBufferedImage extends JFXAbstractImage<JFXBufferedImageHandle> {
	
	public JFXBufferedImage(JFXBufferedImageHandle component) {
		super(component);
	}
	
	public JFXBufferedImage(float width, float height) {
		this(new JFXBufferedImageHandle(width, height));
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
		this.getControl().getCommands().clear();
		
		return new JFXBufferedPainter(this.getControl());
	}

	@Override
	public void paint(JFXAbstractPainter<?> painter, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		this.paint(painter, destX, destY);
	}

	@Override
	public void paint(JFXAbstractPainter<?> painter, float x, float y) {
		for(JFXBufferedPainterCommand command : this.getControl().getCommands()) {
			command.paint(painter, x, y);
		}
	}
	
	@Override
	public Image getHandle() {
		Canvas canvas = new Canvas(this.getWidth(), this.getHeight());
		
		JFXPainter jfxPainter = new JFXPainter(canvas.getGraphicsContext2D());
		this.paint(jfxPainter, 0, 0);
		jfxPainter.dispose();
		
		return canvas.snapshot(null, null);
	}
	
	@Override
	public JFXBufferedImage clone() {
		return new JFXBufferedImage(this.getControl());
	}
}
