package org.herac.tuxguitar.player.impl.midiport.vst.jni;

public final class VSTEffect extends VSTObject {
	
	public VSTEffect(VSTPlugin plugin) {
		this.setInstance(this.malloc(plugin.initEffect()));
	}
	
	public void finalize(){
		if(isInitialized()){
			this.delete(this.getInstance());
			this.setInstance(0);
		}
	}
	
	public void open(){
		if( isInitialized() ){
			this.openEffect( getInstance() );
		}
	}
	
	public void close(){
		if( isInitialized() ){
			this.closeEffect( getInstance() );
		}
	}
	
	public void sendMessages(Object[] messages){
		if( isInitialized() ){
			this.sendMessages( getInstance(), messages );
		}
	}
	
	public void sendProcessReplacing(float[][] inputs, float[][] outputs, int blocksize){
		if( isInitialized() ){
			this.sendProcessReplacing(getInstance(), inputs, outputs, blocksize );
		}
	}
	
	public int getNumParams(){
		if( isInitialized() ){
			return this.getNumParams( getInstance());
		}
		return 0;
	}
	
	public int getNumInputs(){
		if( isInitialized() ){
			return this.getNumInputs( getInstance());
		}
		return 0;
	}
	
	public int getNumOutputs(){
		if( isInitialized() ){
			return this.getNumOutputs( getInstance());
		}
		return 0;
	}
	
	public void setBlockSize( int value ){
		if( isInitialized() ){
			this.setBlockSize( getInstance() , value );
		}
	}
	
	public void setSampleRate( float value ){
		if( isInitialized() ){
			this.setSampleRate( getInstance() , value );
		}
	}
	
	public void setParameter( int index , float value ){
		if( isInitialized() ){
			this.setParameter( getInstance() , index , value );
		}
	}
	
	public float getParameter( int index ){
		if( isInitialized() ){
			return getParameter( getInstance() , index );
		}
		return 0;
	}
	
	public String getParameterName( int index ){
		if( isInitialized() ){
			return getParameterName( getInstance() , index );
		}
		return null;
	}
	
	public String getParameterLabel( int index ){
		if( isInitialized() ){
			return getParameterLabel( getInstance() , index );
		}
		return null;
	}
	
	private synchronized native long malloc( long instance );
	
	private synchronized native void delete( long instance );
	
	private synchronized native void openEffect( long instance );
	
	private synchronized native void closeEffect( long instance );
	
	private synchronized native int getNumParams( long instance );
	
	private synchronized native int getNumInputs( long instance );
	
	private synchronized native int getNumOutputs( long instance );
	
	private synchronized native void setBlockSize( long instance , int value );
	
	private synchronized native void setSampleRate( long instance , float value );
	
	private synchronized native void setParameter( long instance , int index , float value );
	
	private synchronized native float getParameter( long instance , int index );
	
	private synchronized native String getParameterName( long instance , int index );
	
	private synchronized native String getParameterLabel( long instance , int index );
	
	private synchronized native void sendMessages( long instance, Object[] messages );
	
	private synchronized native void sendProcessReplacing(long instance, float[][] inputs, float[][] outputs, int blocksize);
}
