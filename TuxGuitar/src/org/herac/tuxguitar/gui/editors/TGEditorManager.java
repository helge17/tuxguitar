package org.herac.tuxguitar.gui.editors;

import java.util.ArrayList;
import java.util.List;

public class TGEditorManager {
	
	private List redrawListeners;
	private List updateListeners;
	
	public TGEditorManager(){
		this.redrawListeners = new ArrayList();
		this.updateListeners = new ArrayList();
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
	
	public void addRedrawListener( TGRedrawListener listener){
		this.redrawListeners.add( listener );
	}
	
	public void removeRedrawListener( TGRedrawListener listener){
		this.redrawListeners.remove( listener );
	}
	
	public void addUpdateListener( TGUpdateListener listener){
		this.updateListeners.add( listener );
	}
	
	public void removeUpdateListener( TGUpdateListener listener){
		this.updateListeners.remove( listener );
	}
}