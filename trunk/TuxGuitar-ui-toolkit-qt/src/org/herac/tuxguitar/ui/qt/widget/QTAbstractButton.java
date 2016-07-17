package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.qt.event.QTSelectionListenerManager;
import org.herac.tuxguitar.ui.qt.resource.QTImage;
import org.herac.tuxguitar.ui.resource.UIImage;

import com.trolltech.qt.gui.QAbstractButton;

public abstract class QTAbstractButton<T extends QAbstractButton> extends QTWidget<T> {
	
	private QTSelectionListenerManager selectionListener;
	
	private UIImage image;
	
	public QTAbstractButton(T control, QTContainer parent) {
		super(control, parent);
		
		this.selectionListener = new QTSelectionListenerManager(this);
	}
	
	public String getText() {
		return this.getControl().text();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}
	
	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;
		this.getControl().setIcon(this.image != null ? ((QTImage) this.image).createIcon() : null);
	}
	
	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().clicked.connect(this.selectionListener, QTSelectionListenerManager.SIGNAL_METHOD);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().clicked.disconnect();
		}
	}
}
