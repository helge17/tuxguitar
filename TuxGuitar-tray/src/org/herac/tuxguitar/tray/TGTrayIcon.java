package org.herac.tuxguitar.tray;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TrayItem;
import org.herac.tuxguitar.gui.util.TGFileUtils;

public class TGTrayIcon {
	
	private Image image;
	private TrayItem item;
	
	public TGTrayIcon(){
		super();
	}
	
	public void setItem(TrayItem item){
		this.item = item;
	}
	
	public void loadImage(){
		this.dispose();
		if(this.item != null && !this.item.isDisposed()){
			this.image = TGFileUtils.loadImage("icon-24x24.png");
			this.item.setImage(this.image);
		}
	}
	
	public void dispose(){
		if(this.image != null && !this.image.isDisposed()){
			this.image.dispose();
		}
	}
}
