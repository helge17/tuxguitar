package org.herac.tuxguitar.app.view.dialog.track;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMusicKeyUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.util.TGProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackPropertiesAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackTuningAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackInfoAction;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIColorChooser;
import org.herac.tuxguitar.ui.chooser.UIColorChooserHandler;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class TGTrackPropertiesDialog implements TGEventListener {
	
	private static final String[] NOTE_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_TUNING);
	private static final float MINIMUM_LEFT_CONTROLS_WIDTH = 180;
	private static final float MINIMUM_BUTTON_WIDTH = 80;
	private static final float MINIMUM_BUTTON_HEIGHT = 25;
	private static final int MAX_STRINGS = 7;
	private static final int MIN_STRINGS = 4;
	private static final int MAX_OCTAVES = 10;
	private static final int MAX_NOTES = 12;
	
	private TGViewContext context;
	private UIWindow dialog;
	private UITextField nameText;
	private TGColor trackColor;
	private List<TGString> tempStrings;
	private UICheckBox stringTransposition;
	private UICheckBox stringTranspositionTryKeepString;
	private UICheckBox stringTranspositionApplyToChords;
	private UISpinner stringCountSpinner;
	private UIDropDownSelect<Integer>[] stringCombos;
	private UIDropDownSelect<Integer> offsetCombo;
	private int stringCount;
	private UIDropDownSelect<Integer> instrumentCombo;
	private UIColor colorButtonValue;
	private boolean percussionChannel;
	private TGProcess updateItemsProcess;
	
	@SuppressWarnings("unchecked")
	public TGTrackPropertiesDialog(TGViewContext context) {
		this.context = context;
		this.stringCombos = new UIDropDownSelect[MAX_STRINGS];
		this.createSyncProcesses();
	}
	
	public void show() {
		TGSongManager songManager = this.findSongManager();
		TGTrack track = this.findTrack();
		
		this.stringCount = track.getStrings().size();
		this.trackColor = track.getColor().clone(songManager.getFactory());
		this.percussionChannel = songManager.isPercussionChannel(track.getSong(), track.getChannelId());
		this.initTempStrings(track.getStrings());
		
		UIFactory factory = this.getUIFactory();
		UIWindow parent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
		UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = factory.createWindow(parent, true, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("track.properties"));
		
		//GENERAL
		this.initTrackInfo(track);
		
		//INSTRUMENT
		this.initInstrumentFields(track);
		
		//TUNING
		this.initTuningInfo(track);
		
		//BUTTONS
		this.initButtons();
		
		//LISTENERS
		this.initListeners();
		
		this.updateTuningGroup(!this.percussionChannel);
		
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private void initListeners() {
		this.addListeners();
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				removeListeners();
			}
		});
	}
	
	private void initTrackInfo(TGTrack track) {
		final UIFactory factory = this.getUIFactory();
		UITableLayout dialogLayout = (UITableLayout) this.dialog.getLayout();
		
		UITableLayout legendLayout = new UITableLayout();
		UILegendPanel legendPanel = factory.createLegendPanel(this.dialog);
		legendPanel.setLayout(legendLayout);
		legendPanel.setText(TuxGuitar.getProperty("track.properties.general"));
		dialogLayout.set(legendPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UITableLayout topLayout = new UITableLayout();
		UIPanel top = factory.createPanel(legendPanel, false);
		top.setLayout(topLayout);
		legendLayout.set(top, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, true);
		
		UITableLayout bottomLayout = new UITableLayout();
		UIPanel bottom = factory.createPanel(legendPanel, false);
		bottom.setLayout(bottomLayout);
		legendLayout.set(bottom, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, true);
		
		//-----------------------NAME---------------------------------
		UILabel nameLabel = factory.createLabel(top);
		nameLabel.setText(TuxGuitar.getProperty("track.name") + ":");
		topLayout.set(nameLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		this.nameText = factory.createTextField(top);
		this.nameText.setText(track.getName());
		topLayout.set(this.nameText, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, MINIMUM_LEFT_CONTROLS_WIDTH, null, null);
		
		//-----------------------COLOR---------------------------------
		UILabel colorLabel = factory.createLabel(bottom);
		colorLabel.setText(TuxGuitar.getProperty("track.color") + ":");
		bottomLayout.set(colorLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		final UIButton colorButton = factory.createButton(bottom);
		colorButton.setText(TuxGuitar.getProperty("choose"));
		colorButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				UIColorModel colorModel = new UIColorModel();
				colorModel.setRed(TGTrackPropertiesDialog.this.trackColor.getR());
				colorModel.setGreen(TGTrackPropertiesDialog.this.trackColor.getG());
				colorModel.setBlue(TGTrackPropertiesDialog.this.trackColor.getB());
				
				UIColorChooser colorChooser = factory.createColorChooser(TGTrackPropertiesDialog.this.dialog);
				colorChooser.setDefaultModel(colorModel);
				colorChooser.setText(TuxGuitar.getProperty("choose-color"));
				colorChooser.choose(new UIColorChooserHandler() {
					public void onSelectColor(UIColorModel selection) {
						if( selection != null ) {
							TGTrackPropertiesDialog.this.trackColor.setR(selection.getRed());
							TGTrackPropertiesDialog.this.trackColor.setG(selection.getGreen());
							TGTrackPropertiesDialog.this.trackColor.setB(selection.getBlue());
							TGTrackPropertiesDialog.this.setButtonColor(colorButton);
						}
					}
				});
			}
		});
		colorButton.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				TGTrackPropertiesDialog.this.disposeButtonColor();
			}
		});
		bottomLayout.set(colorButton, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, MINIMUM_LEFT_CONTROLS_WIDTH, null, null);
		
		this.setButtonColor(colorButton);
	}
	
	private void initTuningInfo(TGTrack track) {
		UIFactory factory = this.getUIFactory();
		UITableLayout dialogLayout = (UITableLayout) this.dialog.getLayout();
		
		UILegendPanel legendPanel = factory.createLegendPanel(this.dialog);
		legendPanel.setLayout(new UITableLayout());
		legendPanel.setText(TuxGuitar.getProperty("tuning"));
		dialogLayout.set(legendPanel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 2, 1);
		
		initTuningData(legendPanel, track);
		initTuningCombos(legendPanel);
	}
	
	private void initTuningCombos(UILayoutContainer parent) {
		UIFactory factory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		
		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = factory.createPanel(parent, false);
		panel.setLayout(panelLayout);
		parentLayout.set(panel, 1, 2, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, true);
		
		String[] tuningTexts = getAllValueNames();
		for (int i = 0; i < MAX_STRINGS; i++) {
			this.stringCombos[i] = factory.createDropDownSelect(panel);
			for(int value = 0 ; value < tuningTexts.length ; value ++) {
				this.stringCombos[i].addItem(new UISelectItem<Integer>(tuningTexts[value], value));
			}
			panelLayout.set(this.stringCombos[i], (i + 1), 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		}
	}
	
	private void initTuningData(UILayoutContainer parent, TGTrack track) {
		UIFactory factory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		
		UITableLayout panelLayout = new UITableLayout();
		UIPanel panel = factory.createPanel(parent, false);
		panel.setLayout(panelLayout);
		parentLayout.set(panel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, true);
		
		UITableLayout topLayout = new UITableLayout();
		UIPanel top = factory.createPanel(panel, false);
		top.setLayout(topLayout);
		panelLayout.set(top, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, true);
		
		UITableLayout middleLayout = new UITableLayout();
		UIPanel middle = factory.createPanel(panel, false);
		middle.setLayout(middleLayout);
		panelLayout.set(middle, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, true);
		
		UITableLayout bottomLayout = new UITableLayout();
		UIPanel bottom = factory.createPanel(panel, false);
		bottom.setLayout(bottomLayout);
		panelLayout.set(bottom, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, true);
		
		//---------------------------------STRING--------------------------------
		UILabel stringCountLabel = factory.createLabel(top);
		stringCountLabel.setText(TuxGuitar.getProperty("tuning.strings") + ":");
		topLayout.set(stringCountLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, true);
		
		this.stringCountSpinner = factory.createSpinner(top);
		this.stringCountSpinner.setMinimum(MIN_STRINGS);
		this.stringCountSpinner.setMaximum(MAX_STRINGS);
		this.stringCountSpinner.setValue(this.stringCount);
		this.stringCountSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackPropertiesDialog.this.stringCount = TGTrackPropertiesDialog.this.stringCountSpinner.getValue();
				setDefaultTuning(TGTrackPropertiesDialog.this.percussionChannel);
				updateTuningGroup(!TGTrackPropertiesDialog.this.percussionChannel);
			}
		});
		topLayout.set(this.stringCountSpinner, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		//---------------------------------OFFSET--------------------------------
		UILabel offsetLabel = factory.createLabel(middle);
		offsetLabel.setText(TuxGuitar.getProperty("tuning.offset") + ":");
		middleLayout.set(offsetLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, true);
		
		this.offsetCombo = factory.createDropDownSelect(middle);
		for(int i = TGTrack.MIN_OFFSET;i <= TGTrack.MAX_OFFSET;i ++){
			this.offsetCombo.addItem(new UISelectItem<Integer>(Integer.toString(i), i));
		}
		this.offsetCombo.setSelectedValue(track.getOffset());
		middleLayout.set(this.offsetCombo, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
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
				UICheckBox stringTransposition = TGTrackPropertiesDialog.this.stringTransposition;
				UICheckBox stringTranspositionApplyToChords = TGTrackPropertiesDialog.this.stringTranspositionApplyToChords;
				UICheckBox stringTranspositionTryKeepString = TGTrackPropertiesDialog.this.stringTranspositionTryKeepString;
				stringTranspositionApplyToChords.setEnabled((stringTransposition.isEnabled() && stringTransposition.isSelected()));
				stringTranspositionTryKeepString.setEnabled((stringTransposition.isEnabled() && stringTransposition.isSelected()));
			}
		});
	}
	
	private void initButtons() {
		UIFactory factory = this.getUIFactory();
		UITableLayout dialogLayout = (UITableLayout) this.dialog.getLayout();
		
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = factory.createPanel(this.dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true, 1, 2);
		
		UIButton buttonOK = factory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateTrackProperties();
				TGTrackPropertiesDialog.this.dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);
		
		UIButton buttonCancel = factory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackPropertiesDialog.this.dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
	}
	
	private void initInstrumentFields(TGTrack track) {
		UIFactory factory = this.getUIFactory();
		UITableLayout dialogLayout = (UITableLayout) this.dialog.getLayout();
		
		UITableLayout legendLayout = new UITableLayout();
		UILegendPanel legendPanel = factory.createLegendPanel(this.dialog);
		legendPanel.setLayout(legendLayout);
		legendPanel.setText(TuxGuitar.getProperty("instrument"));
		dialogLayout.set(legendPanel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UITableLayout topLayout = new UITableLayout();
		UIPanel top = factory.createPanel(legendPanel, false);
		top.setLayout(topLayout);
		legendLayout.set(top, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, true);
		
		//------------Instrument Combo-------------------------------------
		UILabel instrumentLabel = factory.createLabel(top);
		instrumentLabel.setText(TuxGuitar.getProperty("instrument") + ":");
		topLayout.set(instrumentLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		this.instrumentCombo = factory.createDropDownSelect(top);
		this.instrumentCombo.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				checkPercussionChannel();
			}
		});
		topLayout.set(this.instrumentCombo, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		this.loadChannels(track.getChannelId());
		
		TGActionProcessorListener settingsListener = new TGActionProcessorListener(this.context.getContext(), TGToggleChannelsDialogAction.NAME);
		settingsListener.setAttribute(TGViewContext.ATTRIBUTE_PARENT2, TGTrackPropertiesDialog.this.dialog);
		
		UIButton settings = factory.createButton(top);
		settings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
		settings.setToolTipText(TuxGuitar.getProperty("settings"));
		settings.addSelectionListener(settingsListener);
		topLayout.set(settings, 2, 2, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
	}
	
	protected void loadChannels(Integer selectedChannelId){
		this.instrumentCombo.removeItems();
		this.instrumentCombo.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("track.instrument.default-select-option"), null));
		
		List<TGChannel> channels = findSongManager().getChannels(findSong());
		for(TGChannel channel : channels) {
			this.instrumentCombo.addItem(new UISelectItem<Integer>(channel.getName(), channel.getChannelId()));
		}
		
		this.instrumentCombo.setSelectedValue(selectedChannelId);
	}
	
	protected void reloadChannels(){
		loadChannels(getSelectedChannelId());
	}
	
	protected void checkPercussionChannel(){
		boolean percussionChannel = findSongManager().isPercussionChannel(findSong(), getSelectedChannelId() );
		if( this.percussionChannel != percussionChannel ){
			this.setDefaultTuning(percussionChannel);
			this.updateTuningGroup(!percussionChannel);
		}
		this.percussionChannel = percussionChannel;
	}
	
	protected int getSelectedChannelId(){
		Integer selectedValue = this.instrumentCombo.getSelectedValue();	
		return (selectedValue != null ? selectedValue : -1);
	}
	
	protected void updateTrackProperties() {
		final TGSongManager songManager = this.findSongManager();
		final TGSong song = this.findSong();
		final TGTrack track = this.findTrack();
		
		final String trackName = this.nameText.getText();
		
		final List<TGString> strings = new ArrayList<TGString>();
		for (int i = 0; i < this.stringCount; i++) {
			strings.add(TGSongManager.newString(findSongManager().getFactory(),(i + 1), this.stringCombos[i].getSelectedValue()));
		}
		
		final Integer channelId = getSelectedChannelId();
		final TGChannel channel = songManager.getChannel(song, channelId);
		final Integer offset = ((songManager.isPercussionChannel(song, channelId)) ? 0 : this.offsetCombo.getSelectedValue());
		
		final TGColor trackColor = this.trackColor;
		final boolean infoChanges = hasInfoChanges(track,trackName,trackColor,offset);
		final boolean tuningChanges = hasTuningChanges(track,strings);
		final boolean channelChanges = hasChannelChanges(track, channelId);
		final boolean transposeStrings = shouldTransposeStrings(track, channelId);
		final boolean transposeApplyToChords = (transposeStrings && this.stringTranspositionApplyToChords.isSelected());
		final boolean transposeTryKeepString = (transposeStrings && this.stringTranspositionTryKeepString.isSelected());
		
		if( infoChanges || tuningChanges || channelChanges){
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGChangeTrackPropertiesAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
			
			if( infoChanges ) {
				tgActionProcessor.setAttribute(TGChangeTrackPropertiesAction.ATTRIBUTE_UPDATE_INFO, Boolean.TRUE);
				tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_NAME, trackName);
				tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_COLOR, trackColor);
				tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_OFFSET, offset);
			}
			
			if( tuningChanges ){
				tgActionProcessor.setAttribute(TGChangeTrackPropertiesAction.ATTRIBUTE_UPDATE_TUNING, Boolean.TRUE);
				tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_STRINGS, strings);
				tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_STRINGS, transposeStrings);
				tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_TRY_KEEP_STRINGS, transposeTryKeepString);
				tgActionProcessor.setAttribute(TGChangeTrackTuningAction.ATTRIBUTE_TRANSPOSE_APPLY_TO_CHORDS, transposeApplyToChords);
			}
			
			if( channelChanges ){
				tgActionProcessor.setAttribute(TGChangeTrackPropertiesAction.ATTRIBUTE_UPDATE_CHANNEL, Boolean.TRUE);
				tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
			}
			
			tgActionProcessor.process();
		}
	}
	
	protected boolean shouldTransposeStrings(TGTrack track, int selectedChannelId){
		if( this.stringTransposition.isSelected()){
			boolean percussionChannelNew = findSongManager().isPercussionChannel(track.getSong(), selectedChannelId);
			boolean percussionChannelOld = findSongManager().isPercussionChannel(track.getSong(), track.getChannelId());
			
			return (!percussionChannelNew && !percussionChannelOld);
		}
		return false;
	}
	
	protected boolean hasInfoChanges(TGTrack track,String name,TGColor color,int offset){
		if(!name.equals(track.getName())){
			return true;
		}
		if(!color.isEqual(track.getColor())){
			return true;
		}
		if(offset != track.getOffset()){
			return true;
		}
		return false;
	}
	
	protected boolean hasChannelChanges(TGTrack track,int channelId){
		return ( track.getChannelId() != channelId );
	}
	
	protected boolean hasTuningChanges(TGTrack track, List<TGString> newStrings){
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
	
	protected void setButtonColor(UIButton button){
		button.setFgColor(null);
		this.disposeButtonColor();
		this.colorButtonValue = getUIFactory().createColor(this.trackColor.getR(), this.trackColor.getG(), this.trackColor.getB());
		button.setFgColor(this.colorButtonValue);
	}
	
	protected void disposeButtonColor(){
		if( this.colorButtonValue != null && !this.colorButtonValue.isDisposed()){
			this.colorButtonValue.dispose();
			this.colorButtonValue = null;
		}
	}
	
	protected void updateTuningGroup(boolean enabled) {
		for (int i = 0; i < this.tempStrings.size(); i++) {
			TGString string = (TGString)this.tempStrings.get(i);
			this.stringCombos[i].setSelectedValue(string.getValue());
			this.stringCombos[i].setVisible(true);
			this.stringCombos[i].setEnabled(enabled);
		}
		
		for (int i = this.tempStrings.size(); i < MAX_STRINGS; i++) {
			this.stringCombos[i].setSelectedValue(0);
			this.stringCombos[i].setVisible(false);
		}
		this.offsetCombo.setEnabled(enabled);
		this.stringTransposition.setEnabled(enabled);
		this.stringTranspositionApplyToChords.setEnabled(enabled && this.stringTransposition.isSelected());
		this.stringTranspositionTryKeepString.setEnabled(enabled && this.stringTransposition.isSelected());
	}
	
	protected void initTempStrings(List<TGString> realStrings) {
		this.tempStrings = new ArrayList<TGString>();
		for (int i = 0; i < realStrings.size(); i++) {
			TGString realString = (TGString) realStrings.get(i);
			this.tempStrings.add(realString.clone(findSongManager().getFactory()));
		}
	}
	
	protected void setDefaultTuning( boolean percussionChannel ) {
		this.tempStrings.clear();
		if( percussionChannel ) {
			this.tempStrings.addAll(findSongManager().createPercussionStrings(this.stringCount));
		} else {
			this.tempStrings.addAll(findSongManager().createDefaultInstrumentStrings(this.stringCount));
		}
	}
	
	protected String[] getAllValueNames() {
		String[] valueNames = new String[MAX_NOTES * MAX_OCTAVES];
		for (int i = 0; i < valueNames.length; i++) {
			valueNames[i] =  NOTE_NAMES[ (i -  ((i / MAX_NOTES) * MAX_NOTES) ) ] + (i / MAX_NOTES);
		}
		return valueNames;
	}
	
	public void addListeners(){
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}
	
	public void removeListeners(){
		TuxGuitar.getInstance().getEditorManager().removeUpdateListener(this);
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
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context.getContext()).getFactory();
	}
	
	public void updateItems(){
		if( this.dialog != null && !this.dialog.isDisposed() ){
			this.reloadChannels();
		}
	}
	
	public void createSyncProcesses() {		
		this.updateItemsProcess = new TGSyncProcessLocked(this.context.getContext(), new Runnable() {
			public void run() {
				updateItems();
			}
		});
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.updateItemsProcess.process();
		}
	}
	
	public void processEvent(final TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
	}
}