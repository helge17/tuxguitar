package org.herac.tuxguitar.android.view.tablature;

import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGPoint;
import org.herac.tuxguitar.graphics.TGRectangle;

public class TGSongViewLayoutPainter {
	
	private TGSongViewController controller;
	private TGImage buffer;
	private TGPoint point;
	private boolean refreshBuffer;
	
	public TGSongViewLayoutPainter(TGSongViewController controller) {
		this.controller = controller;
	}
	
	public void dispose() {
		if( this.buffer != null && !this.buffer.isDisposed() ) {
			this.buffer.dispose();
			this.buffer = null;
		}
	}
	
	public void refreshBuffer() {
		this.refreshBuffer = true;
	}
	
	public void paint(TGPainter target, TGRectangle clientArea, float fromX, float fromY) {
		this.resizeBuffer(clientArea);
		this.updatePoint(fromX, fromY);
		
		if( this.refreshBuffer ) {
			this.refreshBuffer = false;
			
			TGPainter tgPainter = this.buffer.createPainter();
			this.paintArea(tgPainter, clientArea);
			this.paintLayout(tgPainter, clientArea);
			tgPainter.dispose();
		}
		target.drawImage(this.buffer, 0, 0);
	}
	
	private void paintLayout(TGPainter painter, TGRectangle area) {
		this.controller.getLayout().paint(painter, area, this.point.getX(), this.point.getY());
	}
	
	private void paintArea(TGPainter painter, TGRectangle area) {
		painter.setBackground(this.controller.getResourceFactory().createColor(255, 255, 255));
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(area.getX(), area.getY(), area.getWidth(), area.getHeight());
		painter.closePath();
	}
	
	private void updatePoint(float x, float y) {
		if( this.point == null || this.point.getX() != x || this.point.getY() != y ) {
			this.point = new TGPoint(x, y);
			this.refreshBuffer();
		}
	}
	
	private void resizeBuffer(TGRectangle area) {
		if( this.buffer == null || this.buffer.isDisposed() || this.buffer.getWidth() != area.getWidth() || this.buffer.getHeight() != area.getHeight() ) {
			this.dispose();
			this.buffer = this.controller.getResourceFactory().createImage(area.getWidth(), area.getHeight());
			this.refreshBuffer();
		}
	}
}
