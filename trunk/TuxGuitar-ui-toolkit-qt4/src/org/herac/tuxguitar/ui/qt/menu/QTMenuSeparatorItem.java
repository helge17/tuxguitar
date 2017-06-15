package org.herac.tuxguitar.ui.qt.menu;

import org.herac.tuxguitar.ui.UIComponent;

import com.trolltech.qt.gui.QAction;

public class QTMenuSeparatorItem extends QTMenuItem<QAction> implements UIComponent {
	
	public QTMenuSeparatorItem(QTAbstractMenu<?> parent) {
		super(parent.createNativeSeparator(), parent);
	}

	public String getText() {
		return null;
	}

	public void setText(String text) {
		// not implemented
	}

	public boolean isEnabled() {
		// not implemented
		return false;
	}

	public void setEnabled(boolean enabled) {
		// not implemented
	}
}
