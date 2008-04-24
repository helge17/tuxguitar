package org.herac.tuxguitar.gui.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class TGTable {
	private ScrolledComposite sComposite;
	private Composite table;
	private SashForm columnControl;
	private Composite rowControl;
	private TGTableColumn columnNumber;
	private TGTableColumn columnName;
	private TGTableColumn columnInstrument;
	private TGTableColumn columnCanvas;
	private List rows;
	private int rowHeight;
	private int scrollIncrement;
	
	public TGTable(Composite parent){
		this.rows = new ArrayList();
		this.newTable(parent);
	}
	
	public void newTable(Composite parent){
		this.sComposite = new ScrolledComposite(parent,SWT.BORDER | SWT.V_SCROLL);
		this.sComposite.setLayout(new GridLayout());
		this.sComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.sComposite.setAlwaysShowScrollBars(true);
		this.sComposite.setExpandHorizontal(true);
		this.sComposite.setExpandVertical(true);
		this.table = new Composite(this.sComposite,SWT.NONE);
		this.table.setLayout(newGridLayout(1,0,0,0,0));
		this.table.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.columnControl = new SashForm(this.table,SWT.HORIZONTAL);
		this.columnControl.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		
		this.columnNumber = new TGTableColumn(this,SWT.LEFT);
		this.columnName = new TGTableColumn(this,SWT.LEFT);
		this.columnInstrument = new TGTableColumn(this,SWT.LEFT);
		this.columnCanvas = new TGTableColumn(this,SWT.CENTER);
		this.columnControl.setWeights(new int[]{1,7,7,20});
		
		this.rowControl = new Composite(this.table,SWT.NONE);
		this.rowControl.setLayout(newGridLayout(1,0,1,0,1));
		this.rowControl.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.sComposite.setContent(this.table);
	}
	
	public Composite getControl(){
		return this.table;
	}
	
	public void newRow(){
		this.rows.add(new TGTableRow(this));
	}
	
	private GridLayout newGridLayout(int cols,int marginWidth,int marginHeight,int horizontalSpacing,int verticalSpacing){
		GridLayout layout = new GridLayout(cols,false);
		layout.marginWidth = marginWidth;
		layout.marginHeight = marginHeight;
		layout.horizontalSpacing = horizontalSpacing;
		layout.verticalSpacing = verticalSpacing;
		return layout;
	}
	
	public void addRowItem(TGTableColumn column,Control control,boolean computeSize){
		if(computeSize){
			this.rowHeight = Math.max(this.rowHeight,control.computeSize(SWT.DEFAULT,SWT.DEFAULT).y);
			this.scrollIncrement = this.rowHeight;
		}
		column.addControl(control);
	}
	
	public int getMinHeight(){
		return (this.sComposite.getMinHeight() + ( this.sComposite.getBorderWidth() * 2 ) );
	}
	
	public Composite getColumnControl(){
		return this.columnControl;
	}
	
	public Composite getRowControl(){
		return this.rowControl;
	}
	
	public int getRowHeight(){
		return this.rowHeight;
	}
	
	public int getScrollIncrement(){
		return this.scrollIncrement;
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
		this.notifyRemoved();
	}
	
	public int getRowCount(){
		return this.rows.size();
	}
	
	public void update(){
		this.layoutColumns();
		this.table.layout(true,true);
		this.sComposite.setMinHeight(this.table.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		this.sComposite.getVerticalBar().setIncrement( (getScrollIncrement() + this.sComposite.getBorderWidth() ) );
	}
	
	private void notifyRemoved(){
		this.columnNumber.notifyRemoved();
		this.columnName.notifyRemoved();
		this.columnInstrument.notifyRemoved();
		this.columnCanvas.notifyRemoved();
	}
	
	private void layoutColumns(){
		this.columnNumber.layout();
		this.columnName.layout();
		this.columnInstrument.layout();
		this.columnCanvas.layout();
	}
	
}
