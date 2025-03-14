package app.tuxguitar.ui.widget;

import app.tuxguitar.ui.event.UICheckTableSelectionListener;

public interface UICheckTable<T> extends UITable<T> {

	boolean isCheckedValue(T value);

	boolean isCheckedItem(UITableItem<T> item);

	void setCheckedValue(T value, boolean checked);

	void setCheckedItem(UITableItem<T> item, boolean checked);

	void addCheckSelectionListener(UICheckTableSelectionListener<T> listener);

	void removeCheckSelectionListener(UICheckTableSelectionListener<T> listener);
}
