package org.herac.tuxguitar.gui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.actions.ActionLock;

public class DialogUtils {
	
	public static final int OPEN_STYLE_WAIT = 0x01;
	
	public static final int OPEN_STYLE_PACK = 0x02;
	
	public static final int OPEN_STYLE_LAYOUT = 0x04;
	
	public static final int OPEN_STYLE_CENTER = 0x08;
	
	public static final int OPEN_STYLE_MAXIMIZED = 0x10;
	
	public static final Shell newDialog(Display display,int style){
		return new Shell(display, style);
	}
	
	public static final Shell newDialog(Shell parent,int style){
		parent.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
		return new Shell(parent, style);
	}
	
	public static final void openDialog(Shell dialog,int style){
		DialogUtils.openDialog(dialog,dialog.getParent().getShell(),style);
	}
	
	public static final void openDialog(Shell dialog,Shell parent,int style){
		Display display = dialog.getDisplay();
		if((style & OPEN_STYLE_PACK) != 0){
			dialog.pack();
		}
		if((style & OPEN_STYLE_LAYOUT) != 0){
			dialog.layout();
		}
		if((style & OPEN_STYLE_MAXIMIZED) != 0){
			dialog.setMaximized(true);
		}
		else if((style & OPEN_STYLE_CENTER) != 0){
			int x = Math.max(0,parent.getBounds().x + (parent.getBounds().width - dialog.getSize().x) / 2);
			int y = Math.max(0,parent.getBounds().y + (parent.getBounds().height - dialog.getSize().y) / 2);
			dialog.setLocation(x,y);
		}
		dialog.open();
		
		parent.setCursor(display.getSystemCursor(SWT.CURSOR_ARROW));
		
		if((style & OPEN_STYLE_WAIT) != 0){
			if( (dialog.getStyle() & SWT.APPLICATION_MODAL) == 0 ){
				ActionLock.unlock();
			}
			while (!display.isDisposed() && !dialog.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
	}
}
