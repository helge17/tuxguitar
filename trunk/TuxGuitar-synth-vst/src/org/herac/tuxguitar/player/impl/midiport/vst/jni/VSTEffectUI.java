package org.herac.tuxguitar.player.impl.midiport.vst.jni;

public final class VSTEffectUI extends VSTObject {
	
	public VSTEffectUI(VSTEffect effect) {
		this.setInstance(this.malloc(effect.getInstance()));
	}
	
	public void finalize(){
		if( this.isInitialized()){
			this.delete(this.getInstance());
			this.setInstance(0);
		}
	}
	
	public void openNativeEditor(){
		if( this.isInitialized() && !this.isNativeEditorOpen() ){
			this.openEditor( getInstance() );
		}
	}
	
	public void closeNativeEditor(){
		if( this.isInitialized() && this.isNativeEditorOpen() ){
			this.closeEditor( getInstance() );
		}
	}
	
	public boolean isNativeEditorOpen(){
		return ( isInitialized() && isEditorOpen( getInstance() ) );
	}
	
	public boolean isEditorAvailable(){
		return ( isInitialized() && isEditorAvailable( getInstance() ) );
	}
	
	private synchronized native long malloc( long instance );
	
	private synchronized native void delete( long instance );
	
	private synchronized native void openEditor( long instance );
	
	private synchronized native void closeEditor( long instance );
	
	private synchronized native boolean isEditorOpen( long instance );
	
	private synchronized native boolean isEditorAvailable( long instance );
}
