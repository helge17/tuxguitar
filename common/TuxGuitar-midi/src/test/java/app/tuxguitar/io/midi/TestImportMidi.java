package app.tuxguitar.io.midi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.base.TGSongReaderHandle;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.io.tg.TGSongReaderImpl;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TestImportMidi {

	@Test
	public void testFileImport() throws IOException {
		// load test song
		TGSongReaderHandle handleRead = new TGSongReaderHandle();
		handleRead.setContext(new TGSongStreamContext());
		handleRead.setInputStream(getClass().getClassLoader().getResource("test_midi_20.tg").openStream());
		handleRead.setFactory(new TGFactory());
		TGSongReaderImpl songReader = new TGSongReaderImpl();
		songReader.read(handleRead);
		TGSong originalSong = handleRead.getSong();

		// export midi
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		TGSongWriterHandle handleWrite = new TGSongWriterHandle();
		handleWrite.setContext(new TGSongStreamContext());
		handleWrite.setFactory(new TGFactory());
		handleWrite.setSong(originalSong);
		handleWrite.setOutputStream(outputStream);
		MidiSongWriter writer = new MidiSongWriter();
		writer.write(handleWrite);
		byte[] rawMidiFileBuffer = outputStream.toByteArray();
		
		// re-import midi
		handleRead.setSong(null);
		handleRead.setInputStream(new ByteArrayInputStream(rawMidiFileBuffer));
		MidiSongReader midiSongReader = new MidiSongReader(new TGContext());
		midiSongReader.read(handleRead);
		TGSong importedSong = handleRead.getSong();
		
		TGMeasure measure = importedSong.getTrack(0).getMeasure(0);
		assertEquals(4, measure.countBeats());
		assertTrue(measure.getBeats().get(0).isRestBeat());
		assertFalse(measure.getBeats().get(1).isRestBeat());
		assertTrue(measure.getBeats().get(2).isRestBeat());
		assertFalse(measure.getBeats().get(3).isRestBeat());
		for (int i=0; i<3; i++) {
			assertEquals(4, measure.getBeat(i).getVoice(0).getDuration().getValue());
		}
		
		measure = importedSong.getTrack(0).getMeasure(1);
		assertEquals(6, measure.countBeats());
		assertFalse(measure.getBeats().get(0).isRestBeat());
		assertTrue(measure.getBeats().get(1).isRestBeat());
		assertFalse(measure.getBeats().get(2).isRestBeat());
		assertTrue(measure.getBeats().get(3).isRestBeat());
		assertFalse(measure.getBeats().get(4).isRestBeat());
		assertTrue(measure.getBeats().get(5).isRestBeat());
	}
}
