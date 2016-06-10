package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.ui.layout.UIAbstractLayout;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;

public class TGTableRowLayout extends UIAbstractLayout {
	
	private TGTableRow row;
	
	public TGTableRowLayout(TGTableRow row) {
		this.row = row;
	}
	
	public UISize computePackedSize(UILayoutContainer container) {
		float minimumHeight = 0;
		for(UIControl control : container.getChildren()) {
			minimumHeight = Math.max(minimumHeight, control.getPackedSize().getHeight());
		}
		return new UISize(0, minimumHeight);
	}
	
	public void setBounds(UILayoutContainer container, UIRectangle bounds) {
		float rowHeight = bounds.getHeight();
		
		this.setBounds(this.row.getTable().getColumnNumber(), this.row.getNumber(), rowHeight);
		this.setBounds(this.row.getTable().getColumnSoloMute(), this.row.getSoloMute(), rowHeight);
		this.setBounds(this.row.getTable().getColumnName(), this.row.getName(), rowHeight);
		this.setBounds(this.row.getTable().getColumnInstrument(), this.row.getInstrument(), rowHeight);
		this.setBounds(this.row.getTable().getColumnCanvas(), this.row.getPainter(), rowHeight);
	}
	
	public void setBounds(TGTableColumn column, TGTableRowCell cell, float rowHeight) {
		this.setBounds(column, cell.getControl(), rowHeight);
	}
	
	public void setBounds(TGTableColumn column, UIControl cell, float rowHeight) {
		UIRectangle bounds = column.getControl().getBounds();
		
		cell.setBounds(new UIRectangle(bounds.getX(), 0, bounds.getWidth(), rowHeight));
	}
}
