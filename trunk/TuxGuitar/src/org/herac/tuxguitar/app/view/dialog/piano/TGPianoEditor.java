package org.herac.tuxguitar.app.view.dialog.piano;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editor.TGExternalBeatViewerEvent;
import org.herac.tuxguitar.app.editor.TGExternalBeatViewerManager;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.tools.scale.ScaleEvent;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGPianoEditor implements TGEventListener{
	
	private TGContext context;
	private TGPiano piano;
	private UIWindow window;
	
	public TGPianoEditor(TGContext context){
		this.context = context;
	}
	
	public TGPiano getPiano(){
		return this.piano;
	}
	
	private UIWindow getPianoWindow() {
		return this.window;
	}
	
	public void show() {
		UIWindow parent = TGWindow.getInstance(this.context).getWindow();
		UITableLayout dialogLayout = new UITableLayout();
		
		this.window = TGApplication.getInstance(this.context).getFactory().createWindow(parent, false, false);
		this.window.setText(TuxGuitar.getProperty("piano.editor"));
		this.window.setLayout(dialogLayout);
		
		this.piano = new TGPiano(this.context, this.window);
		dialogLayout.set(this.piano.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.addListeners();
		
		this.window.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				removeListeners();
				TuxGuitar.getInstance().updateCache(true);
			}
		});
		
		TGDialogUtil.openDialog(this.window, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
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
	
	public boolean isDisposed() {
		return (this.isWindowDisposed() || this.isPianoDisposed());
	}
	
	public boolean isWindowDisposed() {
		return (this.window == null || this.window.isDisposed());
	}
	
	public boolean isPianoDisposed() {
		return (this.piano == null || this.piano.isDisposed());
	}
	
	public void dispose(){
		if(!isDisposed()){
			getPianoWindow().dispose();
		}
	}
	
	public void redraw(){
		if(!isDisposed()){
			getPiano().redraw();
		}
	}
	
	public void redrawPlayingMode(){
		if(!isDisposed() && !TuxGuitar.getInstance().isLocked()){
			getPiano().redrawPlayingMode();
		}
	}
	
	public void loadProperties(){
		if(!isDisposed()){
			getPiano().loadProperties();
			getPianoWindow().setText(TuxGuitar.getProperty("piano.editor"));
		}
	}
	
	public void loadIcons(){
		if(!isDisposed()){
			getPiano().loadIcons();
			getPianoWindow().setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
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
