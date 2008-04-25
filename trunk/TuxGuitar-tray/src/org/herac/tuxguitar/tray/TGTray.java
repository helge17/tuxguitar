package org.herac.tuxguitar.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.icons.IconLoader;
import org.herac.tuxguitar.gui.system.language.LanguageLoader;

public class TGTray implements IconLoader,LanguageLoader{
	
	private boolean visible;
	private Display display;
	private Tray tray;
	private TGTrayIcon icon;
	private TGTrayMenu menu;
	
	public TGTray(){
		this.display = TuxGuitar.instance().getDisplay();
		this.tray = this.display.getSystemTray();
		this.icon = new TGTrayIcon();
		this.menu = new TGTrayMenu();
		TuxGuitar.instance().getIconManager().addLoader(this);
		TuxGuitar.instance().getLanguageManager().addLoader(this);
	}
	
	public void removeTray(){
		if (this.tray != null) {
			setVisible(true);
			TrayItem items[] = this.tray.getItems();
			for(int i = 0; i < items.length; i ++){
				items[i].dispose();
			}
			this.icon.dispose();
			this.menu.dispose();
		}
	}
	
	public void addTray() {
		if (this.tray != null) {
			this.menu.make();
			this.visible = true;
			TrayItem item = new TrayItem (this.tray, SWT.NONE);
			item.setToolTipText(TuxGuitar.APPLICATION_NAME);
			item.addListener (SWT.Selection, new Listener () {
				public void handleEvent (Event event) {
					setVisible();
				}
			});
			item.addListener (SWT.MenuDetect, new Listener () {
				public void handleEvent (Event event) {
					showMenu();
				}
			});
			this.icon.setItem(item);
			this.loadIcons();
		}
	}
	
	public void loadIcons() {
		this.icon.loadImage();
		this.menu.loadIcons();
	}
	
	public void loadProperties(){
		this.menu.loadProperties();
	}
	
	protected void setVisible(){
		this.setVisible(!this.visible);
	}
	
	protected void setVisible(boolean visible){
		if (this.tray != null) {
			Shell shells[] = this.display.getShells();
			for(int i = 0; i < shells.length; i ++){
				shells[i].setVisible( visible );
			}
			this.visible = visible;
		}
	}
	
	protected void showMenu(){
		this.menu.show();
	}
}
