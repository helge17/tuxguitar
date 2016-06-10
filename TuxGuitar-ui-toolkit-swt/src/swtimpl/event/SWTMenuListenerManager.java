package swtimpl.event;

import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.herac.tuxguitar.ui.event.UIMenuEvent;
import org.herac.tuxguitar.ui.event.UIMenuHideListener;
import org.herac.tuxguitar.ui.event.UIMenuHideListenerManager;
import org.herac.tuxguitar.ui.event.UIMenuShowListener;
import org.herac.tuxguitar.ui.event.UIMenuShowListenerManager;

import swtimpl.SWTComponent;

public class SWTMenuListenerManager implements MenuListener {
	
	private SWTComponent<?> control;
	private UIMenuShowListenerManager menuShowListener;
	private UIMenuHideListenerManager menuHideListener;
	
	public SWTMenuListenerManager(SWTComponent<?> control) {
		this.control = control;
		this.menuShowListener = new UIMenuShowListenerManager();
		this.menuHideListener = new UIMenuHideListenerManager();
	}
	
	public boolean isEmpty() {
		return (this.menuShowListener.isEmpty() && this.menuHideListener.isEmpty());
	}
	
	public void addListener(UIMenuShowListener listener) {
		this.menuShowListener.addListener(listener);
	}
	
	public void addListener(UIMenuHideListener listener) {
		this.menuHideListener.addListener(listener);
	}
	
	public void removeListener(UIMenuShowListener listener) {
		this.menuShowListener.removeListener(listener);
	}
	
	public void removeListener(UIMenuHideListener listener) {
		this.menuHideListener.removeListener(listener);
	}
	
	public void menuShown(MenuEvent e) {
		this.menuShowListener.onMenuShow(new UIMenuEvent(this.control));
	}
	
	public void menuHidden(MenuEvent e) {
		this.menuHideListener.onMenuHide(new UIMenuEvent(this.control));
	}
}
