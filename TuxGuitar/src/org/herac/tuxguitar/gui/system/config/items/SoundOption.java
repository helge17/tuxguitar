package org.herac.tuxguitar.gui.system.config.items;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.system.config.TGConfigEditor;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.player.base.MidiPort;
import org.herac.tuxguitar.player.base.MidiSequencer;

public class SoundOption extends Option{
	protected boolean initialized;
	
	//**MidiSequencer module**// 
	protected String msCurrentKey;
	protected List msList;
	protected Combo msCombo;	
	
	//**MidiPort module**// 
	protected String mpCurrentKey;
	protected List mpList;
	protected Combo mpCombo;
	
    public SoundOption(TGConfigEditor configEditor,ToolBar toolBar,final Composite parent){
        super(configEditor,toolBar,parent,TuxGuitar.getProperty("settings.config.sound"));
        this.initialized = false;
    }
    
    public void createOption(){		    	    	
    	getToolItem().setText(TuxGuitar.getProperty("settings.config.sound"));	
		getToolItem().setImage(TuxGuitar.instance().getIconManager().getOptionSound());
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
				SoundOption.this.msList = TuxGuitar.instance().getPlayer().listSequencers();
				SoundOption.this.msCurrentKey = getConfig().getStringConfigValue(TGConfigKeys.MIDI_SEQUENCER);
				SoundOption.this.mpList = TuxGuitar.instance().getPlayer().listPorts();
				SoundOption.this.mpCurrentKey = getConfig().getStringConfigValue(TGConfigKeys.MIDI_PORT);
		    	new SyncThread(new Runnable() {		
					public void run() {
						if(!isDisposed()){
							//---Midi Sequencer---//
							for (int i = 0; i < SoundOption.this.msList.size(); i++) {        	
								MidiSequencer sequencer = (MidiSequencer)SoundOption.this.msList.get(i);
								SoundOption.this.msCombo.add(sequencer.getName());
								if(sequencer.getKey().equals(SoundOption.this.msCurrentKey)){
									SoundOption.this.msCombo.select(i);	
								}
							}    
							if(SoundOption.this.msCombo.getSelectionIndex() < 0 && SoundOption.this.msCombo.getItemCount() > 0){
								SoundOption.this.msCombo.select(0);
							}		    
					    
							//---Midi Port---//
							for (int i = 0; i < SoundOption.this.mpList.size(); i++) {        	
								MidiPort port = (MidiPort)SoundOption.this.mpList.get(i);
								SoundOption.this.mpCombo.add(port.getName());
								if(port.getKey().equals(SoundOption.this.mpCurrentKey)){
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
				}).start();								
			}		
		}).start();
    }
    
    public void updateConfig(){ 		    	
    	if(this.initialized){
    		int msIndex = this.msCombo.getSelectionIndex();
    		if(msIndex >= 0 && msIndex < this.msList.size()){    			
    			getConfig().setProperty(TGConfigKeys.MIDI_SEQUENCER, ((MidiSequencer)this.msList.get(msIndex)).getKey());
    		}    		
    		int mpIndex = this.mpCombo.getSelectionIndex();
    		if(mpIndex >= 0 && mpIndex < this.mpList.size()){    			
    			MidiPort midiPort = (MidiPort)this.mpList.get(mpIndex);
    			getConfig().setProperty(TGConfigKeys.MIDI_PORT, midiPort.getKey());
    		}
    	}
    }
   
    public void updateDefaults(){
    	if(this.initialized){
    		getConfig().setProperty(TGConfigKeys.MIDI_PORT,getDefaults().getProperty(TGConfigKeys.MIDI_PORT));
    		getConfig().setProperty(TGConfigKeys.MIDI_SEQUENCER,getDefaults().getProperty(TGConfigKeys.MIDI_SEQUENCER));
    	}
    }
    
    public void applyConfig(final boolean force){    		
    	if(force || this.initialized){
    		String midiSequencer = getConfig().getStringConfigValue(TGConfigKeys.MIDI_SEQUENCER);    		
			if(force || !TuxGuitar.instance().getPlayer().isSequencerOpen(midiSequencer)){
				TuxGuitar.instance().getPlayer().openSequencer(midiSequencer);
			}
			String midiPort = getConfig().getStringConfigValue(TGConfigKeys.MIDI_PORT);    		
			if(force || !TuxGuitar.instance().getPlayer().isMidiPortOpen(midiPort)){
				TuxGuitar.instance().getPlayer().openPort(midiPort);
			}
    	}
    }
}