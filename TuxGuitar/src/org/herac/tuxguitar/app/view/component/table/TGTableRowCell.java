package org.herac.tuxguitar.app.view.component.table;

import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListener;
import org.herac.tuxguitar.ui.event.UIMouseDownListener;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;

public class TGTableRowCell {
	
	private TGTableRow row;
	private UIPanel cell;
	private UILabel label;

	public TGTableRowCell(TGTableRow row){
		this.row = row;
		this.cell = this.row.getTable().getUIFactory().createPanel(this.row.getControl(), false);
		this.label = this.row.getTable().getUIFactory().createLabel(this.cell);
		
		this.row.getTable().appendListeners(this.cell);
		this.row.getTable().appendListeners(this.label);
		
		this.createLayout();
	}
	
	public void createLayout() {
		UITableLayout uiTableLayout = new UITableLayout();
		uiTableLayout.set(UITableLayout.MARGIN_TOP, 2f);
		uiTableLayout.set(UITableLayout.MARGIN_BOTTOM, 2f);
		uiTableLayout.set(this.label, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		this.cell.setLayout(uiTableLayout);
	}
	
	public void setBgColor(UIColor background){
		this.cell.setBgColor(background);
		this.label.setBgColor(background);
	}
	
	public void setFgColor(UIColor foreground){
		this.cell.setFgColor(foreground);
		this.label.setFgColor(foreground);
	}
	
	public void setMenu(UIPopupMenu menu) {
		this.cell.setPopupMenu(menu);
		this.label.setPopupMenu(menu);
	}
	
	public void setText(String text) {
		this.label.setText(text);
	}
	
	public void setData(String key, Object data) {
		this.cell.setData(key, data);
	}
	
	public <T> T getData(String key) {
		return this.cell.getData(key);
	}
	
	public void addMouseDownListener(UIMouseDownListener listener) {
		this.cell.addMouseDownListener(listener);
		this.label.addMouseDownListener(listener);
	}
	
	public void addMouseUpListener(UIMouseUpListener listener) {
		this.cell.addMouseUpListener(listener);
		this.label.addMouseUpListener(listener);
	}
	
	public void addMouseDoubleClickListener(UIMouseDoubleClickListener listener) {
		this.cell.addMouseDoubleClickListener(listener);
		this.label.addMouseDoubleClickListener(listener);
	}
	
	public UIControl getControl() {
		return this.cell;
	}
	
	public UILabel getLabel() {
		return label;
	}
	
	public void dispose(){
		this.row.dispose();
	}
}
