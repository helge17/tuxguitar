package org.herac.tuxguitar.app.editors;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.fretboard.FretBoard;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.tools.scale.ScaleEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.models.TGBeat;

public class FretBoardEditor implements TGEventListener{
	
	private FretBoard fretBoard;
	private boolean visible;
	
	public FretBoardEditor(){
		TuxGuitar.instance().getIconManager().addLoader(this);
		TuxGuitar.instance().getLanguageManager().addLoader(this);
		TuxGuitar.instance().getScaleManager().addListener(this); 
	}
	
	private FretBoard getFretBoard(){
		return this.fretBoard;
	}
	
	public void hideFretBoard(){
		this.visible = false;
		getFretBoard().setVisible(this.visible);
		TuxGuitar.instance().getEditorManager().removeRedrawListener(this);
		TuxGuitar.instance().getEditorManager().removeBeatViewerListener(this);
		TuxGuitar.instance().updateShellFooter(0,0,0);
	}
	
	public void showFretBoard(){
		this.visible = true;
		getFretBoard().setVisible(this.visible);
		TuxGuitar.instance().getEditorManager().addRedrawListener(this);
		TuxGuitar.instance().getEditorManager().addBeatViewerListener(this);
		TuxGuitar.instance().updateShellFooter(getFretBoard().getHeight(), 730,520);
	}
	
	public void showFretBoard(Composite parent) {
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0,0);
		data.bottom = new FormAttachment(100,0);
		
		this.fretBoard = new FretBoard(parent);
		this.fretBoard.setLayoutData(data);
	}
	
	public void dispose(){
		if(getFretBoard() != null && !getFretBoard().isDisposed()){
			getFretBoard().dispose();
		}
	}
	
	public void redraw(){
		if(getFretBoard() != null && !getFretBoard().isDisposed() && !TuxGuitar.instance().isLocked()){
			getFretBoard().redraw();
		}
	}
	
	public void redrawPlayingMode(){
		if(getFretBoard() != null && !getFretBoard().isDisposed() && !TuxGuitar.instance().isLocked()){
			getFretBoard().redrawPlayingMode();
		}
	}
	
	public boolean isVisible(){
		return (getFretBoard() != null && !getFretBoard().isDisposed() && this.visible);
	}
	
	public void loadProperties(){
		if(getFretBoard() != null && !getFretBoard().isDisposed()){
			getFretBoard().loadProperties();
		}
	}
	
	public void loadIcons(){
		if(getFretBoard() != null && !getFretBoard().isDisposed()){
			getFretBoard().loadIcons();
		}
	}
	
	public void loadScale(){
		if(getFretBoard() != null){
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
