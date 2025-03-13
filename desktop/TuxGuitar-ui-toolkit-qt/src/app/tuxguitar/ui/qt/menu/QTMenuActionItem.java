package app.tuxguitar.ui.qt.menu;

import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.qt.QTComponent;
import app.tuxguitar.ui.qt.event.QTSelectionListenerManager;
import app.tuxguitar.ui.qt.resource.QTImage;
import app.tuxguitar.ui.resource.UIImage;

import io.qt.core.QMetaObject;
import io.qt.core.Qt;
import io.qt.core.Qt.ConnectionType;
import io.qt.gui.QAction;
import io.qt.widgets.QApplication;

public class QTMenuActionItem extends QTMenuItem<QAction> implements UIMenuActionItem {

	private QTSelectionListenerManager selectionListener;

	public QTMenuActionItem(QTAbstractMenu<?> parent) {
		super(parent.createNativeAction(), parent);

		this.selectionListener = new QTAsyncSelectionListenerManager(this);
	}

	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().triggered.connect(this.selectionListener, QTSelectionListenerManager.SIGNAL_METHOD);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().triggered.disconnect();
		}
	}

	public String getText() {
		return this.getControl().text();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}

	public boolean isEnabled() {
		return this.getControl().isEnabled();
	}

	public void setEnabled(boolean enabled) {
		this.getControl().setEnabled(enabled);
	}

	@Override
	public void setImage(UIImage image) {
		super.setImage(image);

		this.getControl().setIcon(image != null ? ((QTImage) image).createIcon() : null);
	}

	private class QTAsyncSelectionListenerManager extends QTSelectionListenerManager {

		public QTAsyncSelectionListenerManager(QTComponent<?> control) {
			super(control);
		}

		public void handle() {
			Runnable runnable = new Runnable() {
				public void run() {
					if(!QTAsyncSelectionListenerManager.this.getControl().isDisposed()) {
						QTAsyncSelectionListenerManager.super.handle();
					}
				}
			};
			QMetaObject.invokeMethod(runnable::run, Qt.ConnectionType.QueuedConnection);
		}
	}
}
