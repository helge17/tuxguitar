package app.tuxguitar.app.view.dialog.track;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionProcessorListener;
import app.tuxguitar.app.action.impl.track.TGOpenTrackTuningDialogAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.track.TGAddTrackAction;
import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.editor.util.TGProcess;
import app.tuxguitar.editor.util.TGSyncProcessLocked;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventException;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.player.base.MidiInstrument;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.helpers.tuning.InstrumentTuning;
import app.tuxguitar.song.helpers.tuning.TuningManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGClef;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIModifyEvent;
import app.tuxguitar.ui.event.UIModifyListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICheckBox;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIReadOnlyTextField;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UITextField;
import app.tuxguitar.ui.widget.UIWindow;

public class TGNewTrackDialog implements TGEventListener {

	private static final float MINIMUM_LEFT_CONTROLS_WIDTH = 180;
	private static final float MINIMUM_BUTTON_WIDTH = 80;
	private static final float MINIMUM_BUTTON_HEIGHT = 25;

	private TGViewContext context;
	private TGSongManager songManager;
	private TGSong song;
	private TGTrack newTrack;
	private UIWindow dialog;
	private UITextField trackName;
	private TGChannel defaultChannel;
	private UIDropDownSelect<TGChannel> channelSelect;
	private UILabel isPercussionLabel;
	private UICheckBox isPercussion;
	private UIDropDownSelect<Integer> midiInstrumentSelect;
	private UISelectItem<Integer> emptyInstrument;
	private UITextField instrumentName;
	private UIDropDownSelect<TGClef> clefSelect;
	private UIDropDownSelect<Integer> keySignatureSelect;
	private UIReadOnlyTextField tuningText;
	private UIButton tuningButton;
	private TGProcess updateTuningProcess;

	public TGNewTrackDialog(TGViewContext context) {
		this.context = context;
		this.createSyncProcesses();
	}

	public void show() {
		this.songManager = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		this.song = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		UIFactory factory = TGApplication.getInstance(this.context.getContext()).getFactory();

		this.newTrack = this.songManager.createTrack(this.song);

		UIWindow parent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		UITableLayout dialogLayout = new UITableLayout();

		this.dialog = factory.createWindow(parent, true, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("track.add"));

		// ----- TRACK NAME -----
		UITableLayout trackLayout = new UITableLayout();
		UILegendPanel trackPanel = factory.createLegendPanel(this.dialog);
		trackPanel.setLayout(trackLayout);
		trackPanel.setText(TuxGuitar.getProperty("track"));
		dialogLayout.set(trackPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel trackNameLabel = factory.createLabel(trackPanel);
		trackNameLabel.setText(TuxGuitar.getProperty("track.name") + ":");
		trackLayout.set(trackNameLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

		this.trackName = factory.createTextField(trackPanel);
		this.trackName.setText(this.newTrack.getName());
		this.trackName.selectAll();
		this.trackName.setFocus();
		trackLayout.set(this.trackName, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, MINIMUM_LEFT_CONTROLS_WIDTH, null, null);

		// ------  INSTRUMENT ------
		UITableLayout instrumentLayout = new UITableLayout();
		UILegendPanel instrumentPanel = factory.createLegendPanel(this.dialog);
		instrumentPanel.setLayout(instrumentLayout);
		instrumentPanel.setText(TuxGuitar.getProperty("instrument"));
		dialogLayout.set(instrumentPanel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		// channel
		this.channelSelect = factory.createDropDownSelect(instrumentPanel);
		// default: new instrument
		this.channelSelect.setSelectedValue(null);
		this.channelSelect.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				updateChannel(TGNewTrackDialog.this.channelSelect.getSelectedItem());
			}
		});
		instrumentLayout.set(this.channelSelect, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		
		// is percussion
		UITableLayout percussionLayout = new UITableLayout();
		UIPanel percussionPanel = factory.createPanel(instrumentPanel, false);
		percussionPanel.setLayout(percussionLayout);
		instrumentLayout.set(percussionPanel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

		this.isPercussion = factory.createCheckBox(percussionPanel);
		this.isPercussion.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGNewTrackDialog.this.updateIsPercussion();
			}
		});
		percussionLayout.set(isPercussion, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);
		this.isPercussionLabel = factory.createLabel(percussionPanel);
		this.isPercussionLabel.setText(TuxGuitar.getProperty("instrument.percussion-channel"));
		percussionLayout.set(this.isPercussionLabel, 1, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, true);

