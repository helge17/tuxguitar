package org.herac.tuxguitar.app.editors.channel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.player.base.MidiInstrument;
import org.herac.tuxguitar.song.models.TGChannel;

public class TGChannelItem {
	
	private TGChannel channel;
	private TGChannelManagerDialog dialog;
	
	private Composite composite;
	
	private Text nameText;
	private Combo programCombo;
	private Combo bankCombo;
	
	private Button removeChannelButton;
	private Button percussionButton;
	private Combo channel1Combo;
	private Combo channel2Combo;
	
	private TGScalePopup volumeScale;
	private TGScalePopup balanceScale;
	private TGScalePopup reverbScale;
	private TGScalePopup chorusScale;
	private TGScalePopup tremoloScale;
	private TGScalePopup phaserScale; 
	
	public TGChannelItem(TGChannelManagerDialog dialog){
		this.dialog = dialog;
	}
	
	public void show(final Composite parent, Object layoutData){
		this.composite = new Composite(parent, SWT.BORDER);
		this.composite.setLayout(this.dialog.createGridLayout(3, false, true, true));
		this.composite.setLayoutData(layoutData);
		
		// Column 1
		Composite col1Composite = new Composite(this.composite, SWT.NONE);
		col1Composite.setLayout(this.dialog.createGridLayout(1,false, true, false));
		col1Composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.nameText = new Text(col1Composite, SWT.BORDER | SWT.LEFT);
		this.nameText.setLayoutData(new GridData(150, SWT.DEFAULT));
		this.nameText.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				checkForNameModified();
			}
		});
		this.nameText.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				checkForNameModified();
			}
		});
		
		this.programCombo = new Combo(col1Composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.programCombo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		this.programCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateChannel(false);
			}
		});
		
		this.bankCombo = new Combo(col1Composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.bankCombo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		this.bankCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateChannel(false);
			}
		});
		
		// Column 2
		Composite col2Composite = new Composite(this.composite, SWT.NONE);
		col2Composite.setLayout(this.dialog.createGridLayout(1,false, true, false));
		col2Composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.percussionButton = new Button(col2Composite, SWT.CHECK);
		this.percussionButton.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.percussionButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateChannel(true);
			}
		});
		
		this.channel1Combo = new Combo(col2Composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.channel1Combo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		this.channel1Combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateChannel(false);
			}
		});
		
		this.channel2Combo = new Combo(col2Composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.channel2Combo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		this.channel2Combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateChannel(false);
			}
		});
		
		// Column 3
		Composite col3Composite = new Composite(this.composite, SWT.NONE);
		col3Composite.setLayout(this.dialog.createGridLayout(1,false, true, false));
		col3Composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.removeChannelButton = new Button(col3Composite, SWT.PUSH);
		this.removeChannelButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
		this.removeChannelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeChannel();
			}
		});
		
		Composite controllerScalesComposite = new Composite(col3Composite, SWT.NONE);
		controllerScalesComposite.setLayout(new RowLayout());
		controllerScalesComposite.setLayoutData(new GridData(SWT.RIGHT,SWT.BOTTOM,true,true));
		
		SelectionListener scaleSelectionListener = new TGScaleSelectionListener(this);
		
		this.volumeScale = new TGScalePopup(controllerScalesComposite);
		this.volumeScale.setSelectionListener(scaleSelectionListener);
		
		this.balanceScale = new TGScalePopup(controllerScalesComposite);
		this.balanceScale.setSelectionListener(scaleSelectionListener);
		
		this.reverbScale = new TGScalePopup(controllerScalesComposite);
		this.reverbScale.setSelectionListener(scaleSelectionListener);
		
		this.chorusScale = new TGScalePopup(controllerScalesComposite);
		this.chorusScale.setSelectionListener(scaleSelectionListener);
		
		this.tremoloScale = new TGScalePopup(controllerScalesComposite);
		this.tremoloScale.setSelectionListener(scaleSelectionListener);
		
		this.phaserScale = new TGScalePopup(controllerScalesComposite);
		this.phaserScale.setSelectionListener(scaleSelectionListener);
		
		//--------------------------------------------------------------//
		
		this.loadProperties();
		this.updateItems();
	}
	
	
	public void loadProperties(){
		if(!isDisposed()){
			this.percussionButton.setText(TuxGuitar.getProperty("instrument.percussion-channel"));
			this.removeChannelButton.setText(TuxGuitar.getProperty("remove"));
			
			this.volumeScale.setText(TuxGuitar.getProperty("instrument.volume"));
			this.balanceScale.setText(TuxGuitar.getProperty("instrument.balance"));
			this.reverbScale.setText(TuxGuitar.getProperty("instrument.reverb"));
			this.chorusScale.setText(TuxGuitar.getProperty("instrument.chorus"));
			this.tremoloScale.setText(TuxGuitar.getProperty("instrument.tremolo"));
			this.phaserScale.setText(TuxGuitar.getProperty("instrument.phaser"));
			
			this.updateChannelCombos( this.getHandle().isPlayerRunning() );
		}
	}
	
	public void updateItems(){
		if(!isDisposed() && getChannel() != null){
			boolean playerRunning = this.getHandle().isPlayerRunning();
			boolean anyTrackConnectedToChannel = this.getHandle().isAnyTrackConnectedToChannel(getChannel());
			
			this.nameText.setText(getChannel().getName());
			this.percussionButton.setSelection(getChannel().isPercussionChannel());
			this.percussionButton.setEnabled(!anyTrackConnectedToChannel);
			this.removeChannelButton.setEnabled(!anyTrackConnectedToChannel);
			
			this.volumeScale.setValue(getChannel().getVolume());
			this.balanceScale.setValue(getChannel().getBalance());
			this.reverbScale.setValue(getChannel().getReverb());
			this.chorusScale.setValue(getChannel().getChorus());
			this.tremoloScale.setValue(getChannel().getTremolo());
			this.phaserScale.setValue(getChannel().getPhaser());
			
			this.updateBankCombo(playerRunning);
			this.updateProgramCombo(playerRunning);
			this.updateChannelCombos(playerRunning);
		}
	}
	
	private void updateChannelCombos(boolean playerRunning){
		if(!isDisposed() && getChannel() != null){
			List channels = getHandle().getFreeChannels(getChannel());
			
			String channel1Prefix = TuxGuitar.getProperty("instrument.channel");
			String channel2Prefix = TuxGuitar.getProperty("instrument.effect-channel");
			
			this.reloadChannelCombo(this.channel1Combo, channels, getChannel().getChannel(), channel1Prefix);
			this.reloadChannelCombo(this.channel2Combo, channels, getChannel().getEffectChannel(), channel2Prefix);
			
			this.channel1Combo.setEnabled(!playerRunning && !getChannel().isPercussionChannel() && this.channel1Combo.getItemCount() > 0);
			this.channel2Combo.setEnabled(!playerRunning && !getChannel().isPercussionChannel() && this.channel2Combo.getItemCount() > 0);
		}
	}
	
	private void reloadChannelCombo(Combo combo, List channels, int selected, String prefix){
		combo.removeAll();
		combo.setData(channels);
		
		for( int i = 0 ; i < channels.size() ; i ++ ){
			Integer channel = (Integer)channels.get(i);
			
			combo.add(prefix + " #" + channel.toString() );
			
			if( selected == channel.intValue() ){
				combo.select( i );
			}
		}
	}
	
	private void updateBankCombo(boolean playerRunning){
		if(!isDisposed() && getChannel() != null){
			if( this.bankCombo.getItemCount() == 0 ){
				String bankPrefix = TuxGuitar.getProperty("instrument.bank");
				for (int i = 0; i < 128; i++) {
					this.bankCombo.add((bankPrefix + " #" + i));
				}
			}
			if( getChannel().getBank() >= 0 && getChannel().getBank() < this.bankCombo.getItemCount() ){
				this.bankCombo.select(getChannel().getBank());
			}
			this.bankCombo.setEnabled(!playerRunning && !getChannel().isPercussionChannel() && this.bankCombo.getItemCount() > 0);
		}
	}
	
	private void updateProgramCombo(boolean playerRunning){
		if(!isDisposed() && getChannel() != null){
			this.programCombo.removeAll();
			
			List programNames = getProgramNames();
			for( int i = 0 ; i < programNames.size() ; i ++ ){
				this.programCombo.add((String)programNames.get(i));
			}
			if( getChannel().getProgram() >= 0 && getChannel().getProgram() < this.programCombo.getItemCount() ){
				this.programCombo.select(getChannel().getProgram());
			}
			this.programCombo.setEnabled(!playerRunning && this.programCombo.getItemCount() > 0);
		}
	}
	
	private List getProgramNames(){
		List programNames = new ArrayList();
		if( getChannel().isPercussionChannel() ){
			String programPrefix = TuxGuitar.getProperty("instrument.program");
			for (int i = 0; i < 128; i++) {
				programNames.add((programPrefix + " #" + i));
			}
		}else{
			MidiInstrument[] instruments = TuxGuitar.instance().getPlayer().getInstruments();
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
		return programNames;
	}
	
	public void checkForNameModified(){
		if( getChannel() != null && !isDisposed() && !this.nameText.getText().equals(getChannel().getName()) ){
			updateChannel(false);
		}
	}
	
	public TGChannelHandle getHandle() {
		return this.dialog.getHandle();
	}
	
	public TGChannel getChannel() {
		return this.channel;
	}

	public void setChannel(TGChannel channel) {
		this.channel = channel;
	}
	
	public Composite getComposite(){
		return this.composite;
	}
	
	public boolean isDisposed() {
		return (this.getComposite() == null || this.getComposite().isDisposed());
	}
	
	public void dispose() {
		if(!isDisposed()){
			getComposite().dispose();
		}
	}
	
	public void updateChannel(boolean percussionChanged){
		if( getChannel() != null && !isDisposed() ){			
			boolean percussionChannel = this.percussionButton.getSelection();
			
			int bank = getChannel().getBank();
			int program = getChannel().getProgram();
			if( percussionChanged ){
				bank = (percussionChannel ? TGChannel.DEFAULT_BANK : TGChannel.DEFAULT_PERCUSSION_BANK);
				program = (percussionChannel ? TGChannel.DEFAULT_PROGRAM : TGChannel.DEFAULT_PERCUSSION_PROGRAM);
			}else{
				int bankSelection = this.bankCombo.getSelectionIndex();
				if( bankSelection >= 0 ){
					bank = bankSelection;
				}
				
				int programSelection = this.programCombo.getSelectionIndex();
				if( programSelection >= 0 ){
					program = programSelection;
				}
			}
			
			int channel1 = -1;
			int channel2 = -1;
			if( percussionChannel ){
				channel1 = TGChannel.DEFAULT_PERCUSSION_CHANNEL;
				channel2 = TGChannel.DEFAULT_PERCUSSION_CHANNEL;
			}else{
				int channel1Selection = this.channel1Combo.getSelectionIndex();
				Object channel1Data = this.channel1Combo.getData();
				if( channel1Selection >= 0 && channel1Data instanceof List && ((List)channel1Data).size() > channel1Selection ){
					channel1 = ((Integer)((List)channel1Data).get(channel1Selection)).intValue();
				}
				
				int channel2Selection = this.channel2Combo.getSelectionIndex();
				Object channel2Data = this.channel2Combo.getData();
				if( channel2Selection >= 0 && channel2Data instanceof List && ((List)channel2Data).size() > channel2Selection ){
					channel2 = ((Integer)((List)channel2Data).get(channel2Selection)).intValue();
				}
			}
			
			getHandle().updateChannel(
				getChannel().getChannelId(), 
				(short)channel1,
				(short)channel2,
				(short)bank,
				(short)program,
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
}
