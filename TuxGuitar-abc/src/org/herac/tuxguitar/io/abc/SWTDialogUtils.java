package org.herac.tuxguitar.io.abc;

import org.eclipse.swt.widgets.Shell;

public class SWTDialogUtils {
	
	public static final int OPEN_STYLE_PACK = 0x01;
	
	public static final int OPEN_STYLE_LAYOUT = 0x02;
	
	public static final int OPEN_STYLE_CENTER = 0x04;
	
	public static final int OPEN_STYLE_MAXIMIZED = 0x8;
	
	public static final Shell newDialog(Shell parent,int style){
		return new Shell(parent, style);
	}
	
	public static final void openDialog(Shell dialog, int style){
		SWTDialogUtils.openDialog(dialog, dialog.getParent().getShell(), style);
	}
	
	public static final void openDialog(final Shell dialog, final Shell parent, int style){
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
	}
}
