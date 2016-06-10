package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.herac.tuxguitar.ui.event.UICheckTableSelectionListener;
import org.herac.tuxguitar.ui.swt.event.SWTCheckTableSelectionListenerManager;
import org.herac.tuxguitar.ui.widget.UICheckTable;
import org.herac.tuxguitar.ui.widget.UITableItem;

public class SWTCheckTable<T> extends SWTTable<T> implements UICheckTable<T> {
	
	public static final int CHECK_TABLE_STYLE = (TABLE_STYLE | SWT.CHECK);
	
	private SWTCheckTableSelectionListenerManager<T> selectionListener;
	
	public SWTCheckTable(SWTContainer<? extends Composite> parent, boolean headerVisible) {
		super(parent, headerVisible, CHECK_TABLE_STYLE);
		
		this.selectionListener = new SWTCheckTableSelectionListenerManager<T>(this);
	}
	
	public boolean isCheckedValue(T value) {
		return this.isCheckedItem(new UITableItem<T>(value));
	}

	public boolean isCheckedItem(UITableItem<T> item) {
		TableItem tableItem = this.getTableItem(item);
		if( tableItem != null ) {
			return tableItem.getChecked();
		}
		return false;
	}

	public void setCheckedValue(T value, boolean checked) {
		this.setCheckedItem(new UITableItem<T>(value), checked);
	}

	public void setCheckedItem(UITableItem<T> item, boolean checked) {
		TableItem tableItem = this.getTableItem(item);
		if( tableItem != null ) {
			tableItem.setChecked(checked);
		}
	}
	
	public void addCheckSelectionListener(UICheckTableSelectionListener<T> listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().addSelectionListener(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeCheckSelectionListener(UICheckTableSelectionListener<T> listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().removeSelectionListener(this.selectionListener);
		}
	}
}
