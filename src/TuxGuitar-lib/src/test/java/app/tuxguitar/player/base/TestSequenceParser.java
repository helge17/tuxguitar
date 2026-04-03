package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.TreeMap;

import app.tuxguitar.io.tg.TestFileFormat20;
import app.tuxguitar.player.impl.sequencer.MidiSequenceHandlerImpl;
import app.tuxguitar.player.impl.sequencer.MidiSequencerImpl;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.util.TGContext;
import org.junit.jupiter.api.Test;

public class TestSequenceParser {

	@Test
	public void testTempoTimestampMap() throws IOException {
		TGContext context = new TGContext();
		TGSong song = new TestFileFormat20().readSong("tempo_20.tg", true).getSong();
		MidiSequenceParser parser = new MidiSequenceParser(song, new TGSongManager(), 0);
		MidiSequencerImpl sequencer = new MidiSequencerImpl(context);
		MidiSequenceHandlerImpl seqHandler = new MidiSequenceHandlerImpl(sequencer,1);
		parser.parse(seqHandler);

		TreeMap<Long,TGTempo> tempoMap = parser.getTempoMap();
		assertEquals(4, tempoMap.size());
		Long quarterTime = TGDuration.QUARTER_TIME;
		assertEquals(110, tempoMap.get(quarterTime).getRawValue());	// measure 1
		assertEquals(130, tempoMap.get(quarterTime*8).getRawValue());	// measure 3
		assertEquals(160, tempoMap.get(quarterTime*29).getRawValue());	// measure 6 (repeatx2 => measures 3 and 4 are played 3 times)
		assertEquals(180, tempoMap.get(quarterTime*34).getRawValue());	// measure 8

		TreeMap<Long,Long> timestampMap = parser.getTimestampMap();
		assertEquals(4, tempoMap.size());
		long t = 0l;
		assertEquals(t, timestampMap.get(quarterTime));
		t += 7 * 60 * 1000 / 110;	// measures 1-2 = 7 quarters at 110 bpm
		assertEquals(t, timestampMap.get(quarterTime*8));
		t += 21 * 60 * 1000 / 130;	// measures (3-4)*3 + 5 = 21 quarters at 130 bpm
		assertEquals(t, timestampMap.get(quarterTime*29));
		t += 5 * 60 * 1000 / 160;	// measures 6-7 = 5 quarters at 160 bpm
		assertEquals(t, timestampMap.get(quarterTime*34));
	}

}
