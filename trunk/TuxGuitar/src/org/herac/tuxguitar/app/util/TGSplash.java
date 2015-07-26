package org.herac.tuxguitar.app.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.graphics.TGImageImpl;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.graphics.TGPainter;

public class TGSplash {
	
	private static TGSplash instance;
	
	private Shell shell;
	
	private TGSplash(){
		super();
	}
	
	public static TGSplash instance(){
		if(instance == null){
			instance = new TGSplash();
		}
		return instance;
	}
	
	public void init() {
		if(TuxGuitar.getInstance().getConfig().getBooleanValue(TGConfigKeys.SHOW_SPLASH)){
			final Image image = TuxGuitar.getInstance().getIconManager().getAppSplash();
			
			this.shell = new Shell(TuxGuitar.getInstance().getDisplay(), SWT.NO_TRIM | SWT.NO_BACKGROUND);
			this.shell.setLayout(new FillLayout());
			this.shell.setBounds(getBounds(image));
			this.shell.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
			this.shell.setText(TuxGuitar.APPLICATION_NAME);
			this.shell.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					TGPainter painter = new TGPainterImpl(e.gc);
					painter.drawImage(new TGImageImpl(image), 0, 0);
				}
			});
			this.shell.open();
			this.shell.redraw();
			this.shell.update();
		}
	}
	
	public void finish(){
		if(this.shell != null && !this.shell.isDisposed()){
			this.shell.close();
			this.shell.dispose();
		}
		instance = null;
	}
	
	private Rectangle getBounds(Image image){
		Rectangle iBounds = image.getBounds();
		Rectangle mBounds = this.shell.getMonitor().getClientArea();
		int x = ( ((mBounds.x + mBounds.width) / 2) - (iBounds.width / 2) );
		int y = ( ((mBounds.y + mBounds.height) / 2) - (iBounds.height / 2) );
		int width = iBounds.width;
		int height = iBounds.height;
		return new Rectangle( x , y , width , height);
	}
	
}
