package app.tuxguitar.app.view.dialog.settings.items;

import java.util.List;
import java.util.function.Function;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.view.dialog.settings.TGSettingsEditor;
import app.tuxguitar.player.base.MidiClick;
import app.tuxguitar.player.base.MidiOutputPort;
import app.tuxguitar.player.base.MidiSequencer;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILayoutContainer;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UISpinner;
import app.tuxguitar.util.TGSynchronizer;

public class SoundOption extends TGSettingsOption {

	private boolean initialized;

	//**MidiSequencer module**//
	private String msCurrentKey;
	private List<MidiSequencer> msList;
	private UIDropDownSelect<MidiSequencer> msCombo;

	//**MidiPort module**//
	private String mpCurrentKey;
	private List<MidiOutputPort> mpList;
	private UIDropDownSelect<MidiOutputPort> mpCombo;

	//**Count In / Metronome click**//
	private UISpinner clickNote;
	private UISpinner clickVelocity;

	public SoundOption(TGSettingsEditor configEditor, UIToolBar toolBar, UILayoutContainer parent){
		super(configEditor, toolBar, parent, TuxGuitar.getProperty("settings.config.sound"));
		this.initialized = false;
	}

	public void createOption() {
		UIFactory uiFactory = this.getUIFactory();

		getToolItem().setText(TuxGuitar.getProperty("settings.config.sound"));
		getToolItem().setImage(TuxGuitar.getInstance().getIconManager().getOptionSound());
		getToolItem().addSelectionListener(this);

		//---Midi Sequencer---//
		showLabel(getPanel(), TuxGuitar.getProperty("midi.sequencer"), true, 1, 1);

		UITableLayout msCompositeLayout = new UITableLayout();
		UIPanel msComposite = uiFactory.createPanel(getPanel(), false);
		msComposite.setLayout(msCompositeLayout);
		this.indent(msComposite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);

		this.msCombo = uiFactory.createDropDownSelect(msComposite);
		msCompositeLayout.set(this.msCombo, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		//---Midi Port---//
		showLabel(getPanel(), TuxGuitar.getProperty("midi.port"), true, 3, 1);

		UITableLayout mpCompositeLayout = new UITableLayout();
		UIPanel mpComposite = uiFactory.createPanel(getPanel(), false);
		mpComposite.setLayout(mpCompositeLayout);
		this.indent(mpComposite, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);

		this.mpCombo = uiFactory.createDropDownSelect(mpComposite);
		mpCompositeLayout.set(this.mpCombo, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		//---Count In / Metronome click---//
		showLabel(getPanel(), TuxGuitar.getProperty("midi.click"), true, 5, 1);

		UITableLayout clickCompositeLayout = new UITableLayout();
		UIPanel clickComposite = uiFactory.createPanel(getPanel(), false);
		clickComposite.setLayout(clickCompositeLayout);
		this.indent(clickComposite, 6, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);

		MidiClick click = MidiClick.read(getConfig());
		this.clickNote = this.createClickSpinner(clickComposite, 1, "midi.click.note", 0, click.getNote());
		this.clickVelocity = this.createClickSpinner(clickComposite, 3, "midi.click.velocity", 1, click.getVelocity());

		this.loadConfig();
	}

	// a label and a spinner from the minimum to 127 (a MIDI note or velocity), in the columns from the given one
	private UISpinner createClickSpinner(UIPanel parent, int column, String key, int minimum, int value) {
		UITableLayout layout = (UITableLayout) parent.getLayout();
		UILabel label = getUIFactory().createLabel(parent);
		label.setText(TuxGuitar.getProperty(key) + ":");
		layout.set(label, 1, column, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		UISpinner spinner = getUIFactory().createSpinner(parent);
		spinner.setMinimum(minimum);
		spinner.setMaximum(127);
		spinner.setValue(value);
		layout.set(spinner, 1, column + 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		return spinner;
	}

	public void loadConfig(){
		new Thread(new Runnable() {
			public void run() {
				SoundOption.this.mpList = TuxGuitar.getInstance().getPlayer().listOutputPorts();
				SoundOption.this.msList = TuxGuitar.getInstance().getPlayer().listSequencers();

				SoundOption.this.mpCurrentKey = getConfig().getStringValue(TGConfigKeys.MIDI_PORT);
				SoundOption.this.msCurrentKey = getConfig().getStringValue(TGConfigKeys.MIDI_SEQUENCER);

				MidiSequencer sequencer = TuxGuitar.getInstance().getPlayer().getSequencer();
				MidiOutputPort outputPort = TuxGuitar.getInstance().getPlayer().getOutputPort();

				final String msLoaded = (sequencer != null ? sequencer.getKey() : null ) ;
				final String mpLoaded = (outputPort != null ? outputPort.getKey() : null );

				TGSynchronizer.getInstance(getViewContext().getContext()).executeLater(new Runnable() {
					public void run() {
						if(!isDisposed()){
							//---Midi Sequencer---//
							String loadedSequencer = msLoaded;
							UISelectItem<MidiSequencer> selectedSequencerItem = null;
							for(MidiSequencer sequencer : SoundOption.this.msList) {
								UISelectItem<MidiSequencer> item = new UISelectItem<MidiSequencer>(sequencer.getName(), sequencer);

								SoundOption.this.msCombo.addItem(item);
								if( SoundOption.this.msCurrentKey != null && SoundOption.this.msCurrentKey.equals(sequencer.getKey())){
									loadedSequencer = null;

									selectedSequencerItem = item;
								} else if(loadedSequencer != null && loadedSequencer.equals(sequencer.getKey())){
									selectedSequencerItem = item;
								} else if(selectedSequencerItem == null) {
									selectedSequencerItem = item;
								}
							}
							if( selectedSequencerItem != null ){
								SoundOption.this.msCombo.setSelectedItem(selectedSequencerItem);
							}

							//---Midi Port---//
							String loadedPort = mpLoaded;
							UISelectItem<MidiOutputPort> selectedPortItem = null;
							for(MidiOutputPort port : SoundOption.this.mpList) {
								UISelectItem<MidiOutputPort> item = new UISelectItem<MidiOutputPort>(port.getName(), port);

								SoundOption.this.mpCombo.addItem(item);
								if( SoundOption.this.mpCurrentKey != null && SoundOption.this.mpCurrentKey.equals(port.getKey())){
									loadedPort = null;

									selectedPortItem = item;
								} else if(loadedPort != null && loadedPort.equals(port.getKey())) {
									selectedPortItem = item;
								} else if(selectedPortItem == null) {
									selectedPortItem = item;
								}
							}
							if( selectedPortItem != null ){
								SoundOption.this.mpCombo.setSelectedItem(selectedPortItem);
							}

							SoundOption.this.initialized = true;
							SoundOption.this.pack();
						}
					}
				});
			}
		}).start();
	}

	// the click is kept whether or not the devices were listed yet: its spinners are filled at once
	public void updateConfig(){
		if(this.initialized){
			this.updateKey(TGConfigKeys.MIDI_SEQUENCER, this.msCombo, MidiSequencer::getKey);
			this.updateKey(TGConfigKeys.MIDI_PORT, this.mpCombo, MidiOutputPort::getKey);
		}
		new MidiClick(this.clickNote.getValue(), this.clickVelocity.getValue()).write(getConfig());
	}

	// the key of what is selected, if anything is
	private <T> void updateKey(String configKey, UIDropDownSelect<T> combo, Function<T, String> keyOf){
		T selected = combo.getSelectedValue();
		if( selected != null ){
			getConfig().setValue(configKey, keyOf.apply(selected));
		}
	}

	public void updateDefaults(){
		if(this.initialized){
			getConfig().setValue(TGConfigKeys.MIDI_PORT, getDefaults().getValue(TGConfigKeys.MIDI_PORT));
			getConfig().setValue(TGConfigKeys.MIDI_SEQUENCER, getDefaults().getValue(TGConfigKeys.MIDI_SEQUENCER));
		}
		MidiClick.DEFAULT.write(getConfig());
	}
}