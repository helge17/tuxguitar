package app.tuxguitar.io.tg.v09;

import static app.tuxguitar.io.tg.LegacyFormatFixture.describeNotes;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.tg.LegacyReaderContract;
import app.tuxguitar.io.tg.LegacyStreamEncoder;
import app.tuxguitar.song.models.TGVelocities;
import app.tuxguitar.song.models.effects.TGEffectHarmonic;

/** Reads hand-encoded 0.9 files: a note's velocity holds until a later note sets another. */
public class TestReadFormat09 extends LegacyReaderContract {

	private static final int VELOCITY = 0x40;
	private static final int VIBRATO = 0x40;
	private static final int HARMONIC = 0x04;

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

	@Test
	public void harmonicsReadTheirDataOnlyWhenArtificialOrTapped() throws Exception {
		LegacyStreamEncoder encoder = this.measureOf(5);
		encoder.bytes(NOTE | EFFECT, 5, 1, 0, 0, HARMONIC, TGEffectHarmonic.TYPE_NATURAL);
		encoder.bytes(NOTE | NEXT_BEAT | EFFECT, 6, 2, 0, 0, HARMONIC, TGEffectHarmonic.TYPE_ARTIFICIAL, 36);
		encoder.bytes(NOTE | NEXT_BEAT | EFFECT, 7, 3, 0, 0, HARMONIC, TGEffectHarmonic.TYPE_TAPPED, 7);
		encoder.bytes(NOTE | NEXT_BEAT | EFFECT, 8, 4, 0, 0, HARMONIC, TGEffectHarmonic.TYPE_PINCH);
		encoder.bytes(NOTE | NEXT_BEAT, 9, 5);
		int velocity = TGVelocities.DEFAULT;
		assertEquals(Arrays.asList(
				"@960 s1 f5 v" + velocity + " harmonic1",
				"@1920 s2 f6 v" + velocity + " harmonic2:12",
				"@2880 s3 f7 v" + velocity + " harmonic3:7",
				"@3840 s4 f8 v" + velocity + " harmonic4:0",
				"@4800 s5 f9 v" + velocity), describeNotes(this.readMeasure(encoder), false));
	}
}
