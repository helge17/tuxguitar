package app.tuxguitar.ui.swt.toolbar;

import org.eclipse.swt.widgets.ToolItem;
import app.tuxguitar.ui.toolbar.UIToolCheckableItem;

public class SWTToolCheckableItem extends SWTToolActionItem implements UIToolCheckableItem {

	public SWTToolCheckableItem(ToolItem item, SWTToolBar parent) {
		super(item, parent);
	}

	public boolean isChecked() {
		return this.getControl().getSelection();
	}

	public void setChecked(boolean checked) {
		this.getControl().setSelection(checked);
	}
}
