package org.herac.tuxguitar.gui.mixer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.undo.undoables.track.UndoableTrackChannel;
import org.herac.tuxguitar.gui.undo.undoables.track.UndoableTrackSoloMute;
import org.herac.tuxguitar.song.models.TGTrack;

public class TGMixerTrack {
	
	protected TGTrack track;
	protected TGMixer mixer;
	protected TGMixerTrackChannel mixerChannel;
	
	protected Button checkSolo;
	protected Button checkMute;
	
	private TGMixerScale scaleVolume;
	private TGMixerScale scaleBalance;
	private TGMixerScalePopup scaleChorus;
	private TGMixerScalePopup scaleReverb;
	private TGMixerScalePopup scalePhaser;
	private TGMixerScalePopup scaleTremolo;
	
	private Label volumeValueLabel;
	private Label volumeValueTitleLabel;
	
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
		this.checkSolo.setSelection(TGMixerTrack.this.track.isSolo());
		this.checkSolo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TGTrack track = TGMixerTrack.this.track;
				
				UndoableTrackSoloMute undoable = UndoableTrackSoloMute.startUndo(track);
				TuxGuitar.instance().getSongManager().getTrackManager().changeSolo(track,TGMixerTrack.this.checkSolo.getSelection());
				TGMixerTrack.this.mixer.fireChanges(track.getChannel(),TGMixer.SOLO);
				TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo(track));
				TuxGuitar.instance().updateCache(true);
			}
		});
		this.checkMute = new Button(composite,SWT.CHECK);
		this.checkMute.setSelection(TGMixerTrack.this.track.isMute());
		this.checkMute.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TGTrack track = TGMixerTrack.this.track;
				
				UndoableTrackSoloMute undoable = UndoableTrackSoloMute.startUndo(track);
				TuxGuitar.instance().getSongManager().getTrackManager().changeMute(track,TGMixerTrack.this.checkMute.getSelection());
				TGMixerTrack.this.mixer.fireChanges(track.getChannel(),TGMixer.MUTE);
				TuxGuitar.instance().getUndoableManager().addEdit(undoable.endUndo(track));
				TuxGuitar.instance().updateCache(true);
			}
		});
		
		new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR).setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		
		Composite effects = new Composite(composite, SWT.NONE);
		effects.setLayout(new GridLayout(4,false));
		effects.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		this.scaleChorus = new TGMixerScalePopup(effects, SWT.VERTICAL, 64, TGMixer.CHORUS, getVerticalScaleData());
		this.scaleChorus.setSelection(  this.track.getChannel().getChorus());
		
		this.scaleReverb = new TGMixerScalePopup(effects, SWT.VERTICAL, 64, TGMixer.REVERB, getVerticalScaleData());
		this.scaleReverb.setSelection(  this.track.getChannel().getReverb());
		
		this.scalePhaser = new TGMixerScalePopup(effects, SWT.VERTICAL, 64, TGMixer.PHASER, getVerticalScaleData());
		this.scalePhaser.setSelection(  this.track.getChannel().getPhaser());
		
		this.scaleTremolo = new TGMixerScalePopup(effects, SWT.VERTICAL, 64, TGMixer.TREMOLO, getVerticalScaleData());
		this.scaleTremolo.setSelection(  this.track.getChannel().getTremolo());
		
		new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR).setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		
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
			this.checkSolo.setSelection(this.track.isSolo());
			this.checkMute.setSelection(this.track.isMute());
		}
		if((type & TGMixer.CHANNEL) != 0 || (type & TGMixer.VOLUME) != 0){
			int value = this.track.getChannel().getVolume();
			this.scaleVolume.setSelection( ( value) );
			this.volumeValueLabel.setText(Integer.toString( value ));
		}
		if((type & TGMixer.CHANNEL) != 0 || (type & TGMixer.BALANCE) != 0){
			int value = this.track.getChannel().getBalance();
			this.scaleBalance.setSelection(value);
		}
		if((type & TGMixer.CHANNEL) != 0 || (type & TGMixer.CHORUS) != 0){
			int value = this.track.getChannel().getChorus();
			this.scaleChorus.setSelection( ( value) );
		}
		if((type & TGMixer.CHANNEL) != 0 || (type & TGMixer.REVERB) != 0){
			int value = this.track.getChannel().getReverb();
			this.scaleReverb.setSelection( ( value) );
		}
		if((type & TGMixer.CHANNEL) != 0 || (type & TGMixer.PHASER) != 0){
			int value = this.track.getChannel().getPhaser();
			this.scalePhaser.setSelection( ( value) );
		}
		if((type & TGMixer.CHANNEL) != 0 || (type & TGMixer.TREMOLO) != 0){
			int value = this.track.getChannel().getTremolo();
			this.scaleTremolo.setSelection( ( value) );
		}
		if((type & TGMixer.CHANNEL) != 0){
			this.mixerChannel.updateItems(true);
		}
	}
	
	public void loadProperties(){
		this.checkSolo.setText(TuxGuitar.getProperty("mixer.track.solo"));
		this.checkMute.setText(TuxGuitar.getProperty("mixer.track.mute"));
		this.volumeValueTitleLabel.setText(TuxGuitar.getProperty("mixer.channel.volume") + ":");
		
		this.scaleVolume.setText(TuxGuitar.getProperty("mixer.channel.volume"));
		this.scaleBalance.setText(TuxGuitar.getProperty("mixer.channel.balance"));
		this.scaleChorus.setText(TuxGuitar.getProperty("mixer.channel.chorus"));
		this.scaleReverb.setText(TuxGuitar.getProperty("mixer.channel.reverb"));
		this.scalePhaser.setText(TuxGuitar.getProperty("mixer.channel.phaser"));
		this.scaleTremolo.setText(TuxGuitar.getProperty("mixer.channel.tremolo"));
		
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
		
		private int type;
		private int value;
		private boolean inverted;
		private Scale scale;
		private String text;
		protected UndoableTrackChannel undoable;
		
		public TGMixerScale(Composite parent, int style, int pageIncrement, int type, Object layoutData){
			this.init(parent, style, pageIncrement, type, layoutData);
		}
		
		public void init(Composite parent, int style, int pageIncrement, int type, Object layoutData){
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
					TGMixerTrack.this.updateChannelValue(getType(), getSelection());
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
		
		public int getType(){
			return this.type;
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
				this.updateToolTipValue();
			}
		}
		
		public void updateToolTipValue(){
			if(this.text != null){
				this.scale.setToolTipText( this.text + ": " + this.value );
			}
		}
		
		public void setText(String text){
			this.text = text;
			this.updateToolTipValue();
		}
		
		public String getText(){
			return this.text;
		}
	}
	
	private class TGMixerScalePopup extends TGMixerScale{
		
		private Shell shell;
		private Composite composite;
		private Button item;
		
		public TGMixerScalePopup(Composite parent, int style, int pageIncrement, int type, Object layoutData){
			super(parent, style, pageIncrement, type, layoutData);
		}
		
		public void init(Composite parent, int style, int pageIncrement, int type, Object layoutData){
			this.shell = new Shell( parent.getShell(), SWT.NO_TRIM);
			this.shell.setVisible(false);
			this.shell.setLayout(getGridLayout());
			this.shell.addShellListener(new ShellAdapter() {
				public void shellDeactivated(ShellEvent e) {
					hideShell();
				}
				public void shellClosed(ShellEvent e) {
					e.doit = false;
					hideShell();
				}
			});
			
			this.composite = new Composite(this.shell, SWT.BORDER);
			this.composite.setLayout(getGridLayout());
			this.composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			this.item = new Button(parent, SWT.PUSH);
			this.item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					showSelect();
				}
			});
			
			super.init(this.composite, style, pageIncrement, type, layoutData);
		}
		
		private GridLayout getGridLayout(){
			GridLayout layout = new GridLayout();
			layout.horizontalSpacing = 0;
			layout.verticalSpacing = 0;
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			layout.marginTop = 0;
			layout.marginBottom = 0;
			layout.marginLeft = 0;
			layout.marginHeight = 0;
			return layout;
		}
		
		public void updateToolTipValue(){
			super.updateToolTipValue();
			if(this.getText() != null){
				this.item.setToolTipText( this.getText() + ": " + this.getSelection() );
			}
		}
		
		public void setText(String text){
			super.setText(text);
			if(this.getText() != null && this.getText().length() > 0){
				this.item.setText( this.getText().substring(0,1) );
			}
		}
		
		public void showSelect() {
			if(!this.shell.isVisible()){
				Rectangle bounds = this.item.getBounds();
				Point location = this.item.getParent().toDisplay(new Point(bounds.x, bounds.y));
				
				this.shell.pack();
				this.shell.setLocation( (location.x + (bounds.width / 2)) - (this.shell.getSize().x / 2), location.y + bounds.height);
				this.shell.setVisible(true);
				this.shell.setActive();
			}
		}
		
		public void hideShell() {
			this.shell.setVisible(false);
		}
	}
}
