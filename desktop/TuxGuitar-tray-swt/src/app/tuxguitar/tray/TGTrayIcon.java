package app.tuxguitar.tray;

import org.eclipse.swt.widgets.TrayItem;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.ui.swt.resource.SWTImage;

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
			this.item.setImage(((SWTImage) TuxGuitar.getInstance().getIconManager().getAppIcon()).getHandle());
		}
	}
}
