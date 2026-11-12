package app.tuxguitar.io.tg.v10;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.LegacyNoteEffectWriterContract;

/** Pins how the 1.0 writer stores note effects. */
public class TestWriteNoteEffect extends LegacyNoteEffectWriterContract {

	protected TGSongWriter newWriter() {
		return new TGSongWriterImpl();
	}

	protected TGSongReader newReader() {
		return new TGSongReaderImpl();
	}

	protected boolean withLetRing() {
		return false;
	}

	protected String goldenSha256() {
		return "abfff41b27a29240b4fed72ee22ebc38d4d48333a653453263fa535bc58b1d6d";
	}
}
