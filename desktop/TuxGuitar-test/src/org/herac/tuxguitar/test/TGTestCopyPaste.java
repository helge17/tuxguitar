package org.herac.tuxguitar.test;

// note: created in 09/2023, after publication of v1.6.0
// these test scenarios only cover features added after this version (and partially)

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.marker.TGUpdateMarkerAction;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.measure.TGCopyMeasureAction;
import org.herac.tuxguitar.editor.action.measure.TGPasteMeasureAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGSong;


public class TGTestCopyPaste extends TGTest {

	@BeforeEach
	void setupTest() {
		verbose=false;
		doCloseAllSongsWithoutConfirmationChecked();
		doInsertMeasuresChecked(2, 9);
		doMouseClickChecked(2, 0, 1);
		// modify something, to check copy/paste is effective
		doSetDurationChecked(TGDuration.QUARTER);
		// to check "copy markers" option
		doAddMarkerChecked(2, "measure2");
		verbose=true;
	}

	protected void doAddMarkerChecked(int measureNb, String name) {
		TGSongManager songManager = TuxGuitar.getInstance().getDocumentManager().getSongManager();
		TGSong song = TuxGuitar.getInstance().getDocumentManager().getSong();

		log(String.format("creating marker %s...", name));
		assertNull(song.getMeasureHeader(measureNb-1).getMarker(),
				String.format("unexpected marker in measure %d", measureNb));

		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
				TGUpdateMarkerAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		TGMarker marker = songManager.getFactory().newMarker();
		marker.setMeasure(measureNb);
		marker.setTitle(name);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, marker);
		tgActionProcessor.process();

		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			TGMarker newMarker = song.getMeasureHeader(measureNb-1).getMarker();
			while (newMarker==null || !newMarker.getTitle().equals(name)) {
				doWait();
				newMarker = song.getMeasureHeader(measureNb-1).getMarker();
			}
		});
		OK();
	}

	protected void doCopyMeasures(int from, int to, boolean copyAllTracks, boolean copyMarkers) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
				TGCopyMeasureAction.NAME);
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_ALL_TRACKS, (Boolean)copyAllTracks);
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_1, Integer.valueOf(from));
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_MEASURE_NUMBER_2, Integer.valueOf(to));
		tgActionProcessor.setAttribute(TGCopyMeasureAction.ATTRIBUTE_COPY_MARKERS, (Boolean)copyMarkers);
		log(String.format("Copying measures %d to %d...\n", from, to));
		tgActionProcessor.process();
	}

	protected void doPasteMeasures(boolean replace, int count) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
				TGPasteMeasureAction.NAME);
		tgActionProcessor.setAttribute(TGPasteMeasureAction.ATTRIBUTE_PASTE_MODE,
				replace ? TGPasteMeasureAction.TRANSFER_TYPE_REPLACE : TGPasteMeasureAction.TRANSFER_TYPE_INSERT);
		tgActionProcessor.setAttribute(TGPasteMeasureAction.ATTRIBUTE_PASTE_COUNT, Integer.valueOf(count));
		log("pasting...\n");
		tgActionProcessor.process();
	}

	@Test
	void CopyPasteSingleTrackNoMarker() {
		log("\n--CopyPasteSingleTrackNoMarker\n");
		TGSong song = TuxGuitar.getInstance().getDocumentManager().getSong();

		doCopyMeasures(2, 4, false, false);
		doMouseClickChecked(5, 0, 1);
		doPasteMeasures(true, 1);
		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			while(song.getTrack(0).getMeasure(4).countBeats()!=4) {
				doWait();
			}
		});
		log("pasted measure content OK\n");
		log("checking absence of marker...");
		assertNull(song.getMeasureHeader(4).getMarker(), "unexpected pasted marker");
		OK();
	}

	@Test
	void CopyPasteSingleTrackWithMarker() {
		log("\n--CopyPasteSingleTrackWithMarker\n");
		TGSong song = TuxGuitar.getInstance().getDocumentManager().getSong();

		doCopyMeasures(2, 4, false, true);
		doMouseClickChecked(7, 0, 1);
		doPasteMeasures(true, 1);
		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			while(song.getTrack(0).getMeasure(6).countBeats()!=4) {
				doWait();
			}
		});
		log("pasted measure content OK\n");
		log("checking presence of marker...");
		TGMarker pastedMarker =song.getMeasureHeader(6).getMarker();
		assertTrue(pastedMarker!=null && pastedMarker.getTitle().equals("measure2"), "failed to paste marker");
		OK();
	}

	@Test
	void CopyMultiplePasteSingleTrackWithMarker() {
		log("\n--CopyMultiplePasteSingleTrackWithMarker\n");
		TGSong song = TuxGuitar.getInstance().getDocumentManager().getSong();

		doCopyMeasures(2, 4, false, true);
		doMouseClickChecked(6, 0, 1);
		doPasteMeasures(true, 2);
		assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
			while(song.getTrack(0).getMeasure(5).countBeats()!=4 || song.getTrack(0).getMeasure(8).countBeats()!=4) {
				doWait();
			}
		});
		log("pasted measure content OK\n");
		log("checking presence of markers...");
		TGMarker pastedMarker = song.getMeasureHeader(5).getMarker();
		assertTrue(pastedMarker!=null && pastedMarker.getTitle().equals("measure2"), "failed to paste marker #1");
		pastedMarker = song.getMeasureHeader(8).getMarker();
		assertTrue(pastedMarker!=null && pastedMarker.getTitle().equals("measure2"), "failed to paste marker #2");
		OK();
		log("checking markers are cloned...");	// and not several headers sharing references to the same marker
		song.getMeasureHeader(5).getMarker().setTitle("measure6");
		assertTrue(song.getMeasureHeader(1).getMarker().getTitle().equals("measure2"));
		assertTrue(song.getMeasureHeader(5).getMarker().getTitle().equals("measure6"));
		assertTrue(song.getMeasureHeader(8).getMarker().getTitle().equals("measure2"));
	}

}
