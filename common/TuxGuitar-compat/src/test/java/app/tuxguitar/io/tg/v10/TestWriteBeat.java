package app.tuxguitar.io.tg.v10;

import java.util.Arrays;
import java.util.List;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyBeatWriterContract;

/** Pins how the 1.0 writer stores beats. */
public class TestWriteBeat extends LegacyBeatWriterContract {

	protected TGSongWriter newWriter() {
		return new TGSongWriterImpl();
	}

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	/** 1.0 has one voice: voice 1's notes join voice 0, its rests, the directions and the strokes are dropped. */
	protected List<String> expectedBeats() {
		return Arrays.asList(
				"+0 v0:d8 s1f5 s6f3 chord[Am 1 [0, 1, 2, 2, 0, -1]] text[intro]",
				"+480 v0:d8 s2f7",
				"+960 v0:d8 s3f3",
				"+1440 v0:d4. rest",
				"+2880 v0:d4 s1f0 s6f3");
	}

	protected String goldenSha256() {
		return "01aa9998e25eaa41a545fb25476624c5083c1f680e69cf2ce24169a092d317a1";
	}
}
