package org.herac.tuxguitar.ui.qt.menu;

import org.herac.tuxguitar.ui.menu.UIMenuItem;
import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIKeyConvination;

import com.trolltech.qt.core.QObject;

public abstract class QTMenuItem<T extends QObject> extends QTComponent<T> implements UIMenuItem {
	
	private UIKeyConvination keyConvination;
	private UIImage image;
	private QTAbstractMenu<?> parent;
	
	public QTMenuItem(T item, QTAbstractMenu<?> parent) {
		super(item);
		
		this.parent = parent;
		this.parent.addItem(this);
	}
	
	public QTAbstractMenu<?> getParent() {
		return this.parent;
	}
	
	public void dispose() {
		this.getParent().removeItem(this);
		this.getControl().dispose();
		
		super.dispose();
	}
	
	public UIKeyConvination getKeyConvination() {
		return keyConvination;
	}

	public void setKeyConvination(UIKeyConvination keyConvination) {
		this.keyConvination = keyConvination;
	}

	public UIImage getImage() {
		return this.image;
	}
	
	public void setImage(UIImage image) {
		this.image = image;
	}
}
