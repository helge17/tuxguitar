package app.tuxguitar.app.view.dialog.track;

import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionProcessorListener;
import app.tuxguitar.app.action.impl.track.TGOpenTrackTuningDialogAction;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import app.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.track.TGSetTrackChannelAction;
import app.tuxguitar.editor.action.track.TGSetTrackInfoAction;
import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.editor.util.TGProcess;
import app.tuxguitar.editor.util.TGSyncProcessLocked;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGColor;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.chooser.UIColorChooser;
import app.tuxguitar.ui.chooser.UIColorChooserHandler;
import app.tuxguitar.ui.event.UICloseEvent;
import app.tuxguitar.ui.event.UICloseListener;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIFocusEvent;
import app.tuxguitar.ui.event.UIFocusLostListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIReadOnlyTextField;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UISpinner;
import app.tuxguitar.ui.widget.UITextField;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGMusicKeyUtils;

public class TGTrackPropertiesDialog implements TGEventListener {

	private static final float MINIMUM_LEFT_CONTROLS_WIDTH = 180;
	private static final float MINIMUM_BUTTON_WIDTH = 80;
	private static final float MINIMUM_BUTTON_HEIGHT = 25;
	private static final int MIN_MAXFRET_NUMER = 12;
	private static final int MAX_MAXFRET_NUMBER = 39;

	private TGViewContext context;
	private UIWindow dialog;
	private UITextField nameText;
	private UIButton colorButton;
	private UIColor colorButtonBg;
	private UIDropDownSelect<Integer> channelSelect;
	private UIReadOnlyTextField tuningText;
	private UISpinner maxFretNumber;
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
				TGTrackPropertiesDialog.this.updateTrackNameMaxfret();
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
				TGTrackPropertiesDialog.this.updateTrackNameMaxfret();
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
		this.updateChannelSelect();
		legendLayout.set(this.channelSelect, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		UIButton settings = factory.createButton(legendPanel);
		settings.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.SETTINGS));
		settings.setToolTipText(TuxGuitar.getProperty("settings"));
		settings.addSelectionListener(this.createOpenViewAction(TGToggleChannelsDialogAction.NAME));
		legendLayout.set(settings, 3, 3, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

		//------------Tuning -------------------------------------
		UILabel tuningLabel = factory.createLabel(legendPanel);
		tuningLabel.setText(TuxGuitar.getProperty("tuning") + ":");
		legendLayout.set(tuningLabel, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

		this.tuningText = factory.createReadOnlyTextField(legendPanel);
		legendLayout.set(this.tuningText, 4, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		UIButton tuningSettings = factory.createButton(legendPanel);
		tuningSettings.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.SETTINGS));
		tuningSettings.setToolTipText(TuxGuitar.getProperty("settings"));
		tuningSettings.addSelectionListener(this.createOpenViewAction(TGOpenTrackTuningDialogAction.NAME));
		legendLayout.set(tuningSettings, 4, 3, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

		//-------------- max fret number ---------------
		UILabel maxFretLabel = factory.createLabel(legendPanel);
		maxFretLabel.setText(TuxGuitar.getProperty("track.maxFret") + ":");
		legendLayout.set(maxFretLabel, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

		this.maxFretNumber = factory.createSpinner(legendPanel);
		this.maxFretNumber.setMinimum(MIN_MAXFRET_NUMER);
		this.maxFretNumber.setMaximum(MAX_MAXFRET_NUMBER);
		this.maxFretNumber.setValue(track.getMaxFret());
		legendLayout.set(this.maxFretNumber,5,2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

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
				TGTrackPropertiesDialog.this.updateTrackNameMaxfret();
				TGTrackPropertiesDialog.this.dialog.dispose();
			}
		});
		buttonsLayout.set(buttonClose, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);
		buttonsLayout.set(buttonClose, UITableLayout.MARGIN_RIGHT, 0f);
	}

	public void updateItems(){
		if( this.dialog != null && !this.dialog.isDisposed() ){
			this.updateChannelSelect();
			this.updateTuningText();
			this.updateColorButton();
			this.updateMaxFretControl();
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

	private void updateTuningText() {
		StringBuilder label = new StringBuilder();
		boolean isValid = true;
		List<TGString> tuning = this.findTrack().getStrings();
		for(int i = 0 ; i < tuning.size(); i ++) {
			if( i > 0 ) {
				label.append(" ");
			}
			String noteName = TGMusicKeyUtils.sharpNoteName(tuning.get(tuning.size() - i - 1).getValue());
			isValid &= (noteName!=null);
			label.append(noteName);
		}

		boolean enabled = !this.isPercussionChannel();

		this.tuningText.setText((enabled && isValid) ? label.toString() : "");
		this.tuningText.setEnabled(enabled);
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

	private void updateMaxFretControl() {
		this.maxFretNumber.setVisible(!findTrack().isPercussion());
	}

	private int getSelectedChannelId(){
		Integer selectedValue = this.channelSelect.getSelectedValue();
		return (selectedValue != null ? selectedValue : -1);
	}

	private void updateTrackNameMaxfret() {
		this.updateTrackInfo(this.nameText.getText(), this.findTrack().getColor(), this.maxFretNumber.getValue());
	}

	private void updateTrackColor(UIColorModel selection) {
		TGColor tgColor = this.findSongManager().getFactory().newColor();
		tgColor.setR(selection.getRed());
		tgColor.setG(selection.getGreen());
		tgColor.setB(selection.getBlue());

		this.updateTrackInfo(this.findTrack().getName(), tgColor, this.findTrack().getMaxFret());
	}

	private void updateTrackInfo(String name, TGColor color, int maxFret) {
		TGSong song = this.findSong();
		TGTrack track = this.findTrack();

		if( this.hasInfoChanges(name, color, maxFret) ){
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGSetTrackInfoAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
			tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_NAME, name);
			tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_COLOR, color);
			tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_OFFSET, track.getOffset());
			tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_MAXFRET, maxFret);
			// checking if notes may be lost (reduction of maxFret)
			if ((maxFret<track.getMaxFret()) && (maxFret<track.getHighestFret())) {
				// warning dialog: OK/Cancel
				TGActionProcessor tgActionProcessorConfirm = new TGActionProcessor(this.context.getContext(), TGOpenViewAction.NAME);
				tgActionProcessorConfirm.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGConfirmDialogController());
				tgActionProcessorConfirm.setAttribute(TGConfirmDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("track.confirm.reduce-fret-number", new String[] {String.valueOf(maxFret)}));
				tgActionProcessorConfirm.setAttribute(TGConfirmDialog.ATTRIBUTE_STYLE, TGConfirmDialog.BUTTON_YES | TGConfirmDialog.BUTTON_CANCEL);
				tgActionProcessorConfirm.setAttribute(TGConfirmDialog.ATTRIBUTE_DEFAULT_BUTTON, TGConfirmDialog.BUTTON_CANCEL);
				tgActionProcessorConfirm.setAttribute(TGConfirmDialog.ATTRIBUTE_RUNNABLE_YES,
						new Runnable() {
							public void run() {
								tgActionProcessor.process();
							}
						});
				tgActionProcessorConfirm.process();
			}
			else {
				// no risk to delete any note, just proceed
				tgActionProcessor.process();
			}
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

	private boolean hasInfoChanges(String name, TGColor color, int maxFret){
		TGTrack track = this.findTrack();
		if(!name.equals(track.getName())){
			return true;
		}
		if(!color.isEqual(track.getColor())){
			return true;
		}
		if (maxFret != track.getMaxFret()) {
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