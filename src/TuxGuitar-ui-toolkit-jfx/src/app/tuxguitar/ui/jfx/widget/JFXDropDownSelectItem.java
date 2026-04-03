package app.tuxguitar.ui.jfx.widget;

import app.tuxguitar.ui.widget.UISelectItem;

public class JFXDropDownSelectItem<T> {

	private UISelectItem<T> item;

	public JFXDropDownSelectItem(UISelectItem<T> item) {
		this.item = item;
	}

	public UISelectItem<T> getItem() {
		return item;
	}

	public String toString() {
		return this.item.getText();
	}
}
