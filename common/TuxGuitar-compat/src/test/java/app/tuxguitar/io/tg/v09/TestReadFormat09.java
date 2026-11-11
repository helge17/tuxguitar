package app.tuxguitar.io.tg.v09;

import static app.tuxguitar.io.tg.LegacyFormatFixture.describeNotes;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.tg.LegacyReaderContract;
import app.tuxguitar.io.tg.LegacyStreamEncoder;
import app.tuxguitar.song.models.TGVelocities;

/** Reads hand-encoded 0.9 files: a note's velocity holds until a later note sets another. */
public class TestReadFormat09 extends LegacyReaderContract {

	private static final int VELOCITY = 0x40;
	private static final int VIBRATO = 0x40;

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	protected String version() {
		return TGSongReaderImpl.SUPPORTED_FORMAT.getVersion();
	}

	protected void trackHead(LegacyStreamEncoder encoder) {
		encoder.bytes(0);
	}

	protected void note(LegacyStreamEncoder encoder, int value, int string) {
		encoder.bytes(value, string);
	}

	@Test
	public void velocityCarriesOverUntilAnotherIsSetAndOnlyFlaggedEffectsAreRead() throws Exception {
		LegacyStreamEncoder encoder = this.measureOf(3);
		encoder.bytes(NOTE, 5, 1);
		encoder.bytes(NOTE | NEXT_BEAT | VELOCITY, 7, 2, 110);
		encoder.bytes(NOTE | NEXT_BEAT | EFFECT, 9, 3, 0, 0, VIBRATO);
		assertEquals(Arrays.asList("@960 s1 f5 v" + TGVelocities.DEFAULT, "@1920 s2 f7 v110", "@2880 s3 f9 v110 vibrato"),
				describeNotes(this.readMeasure(encoder), false));
	}
}
