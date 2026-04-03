package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.io.tg.TestFileFormat20;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.helpers.TGMeasureError;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTimeSignature;
import app.tuxguitar.song.models.TGVoice;
import org.junit.jupiter.api.Test;

public class TestFreeEditionMode {
	
	private TGFactory factory;
	private TGSongManager songMgr;
	
	public TestFreeEditionMode() {
		this.factory = new TGFactory();
		this.songMgr = new TGSongManager();
	}
	
	@Test
	public void testInsertBeat() throws Throwable {
		TGSong song = new TestFileFormat20().readSong("freeEditionMode_20.tg", true).getSong();
		assertNotNull(song);
		
		TGMeasure measure = song.getTrack(0).getMeasure(0);
		TGBeat beat = measure.getBeat(0);
		TGVoice voice = beat.getVoice(0);
		TGDuration duration = factory.newDuration();
		duration.setValue(TGDuration.WHOLE);
		
		songMgr.getMeasureManager().insertRestBeatWithoutControl(beat, voice.getIndex());
		
		// no added measure, first measure with 2 whole notes, invalid
		assertEquals(2, song.countMeasureHeaders());
		measure = song.getTrack(0).getMeasure(0);
		assertEquals(2, measure.countBeats());
		List<TGMeasureError> listErrors = songMgr.getMeasureManager().getMeasureErrors(measure);
		assertEquals(1, listErrors.size());
		assertEquals(measure, listErrors.get(0).getMeasure());
		assertEquals(0, listErrors.get(0).getVoiceIndex());
		assertEquals(TGMeasureError.VOICE_TOO_LONG, listErrors.get(0).getErrCode());
		// first beat: inserted rest
		beat = measure.getBeat(0);
		assertTrue(beat.isRestBeat());
		assertFalse(beat.getVoice(0).isEmpty());
		assertTrue(beat.getVoice(0).isRestVoice());
		assertEquals(TGDuration.WHOLE, beat.getVoice(0).getDuration().getValue());
		assertTrue(beat.getVoice(1).isEmpty());
		// second beat, note initially present
		beat = measure.getBeat(1);
		assertFalse(beat.isRestBeat());
		assertFalse(beat.getVoice(0).isRestVoice());
		assertEquals(TGDuration.WHOLE, beat.getVoice(0).getDuration().getValue());
		assertTrue(beat.getVoice(1).isEmpty());
	}
	
	@Test
	public void testInsertBeatVoice2() throws Throwable {
		TGSong song = new TestFileFormat20().readSong("freeEditionMode_20.tg", true).getSong();
		assertNotNull(song);
		
		TGMeasure measure = song.getTrack(0).getMeasure(1);
		TGBeat beat = measure.getBeat(1);
		TGVoice voice = beat.getVoice(1);
		TGDuration duration = factory.newDuration();
		duration.setValue(TGDuration.HALF);
		
		songMgr.getMeasureManager().insertRestBeatWithoutControl(beat, voice.getIndex());
		
		assertEquals(2, song.countMeasureHeaders());
		measure = song.getTrack(0).getMeasure(1);
		assertEquals(3, measure.countBeats());
		List<TGMeasureError> listErrors = songMgr.getMeasureManager().getMeasureErrors(measure);
		assertEquals(1, listErrors.size());
		assertEquals(measure, listErrors.get(0).getMeasure());
		assertEquals(1, listErrors.get(0).getVoiceIndex());
		assertEquals(TGMeasureError.VOICE_TOO_LONG, listErrors.get(0).getErrCode());
		// first beat: unchanged
		beat = measure.getBeat(0);
		assertEquals(1, beat.getVoice(0).countNotes());
		assertEquals(3, beat.getVoice(0).getNote(0).getValue());
		assertEquals(TGDuration.WHOLE, beat.getVoice(0).getDuration().getValue());
		assertEquals(1, beat.getVoice(1).countNotes());
		assertEquals(0, beat.getVoice(1).getNote(0).getValue());
		assertEquals(TGDuration.HALF, beat.getVoice(1).getDuration().getValue());
		// second beat: inserted half, voice 2
		beat = measure.getBeat(1);
		assertTrue(beat.isRestBeat());
		assertTrue(beat.getVoice(0).isEmpty());
		assertFalse(beat.getVoice(1).isEmpty());
		assertTrue(beat.getVoice(1).isRestVoice());
		assertEquals(TGDuration.HALF, beat.getVoice(1).getDuration().getValue());
		// third beat, unchanged (initial second beat)
		beat = measure.getBeat(2);
		assertTrue(beat.getVoice(0).isEmpty());
		assertFalse(beat.getVoice(1).isEmpty());
		assertEquals(1, beat.getVoice(1).countNotes());
		assertEquals(2, beat.getVoice(1).getNote(0).getValue());
		assertEquals(TGDuration.HALF, beat.getVoice(1).getDuration().getValue());
	}

