package org.herac.tuxguitar.app.view.dialog.track;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.util.TGMusicKeyUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
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

public class TGTrackPropertiesDialog implements TGEventListener {
	
	private static final String[] NOTE_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_TUNING);
	private static final int MINIMUM_LEFT_CONTROLS_WIDTH = 180;
	private static final int MINIMUM_BUTTON_WIDTH = 80;
	private static final int MINIMUM_BUTTON_HEIGHT = 25;
	private static final int MAX_STRINGS = 7;
	private static final int MIN_STRINGS = 4;
	private static final int MAX_OCTAVES = 10;
	private static final int MAX_NOTES = 12;
	
	private TGViewContext context;
	private Shell dialog;
	private Text nameText;
	private TGColor trackColor;
	private List<TGString> tempStrings;
	private Button stringTransposition;
	private Button stringTranspositionTryKeepString;
	private Button stringTranspositionApplyToChords;
	private Spinner stringCountSpinner;
	private Combo[] stringCombos = new Combo[MAX_STRINGS];
	private Combo offsetCombo;
	private int stringCount;
	private Combo instrumentCombo;
	private Color colorButtonValue;
	private boolean percussionChannel;
	private TGProcess updateItemsProcess;
	
	public TGTrackPropertiesDialog(TGViewContext context) {
		this.context = context;
		this.createSyncProcesses();
	}
	
	public void show() {
		TGSongManager songManager = this.findSongManager();
		TGTrack track = this.findTrack();
		
		Shell parent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		
		this.stringCount = track.getStrings().size();
		this.trackColor = track.getColor().clone(songManager.getFactory());
		this.percussionChannel = songManager.isPercussionChannel(track.getSong(), track.getChannelId());
		this.initTempStrings(track.getStrings());
		
		this.dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.dialog.setLayout(new GridLayout(2,false));
		this.dialog.setText(TuxGuitar.getProperty("track.properties"));
		
		Composite left = new Composite(this.dialog,SWT.NONE);
		left.setLayout(new GridLayout());
		left.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Composite right = new Composite(this.dialog,SWT.NONE);
		right.setLayout(new GridLayout());
		right.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Composite bottom = new Composite(this.dialog, SWT.NONE);
		bottom.setLayout(new GridLayout(2,false));
		bottom.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true,2,1));
		
		//GENERAL
		initTrackInfo(makeGroup(left,1,TuxGuitar.getProperty("track.properties.general")), track);
		
		//INSTRUMENT
		initInstrumentFields(makeGroup(left,1,TuxGuitar.getProperty("instrument")), track);
		
		//TUNING
		initTuningInfo(makeGroup(right,2,TuxGuitar.getProperty("tuning")), track);
		
		//BUTTONS
		initButtons(bottom);
		
		//LISTENERS
		initListeners();
		
		updateTuningGroup(!this.percussionChannel);
		
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK /* | DialogUtils.OPEN_STYLE_WAIT*/);
	}
	
	private Group makeGroup(Composite parent,int horizontalSpan,String text){
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(makeGridData(horizontalSpan));
		group.setText(text);
		
		return group;
	}
	
	private GridData makeGridData(int horizontalSpan){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.horizontalSpan = horizontalSpan;
		return data;
	}
	
	public GridData getButtonsData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = MINIMUM_BUTTON_WIDTH;
		data.minimumHeight = MINIMUM_BUTTON_HEIGHT;
		return data;
	}
	
	private void initListeners() {
		this.addListeners();
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				removeListeners();
			}
		});
	}
	
	private void initTrackInfo(Composite composite,TGTrack track) {
		composite.setLayout(new GridLayout());
		Composite top = new Composite(composite, SWT.NONE);
		top.setLayout(new GridLayout());
		top.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		Composite bottom = new Composite(composite, SWT.NONE);
		bottom.setLayout(new GridLayout());
		bottom.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,true));
		
		//-----------------------NAME---------------------------------
		Label nameLabel = new Label(top, SWT.NONE);
		nameLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		nameLabel.setText(TuxGuitar.getProperty("track.name") + ":");
		
		this.nameText = new Text(top, SWT.BORDER);
		this.nameText.setLayoutData(getAlignmentData(MINIMUM_LEFT_CONTROLS_WIDTH,SWT.FILL));
		this.nameText.setText(track.getName());
		
		//-----------------------COLOR---------------------------------
		Label colorLabel = new Label(bottom, SWT.NONE);
		colorLabel.setText(TuxGuitar.getProperty("track.color") + ":");
		colorLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		
		final Button colorButton = new Button(bottom, SWT.PUSH);
		colorButton.setLayoutData(getAlignmentData(MINIMUM_LEFT_CONTROLS_WIDTH,SWT.FILL));
		colorButton.setText(TuxGuitar.getProperty("choose"));
		colorButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ColorDialog dlg = new ColorDialog(TGTrackPropertiesDialog.this.dialog);
				dlg.setRGB(TGTrackPropertiesDialog.this.dialog.getDisplay().getSystemColor(SWT.COLOR_BLACK).getRGB());
				dlg.setText(TuxGuitar.getProperty("choose-color"));
				RGB rgb = dlg.open();
				if (rgb != null) {
					TGTrackPropertiesDialog.this.trackColor.setR(rgb.red);
					TGTrackPropertiesDialog.this.trackColor.setG(rgb.green);
					TGTrackPropertiesDialog.this.trackColor.setB(rgb.blue);
					TGTrackPropertiesDialog.this.setButtonColor(colorButton);
				}
			}
		});
		colorButton.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				TGTrackPropertiesDialog.this.disposeButtonColor();
			}
		});
		this.setButtonColor(colorButton);
	}
	
	private void initTuningInfo(Composite composite,TGTrack track) {
		composite.setLayout(new GridLayout(2,false));
		initTuningData(composite,track);
		initTuningCombos(composite);
	}
	
	private void initTuningCombos(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,false,true));
		String[] tuningTexts = getAllValueNames();
		for (int i = 0; i < MAX_STRINGS; i++) {
			this.stringCombos[i] = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
			this.stringCombos[i].setItems(tuningTexts);
		}
	}
	
	private void initTuningData(Composite parent,TGTrack track) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		
		Composite top = new Composite(composite, SWT.NONE);
		top.setLayout(new GridLayout());
		top.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		
		Composite middle = new Composite(composite, SWT.NONE);
		middle.setLayout(new GridLayout());
		middle.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		
		Composite bottom = new Composite( composite , SWT.NONE );
		bottom.setLayout( new GridLayout() );
		bottom.setLayoutData( new GridData(SWT.FILL,SWT.TOP,true,true) );
		
		//---------------------------------STRING--------------------------------
		Label stringCountLabel = new Label(top, SWT.NONE);
		stringCountLabel.setText(TuxGuitar.getProperty("tuning.strings") + ":");
		stringCountLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,true));
		
		this.stringCountSpinner = new Spinner(top, SWT.BORDER);
		this.stringCountSpinner.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		this.stringCountSpinner.setMinimum(MIN_STRINGS);
		this.stringCountSpinner.setMaximum(MAX_STRINGS);
		this.stringCountSpinner.setSelection(this.stringCount);
		this.stringCountSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TGTrackPropertiesDialog.this.stringCount = TGTrackPropertiesDialog.this.stringCountSpinner.getSelection();
				setDefaultTuning(TGTrackPropertiesDialog.this.percussionChannel);
				updateTuningGroup(!TGTrackPropertiesDialog.this.percussionChannel);
			}
		});
		
		//---------------------------------OFFSET--------------------------------
		Label offsetLabel = new Label(middle, SWT.NONE);
		offsetLabel.setText(TuxGuitar.getProperty("tuning.offset") + ":");
		offsetLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,true));
		
		this.offsetCombo = new Combo(middle, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.offsetCombo.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		for(int i = TGTrack.MIN_OFFSET;i <= TGTrack.MAX_OFFSET;i ++){
			this.offsetCombo.add(Integer.toString(i));
			if(i == track.getOffset()){
				this.offsetCombo.select(i - TGTrack.MIN_OFFSET);
			}
		}
		
		//---------------------------------OPTIONS----------------------------------
		this.stringTransposition = new Button( bottom , SWT.CHECK );
		this.stringTransposition.setLayoutData( new GridData(SWT.FILL,SWT.CENTER,true,true) );
		this.stringTransposition.setText(TuxGuitar.getProperty("tuning.strings.transpose"));
		this.stringTransposition.setSelection( true );
		
		this.stringTranspositionApplyToChords = new Button( bottom , SWT.CHECK );
		this.stringTranspositionApplyToChords.setLayoutData( new GridData(SWT.FILL,SWT.CENTER,true,true) );
		this.stringTranspositionApplyToChords.setText(TuxGuitar.getProperty("tuning.strings.transpose.apply-to-chords"));
		this.stringTranspositionApplyToChords.setSelection( true );
		
		this.stringTranspositionTryKeepString = new Button( bottom , SWT.CHECK );
		this.stringTranspositionTryKeepString.setLayoutData( new GridData(SWT.FILL,SWT.CENTER,true,true) );
		this.stringTranspositionTryKeepString.setText(TuxGuitar.getProperty("tuning.strings.transpose.try-keep-strings"));
		this.stringTranspositionTryKeepString.setSelection( true );
		
		this.stringTransposition.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button stringTransposition = TGTrackPropertiesDialog.this.stringTransposition;
				Button stringTranspositionApplyToChords = TGTrackPropertiesDialog.this.stringTranspositionApplyToChords;
				Button stringTranspositionTryKeepString = TGTrackPropertiesDialog.this.stringTranspositionTryKeepString;
				stringTranspositionApplyToChords.setEnabled( ( stringTransposition.isEnabled() && stringTransposition.getSelection() ) );
				stringTranspositionTryKeepString.setEnabled( ( stringTransposition.isEnabled() && stringTransposition.getSelection() ) );
			}
		});
	}
	
	private GridData getAlignmentData(int minimumWidth,int horizontalAlignment){
		GridData data = new GridData();
		data.minimumWidth = minimumWidth;
		data.horizontalAlignment = horizontalAlignment;
		data.verticalAlignment = SWT.DEFAULT;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		return data;
	}
	
	private void initButtons(final Composite parent) {
		Button buttonOK = new Button(parent, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonsData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				updateTrackProperties();
				TGTrackPropertiesDialog.this.dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(parent, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonsData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TGTrackPropertiesDialog.this.dialog.dispose();
			}
		});
		
		this.dialog.setDefaultButton( buttonOK );
	}
	
	private void initInstrumentFields(Composite composite,TGTrack track) {
		composite.setLayout(new GridLayout());
		
		Composite top = new Composite(composite, SWT.NONE);
		top.setLayout(new GridLayout(2,false));
		top.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		
		//------------Instrument Combo-------------------------------------
		Label instrumentLabel = new Label(top, SWT.NONE);
		instrumentLabel.setText(TuxGuitar.getProperty("instrument") + ":");
		instrumentLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,2,1));
		
		this.instrumentCombo = new Combo(top, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.instrumentCombo.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		this.instrumentCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				checkPercussionChannel();
			}
		});
		this.loadChannels(track.getChannelId());
		
		TGActionProcessorListener settingsListener = new TGActionProcessorListener(this.context.getContext(), TGToggleChannelsDialogAction.NAME);
		settingsListener.setAttribute(TGViewContext.ATTRIBUTE_PARENT, TGTrackPropertiesDialog.this.dialog);
		
		Button settings = new Button(top, SWT.PUSH);
		settings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
		settings.setToolTipText(TuxGuitar.getProperty("settings"));
		settings.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,false));
		settings.addSelectionListener(settingsListener);
	}
	
	
	
	protected void loadChannels(int selectedChannelId){
		List<Integer> tgChannelsData = new ArrayList<Integer>();
		List<TGChannel> tgChannelsAvailable = findSongManager().getChannels(findSong());
		
		Combo tgChannelsCombo = this.instrumentCombo;
		tgChannelsCombo.removeAll();
		tgChannelsCombo.add(TuxGuitar.getProperty("track.instrument.default-select-option"));
		tgChannelsCombo.select(0);
		tgChannelsData.add(new Integer(-1));
		
		for( int i = 0 ; i < tgChannelsAvailable.size() ; i ++) {
			TGChannel tgChannel = (TGChannel)tgChannelsAvailable.get(i);
			tgChannelsData.add(new Integer(tgChannel.getChannelId()));
			tgChannelsCombo.add(tgChannel.getName());
			
			if( tgChannel.getChannelId() == selectedChannelId ){
				tgChannelsCombo.select(tgChannelsCombo.getItemCount() - 1);
			}
			
		}
		tgChannelsCombo.setData(tgChannelsData);
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
	
	@SuppressWarnings("unchecked")
	protected int getSelectedChannelId(){
		int index = this.instrumentCombo.getSelectionIndex();
		if( index >= 0 ){
			Object data = this.instrumentCombo.getData();
			if( data instanceof List && ((List<Integer>)data).size() > index ){
				return ((Integer)((List<Integer>)data).get(index)).intValue();
			}
		}
		return -1;
	}
	
	protected void updateTrackProperties() {
		final TGSongManager songManager = this.findSongManager();
		final TGSong song = this.findSong();
		final TGTrack track = this.findTrack();
		
		final String trackName = this.nameText.getText();
		
		final List<TGString> strings = new ArrayList<TGString>();
		for (int i = 0; i < this.stringCount; i++) {
			strings.add(TGSongManager.newString(findSongManager().getFactory(),(i + 1), this.stringCombos[i].getSelectionIndex()));
		}
		
		final Integer channelId = getSelectedChannelId();
		final TGChannel channel = songManager.getChannel(song, channelId);
		final Integer offset = ((songManager.isPercussionChannel(song, channelId)) ? 0 : (TGTrack.MIN_OFFSET + this.offsetCombo.getSelectionIndex()));
		
		final TGColor trackColor = this.trackColor;
		final boolean infoChanges = hasInfoChanges(track,trackName,trackColor,offset);
		final boolean tuningChanges = hasTuningChanges(track,strings);
		final boolean channelChanges = hasChannelChanges(track, channelId);
		final boolean transposeStrings = shouldTransposeStrings(track, channelId);
		final boolean transposeApplyToChords = (transposeStrings && this.stringTranspositionApplyToChords.getSelection());
		final boolean transposeTryKeepString = (transposeStrings && this.stringTranspositionTryKeepString.getSelection());
		
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
		if(this.stringTransposition.getSelection()){
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
	
	protected void setButtonColor(Button button){
		Color color = new Color(this.dialog.getDisplay(), this.trackColor.getR(), this.trackColor.getG(), this.trackColor.getB());
		button.setForeground( color );
		this.disposeButtonColor();
		this.colorButtonValue = color;
	}
	
	protected void disposeButtonColor(){
		if(this.colorButtonValue != null && !this.colorButtonValue.isDisposed()){
			this.colorButtonValue.dispose();
			this.colorButtonValue = null;
		}
	}
	
	protected void updateTuningGroup(boolean enabled) {
		for (int i = 0; i < this.tempStrings.size(); i++) {
			TGString string = (TGString)this.tempStrings.get(i);
			this.stringCombos[i].select(string.getValue());
			this.stringCombos[i].setVisible(true);
			this.stringCombos[i].setEnabled(enabled);
		}
		
		for (int i = this.tempStrings.size(); i < MAX_STRINGS; i++) {
			this.stringCombos[i].select(0);
			this.stringCombos[i].setVisible(false);
		}
		this.offsetCombo.setEnabled(enabled);
		this.stringTransposition.setEnabled(enabled);
		this.stringTranspositionApplyToChords.setEnabled(enabled && this.stringTransposition.getSelection());
		this.stringTranspositionTryKeepString.setEnabled(enabled && this.stringTransposition.getSelection());
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