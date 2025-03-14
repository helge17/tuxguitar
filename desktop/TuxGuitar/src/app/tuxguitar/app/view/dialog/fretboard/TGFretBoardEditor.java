package app.tuxguitar.app.view.dialog.fretboard;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.editor.TGExternalBeatViewerEvent;
import app.tuxguitar.app.editor.TGExternalBeatViewerManager;
import app.tuxguitar.app.system.icons.TGSkinEvent;
import app.tuxguitar.app.system.language.TGLanguageEvent;
import app.tuxguitar.app.tools.scale.ScaleEvent;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.editor.event.TGRedrawEvent;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGFretBoardEditor implements TGEventListener{

	private TGContext context;
	private TGFretBoard fretBoard;
	private boolean visible;

	public TGFretBoardEditor(TGContext context){
		this.context = context;
		this.appendListeners();
	}

	public void appendListeners() {
		TuxGuitar.getInstance().getSkinManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getScaleManager().addListener(this);
	}

	public TGFretBoard getFretBoard(){
		return this.fretBoard;
	}

	public void createFretBoard(UIPanel parent, boolean visible) {
		this.fretBoard = new TGFretBoard(this.context, parent);
		this.fretBoard.setVisible(visible);

		UITableLayout uiLayout = (UITableLayout) parent.getLayout();
		uiLayout.set(this.fretBoard.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);

		if( visible ) {
			this.showFretBoard();
		}
	}

	public void hideFretBoard(){
		this.visible = false;
		getFretBoard().setVisible(this.visible);

		TGEditorManager.getInstance(this.context).removeRedrawListener(this);
		TGExternalBeatViewerManager.getInstance(this.context).removeBeatViewerListener(this);

		TGWindow tgWindow = TGWindow.getInstance(this.context);
		tgWindow.getWindow().layout();
	}

	public void showFretBoard(){
		this.visible = true;
		getFretBoard().setVisible(this.visible);
		getFretBoard().computePackedSize();

		TGEditorManager.getInstance(this.context).addRedrawListener(this);
		TGExternalBeatViewerManager.getInstance(this.context).addBeatViewerListener(this);

		TGWindow tgWindow = TGWindow.getInstance(this.context);
		tgWindow.getWindow().layout();
	}

	public void dispose(){
		if( getFretBoard() != null && !getFretBoard().isDisposed()){
			getFretBoard().dispose();
		}
	}

	public void redraw(){
		if( getFretBoard() != null && !getFretBoard().isDisposed() /*&& !TuxGuitar.getInstance().isLocked()*/){
			getFretBoard().redraw();
		}
	}

	public void redrawPlayingMode(){
		if( getFretBoard() != null && !getFretBoard().isDisposed() && !TuxGuitar.getInstance().isLocked()){
			getFretBoard().redrawPlayingMode();
		}
	}

	public boolean isVisible(){
		return (getFretBoard() != null && !getFretBoard().isDisposed() && this.visible);
	}

	public void loadProperties(){
		if( getFretBoard() != null && !getFretBoard().isDisposed()){
			getFretBoard().loadProperties();
		}
	}

	public void loadIcons(){
		if( getFretBoard() != null && !getFretBoard().isDisposed()){
			getFretBoard().loadIcons();
		}
	}

	public void loadScale(){
		if( getFretBoard() != null){
			getFretBoard().loadScale();
		}
	}

	public void showExternalBeat(TGBeat beat) {
		if(getFretBoard() != null && !getFretBoard().isDisposed()){
			getFretBoard().setExternalBeat(beat);
		}
	}

	public void hideExternalBeat() {
		if(getFretBoard() != null && !getFretBoard().isDisposed()){
			getFretBoard().setExternalBeat(null);
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

	public static TGFretBoardEditor getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGFretBoardEditor.class.getName(), new TGSingletonFactory<TGFretBoardEditor>() {
			public TGFretBoardEditor createInstance(TGContext context) {
				return new TGFretBoardEditor(context);
			}
		});
	}
}
