package org.herac.tuxguitar.ui.qt.menu;

import org.herac.tuxguitar.ui.menu.UIMenuItem;
import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIKeyCombination;

import org.qtjambi.qt.core.QObject;

public abstract class QTMenuItem<T extends QObject> extends QTComponent<T> implements UIMenuItem {
	
	private UIKeyCombination keyCombination;
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
	
	public UIKeyCombination getKeyCombination() {
		return keyCombination;
	}

	public void setKeyCombination(UIKeyCombination keyCombination) {
		this.keyCombination = keyCombination;
	}

	public UIImage getImage() {
		return this.image;
	}
	
	public void setImage(UIImage image) {
		this.image = image;
	}
}
