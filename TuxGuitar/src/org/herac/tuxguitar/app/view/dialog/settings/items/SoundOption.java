package org.herac.tuxguitar.app.view.dialog.settings.items;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.view.dialog.settings.TGSettingsEditor;
import org.herac.tuxguitar.player.base.MidiOutputPort;
import org.herac.tuxguitar.player.base.MidiSequencer;
import org.herac.tuxguitar.util.TGSynchronizer;

public class SoundOption extends Option{
	protected boolean initialized;
	
	//**MidiSequencer module**//
	protected String msCurrentKey;
	protected List<MidiSequencer> msList;
	protected Combo msCombo;
	
	//**MidiPort module**//
	protected String mpCurrentKey;
	protected List<MidiOutputPort> mpList;
	protected Combo mpCombo;
	
	public SoundOption(TGSettingsEditor configEditor,ToolBar toolBar,final Composite parent){
		super(configEditor,toolBar,parent,TuxGuitar.getProperty("settings.config.sound"));
		this.initialized = false;
	}
	
	public void createOption(){
		getToolItem().setText(TuxGuitar.getProperty("settings.config.sound"));
		getToolItem().setImage(TuxGuitar.getInstance().getIconManager().getOptionSound());
		getToolItem().addSelectionListener(this);
		
		//---Midi Sequencer---//
		showLabel(getComposite(),SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.BOLD,0,TuxGuitar.getProperty("midi.sequencer"));
		
		Composite msComposite = new Composite(getComposite(),SWT.NONE);
		msComposite.setLayout(new GridLayout());
		msComposite.setLayoutData(getTabbedData());
		
		this.msCombo = new Combo(msComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.msCombo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		//---Midi Port---//
		showLabel(getComposite(),SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.BOLD,0,TuxGuitar.getProperty("midi.port"));
		
		Composite mpComposite = new Composite(getComposite(),SWT.NONE);
		mpComposite.setLayout(new GridLayout());
		mpComposite.setLayoutData(getTabbedData());
		
		this.mpCombo = new Combo(mpComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.mpCombo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.loadConfig();
	}
	
	protected void loadConfig(){
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
							for (int i = 0; i < SoundOption.this.msList.size(); i++) {
								MidiSequencer sequencer = (MidiSequencer)SoundOption.this.msList.get(i);
								SoundOption.this.msCombo.add(sequencer.getName());
								if(SoundOption.this.msCurrentKey != null && SoundOption.this.msCurrentKey.equals(sequencer.getKey())){
									SoundOption.this.msCombo.select(i);
									loadedSequencer = null;
								}else if(loadedSequencer != null && loadedSequencer.equals(sequencer.getKey())){
									SoundOption.this.msCombo.select(i);
								}
							}
							if(SoundOption.this.msCombo.getSelectionIndex() < 0 && SoundOption.this.msCombo.getItemCount() > 0){
								SoundOption.this.msCombo.select(0);
							}
							
							//---Midi Port---//
							String loadedPort = mpLoaded;
							for (int i = 0; i < SoundOption.this.mpList.size(); i++) {
								MidiOutputPort port = (MidiOutputPort)SoundOption.this.mpList.get(i);
								SoundOption.this.mpCombo.add(port.getName());
								if(SoundOption.this.mpCurrentKey != null && SoundOption.this.mpCurrentKey.equals(port.getKey())){
									SoundOption.this.mpCombo.select(i);
									loadedPort = null;
								}else if(loadedPort != null && loadedPort.equals(port.getKey())){
									SoundOption.this.mpCombo.select(i);
								}
							}
							if(SoundOption.this.mpCombo.getSelectionIndex() < 0 && SoundOption.this.mpCombo.getItemCount() > 0){
								SoundOption.this.mpCombo.select(0);
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
			int msIndex = this.msCombo.getSelectionIndex();
			if(msIndex >= 0 && msIndex < this.msList.size()){
				getConfig().setValue(TGConfigKeys.MIDI_SEQUENCER, ((MidiSequencer)this.msList.get(msIndex)).getKey());
			}
			int mpIndex = this.mpCombo.getSelectionIndex();
			if(mpIndex >= 0 && mpIndex < this.mpList.size()){
				MidiOutputPort midiPort = (MidiOutputPort)this.mpList.get(mpIndex);
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