package app.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import app.tuxguitar.ui.event.UICloseListener;
import app.tuxguitar.ui.menu.UIMenuBar;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.swt.event.SWTCloseListenerManager;
import app.tuxguitar.ui.swt.menu.SWTMenu;
import app.tuxguitar.ui.swt.resource.SWTImage;
import app.tuxguitar.ui.widget.UIWindow;

public class SWTWindow extends SWTLayoutContainer<Shell> implements UIWindow {

	private UIImage image;
	private UIMenuBar menuBar;
	private SWTCloseListenerManager closeListener;
	private SWTWindowResizeListener resizeListener;

	public SWTWindow(Shell shell, SWTContainer<? extends Composite> parent) {
		super(shell, parent);

		this.closeListener = new SWTCloseListenerManager(this);
		this.resizeListener = new SWTWindowResizeListener(this);
		this.getControl().addListener (SWT.Resize,  this.resizeListener);
	}

	public SWTWindow(Display display) {
		this(new Shell(display), null);
	}

	public SWTWindow(SWTWindow parent, boolean modal, boolean resizable) {
		this(new Shell(parent.getControl(), SWT.DIALOG_TRIM | (modal ? SWT.APPLICATION_MODAL : 0) | (resizable ? SWT.RESIZE | SWT.MAX : 0)), parent);
	}

	public String getText() {
		return this.getControl().getText();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}

	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;

		this.getControl().setImage(this.image != null ? ((SWTImage) this.image).getHandle() : null);
	}

	public UIMenuBar getMenuBar() {
		return this.menuBar;
	}

	public void setMenuBar(UIMenuBar menuBar) {
		this.menuBar = menuBar;
		this.getControl().setMenuBar(this.menuBar != null ? ((SWTMenu) this.menuBar).getControl() : null);
	}

	public void open() {
		this.getControl().open();
	}

	public void close() {
		this.getControl().close();
	}

	public void setBounds(UIRectangle bounds) {
		this.resizeListener.setBounds(bounds);
		super.setBounds(bounds);
	}

	public void minimize() {
		if((this.getControl().getStyle() & SWT.MIN) != 0) {
			this.getControl().setMinimized(true);
		}
	}

	public void maximize() {
		if((this.getControl().getStyle() & SWT.MAX) != 0) {
			this.getControl().setMaximized(true);
		}
	}

	public boolean isMaximized() {
		return this.getControl().getMaximized();
	}

	public void moveToTop() {
		this.getControl().setMinimized(false);
		this.getControl().forceActive();
	}

	public void addCloseListener(UICloseListener listener) {
		if( this.closeListener.isEmpty() ) {
			this.getControl().addShellListener(this.closeListener);
		}
		this.closeListener.addListener(listener);
	}

	public void removeCloseListener(UICloseListener listener) {
		this.closeListener.removeListener(listener);
		if( this.closeListener.isEmpty() ) {
			this.getControl().removeShellListener(this.closeListener);
		}
	}

	private class SWTWindowResizeListener implements Listener {

		private UIRectangle bounds;
		private SWTWindow window;

		public SWTWindowResizeListener(SWTWindow window) {
			this.window = window;
		}

		public void handleEvent (Event e) {
			UIRectangle bounds = this.window.getBounds();
			if( this.bounds == null || !this.bounds.equals(bounds)) {
				this.bounds = bounds;
				this.window.layout();
			}
		}

		public void setBounds(UIRectangle bounds) {
			this.bounds = bounds;
		}
	}

	@Override
	public void setMinimumSize(int width, int height) {
		this.getControl().setMinimumSize(width, height);
	}
}
