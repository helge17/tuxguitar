package org.herac.tuxguitar.ui.jfx.resource;

import javafx.scene.canvas.Canvas;

public class JFXSnapshotImagePainter extends JFXPainter {
	
	private Canvas canvas;
	private JFXSnapshotImageHandle handle;
	
	public JFXSnapshotImagePainter(JFXSnapshotImageHandle handle, Canvas canvas) {
		super(canvas.getGraphicsContext2D());
		
		this.canvas = canvas;
		this.handle = handle;
        if( this.handle.getHandle() != null ) {
        	this.getControl().drawImage(this.handle.getHandle(), 0, 0);
        }
	}
	
	public JFXSnapshotImagePainter(JFXSnapshotImageHandle handle) {
		this(handle, new Canvas(handle.getWidth(), handle.getHeight()));
	}
	
	public void dispose() {
		this.handle.setHandle(this.canvas.snapshot(null, null));
	}
}
