package app.tuxguitar.app.view.component.table;

import app.tuxguitar.ui.layout.UIAbstractLayout;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UILayoutContainer;

public class TGTableRowLayout extends UIAbstractLayout {

	private TGTableRow row;

	public TGTableRowLayout(TGTableRow row) {
		this.row = row;
	}

	public UISize getComputedPackedSize(UILayoutContainer container) {
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

	public void setBounds(TGTableHeader column, TGTableRowCell cell, float rowHeight) {
		this.setBounds(column, cell.getControl(), rowHeight);
	}

	public void setBounds(TGTableHeader column, UIControl cell, float rowHeight) {
		UIRectangle bounds = column.getControl().getBounds();

		cell.setBounds(new UIRectangle(bounds.getX(), 0, bounds.getWidth(), rowHeight));
	}
}
