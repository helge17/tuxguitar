package org.herac.tuxguitar.app.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.piano.Piano;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.tools.scale.ScaleEvent;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.models.TGBeat;

public class PianoEditor implements TGEventListener{
	
	private Piano piano;
	
	public PianoEditor(){
		super();
	}
	
	public void show() {
		Shell dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(), SWT.DIALOG_TRIM);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("piano.editor"));
		
		this.piano = new Piano(dialog,SWT.NONE);
		
		this.addListeners();
		dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				removeListeners();
				TuxGuitar.getInstance().updateCache(true);
			}
		});
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	public void addListeners(){
		TuxGuitar.getInstance().getIconManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getScaleManager().addListener(this);
		TuxGuitar.getInstance().getEditorManager().addRedrawListener(this);
		TuxGuitar.getInstance().getEditorManager().addBeatViewerListener(this);
	}
	
	public void removeListeners(){
		TuxGuitar.getInstance().getIconManager().removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
		TuxGuitar.getInstance().getScaleManager().removeListener(this); 
		TuxGuitar.getInstance().getEditorManager().removeRedrawListener(this);
		TuxGuitar.getInstance().getEditorManager().removeBeatViewerListener(this);
	}
	
	private Piano getPiano(){
		return this.piano;
	}
	
	public void dispose(){
		if(!isDisposed()){
			getPiano().getShell().dispose();
			getPiano().dispose();
		}
	}
	
	public void redraw(){
		if(!isDisposed() && !TuxGuitar.getInstance().isLocked()){
			getPiano().redraw();
		}
	}
	
	public void redrawPlayingMode(){
		if(!isDisposed() && !TuxGuitar.getInstance().isLocked()){
			getPiano().redrawPlayingMode();
		}
	}
	
	public boolean isDisposed() {
		return (this.getPiano() == null || getPiano().isDisposed());
	}
	
	public void loadProperties(){
		if(!isDisposed()){
			getPiano().loadProperties();
			getPiano().getShell().setText(TuxGuitar.getProperty("piano.editor"));
		}
	}
	
	public void loadIcons(){
		if(!isDisposed()){
			getPiano().loadIcons();
		}
	}
	
	public void loadScale(){
		if(!isDisposed()){
			getPiano().loadScale();
		}
	}
	
	public void showExternalBeat(TGBeat beat) {
		if(!isDisposed()){
			getPiano().setExternalBeat(beat);
		}
	}
	
	public void hideExternalBeat() {
		if(!isDisposed()){
			getPiano().setExternalBeat(null);
		}
	}
	
	public void processRedrawEvent(TGEvent event) {
		int type = ((Integer)event.getProperty(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.NORMAL ){
			this.redraw();
		}else if( type == TGRedrawEvent.PLAYING_NEW_BEAT ){
			this.redrawPlayingMode();
		}
	}
	
	public void processExternalBeatEvent(TGEvent event) {
		if( TGExternalBeatViewerEvent.ACTION_SHOW.equals(event.getProperty(TGExternalBeatViewerEvent.PROPERTY_ACTION)) ) {
			this.showExternalBeat((TGBeat) event.getProperty(TGExternalBeatViewerEvent.PROPERTY_BEAT));
		}
		else if( TGExternalBeatViewerEvent.ACTION_HIDE.equals(event.getProperty(TGExternalBeatViewerEvent.PROPERTY_ACTION)) ) {
			this.hideExternalBeat();
		}
	}
	
	public void processEvent(TGEvent event) {
		if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIcons();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadProperties();
		}
		else if( TGRedrawEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processRedrawEvent(event);
		}
		else if( TGExternalBeatViewerEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processExternalBeatEvent(event);
		}
		else if( ScaleEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadScale();
		}
	}
}
