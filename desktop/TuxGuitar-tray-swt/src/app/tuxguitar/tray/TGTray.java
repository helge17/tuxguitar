package app.tuxguitar.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGSkinEvent;
import app.tuxguitar.app.system.language.TGLanguageEvent;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.ui.swt.SWTApplication;
import app.tuxguitar.util.TGContext;

public class TGTray implements TGEventListener {

	private boolean visible;
	private Display display;
	private Tray tray;
	private TGTrayIcon icon;
	private TGTrayMenu menu;

	public TGTray(TGContext context){
		this.display = ((SWTApplication)TGApplication.getInstance(context).getApplication()).getDisplay();
		this.tray = this.display.getSystemTray();
		this.icon = new TGTrayIcon();
		this.menu = new TGTrayMenu(context);
		TuxGuitar.getInstance().getSkinManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
	}

	public void removeTray(){
		if (this.tray != null) {
			setVisible(true);
			TrayItem items[] = this.tray.getItems();
			for(int i = 0; i < items.length; i ++){
				items[i].dispose();
			}
			this.menu.dispose();
		}
	}

	public void addTray() {
		if (this.tray != null) {
			this.menu.make();
			this.visible = true;
			TrayItem item = new TrayItem (this.tray, SWT.NONE);
			item.setToolTipText(TGApplication.NAME);
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

	public void processEvent(TGEvent event) {
		if( TGSkinEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIcons();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadProperties();
		}
	}
}
