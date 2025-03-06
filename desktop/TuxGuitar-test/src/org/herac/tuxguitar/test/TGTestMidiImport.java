package org.herac.tuxguitar.test;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.io.base.TGSongReaderHandle;
import org.herac.tuxguitar.io.midi.MidiSongReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TGTestMidiImport extends TGTest {

	@BeforeEach
	void setupTest() {
		verbose=false;
		doCloseAllSongsWithoutConfirmationChecked();
		verbose=true;
	}

	// see https://github.com/helge17/tuxguitar/issues/150
	// take care, access to configuration file for tunings definition is required for this test to succeed
	// add in run configuration the VM parameter: "-Dtuxguitar.share.path="(path to built version of tuxguitar)/share/"
	@Test
	void issue150() {
		byte[] ionianScaleBytes = {
				 0x4d, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06, 0x00, 0x01, 0x00, 0x02, 0x00, (byte) 0xdc, 0x4d, 0x54,
				 0x72, 0x6b, 0x00, 0x00, 0x00, 0x13, 0x00, (byte) 0xff, 0x51, 0x03, 0x07, (byte) 0xa1, 0x20, 0x00, (byte) 0xff, 0x58,
				 0x04, 0x04, 0x02, 0x18, 0x08, 0x01, (byte) 0xff, 0x2f, 0x00, 0x4d, 0x54, 0x72, 0x6b, 0x00, 0x00, 0x00,
				 0x71, 0x00, (byte) 0xc0, 0x00, 0x00, (byte) 0x90, 0x3c, 0x7f, (byte) 0x83, 0x38, 0x3c, 0x00, 0x00, 0x3e, 0x7f, (byte) 0x83,
				 0x38, 0x3e, 0x00, 0x00, 0x40, 0x7f, (byte) 0x83, 0x38, 0x40, 0x00, 0x00, 0x41, 0x7f, (byte) 0x83, 0x38, 0x41,
				 0x00, 0x00, 0x43, 0x7f, (byte) 0x83, 0x38, 0x43, 0x00, 0x00, 0x45, 0x7f, (byte) 0x83, 0x38, 0x45, 0x00, 0x00,
				 0x47, 0x7f, (byte) 0x83, 0x38, 0x47, 0x00, 0x00, 0x48, 0x7f, (byte) 0x83, 0x38, 0x47, 0x7f, 0x00, 0x48, 0x00,
				 (byte) 0x83, 0x38, 0x45, 0x7f, 0x00, 0x47, 0x00, (byte) 0x83, 0x38, 0x43, 0x7f, 0x00, 0x45, 0x00, (byte) 0x83, 0x38,
				 0x41, 0x7f, 0x00, 0x43, 0x00, (byte) 0x83, 0x38, 0x40, 0x7f, 0x00, 0x41, 0x00, (byte) 0x83, 0x38, 0x3e, 0x7f,
				 0x00, 0x40, 0x00, (byte) 0x83, 0x38, 0x3c, 0x7f, 0x00, 0x3e, 0x00, (byte) 0x83, 0x38, 0x3c, 0x00, 0x01, (byte) 0xff,
				 0x2f, 0x00 };
		ByteArrayInputStream stream = new ByteArrayInputStream(ionianScaleBytes);
		TGSongReaderHandle handle = new TGSongReaderHandle();
		handle.setInputStream(stream);
		handle.setFactory(new TGFactory());
		new MidiSongReader(TuxGuitar.getInstance().getContext()).read(handle);

		// check vs. expected values
		int[] expectedStrings = {2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2};
		int[] expectedValues  = {1, 3, 0, 1, 3, 5, 7, 8, 7, 5, 3, 1, 0, 3, 1};
		int i=0;
		Iterator<TGMeasure> measures = handle.getSong().getTrack(0).getMeasures();
		while(measures.hasNext()) {
			TGMeasure measure = measures.next();
			for (TGBeat beat : measure.getBeats()) {
				TGNote note = beat.getVoice(0).getNote(0);
				String msg = String.format("measure %d, note #%d, string %d, value %d", measure.getNumber(), i, note.getString(), note.getValue());
				log(msg + "...");
				assertEquals(1,beat.getVoice(0).getNotes().size(), "more than one note in beat");
				assertEquals(expectedStrings[i], note.getString(), msg);
				assertEquals(expectedValues[i], note.getValue(), msg);
				OK();
				i++;
			}
		}


	}

}
