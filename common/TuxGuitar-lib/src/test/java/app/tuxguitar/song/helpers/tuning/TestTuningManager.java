package app.tuxguitar.song.helpers.tuning;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGClef;
import app.tuxguitar.util.TGContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TestTuningManager {
	
	@Test
	public void testInstrumentTunings() throws FileNotFoundException {
		TuningManager tuningManager = TuningManager.getInstance(new TGContext());
		File configFile = new File(getClass().getClassLoader().getResource("tunings.xml").getFile());
		tuningManager.loadTunings(new FileInputStream(configFile));
		
		// undefined instrument
		InstrumentTuning instrumentTuning = tuningManager.getInstrumentTuning(-1);
		assertNull(instrumentTuning);

		// all bass guitars
		for (int instrument = 32; instrument <= 39 ; instrument++) {
			instrumentTuning = tuningManager.getInstrumentTuning(instrument);
			assertEquals(TGClef.CLEF_BASS_8, instrumentTuning.getClef());
			int strings[] = instrumentTuning.getTuning().getValues();
			assertEquals(4, strings.length);
			assertEquals(43, strings[0]);
			assertEquals(38, strings[1]);
			assertEquals(33, strings[2]);
			assertEquals(28, strings[3]);
		}
	}
}
