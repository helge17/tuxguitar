package app.tuxguitar.app.view.dialog.track;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.util.TGMessageDialogUtil;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.track.TGChangeTrackTuningAction;
import app.tuxguitar.song.helpers.tuning.TuningGroup;
import app.tuxguitar.song.helpers.tuning.TuningPreset;
import app.tuxguitar.editor.util.TGSyncProcessLocked;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIMouseDoubleClickListener;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.*;
import app.tuxguitar.util.TGMusicKeyUtils;

public class TGTrackTuningDialog {

	private static final float MINIMUM_BUTTON_WIDTH = 80;
	private static final float MINIMUM_BUTTON_HEIGHT = 25;

	private TGViewContext context;
	private UIWindow dialog;

	private TuningGroup allTuningsGroup;	// group #0 = custom tuning presets, other groups = TuxGuitar's tuning presets

	private List<TGTrackTuningModel> tuning;
	private UITable<TGTrackTuningModel> tuningTable;
	private UISpinner offsetSpinner;
	private UIButton buttonEdit;
	private UIButton buttonDelete;
	private UIButton buttonMoveUp;
	private UIButton buttonMoveDown;
	private UIPanel presetsPanel;
	private UIButton buttonPresetSave;
	private UIButton buttonPresetSaveAs;
	private UIButton buttonPresetDelete;
	private UITextField newPresetName;
	private TuningPreset currentSelectedPreset;
	private boolean isNewPreset;

	public TGTrackTuningDialog(TGViewContext context) {
		this.context = context;
		TuningGroup tgTunings = TuxGuitar.getInstance().getTuningManager().getTgTuningsGroup();
		TuningGroup customTunings = TuxGuitar.getInstance().getTuningManager().getCustomTuningsGroup();
		customTunings.setName(TuxGuitar.getProperty("tuning.preset.select"));
		allTuningsGroup = new TuningGroup();
		allTuningsGroup.addGroup(customTunings);
		for (TuningGroup group : tgTunings.getGroups()) {
			allTuningsGroup.addGroup(group);
		}
	}

