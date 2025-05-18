package app.tuxguitar.app.view.dialog.channel;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.util.TGContinuousControl;
import app.tuxguitar.app.util.TGContinousControlSelectionListener;
import app.tuxguitar.player.base.MidiInstrument;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIFocusEvent;
import app.tuxguitar.ui.event.UIFocusLostListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICheckBox;
import app.tuxguitar.ui.widget.UIContainer;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UIKnob;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UITextField;
import app.tuxguitar.util.TGContext;

public class TGChannelItem implements TGContinuousControl{

	private static final int MINIMUM_KNOB_VALUE = 0;
	private static final int MAXIMUM_KNOB_VALUE = 127;
	private static final int MINIMUM_KNOB_INCREMENT = 4;

	private TGChannel channel;
	private TGChannelManagerDialog dialog;
	private TGChannelSettingsDialog channelUI;

	private UIPanel composite;

	private UITextField nameText;
	private UIDropDownSelect<Short> programCombo;
	private UIDropDownSelect<Short> bankCombo;

	private UIButton setupChannelButton;
	private UIButton removeChannelButton;
	private UICheckBox percussionButton;

	private UIKnob volumeScale;
	private UIKnob balanceScale;
	private UIKnob reverbScale;
	private UIKnob chorusScale;
	private UIKnob tremoloScale;
	private UIKnob phaserScale;

	public TGChannelItem(TGChannelManagerDialog dialog){
		this.dialog = dialog;
	}

