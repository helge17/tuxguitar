package org.herac.tuxguitar.app.view.dialog.settings.items;

import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.view.dialog.settings.TGSettingsEditor;
import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiSequencer;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.util.TGSynchronizer;

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
		
		this.loadConfig();
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
	
	public void updateConfig(){
		if(this.initialized){
			MidiSequencer midiSequencer = this.msCombo.getSelectedValue();
			if( midiSequencer != null ){
				getConfig().setValue(TGConfigKeys.MIDI_SEQUENCER, midiSequencer.getKey());
			}
			MidiOutputPort midiPort = this.mpCombo.getSelectedValue();
			if( midiPort != null ){
				getConfig().setValue(TGConfigKeys.MIDI_PORT, midiPort.getKey());
			}
		}
	}
	
	public void updateDefaults(){
		if(this.initialized){
			getConfig().setValue(TGConfigKeys.MIDI_PORT, getDefaults().getValue(TGConfigKeys.MIDI_PORT));
			getConfig().setValue(TGConfigKeys.MIDI_SEQUENCER, getDefaults().getValue(TGConfigKeys.MIDI_SEQUENCER));
		}
	}
}