package org.herac.tuxguitar.ui.jfx.resource;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;

public class JFXImagePainter extends JFXPainter {
	
	private Canvas canvas;
	private JFXImageHandle handle;
	
	public JFXImagePainter(JFXImageHandle handle, Canvas canvas) {
		super(canvas.getGraphicsContext2D());
		
		this.canvas = canvas;
		this.handle = handle;
        if( this.handle.getHandle() != null ) {
        	this.getControl().drawImage(this.handle.getHandle(), 0, 0);
        }
	}
	
	public JFXImagePainter(JFXImageHandle handle) {
		this(handle, new Canvas(handle.getWidth(), handle.getHeight()));
	}
	
	public void dispose() {
		if(!Platform.isFxApplicationThread()) {
			Platform.runLater(new Runnable() {
				public void run() {
					JFXImagePainter.this.handle.setHandle(JFXImagePainter.this.canvas.snapshot(null, null));
					JFXImagePainter.super.dispose();
				}
			});
			
			while(!this.isDisposed()) {
				Thread.yield();
			}
		} else {
			this.handle.setHandle(this.canvas.snapshot(null, null));
			
			super.dispose();
		}
	}
}
