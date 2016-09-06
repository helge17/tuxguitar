package org.herac.tuxguitar.app.view.component.table;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIDivider;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.util.TGContext;

public class TGTable {
	
	private TGContext context;
	private UIPanel table;
	private UIPanel columnControl;
	private UIPanel rowControl;
	private TGTableColumn columnNumber;
	private TGTableColumn columnSoloMute;
	private TGTableColumn columnName;
	private TGTableColumn columnInstrument;
	private TGTableColumn columnCanvas;
	private List<TGTableRow> rows;
	
	public TGTable(TGContext context, UILayoutContainer parent){
		this.context = context;
		this.rows = new ArrayList<TGTableRow>();
		this.newTable(parent);
	}
	
	public void newTable(UILayoutContainer parent){
		UIFactory uiFactory = this.getUIFactory();
		
		this.table = uiFactory.createPanel(parent, false);
		
		this.columnControl = uiFactory.createPanel(this.table, false);
		
		this.columnNumber = new TGTableColumn(this);
		this.columnSoloMute = new TGTableColumn(this);
		this.columnName = new TGTableColumn(this);
		this.columnInstrument = new TGTableColumn(this);
		this.columnCanvas = new TGTableColumn(this);
		
		this.rowControl = uiFactory.createPanel(this.table, false);
		this.rowControl.setLayout(new TGTableBodyLayout());
		
		this.createTableLayout();
		this.createColumnLayout();
	}
	
	public UIPanel getControl(){
		return this.table;
	}	

	public void createTableLayout() {
		UITableLayout uiLayout = new UITableLayout(0f);
		uiLayout.set(this.columnControl, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
		uiLayout.set(this.rowControl, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 1, null, null, 0f);
		
		this.table.setLayout(uiLayout);
	}
	
	public void createColumnLayout() {
		TGTableDividerHelper dividerHelper = new TGTableDividerHelper(this);
		UITableLayout uiLayout = new UITableLayout(0f);
		
		int columnIndex = 0;
		this.createColumnHeaderLayout(uiLayout, this.columnNumber, ++columnIndex, false, null);
		this.createColumnDividerLayout(uiLayout, dividerHelper.createDivider(this.columnNumber, this.columnSoloMute), ++columnIndex);
		this.createColumnHeaderLayout(uiLayout, this.columnSoloMute, ++columnIndex, false, null);
		this.createColumnDividerLayout(uiLayout, dividerHelper.createDivider(this.columnSoloMute, this.columnName), ++columnIndex);
		this.createColumnHeaderLayout(uiLayout, this.columnName, ++columnIndex, false, 250f);
		this.createColumnDividerLayout(uiLayout, dividerHelper.createDivider(this.columnName, this.columnInstrument), ++columnIndex);
		this.createColumnHeaderLayout(uiLayout, this.columnInstrument, ++columnIndex, false, 250f);
		this.createColumnDividerLayout(uiLayout, dividerHelper.createDivider(this.columnInstrument, this.columnCanvas), ++columnIndex);
		this.createColumnHeaderLayout(uiLayout, this.columnCanvas, ++columnIndex, true, null);
		
		this.columnControl.setLayout(uiLayout);
	}
	
	public void createColumnDividerLayout(UITableLayout uiLayout, UIDivider divider, int columnIndex) {
		uiLayout.set(divider, 1, columnIndex, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		uiLayout.set(divider, UITableLayout.PACKED_WIDTH, 2f);
		uiLayout.set(divider, UITableLayout.MARGIN, 0f);
	}
	
	public void createColumnHeaderLayout(UITableLayout uiLayout, TGTableColumn column, int columnIndex, Boolean fillX, Float minimumPackedWidth) {
		uiLayout.set(column.getControl(), 1, columnIndex, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, fillX, false);
		uiLayout.set(column.getControl(), UITableLayout.MINIMUM_PACKED_WIDTH, minimumPackedWidth);
		uiLayout.set(column.getControl(), UITableLayout.MARGIN, 0f);
	}
	
	public void createRow(){
		this.rows.add(new TGTableRow(this));
	}
	
	public float getRowHeight(){
		return ((TGTableBodyLayout) this.rowControl.getLayout()).getRowHeight();
	}
	
	public float getMinHeight(){
		return this.table.getPackedSize().getHeight();
	}
	
	public UIPanel getColumnControl(){
		return this.columnControl;
	}
	
	public UIPanel getRowControl(){
		return this.rowControl;
	}
	
	public TGTableColumn getColumnInstrument() {
		return this.columnInstrument;
	}
	
	public TGTableColumn getColumnName() {
		return this.columnName;
	}
	
	public TGTableColumn getColumnNumber() {
		return this.columnNumber;
	}
	
	public TGTableColumn getColumnSoloMute() {
		return this.columnSoloMute;
	}
	
	public TGTableColumn getColumnCanvas() {
		return this.columnCanvas;
	}
	
	public TGTableRow getRow(int index){
		if(index >= 0 && index < this.rows.size()){
			return (TGTableRow)this.rows.get(index);
		}
		return null;
	}
	
	public void removeRowsAfter(int index){
		while(index < this.rows.size()){
			TGTableRow row = (TGTableRow)this.rows.get(index);
			row.dispose();
			this.rows.remove(index);
		}
	}
	
	public int getRowCount(){
		return this.rows.size();
	}
	
	public void appendListeners(UIControl control){
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(control);
	}
	
	public void update(){
		this.table.layout();
	}
	
	public TGContext getContext() {
		return context;
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
}
