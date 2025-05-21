package app.tuxguitar.app.view.dialog.toolbar;

import java.util.Collections;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.toolbar.main.TGMainToolBar;
import app.tuxguitar.app.view.toolbar.main.TGMainToolBarConfig;
import app.tuxguitar.app.view.toolbar.main.TGMainToolBarConfigManager;
import app.tuxguitar.app.view.toolbar.main.TGMainToolBarConfigMap;
import app.tuxguitar.app.view.toolbar.main.TGMainToolBarItem;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UITable;
import app.tuxguitar.ui.widget.UITableItem;
import app.tuxguitar.ui.widget.UITextField;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGMainToolBarDialog {

	private static final float MINIMUM_BUTTON_WIDTH = 80;

	private TGContext context;
	private UIWindow dialog;
	private TGIconManager iconManager;
	private TGMainToolBarConfigManager configMgr;
	private TGMainToolBarConfigMap configMap;
	private TGMainToolBarConfig config;
	private TGMainToolBar mainToolBar;
	private UIDropDownSelect<Integer> selectToolbarName;
	private UITextField newToolBarName;
	private UIDropDownSelect<String> selectGroup;
	private UITable<Integer> tableControls;
	private UITable<Integer> tableAreaControls;
	private UIDropDownSelect<Integer> selectArea;
	private boolean modified;
	private UIButton saveButton;
	private UIButton saveAsButton;
	private UIButton deleteButton;
	private UIButton leftButton;
	private UIButton rightButton;
	private UIButton upButton;
	private UIButton downButton;

	public TGMainToolBarDialog() {
		this.configMap = new TGMainToolBarConfigMap();
		this.configMgr = new TGMainToolBarConfigManager();
		this.iconManager = TuxGuitar.getInstance().getIconManager();
	}

	public void show(final TGViewContext context) {
		this.context = context.getContext();
		this.mainToolBar = TGMainToolBar.getInstance(this.context);
		this.config = this.mainToolBar.getConfig().clone();

		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		this.dialog = uiFactory.createWindow(uiParent, true, false);

		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("toolbar.settings"));

		// ------- header ------
		UIPanel header = uiFactory.createPanel(this.dialog, true);
		UITableLayout headerLayout = new UITableLayout();
		header.setLayout(headerLayout);
		this.initHeader(header, headerLayout, uiFactory);
		dialogLayout.set(header, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		// ---- main area: controls -----
		UIPanel controlsPanel = uiFactory.createPanel(this.dialog, true);
		UITableLayout controlsLayout = new UITableLayout();
		controlsPanel.setLayout(controlsLayout);
		this.initControls(controlsPanel, controlsLayout, uiFactory);
		dialogLayout.set(controlsPanel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		// ------- buttons -----
		UIPanel buttonsPanel = uiFactory.createPanel(this.dialog, false);
		UITableLayout buttonsLayout = new UITableLayout();
		buttonsPanel.setLayout(buttonsLayout);
		this.initButtons(buttonsPanel, buttonsLayout, uiFactory);
		dialogLayout.set(buttonsPanel, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		this.fillToolBarNames();
		this.updateButtons();
		this.addDropDownSelectionListeners();

		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	private void initHeader(UIPanel panel, UITableLayout layout, UIFactory uiFactory) {
		UILabel toolbarNameLabel = uiFactory.createLabel(panel);
		toolbarNameLabel.setText(TuxGuitar.getProperty("toolbar.settings.toolbar-name") + ":");
		layout.set(toolbarNameLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);
		this.selectToolbarName = uiFactory.createDropDownSelect(panel);
		layout.set(this.selectToolbarName, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.saveButton = uiFactory.createButton(panel);
		this.saveButton.setImage(this.iconManager.getImageByName(TGIconManager.FILE_SAVE));
		this.saveButton.setToolTipText(TuxGuitar.getProperty("file.save"));
		this.saveButton.setEnabled(false);
		this.saveButton.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.save();
			}
		});
		layout.set(this.saveButton, 1, 3, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, false);
		this.deleteButton = uiFactory.createButton(panel);
		this.deleteButton.setImage(this.iconManager.getImageByName(TGIconManager.LIST_REMOVE));
		this.deleteButton.setToolTipText(TuxGuitar.getProperty("toolbar.settings.delete"));
		this.deleteButton.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.delete();
			}
		});
		layout.set(this.deleteButton, 1, 4, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, false);

		this.newToolBarName = uiFactory.createTextField(panel);
		layout.set(this.newToolBarName, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.saveAsButton = uiFactory.createButton(panel);
		this.saveAsButton.setToolTipText(TuxGuitar.getProperty("file.save-as"));
		this.saveAsButton.setImage(this.iconManager.getImageByName(TGIconManager.FILE_SAVE_AS));
		this.saveAsButton.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.saveAs();
			}
		});
		layout.set(this.saveAsButton, 2, 3, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, false);
	}

	private void initControls(UIPanel panel, UITableLayout layout, UIFactory uiFactory) {
		this.tableAreaControls = uiFactory.createTable(panel, false);
		this.tableAreaControls.setColumns(1);

		boolean first = true;
		this.selectGroup = uiFactory.createDropDownSelect(panel);
		for (String groupName : this.configMap.getToolBarGroupsNames()) {
			String displayed = groupName == "" ? "" : TuxGuitar.getProperty(groupName);	// to avoid a warning
			UISelectItem<String> item = new UISelectItem<String>(displayed, groupName);
			this.selectGroup.addItem(item);
			if (first) {
				this.selectGroup.setSelectedItem(item);
				first = false;
			}
		}
		layout.set(this.selectGroup, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, true, true);

		this.selectArea = uiFactory.createDropDownSelect(panel);
		// fill list of areas
		first = true;
		for (int i = 0; i < TGMainToolBar.AREAS.length; i++) {
			int areaIndex = TGMainToolBar.AREAS[i];
			String areaName = TGMainToolBar.AREA_NAMES[i];
			UISelectItem<Integer> item = new UISelectItem<Integer>(TuxGuitar.getProperty(areaName), areaIndex);
			this.selectArea.addItem(item);
			if (first) {
				this.selectArea.setSelectedItem(item);
				first = false;
			}
		}
		layout.set(this.selectArea, 1, 3, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, true, true);

		this.tableControls = uiFactory.createTable(panel, false);
		this.tableControls.setColumns(1);
		this.tableControls.setColumnName(0, TuxGuitar.getProperty("toolbar.settings.controls"));
		this.fillControlsList();
		Float controlsListsWidth = 250f;
		layout.set(this.tableControls, UITableLayout.PACKED_WIDTH, controlsListsWidth);
		layout.set(this.tableControls, UITableLayout.PACKED_HEIGHT, 450f);
		layout.set(this.tableControls, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, true, true);

		UIPanel leftRightPanel = uiFactory.createPanel(panel, false);
		UITableLayout leftRightLayout = new UITableLayout();
		leftRightPanel.setLayout(leftRightLayout);
		this.leftButton = uiFactory.createButton(leftRightPanel);
		this.leftButton.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.removeControl();
			}
		});
		this.leftButton.setImage(iconManager.getImageByName(TGIconManager.ARROW_LEFT));
		leftRightLayout.set(this.leftButton, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_BOTTOM, true, true);
		this.rightButton = uiFactory.createButton(leftRightPanel);
		this.rightButton.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.addControl();
			}
		});
		this.rightButton.setImage(iconManager.getImageByName(TGIconManager.ARROW_RIGHT));
		leftRightLayout.set(this.rightButton, 2, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_TOP, true, true);
		layout.set(leftRightPanel, 2, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, true, true);

		this.tableAreaControls.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.updateButtons();
			}
		});
		layout.set(this.tableAreaControls, UITableLayout.PACKED_WIDTH, controlsListsWidth);
		layout.set(this.tableAreaControls, UITableLayout.PACKED_HEIGHT, 450f);
		layout.set(this.tableAreaControls, 2, 3, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, true, true);

		UIPanel upDownPanel = uiFactory.createPanel(panel, false);
		UITableLayout upDownLayout = new UITableLayout();
		upDownPanel.setLayout(upDownLayout);
		this.upButton = uiFactory.createButton(upDownPanel);
		this.upButton.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.moveControl(true);
			}
		});
		this.upButton.setImage(iconManager.getImageByName(TGIconManager.ARROW_UP));
		upDownLayout.set(this.upButton, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_BOTTOM, true, true);
		this.downButton = uiFactory.createButton(upDownPanel);
		this.downButton.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.moveControl(false);
			}
		});
		this.downButton.setImage(iconManager.getImageByName(TGIconManager.ARROW_DOWN));
		upDownLayout.set(this.downButton, 2, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_TOP, true, true);
		layout.set(upDownPanel, 2, 4, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, true, true);
	}

	private void initButtons(UIPanel panel, UITableLayout layout, UIFactory uiFactory) {
		UIButton buttonDefaults = uiFactory.createButton(panel);
		buttonDefaults.setText(TuxGuitar.getProperty("defaults"));
		buttonDefaults.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.setDefaults();
			}
		});
		layout.set(buttonDefaults, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);
		layout.set(buttonDefaults, UITableLayout.MINIMUM_PACKED_WIDTH, MINIMUM_BUTTON_WIDTH);

		UIButton buttonApply = uiFactory.createButton(panel);
		buttonApply.setText(TuxGuitar.getProperty("apply"));
		buttonApply.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.apply();
			}
		});
		layout.set(buttonApply, 1, 2, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);
		layout.set(buttonApply, UITableLayout.MINIMUM_PACKED_WIDTH, MINIMUM_BUTTON_WIDTH);

		UIButton buttonOK = uiFactory.createButton(panel);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.OK();
			}
		});
		layout.set(buttonOK, 1, 3, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);
		layout.set(buttonOK, UITableLayout.MINIMUM_PACKED_WIDTH, MINIMUM_BUTTON_WIDTH);

		UIButton buttonCancel = uiFactory.createButton(panel);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.dialog.dispose();
			}
		});
		layout.set(buttonCancel, 1, 4, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);
		layout.set(buttonCancel, UITableLayout.MINIMUM_PACKED_WIDTH, MINIMUM_BUTTON_WIDTH);
	}

	private void addDropDownSelectionListeners() {
		this.selectGroup.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.fillControlsList();
			}
		});
		this.selectArea.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.fillAreaControlsList();
			}
		});
		this.selectToolbarName.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGMainToolBarDialog.this.chooseToolBar();
			}
		});
	}

	private void chooseToolBar() {
		int toolBarIndex = this.selectToolbarName.getSelectedValue();
		String toolBarName = this.configMgr.getConfigNames().get(toolBarIndex);
		TGMainToolBarConfig config = TGMainToolBarDialog.this.configMgr.getConfig(toolBarName).clone();
		if (config != null) {
			this.modified = false;
			this.config = config;
			this.fillAreaControlsList();
		}
	}

	private void fillToolBarNames() {
		this.selectToolbarName.removeItems();
		Integer index = 0;
		for (String toolBarName : configMgr.getConfigNames()) {
			String displayedToolBarName = (toolBarName.equals("")
					? TuxGuitar.getProperty("toolbar.settings.default-maintoolbar-name")
					: toolBarName);
			UISelectItem<Integer> item = new UISelectItem<Integer>(displayedToolBarName, index);
			index++;
			this.selectToolbarName.addItem(item);
			if (toolBarName.equals(this.config.getName())) {
				this.selectToolbarName.setSelectedItem(item);
				this.fillAreaControlsList(this.selectArea.getSelectedValue());
				this.updateButtons();
			}
		}
	}

	private void fillControlsList() {
		this.tableControls.removeItems();
		String group = this.selectGroup.getSelectedValue();
		List<String> controlNames = this.configMap.getToolBarItemNames();
		for (int i = 0; i < controlNames.size(); i++) {
			String controlName = controlNames.get(i);
			TGMainToolBarItem toolBarItem = this.configMap.getToolBarItem(controlName);
			if (group.equals("") || toolBarItem.getGroupName().equals("") || group.equals(toolBarItem.getGroupName())) {
				UITableItem<Integer> uiTableItem = new UITableItem<Integer>(i);
				uiTableItem.setText(0, TuxGuitar.getProperty(controlName));
				uiTableItem.setImage(iconManager.getImageByName(toolBarItem.getIconFileName()));
				this.tableControls.addItem(uiTableItem);
				if (i == 0) {
					this.tableControls.setSelectedItem(uiTableItem);
				}
			}
		}
	}

	private void fillAreaControlsList() {
		int areaIndex = this.selectArea.getSelectedValue();
		this.fillAreaControlsList(areaIndex);
	}

	private void fillAreaControlsList(int areaIndex) {
		this.fillAreaControlsList(areaIndex, 0);
	}

	private void fillAreaControlsList(int areaIndex, int selectedIndex) {
		this.tableAreaControls.removeItems();
		List<String> areaContent = this.config.getAreaContent(areaIndex);
		for (Integer i = 0; i < areaContent.size(); i++) {
			String areaControlName = areaContent.get(i);
			TGMainToolBarItem areaItem = this.configMap.getToolBarItem(areaControlName);
			if (areaItem != null) {
				UITableItem<Integer> uiTableItem = new UITableItem<Integer>(i);
				uiTableItem.setText(0, TuxGuitar.getProperty(areaControlName));
				if (areaItem.getIconFileName() != null) {
					uiTableItem.setImage(iconManager.getImageByName(areaItem.getIconFileName()));
				}
				this.tableAreaControls.addItem(uiTableItem);
				if (i == selectedIndex) {
					this.tableAreaControls.setSelectedItem(uiTableItem);
				}
			}
		}
		this.updateButtons();
	}

	private void updateButtons() {
		this.newToolBarName.setEnabled(true);
		String selectedToolBarName = this.configMgr.getConfigNames().get(this.selectToolbarName.getSelectedValue());
		if (this.newToolBarName.getText().equals("")) {
			this.setUntitledToolBarName();
		}
		this.saveButton.setEnabled(this.modified && !selectedToolBarName.equals(""));
		this.saveAsButton.setEnabled(true);
		this.deleteButton.setEnabled(!this.config.getName().equals(""));

		Integer areaIndex = this.selectArea.getSelectedValue();
		Integer areaControlIndex = this.tableAreaControls.getSelectedValue();
		int nbAreaControls = this.config.getAreaContent(areaIndex).size();

		// last item of right area cannot be deleted or moved (toolBar settings: it
		// would not be reversible)
		boolean lastRightItem = (this.selectArea.getSelectedValue() == TGMainToolBar.RIGHT_AREA)
				&& (this.tableAreaControls.getSelectedItem() != null)
				&& (this.tableAreaControls.getSelectedValue() == this.tableAreaControls.getItemCount() - 1);
		this.leftButton.setEnabled((this.tableAreaControls.getSelectedItem() != null) && !lastRightItem);
		this.rightButton.setEnabled(this.tableControls.getSelectedItem() != null);
		this.upButton.setEnabled((areaControlIndex != null) && (areaControlIndex != 0) && !lastRightItem);
		this.downButton.setEnabled(
				(areaControlIndex != null) && (areaControlIndex < (nbAreaControls - 1)) && (nbAreaControls > 1)
				&& ((this.selectArea.getSelectedValue() != TGMainToolBar.RIGHT_AREA) || (areaControlIndex < (nbAreaControls - 2))));
	}

	private void setUntitledToolBarName() {
		String newName = TuxGuitar.getProperty("file.save.default-name") + " ";
		int n = 1;
		List<String> toolBarNames = configMgr.getConfigNames();
		while (toolBarNames.contains(newName + String.valueOf(n))) {
			n++;
		}
		this.newToolBarName.setText(newName + String.valueOf(n));
	}

	private void moveControl(boolean upDirection) {
		Integer areaIndex = this.selectArea.getSelectedValue();
		Integer areaControlIndex = this.tableAreaControls.getSelectedValue();
		if (areaControlIndex == null) {
			return;
		}
		int nbAreaControls = this.config.getAreaContent(areaIndex).size();
		if (!upDirection && (areaControlIndex >= nbAreaControls - 1)) {
			return;
		}
		if (upDirection && (areaControlIndex == 0)) {
			return;
		}
		this.modified = true;
		int swapped = areaControlIndex + (upDirection ? -1 : 1);
		Collections.swap(this.config.getAreaContent(areaIndex), areaControlIndex, swapped);
		this.fillAreaControlsList(areaIndex, swapped);
	}

	private void removeControl() {
		Integer areaIndex = this.selectArea.getSelectedValue();
		Integer areaControlIndex = this.tableAreaControls.getSelectedValue();
		if (areaControlIndex == null) {
			return;
		}
		List<String> listControls = this.config.getAreaContent(areaIndex);
		listControls.remove((int)areaControlIndex);
		this.modified = true;
		this.fillAreaControlsList(areaIndex, Math.max(0, Math.max(0, areaControlIndex - 1)));
	}

	// add a control *before* selected item
	// cant' add after, as it's forbidden to add something after last control of right area
	private void addControl() {
		Integer areaIndex = this.selectArea.getSelectedValue();
		Integer areaControlIndex = this.tableAreaControls.getSelectedValue();
		Integer controlIndex = this.tableControls.getSelectedValue();

		if (controlIndex == null) {
			return;
		}
		String controlName = this.configMap.getToolBarItemNames().get(controlIndex);
		if ((areaControlIndex == null) || this.tableAreaControls.getItemCount() == 0) {
			areaControlIndex = 0;
		}
		this.config.getAreaContent(areaIndex).add(areaControlIndex, controlName);
		this.modified = true;
		this.fillAreaControlsList(areaIndex, areaControlIndex);
	}

	private void save() {
		configMgr.saveConfig(this.config);
		this.modified = false;
		this.updateButtons();
	}

	private void saveAs() {
		String newName = this.newToolBarName.getText();
		if (!newName.equals("")) {
			this.config.setName(newName);
			configMgr.saveConfig(this.config);
			this.modified = false;
			this.fillToolBarNames();
			this.fillAreaControlsList();
			this.setUntitledToolBarName();
		}
	}

	private void delete() {
		boolean apply = (this.mainToolBar.getConfig().getName().equals(this.config.getName()));
		configMgr.deleteConfig(this.config);
		this.setDefaults();
		if (apply) {
			this.apply();
		}
	}

	private void setDefaults() {
		this.config = configMgr.getDefaultConfig().clone();
		this.modified = false;
		this.fillToolBarNames();
		this.fillAreaControlsList();
	}

	private void apply() {
		this.mainToolBar.setConfig(config);
		this.mainToolBar.layout();
		this.mainToolBar.updateItems();
		TGConfigManager.getInstance(context).setValue(TGConfigKeys.MAIN_TOOLBAR_NAME, config.getName());
	}

	private void OK() {
		this.apply();
		if (this.modified) {
			if (this.mainToolBar.getConfig().getName().equals("")) {
				this.saveAs();
				TGConfigManager.getInstance(context).setValue(TGConfigKeys.MAIN_TOOLBAR_NAME, config.getName());
			} else {
				this.save();
			}
		}
		this.dialog.dispose();
	}
}
