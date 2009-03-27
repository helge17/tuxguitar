/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.settings;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.system.plugins.TGPlugin;
import org.herac.tuxguitar.gui.system.plugins.TGPluginSetup;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EditPluginsAction extends Action{
	public static final String NAME = "action.settings.plugins";
	
	private static final int TABLE_WIDTH = 400;
	private static final int TABLE_HEIGHT = 300;
	
	public EditPluginsAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE);
	}
	
	protected int execute(TypedEvent e){
		showDialog();
		return 0;
	}
	
	public void showDialog() {
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		dialog.setText(TuxGuitar.getProperty("plugins"));
		
		final Table table = new Table(dialog, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL);
		table.setLayoutData(new GridData(TABLE_WIDTH,TABLE_HEIGHT));
		table.setHeaderVisible(true);
		
		final TableColumn columnEnabled = new TableColumn(table, SWT.LEFT);
		final TableColumn columnPlugin = new TableColumn(table, SWT.LEFT);
		columnEnabled.setText(TuxGuitar.getProperty("plugin.column.enabled"));
		columnPlugin.setText(TuxGuitar.getProperty("plugin.column.name"));
		columnEnabled.setWidth( (TABLE_WIDTH / 4) );
		columnPlugin.setWidth( (TABLE_WIDTH - (TABLE_WIDTH / 4)) );
		
		Iterator it = TuxGuitar.instance().getPluginManager().getPlugins().iterator();
		while(it.hasNext()){
			TGPlugin plugin = (TGPlugin)it.next();
			TableItem item = new TableItem(table, SWT.NONE);
			item.setData(plugin);
			item.setText(1,((plugin.getName() != null)?plugin.getName():"Undefined Plugin"));
			item.setChecked(TuxGuitar.instance().getPluginManager().isEnabled(plugin));
		}
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(3,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		final Button buttonSetup = new Button(buttons, SWT.PUSH);
		buttonSetup.setText(TuxGuitar.getProperty("configure"));
		buttonSetup.setLayoutData(getButtonData());
		buttonSetup.setEnabled(false);
		buttonSetup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TableItem item = table.getItem(table.getSelectionIndex());
				if(item != null && item.getData() instanceof TGPluginSetup){
					try {
						((TGPluginSetup)item.getData()).setupDialog(dialog);
					}catch(Throwable throwable){
						MessageDialog.errorMessage(dialog, throwable);
					}
				}
			}
		});
		
		final Button buttonInfo = new Button(buttons, SWT.PUSH);
		buttonInfo.setText(TuxGuitar.getProperty("info"));
		buttonInfo.setLayoutData(getButtonData());
		buttonInfo.setEnabled(false);
		buttonInfo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TableItem item = table.getItem(table.getSelectionIndex());
				if(item != null && item.getData() instanceof TGPlugin){
					try {
						showInfo(dialog,(TGPlugin)item.getData());
					}catch(Throwable throwable){
						MessageDialog.errorMessage(dialog, throwable);
					}
				}
			}
		});
		
		Button buttonClose = new Button(buttons, SWT.PUSH);
		buttonClose.setText(TuxGuitar.getProperty("close"));
		buttonClose.setLayoutData(getButtonData());
		buttonClose.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		table.addListener (SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				buttonInfo.setEnabled(false);
				buttonSetup.setEnabled(false);
				if(event.item instanceof TableItem && event.item.getData() instanceof TGPlugin){
					final TableItem item = (TableItem)event.item;
					if(event.detail == SWT.CHECK){
						TuxGuitar.instance().loadCursor(dialog,SWT.CURSOR_WAIT);
						TuxGuitar.instance().getPluginManager().setEnabled((TGPlugin)item.getData(),item.getChecked());
						TuxGuitar.instance().loadCursor(dialog,SWT.CURSOR_ARROW);
						table.setSelection(item);
					}
					buttonInfo.setEnabled(true);
					buttonSetup.setEnabled((item.getData() instanceof TGPluginSetup));
				}
			}
		});
		
		dialog.setDefaultButton( buttonInfo );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	public void showInfo(Shell parent,TGPlugin plugin) {
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		dialog.setText(TuxGuitar.getProperty("plugins"));
		
		Composite info = new Composite(dialog,SWT.NONE);
		info.setLayout(new GridLayout(2,false));
		
		showInfoString(info,TuxGuitar.getProperty("name") + ":",plugin.getName());
		showInfoString(info,TuxGuitar.getProperty("version") + ":",plugin.getVersion());
		showInfoString(info,TuxGuitar.getProperty("author") + ":",plugin.getAuthor());
		showInfoString(info,TuxGuitar.getProperty("description") + ":",plugin.getDescription());
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout());
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		Button buttonExit = new Button(buttons, SWT.PUSH);
		buttonExit.setText(TuxGuitar.getProperty("exit"));
		buttonExit.setLayoutData(getButtonData());
		buttonExit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonExit );
		
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	private void showInfoString(Composite parent,String key,String value){
		Label labelKey = new Label(parent,SWT.LEFT);
		Label labelValue = new Label(parent,SWT.LEFT | SWT.WRAP);
		labelKey.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,true));
		labelValue.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		setBold(labelKey);
		labelKey.setText(key);
		labelValue.setText( (value != null && value.length() > 0)?value:TuxGuitar.getProperty("plugin.unknown-value"));
	}
	
	private void setBold(Label label){
		FontData[] fontDatas = label.getFont().getFontData();
		if(fontDatas.length > 0){
			final Font font = new Font(label.getDisplay(),fontDatas[0].getName(),(fontDatas[0].getHeight()),SWT.BOLD);
			label.setFont(font);
			label.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					font.dispose();
				}
			});
		}
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
}
