package org.herac.tuxguitar.app.view.dialog.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsManager;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginInfo;
import org.herac.tuxguitar.util.plugin.TGPluginManager;

public class TGPluginListDialog {
	
	private static final int TABLE_WIDTH = 400;
	private static final int TABLE_HEIGHT = 300;
	
	public void show(final TGViewContext context) {
		final Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
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
		
		Iterator<String> it = getModuleIds().iterator();
		while(it.hasNext()){
			String moduleId = (String)it.next();
			TGPluginInfo pluginInfo = new TGPluginInfo(context.getContext(), moduleId);
			
			String pluginName = pluginInfo.getName();
			if( pluginName == null ){
				pluginName = moduleId;
			}
			
			TableItem item = new TableItem(table, SWT.NONE);
			item.setData(moduleId);
			item.setText(1, (pluginName != null ? pluginName : "Undefined Plugin") );
			item.setChecked(TuxGuitar.getInstance().getPluginManager().isEnabled(moduleId));
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
				if( item.getData() instanceof String ){
					String moduleId = (String)item.getData();
					if( isConfigurable(moduleId) ){
						configure(context.getContext(), dialog, moduleId);
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
				if( item.getData() instanceof String ){
					showInfo(context.getContext(), dialog, (String)item.getData());
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
				if(event.item instanceof TableItem && event.item.getData() instanceof String){
					final TableItem item = (TableItem)event.item;
					if( event.detail == SWT.CHECK ){
						dialog.setCursor(dialog.getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
						
						TGPluginManager.getInstance(context.getContext()).updatePluginStatus((String)item.getData(), item.getChecked());
						table.setSelection(item);
						
						dialog.setCursor(dialog.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
					}
					buttonInfo.setEnabled(true);
					buttonSetup.setEnabled(isConfigurable((String)item.getData()));
				}
			}
		});
		
		dialog.setDefaultButton( buttonInfo );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	private List<String> getModuleIds(){
		List<String> moduleIds = new ArrayList<String>();
		Iterator<TGPlugin> it = TuxGuitar.getInstance().getPluginManager().getPlugins().iterator();
		while(it.hasNext()){
			TGPlugin plugin = (TGPlugin)it.next();
			if(!moduleIds.contains( plugin.getModuleId() )){
				moduleIds.add( plugin.getModuleId() );
			}
		}
		return moduleIds;
	}
	
	private boolean isConfigurable(String moduleId){
		return TGPluginSettingsManager.getInstance().containsPluginSettingsHandler(moduleId);
	}
	
	public void showInfo(TGContext context, Shell parent, String moduleId) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPluginInfoDialogController());
		tgActionProcessor.setAttribute(TGViewContext.ATTRIBUTE_PARENT, parent);
		tgActionProcessor.setAttribute(TGPluginInfoDialog.ATTRIBUTE_MODULE_ID, moduleId);
		tgActionProcessor.process();
	}
	
	public void configure(TGContext context, Shell parent, String moduleId) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPluginSettingsDialogController());
		tgActionProcessor.setAttribute(TGViewContext.ATTRIBUTE_PARENT, parent);
		tgActionProcessor.setAttribute(TGPluginSettingsDialogController.ATTRIBUTE_MODULE_ID, moduleId);
		tgActionProcessor.process();
	}
}
