package app.tuxguitar.io.tg.v08;

import static app.tuxguitar.io.tg.LegacyFormatFixture.describeNotes;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.tg.LegacyReaderContract;
import app.tuxguitar.io.tg.LegacyStreamEncoder;

/** Reads hand-encoded 0.8 files: every note carries its own velocity. */
public class TestReadFormat08 extends LegacyReaderContract {

	private static final int VIBRATO_AND_DEAD_NOTE = 0x05;

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	protected String version() {
		return TGSongReaderImpl.SUPPORTED_FORMAT.getVersion();
	}

	protected void trackHead(LegacyStreamEncoder encoder) {
		// a 0.8 track starts with its name
	}

	protected void note(LegacyStreamEncoder encoder, int value, int string) {
		encoder.bytes(value, 100, string);
	}

	@Test
	public void notesReadTheirOwnVelocityAndOnlyFlaggedEffects() throws Exception {
		LegacyStreamEncoder encoder = this.measureOf(2);
		encoder.bytes(NOTE | EFFECT, 5, 80, 1, VIBRATO_AND_DEAD_NOTE);
		encoder.bytes(NOTE | NEXT_BEAT, 7, 110, 2);
		assertEquals(Arrays.asList("@960 s1 f5 v80 vibrato dead", "@1920 s2 f7 v110"),
				describeNotes(this.readMeasure(encoder), false));
	}
}
