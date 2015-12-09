package org.herac.tuxguitar.app.view.util;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.app.graphics.TGImageImpl;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGBufferedPainterLocked {
	
	private TGContext context;
	private TGImage buffer;
	private TGBufferedPainterHandle handle;
	
	public TGBufferedPainterLocked(TGContext context, TGBufferedPainterHandle handle) {
		this.context = context;
		this.handle = handle;
		this.handle.getPaintableControl().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				disposePaintBuffer();
			}
		});
	}
	
	public void disposePaintBuffer() {
		if( this.buffer != null && !this.buffer.isDisposed() ) {
			this.buffer.dispose();
			this.buffer = null;
		}
	}
	
	public void fillPaintBuffer() {
		Rectangle size = this.handle.getPaintableControl().getClientArea();
		int clientWidth = size.width;
		int clientHeight = size.height;
		
		if( this.buffer == null || this.buffer.isDisposed() || this.buffer.getWidth() != clientWidth || this.buffer.getHeight() != clientHeight ) {
			this.buffer = new TGImageImpl(this.handle.getPaintableControl().getDisplay(), clientWidth, clientHeight);
		}
		
		TGPainter tgPainter = this.buffer.createPainter();
		
		this.handle.paintControl(tgPainter);
		
		tgPainter.dispose();
	}
	
	public void paintBufferLocked(TGPainter painter) {
		TGEditorManager editor = TGEditorManager.getInstance(this.context);
		if (editor.tryLock()) {
			try {
				this.fillPaintBuffer();
			} finally {
				editor.unlock();
			}
		} else {
			// try later
			this.redrawLater();
		}
		
		if( this.buffer != null && !this.buffer.isDisposed() ) {
			painter.drawImage(this.buffer, 0, 0);
		}
	}
	
	public void redrawLater() {
		final Composite paintableControl = this.handle.getPaintableControl();
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				if(!paintableControl.isDisposed()) {
					paintableControl.redraw();
				}
			}
		});
	}
	
	public static interface TGBufferedPainterHandle {
		
		void paintControl(TGPainter painter);
		
		Composite getPaintableControl();
	}
}
