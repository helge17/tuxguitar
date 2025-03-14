package app.tuxguitar.midi.synth.remote;

import java.io.IOException;

public interface TGCommand<T> {

	T process() throws IOException;
}