	public void show() {
		TGTrack track = this.findTrack();

		if(!track.isPercussion()) {
			this.tuning = getTuningFromTrack(track);

			UIFactory factory = this.getUIFactory();
			UIWindow parent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
			UITableLayout dialogLayout = new UITableLayout();

			this.dialog = factory.createWindow(parent, true, false);
			this.dialog.setLayout(dialogLayout);
			this.dialog.setText(TuxGuitar.getProperty("tuning"));

			UITableLayout leftUpperPanelLayout = new UITableLayout();
			UILegendPanel leftUpperPanel = factory.createLegendPanel(this.dialog);
			leftUpperPanel.setLayout(leftUpperPanelLayout);
			leftUpperPanel.setText(TuxGuitar.getProperty("tuning.presets"));
			dialogLayout.set(leftUpperPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			UITableLayout leftLowerPanelLayout = new UITableLayout();
			UILegendPanel leftLowerPanel = factory.createLegendPanel(this.dialog);
			leftLowerPanel.setLayout(leftLowerPanelLayout);
			leftLowerPanel.setText(TuxGuitar.getProperty("tuning.strings"));
			dialogLayout.set(leftLowerPanel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			UITableLayout rightPanelLayout = new UITableLayout();
			UILegendPanel rightPanel = factory.createLegendPanel(this.dialog);
			rightPanel.setLayout(rightPanelLayout);
			rightPanel.setText(TuxGuitar.getProperty("options"));
			dialogLayout.set(rightPanel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 2, 1);

			UITableLayout bottomPanelLayout = new UITableLayout(0f);
			UIPanel bottomPanel = factory.createPanel(this.dialog, false);
			bottomPanel.setLayout(bottomPanelLayout);
			dialogLayout.set(bottomPanel, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true, 1, 2);

			this.initTuningPresets(leftUpperPanel);
			this.initTuningStringTable(leftLowerPanel);
			this.initTuningOptions(rightPanel, track);
			this.initButtons(bottomPanel);
			this.updateTuningControls();

			TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
		}
	}

	private TuningPreset findTuningInGroup(List<TGTrackTuningModel> tuningModel, TuningGroup group) {
		if (group.getGroups() != null && !group.getGroups().isEmpty()) {
			for (TuningGroup searchGroup : group.getGroups() ) {
				TuningPreset found = findTuningInGroup(tuning, searchGroup);
				if (found != null) {
					return(found);
				}
			}
		}
		else {
			for (TuningPreset preset : group.getTunings()) {
				if (areTuningsEqual(tuningModel, preset)) {
					return(preset);
				}
			}
		}
		return(null);
	}

	// return first matching occurrence (several presets may have the same name)
	private TuningPreset findTuningInGroup(String tuningName, TuningGroup group) {
		if (group.getGroups() != null && !group.getGroups().isEmpty()) {
			for (TuningGroup searchGroup : group.getGroups() ) {
				TuningPreset found = findTuningInGroup(tuningName, searchGroup);
				if (found != null) {
					return(found);
				}
			}
		}
		else {
			for (TuningPreset preset : group.getTunings()) {
				if (tuningName.equals(preset.getName())) {
					return(preset);
				}
			}
		}
		return(null);
	}

	private String findUnsavedPresetName() {
		String name = TuxGuitar.getProperty("file.save.default-name") + " ";
		int n=1;
		TuningGroup customTuningsGroup = allTuningsGroup.getGroups().get(0);

		while (findTuningInGroup(name + String.valueOf(n), customTuningsGroup) != null) {
			n++;
		}
		return(name + String.valueOf(n));
	}
	private void populateGroupsDropDown(UIDropDownSelect<TuningGroup> select, TuningGroup group, TuningGroup groupToSelect) {
		select.setIgnoreEvents(true);
		select.removeItems();
		if (group != null) {
			for (TuningGroup subGroup : group.getGroups()) {
				boolean wasEmpty = (select.getItemCount() == 0);
				select.addItem(new UISelectItem<TuningGroup>(subGroup.getName(), subGroup));
				if ((wasEmpty && groupToSelect==null) || subGroup==groupToSelect) {
					select.setSelectedValue(subGroup);
				}
			}
		}
		select.setEnabled(select.getItemCount() > 0);
		select.setIgnoreEvents(false);
	}

	private void populateTuningsDropDown(UIDropDownSelect<TuningPreset> select, TuningGroup group, TuningPreset presetToSelect) {
		select.setIgnoreEvents(true);
		select.removeItems();
		if (group != null) {
			for (TuningPreset tuning : group.getTunings()) {
				select.addItem(new UISelectItem<TuningPreset>(tuningPresetLabel(tuning), tuning));
			}
			if (presetToSelect != null) {
				select.setSelectedValue(presetToSelect);
				this.currentSelectedPreset = presetToSelect;
			}
			else {
				this.currentSelectedPreset = null;
			}
		}
		select.setEnabled(select.getItemCount() > 0);
		select.setIgnoreEvents(false);
	}

	// add a suffix to tuning preset name, to show all notes
	private String tuningPresetLabel(TuningPreset preset) {
		StringBuilder label = new StringBuilder();
		label.append(preset.getName()).append(" - ");
		int[] values = preset.getValues();
		for(int i = 0 ; i < values.length; i ++) {
			if( i > 0 ) {
				label.append(" ");
			}
		label.append(TGMusicKeyUtils.sharpNoteName(values[values.length - i - 1]));
		}
		return label.toString();
	}

	private void initTuningPresets(UILayoutContainer parent) {
		UIFactory factory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();

		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = factory.createPanel(parent, false);
		panel.setLayout(panelLayout);
		parentLayout.set(panel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		presetsPanel = factory.createPanel(panel, false);
		panelLayout.set(presetsPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		// width margin: effective width depends on the length of the user-defined tuning names.
		panelLayout.set(presetsPanel, UITableLayout.PACKED_WIDTH, 450f);
		// height margin: effective height could be variable, depending if buttons are displayed or not.
		panelLayout.set(presetsPanel, UITableLayout.PACKED_HEIGHT, 110f);
	}

	private void initTuningStringTable(UILayoutContainer parent) {
		UIFactory factory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();

		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = factory.createPanel(parent, false);
		panel.setLayout(panelLayout);
		parentLayout.set(panel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.tuningTable = factory.createTable(panel, true);
		this.tuningTable.setColumns(2);
		this.tuningTable.setColumnName(0, TuxGuitar.getProperty("tuning.label"));
		this.tuningTable.setColumnName(1, TuxGuitar.getProperty("tuning.value"));
		this.tuningTable.addMouseDoubleClickListener(new UIMouseDoubleClickListener() {
			public void onMouseDoubleClick(UIMouseEvent event) {
				TGTrackTuningDialog.this.onEditTuningModel();
			}
		});
		panelLayout.set(this.tuningTable, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, true);
		panelLayout.set(this.tuningTable, UITableLayout.PACKED_WIDTH, 320f);
		panelLayout.set(this.tuningTable, UITableLayout.PACKED_HEIGHT, 200f);

		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttonsPanel = factory.createPanel(panel, false);
		buttonsPanel.setLayout(buttonsLayout);
		panelLayout.set(buttonsPanel, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);

		UIButton buttonAdd = factory.createButton(buttonsPanel);
		buttonAdd.setImage(TGIconManager.getInstance(this.context.getContext()).getListAdd());
		buttonAdd.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackTuningDialog.this.onAddTuningModel();
			}
		});

		buttonEdit = factory.createButton(buttonsPanel);
		buttonEdit.setImage(TGIconManager.getInstance(this.context.getContext()).getListEdit());
		buttonEdit.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackTuningDialog.this.onEditTuningModel();
			}
		});

