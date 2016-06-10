package org.herac.tuxguitar.tray;

import org.eclipse.swt.widgets.TrayItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.ui.swt.resource.SWTImage;

public class TGTrayIcon {
	
	private TrayItem item;
	
	public TGTrayIcon(){
		super();
	}
	
	public void setItem(TrayItem item){
		this.item = item;
	}
	
	public void loadImage(){
		if( this.item != null && !this.item.isDisposed() ){
			this.item.setImage(((SWTImage) TuxGuitar.getInstance().getIconManager().getAppIcon24()).getHandle());
		}
	}
}
