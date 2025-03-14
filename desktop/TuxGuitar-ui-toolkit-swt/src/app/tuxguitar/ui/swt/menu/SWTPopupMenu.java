package app.tuxguitar.ui.swt.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import app.tuxguitar.ui.event.UIMenuHideListener;
import app.tuxguitar.ui.event.UIMenuShowListener;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.ui.resource.UIPosition;
import app.tuxguitar.ui.swt.event.SWTMenuListenerManager;
import app.tuxguitar.ui.swt.widget.SWTWindow;

public class SWTPopupMenu extends SWTMenu implements UIPopupMenu {

	private SWTMenuListenerManager menuListener;

	public SWTPopupMenu(Shell shell) {
		super(shell, SWT.POP_UP);

		this.menuListener = new SWTMenuListenerManager(this);
	}

	public SWTPopupMenu(SWTWindow window) {
		this(window.getControl());
	}

	public void open(UIPosition position) {
		this.getControl().setLocation(Math.round(position.getX()), Math.round(position.getY()));
		this.getControl().setVisible(true);
	}

	public void addMenuShowListener(UIMenuShowListener listener) {
		if( this.menuListener.isEmpty() ) {
			this.getControl().addMenuListener(this.menuListener);
		}
		this.menuListener.addListener(listener);
	}

	public void addMenuHideListener(UIMenuHideListener listener) {
		if( this.menuListener.isEmpty() ) {
			this.getControl().addMenuListener(this.menuListener);
		}
		this.menuListener.addListener(listener);
	}

	public void removeMenuShowListener(UIMenuShowListener listener) {
		this.menuListener.removeListener(listener);
		if( this.menuListener.isEmpty() ) {
			this.getControl().removeMenuListener(this.menuListener);
		}
	}

	public void removeMenuHideListener(UIMenuHideListener listener) {
		this.menuListener.removeListener(listener);
		if( this.menuListener.isEmpty() ) {
			this.getControl().removeMenuListener(this.menuListener);
		}
	}
}
