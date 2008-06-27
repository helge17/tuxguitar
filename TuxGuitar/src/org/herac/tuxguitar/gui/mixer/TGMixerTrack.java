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
	
	protected Button checkSolo;
	protected Button checkMute;
	
	private TGMixerScale scaleVolume;
	private TGMixerScale scaleBalance;
	private TGMixerScale scaleChorus;
	private TGMixerScale scaleReverb;
	private TGMixerScale scalePhaser;
	private TGMixerScale scaleTremolo;
	
	private Label volumeValueLabel;
	private Label volumeValueTitleLabel;
	
	private String tipVolume;
	private String tipBalance;
	private String tipChorus;
	private String tipReverb;
	private String tipPhaser;
	private String tipTremolo;
	
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
		
		this.checkSolo = new Button(composite,SWT.CHECK);
		this.checkSolo.setSelection(TGMixerTrack.this.track.getChannel().isSolo());
		this.checkSolo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				UndoableTrackChannel undoable = UndoableTrackChannel.startUndo();
				
				TGMixerTrack.this.track.getChannel().setSolo(TGMixerTrack.this.checkSolo.getSelection());
				if(TGMixerTrack.this.track.getChannel().isSolo()){
					TGMixerTrack.this.track.getChannel().setMute(false);
				}
				TGMixerTrack.this.mixer.fireChanges(TGMixerTrack.this.track.getChannel(),TGMixer.SOLO);
				
				TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo());
				TuxGuitar.instance().updateCache(true);
			}
		});
		this.checkMute = new Button(composite,SWT.CHECK);
		this.checkMute.setSelection(TGMixerTrack.this.track.getChannel().isMute());
		this.checkMute.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				UndoableTrackChannel undoable = UndoableTrackChannel.startUndo();
				
				TGMixerTrack.this.track.getChannel().setMute(TGMixerTrack.this.checkMute.getSelection());
				if(TGMixerTrack.this.track.getChannel().isMute()){
					TGMixerTrack.this.track.getChannel().setSolo(false);
				}
				TGMixerTrack.this.mixer.fireChanges(TGMixerTrack.this.track.getChannel(),TGMixer.MUTE);
				
				TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo());
				TuxGuitar.instance().updateCache(true);
			}
		});
		
		new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR).setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		
		Composite compositeEffects = new Composite(composite, SWT.NONE);
		compositeEffects.setLayout(new GridLayout(4,false));
		
		this.scaleChorus = new TGMixerScale(compositeEffects, SWT.VERTICAL, 64, TGMixer.CHORUS, getVerticalScaleData());
		this.scaleChorus.setSelection(  this.track.getChannel().getChorus());
		
		this.scaleReverb = new TGMixerScale(compositeEffects, SWT.VERTICAL, 64, TGMixer.REVERB, getVerticalScaleData());
		this.scaleReverb.setSelection(  this.track.getChannel().getReverb());
		
		this.scalePhaser = new TGMixerScale(compositeEffects, SWT.VERTICAL, 64, TGMixer.PHASER, getVerticalScaleData());
		this.scalePhaser.setSelection(  this.track.getChannel().getPhaser());
		
		this.scaleTremolo = new TGMixerScale(compositeEffects, SWT.VERTICAL, 64, TGMixer.TREMOLO, getVerticalScaleData());
		this.scaleTremolo.setSelection(  this.track.getChannel().getTremolo());
		
		this.scaleBalance = new TGMixerScale(composite, SWT.HORIZONTAL, 64, TGMixer.BALANCE, getHorizontalScaleData());
		this.scaleBalance.setSelection(this.track.getChannel().getBalance());
		
		this.scaleVolume = new TGMixerScale(composite, SWT.VERTICAL, 16, TGMixer.VOLUME, new GridData(SWT.CENTER,SWT.FILL,true,true));
		this.scaleVolume.setSelection(  this.track.getChannel().getVolume());
		
		Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		
		Composite volumeValueComposite = new Composite(composite, SWT.NONE);
		volumeValueComposite.setLayout(new GridLayout(2,false));
		
		this.volumeValueTitleLabel = new Label(volumeValueComposite, SWT.LEFT);
		this.volumeValueLabel = new Label(volumeValueComposite, SWT.CENTER);
		this.volumeValueLabel.setLayoutData(getVolumeValueLabelData());
		this.volumeValueLabel.setText(Integer.toString(this.track.getChannel().getVolume()));
	}
	
	private GridData getHorizontalScaleData(){
		GridData data = new GridData(SWT.CENTER,SWT.NONE,false,true);
		data.widthHint = 80;
		return data;
	}
	
	private GridData getVerticalScaleData(){
		GridData data = new GridData(SWT.CENTER,SWT.FILL,false,true);
		data.heightHint = 65;
		return data;
	}
	
	private GridData getVolumeValueLabelData(){
		GridData data = new GridData(SWT.CENTER,SWT.NONE,true,false);
		data.minimumWidth = 40;
		return data;
	}
	
	public void fireChanges(int type){
		if((type & TGMixer.SOLO) != 0 || (type & TGMixer.MUTE) != 0){
			this.checkSolo.setSelection(this.track.getChannel().isSolo());
			this.checkMute.setSelection(this.track.getChannel().isMute());
		}
		if((type & TGMixer.CHANNEL) != 0 || (type & TGMixer.VOLUME) != 0){
			int value = this.track.getChannel().getVolume();
			this.scaleVolume.setSelection( ( value) );
			this.scaleVolume.setToolTipText(this.tipVolume + ": " + value);
			this.volumeValueLabel.setText(Integer.toString( value ));
		}
		if((type & TGMixer.CHANNEL) != 0 || (type & TGMixer.BALANCE) != 0){
			int value = this.track.getChannel().getBalance();
			this.scaleBalance.setSelection(value);
			this.scaleBalance.setToolTipText(this.tipBalance + ": " + value);
		}
		if((type & TGMixer.CHANNEL) != 0 || (type & TGMixer.CHORUS) != 0){
			int value = this.track.getChannel().getChorus();
			this.scaleChorus.setSelection( ( value) );
			this.scaleChorus.setToolTipText(this.tipChorus + ": " + value);
		}
		if((type & TGMixer.CHANNEL) != 0 || (type & TGMixer.REVERB) != 0){
			int value = this.track.getChannel().getReverb();
			this.scaleReverb.setSelection( ( value) );
			this.scaleReverb.setToolTipText(this.tipReverb + ": " + value);
		}
		if((type & TGMixer.CHANNEL) != 0 || (type & TGMixer.PHASER) != 0){
			int value = this.track.getChannel().getPhaser();
			this.scalePhaser.setSelection( ( value) );
			this.scalePhaser.setToolTipText(this.tipPhaser + ": " + value);
		}
		if((type & TGMixer.CHANNEL) != 0 || (type & TGMixer.TREMOLO) != 0){
			int value = this.track.getChannel().getTremolo();
			this.scaleTremolo.setSelection( ( value) );
			this.scaleTremolo.setToolTipText(this.tipTremolo + ": " + value);
		}
		if((type & TGMixer.CHANNEL) != 0){
			this.mixerChannel.updateItems(true);
		}
	}
	
	public void loadProperties(){
		this.tipVolume = TuxGuitar.getProperty("mixer.channel.volume");
		this.tipBalance = TuxGuitar.getProperty("mixer.channel.balance");
		this.tipChorus = TuxGuitar.getProperty("mixer.channel.chorus");
		this.tipReverb = TuxGuitar.getProperty("mixer.channel.reverb");
		this.tipPhaser = TuxGuitar.getProperty("mixer.channel.phaser");
		this.tipTremolo = TuxGuitar.getProperty("mixer.channel.tremolo");
		this.checkSolo.setText(TuxGuitar.getProperty("mixer.track.solo"));
		this.checkMute.setText(TuxGuitar.getProperty("mixer.track.mute"));
		this.volumeValueTitleLabel.setText(TuxGuitar.getProperty("mixer.channel.volume") + ":");
		
		this.scaleVolume.setToolTipText(this.tipVolume + ": " + this.track.getChannel().getVolume());
		this.scaleBalance.setToolTipText(this.tipBalance + ": " + this.track.getChannel().getBalance());
		this.scaleChorus.setToolTipText(this.tipChorus + ": " + this.track.getChannel().getChorus());
		this.scaleReverb.setToolTipText(this.tipReverb + ": " + this.track.getChannel().getReverb());
		this.scalePhaser.setToolTipText(this.tipPhaser + ": " + this.track.getChannel().getPhaser());
		this.scaleTremolo.setToolTipText(this.tipTremolo + ": " + this.track.getChannel().getTremolo());
		
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
	
	protected void updateChannelValue(int type, int value){
		if( (type & TGMixer.VOLUME) != 0 ){
			TGMixerTrack.this.track.getChannel().setVolume( (short)value );
		}
		else if( (type & TGMixer.BALANCE) != 0 ){
			TGMixerTrack.this.track.getChannel().setBalance( (short)value );
		}
		else if( (type & TGMixer.CHORUS) != 0 ){
			TGMixerTrack.this.track.getChannel().setChorus( (short)value );
		}
		else if( (type & TGMixer.REVERB) != 0 ){
			TGMixerTrack.this.track.getChannel().setReverb( (short)value );
		}
		else if( (type & TGMixer.PHASER) != 0 ){
			TGMixerTrack.this.track.getChannel().setPhaser( (short)value );
		}
		else if( (type & TGMixer.TREMOLO) != 0 ){
			TGMixerTrack.this.track.getChannel().setTremolo( (short)value );
		}
		this.mixer.fireChanges(TGMixerTrack.this.track.getChannel(), type);
	}
	
	private class TGMixerScale {
		
		protected int type;
		protected int value;
		protected boolean inverted;
		protected Scale scale;
		protected UndoableTrackChannel undoable;
		
		public TGMixerScale(Composite parent, int style, int pageIncrement, int type, Object layoutData){
			this.scale = new Scale(parent, style);
			this.scale.setMaximum(127);
			this.scale.setMinimum(0);
			this.scale.setIncrement(1);
			this.scale.setPageIncrement(pageIncrement);
			this.scale.setLayoutData(layoutData);
			this.type = type;
			this.value = -1;
			this.inverted =  ((style & SWT.VERTICAL) != 0 );
			this.addDefaultListeners();
		}
		
		public void addDefaultListeners(){
			this.scale.addListener(SWT.Selection, new Listener() {
				public synchronized void handleEvent(Event event) {
					TGMixerScale.this.updateValue();
					TGMixerTrack.this.updateChannelValue(TGMixerScale.this.type, getSelection());
				}
			});
			this.scale.addMouseListener(new MouseAdapter() {
				public void mouseDown(MouseEvent arg0) {
					TGMixerScale.this.undoable = UndoableTrackChannel.startUndo();
				}
				public void mouseUp(MouseEvent arg0) {
					if(TGMixerScale.this.undoable != null){
						TuxGuitar.instance().getUndoableManager().addEdit(TGMixerScale.this.undoable.endUndo());
						TuxGuitar.instance().getFileHistory().setUnsavedFile();
						TuxGuitar.instance().updateCache(true);
						TGMixerScale.this.undoable = null;
					}
				}
			});
		}
		
		public void updateValue(){
			this.setSelection( ( this.inverted ? 127 - this.scale.getSelection() : this.scale.getSelection() ) );
		}		
		
		public int getSelection(){
			if(this.value < 0){
				this.updateValue();
			}
			return this.value;
		}
		
		public void setSelection(int value){
			if(value != this.value){
				this.value = value;
				this.scale.setSelection( ( this.inverted ? 127 - this.value : this.value ) );
			}
		}
		
		public void setToolTipText(String text){
			this.scale.setToolTipText( text );
		}
	}
}
