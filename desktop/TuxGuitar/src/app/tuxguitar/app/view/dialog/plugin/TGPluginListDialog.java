package app.tuxguitar.app.view.dialog.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.system.plugins.TGPluginSettingsManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UICheckTableSelectionEvent;
import app.tuxguitar.ui.event.UICheckTableSelectionListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.event.UIModifyEvent;
import app.tuxguitar.ui.event.UIModifyListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UICursor;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICheckTable;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UITableItem;
import app.tuxguitar.ui.widget.UITextField;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginInfo;
import app.tuxguitar.util.plugin.TGPluginManager;

public class TGPluginListDialog {

	private UITextField filterText;
	private static final float TABLE_WIDTH = 400;
	private static final float TABLE_HEIGHT = 300;
	private UICheckTable<String> table;

	public void show(final TGViewContext context) {
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("plugins"));

		this.filterText = uiFactory.createTextField(dialog);
		this.filterText.addModifyListener(new UIModifyListener() {
			@Override
			public void onModify(UIModifyEvent event) {
				TGPluginListDialog.this.updateTableItems(context.getContext());
			}
		});
		dialogLayout.set(filterText, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.table = uiFactory.createCheckTable(dialog, true);
		table.setColumns(2);
		table.setColumnName(0, TuxGuitar.getProperty("plugin.column.enabled"));
		table.setColumnName(1, TuxGuitar.getProperty("plugin.column.name"));

		dialogLayout.set(table, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		dialogLayout.set(table, UITableLayout.PACKED_WIDTH, TABLE_WIDTH);
		dialogLayout.set(table, UITableLayout.PACKED_HEIGHT, TABLE_HEIGHT);

		this.updateTableItems(context.getContext());

		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		final UIButton buttonSetup = uiFactory.createButton(buttons);
		buttonSetup.setText(TuxGuitar.getProperty("configure"));
		buttonSetup.setEnabled(false);
		buttonSetup.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				String moduleId = table.getSelectedValue();
				if( moduleId != null && isConfigurable(moduleId) ){
					configure(context.getContext(), dialog, moduleId);
				}
			}
		});
		buttonsLayout.set(buttonSetup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		final UIButton buttonInfo = uiFactory.createButton(buttons);
		buttonInfo.setText(TuxGuitar.getProperty("info"));
		buttonInfo.setDefaultButton();
		buttonInfo.setEnabled(false);
		buttonInfo.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				String moduleId = table.getSelectedValue();
				if( moduleId != null ){
					showInfo(context.getContext(), dialog, moduleId);
				}
			}
		});
		buttonsLayout.set(buttonInfo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		UIButton buttonClose = uiFactory.createButton(buttons);
		buttonClose.setText(TuxGuitar.getProperty("close"));
		buttonClose.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonClose, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonClose, UITableLayout.MARGIN_RIGHT, 0f);

		table.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				String moduleId = table.getSelectedValue();

				buttonInfo.setEnabled(moduleId != null);
				buttonSetup.setEnabled(moduleId != null && isConfigurable(moduleId));
			}
		});
		table.addCheckSelectionListener(new UICheckTableSelectionListener<String>() {
			public void onSelect(UICheckTableSelectionEvent<String> event) {
				if( event.getSelectedItem() != null ) {
					dialog.setCursor(UICursor.WAIT);

					UITableItem<String> item = event.getSelectedItem();
					TGPluginManager.getInstance(context.getContext()).updatePluginStatus(item.getValue(), table.isCheckedItem(item));
					table.setSelectedItem(item);

					dialog.setCursor(UICursor.NORMAL);
				}
			}
		});

		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	public void updateTableItems(TGContext context) {
		table.removeItems();

		Iterator<String> it = getModuleIds().iterator();
		while(it.hasNext()){
			String moduleId = (String)it.next();
			TGPluginInfo pluginInfo = new TGPluginInfo(context, moduleId);

			String pluginName = pluginInfo.getName();
			if( pluginName == null ){
				pluginName = moduleId;
			}

			UITableItem<String> item = new UITableItem<String>(moduleId);
			item.setText(1, (pluginName != null ? pluginName : "Undefined Plugin"));
			if (item.getText(1).toLowerCase().contains(this.filterText.getText().toLowerCase())) {
				table.addItem(item);
				table.setCheckedItem(item, TuxGuitar.getInstance().getPluginManager().isEnabled(moduleId));
			}
		}
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

	public void showInfo(TGContext context, UIWindow parent, String moduleId) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPluginInfoDialogController());
		tgActionProcessor.setAttribute(TGViewContext.ATTRIBUTE_PARENT, parent);
		tgActionProcessor.setAttribute(TGPluginInfoDialog.ATTRIBUTE_MODULE_ID, moduleId);
		tgActionProcessor.process();
	}

	public void configure(TGContext context, UIWindow parent, String moduleId) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGPluginSettingsDialogController());
		tgActionProcessor.setAttribute(TGViewContext.ATTRIBUTE_PARENT, parent);
		tgActionProcessor.setAttribute(TGPluginSettingsDialogController.ATTRIBUTE_MODULE_ID, moduleId);
		tgActionProcessor.process();
	}
}
