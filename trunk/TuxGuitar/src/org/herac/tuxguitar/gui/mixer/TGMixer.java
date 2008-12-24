/*
 * Created on 20-mar-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.mixer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGUpdateListener;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.system.icons.IconLoader;
import org.herac.tuxguitar.gui.system.language.LanguageLoader;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGTrack;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TGMixer implements TGUpdateListener,IconLoader,LanguageLoader{
	
	public static final int MUTE = 0x01;
	public static final int SOLO = 0x02;
	public static final int VOLUME = 0x04;
	public static final int BALANCE = 0x08;
	public static final int CHORUS = 0x10;
	public static final int REVERB = 0x20;
	public static final int PHASER = 0x40;
	public static final int TREMOLO = 0x80;
	public static final int CHANNEL = 0x100;
	
	public static final int CHANGE_ALL = (MUTE | SOLO | VOLUME | BALANCE | CHORUS | REVERB | PHASER | TREMOLO | CHANNEL);
	
	protected Shell dialog;
	private List tracks;
	private Scale volumeScale;
	private Label volumeValueLabel;
	private Label volumeValueTitleLabel;
	private String volumeTip;
	private int volumeValue;
	
	public TGMixer() {
		this.tracks = new ArrayList();
	}
	
	public void show() {
		this.dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM);
		this.loadData();
		
		this.addListeners();
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				removeListeners();
				TuxGuitar.instance().updateCache(true);
			}
		});
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER );
	}
	
	public void addListeners(){
		TuxGuitar.instance().getIconManager().addLoader(this);
		TuxGuitar.instance().getLanguageManager().addLoader(this);
		TuxGuitar.instance().getEditorManager().addUpdateListener(this);
	}
	
	public void removeListeners(){
		TuxGuitar.instance().getIconManager().removeLoader(this);
		TuxGuitar.instance().getLanguageManager().removeLoader(this);
		TuxGuitar.instance().getEditorManager().removeUpdateListener(this);
	}
	
	protected void loadData(){
		this.tracks.clear();
		Iterator it = TuxGuitar.instance().getSongManager().getSong().getTracks();
		while (it.hasNext()) {
			TGTrack track = (TGTrack) it.next();
			TGMixerTrack trackMixer = new TGMixerTrack(this,track);
			trackMixer.init(this.dialog);
			this.tracks.add(trackMixer);
		}
		Composite composite = new Composite(this.dialog, SWT.BORDER);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.CENTER,SWT.FILL,true,true));
		
		this.volumeValue = -1;
		
		this.volumeScale = new Scale(composite, SWT.VERTICAL);
		this.volumeScale.setMaximum(10);
		this.volumeScale.setMinimum(0);
		this.volumeScale.setIncrement(1);
		this.volumeScale.setPageIncrement(1);
		this.volumeScale.setLayoutData(new GridData(SWT.CENTER,SWT.FILL,true,true));
		
		Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		
		Composite volumeValueComposite = new Composite(composite, SWT.NONE);
		volumeValueComposite.setLayout(new GridLayout(2,false));
		
		this.volumeValueTitleLabel = new Label(volumeValueComposite, SWT.NONE);
		
		this.volumeValueLabel = new Label(volumeValueComposite, SWT.CENTER);
		this.volumeValueLabel.setLayoutData(getVolumeValueLabelData());
		
		this.volumeScale.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				changeVolume();
			}
		});
		
		this.loadVolume();
		this.loadIcons();
		this.loadProperties(false);
		
		this.dialog.setLayout(getLayout(this.dialog.getChildren().length));
		this.dialog.pack();
	}
	
	private GridLayout getLayout(int columns){
		GridLayout layout = new GridLayout(columns, false);
		layout.verticalSpacing = 1;
		layout.horizontalSpacing = 1;
		return layout;
	}
	
	protected void changeVolume(){
		int volume = (short)(TGMixer.this.volumeScale.getMaximum() - TGMixer.this.volumeScale.getSelection());
		if(volume != TuxGuitar.instance().getPlayer().getVolume()){
			TuxGuitar.instance().getPlayer().setVolume(volume);
			this.volumeScale.setToolTipText(TGMixer.this.volumeTip + ": " + TuxGuitar.instance().getPlayer().getVolume());
			this.volumeValueLabel.setText(Integer.toString(TGMixer.this.volumeScale.getMaximum() - TGMixer.this.volumeScale.getSelection()));
			this.volumeValue = volume;
		}
	}
	
	protected void loadVolume(){
		int volume = TuxGuitar.instance().getPlayer().getVolume();
		if(this.volumeValue != volume){
			this.volumeScale.setSelection(this.volumeScale.getMaximum() - TuxGuitar.instance().getPlayer().getVolume());
			this.volumeValueLabel.setText(Integer.toString(this.volumeScale.getMaximum() - this.volumeScale.getSelection()));
			this.volumeValue = volume;
		}
	}
	
	private GridData getVolumeValueLabelData(){
		GridData data = new GridData(SWT.CENTER,SWT.NONE,true,false);
		data.minimumWidth = 40;
		return data;
	}
	
	protected void clear(){
		Control[] controls = this.dialog.getChildren();
		for(int i = 0;i < controls.length;i++){
			controls[i].dispose();
		}
	}
	
	public boolean isDisposed() {
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	public synchronized void fireChanges(TGChannel channel,int type){
		Iterator it = this.tracks.iterator();
		while(it.hasNext()){
			TGMixerTrack mixer = (TGMixerTrack)it.next();
			if(mixer.getTrack().getChannel().getChannel() == channel.getChannel()){
				channel.copy(mixer.getTrack().getChannel());
			}
			mixer.fireChanges(type);
		}
		if (TuxGuitar.instance().getPlayer().isRunning()) {
			TuxGuitar.instance().getPlayer().updateControllers();
		}
	}
	
	public synchronized void loadProperties(){
		this.loadProperties(true);
	}
	
	public synchronized void loadProperties(boolean pack){
		if(!isDisposed()){
			Iterator it = this.tracks.iterator();
			while(it.hasNext()){
				TGMixerTrack mixer = (TGMixerTrack)it.next();
				mixer.loadProperties();
			}
			this.volumeValueTitleLabel.setText(TuxGuitar.getProperty("mixer.volume") + ":");
			this.volumeTip = TuxGuitar.getProperty("mixer.volume");
			this.volumeScale.setToolTipText(this.volumeTip + ": " + TuxGuitar.instance().getPlayer().getVolume());
			this.dialog.setText(TuxGuitar.getProperty("mixer"));
			if( pack ){
				this.dialog.pack();
				this.dialog.layout(true,true);
				this.dialog.redraw();
			}
		}
	}
	
	public synchronized void loadIcons(){
		if(!isDisposed()){
			this.dialog.setImage(TuxGuitar.instance().getIconManager().getAppIcon());
		}
	}
	
	public synchronized void updateItems(){
		if(!isDisposed()){
			Iterator it = this.tracks.iterator();
			while(it.hasNext()){
				TGMixerTrack mixer = (TGMixerTrack)it.next();
				mixer.updateItems();
			}
		}
	}
	
	public synchronized void updateValues(){
		if(!isDisposed()){
			this.loadVolume();
			
			Iterator it = this.tracks.iterator();
			while(it.hasNext()){
				TGMixerTrack mixer = (TGMixerTrack)it.next();
				mixer.fireChanges(CHANGE_ALL);
			}
		}
	}
	
	public synchronized void update(){
		if(!isDisposed()){
			new SyncThread(new Runnable() {
				public void run() {
					if(!isDisposed()){
						TGMixer.this.clear();
						TGMixer.this.loadData();
						TGMixer.this.dialog.layout(true,true);
						TGMixer.this.dialog.redraw();
					}
				}
			}).start();
		}
	}
	
	public synchronized void dispose() {
		if(!isDisposed()){
			this.dialog.dispose();
		}
	}

	public void doUpdate(int type) {
		if( type == TGUpdateListener.SELECTION ){
			this.updateItems();
		}else if( type == TGUpdateListener.SONG_LOADED ){
			this.update();
		}
	}
}
