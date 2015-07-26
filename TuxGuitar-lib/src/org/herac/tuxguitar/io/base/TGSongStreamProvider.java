package org.herac.tuxguitar.io.base;

public interface TGSongStreamProvider {
	
	String getProviderId();
	
	TGSongStream openStream(TGSongStreamContext context);
}
