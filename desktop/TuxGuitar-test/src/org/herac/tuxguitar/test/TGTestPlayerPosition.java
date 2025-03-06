package org.herac.tuxguitar.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.herac.tuxguitar.app.action.impl.transport.TGTransportModeAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportPlayPauseAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportPlayStopAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeTempoAction;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerMode;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGTrack;

public class TGTestPlayerPosition extends TGTest {

	private final int tempoValue = 240;	// to speed up tests
	private final long tickOverflowTolerance = 50;	// empirical
	private MidiPlayer player;

	/******** helper functions ************/

	protected long measureNumber(long tickPosition) {
		return (tickPosition/TGDuration.QUARTER_TIME + 3)/4;	// empirical
	}
	protected long tickPosition(long measureNb) {
		return (4*measureNb - 3) * TGDuration.QUARTER_TIME;	// empirical
	}

	/******** user interactions, and acceptance criteria ************/

	protected void doSetSongTempoChecked(int value) {
		TGTrack track = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
				TGChangeTempoAction.NAME);
		TGTempo tempo = new TGFactory().newTempo();
		tempo.setValue(value);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, track.getMeasure(0).getHeader());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TEMPO, tempo);
		log(String.format("setting tempo to %d...", value));
		tgActionProcessor.process();
		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			while (track.getMeasure(0).getHeader().getTempo().getValue() != value) {
				doWait();
			}
		});
		OK();
	}

	protected void doPlayPause() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
				TGTransportPlayPauseAction.NAME);
		log("play/pause\n");
		tgActionProcessor.process();
	}

	protected void doPlayStop() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
				TGTransportPlayStopAction.NAME);
		log("play/stop\n");
		tgActionProcessor.process();
	}

	protected void doStop() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
				TGTransportStopAction.NAME);
		log("stop\n");
		tgActionProcessor.process();
	}

	// checks all measures in the interval are played
	// if OK, returns as soon as player has started last specified measure
	protected void checkPlaysInterval(int from, int to) {
		int durationSec = (to-from)*240/tempoValue;

		assertTrue(from>0, String.format("illegal arguments %d,%d\n",from,to));
		assertTrue(to>from,  String.format("illegal arguments %d,%d\n",from,to));

		assertTimeoutPreemptively(Duration.ofSeconds(durationSec+1), () -> {
			log("waiting for player to play...");
			while (!player.isRunning()) {
				doWait();
			}
			OK();
			log(String.format("check: playing %d...", from));
			long measure = measureNumber(player.getTickPosition());
			assertTrue(measure == from, String.format("KO, played %d\n", measure));
			OK();
			// check sequence of played measures
			for (int i=from+1; i<=to; i++) {
				log(String.format("check: play %d...", i));
				while (measureNumber(player.getTickPosition()) != i) {
					assertTrue(player.isRunning());
					doWait();
				}
				OK();
			}
		});
	}

	// checks measure1 then measure2 are played
	// empirical: a small transient 'overflow' in measure1+1 can sometimes be observed
	// and is tolerated
	// if OK, returns as soon as player has started measure2
	protected void checkPlaysMeasures(int measure1, int measure2) {
		assertTimeoutPreemptively(Duration.ofSeconds(3), () -> {
			log("waiting for player to play...");
			while (!player.isRunning()) {
				doWait();
			}
			OK();
			log(String.format("check: playing %d...", measure1));
			long measure = measureNumber(player.getTickPosition());
			assertTrue(measure == measure1, String.format("KO, played %d\n", measure));
			OK();
			log(String.format("check: play %d...", measure2));
			long position = player.getTickPosition();
			while (player.isRunning() && measureNumber(position) == measure1) {
				doWait();
				position = player.getTickPosition();
			}
			assertTrue(player.isRunning(), "KO, unexpected stop");
			// tolerance for a small overflow in measure1+1
			long currentMeasureMin;
			while (player.isRunning() && measureNumber(position) != measure2) {
				currentMeasureMin = measureNumber(position-tickOverflowTolerance);
				assertTrue(currentMeasureMin==measure1,
						String.format("KO, played %d(%d)\n", measureNumber(position),position));
				doWait();
				position = player.getTickPosition();
			}
			OK();
		});
	}

	// checks it starts playing from specified measure
	// returns as soon as this measure is started
	protected void checkPlaysFrom(int from) {
		assertTrue(from>0, String.format("illegal parameters %d\n",from));

		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			log("waiting for player to play...");
			while (!player.isRunning()) {
				doWait();
			}
			OK();
			log(String.format("check: play %d...", from));
			long measure = measureNumber(player.getTickPosition());
			assertTrue(measure == from, String.format("KO, played %d\n", measure));
			OK();
		});
	}

	// check it starts playing from specified tickPosition (with some margin)
	// returns as soon as it is detected
	protected void checkPlaysFromTick(long tick) {
		assertTrue(tick>0, String.format("illegal parameter %d\n",tick));
		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			log("waiting for player to play...");
			while (!player.isRunning()) {
				doWait();
			}
			long playedTick = player.getTickPosition();
			OK();
			log(String.format("check: play tick %d...", tick));
			assertTrue((playedTick >= tick) && (playedTick - tick)<tickOverflowTolerance,
				String.format("KO, played tick %d, expected %d\n", playedTick, tick));
			OK();
		});
	}

	// check it plays until specified tickPosition
	// WARNING: there is a timeout, call this function only when close to the theoretical stopping point
	protected void checkPlaysUntilTick(long tick) {
		assertTrue(tick>0, String.format("illegal parameter %d\n",tick));
		assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
			log("waiting for player to stop...");
			long playedTick = player.getTickPosition();
			while (player.isRunning()) {
				playedTick = player.getTickPosition();
				assertTrue((playedTick <= tick),
						String.format("KO, did not stop at tick %d, played %d\n", tick, playedTick));
				doWait();
			}
			OK();
			log(String.format("check: stopped at tick %d...", tick));
			assertTrue((playedTick <= tick) && (tick - playedTick)<tickOverflowTolerance,
					String.format("KO, stopped at tick %d, expected %d\n", playedTick, tick));
			OK();
		});
	}

	// checks the last played measure, and the final position of caret after stop
	// empirical: a small transient 'overflow' in [last played measure+1] can sometimes be observed
	// (even if last played measure is the last measure of song)
	// this is tolerated
	// if OK, returns as soon as caret has been detected in the specified position
	protected void checkStopsSetCaretAt(int singleMeasurePlayed, int finalCaretMeasure) {
		Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();

		assertTrue(player.isRunning(), "player not running, can't check it stops");
		assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
			log(String.format("waiting for player to stop at %d...",singleMeasurePlayed));
			while (player.isRunning()) {
				long position = player.getTickPosition();
				long currentMeasureMax = measureNumber(position);
				long currentMeasureMin = measureNumber(position-20);
				assertTrue(currentMeasureMin == singleMeasurePlayed || currentMeasureMax == singleMeasurePlayed,
						String.format("KO, played %d (%d)\n", currentMeasureMax, position));
				doWait();
			}
			while (caret.getMeasure().getNumber()!=finalCaretMeasure) {
				doWait();
			}
			// TODO find another criterion to stop first loop: need to ensure that player is REALLY stopped
			// when just waiting until !isRunning(), next call to 'play' may do nothing if done too soon
			// (dirty) workaround: wait a bit after stop has been detected
			Thread.sleep(500);
		});
		OK();
	}

	// yet only "simple mode" (i.e. no training mode)
	protected void setPlayerModeChecked(boolean isLooped, int from, int to) {
		Integer type = MidiPlayerMode.TYPE_CUSTOM;
		Boolean loop = isLooped;
		Integer simplePercent = 100;

		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
				TGTransportModeAction.NAME);
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_TYPE, type);
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_LOOP, loop);
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_SIMPLE_PERCENT, simplePercent);
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_CUSTOM_PERCENT_FROM, 100);
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_CUSTOM_PERCENT_TO, 100);
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_CUSTOM_PERCENT_INCREMENT, 0);
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_LOOP_S_HEADER, loop ? from : -1);
		tgActionProcessor.setAttribute(TGTransportModeAction.ATTRIBUTE_LOOP_E_HEADER, loop ? to : -1);
		log(String.format("setting player mode(loop=%b, from=%d, to=%d)...", isLooped, from, to));
		tgActionProcessor.process();

		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			MidiPlayerMode mode = player.getMode();
			while ( mode.getType() != type
					|| mode.isLoop()!= loop
					|| mode.getSimplePercent() != simplePercent
					|| (loop && (mode.getLoopSHeader()!=from || mode.getLoopEHeader()!=to))
					) {
				doWait();
			}
		});
		OK();
	}

	/******** the test scenarios ************/

	@BeforeEach
	// same song structure for all tests: empty measures, fast tempo to speed up tests
	void cleanSong() {
		player =  MidiPlayer.getInstance(TuxGuitar.getInstance().getContext());
		verbose=false;
		doCloseAllSongsWithoutConfirmationChecked();

		doSetSongTempoChecked(tempoValue);
		doInsertMeasuresChecked(2, 9);
		verbose=true;
	}

	@Test
	void playUntilTheEnd() {
		log("\n----playUntilTheEnd\n");
		// click close to the end
		doMouseClickChecked(9, 0, 1);
		// play: shall stop at the end, then return caret where initially clicked
		doPlayPause();
		checkPlaysInterval(9,10);
		checkStopsSetCaretAt(10,9);
		// play shall restart from caret position
		doPlayPause();
		checkPlaysFrom(9);
	}

	@Test
	void playStop() {
		log("\n----playStop\n");
		// click anywhere
		doMouseClickChecked(4, 0, 1);
		// play, wait a bit then stop
		doPlayStop();
		checkPlaysInterval(4,6);
		doPlayStop();
		// shall stop, then return caret where initially clicked
		checkStopsSetCaretAt(6,4);
		doPlayStop();
		checkPlaysFrom(4);
	}

	@Test
	void PlayPausePlayStop() {
		log("\n----PlayPausePlayStop\n");
		doMouseClickChecked(4, 0, 1);
		doPlayPause();
		checkPlaysInterval(4,6);
		doPlayPause();
		checkStopsSetCaretAt(6,6);
		doPlayPause();
		checkPlaysInterval(6, 8);
		doPlayStop();
		checkStopsSetCaretAt(8,6);
	}

	@Test
	void playSelection() {
		// selecting left to right
		log("\n----playSelection\n");
		doSelectMeasuresChecked(4, 6);
		doPlayPause();
		checkPlaysInterval(4, 6);
		checkStopsSetCaretAt(6, 4);
	}

	@Test
	void playSelectionReverse() {
		// selecting right to left
		log("\n----playSelectionReverse\n");
		doSelectMeasuresChecked(6, 4);
		doPlayPause();
		checkPlaysInterval(4, 6);
		checkStopsSetCaretAt(6, 4);
	}

	@Test
	void playSelectionPausePlayStop() {
		log("\n----playSelectionPausePlayStop\n");
		doSelectMeasuresChecked(4,8);
		// play: shall start at selection start
		doPlayPause();
		checkPlaysInterval(4,6);
		// pause: shall stop, caret in the middle of selection
		doPlayPause();
		checkStopsSetCaretAt(6,6);
		// play again: shall restart from caret position
		doPlayPause();
		checkPlaysInterval(6, 7);
		// stop: caret shall come back to selection start
		doPlayStop();
		checkStopsSetCaretAt(7,4);
	}

	@Test
	void playClickPlaying() {
		log("\n----playClickPlaying\n");
		doMouseClickChecked(4, 0, 1);
		doPlayPause();
		checkPlaysInterval(4, 6);
		doMouseClick(2,0,1);
		//can't use checkPlaysMeasures(6, 2) here, measure 6 may not be detected
		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			while (measureNumber(player.getTickPosition()) == 6) {
				doWait();
			}
			while (measureNumber(player.getTickPosition()) != 2) {
				doWait();
			}
		});
		checkPlaysMeasures(2,3);
		doPlayStop();
		checkStopsSetCaretAt(3, 2);
	}


	@Test
	void playStopTwice() {
		log("\n----playStopTwice\n");
		doMouseClickChecked(4, 0, 1);
		doPlayPause();
		checkPlaysMeasures(4, 5);
		doPlayStop();
		checkStopsSetCaretAt(5, 4);
		// second 'stop' action, shall do nothing
		doStop();
		// not trivial to detect absence of action, just wait a bit and check caret did not move
		log("checking caret does not move...");
		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
			Thread.sleep(200);
			int measureNb = caret.getMeasure().getNumber();
			assertTrue(measureNb == 4, String.format("unexpected move to %d", measureNb));
		});
		OK();
	}

	@Test
	void loopStop() {
		log("\n----loopStop\n");
		// define a loop
		setPlayerModeChecked(true, 4, 6);
		// click anywhere
		doMouseClickChecked(2, 0, 1);
		// play: shall loop
		doPlayPause();
		checkPlaysInterval(4, 6);
		checkPlaysMeasures(6, 4);
		checkPlaysInterval(4, 5);
		// stop: shall move caret back to loop start
		doPlayStop();
		checkStopsSetCaretAt(5, 4);
	}

	@Test
	void loopPause() {
		log("\n----loopPause\n");
		// define a loop
		setPlayerModeChecked(true, 4, 6);
		// click anywhere, then play
		doMouseClickChecked(2, 0, 1);
		doPlayPause();
		checkPlaysInterval(4, 5);
		// pause within loop, caret shall be set where 'pause' was hit
		doPlayPause();
		checkStopsSetCaretAt(5, 5);
		// play: shall restart from caret position
		doPlayPause();
		checkPlaysFrom(5);
	}

	@Test
	void loopPlaySelection() {
		log("\n----loopPlaySelection\n");
		// define a loop then click anywhere
		setPlayerModeChecked(true, 6, 8);
		doMouseClickChecked(2, 0, 1);
		// select measures (out of loop), left to right
		doSelectMeasuresChecked(4, 6);
		// play: selection overrides loop
		// shall play selection once then come back to selection start
		doPlayPause();
		checkPlaysInterval(4, 6);
		checkStopsSetCaretAt(6,4);
		doPlayPause();
		checkPlaysFrom(4);
	}

	@Test
	void loopPlaySelectionReverse() {
		log("\n----loopPlaySelectionReverse\n");
		// define a loop then click anywhere
		setPlayerModeChecked(true, 6, 8);
		doMouseClickChecked(2, 0, 1);
		// select measures (out of loop), right to left
		doSelectMeasuresChecked(6, 4);
		// play: selection overrides loop
		// shall play selection once then come back to selection start
		doPlayPause();
		checkPlaysInterval(4, 6);
		checkStopsSetCaretAt(6,4);
		doPlayPause();
		checkPlaysFrom(4);
	}
}