		// midi instrument selection
		UITableLayout midiInstrumentLayout = new UITableLayout();
		UIPanel midiInstrumentPanel = factory.createPanel(instrumentPanel, false);
		midiInstrumentPanel.setLayout(midiInstrumentLayout);
		instrumentLayout.set(midiInstrumentPanel, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);

		UILabel midiInstrumentLabel = factory.createLabel(midiInstrumentPanel);
		midiInstrumentLabel.setText(TuxGuitar.getProperty("track.instrument.midi"));
		midiInstrumentLayout.set(midiInstrumentLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, false);
		this.midiInstrumentSelect = factory.createDropDownSelect(midiInstrumentPanel);
		this.midiInstrumentSelect.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGNewTrackDialog.this.updateProperties();
			}
		});
		midiInstrumentLayout.set(this.midiInstrumentSelect, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		UILabel midiInstrumentNameLabel = factory.createLabel(midiInstrumentPanel);
		midiInstrumentNameLabel.setText(TuxGuitar.getProperty("track.instrument.midi.name"));
		midiInstrumentLayout.set(midiInstrumentNameLabel, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, false);
		this.instrumentName = factory.createTextField(midiInstrumentPanel);
		midiInstrumentLayout.set(this.instrumentName, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		// ------  TRACK PROPERTIES ------
		UITableLayout propertiesLayout = new UITableLayout();
		UILegendPanel propertiesPanel = factory.createLegendPanel(this.dialog);
		propertiesPanel.setLayout(propertiesLayout);
		propertiesPanel.setText(TuxGuitar.getProperty("track.properties"));
		dialogLayout.set(propertiesPanel, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		// clef
		UILabel clefLabel = factory.createLabel(propertiesPanel);
		clefLabel.setText(TuxGuitar.getProperty("composition.clef"));
		propertiesLayout.set(clefLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, true);
		this.clefSelect = factory.createDropDownSelect(propertiesPanel);
		propertiesLayout.set(this.clefSelect, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		// key signature
		UILabel keySignatureLabel = factory.createLabel(propertiesPanel);
		keySignatureLabel.setText(TuxGuitar.getProperty("composition.keysignature"));
		propertiesLayout.set(keySignatureLabel, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, true);
		this.keySignatureSelect = factory.createDropDownSelect(propertiesPanel);
		propertiesLayout.set(this.keySignatureSelect, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		// tuning
		UILabel tuningLabel = factory.createLabel(propertiesPanel);
		tuningLabel.setText(TuxGuitar.getProperty("tuning"));
		propertiesLayout.set(tuningLabel, 3, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, true);
		this.tuningText = factory.createReadOnlyTextField(propertiesPanel);
		this.updateTuningText();
		propertiesLayout.set(this.tuningText, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		this.tuningButton = factory.createButton(propertiesPanel);
		this.tuningButton.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.SETTINGS));
		this.tuningButton.addSelectionListener(this.createOpenViewAction(TGOpenTrackTuningDialogAction.NAME));
		propertiesLayout.set(this.tuningButton, 3, 3, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, false);

		// ------ OK / CANCEL -----
		UITableLayout buttonsLayout = new UITableLayout();
		UIPanel buttonsPanel = factory.createPanel(this.dialog, false);
		buttonsPanel.setLayout(buttonsLayout);
		dialogLayout.set(buttonsPanel, 4, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		UIButton buttonOK = factory.createButton(buttonsPanel);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGNewTrackDialog.this.createNewTrack();
				TGNewTrackDialog.this.dialog.dispose();
			}
		});
		// disable OK button if track name is empty
		this.trackName.addModifyListener(new UIModifyListener() {
			@Override
			public void onModify(UIModifyEvent event) {
				buttonOK.setEnabled(!TGNewTrackDialog.this.trackName.getText().isEmpty());
			}
		});

		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);

		UIButton buttonCancel = factory.createButton(buttonsPanel);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGNewTrackDialog.this.dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, MINIMUM_BUTTON_WIDTH, MINIMUM_BUTTON_HEIGHT, null);

		this.initDropDownLists();
		this.channelSelect.setSelectedValue(null);	// default: new instrument
		this.updateChannel(this.channelSelect.getSelectedItem());
		this.keySignatureSelect.setSelectedValue(songManager.getKeySignatureProposal(song));
		this.updateProperties();

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
	}

	private void addListeners() {
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}

	private void removeListeners() {
		TuxGuitar.getInstance().getEditorManager().removeUpdateListener(this);
	}

	private void initDropDownLists() {
		// channel
		this.channelSelect.addItem(new UISelectItem<TGChannel>(TuxGuitar.getProperty("track.instrument.new"), null));
		List<TGChannel> channels = this.songManager.getChannels(this.song);
		for(TGChannel channel : channels) {
			this.channelSelect.addItem(new UISelectItem<TGChannel>(channel.getName(), channel));
		}
		this.defaultChannel = this.songManager.createChannel();
		// midi instruments
		MidiInstrument[] instruments = MidiPlayer.getInstance(context.getContext()).getInstruments();
		for (int i = 0; i < instruments.length; i++) {
			this.midiInstrumentSelect.addItem(new UISelectItem<Integer>(instruments[i].getName(), i));
		}
		this.emptyInstrument = new UISelectItem<Integer>("",-1);
		// clefs
		for (TGClef newClef : TGClef.CLEFS) {
			this.clefSelect.addItem(new UISelectItem<TGClef>(TuxGuitar.getProperty(newClef.getName()), newClef));
		}
		// key signatures
		this.keySignatureSelect.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.natural"), 0));
		for (int i=1; i<=7; i++) {
			this.keySignatureSelect.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-" + String.valueOf(i)), i));
		}
		for (int i=1; i<=7; i++) {
			this.keySignatureSelect.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-" + String.valueOf(i)), i+7));
		}
	}

	private void updateChannel(UISelectItem<TGChannel> selectedChannelItem) {
		TGChannel selectedChannel = selectedChannelItem == null ? null : selectedChannelItem.getValue();
		boolean isPercussionChannel = (selectedChannel != null) && (selectedChannel.isPercussionChannel());

		this.midiInstrumentSelect.setIgnoreEvents(true);
		this.isPercussion.setIgnoreEvents(true);

		if (selectedChannel == null) {
			// new channel
			this.isPercussion.setSelected(false);
			this.midiInstrumentSelect.setSelectedValue((int)this.defaultChannel.getProgram());
		} else {
			// existing channel
			this.isPercussion.setSelected(isPercussionChannel);
			if (isPercussionChannel) {
				this.midiInstrumentSelect.addItem(emptyInstrument);
				this.midiInstrumentSelect.setSelectedItem(emptyInstrument);
			} else {
				this.midiInstrumentSelect.setSelectedValue((int)selectedChannel.getProgram());
			}
		}
		this.updateProperties();

		this.midiInstrumentSelect.setIgnoreEvents(false);
		this.isPercussion.setIgnoreEvents(false);
	}

	// user checks or un-checks "isPercussion"
	private void updateIsPercussion() {
		this.midiInstrumentSelect.removeItem(this.emptyInstrument);
		if (this.isPercussion.isEnabled()) {
			if (this.isPercussion.isSelected()) {
				this.midiInstrumentSelect.addItem(emptyInstrument);
				this.midiInstrumentSelect.setSelectedItem(emptyInstrument);
			} else {
				this.midiInstrumentSelect.setSelectedValue((int)this.defaultChannel.getProgram());
			}
		}
		this.updateProperties();
	}

	private void updateProperties() {
		TGChannel selectedChannel = this.channelSelect.getSelectedValue();
		UISelectItem<Integer> selectedInstrument = this.midiInstrumentSelect.getSelectedItem();
		if (selectedChannel == null) {
			// new instrument
			//only enable the creation of a percussion channel if none already exist
			boolean percussionTrackExists = false;
			Iterator<TGChannel> songChannels = this.song.getChannels();
			while (songChannels.hasNext() && !percussionTrackExists) {
				percussionTrackExists |= songChannels.next().isPercussionChannel();
			}
			this.isPercussion.setEnabled(!percussionTrackExists);
			this.isPercussionLabel.setEnabled(!percussionTrackExists);
			this.midiInstrumentSelect.setEnabled(true);
			// name
			String prefix = null;
			if (this.isPercussion.isSelected()) {
				prefix = TuxGuitar.getProperty("instrument.drumkit");
			} else {
				prefix = selectedInstrument.getText();
			}
			this.instrumentName.setText(this.songManager.createChannelName(this.song, prefix));
			this.instrumentName.setEnabled(true);
		}
		else {
			// existing instrument
			this.isPercussion.setEnabled(false);
			this.midiInstrumentSelect.setEnabled(false);
			// name
			this.instrumentName.setText(selectedChannel.getName());
			this.instrumentName.setEnabled(false);
		}
		
		// set instrument attributes: clef and tuning
		TuningManager tuningManager = TuningManager.getInstance(this.context.getContext());
		InstrumentTuning instrumentTuning = tuningManager.getInstrumentTuning(selectedInstrument.getValue());
		InstrumentTuning defaultInstrumentTuning = tuningManager.getDefaultInstrumentTuning();
		if (instrumentTuning == null) {
			instrumentTuning = defaultInstrumentTuning;
		}
		TGClef clef = instrumentTuning.getClef() == null ? defaultInstrumentTuning.getClef() : instrumentTuning.getClef();
		this.clefSelect.setSelectedValue(clef);
		this.clefSelect.setEnabled(!this.isPercussion.isSelected());
		int[] stringValues = instrumentTuning.getTuning() == null ? defaultInstrumentTuning.getTuning().getValues() :instrumentTuning.getTuning().getValues();
		List<TGString> strings = new ArrayList<TGString>();
		for(int i = 0; i < stringValues.length; i ++) {
			strings.add(TGSongManager.newString(this.songManager.getFactory(),(i + 1), stringValues[i]));
		}
		this.newTrack.setStrings(strings);
		this.tuningText.setEnabled(!this.isPercussion.isSelected());
		this.tuningButton.setEnabled(!this.isPercussion.isSelected());
		this.updateTuningText();

		this.keySignatureSelect.setEnabled(!this.isPercussion.isSelected());
	}

	private void updateTuningText() {
		if( this.dialog != null && !this.dialog.isDisposed() ) {
			this.tuningText.setText(this.songManager.getTrackManager().getStringsNames(this.newTrack));
		}
	}

	private void createSyncProcesses() {
		this.updateTuningProcess = new TGSyncProcessLocked(this.context.getContext(), new Runnable() {
			public void run() {
				TGNewTrackDialog.this.updateTuningText();
			}
		});

	}

	@Override
	public void processEvent(TGEvent event) throws TGEventException {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
			if( type == TGUpdateEvent.SELECTION ){
				this.updateTuningProcess.process();
			}
		}
	}

	public TGActionProcessorListener createOpenViewAction(String actionId) {
		TGActionProcessorListener tgActionProcessor = new TGActionProcessorListener(this.context.getContext(), actionId);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, this.newTrack);
		tgActionProcessor.setAttribute(TGViewContext.ATTRIBUTE_PARENT, this.dialog);
		return tgActionProcessor;
	}

	private void createNewTrack() {
		this.newTrack.setName(this.trackName.getText());
		
		// create channel if needed
		TGChannel selectedChannel = this.channelSelect.getSelectedValue();
		if (selectedChannel == null) {
			selectedChannel = this.songManager.addChannel(song);
			if (this.isPercussion.isSelected()) {
				selectedChannel.setBank(TGChannel.DEFAULT_PERCUSSION_BANK);
				selectedChannel.setProgram(TGChannel.DEFAULT_PERCUSSION_PROGRAM);
			} else {
				int selectedProgram = this.midiInstrumentSelect.getSelectedValue();
				if ((selectedProgram <= 0) || (selectedProgram > 128)) {
					selectedProgram = TGChannel.DEFAULT_PROGRAM;
				}
				selectedChannel.setProgram((short)selectedProgram);
			}
		}
		selectedChannel.setName(this.instrumentName.getText());
		// associate channel to track
		this.newTrack.setChannelId(selectedChannel.getChannelId());
		
		TGMeasure firstMeasure = this.newTrack.getMeasure(0);
		firstMeasure.setClef(this.clefSelect.getSelectedValue());
		firstMeasure.setKeySignature(this.keySignatureSelect.getSelectedValue());

		TGActionProcessor actionProcessor = new TGActionProcessor(this.context.getContext(), TGAddTrackAction.NAME);
		actionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, this.newTrack);
		actionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.song);
		actionProcessor.process();
	}

}