		buttonMoveUp = factory.createButton(buttonsPanel);
		buttonMoveUp.setImage(TGIconManager.getInstance(this.context.getContext()).getImageByName(TGIconManager.ARROW_UP));
		buttonMoveUp.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackTuningDialog.this.moveString(-1);
			}
		});

		buttonMoveDown = factory.createButton(buttonsPanel);
		buttonMoveDown.setImage(TGIconManager.getInstance(this.context.getContext()).getImageByName(TGIconManager.ARROW_DOWN));
		buttonMoveDown.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackTuningDialog.this.moveString(1);
			}
		});

		buttonDelete = factory.createButton(buttonsPanel);
		buttonDelete.setImage(TGIconManager.getInstance(this.context.getContext()).getImageByName(TGIconManager.LIST_REMOVE));
		buttonDelete.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackTuningDialog.this.onRemoveTuningModel();
			}
		});

		this.tuningTable.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackTuningDialog.this.updateTuningButtons();
			}
		});

		buttonsLayout.set(buttonAdd, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, false);
		buttonsLayout.set(buttonDelete, 1, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, false);
		buttonsLayout.set(buttonMoveUp, 1, 3, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
		buttonsLayout.set(buttonMoveDown, 1, 4, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);
		buttonsLayout.set(buttonEdit, 1, 5, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);
	}

	// call this method:
	// - either if a tuning group drop-down list is clicked:
	//	 selectGroup is the selected group, selectPreset is null
	// - or when selecting an existing preset (e.g. load a file using default preset):
	//   selectGroup is null, selectPreset is the selected preset
	// - or when selecting an unknown preset (e.g. load a file using unknown preset, or edit current tuning):
	//   selectGroup and selectPreset are null
	private void updatePresetsPanel(TuningGroup selectGroup, TuningPreset selectPreset) {
		// error case
		if (selectGroup != null && selectPreset != null) return;

		UIFactory factory = this.getUIFactory();
		TuningGroup group = null;
		int nDropDown=0;

		this.isNewPreset = (selectGroup==null && selectPreset==null);

		// build list of successive groups/subGroups from root
		List<TuningGroup> selectedGroupsList;
		if (selectGroup == null) {
			if (selectPreset == null) {
				selectedGroupsList = findParentGroups(allTuningsGroup.getGroups().get(0));
			} else {
				selectedGroupsList = findParentGroups(selectPreset);
			}
		} else {
			selectedGroupsList = findParentGroups(selectGroup);
		}
		if (selectedGroupsList==null || selectedGroupsList.size()==0) {
			return;
		}

		// populate groups drop-down lists
		// don't update lists if currently editing a custom preset
		if (selectGroup != null || selectPreset != null || this.currentSelectedPreset==null || !isCustomTuningPreset(this.currentSelectedPreset)) {
			for (UIControl control: presetsPanel.getChildren()) {
				control.dispose();
			}
			UITableLayout presetsPanelLayout = new UITableLayout(0f);
			presetsPanel.setLayout(presetsPanelLayout);
			group = selectedGroupsList.get(0);
			while (group.getGroups().size() > 0) {
				UIDropDownSelect<TuningGroup> groupSelect = factory.createDropDownSelect(presetsPanel);
				presetsPanelLayout.set(groupSelect, 1+nDropDown, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
				TuningGroup selectedGroupInList = null;
				if (selectedGroupsList.size()>nDropDown+1) {
					selectedGroupInList = selectedGroupsList.get(nDropDown+1);
				}
				this.populateGroupsDropDown(groupSelect, group, selectedGroupInList);
				groupSelect.addSelectionListener(new UISelectionListener() {
					public void onSelect(UISelectionEvent event) {
						TGTrackTuningDialog.this.onSelectGroup((UIDropDownSelect<TuningGroup>) event.getComponent());
					}
				});
				nDropDown++;
				group = selectedGroupInList!=null ? selectedGroupInList : group.getGroups().get(0);
			};
			// populate tuning presets drop-down list
			UIDropDownSelect<TuningPreset> tuningSelect = factory.createDropDownSelect(presetsPanel);
			presetsPanelLayout.set(tuningSelect, 1+nDropDown, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
			TuningPreset toSelect = null;
			if (selectPreset != null) {
				toSelect = selectPreset;
			} else if (selectGroup!=null && group.getTunings().size()>0) {
				toSelect = group.getTunings().get(0);
			}
			this.populateTuningsDropDown(tuningSelect, group, toSelect);
			if (tuningSelect.getSelectedValue()!=null) {
				updateTuningTable(tuningSelect.getSelectedValue());
			}
			tuningSelect.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					TGTrackTuningDialog.this.onSelectPreset((UIDropDownSelect<TuningPreset>) event.getComponent());
				}
			});

			// create buttons if needed (custom tuning or no tuning selected)
			if (currentSelectedPreset==null || isCustomTuningPreset(currentSelectedPreset)) {
				buttonPresetDelete = factory.createButton(presetsPanel);
				buttonPresetDelete.setImage(TGIconManager.getInstance(this.context.getContext()).getImageByName(TGIconManager.LIST_REMOVE));
				buttonPresetDelete.setToolTipText(TuxGuitar.getProperty("tuning.preset.delete"));
				buttonPresetDelete.addSelectionListener(new UISelectionListener() {
					public void onSelect(UISelectionEvent event) {
						TGTrackTuningDialog.this.onDeletePreset();
					}
				});
				presetsPanelLayout.set(buttonPresetDelete, 1+nDropDown, 2, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);
				buttonPresetSave = factory.createButton(presetsPanel);
				buttonPresetSave.setImage(TGIconManager.getInstance(this.context.getContext()).getImageByName(TGIconManager.FILE_SAVE));
				buttonPresetSave.setToolTipText(TuxGuitar.getProperty("tuning.preset.save"));
				buttonPresetSave.addSelectionListener(new UISelectionListener() {
					public void onSelect(UISelectionEvent event) {
						TGTrackTuningDialog.this.onSavePreset();
					}
				});
				presetsPanelLayout.set(buttonPresetSave, 1+nDropDown, 3, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);
				newPresetName = factory.createTextField(presetsPanel);
				newPresetName.setEnabled(true);
				// if preset name gets to long, it might break the UI layout
				newPresetName.setTextLimit(20);
				presetsPanelLayout.set(newPresetName, 2+nDropDown, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
				buttonPresetSaveAs = factory.createButton(presetsPanel);
				buttonPresetSaveAs.setImage(TGIconManager.getInstance(this.context.getContext()).getImageByName(TGIconManager.FILE_SAVE_AS));
				buttonPresetSaveAs.setToolTipText(TuxGuitar.getProperty("tuning.preset.saveas"));
				buttonPresetSaveAs.addSelectionListener(new UISelectionListener() {
					public void onSelect(UISelectionEvent event) {
						TGTrackTuningDialog.this.onSavePresetAs();
					}
				});
				buttonPresetSaveAs.setEnabled(true);
				presetsPanelLayout.set(buttonPresetSaveAs, 2+nDropDown, 2, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);
			}
			presetsPanel.layout();
		}
		// enable/disable buttons if needed (custom tuning or no tuning selected)
		updatePresetsButtons();
		updateTuningButtons();
	}

	private void updatePresetsButtons() {
		if (currentSelectedPreset==null || isCustomTuningPreset(currentSelectedPreset)) {
			boolean isModifiedTuning = this.isNewPreset && currentSelectedPreset!=null;
			buttonPresetDelete.setEnabled(currentSelectedPreset!=null);
			buttonPresetSave.setEnabled(isModifiedTuning);
			if (this.isNewPreset) {
				newPresetName.setText(findUnsavedPresetName());
			}
		}
	}

	private void initTuningOptions(UILayoutContainer parent, TGTrack track) {
		UIFactory factory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();

		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = factory.createPanel(parent, false);
		panel.setLayout(panelLayout);
		parentLayout.set(panel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UITableLayout topLayout = new UITableLayout(0f);
		UIPanel top = factory.createPanel(panel, false);
		top.setLayout(topLayout);
		panelLayout.set(top, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, true, 1, 1, null, null, 0f);

		//---------------------------------OFFSET--------------------------------
		UILabel offsetLabel = factory.createLabel(top);
		offsetLabel.setText(TuxGuitar.getProperty("tuning.offset") + ":");
		topLayout.set(offsetLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, true);

		this.offsetSpinner = factory.createSpinner(top);
		this.offsetSpinner.setMinimum(TGTrack.MIN_OFFSET);
		this.offsetSpinner.setMaximum(TGTrack.MAX_OFFSET);
		this.offsetSpinner.setValue(track.getOffset());
		topLayout.set(this.offsetSpinner, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

	}

	private void initButtons(UILayoutContainer parent) {
		UIFactory factory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();

		UIButton buttonOK = factory.createButton(parent);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				new TGSyncProcessLocked(getContext().getContext(), new Runnable() {
					public void run() {
						if( TGTrackTuningDialog.this.updateTrackTuning() ) {
							TGTrackTuningDialog.this.dialog.dispose();
						}
					}
				}).process();
			}
		});
		parentLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);

		UIButton buttonCancel = factory.createButton(parent);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackTuningDialog.this.dialog.dispose();
			}
		});
		parentLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);
		parentLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
	}

	private void onSelectGroup(UIDropDownSelect<TuningGroup> select) {
		this.updatePresetsPanel(select.getSelectedValue(), null);
	}

	private void onSelectPreset(UIDropDownSelect<TuningPreset> select) {
		this.currentSelectedPreset = select.getSelectedValue();
		updateTuningTable(select.getSelectedValue());
		updatePresetsButtons();
		updateTuningButtons();
	}

	private void onSavePreset() {
		TuningGroup customTuningsGroup = allTuningsGroup.getGroups().get(0);
		TuningPreset preset = convertTuning(customTuningsGroup, currentSelectedPreset.getName(), this.tuning);
		customTuningsGroup.removeTuningPreset(currentSelectedPreset);
		customTuningsGroup.addTuningPreset(preset);
		TuxGuitar.getInstance().getTuningManager().saveCustomTunings(customTuningsGroup);
		this.updatePresetsPanel(null, preset);
	}

	private void onSavePresetAs() {
		TuningGroup customTuningsGroup = allTuningsGroup.getGroups().get(0);
		TuningPreset preset = convertTuning(customTuningsGroup, this.newPresetName.getText(), this.tuning);
		customTuningsGroup.addTuningPreset(preset);
		TuxGuitar.getInstance().getTuningManager().saveCustomTunings(customTuningsGroup);
		this.updatePresetsPanel(null, preset);
	}

	private void onDeletePreset() {
		TuningGroup customTuningsGroup = allTuningsGroup.getGroups().get(0);
		customTuningsGroup.removeTuningPreset(currentSelectedPreset);
		TuxGuitar.getInstance().getTuningManager().saveCustomTunings(customTuningsGroup);
		this.currentSelectedPreset = null;
		this.updatePresetsPanel(null, null);
	}

	private void updateTuningTable(TuningPreset preset) {
		tuning.clear();
		if (preset!=null && preset.getValues().length>0) {
			for (int value:preset.getValues()) {
				TGTrackTuningModel model = new TGTrackTuningModel();
				model.setValue(value);
				tuning.add(model);
			}
			updateTuningTable();
		}
	}

	private void onAddTuningModel() {
		new TGTrackTuningChooserDialog(this).select(new TGTrackTuningChooserHandler() {
			public void handleSelection(TGTrackTuningModel model) {
				addTuningModel(model);
			}
		});
	}

	private void onEditTuningModel() {
		final TGTrackTuningModel editingModel = this.tuningTable.getSelectedValue();
		if( editingModel != null ) {
			new TGTrackTuningChooserDialog(this).select(new TGTrackTuningChooserHandler() {
				public void handleSelection(TGTrackTuningModel model) {
					editingModel.setValue(model.getValue());
					updateTuningControls();
				}
			}, editingModel);
		}
	}

	private void onRemoveTuningModel() {
		TGTrackTuningModel model = this.tuningTable.getSelectedValue();
		if( model != null ) {
			removeTuningModel(model);
		}
	}

	private List<TuningGroup> findParentGroups(TuningGroup group) {
		if (group == null) {
			return(null);
		}
		List<TuningGroup> listGroups = new ArrayList<TuningGroup>();
		do {
			listGroups.add(0,group);
			group = group.getParent();
		} while (group != null);
		return(listGroups);
	}

	private List<TuningGroup> findParentGroups(TuningPreset preset) {
		return(findParentGroups(preset.getParent()));
	}

	private void moveString(int delta) {
		final TGTrackTuningModel model = this.tuningTable.getSelectedValue();
		if (model != null) {
			int index = this.tuning.indexOf(model);
			this.tuning.remove(index);
			this.tuning.add(index + delta, model);
			this.updateTuningControls();
		}
	}

	private boolean isCustomTuningPreset(TuningPreset preset) {
		if (preset == null) {
			return(false);
		}
		TuningGroup parent = preset.getParent();
		while (parent!=null) {
			if (parent.equals(allTuningsGroup.getGroups().get(0))) {
				return(true);
			}
			parent = parent.getParent();
		}
		return(false);
	}
	private TuningPreset convertTuning(TuningGroup parent, String name, List<TGTrackTuningModel> tuningModel) {
		int[] noteValues = new int[tuningModel.size()];
		for (int i=0; i<tuningModel.size();i++) {
			noteValues[i] = tuningModel.get(i).getValue();
		}
		return new TuningPreset(parent, name, noteValues);
	}

	private boolean areTuningsEqual(List<TGTrackTuningModel> tuningModel, TuningPreset tuningPreset) {
		if (tuningModel==null && tuningPreset==null) {
			return(true);
		}
		if (tuningModel!=null && tuningPreset==null) {
			return(false);
		}
		if (tuningModel==null && tuningPreset!=null) {
			return(false);
		}
		if (tuningPreset.getValues().length != tuningModel.size()) {
			return(false);
		}
		for (int i=0; i<tuningModel.size(); i++) {
			if (tuningPreset.getValues()[i] != tuningModel.get(i).getValue()) {
				return(false);
			}
		}
		return(true);
	}

	private void updateTuningTable() {
		TGTrackTuningModel selection = this.tuningTable.getSelectedValue();

		this.tuningTable.removeItems();
		for(TGTrackTuningModel model : this.tuning) {
			UITableItem<TGTrackTuningModel> item = new UITableItem<TGTrackTuningModel>(model);
			item.setText(0, TGMusicKeyUtils.sharpNoteName(model.getValue()));
			item.setText(1, TGMusicKeyUtils.sharpNoteFullName(model.getValue()));

			this.tuningTable.addItem(item);
		}

		if( selection != null ) {
			this.tuningTable.setSelectedValue(selection);
		}
	}

	private void updateTuningButtons() {
		TGTrackTuningModel model = this.tuningTable.getSelectedValue();
		int index = model != null ? this.tuning.indexOf(model) : -1;
		buttonEdit.setEnabled(model != null);
		buttonDelete.setEnabled(model != null);
		buttonMoveUp.setEnabled(model != null && index > 0);
		buttonMoveDown.setEnabled(model != null && index < this.tuning.size() - 1);
	}

	private void updateTuningControls() {
		this.updateTuningTable();
		this.updatePresetsPanel(null, findTuningInGroup(this.tuning, this.allTuningsGroup));
		this.updateTuningButtons();
	}

	private static List<TGTrackTuningModel> getTuningFromTrack(TGTrack track) {
		List<TGTrackTuningModel> tuning = new ArrayList<>();
		for(int i = 0; i < track.stringCount(); i ++) {
			TGString string = track.getString(i + 1);
			TGTrackTuningModel model = new TGTrackTuningModel();
			model.setValue(string.getValue());
			tuning.add(model);
		}
		return tuning;
	}

	private void addTuningModel(TGTrackTuningModel model) {
		if( this.tuning.add(model)) {
			this.updateTuningControls();
		}
	}

	private void removeTuningModel(TGTrackTuningModel model) {
		if( this.tuning.remove(model)) {
			this.updateTuningControls();
		}
	}

	private boolean updateTrackTuning() {
		final TGSongManager songManager = this.findSongManager();
		final TGSong song = this.findSong();
		final TGTrack track = this.findTrack();

		final List<TGString> strings = new ArrayList<TGString>();
		for(int i = 0; i < this.tuning.size(); i ++) {
			strings.add(TGSongManager.newString(findSongManager().getFactory(),(i + 1), this.tuning.get(i).getValue()));
		}

		final Integer offset = ((songManager.isPercussionChannel(song, track.getChannelId())) ? 0 : this.offsetSpinner.getValue());
		final boolean offsetChanges = (offset != null && !offset.equals(track.getOffset()));
		final boolean tuningChanges = hasTuningChanges(track, strings);

		if( this.validateTrackTuning(strings)) {
			if( tuningChanges || offsetChanges ){
				TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGChangeTrackTuningAction.NAME);
				tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
				tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);

				if( tuningChanges ) {
					tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_STRINGS, strings);
				}
				if( offsetChanges ) {
					tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_OFFSET, offset);
				}
				tgActionProcessor.process();
			}
			return true;
		}
		return false;
	}

	private boolean validateTrackTuning(List<TGString> strings) {
		if( strings.size() < TGTrack.MIN_STRINGS || strings.size() > TGTrack.MAX_STRINGS ) {
			TGMessageDialogUtil.errorMessage(this.getContext().getContext(), this.dialog, TuxGuitar.getProperty("tuning.strings.range-error", new String[] {Integer.toString(TGTrack.MIN_STRINGS), Integer.toString(TGTrack.MAX_STRINGS)}));

			return false;
		}
		return true;
	}

	private boolean hasTuningChanges(TGTrack track, List<TGString> newStrings){
		List<TGString> oldStrings = track.getStrings();
		//check the number of strings
		if(oldStrings.size() != newStrings.size()){
			return true;
		}
		//check the tuning of strings
		for(int i = 0;i < oldStrings.size();i++){
			TGString oldString = (TGString)oldStrings.get(i);
			boolean stringExists = false;
			for(int j = 0;j < newStrings.size();j++){
				TGString newString = (TGString)newStrings.get(j);
				if(newString.isEqual(oldString)){
					stringExists = true;
				}
			}
			if(!stringExists){
				return true;
			}
		}
		return false;
	}

	private TGSongManager findSongManager() {
		return this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
	}

	private TGSong findSong() {
		return this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
	}

	private TGTrack findTrack() {
		return this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
	}

	public TGViewContext getContext() {
		return this.context;
	}

	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context.getContext()).getFactory();
	}

	public UIWindow getDialog() {
		return this.dialog;
	}
}
