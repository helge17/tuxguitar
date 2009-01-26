package org.herac.tuxguitar.gui.editors;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.song.models.TGBeat;

public class TGEditorManager {
	
	private List redrawListeners;
	private List updateListeners;
	private List beatViewerListeners;
	
	public TGEditorManager(){
		this.redrawListeners = new ArrayList();
		this.updateListeners = new ArrayList();
		this.beatViewerListeners = new ArrayList();
	}
	
	public void doRedraw( int type ){
		for(int i = 0; i < this.redrawListeners.size(); i ++){
			TGRedrawListener listener = (TGRedrawListener) this.redrawListeners.get( i );
			listener.doRedraw( type );
		}
	}
	
	public void doUpdate( int type ){
		for(int i = 0; i < this.updateListeners.size(); i ++){
			TGUpdateListener listener = (TGUpdateListener) this.updateListeners.get( i );
			listener.doUpdate( type );
		}
	}
	
	public void showExternalBeat( TGBeat beat ){
		for(int i = 0; i < this.beatViewerListeners.size(); i ++){
			TGExternalBeatViewerListener listener = (TGExternalBeatViewerListener) this.beatViewerListeners.get( i );
			listener.showExternalBeat(beat);
		}
	}
	
	public void hideExternalBeat(){
		for(int i = 0; i < this.beatViewerListeners.size(); i ++){
			TGExternalBeatViewerListener listener = (TGExternalBeatViewerListener) this.beatViewerListeners.get( i );
			listener.hideExternalBeat();
		}
	}
	
	public void addRedrawListener( TGRedrawListener listener){
		if(!this.redrawListeners.contains( listener )){
			this.redrawListeners.add( listener );
		}
	}
	
	public void removeRedrawListener( TGRedrawListener listener){
		if(this.redrawListeners.contains( listener )){
			this.redrawListeners.remove( listener );
		}
	}
	
	public void addUpdateListener( TGUpdateListener listener){
		if(!this.updateListeners.contains( listener )){
			this.updateListeners.add( listener );
		}
	}
	
	public void removeUpdateListener( TGUpdateListener listener){
		if(this.updateListeners.contains( listener )){
			this.updateListeners.remove( listener );
		}
	}
	
	public void addBeatViewerListener( TGExternalBeatViewerListener listener){
		if(!this.beatViewerListeners.contains( listener )){
			this.beatViewerListeners.add( listener );
		}
	}
	
	public void removeBeatViewerListener( TGExternalBeatViewerListener listener){
		if(this.beatViewerListeners.contains( listener )){
			this.beatViewerListeners.remove( listener );
		}
	}
}