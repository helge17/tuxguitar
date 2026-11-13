package app.tuxguitar.vst3;

/**
 * Hears what an open plugin tells its host.
 */
public interface TGVst3Listener {

	void notice(TGVst3Notice notice);
}
