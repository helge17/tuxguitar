package org.herac.tuxguitar.app.view.dialog.track;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.util.TGMusicKeyUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackTuningAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UITable;
import org.herac.tuxguitar.ui.widget.UITableItem;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class TGTrackTuningDialog {
	
	private static final String[] NOTE_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_TUNING);
	private static final float MINIMUM_BUTTON_WIDTH = 80;
	private static final float MINIMUM_BUTTON_HEIGHT = 25;
	private static final int MAX_STRINGS = 20;
	private static final int MIN_STRINGS = 4;
	private static final int MAX_OCTAVES = 10;
	private static final int MAX_NOTES = 12;
	
	private TGViewContext context;
	private UIWindow dialog;
	
	private List<TGTrackTuningModel> tuning;
	private List<TGTrackTuningModel[]> tuningPresets;
	private UITable<TGTrackTuningModel> tuningTable;
	private UIDropDownSelect<TGTrackTuningModel[]> tuningPresetsSelect;
	private UICheckBox stringTransposition;
	private UICheckBox stringTranspositionTryKeepString;
	private UICheckBox stringTranspositionApplyToChords;
	private UIDropDownSelect<Integer> offsetCombo;
	
	public TGTrackTuningDialog(TGViewContext context) {
		this.context = context;
		this.tuning = new ArrayList<TGTrackTuningModel>();
		this.tuningPresets = new ArrayList<TGTrackTuningModel[]>();
	}
	
	public void show() {
		TGSongManager songManager = this.findSongManager();
		TGTrack track = this.findTrack();
		
		if(!songManager.isPercussionChannel(track.getSong(), track.getChannelId())) {
			UIFactory factory = this.getUIFactory();
			UIWindow parent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
			UITableLayout dialogLayout = new UITableLayout();
			
			this.dialog = factory.createWindow(parent, true, false);
			this.dialog.setLayout(dialogLayout);
			this.dialog.setText(TuxGuitar.getProperty("tuning"));
			
			UITableLayout leftPanelLayout = new UITableLayout();
			UILegendPanel leftPanel = factory.createLegendPanel(this.dialog);
			leftPanel.setLayout(leftPanelLayout);
			leftPanel.setText(TuxGuitar.getProperty("tuning.strings"));
			dialogLayout.set(leftPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			UITableLayout rightPanelLayout = new UITableLayout();
			UILegendPanel rightPanel = factory.createLegendPanel(this.dialog);
			rightPanel.setLayout(rightPanelLayout);
			rightPanel.setText(TuxGuitar.getProperty("options"));
			dialogLayout.set(rightPanel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			
			UITableLayout bottomPanelLayout = new UITableLayout(0f);
			UIPanel bottomPanel = factory.createPanel(this.dialog, false);
			bottomPanel.setLayout(bottomPanelLayout);
			dialogLayout.set(bottomPanel, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true, 1, 2);
			
			this.initTuningTable(leftPanel);
			
			this.initTuningOptions(rightPanel, track);
			
			this.initButtons(bottomPanel);
			
			this.updateTuningFromTrack(track);
			
			TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
		}
	}
	
	private void initTuningTable(UILayoutContainer parent) {
		UIFactory factory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		
		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = factory.createPanel(parent, false);
		panel.setLayout(panelLayout);
		parentLayout.set(panel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.createTuningPresets();
		this.tuningPresetsSelect = factory.createDropDownSelect(panel);
		panelLayout.set(this.tuningPresetsSelect, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		this.tuningPresetsSelect.addItem(new UISelectItem<TGTrackTuningModel[]>(TuxGuitar.getProperty("tuning.preset.select")));
		for(TGTrackTuningModel[] tuningPreset : this.tuningPresets) {
			this.tuningPresetsSelect.addItem(new UISelectItem<TGTrackTuningModel[]>(this.createTuningPresetLabel(tuningPreset), tuningPreset));
		}
		this.tuningPresetsSelect.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackTuningDialog.this.onSelectPreset();
			}
		});
		
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
		panelLayout.set(this.tuningTable, UITableLayout.PACKED_WIDTH, 250f);
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
		
		UIButton buttonEdit = factory.createButton(buttonsPanel);
		buttonEdit.setImage(TGIconManager.getInstance(this.context.getContext()).getListEdit());
		buttonEdit.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackTuningDialog.this.onEditTuningModel();
			}
		});

		UIButton buttonDelete = factory.createButton(buttonsPanel);
		buttonDelete.setImage(TGIconManager.getInstance(this.context.getContext()).getListRemove());
		buttonDelete.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackTuningDialog.this.onRemoveTuningModel();
			}
		});
		
		buttonsLayout.set(buttonAdd, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, false);
		buttonsLayout.set(buttonDelete, 1, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, false);
		buttonsLayout.set(buttonEdit, 1, 3, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);
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
		
		UITableLayout bottomLayout = new UITableLayout(0f);
		UIPanel bottom = factory.createPanel(panel, false);
		bottom.setLayout(bottomLayout);
		panelLayout.set(bottom, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, true, 1, 1, null, null, 0f);
		
		//---------------------------------OFFSET--------------------------------
		UILabel offsetLabel = factory.createLabel(top);
		offsetLabel.setText(TuxGuitar.getProperty("tuning.offset") + ":");
		topLayout.set(offsetLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, true);
		
		this.offsetCombo = factory.createDropDownSelect(top);
		for(int i = TGTrack.MIN_OFFSET;i <= TGTrack.MAX_OFFSET;i ++){
			this.offsetCombo.addItem(new UISelectItem<Integer>(Integer.toString(i), i));
		}
		this.offsetCombo.setSelectedValue(track.getOffset());
		topLayout.set(this.offsetCombo, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		//---------------------------------OPTIONS----------------------------------
		this.stringTransposition = factory.createCheckBox(bottom);
		this.stringTransposition.setText(TuxGuitar.getProperty("tuning.strings.transpose"));
		this.stringTransposition.setSelected( true );
		bottomLayout.set(this.stringTransposition, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		this.stringTranspositionApplyToChords = factory.createCheckBox(bottom);
		this.stringTranspositionApplyToChords.setText(TuxGuitar.getProperty("tuning.strings.transpose.apply-to-chords"));
		this.stringTranspositionApplyToChords.setSelected( true );
		bottomLayout.set(this.stringTranspositionApplyToChords, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		this.stringTranspositionTryKeepString = factory.createCheckBox(bottom);
		this.stringTranspositionTryKeepString.setText(TuxGuitar.getProperty("tuning.strings.transpose.try-keep-strings"));
		this.stringTranspositionTryKeepString.setSelected( true );
		bottomLayout.set(this.stringTranspositionTryKeepString, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		this.stringTransposition.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				UICheckBox stringTransposition = TGTrackTuningDialog.this.stringTransposition;
				UICheckBox stringTranspositionApplyToChords = TGTrackTuningDialog.this.stringTranspositionApplyToChords;
				UICheckBox stringTranspositionTryKeepString = TGTrackTuningDialog.this.stringTranspositionTryKeepString;
				stringTranspositionApplyToChords.setEnabled((stringTransposition.isEnabled() && stringTransposition.isSelected()));
				stringTranspositionTryKeepString.setEnabled((stringTransposition.isEnabled() && stringTransposition.isSelected()));
			}
		});
	}
	
	private void initButtons(UILayoutContainer parent) {
		UIFactory factory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		
		UIButton buttonOK = factory.createButton(parent);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if( TGTrackTuningDialog.this.updateTrackTuning() ) {
					TGTrackTuningDialog.this.dialog.dispose();
				}
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
	
	private void onSelectPreset() {
		TGTrackTuningModel[] models = this.tuningPresetsSelect.getSelectedValue();
		if( models != null ) {
			this.updateTuningFromPreset(models);
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
	
	private boolean isUsingPreset(TGTrackTuningModel[] preset) {
		if( this.tuning.size() == preset.length ) {
			for(int i = 0 ; i < this.tuning.size(); i ++) {
				if(!this.tuning.get(i).getValue().equals(preset[i].getValue())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	private void updateTuningPresetSelection() {
		TGTrackTuningModel[] selection = null;
		for(TGTrackTuningModel[] preset : this.tuningPresets) {
			if( this.isUsingPreset(preset)) {
				selection = preset;
			}
		}
		this.tuningPresetsSelect.setIgnoreEvents(true);
		this.tuningPresetsSelect.setSelectedValue(selection);
		this.tuningPresetsSelect.setIgnoreEvents(false);
	}
	
	private void updateTuningTable() {
		TGTrackTuningModel selection = this.tuningTable.getSelectedValue();
		
		this.tuningTable.removeItems();
		for(TGTrackTuningModel model : this.tuning) {
			UITableItem<TGTrackTuningModel> item = new UITableItem<TGTrackTuningModel>(model);
			item.setText(0, this.getValueLabel(model.getValue()));
			item.setText(1, this.getValueLabel(model.getValue(), true));
			
			this.tuningTable.addItem(item);
		}
		
		if( selection != null ) {
			this.tuningTable.setSelectedValue(selection);
		}
	}
	
	private void updateTuningControls() {
		this.updateTuningTable();
		this.updateTuningPresetSelection();
	}
	
	private void updateTuningFromTrack(TGTrack track) {
		this.tuning.clear();
		for(int i = 0; i < track.stringCount(); i ++) {
			TGString string = track.getString(i + 1);
			TGTrackTuningModel model = new TGTrackTuningModel();
			model.setValue(string.getValue());
			this.tuning.add(model);
		}
		
		this.updateTuningControls();
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
	
	private void updateTuningModels(List<TGTrackTuningModel> models) {
		this.tuning.clear();
		if( this.tuning.addAll(models)) {
			this.updateTuningControls();
		}
	}
	
	private void updateTuningFromPreset(TGTrackTuningModel[] preset) {
		List<TGTrackTuningModel> models = new ArrayList<>();
		for(TGTrackTuningModel presetModel : preset) {
			TGTrackTuningModel model = new TGTrackTuningModel();
			model.setValue(presetModel.getValue());
			models.add(model);
		}
		this.updateTuningModels(models);
	}
	
	private boolean updateTrackTuning() {
		final TGSongManager songManager = this.findSongManager();
		final TGSong song = this.findSong();
		final TGTrack track = this.findTrack();
		
		final List<TGString> strings = new ArrayList<TGString>();
		for(int i = 0; i < this.tuning.size(); i ++) {
			strings.add(TGSongManager.newString(findSongManager().getFactory(),(i + 1), this.tuning.get(i).getValue()));
		}
		
		final Integer offset = ((songManager.isPercussionChannel(song, track.getChannelId())) ? 0 : this.offsetCombo.getSelectedValue());
		final boolean offsetChanges = (offset != null && !offset.equals(track.getOffset()));
		final boolean tuningChanges = hasTuningChanges(track, strings);
		final boolean transposeStrings = shouldTransposeStrings(track, track.getChannelId());
		final boolean transposeApplyToChords = (transposeStrings && this.stringTranspositionApplyToChords.isSelected());
		final boolean transposeTryKeepString = (transposeStrings && this.stringTranspositionTryKeepString.isSelected());
		
		if( this.validateTrackTuning(strings)) {
			if( tuningChanges || offsetChanges ){
				TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGChangeTrackTuningAction.NAME);
				tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
				tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
				
				if( tuningChanges ) {
					tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_STRINGS, strings);
					tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_STRINGS, transposeStrings);
					tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_TRY_KEEP_STRINGS, transposeTryKeepString);
					tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_APPLY_TO_CHORDS, transposeApplyToChords);
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
		if( strings.size() < MIN_STRINGS || strings.size() > MAX_STRINGS ) {
			TGMessageDialogUtil.errorMessage(this.getContext().getContext(), this.dialog, TuxGuitar.getProperty("tuning.strings.range-error", new String[] {Integer.toString(MIN_STRINGS), Integer.toString(MAX_STRINGS)}));
			
			return false;
		}
		return true;
	}
	
	private boolean shouldTransposeStrings(TGTrack track, int selectedChannelId){
		if( this.stringTransposition.isSelected()){
			boolean percussionChannelNew = findSongManager().isPercussionChannel(track.getSong(), selectedChannelId);
			boolean percussionChannelOld = findSongManager().isPercussionChannel(track.getSong(), track.getChannelId());
			
			return (!percussionChannelNew && !percussionChannelOld);
		}
		return false;
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
	
	public String createTuningPresetLabel(TGTrackTuningModel[] tuningPreset) {
		StringBuilder label = new StringBuilder();
		for(int i = 0 ; i < tuningPreset.length; i ++) {
			if( i > 0 ) {
				label.append(" ");
			}
			label.append(this.getValueLabel(tuningPreset[tuningPreset.length - i - 1].getValue()));
		}
		return label.toString();
	}
	
	public TGTrackTuningModel[] createTuningPreset(int[] values) {
		TGTrackTuningModel[] models = new TGTrackTuningModel[values.length];
		for(int i = 0 ; i < models.length; i ++) {
			models[i] = new TGTrackTuningModel();
			models[i].setValue(values[i]);
		}
		return models;
	}
	
	public void createTuningPresets() {
		this.tuningPresets.clear();
		for(int[] tuningValues : TGSongManager.DEFAULT_TUNING_VALUES) {
			this.tuningPresets.add(this.createTuningPreset(tuningValues));
		}
	}
	
	public String[] getValueLabels() {
		String[] valueNames = new String[MAX_NOTES * MAX_OCTAVES];
		for (int i = 0; i < valueNames.length; i++) {
			valueNames[i] = this.getValueLabel(i, true);
		}
		return valueNames;
	}
	
	public String getValueLabel(Integer value) {
		return this.getValueLabel(value, false);
	}
	
	public String getValueLabel(Integer value, boolean octave) {
		StringBuilder sb = new StringBuilder();
		if( value != null ) {
			sb.append(NOTE_NAMES[value % NOTE_NAMES.length]);
			
			if( octave ) {
				sb.append(value / MAX_NOTES);
			}
		}
		return sb.toString();
	}
	
	public TGSongManager findSongManager() {
		return this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
	}
	
	public TGSong findSong() {
		return this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
	}
	
	public TGTrack findTrack() {
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