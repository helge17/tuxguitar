package app.tuxguitar.midi.synth;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.song.models.TGSong;

/**
 * Writes a song as an audio file in place of an app's own audio export: an app registers one with the synth manager (an
 * extension of this type), and its audio export then offers the formats it names and writes through it.
 */
public interface TGSynthAudioExporter {

	// the formats offered, by name, the first being the default
	List<String> getFormats();

	// the file name extension of a format's files (without the dot)
	String getExtension(String format);

	// the song as an audio file of the format named
	void export(TGSong song, String format, OutputStream out) throws IOException, MidiPlayerException;
}