	public void show(final UIContainer parent){
		UIFactory uiFactory = this.dialog.getUIFactory();
		UITableLayout uiLayout = new UITableLayout(0f);

		this.composite = uiFactory.createPanel(parent, true);
		this.composite.setLayout(uiLayout);
		this.composite.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				disposeChannelUI();
			}
		});

		// Column 1
		UITableLayout col1Layout = new UITableLayout();
		UIPanel col1Panel = uiFactory.createPanel(this.composite, false);
		col1Panel.setLayout(col1Layout);
		uiLayout.set(col1Panel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);

		this.nameText = uiFactory.createTextField(col1Panel);
		this.nameText.addFocusLostListener(new UIFocusLostListener() {
			public void onFocusLost(UIFocusEvent event) {
				checkForNameModified();
			}
		});
		col1Layout.set(this.nameText, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 250f, null, null);

		this.programCombo = uiFactory.createDropDownSelect(col1Panel);
		this.programCombo.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateChannel(false);
			}
		});
		col1Layout.set(this.programCombo, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		this.bankCombo = uiFactory.createDropDownSelect(col1Panel);
		this.bankCombo.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateChannel(false);
			}
		});
		col1Layout.set(this.bankCombo, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

		// Column 2
		UITableLayout col2Layout = new UITableLayout();
		UIPanel col2Panel = uiFactory.createPanel(this.composite, false);
		col2Panel.setLayout(col2Layout);
		uiLayout.set(col2Panel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);

		this.percussionButton = uiFactory.createCheckBox(col2Panel);
		this.percussionButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateChannel(true);
			}
		});
		col2Layout.set(this.percussionButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, true);

		// Column 3
		UITableLayout col3Layout = new UITableLayout();
		UIPanel col3Panel = uiFactory.createPanel(this.composite, false);
		col3Panel.setLayout(col3Layout);
		uiLayout.set(col3Panel, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);

		UITableLayout actionButtonsLayout = new UITableLayout(0f);
		UIPanel actionButtonsComposite = uiFactory.createPanel(col3Panel, false);
		actionButtonsComposite.setLayout(actionButtonsLayout);
		col3Layout.set(actionButtonsComposite, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_TOP, true, true, 1, 1, null, null, 0f);

		this.setupChannelButton = uiFactory.createButton(actionButtonsComposite);
		this.setupChannelButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				setupChannel();
			}
		});
		actionButtonsLayout.set(this.setupChannelButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);

		this.removeChannelButton = uiFactory.createButton(actionButtonsComposite);
		this.removeChannelButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				removeChannel();
			}
		});
		actionButtonsLayout.set(this.removeChannelButton, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);

		UITableLayout controllerScalesLayout = new UITableLayout(0f);
		UIPanel controllerScalesComposite = uiFactory.createPanel(col3Panel, false);
		controllerScalesComposite.setLayout(controllerScalesLayout);
		col3Layout.set(controllerScalesComposite, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_BOTTOM, false, true, 1, 1, null, null, 0f);

		TGContinousControlSelectionListener scaleSelectionListener = new TGContinousControlSelectionListener(this);

		this.volumeScale = uiFactory.createKnob(controllerScalesComposite);
		this.volumeScale.setMinimum(TGChannel.MIN_VOLUME);
		this.volumeScale.setMaximum(TGChannel.MAX_VOLUME);
		this.volumeScale.setIncrement(MINIMUM_KNOB_INCREMENT);
		this.volumeScale.addSelectionListener(scaleSelectionListener);
		controllerScalesLayout.set(this.volumeScale, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);

		this.balanceScale = uiFactory.createKnob(controllerScalesComposite);
		this.balanceScale.setMinimum(MINIMUM_KNOB_VALUE);
		this.balanceScale.setMaximum(MAXIMUM_KNOB_VALUE);
		this.balanceScale.setIncrement(MINIMUM_KNOB_INCREMENT);
		this.balanceScale.addSelectionListener(scaleSelectionListener);
		controllerScalesLayout.set(this.balanceScale, 1, 2, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);

		this.reverbScale = uiFactory.createKnob(controllerScalesComposite);
		this.reverbScale.setMinimum(MINIMUM_KNOB_VALUE);
		this.reverbScale.setMaximum(MAXIMUM_KNOB_VALUE);
		this.reverbScale.setIncrement(MINIMUM_KNOB_INCREMENT);
		this.reverbScale.addSelectionListener(scaleSelectionListener);
		controllerScalesLayout.set(this.reverbScale, 1, 3, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);

		this.chorusScale = uiFactory.createKnob(controllerScalesComposite);
		this.chorusScale.setMinimum(MINIMUM_KNOB_VALUE);
		this.chorusScale.setMaximum(MAXIMUM_KNOB_VALUE);
		this.chorusScale.setIncrement(MINIMUM_KNOB_INCREMENT);
		this.chorusScale.addSelectionListener(scaleSelectionListener);
		controllerScalesLayout.set(this.chorusScale, 1, 4, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);

		this.tremoloScale = uiFactory.createKnob(controllerScalesComposite);
		this.tremoloScale.setMinimum(MINIMUM_KNOB_VALUE);
		this.tremoloScale.setMaximum(MAXIMUM_KNOB_VALUE);
		this.tremoloScale.setIncrement(MINIMUM_KNOB_INCREMENT);
		this.tremoloScale.addSelectionListener(scaleSelectionListener);
		controllerScalesLayout.set(this.tremoloScale, 1, 5, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);

		this.phaserScale = uiFactory.createKnob(controllerScalesComposite);
		this.phaserScale.setMinimum(MINIMUM_KNOB_VALUE);
		this.phaserScale.setMaximum(MAXIMUM_KNOB_VALUE);
		this.phaserScale.setIncrement(MINIMUM_KNOB_INCREMENT);
		this.phaserScale.addSelectionListener(scaleSelectionListener);
		controllerScalesLayout.set(this.phaserScale, 1, 6, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);

		//--------------------------------------------------------------//

		this.loadIcons();
		this.loadProperties();
		this.updateItems();
	}

	public void loadChannel(TGChannel channel) {
		boolean updated = (this.channel == null || !this.channel.equals(channel));
		if( updated ) {
			this.disposeChannelUI();
		}
		this.channel = channel;
		this.updateItems();
	}

	public void loadProperties(){
		if(!isDisposed()){
			this.percussionButton.setText(TuxGuitar.getProperty("instrument.percussion-channel"));
			this.removeChannelButton.setText(TuxGuitar.getProperty("remove"));
			this.setupChannelButton.setToolTipText(TuxGuitar.getProperty("settings"));

			this.volumeScale.setToolTipText(TuxGuitar.getProperty("instrument.volume"));
			this.balanceScale.setToolTipText(TuxGuitar.getProperty("instrument.balance"));
			this.reverbScale.setToolTipText(TuxGuitar.getProperty("instrument.reverb"));
			this.chorusScale.setToolTipText(TuxGuitar.getProperty("instrument.chorus"));
			this.tremoloScale.setToolTipText(TuxGuitar.getProperty("instrument.tremolo"));
			this.phaserScale.setToolTipText(TuxGuitar.getProperty("instrument.phaser"));
		}
	}

	public void loadIcons(){
		if(!isDisposed()){
			this.setupChannelButton.setImage(TGIconManager.getInstance(getContext()).getImageByName(TGIconManager.SETTINGS));
		}
	}

	public void updateItems(){
		if(!isDisposed() && getChannel() != null){
			this.updateIgnoreEvents(true);

			boolean playerRunning = this.getHandle().isPlayerRunning();
			boolean anyPercussionChannel = this.getHandle().isAnyPercussionChannel();
			boolean anyTrackConnectedToChannel = this.getHandle().isAnyTrackConnectedToChannel(getChannel());

			this.nameText.setText(getChannel().getName());
			this.percussionButton.setSelected(getChannel().isPercussionChannel());
			this.percussionButton.setEnabled(!anyTrackConnectedToChannel && (!anyPercussionChannel || getChannel().isPercussionChannel()));
			this.removeChannelButton.setEnabled(!anyTrackConnectedToChannel);
			this.setupChannelButton.setEnabled(this.dialog.getChannelSettingsHandlerManager().isChannelSettingsHandlerAvailable());

			this.volumeScale.setValue(getChannel().getVolume());
			this.balanceScale.setValue(getChannel().getBalance());
			this.reverbScale.setValue(getChannel().getReverb());
			this.chorusScale.setValue(getChannel().getChorus());
			this.tremoloScale.setValue(getChannel().getTremolo());
			this.phaserScale.setValue(getChannel().getPhaser());

			this.updateBankCombo(playerRunning);
			this.updateProgramCombo(playerRunning);

			this.updateIgnoreEvents(false);
		}
	}

	private void updateIgnoreEvents(boolean ignoreEvents) {
		this.nameText.setIgnoreEvents(ignoreEvents);
		this.percussionButton.setIgnoreEvents(ignoreEvents);
		this.removeChannelButton.setIgnoreEvents(ignoreEvents);
		this.setupChannelButton.setIgnoreEvents(ignoreEvents);
		this.volumeScale.setIgnoreEvents(ignoreEvents);
		this.balanceScale.setIgnoreEvents(ignoreEvents);
		this.reverbScale.setIgnoreEvents(ignoreEvents);
		this.chorusScale.setIgnoreEvents(ignoreEvents);
		this.tremoloScale.setIgnoreEvents(ignoreEvents);
		this.phaserScale.setIgnoreEvents(ignoreEvents);
		this.bankCombo.setIgnoreEvents(ignoreEvents);
		this.programCombo.setIgnoreEvents(ignoreEvents);
	}

	private void updateBankCombo(boolean playerRunning){
		if(!isDisposed() && getChannel() != null){
			if( this.bankCombo.getItemCount() == 0 ){
				String bankPrefix = TuxGuitar.getProperty("instrument.bank");
				for(short i = 0; i < 128; i++) {
					this.bankCombo.addItem(new UISelectItem<Short>((bankPrefix + " #" + i), i));
				}
			}
			if( getChannel().getBank() >= 0 && getChannel().getBank() < this.bankCombo.getItemCount() ){
				this.bankCombo.setSelectedValue(getChannel().getBank());
			}
			this.bankCombo.setEnabled(!getChannel().isPercussionChannel());
		}
	}

	private void updateProgramCombo(boolean playerRunning){
		if(!isDisposed() && getChannel() != null){
			String programNamesKey = ("programNames");
			List<String> programNames = getProgramNames();
			List<String> cachedProgramNames = this.programCombo.getData(programNamesKey);
			if( cachedProgramNames == null || isDifferentList(programNames, cachedProgramNames)){
				this.programCombo.removeItems();
				this.programCombo.setData(programNamesKey, programNames);
				for(Short i = 0 ; i < programNames.size(); i ++ ){
					this.programCombo.addItem(new UISelectItem<Short>(programNames.get(i), i));
				}
			}
			if( getChannel().getProgram() >= 0 && getChannel().getProgram() < this.programCombo.getItemCount() ){
				this.programCombo.setSelectedValue(getChannel().getProgram());
			}
		}
	}

	private List<String> getProgramNames(){
		List<String> programNames = new ArrayList<String>();
		if(!getChannel().isPercussionChannel() ){
			MidiInstrument[] instruments = MidiPlayer.getInstance(getContext()).getInstruments();
			if (instruments != null) {
				int count = instruments.length;
				if (count > 128) {
					count = 128;
				}
				for (int i = 0; i < count; i++) {
					programNames.add(instruments[i].getName());
				}
			}
		}
		if( programNames.isEmpty() ){
			String programPrefix = TuxGuitar.getProperty("instrument.program");
			for (int i = 0; i < 128; i++) {
				programNames.add((programPrefix + " #" + i));
			}
		}
		return programNames;
	}

	private boolean isDifferentList(List<? extends Object> list1, List<? extends Object> list2){
		if( list1.size() != list2.size() ){
			return true;
		}
		for( int i = 0 ; i < list1.size() ; i ++ ){
			if(!list1.get(i).equals(list2.get(i)) ){
				return true;
			}
		}

		return false;
	}

	public void checkForNameModified(){
		if( getChannel() != null && !isDisposed() && !this.nameText.getText().equals(getChannel().getName()) ){
			updateChannel(false);
		}
	}

	public TGContext getContext() {
		return this.dialog.getContext();
	}

	public TGChannelHandle getHandle() {
		return this.dialog.getHandle();
	}

	public TGChannel getChannel() {
		return this.channel;
	}

	public UIPanel getComposite(){
		return this.composite;
	}

	public boolean isDisposed() {
		return (this.getComposite() == null || this.getComposite().isDisposed());
	}

	public void dispose() {
		if(!this.isDisposed()) {
			this.getComposite().dispose();
		}
	}

	public void doActionWhenStable() {
		this.updateChannel(false);
	}

	public void updateChannel(boolean percussionChanged){
		if( getChannel() != null && !isDisposed() ){
			boolean percussionChannel = this.percussionButton.isSelected();

			short bank = getChannel().getBank();
			short program = getChannel().getProgram();
			if( percussionChanged ){
				bank = (percussionChannel ? TGChannel.DEFAULT_PERCUSSION_BANK : TGChannel.DEFAULT_BANK);
				program = (percussionChannel ? TGChannel.DEFAULT_PERCUSSION_PROGRAM : TGChannel.DEFAULT_PROGRAM);
			}else{
				if(!percussionChannel ){
					Short bankSelection = this.bankCombo.getSelectedValue();
					if( bankSelection != null ){
						bank = bankSelection;
					}
				}

				Short programSelection = this.programCombo.getSelectedValue();
				if( programSelection != null ){
					program = programSelection;
				}
			}

			getHandle().updateChannel(
				getChannel().getChannelId(),
				bank,
				program,
				(short)this.volumeScale.getValue(),
				(short)this.balanceScale.getValue(),
				(short)this.chorusScale.getValue(),
				(short)this.reverbScale.getValue(),
				(short)this.phaserScale.getValue(),
				(short)this.tremoloScale.getValue(),
				this.nameText.getText()
			);
		}
	}

	public void removeChannel(){
		if( getChannel() != null && !isDisposed() ){
			getHandle().removeChannel(getChannel());
		}
	}

	public void setupChannel() {
		if( getChannel() != null && !isDisposed() ) {
			if( this.channelUI == null ) {
				this.channelUI = this.dialog.getChannelSettingsHandlerManager().findChannelSettingsDialog(getChannel());
			}
			if( this.channelUI != null ) {
				if( this.channelUI.isOpen()) {
					this.channelUI.close();
				} else {
					this.channelUI.open(this.dialog.getWindow());
				}
			}
		}
	}

	public void disposeChannelUI() {
		if( this.channelUI != null && this.channelUI.isOpen()) {
			this.channelUI.close();
		}
		this.channelUI = null;
	}
}
