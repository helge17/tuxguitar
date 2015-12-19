package org.herac.tuxguitar.app.view.dialog.channel;

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
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.util.TGContext;

public class TGChannelItem {
	
	private TGChannel channel;
	private TGChannelManagerDialog dialog;
	
	private Composite composite;
	
	private Text nameText;
	private Combo programCombo;
	private Combo bankCombo;
	
	private Button setupChannelButton;
	private Button removeChannelButton;
	private Button percussionButton;
	
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
		col2Composite.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,true));
		
		this.percussionButton = new Button(col2Composite, SWT.CHECK);
		this.percussionButton.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.percussionButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateChannel(true);
			}
		});
		
		// Column 3
		Composite col3Composite = new Composite(this.composite, SWT.NONE);
		col3Composite.setLayout(this.dialog.createGridLayout(1,false, true, false));
		col3Composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Composite actionButtonsComposite = new Composite(col3Composite, SWT.NONE);
		actionButtonsComposite.setLayout(this.dialog.createGridLayout(2, false, true, false));
		actionButtonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		this.setupChannelButton = new Button(actionButtonsComposite, SWT.PUSH);
		this.setupChannelButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
		this.setupChannelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setupChannel();
			}
		});
		
		this.removeChannelButton = new Button(actionButtonsComposite, SWT.PUSH);
		this.removeChannelButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
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
		
		this.loadIcons();
		this.loadProperties();
		this.updateItems();
	}
	
	public void loadProperties(){
		if(!isDisposed()){
			this.percussionButton.setText(TuxGuitar.getProperty("instrument.percussion-channel"));
			this.removeChannelButton.setText(TuxGuitar.getProperty("remove"));
			this.setupChannelButton.setToolTipText(TuxGuitar.getProperty("settings"));
			
			this.volumeScale.setText(TuxGuitar.getProperty("instrument.volume"));
			this.balanceScale.setText(TuxGuitar.getProperty("instrument.balance"));
			this.reverbScale.setText(TuxGuitar.getProperty("instrument.reverb"));
			this.chorusScale.setText(TuxGuitar.getProperty("instrument.chorus"));
			this.tremoloScale.setText(TuxGuitar.getProperty("instrument.tremolo"));
			this.phaserScale.setText(TuxGuitar.getProperty("instrument.phaser"));
		}
	}
	
	public void loadIcons(){
		if(!isDisposed()){
			this.setupChannelButton.setImage(TuxGuitar.getInstance().getIconManager().getSettings());
		}
	}
	
	public void resetItems(){
		if(!isDisposed() ){
			this.volumeScale.reset();
			this.balanceScale.reset();
			this.reverbScale.reset();
			this.chorusScale.reset();
			this.tremoloScale.reset();
			this.phaserScale.reset();
		}
	}
	
	public void updateItems(){
		if(!isDisposed() && getChannel() != null){
			boolean playerRunning = this.getHandle().isPlayerRunning();
			boolean anyPercussionChannel = this.getHandle().isAnyPercussionChannel();
			boolean anyTrackConnectedToChannel = this.getHandle().isAnyTrackConnectedToChannel(getChannel());
			
			this.nameText.setText(getChannel().getName());
			this.percussionButton.setSelection(getChannel().isPercussionChannel());
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
			this.bankCombo.setEnabled(!getChannel().isPercussionChannel());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateProgramCombo(boolean playerRunning){
		if(!isDisposed() && getChannel() != null){
			List<String> programNames = getProgramNames();
			if(!(this.programCombo.getData() instanceof List) || isDifferentList(programNames, (List<String>)this.programCombo.getData())){
				this.programCombo.removeAll();
				this.programCombo.setData(programNames);
				for( int i = 0 ; i < programNames.size() ; i ++ ){
					this.programCombo.add((String)programNames.get(i));
				}
			}
			if( getChannel().getProgram() >= 0 && getChannel().getProgram() < this.programCombo.getItemCount() ){
				this.programCombo.select(getChannel().getProgram());
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
	
	public void checkForChannelChanged( TGChannel newChannel ){
		if( this.channel == null || (newChannel != null && !newChannel.equals(this.channel))){
			this.resetItems();
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

	public void setChannel(TGChannel channel) {
		this.checkForChannelChanged(channel);
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
				bank = (percussionChannel ? TGChannel.DEFAULT_PERCUSSION_BANK : TGChannel.DEFAULT_BANK);
				program = (percussionChannel ? TGChannel.DEFAULT_PERCUSSION_PROGRAM : TGChannel.DEFAULT_PROGRAM);
			}else{
				if(!percussionChannel ){
					int bankSelection = this.bankCombo.getSelectionIndex();
					if( bankSelection >= 0 ){
						bank = bankSelection;
					}
				}
				
				int programSelection = this.programCombo.getSelectionIndex();
				if( programSelection >= 0 ){
					program = programSelection;
				}
			}
			
			getHandle().updateChannel(
				getChannel().getChannelId(), 
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
	
	public void setupChannel(){
		if( getChannel() != null && !isDisposed() ){
			TGChannelSettingsDialog settingsDialog = this.dialog.getChannelSettingsHandlerManager().findChannelSettingsDialog(getChannel());
			if( settingsDialog != null ){
				settingsDialog.show(this.dialog.getShell());
			}
		}
	}
}
