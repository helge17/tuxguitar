package org.herac.tuxguitar.gui.mixer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.undo.undoables.track.UndoableTrackChannel;
import org.herac.tuxguitar.song.models.TGTrack;

public class TGMixerTrack {
	protected TGTrack track;
	protected TGMixer mixer;
	protected TGMixerTrackChannel mixerChannel;
	protected Button soloCheckBox;
	protected Button muteCheckBox;
	protected Scale balanceScale;
	protected Scale volumeScale;
	private Label volumeValueLabel;
	private Label volumeValueTitleLabel;
	
	protected String tipVolume;
	protected String tipBalance;
	
	protected UndoableTrackChannel undoableVolume;
	protected UndoableTrackChannel undoableBalance;
	
	public TGMixerTrack(TGMixer mixer,TGTrack track){
		this.mixer = mixer;
		this.track = track;
	}
	
	public void init(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(SWT.CENTER,SWT.FILL,true,true));
		
		this.mixerChannel = new TGMixerTrackChannel(this);
		this.mixerChannel.init(composite);
		
		this.soloCheckBox = new Button(composite,SWT.CHECK);
		this.soloCheckBox.setSelection(TGMixerTrack.this.track.getChannel().isSolo());
		this.soloCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				UndoableTrackChannel undoable = UndoableTrackChannel.startUndo();
				
				TGMixerTrack.this.track.getChannel().setSolo(TGMixerTrack.this.soloCheckBox.getSelection());
				if(TGMixerTrack.this.track.getChannel().isSolo()){
					TGMixerTrack.this.track.getChannel().setMute(false);
				}
				TGMixerTrack.this.mixer.fireChanges(TGMixerTrack.this.track.getChannel(),TGMixer.SOLO);
				
				TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo());
				TuxGuitar.instance().updateCache(true);
			}
		});
		this.muteCheckBox = new Button(composite,SWT.CHECK);
		this.muteCheckBox.setSelection(TGMixerTrack.this.track.getChannel().isMute());
		this.muteCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				UndoableTrackChannel undoable = UndoableTrackChannel.startUndo();
				
				TGMixerTrack.this.track.getChannel().setMute(TGMixerTrack.this.muteCheckBox.getSelection());
				if(TGMixerTrack.this.track.getChannel().isMute()){
					TGMixerTrack.this.track.getChannel().setSolo(false);
				}
				TGMixerTrack.this.mixer.fireChanges(TGMixerTrack.this.track.getChannel(),TGMixer.MUTE);
				
				TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo());
				TuxGuitar.instance().updateCache(true);
			}
		});
		
		this.balanceScale = new Scale(composite, SWT.HORIZONTAL);
		this.balanceScale.setMaximum(127);
		this.balanceScale.setMinimum(0);
		this.balanceScale.setIncrement(1);
		this.balanceScale.setPageIncrement(64);
		this.balanceScale.setLayoutData(getBalanceScaleData());
		
		this.volumeScale = new Scale(composite, SWT.VERTICAL);
		this.volumeScale.setMaximum(127);
		this.volumeScale.setMinimum(0);
		this.volumeScale.setIncrement(1);
		this.volumeScale.setPageIncrement(16);
		this.volumeScale.setLayoutData(new GridData(SWT.CENTER,SWT.FILL,true,true));
		
		Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		
		Composite volumeValueComposite = new Composite(composite, SWT.NONE);
		volumeValueComposite.setLayout(new GridLayout(2,false));
		
		this.volumeValueTitleLabel = new Label(volumeValueComposite, SWT.LEFT);
		
		this.volumeValueLabel = new Label(volumeValueComposite, SWT.CENTER);
		this.volumeValueLabel.setLayoutData(getVolumeValueLabelData());
		
		this.balanceScale.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TGMixerTrack.this.track.getChannel().setBalance((short)TGMixerTrack.this.balanceScale.getSelection());
				TGMixerTrack.this.mixer.fireChanges(TGMixerTrack.this.track.getChannel(),TGMixer.BALANCE);
			}
		});
		this.balanceScale.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent arg0) {
				TGMixerTrack.this.undoableBalance = UndoableTrackChannel.startUndo();
			}
			public void mouseUp(MouseEvent arg0) {
				if(TGMixerTrack.this.undoableBalance != null){
					TuxGuitar.instance().getUndoableManager().addEdit(TGMixerTrack.this.undoableBalance.endUndo());
					TuxGuitar.instance().getFileHistory().setUnsavedFile();
					TuxGuitar.instance().updateCache(true);
					TGMixerTrack.this.undoableBalance = null;
				}
			}
		});
		
		this.volumeScale.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TGMixerTrack.this.track.getChannel().setVolume((short)(TGMixerTrack.this.volumeScale.getMaximum() - TGMixerTrack.this.volumeScale.getSelection()));
				TGMixerTrack.this.mixer.fireChanges(TGMixerTrack.this.track.getChannel(),TGMixer.VOLUME);
			}
		});
		this.volumeScale.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent arg0) {
				TGMixerTrack.this.undoableVolume = UndoableTrackChannel.startUndo();
			}
			public void mouseUp(MouseEvent arg0) {
				if(TGMixerTrack.this.undoableVolume != null){
					TuxGuitar.instance().getUndoableManager().addEdit(TGMixerTrack.this.undoableVolume.endUndo());
					TuxGuitar.instance().getFileHistory().setUnsavedFile();
					TuxGuitar.instance().updateCache(true);
					TGMixerTrack.this.undoableVolume = null;
				}
			}
		});
		
		this.balanceScale.setSelection(this.track.getChannel().getBalance());
		this.volumeScale.setSelection(this.volumeScale.getMaximum() - this.track.getChannel().getVolume());
		this.volumeValueLabel.setText(Integer.toString(this.volumeScale.getMaximum() - this.volumeScale.getSelection()));
	}
	
	private GridData getBalanceScaleData(){
		GridData data = new GridData(SWT.CENTER,SWT.NONE,false,true);
		data.widthHint = 65;
		return data;
	}
	
	private GridData getVolumeValueLabelData(){
		GridData data = new GridData(SWT.CENTER,SWT.NONE,true,false);
		data.minimumWidth = 40;
		return data;
	}
	
	public void fireChanges(int type){
		if((type & TGMixer.SOLO) != 0 || (type & TGMixer.MUTE) != 0){
			this.soloCheckBox.setSelection(this.track.getChannel().isSolo());
			this.muteCheckBox.setSelection(this.track.getChannel().isMute());
		}
		if((type & TGMixer.CHANNEL) != 0 || (type & TGMixer.VOLUME) != 0){
			int value = this.track.getChannel().getVolume();
			int maximum = this.volumeScale.getMaximum();
			if( this.volumeScale.getSelection() != (maximum - value) ){
				this.volumeScale.setSelection( (maximum - value) );
			}
			this.volumeScale.setToolTipText(this.tipVolume + ": " + value);
			this.volumeValueLabel.setText(Integer.toString( value ));
		}
		if((type & TGMixer.CHANNEL) != 0 || (type & TGMixer.BALANCE) != 0){
			int value = this.track.getChannel().getBalance();
			if( this.balanceScale.getSelection() != value){
				this.balanceScale.setSelection(value);
			}
			this.balanceScale.setToolTipText(this.tipBalance + ": " + value);
		}
		if((type & TGMixer.CHANNEL) != 0){
			this.mixerChannel.updateItems(true);
		}
	}
	
	public void loadProperties(){
		this.soloCheckBox.setText(TuxGuitar.getProperty("mixer.track.solo"));
		this.muteCheckBox.setText(TuxGuitar.getProperty("mixer.track.mute"));
		this.volumeValueTitleLabel.setText(TuxGuitar.getProperty("mixer.channel.volume") + ":");
		this.tipVolume = TuxGuitar.getProperty("mixer.channel.volume");
		this.tipBalance = TuxGuitar.getProperty("mixer.channel.balance");
		
		this.volumeScale.setToolTipText(this.tipVolume + ": " + this.track.getChannel().getVolume());
		this.balanceScale.setToolTipText(this.tipBalance + ": " + this.track.getChannel().getBalance());
		
		this.mixerChannel.updateItems(true);
	}
	
	public void updateItems(){
		this.mixerChannel.updateItems(false);
	}
	
	public TGTrack getTrack(){
		return this.track;
	}
	
	public TGMixer getMixer(){
		return this.mixer;
	}
}
