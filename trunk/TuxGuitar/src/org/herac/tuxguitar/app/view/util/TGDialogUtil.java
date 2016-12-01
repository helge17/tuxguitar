package org.herac.tuxguitar.app.view.util;

import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class TGDialogUtil {
	
	public static final int OPEN_STYLE_PACK = 0x01;
	
	public static final int OPEN_STYLE_LAYOUT = 0x02;
	
	public static final int OPEN_STYLE_CENTER = 0x04;
	
	public static final int OPEN_STYLE_MAXIMIZED = 0x8;
	
	public static final void openDialog(UIWindow dialog, int style){
		TGDialogUtil.openDialog(dialog, (UIWindow) dialog.getParent(), style);
	}
	
	public static final void openDialog(UIWindow dialog, UIWindow parent, int style){
		if((style & OPEN_STYLE_PACK) != 0){
			dialog.pack();
		}
		if((style & OPEN_STYLE_LAYOUT) != 0){
			dialog.layout();
		}
		if((style & OPEN_STYLE_MAXIMIZED) != 0){
			dialog.maximize();
		}
		else if((style & OPEN_STYLE_CENTER) != 0) {
			UIRectangle parentBounds = parent.getBounds();
			UIRectangle dialogBounds = dialog.getBounds();
			dialogBounds.getPosition().setX(Math.max(0, parentBounds.getX() + (parentBounds.getWidth() - dialogBounds.getWidth()) / 2f));
			dialogBounds.getPosition().setY(Math.max(0, parentBounds.getY() + (parentBounds.getHeight() - dialogBounds.getHeight()) / 2f));
			dialog.setBounds(dialogBounds);
		}
		dialog.open();
	}
}
