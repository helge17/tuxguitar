package org.herac.tuxguitar.app.view.dialog.piano;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editor.TGExternalBeatViewerEvent;
import org.herac.tuxguitar.app.editor.TGExternalBeatViewerManager;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.tools.scale.ScaleEvent;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGPianoEditor implements TGEventListener{
	
	private TGContext context;
	private TGPiano piano;
	
	public TGPianoEditor(TGContext context){
		this.context = context;
	}
	
	public void show() {
		Shell dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(), SWT.DIALOG_TRIM);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("piano.editor"));
		
		this.piano = new TGPiano(this.context, dialog,SWT.NONE);
		
		this.addListeners();
		dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				removeListeners();
			}
		});
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	public void addListeners(){
		TuxGuitar.getInstance().getIconManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getScaleManager().addListener(this);
		TuxGuitar.getInstance().getEditorManager().addRedrawListener(this);
		TGExternalBeatViewerManager.getInstance(this.context).addBeatViewerListener(this);
	}
	
	public void removeListeners(){
		TuxGuitar.getInstance().getIconManager().removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
		TuxGuitar.getInstance().getScaleManager().removeListener(this); 
		TuxGuitar.getInstance().getEditorManager().removeRedrawListener(this);
		TGExternalBeatViewerManager.getInstance(this.context).removeBeatViewerListener(this);
	}
	
	private TGPiano getPiano(){
		return this.piano;
	}
	
	public void dispose(){
		if(!isDisposed()){
			getPiano().getShell().dispose();
			getPiano().dispose();
		}
	}
	
	public void redraw(){
		if(!isDisposed()/* && !TuxGuitar.getInstance().isLocked()*/){
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
		int type = ((Integer)event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.NORMAL ){
			this.redraw();
		}else if( type == TGRedrawEvent.PLAYING_NEW_BEAT ){
			this.redrawPlayingMode();
		}
	}
	
	public void processExternalBeatEvent(TGEvent event) {
		if( TGExternalBeatViewerEvent.ACTION_SHOW.equals(event.getAttribute(TGExternalBeatViewerEvent.PROPERTY_ACTION)) ) {
			this.showExternalBeat((TGBeat) event.getAttribute(TGExternalBeatViewerEvent.PROPERTY_BEAT));
		}
		else if( TGExternalBeatViewerEvent.ACTION_HIDE.equals(event.getAttribute(TGExternalBeatViewerEvent.PROPERTY_ACTION)) ) {
			this.hideExternalBeat();
		}
	}
	
	public void processEvent(final TGEvent event) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadIcons();
				}
				else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadProperties();
				}
				else if( TGRedrawEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					processRedrawEvent(event);
				}
				else if( TGExternalBeatViewerEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					processExternalBeatEvent(event);
				}
				else if( ScaleEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadScale();
				}
			}
		});
	}
	
	public static TGPianoEditor getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGPianoEditor.class.getName(), new TGSingletonFactory<TGPianoEditor>() {
			public TGPianoEditor createInstance(TGContext context) {
				return new TGPianoEditor(context);
			}
		});
	}
}
