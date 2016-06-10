package org.herac.tuxguitar.ui.swt.widget;

import java.awt.Point;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.swt.SWTComponent;
import org.herac.tuxguitar.ui.swt.resource.SWTImage;
import org.herac.tuxguitar.ui.widget.UISplashWindow;

public class SWTSplashWindow extends SWTComponent<Shell> implements UISplashWindow {
	
	private UIImage image;
	private UIImage splashImage;
	
	public SWTSplashWindow(Display display) {
		super(new Shell(display, SWT.NO_TRIM | SWT.NO_BACKGROUND));
		
		this.getControl().setLayout(new FillLayout());
		this.getControl().addPaintListener(new SWTPaintListener(this));
	}
	
	public String getText() {
		return this.getControl().getText();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}

	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;
		
		this.getControl().setImage(this.image != null ? ((SWTImage) this.image).getHandle() : null);
	}
	
	public UIImage getSplashImage() {
		return splashImage;
	}

	public void setSplashImage(UIImage splashImage) {
		this.splashImage = splashImage;
	}

	public void dispose() {
		this.getControl().dispose();
	}

	public boolean isDisposed() {
		return this.getControl().isDisposed();
	}
	
	public void open() {
		this.getControl().setBounds(getPreferredBounds());
		this.getControl().open();
		this.getControl().redraw();
		this.getControl().update();
	}
	
	public Rectangle getPreferredBounds(){
		Point iBounds = this.getSplashImageSize();
		Rectangle mBounds = this.getControl().getMonitor().getClientArea();
		int x = ( ((mBounds.x + mBounds.width) / 2) - (iBounds.x / 2) );
		int y = ( ((mBounds.y + mBounds.height) / 2) - (iBounds.y / 2) );
		int width = iBounds.x;
		int height = iBounds.y;
		return new Rectangle( x , y , width , height);
	}
	
	public Point getSplashImageSize(){
		if( this.getSplashImage() != null && !this.getSplashImage().isDisposed() ) {
			this.getSplashImage().getWidth();
			return new Point(Math.round(this.getSplashImage().getWidth()), Math.round(this.getSplashImage().getHeight()));
		}
		return new Point(0, 0);
	}
	
	public class SWTPaintListener implements PaintListener {
		
		private SWTSplashWindow window;
		
		public SWTPaintListener(SWTSplashWindow window) {
			this.window = window;
		}
		
		public void paintControl(PaintEvent e) {
			if( this.window.getSplashImage() != null && !this.window.getSplashImage().isDisposed() ) {
				e.gc.drawImage(((SWTImage) this.window.getSplashImage()).getHandle(), 0, 0);
			}
		}
	}
}
