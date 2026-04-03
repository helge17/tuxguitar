package app.tuxguitar.ui.widget;

import app.tuxguitar.ui.event.UISelectionListener;

public interface UIDropDownSelect<T> extends UIControl {

	T getSelectedValue();

	void setSelectedValue(T value);

	UISelectItem<T> getSelectedItem();

	void setSelectedItem(UISelectItem<T> item);

	void addItem(UISelectItem<T> item);

	void removeItem(UISelectItem<T> item);

	void removeItems();

	int getItemCount();

	void addSelectionListener(UISelectionListener listener);

	void removeSelectionListener(UISelectionListener listener);
}
