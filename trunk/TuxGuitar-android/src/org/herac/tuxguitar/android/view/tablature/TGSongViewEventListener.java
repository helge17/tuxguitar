package org.herac.tuxguitar.android.view.tablature;

import org.herac.tuxguitar.editor.event.TGDestroyEvent;
import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.editor.event.TGUpdateMeasureEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;

public class TGSongViewEventListener implements TGEventListener {
	
	private TGSongViewController songView;
	
	public TGSongViewEventListener(TGSongViewController songView) {
		this.songView = songView;
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.songView.updateSelection();
		}
		else if( type == TGUpdateEvent.MEASURE_UPDATED ){
			this.songView.updateMeasure(((Integer) event.getAttribute(TGUpdateMeasureEvent.PROPERTY_MEASURE_NUMBER)).intValue());
		}
		else if( type == TGUpdateEvent.SONG_UPDATED ){
			this.songView.updateTablature();
		}
		else if( type == TGUpdateEvent.SONG_LOADED ){
			this.songView.updateTablature();
			this.songView.resetScroll();
			this.songView.resetCaret();
		}
	}
	
	public void processRedrawEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.NORMAL ){
			this.songView.redraw();
		}else if( type == TGRedrawEvent.PLAYING_NEW_BEAT ){
			this.songView.redrawPlayingMode();
		}
	}
	
	public void processDestroyEvent(TGEvent event) {
		this.songView.dispose();
	}
	
	public void processEvent(TGEvent event) {
		if( TGRedrawEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processRedrawEvent(event);
		} 
		else if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
		else if( TGDestroyEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processDestroyEvent(event);
		}
	}
}
