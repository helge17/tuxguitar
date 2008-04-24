package org.herac.tuxguitar.gui.system.config.items;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.items.ToolItems;
import org.herac.tuxguitar.gui.system.config.TGConfigEditor;

public class ToolBarsOption extends Option{
	protected boolean initialized;
	
	protected Table table;
	protected TableColumn column;
	protected Button moveUp;
	protected Button moveDown;
	
	public ToolBarsOption(TGConfigEditor configEditor,ToolBar toolBar,final Composite parent){
		super(configEditor,toolBar,parent,TuxGuitar.getProperty("settings.config.toolbars"), SWT.FILL,SWT.FILL);
		this.initialized = false;
	}
	
	public void createOption() {
		getToolItem().setText(TuxGuitar.getProperty("settings.config.toolbars"));
		getToolItem().setImage(TuxGuitar.instance().getIconManager().getOptionToolbars());
		getToolItem().addSelectionListener(this);
		
		showLabel(getComposite(),SWT.FILL, SWT.TOP, true, false, SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.BOLD,0,TuxGuitar.getProperty("settings.config.toolbars.tip"));
		
		Composite composite = new Composite(getComposite(),SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(getTabbedData(SWT.FILL, SWT.FILL));
		
		this.table = new Table(composite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL);
		this.table.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(false);
		
		this.column = new TableColumn(this.table, SWT.LEFT);
		this.column.setText(TuxGuitar.getProperty("settings.config.toolbars.list"));
		this.column.pack();
		
		Composite buttons = new Composite(getComposite(), SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.BOTTOM,true,false));
		
		this.moveUp = new Button(buttons,SWT.PUSH);
		this.moveUp.setLayoutData(getButtonData());
		this.moveUp.setText(TuxGuitar.getProperty("settings.config.toolbars.move-up"));
		this.moveUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveUp();
			}
		});
		
		this.moveDown = new Button(buttons,SWT.PUSH);
		this.moveDown.setLayoutData(getButtonData());
		this.moveDown.setText(TuxGuitar.getProperty("settings.config.toolbars.move-down"));
		this.moveDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveDown();
			}
		});
		
		this.loadConfig();
	}
	
	protected GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected void moveUp(){
		if(this.initialized){
			int count = this.table.getItemCount();
			int index = this.table.getSelectionIndex();
			if(index > 0 && index < count){
				TableItem item1 = this.table.getItem(index);
				TableItem item2 = this.table.getItem(index - 1);
				this.swapItems(item1,item2);
				this.table.setSelection(index - 1);
			}
		}
	}
	
	protected void moveDown(){
		if(this.initialized){
			int count = this.table.getItemCount();
			int index = this.table.getSelectionIndex();
			if(index >= 0 && index < ( count - 1 ) ){
				TableItem item1 = this.table.getItem(index);
				TableItem item2 = this.table.getItem(index + 1);
				this.swapItems(item1,item2);
				this.table.setSelection(index + 1);
			}
		}
	}
	
	protected void swapItems(TableItem item1, TableItem item2){
		ToolItems data1 = (ToolItems)item1.getData();
		ToolItems data2 = (ToolItems)item2.getData();
		loadItem(item1, data2);
		loadItem(item2, data1);
	}
	
	protected void loadItem(TableItem item, ToolItems data){
		item.setText( TuxGuitar.getProperty( data.getName() ));
		item.setChecked( data.isEnabled() );
		item.setData( data );
	}
	
	protected void loadConfig(){
		new Thread(new Runnable() {
			public void run() {
				final ToolItems[] items = TuxGuitar.instance().getItemManager().getToolBars();
				new SyncThread(new Runnable() {
					public void run() {
						if(!isDisposed()){
							for(int i = 0;i < items.length; i ++){
								loadItem(new TableItem(ToolBarsOption.this.table, SWT.NONE), items[i]);
							}
							ToolBarsOption.this.initialized = true;
							ToolBarsOption.this.column.pack();
							ToolBarsOption.this.pack();
						}
					}
				}).start();
			}
		}).start();
	}
	
	public void updateConfig() {
		if(this.initialized){
			for( int i = 0 ; i < this.table.getItemCount() ; i ++){
				TableItem item = this.table.getItem( i );
				ToolItems data = (ToolItems)item.getData();
				TuxGuitar.instance().getItemManager().setToolBarStatus(data.getName(), item.getChecked() , i);
			}
			TuxGuitar.instance().getItemManager().writeToolBars();
		}
	}
	
	public void updateDefaults() {
		if(this.initialized){
			TuxGuitar.instance().getItemManager().setDefaultToolBars();
			TuxGuitar.instance().getItemManager().writeToolBars();
		}
	}
	
	public void applyConfig(boolean force){
		if(force || (this.initialized && TuxGuitar.instance().getItemManager().shouldReloadToolBars())){
			addSyncThread(new Runnable() {
				public void run() {
					TuxGuitar.instance().loadToolBars();
				}
			});
		}
	}
	
	public Point computeSize(){
		return this.computeSize(SWT.DEFAULT,SWT.NONE);
	}
}
