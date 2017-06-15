package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.event.UICloseListener;
import org.herac.tuxguitar.ui.event.UIResizeEvent;
import org.herac.tuxguitar.ui.event.UIResizeListener;
import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.qt.event.QTCloseListenerManager;
import org.herac.tuxguitar.ui.qt.menu.QTMenuBar;
import org.herac.tuxguitar.ui.qt.resource.QTImage;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UIWindow;

import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.core.QEvent.Type;
import com.trolltech.qt.gui.QContentsMargins;
import com.trolltech.qt.gui.QWidget;

public abstract class QTAbstractWindow<T extends QWidget> extends QTLayoutContainer<T> implements UIWindow {
	
	private UIImage image;
	private UIMenuBar menuBar;
	private QTWindowCloseListener closeListener;
	private QTWindowResizeListener resizeListener;
	
	public QTAbstractWindow(T widget, QTContainer parent) {
		super(widget, parent, false);
		
		this.closeListener = new QTWindowCloseListener(this);
		this.resizeListener = new QTWindowResizeListener(this);
		this.addResizeListener(this.resizeListener);
		this.connectCloseListener();
	}
	
	public String getText() {
		return this.getControl().windowTitle();
	}

	public void setText(String text) {
		this.getControl().setWindowTitle(text);
	}

	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;
		this.getControl().setWindowIcon(this.image != null ? ((QTImage) this.image).createIcon() : null);
	}
	
	public UIMenuBar getMenuBar() {
		return this.menuBar;
	}

	public void setMenuBar(UIMenuBar menuBar) {
		this.menuBar = menuBar;
		if( this.isVisible() ) {
			this.layout();
		}
	}
	
	public void open() {
		this.getControl().show();
	}

	public void close() {
		this.getControl().close();
	}
	
	public void computeMargins() {
		super.computeMargins();
		
		if( this.menuBar != null ) {
			QContentsMargins margins = this.getContainerChildMargins();
			margins.top += ((QTMenuBar)this.menuBar).getControl().sizeHint().height();
			
			this.setContainerChildMargins(margins);
		}
	}
	
	public void setBounds(UIRectangle bounds) {
		this.resizeListener.setBounds(bounds);
		
		super.setBounds(bounds);
	}
	
	public void minimize() {
		this.getControl().showMinimized();
	}
	
	public void maximize() {
		this.getControl().showMaximized();
	}
	
	public boolean isMaximized() {
		return this.getControl().isMaximized();
	}
	
	public void moveToTop() {
		this.getControl().raise();
		this.getControl().activateWindow();
	}
	
	public void connectCloseListener() {
		this.getEventFilter().connect(Type.Close, this.closeListener);
	}
	
	public void addCloseListener(UICloseListener listener) {
		this.closeListener.addListener(listener);
	}

	public void removeCloseListener(UICloseListener listener) {
		this.closeListener.removeListener(listener);
	}
	
	public void dispose() {
		if( this.menuBar != null && !this.menuBar.isDisposed()) {
			this.menuBar.dispose();
		}
		
		super.dispose();
	}
	
	private class QTWindowResizeListener implements UIResizeListener {
		
		private UIRectangle bounds;
		private QTAbstractWindow<?> window;
		
		public QTWindowResizeListener(QTAbstractWindow<?> window) {
			this.window = window;
		}
		
		public void onResize(UIResizeEvent event) {
			if(!this.window.isDisposed()) {
				UIRectangle bounds = this.window.getBounds();
				
				int frameY = this.window.getControl().frameGeometry().y();
				if( frameY < 0 ) {
					bounds.getPosition().setY(bounds.getPosition().getY() - frameY);
					
					this.bounds = null;
				}
				
				if( this.bounds == null || !this.bounds.equals(bounds)) {
					this.bounds = bounds;
					this.window.layout(bounds);
				}
			}
		}
		
		public void setBounds(UIRectangle bounds) {
			this.bounds = bounds;
		}
	}
	
	private class QTWindowCloseListener extends QTCloseListenerManager {
		
		public QTWindowCloseListener(QTAbstractWindow<?> window) {
			super(window);
		}
		
		public void handle(QEvent event) {
			if(!this.getControl().isDisposed()) {
				if(!this.isEmpty()) {
					super.handle(event);
				} else {
					this.getControl().dispose();
				}
			}
		}
	}
}