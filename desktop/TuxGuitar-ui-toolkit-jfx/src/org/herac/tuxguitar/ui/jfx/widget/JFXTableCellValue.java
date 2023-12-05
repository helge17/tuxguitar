package org.herac.tuxguitar.ui.jfx.widget;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.widget.UITableItem;

public class JFXTableCellValue<T> extends JFXComponent<UITableItem<T>>{
	
	private Integer column;
	private BooleanProperty checked;
	
	public JFXTableCellValue(UITableItem<T> item, Integer column) {
		super(item);
		
		this.column = column;
		this.checked = new SimpleBooleanProperty(false);
	}
	
	public String getText() {
		return this.getControl().getText(this.column);
	}
	
	public UIImage getImage() {
		return (this.isFirstColumn() ? this.getControl().getImage() : null);
	}
	
	public boolean isFirstColumn() {
		return (this.column == 0);
	}
	
	public boolean isChecked() {
		return this.checked.get();
	}
	
	public void setChecked(boolean checked) {
		this.checked.set(checked);
	}
	
	public BooleanProperty checkedProperty() {
		return this.checked;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof JFXTableCellValue ) {
			return (this.hashCode() == obj.hashCode());
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return (JFXTableCellValue.class.getName() + "-" + this.getControl().hashCode() + "-" + this.column.hashCode()).hashCode();
	}
}
