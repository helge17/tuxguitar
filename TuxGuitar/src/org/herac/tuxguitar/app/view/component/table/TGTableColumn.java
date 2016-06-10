package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;

public class TGTableColumn {
	
	private TGTable table;
	private UIPanel column;
	private UILabel label;
	
	public TGTableColumn(TGTable table){
		this.table = table;
		this.column = this.table.getUIFactory().createPanel(this.table.getColumnControl(), true);
		this.label = this.table.getUIFactory().createLabel(this.column);
		
		this.table.appendListeners(this.column);
		
		this.createLayout();
	}
	
	public void createLayout() {
		UITableLayout uiTableLayout = new UITableLayout();
		uiTableLayout.set(UITableLayout.MARGIN_TOP, 2f);
		uiTableLayout.set(UITableLayout.MARGIN_BOTTOM, 2f);
		uiTableLayout.set(this.label, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		this.column.setLayout(uiTableLayout);
	}
	
	public UIControl getControl(){
		return this.column;
	}
	
	public UILabel getLabel(){
		return this.label;
	}
	
	public void setTitle(String title){
		this.label.setText(title);
	}
}
