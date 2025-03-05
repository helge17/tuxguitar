package org.herac.tuxguitar.ui.qt.menu;

import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;

public class QTMenuCheckableItem extends QTMenuActionItem implements UIMenuCheckableItem {
	
	public QTMenuCheckableItem(QTAbstractMenu<?> parent) {
		super(parent);
		
		this.getControl().setCheckable(true);
	}

	public boolean isChecked() {
		return this.getControl().isChecked();
	}

	public void setChecked(boolean checked) {
		this.getControl().setChecked(checked);
	}
}
