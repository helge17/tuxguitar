package org.herac.tuxguitar.cocoa.toolbar;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.util.TGSynchronizer;

public class MacToolbarAction {
	
	protected static void toogleToolbar(){
		try {
			TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
				public void run() throws Throwable {
					TuxGuitar.instance().getItemManager().toogleToolbarVisibility();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	/*
	public static void toogleToolbar(){
		Shell shell = TuxGuitar.instance().getShell();
		Control[] children = shell.getChildren();
		if( children.length > 0 && children[1] instanceof Composite){
			Composite container = (Composite)children[1];
			Object containerData = container.getLayoutData();
			if( containerData instanceof FormData ){
				FormData data = (FormData) containerData;
				CoolBar coolbar = TuxGuitar.instance().getItemManager().getCoolbar();
				if( coolbar.isDisposed() ){
					TuxGuitar.instance().getItemManager().createCoolbar();
					coolbar = TuxGuitar.instance().getItemManager().getCoolbar();
					data.top = new FormAttachment( coolbar, TuxGuitar.MARGIN_WIDTH);
				}else{
					data.top = new FormAttachment(0, TuxGuitar.MARGIN_WIDTH);
					coolbar.dispose();
				}
			}
			container.layout( true, true );
		}
		shell.layout(true,true);
	}
	*/
}