	@Test
	public void testChangeDuration() throws Throwable {
		TGSong song = new TestFileFormat20().readSong("freeEditionMode_20.tg", true).getSong();
		assertNotNull(song);
		
		TGMeasure measure = song.getTrack(0).getMeasure(0);
		TGBeat beat = measure.getBeat(0);
		List<TGBeat> listBeats = new ArrayList<TGBeat>();
		listBeats.add(beat);
		TGVoice voice = beat.getVoice(0);
		TGDuration duration = factory.newDuration();
		duration.setValue(TGDuration.QUARTER);
		
		songMgr.setFreeEditionMode(true);
		songMgr.getMeasureManager().changeDuration(measure, beat, duration.clone(factory), voice.getIndex(), true);
		
		// no added measure, first measure with 1 quarter note, invalid
		assertEquals(2, song.countMeasureHeaders());
		measure = song.getTrack(0).getMeasure(0);
		assertEquals(1, measure.countBeats());
		List<TGMeasureError> listErrors = songMgr.getMeasureManager().getMeasureErrors(measure);
		assertEquals(1, listErrors.size());
		assertEquals(measure, listErrors.get(0).getMeasure());
		assertEquals(0, listErrors.get(0).getVoiceIndex());
		assertEquals(TGMeasureError.VOICE_TOO_SHORT, listErrors.get(0).getErrCode());
		// first beat: quarter
		beat = measure.getBeat(0);
		assertFalse(beat.isRestBeat());
		assertFalse(beat.getVoice(0).isEmpty());
		assertFalse(beat.getVoice(0).isRestVoice());
		assertEquals(1, beat.getVoice(0).countNotes());
		assertEquals(TGDuration.QUARTER, beat.getVoice(0).getDuration().getValue());
		assertTrue(beat.getVoice(1).isEmpty());
	}

