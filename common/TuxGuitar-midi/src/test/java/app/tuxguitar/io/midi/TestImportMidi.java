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
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
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
		assertTrue(measure.getBeat(0).isRestBeat());
		assertFalse(measure.getBeat(1).isRestBeat());
		assertTrue(measure.getBeat(2).isRestBeat());
		assertFalse(measure.getBeat(3).isRestBeat());
		for (int i=0; i<3; i++) {
			assertEquals(4, measure.getBeat(i).getVoice(0).getDuration().getValue());
		}
		
		measure = importedSong.getTrack(0).getMeasure(1);
		assertEquals(6, measure.countBeats());
		assertFalse(measure.getBeat(0).isRestBeat());
		assertTrue(measure.getBeat(1).isRestBeat());
		assertFalse(measure.getBeat(2).isRestBeat());
		assertTrue(measure.getBeat(3).isRestBeat());
		assertFalse(measure.getBeat(4).isRestBeat());
		assertTrue(measure.getBeat(5).isRestBeat());
	}

	// quantization: too long and too short notes, too early or too late start
	@Test
	public void testQuantizationDuration() throws IOException {
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
		MidiSettings settings = new MidiSettings();
		settings.setMaxDivision(3);
		settings.setMaxDurationValue(TGDuration.SIXTEENTH);
		handleRead.getContext().setAttribute(MidiSettings.class.getName(), settings);
		MidiSongReader midiSongReader = new MidiSongReader(new TGContext());
		midiSongReader.read(handleRead);
		TGSong importedSong = handleRead.getSong();

		// too short
		TGMeasure measure = importedSong.getTrack(1).getMeasure(0);
		assertEquals(4, measure.getBeat(0).getVoice(0).getDuration().getValue());
		assertFalse(measure.getBeat(0).getVoice(0).getDuration().isDotted());
		assertFalse(measure.getBeat(0).isRestBeat());
		assertTrue(measure.getBeat(1).isRestBeat());

		// too long, measure 2
		measure = importedSong.getTrack(1).getMeasure(1);
		assertEquals(4, measure.getBeat(0).getVoice(0).getDuration().getValue());
		assertFalse(measure.getBeat(0).getVoice(0).getDuration().isDotted());
		assertFalse(measure.getBeat(0).isRestBeat());
		assertTrue(measure.getBeat(1).isRestBeat());
		int i = 2;
		while(measure.getBeat(i).isRestBeat()) {
			i++;
		}
		assertEquals(4, measure.getBeat(i).getVoice(0).getDuration().getValue());
		assertFalse(measure.getBeat(i).getVoice(0).getDuration().isDotted());
		assertEquals(measure.getStart() + 2 * TGDuration.QUARTER_TIME, measure.getBeat(i).getStart());
		assertTrue(measure.getBeat(i+1).isRestBeat());

		// measure 3, too early, inside measure
		measure = importedSong.getTrack(1).getMeasure(2);
		assertTrue(measure.getBeat(0).isRestBeat());
		i = 1;
		while(measure.getBeat(i).isRestBeat()) {
			i++;
		}
		assertEquals(4, measure.getBeat(i).getVoice(0).getDuration().getValue());
		assertFalse(measure.getBeat(i).getVoice(0).getDuration().isDotted());
		assertEquals(measure.getStart() + TGDuration.QUARTER_TIME, measure.getBeat(i).getStart());

		// measure 4, too early then too late in the same measure
		measure = importedSong.getTrack(1).getMeasure(3);
		assertFalse(measure.getBeat(0).isRestBeat());
		assertEquals(4, measure.getBeat(0).getVoice(0).getDuration().getValue());
		assertFalse(measure.getBeat(0).getVoice(0).getDuration().isDotted());
		assertTrue(measure.getBeat(1).isRestBeat());
		i = 1;
		while(measure.getBeat(i).isRestBeat()) {
			i++;
		}
		assertEquals(4, measure.getBeat(i).getVoice(0).getDuration().getValue());
		assertFalse(measure.getBeat(i).getVoice(0).getDuration().isDotted());
		assertEquals(measure.getStart() + 3 * TGDuration.QUARTER_TIME, measure.getBeat(i).getStart());

		// note starting too early at measure boundary
		measure = importedSong.getTrack(1).getMeasure(4);
		for (TGBeat b : measure.getBeats()) {
			assertTrue(b.isRestBeat());
		}
		measure = importedSong.getTrack(1).getMeasure(5);
		assertEquals(4, measure.getBeat(0).getVoice(0).getDuration().getValue());
		assertFalse(measure.getBeat(0).getVoice(0).getDuration().isDotted());
		assertFalse(measure.getBeat(0).isRestBeat());
		assertTrue(measure.getBeat(1).isRestBeat());

		// note starting too late at measure boundary
		i = 2;
		while(measure.getBeat(i).isRestBeat()) {
			i++;
		}
		assertEquals(4, measure.getBeat(i).getVoice(0).getDuration().getValue());
		assertFalse(measure.getBeat(i).getVoice(0).getDuration().isDotted());
		assertEquals(measure.getStart() + 3* TGDuration.QUARTER_TIME, measure.getBeat(i).getStart());

		// quarter split over 2 measures
		measure = importedSong.getTrack(1).getMeasure(6);
		i=0;
		while(measure.getBeat(i).isRestBeat()) {
			i++;
		}
		assertEquals(8, measure.getBeat(i).getVoice(0).getDuration().getValue());
		assertFalse(measure.getBeat(i).getVoice(0).getDuration().isDotted());
		assertEquals(measure.getStart() + 7*TGDuration.QUARTER_TIME/2, measure.getBeat(i).getStart());

		measure = importedSong.getTrack(1).getMeasure(7);
		assertEquals(8, measure.getBeat(0).getVoice(0).getDuration().getValue());
		assertFalse(measure.getBeat(0).getVoice(0).getDuration().isDotted());
		// one of the 2 notes is tied (don't care about the order)
		assertTrue(measure.getBeat(0).getVoice(0).getNote(0).isTiedNote()
					|| measure.getBeat(0).getVoice(0).getNote(1).isTiedNote());

		// check song is valid
		// especially, check tied notes are valid (on the same string)
		TGSongManager songManager = new TGSongManager();
		assertTrue(songManager.getMeasureErrors(importedSong).isEmpty());

		// import short triplet with different parameters
		TGMeasure refMeasure = originalSong.getTrack(1).getMeasure(8);
		assertTrue(identicalRhythm(refMeasure, importedSong.getTrack(1).getMeasure(8)));
		// re-import with smaller max duration
		settings.setMaxDurationValue(TGDuration.SIXTY_FOURTH);
		handleRead.getContext().setAttribute(MidiSettings.class.getName(), settings);
		writer.write(handleWrite);
		rawMidiFileBuffer = outputStream.toByteArray();
		handleRead.setSong(null);
		handleRead.setInputStream(new ByteArrayInputStream(rawMidiFileBuffer));
		handleRead.getContext().setAttribute(MidiSettings.class.getName(), settings);
		midiSongReader.read(handleRead);
		importedSong = handleRead.getSong();
		assertTrue(identicalRhythm(refMeasure, importedSong.getTrack(1).getMeasure(8)));
	}

	private boolean identicalRhythm(TGMeasure meas1, TGMeasure meas2) {
		if ((meas1 == null) || (meas2 == null)) return false;
		if (meas1.getBeats().isEmpty() || meas2.getBeats().isEmpty()) return false;
		if (meas1.getBeats().size() != meas2.getBeats().size()) return false;
		for (int i = 0; i < meas1.getBeats().size(); i++) {
			if (meas1.getBeat(i).getPreciseStart() != meas1.getBeat(i).getPreciseStart()) return false;
		}
		return true;
	}
}
