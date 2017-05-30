package org.herac.tuxguitar.player.impl.midiport.vst.jni;

import java.io.File;

public final class VSTPlugin extends VSTObject {
	
	private File file;
	
	public VSTPlugin(File file) throws Throwable {
		if( file == null || !file.exists() ){
			throw new Exception("VSTPlugin failed: No such file");
		}
		this.file = file;
		this.setInstance(this.malloc(this.file.getAbsolutePath()));
		if( !this.isInitialized() ){
			throw new Exception("VSTPlugin could not be loaded!");
		}
	}
	
	public void finalize() {
		if( this.isInitialized()) {
			this.delete(this.getInstance());
			this.setInstance(0);
		}
	}
	
	public long initEffect() {
		if( this.isInitialized()){
			return this.initEffect(this.getInstance());
		}
		return 0;
	}
	
	public File getFile() {
		return file;
	}

	private synchronized native long malloc( String filename );
	
	private synchronized native void delete( long instance );
	
	private synchronized native long initEffect( long instance );
	
}