	@Test
	public void testChangeTimeSignature() throws Throwable {
		TGSong song = new TestFileFormat20().readSong("freeEditionMode_20.tg", true).getSong();
		assertNotNull(song);
		
		TGMeasureHeader measureHeader = song.getMeasureHeader(0);
		TGTimeSignature timeSignature = factory.newTimeSignature();
		timeSignature.setNumerator(3);
		TGDuration denominatorDuration = factory.newDuration();
		denominatorDuration.setValue(TGDuration.QUARTER);
		timeSignature.setDenominator(denominatorDuration);
		
		songMgr.setFreeEditionMode(true);
		
		// 1st test: one measure only
		songMgr.changeTimeSignature(song, measureHeader, timeSignature, false);
		
		// expected result: content of measures did not change, measure 1 is now invalid
		assertEquals(2, song.countMeasureHeaders());
		TGMeasure measure = song.getTrack(0).getMeasure(0);
		List<TGMeasureError> listErrors = songMgr.getMeasureManager().getMeasureErrors(measure);
		assertEquals(1, listErrors.size());
		assertEquals(measure, listErrors.get(0).getMeasure());
		assertEquals(0, listErrors.get(0).getVoiceIndex());
		assertEquals(TGMeasureError.VOICE_TOO_LONG, listErrors.get(0).getErrCode());
		assertEquals(3, song.getMeasureHeader(0).getTimeSignature().getNumerator());
		assertEquals(1, measure.getBeat(0).getVoice(0).countNotes());

		measure = song.getTrack(0).getMeasure(1);
		assertTrue(songMgr.getMeasureManager().isMeasureDurationValid(measure));
		assertEquals(4, song.getMeasureHeader(1).getTimeSignature().getNumerator());
		
		// 2nd test: until the end
		timeSignature.setNumerator(2);
		songMgr.changeTimeSignature(song, measureHeader, timeSignature, true);
		
		// expected result: content of measures did not change, measures 1 and 2 are now invalid
		assertEquals(2, song.countMeasureHeaders());
		measure = song.getTrack(0).getMeasure(0);
		assertEquals(2, song.getMeasureHeader(0).getTimeSignature().getNumerator());
		assertEquals(1, measure.getBeat(0).getVoice(0).countNotes());
		assertEquals(2, song.getMeasureHeader(1).getTimeSignature().getNumerator());

		measure = song.getTrack(0).getMeasure(1);
		listErrors = songMgr.getMeasureErrors(song);
		assertEquals(5, listErrors.size());
		int nErr=0;
		for (int nTrack=0; nTrack<2; nTrack++) {
			for (int nMeasure=0; nMeasure<2; nMeasure++) {
				for (int nVoice=0; nVoice<2; nVoice++) {
					if (nVoice==0 || (nTrack==0 && nMeasure==1)) {
						TGMeasureError err = listErrors.get(nErr);
						assertEquals(nTrack+1, err.getMeasure().getTrack().getNumber());
						assertEquals(nMeasure+1, err.getMeasure().getNumber());
						assertEquals(nVoice, err.getVoiceIndex());
						assertEquals(TGMeasureError.VOICE_TOO_LONG, err.getErrCode());
						nErr++;
					}
				}
			}
		}
		assertEquals(5, nErr);
	}

	@Test
	public void testFix() throws Throwable {
		// test song:
		// - track 1 has only measures with invalid voice(s)
		// - track2 has the targeted content for fixed measures
		// test: fix measures in track1 and compare result with track2
		TGSong song = new TestFileFormat20().readSong("fix_20.tg", true).getSong();
		assertNotNull(song);
		for (int m=0; m<song.countMeasureHeaders(); m++) {
			TGMeasure measure = song.getTrack(0).getMeasure(m);
			List<TGMeasureError> listErrors = songMgr.getMeasureManager().getMeasureErrors(measure);
			assertNotEquals(0, listErrors.size());
			for (TGMeasureError err : listErrors) {
				songMgr.getMeasureManager().fixVoiceLongShort(measure, err.getVoiceIndex());
			}
			String errMsg = "KO, measure " + String.valueOf(measure.getNumber());
			assertTrue(this.measuresContentsAreEqual(measure, song.getTrack(1).getMeasure(m)), errMsg);
		}
	}
	
	private boolean measuresContentsAreEqual(TGMeasure measure1, TGMeasure measure2) {
		if (measure1.countBeats() != measure2.countBeats()) return false;
		for (int b=0; b<measure1.countBeats(); b++) {
			for (int v=0; v<TGBeat.MAX_VOICES; v++) {
				TGVoice voice1 = measure1.getBeat(b).getVoice(v);
				TGVoice voice2 = measure2.getBeat(b).getVoice(v);
				if (voice1.isEmpty() != voice2.isEmpty()) return false;
				if (!voice1.isEmpty()) {
					if (voice1.isRestVoice() != voice2.isRestVoice()) return false;
					if (voice1.getDuration().getPreciseTime() != voice2.getDuration().getPreciseTime()) return false;
					for (int n=0; n<voice1.countNotes(); n++) {
						TGNote note1 = voice1.getNote(n);
						TGNote note2 = voice2.getNote(n);
						if (note1.getValue() != note2.getValue()) return false;
						if (note1.getString() != note2.getString()) return false;
					}
				}
			}
		}
		return true;
	}
	
}
