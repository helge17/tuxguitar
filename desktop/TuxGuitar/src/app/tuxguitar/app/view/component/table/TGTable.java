package app.tuxguitar.app.view.component.table;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UIDivider;
import app.tuxguitar.ui.widget.UILayoutContainer;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.util.TGContext;

public class TGTable {

	private static final float DIVIDER_WIDTH = 2f;

	private final TGTableViewer viewer;
	private TGContext context;
	private UIPanel table;
	private UIPanel columnControl;
	private UIPanel rowControl;
	private TGTableHeaderLabel columnNumber;
	private TGTableHeaderLabel columnSoloMute;
	private TGTableHeaderLabel columnName;
	private TGTableHeaderLabel columnInstrument;
	private TGTableHeaderMeasures columnCanvas;
	private List<TGTableRow> rows;

	public TGTable(TGContext context, TGTableViewer viewer, UILayoutContainer parent){
		this.context = context;
		this.viewer = viewer;
		this.rows = new ArrayList<TGTableRow>();
		this.newTable(parent);
	}

	public void newTable(UILayoutContainer parent){
		UIFactory uiFactory = this.getUIFactory();

		this.table = uiFactory.createPanel(parent, false);

		this.columnControl = uiFactory.createPanel(this.table, false);

		this.columnNumber = new TGTableHeaderLabel(this);
		this.columnSoloMute = new TGTableHeaderLabel(this);
		this.columnName = new TGTableHeaderLabel(this);
		this.columnInstrument = new TGTableHeaderLabel(this);
		this.columnCanvas = new TGTableHeaderMeasures(this);

		this.rowControl = uiFactory.createPanel(this.table, false);
		this.rowControl.setLayout(new TGTableBodyLayout());

		this.createTableLayout();
		this.createColumnLayout();
	}

	public boolean rowsAreInitialized() {
		return ((TGTableBodyLayout) this.rowControl.getLayout()).isInitialized();
	}

	public UIPanel getControl(){
		return this.table;
	}

	public TGTableViewer getViewer() {
		return viewer;
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
		this.createColumnHeaderLayout(uiLayout, this.columnSoloMute, ++columnIndex, false, null);
		this.createColumnHeaderLayout(uiLayout, this.columnName, ++columnIndex, false, 250f);
		this.createColumnDividerLayout(uiLayout, dividerHelper.createDivider(this.columnName, this.columnInstrument, false), ++columnIndex);
		this.createColumnHeaderLayout(uiLayout, this.columnInstrument, ++columnIndex, false, 250f);
		this.createColumnDividerLayout(uiLayout, dividerHelper.createDivider(this.columnInstrument, this.columnCanvas, true), ++columnIndex);
		this.createColumnHeaderLayout(uiLayout, this.columnCanvas, ++columnIndex, true, null);

		this.columnControl.setLayout(uiLayout);
	}

	public void createColumnDividerLayout(UITableLayout uiLayout, UIDivider divider, int columnIndex) {
		uiLayout.set(divider, 1, columnIndex, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		uiLayout.set(divider, UITableLayout.PACKED_WIDTH, DIVIDER_WIDTH);
		uiLayout.set(divider, UITableLayout.MARGIN, 0f);
	}

	private void createColumnHeaderLayout(UITableLayout uiLayout, TGTableHeader column, int columnIndex, Boolean fillX, Float minimumPackedWidth) {
		uiLayout.set(column.getControl(), 1, columnIndex, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, fillX, false);
		uiLayout.set(column.getControl(), UITableLayout.MINIMUM_PACKED_WIDTH, minimumPackedWidth);
		uiLayout.set(column.getControl(), UITableLayout.MARGIN, 0f);
	}
	public void createRow(){
		this.rows.add(new TGTableRow(this));
	}

	public int getRowIndex(TGTableRow row) {
		return this.rows.indexOf(row);
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

	public TGTableHeaderLabel getColumnInstrument() {
		return this.columnInstrument;
	}

	public TGTableHeaderLabel getColumnName() {
		return this.columnName;
	}

	public TGTableHeaderLabel getColumnNumber() {
		return this.columnNumber;
	}

	public TGTableHeaderLabel getColumnSoloMute() {
		return this.columnSoloMute;
	}

	public TGTableHeaderMeasures getColumnCanvas() {
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
