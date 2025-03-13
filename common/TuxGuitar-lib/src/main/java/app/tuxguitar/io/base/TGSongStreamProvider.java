package app.tuxguitar.io.base;

public interface TGSongStreamProvider {

	String getProviderId();

	TGSongStream openStream(TGSongStreamContext context);
}
