package org.herac.tuxguitar.ui.swt.menu;

import org.eclipse.swt.widgets.MenuItem;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;

public class SWTMenuCheckableItem extends SWTMenuActionItem implements UIMenuCheckableItem {
	
	public SWTMenuCheckableItem(MenuItem item, SWTMenu parent) {
		super(item, parent);
	}

	public boolean isChecked() {
		return this.getControl().getSelection();
	}

	public void setChecked(boolean checked) {
		this.getControl().setSelection(checked);
	}
}
