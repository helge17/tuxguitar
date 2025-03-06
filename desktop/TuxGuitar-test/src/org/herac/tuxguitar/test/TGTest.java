package org.herac.tuxguitar.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.app.action.impl.file.TGCloseAllDocumentsAction;
import org.herac.tuxguitar.app.action.impl.selector.TGExtendSelectionNextAction;
import org.herac.tuxguitar.app.action.impl.selector.TGExtendSelectionPreviousAction;
import org.herac.tuxguitar.app.action.listener.save.TGUnsavedDocumentInterceptor;
import org.herac.tuxguitar.app.document.TGDocument;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.component.tab.Selector;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.duration.TGSetDurationAction;
import org.herac.tuxguitar.editor.action.file.TGNewSongAction;
import org.herac.tuxguitar.editor.action.measure.TGAddMeasureListAction;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGTest {
	// note:
	// methods triggering actions are prefixed with "do"
	//    results of "do" methods with suffix "Checked" ARE checked (i.e. action was effective or test fails)
	//    with no suffix, action is launched but nothing is checked
	// methods prefixed with "check" do not trigger any action, but check something

	static boolean TGisRunning;
	protected boolean verbose;

	@BeforeAll
	static void startTG() {
		if (TGisRunning) return;

		TuxGuitar tg = TuxGuitar.getInstance();
		assertNotNull(tg);

		new Thread() {
			public void run() {
				tg.createApplication(null);
			}
		}.start();
		assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
			while (!tg.isInitialized()) {
				Thread.sleep(500);
			}
		});
		TGisRunning = true;
	}

	// VERY basic logging to console, TODO: improve!
	protected void log(String s) {
		if (verbose) System.out.printf("%s",s);
	}
	protected void OK() {
		log("OK\n");
	}

	protected void doWait() throws InterruptedException {
		Thread.sleep(10);
	}

	protected void doInsertMeasuresChecked(int from, int nb) {
		TGTrack track = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
		int nbMeasuresInit = track.countMeasures();

		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
				TGAddMeasureListAction.NAME);
		tgActionProcessor.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_COUNT, nb);
		tgActionProcessor.setAttribute(TGAddMeasureListAction.ATTRIBUTE_MEASURE_NUMBER, from);
		log(String.format("inserting %d measures... ",nb));
		tgActionProcessor.process();

		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			while (track.countMeasures() != (nbMeasuresInit+nb)) {
				doWait();
			}
		});
		OK();
	}

	// measures and strings are numbered from 1
	// beats are numbered from 0
	// not fully representative in all cases:
	// not all conditions of onMouseUp (TGMouseClickAction) are emulated here
	protected void doMouseClick(int measureNb, int beatNb, int stringNb) {
		TGActionManager tgActionManager = TGActionManager.getInstance(TuxGuitar.getInstance().getContext());
		Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
		TGTrack track = tablature.getCaret().getTrack();

		log(String.format("clicking in measure %d, beat %d, string %d\n", measureNb, beatNb, stringNb));
		// see MouseKit.onMouseDown and TGStartDragSelectionAction.processAction
		TGActionContext context = tgActionManager.createActionContext();
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, track.getMeasure(measureNb-1));
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, track.getMeasure(measureNb-1).getBeat(beatNb));
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, track.getString(stringNb));

		tgActionManager.execute(TGMoveToAction.NAME, context);
		TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		Selector selector = tablature.getSelector();
		selector.initializeSelection(beat);

		// onMouseUp (not all conditions)
		if (MidiPlayer.getInstance(TuxGuitar.getInstance().getContext()).isRunning()) {
			log("mouse up, midi player running\n");
			context = tgActionManager.createActionContext();
			context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION, true);
			TGActionManager.getInstance(TuxGuitar.getInstance().getContext()).execute(TGMoveToAction.NAME, context);
		}
	}

	protected void doMouseShiftClick(int measureNb, int beatNb, int stringNb) {
		TGActionManager tgActionManager = TGActionManager.getInstance(TuxGuitar.getInstance().getContext());
		Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
		TGTrack track = tablature.getCaret().getTrack();
		Selector selector = tablature.getSelector();

		log(String.format("shift-clicking in measure %d, beat %d, string %d\n", measureNb, beatNb, stringNb));
		// see MouseKit.onMouseDown and TGUpdateDragSelectionAction.processAction
		TGActionContext context = tgActionManager.createActionContext();
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, track.getMeasure(measureNb-1));
		TGBeat beat = track.getMeasure(measureNb-1).getBeat(beatNb);
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, track.getString(stringNb));
		if (!selector.isActive() && tablature.getCaret().getSelectedBeat() != null) {
			selector.initializeSelection(tablature.getCaret().getSelectedBeat());
		}
		context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION, true);
		TGActionManager.getInstance(TuxGuitar.getInstance().getContext()).execute(TGMoveToAction.NAME, context);
		if (selector.getStartBeat() != null && beat.getMeasure().getTrack() == selector.getStartBeat().getMeasure().getTrack()) {
			selector.updateSelection(beat);
		}
		// onMouseUp (not all conditions)
		if (MidiPlayer.getInstance(TuxGuitar.getInstance().getContext()).isRunning()) {
			log("mouse up, midi player running\n");
			context = tgActionManager.createActionContext();
			context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION, true);
			TGActionManager.getInstance(TuxGuitar.getInstance().getContext()).execute(TGMoveToAction.NAME, context);
		}
	}

	protected void checkCaretPosition(int measureNb, int beatNb, int stringNb) {
		Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
		log("checking caret position...");

		assertTrue(caret.getMeasure().getNumber() == measureNb ,
				String.format("measure %d, expected %d\n", caret.getMeasure().getNumber(), measureNb));
		assertTrue((TGBeat)caret.getSelectedBeat() == caret.getMeasure().getBeat(beatNb),
				"unexpected beat\n");
		assertTrue(caret.getStringNumber() == stringNb,
				String.format("string %d, expected %d\n", caret.getStringNumber(), stringNb));
		OK();
	}

	protected void doMouseClickChecked(int measureNb, int beatNb, int stringNb) {
		doMouseClick(measureNb, beatNb, stringNb);
		checkCaretPosition(measureNb, beatNb, stringNb);
	}

	protected void checkCaretReachesMeasure(int measureNb) {
		Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
		log(String.format("checking caret is in measure %d...",measureNb));
		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			while (caret.getMeasure().getNumber() != measureNb) {
				doWait();
			}
		});
		OK();
	}

	protected void doSelectMeasuresChecked(int from, int to) {
		TGActionProcessor tgActionProcessor;
		Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();

		assertTrue(from>0 && to>0 && to!=from, String.format("illegal parameters, should be different and positive: %d,%d\n",from,to));

		int nb;
		if (to>from) {
			tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
					TGExtendSelectionNextAction.NAME);
			nb = to-from;
		} else {
			tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
					TGExtendSelectionPreviousAction.NAME);
			nb = from-to;
		}
		doMouseClickChecked(from, 0, 1);
		for (int i=1; i<nb+1; i++) {
			log("extending selection...");
			tgActionProcessor.process();
			final int iteration = i;
			assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
				boolean ok = false;
				Selector selector = tablature.getSelector();
				while (!ok) {
					int start = to > from ? from : from-iteration;
					int end = to > from ? from + iteration : from;
					ok  = (selector.getStartBeat() == null ? false : selector.getStartBeat().getMeasure().getNumber() == start);
					ok &= (selector.getEndBeat() == null ? false : selector.getEndBeat().getMeasure().getNumber() == end);
					doWait();
				}
			});
		}
		OK();
	}

	protected void doNewSongChecked() {
		TGContext context = TuxGuitar.getInstance().getContext();
		TGDocumentListManager docManager = TGDocumentListManager.getInstance(context);
		List<TGDocument> documents = new ArrayList<TGDocument>(docManager.getDocuments());
		int documentsNb = documents.size();
		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
				TGNewSongAction.NAME);
		log("creating new song...");
		tgActionProcessor.process();
		assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
			// TODO remove this sleep (yet needed to stabilize tests), find a better criterion
			Thread.sleep(200);
			while (docManager.getDocuments().size() != documentsNb+1) {
				doWait();
			}
		});
		OK();
	}

	protected void doCloseAllSongsWithoutConfirmationChecked() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
				TGCloseAllDocumentsAction.NAME);
		tgActionProcessor.setAttribute(TGUnsavedDocumentInterceptor.UNSAVED_INTERCEPTOR_BY_PASS, Boolean.TRUE);
		log("closing all songs...");
		tgActionProcessor.process();

		// wait until closed
		assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
			boolean allClosed = false;
			while (!allClosed) {
				TGContext context = TuxGuitar.getInstance().getContext();
				List<TGDocument> documents = new ArrayList<TGDocument>(TGDocumentListManager.getInstance(context).getDocuments());
				// TODO improvement required, this is not a clean criterion
				allClosed = (documents.size() == 1);
				Thread.sleep(500);
			}
		});
		OK();
	}

	protected void checkCaretDuration(int value) {
		// TODO: difficult to find a clean criterion to detect end of setDuration...
		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			boolean ok = false;
			while (!ok) {
				Thread.sleep(100);
				ok = (value == TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getDuration().getValue());
			}
		});
		OK();
	}

	protected void doSetDuration(int value) {
		Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
		TGDuration duration = tablature.getCaret().getDuration();
		duration.setValue(value);
		duration.setDotted(false);
		duration.setDoubleDotted(false);
		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
				TGSetDurationAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE, tablature.getCurrentNoteRange());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE, tablature.getCurrentBeatRange());
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION, duration);
		log(String.format("setting duration to %d", value));
		tgActionProcessor.process();
	}
	protected void doSetDurationChecked(int value) {
		doSetDuration(value);
		log("...");
		checkCaretDuration(value);
	}
}
