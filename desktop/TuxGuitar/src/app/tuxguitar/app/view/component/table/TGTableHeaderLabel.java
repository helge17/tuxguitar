package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;

public class TGTableHeaderLabel implements TGTableHeader {

	private UIPanel column;
	private UILabel label;

	public TGTableHeaderLabel(TGTable table){
		this.column = table.getUIFactory().createPanel(table.getColumnControl(), false);
		this.label = table.getUIFactory().createLabel(this.column);

		table.appendListeners(this.column);

		UITableLayout layout = new UITableLayout();
		layout.set(UITableLayout.MARGIN_TOP, 0f);
		layout.set(UITableLayout.MARGIN_BOTTOM, 0f);
		layout.set(this.label, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

		this.column.setLayout(layout);
	}

	public UIPanel getControl(){
		return this.column;
	}

	public void setTitle(String title){
		this.label.setText(title);
	}
}
