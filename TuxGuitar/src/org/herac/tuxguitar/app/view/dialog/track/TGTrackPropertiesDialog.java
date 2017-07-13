package org.herac.tuxguitar.app.view.dialog.track;

import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.track.TGOpenTrackTuningDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGMusicKeyUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.util.TGProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.track.TGSetTrackChannelAction;
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
import org.herac.tuxguitar.ui.event.UICloseEvent;
import org.herac.tuxguitar.ui.event.UICloseListener;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UIFocusEvent;
import org.herac.tuxguitar.ui.event.UIFocusLostListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIReadOnlyTextField;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class TGTrackPropertiesDialog implements TGEventListener {
	
	private static final String[] NOTE_NAMES = TGMusicKeyUtils.getSharpKeyNames(TGMusicKeyUtils.PREFIX_TUNING);
	private static final float MINIMUM_LEFT_CONTROLS_WIDTH = 180;
	private static final float MINIMUM_BUTTON_WIDTH = 80;
	private static final float MINIMUM_BUTTON_HEIGHT = 25;
	
	private TGViewContext context;
	private UIWindow dialog;
	private UITextField nameText;
	private UIButton colorButton;
	private UIColor colorButtonBg;
	private UIDropDownSelect<Integer> channelSelect;
	private UIReadOnlyTextField tuningText;
	private UIButton tuningSettings;
	private TGProcess updateItemsProcess;
	
	public TGTrackPropertiesDialog(TGViewContext context) {
		this.context = context;
		this.createSyncProcesses();
	}
	
	public void show() {
		TGTrack track = this.findTrack();
		
		UIFactory factory = this.getUIFactory();
		UIWindow parent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = factory.createWindow(parent, true, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("track.properties"));
		
		//GENERAL
		this.initTrackInfo(track);
		
		//BUTTONS
		this.initButtons();
		
		//LISTENERS
		this.initListeners();
		
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private void initListeners() {
		this.addListeners();
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				removeListeners();
			}
		});
		this.dialog.addCloseListener(new UICloseListener() {
			public void onClose(UICloseEvent event) {
				TGTrackPropertiesDialog.this.updateTrackName();
				TGTrackPropertiesDialog.this.dialog.dispose();
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
		
		//-----------------------NAME---------------------------------
		UILabel nameLabel = factory.createLabel(legendPanel);
		nameLabel.setText(TuxGuitar.getProperty("track.name") + ":");
		legendLayout.set(nameLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		this.nameText = factory.createTextField(legendPanel);
		this.nameText.setText(track.getName());
		this.nameText.addFocusLostListener(new UIFocusLostListener() {
			public void onFocusLost(UIFocusEvent event) {
				TGTrackPropertiesDialog.this.updateTrackName();
			}
		});
		
		legendLayout.set(this.nameText, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, MINIMUM_LEFT_CONTROLS_WIDTH, null, null);
		
		//-----------------------COLOR---------------------------------
		UILabel colorLabel = factory.createLabel(legendPanel);
		colorLabel.setText(TuxGuitar.getProperty("track.color") + ":");
		legendLayout.set(colorLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		this.colorButton = factory.createButton(legendPanel);
		this.colorButton.setText(TuxGuitar.getProperty("choose"));
		this.colorButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGColor tgColor = findTrack().getColor();
				UIColorModel colorModel = new UIColorModel();
				colorModel.setRed(tgColor.getR());
				colorModel.setGreen(tgColor.getG());
				colorModel.setBlue(tgColor.getB());
				
				UIColorChooser colorChooser = factory.createColorChooser(TGTrackPropertiesDialog.this.dialog);
				colorChooser.setDefaultModel(colorModel);
				colorChooser.setText(TuxGuitar.getProperty("choose-color"));
				colorChooser.choose(new UIColorChooserHandler() {
					public void onSelectColor(UIColorModel selection) {
						if( selection != null ) {
							TGTrackPropertiesDialog.this.updateTrackColor(selection);
						}
					}
				});
			}
		});
		this.colorButton.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				TGTrackPropertiesDialog.this.disposeColorButtonBackground();
			}
		});
		legendLayout.set(this.colorButton, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, MINIMUM_LEFT_CONTROLS_WIDTH, null, null);
		
		//------------Instrument Combo-------------------------------------
		UILabel instrumentLabel = factory.createLabel(legendPanel);
		instrumentLabel.setText(TuxGuitar.getProperty("instrument") + ":");
		legendLayout.set(instrumentLabel, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		this.channelSelect = factory.createDropDownSelect(legendPanel);
		this.channelSelect.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackPropertiesDialog.this.updateTrackChannel();
			}
		});
		legendLayout.set(this.channelSelect, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		UIButton settings = factory.createButton(legendPanel);
		settings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
		settings.setToolTipText(TuxGuitar.getProperty("settings"));
		settings.addSelectionListener(this.createOpenViewAction(TGToggleChannelsDialogAction.NAME));
		legendLayout.set(settings, 3, 3, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		//------------Tuning -------------------------------------
		UILabel tuningLabel = factory.createLabel(legendPanel);
		tuningLabel.setText(TuxGuitar.getProperty("tuning") + ":");
		legendLayout.set(tuningLabel, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		this.tuningText = factory.createReadOnlyTextField(legendPanel);
		legendLayout.set(this.tuningText, 4, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		this.tuningSettings = factory.createButton(legendPanel);
		this.tuningSettings.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
		this.tuningSettings.setToolTipText(TuxGuitar.getProperty("settings"));
		this.tuningSettings.addSelectionListener(this.createOpenViewAction(TGOpenTrackTuningDialogAction.NAME));
		legendLayout.set(this.tuningSettings, 4, 3, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
	}
	
	public TGActionProcessorListener createOpenViewAction(String actionId) {
		TGActionProcessorListener tgActionProcessor = new TGActionProcessorListener(this.context.getContext(), actionId);
		tgActionProcessor.setAttribute(TGViewContext.ATTRIBUTE_PARENT, this.dialog);
		return tgActionProcessor;
	}
	
	private void initButtons() {
		UIFactory factory = this.getUIFactory();
		UITableLayout dialogLayout = (UITableLayout) this.dialog.getLayout();
		
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = factory.createPanel(this.dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonClose = factory.createButton(buttons);
		buttonClose.setText(TuxGuitar.getProperty("close"));
		buttonClose.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackPropertiesDialog.this.updateTrackName();
				TGTrackPropertiesDialog.this.dialog.dispose();
			}
		});
		buttonsLayout.set(buttonClose, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);
		buttonsLayout.set(buttonClose, UITableLayout.MARGIN_RIGHT, 0f);
	}
	
	public void updateItems(){
		if( this.dialog != null && !this.dialog.isDisposed() ){
			this.updateChannelSelect();
			this.updateTuningFields();
			this.updateColorButton();
		}
	}
	
	private void updateColorButton() {
		TGColor tgColor = this.findTrack().getColor();
		
		this.colorButton.setFgColor(null);
		this.disposeColorButtonBackground();
		this.colorButtonBg = getUIFactory().createColor(tgColor.getR(), tgColor.getG(), tgColor.getB());
		this.colorButton.setFgColor(this.colorButtonBg);
	}
	
	private void disposeColorButtonBackground(){
		if( this.colorButtonBg != null && !this.colorButtonBg.isDisposed()){
			this.colorButtonBg.dispose();
			this.colorButtonBg = null;
		}
	}
	
	private void updateTuningFields() {
		StringBuilder label = new StringBuilder(); 
		List<TGString> tuning = this.findTrack().getStrings();
		for(int i = 0 ; i < tuning.size(); i ++) {
			if( i > 0 ) {
				label.append(" ");
			}
			label.append(NOTE_NAMES[tuning.get(tuning.size() - i - 1).getValue() % NOTE_NAMES.length]);
		}
		
		boolean enabled = !this.isPercussionChannel();
		
		this.tuningText.setText(label.toString());
		this.tuningText.setEnabled(enabled);
		this.tuningSettings.setEnabled(enabled);
	}
	
	private void updateChannelSelect() {
		this.channelSelect.setIgnoreEvents(true);
		this.channelSelect.removeItems();
		this.channelSelect.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("track.instrument.default-select-option"), null));
		
		List<TGChannel> channels = findSongManager().getChannels(findSong());
		for(TGChannel channel : channels) {
			this.channelSelect.addItem(new UISelectItem<Integer>(channel.getName(), channel.getChannelId()));
		}
		
		this.channelSelect.setSelectedValue(this.findTrack().getChannelId());
		this.channelSelect.setIgnoreEvents(false);
	}
	
	private int getSelectedChannelId(){
		Integer selectedValue = this.channelSelect.getSelectedValue();	
		return (selectedValue != null ? selectedValue : -1);
	}
	
	private void updateTrackName() {
		this.updateTrackInfo(this.nameText.getText(), this.findTrack().getColor());
	}
	
	private void updateTrackColor(UIColorModel selection) {
		TGColor tgColor = this.findSongManager().getFactory().newColor();
		tgColor.setR(selection.getRed());
		tgColor.setG(selection.getGreen());
		tgColor.setB(selection.getBlue());
		
		this.updateTrackInfo(this.findTrack().getName(), tgColor);
	}
	
	private void updateTrackInfo(String name, TGColor color) {
		TGSong song = this.findSong();
		TGTrack track = this.findTrack();
		
		if( this.hasInfoChanges(name, color) ){
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGSetTrackInfoAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
			tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_NAME, name);
			tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_COLOR, color);
			tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_OFFSET, track.getOffset());
			tgActionProcessor.process();
		}
	}
	
	private void updateTrackChannel() {
		TGSong song = this.findSong();
		TGTrack track = this.findTrack();
		Integer channelId = this.getSelectedChannelId();
		
		if( this.hasChannelChanges(channelId) ){
			TGChannel channel = this.findSongManager().getChannel(song, channelId);
			
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGSetTrackChannelAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
			tgActionProcessor.process();
		}
	}
	
	private boolean hasInfoChanges(String name, TGColor color){
		TGTrack track = this.findTrack();
		if(!name.equals(track.getName())){
			return true;
		}
		if(!color.isEqual(track.getColor())){
			return true;
		}
		return false;
	}
	
	private boolean hasChannelChanges(int channelId){
		return ( this.findTrack().getChannelId() != channelId );
	}
	
	private boolean isPercussionChannel() {
		return this.findSongManager().isPercussionChannel(this.findSong(), this.findTrack().getChannelId());
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