package app.tuxguitar.app.view.dialog.piano;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.editor.TGExternalBeatViewerEvent;
import app.tuxguitar.app.editor.TGExternalBeatViewerManager;
import app.tuxguitar.app.system.icons.TGSkinEvent;
import app.tuxguitar.app.system.language.TGLanguageEvent;
import app.tuxguitar.app.tools.scale.ScaleEvent;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.editor.event.TGRedrawEvent;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

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
		TuxGuitar.getInstance().getSkinManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getScaleManager().addListener(this);
		TuxGuitar.getInstance().getEditorManager().addRedrawListener(this);
		TGExternalBeatViewerManager.getInstance(this.context).addBeatViewerListener(this);
	}

	public void removeListeners(){
		TuxGuitar.getInstance().getSkinManager().removeLoader(this);
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
				if( TGSkinEvent.EVENT_TYPE.equals(event.getEventType()) ) {
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
