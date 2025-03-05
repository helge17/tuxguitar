package org.herac.tuxguitar.ui.qt.toolbar;

import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;

public class QTToolCheckableItem extends QTToolActionItem implements UIToolCheckableItem {
	
	public QTToolCheckableItem(QTToolBar parent) {
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
