package org.herac.tuxguitar.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.document.TGDocument;
import org.herac.tuxguitar.app.document.TGDocumentListAttributes;
import org.herac.tuxguitar.app.document.TGDocumentListManager;
import org.herac.tuxguitar.app.view.component.tab.Selector;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.file.TGLoadSongAction;
import org.herac.tuxguitar.song.models.TGDuration;


public class TGTestSwitchTab extends TGTest {

	private TGDocumentListManager documentManager;
	private TGDocument doc1;
	private TGDocument doc2;

	private void doSwitchDocumentChecked(TGDocument doc) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(),
				TGLoadSongAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, doc.getSong());
		tgActionProcessor.setAttribute(TGDocumentListAttributes.ATTRIBUTE_UNWANTED, doc.isUnwanted());
		log("Switching to doc ...");
		tgActionProcessor.process();
		// TODO: difficult to find a clean criterion to detect everything has been refreshed after switching doc...
		assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
			boolean ok = false;
			while (!ok) {
				Thread.sleep(100);
				ok = (doc == documentManager.findCurrentDocument());
			}
		});
		OK();
	}

	@BeforeEach
	void setupTest() {
		documentManager = TGDocumentListManager.getInstance(TuxGuitar.getInstance().getContext());

		verbose=false;
		doCloseAllSongsWithoutConfirmationChecked();
		doc1 = documentManager.findCurrentDocument();
		doInsertMeasuresChecked(2, 5);
		doMouseClickChecked(1, 0, 1);

		doNewSongChecked();
		doc2 = documentManager.findCurrentDocument();
		doInsertMeasuresChecked(2, 9);
		doMouseClickChecked(1, 0, 1);
		verbose=true;
	}

	// did reproduce known issue from 1.6.0beta3, and validate its correction
	// https://github.com/helge17/tuxguitar/issues/101
	@Test
	@DisplayName("issue #101")
	void testIssue101() {
		log("\n-- testIssue101\n");
		doMouseClickChecked(3, 0, 1);
		doSwitchDocumentChecked(doc1);
		doSwitchDocumentChecked(doc2);
		log("Checking selection is inactive...");
		assertFalse(TuxGuitar.getInstance().getTablatureEditor().getTablature().getSelector().isActive());
		OK();
	}

	// introduced after 1.6.0
	// see https://github.com/helge17/tuxguitar/discussions/81
	@Test
	@DisplayName("Caret position is memorized")
	void switchTabsKeepsCaret() {
		log("\n-- switchTabsKeepsCaret\n");
		doMouseClickChecked(3, 0, 4);
		doSetDurationChecked(TGDuration.QUARTER);
		doMouseClickChecked(3, 2, 4);
		doSwitchDocumentChecked(doc1);
		checkCaretPosition(1, 0, 1);
		doMouseClickChecked(2, 0, 2);
		doSwitchDocumentChecked(doc2);
		checkCaretPosition(3, 2, 4);
		doSwitchDocumentChecked(doc1);
		checkCaretPosition(2, 0, 2);
		doNewSongChecked();
		checkCaretPosition(1, 0, 1);
	}

	@Test
	@DisplayName("Selection is memorized")
	void switchTabsKeepsSelection() {
		log("\n-- switchTabsKeepsSelection()\n");
		doSelectMeasuresChecked(2, 4);
		doSwitchDocumentChecked(doc1);
		Selector selector = TuxGuitar.getInstance().getTablatureEditor().getTablature().getSelector();
		log("checking selection is inactive...");
		assertFalse(selector.isActive());
		OK();
		doSelectMeasuresChecked(1, 3);
		doSwitchDocumentChecked(doc2);
		log("checking selection is active...");
		assertTrue(selector.isActive());
		OK();
		log("checking selection starts from 2...");
		assertNotNull(selector.getStartBeat());
		assertTrue(selector.getStartBeat().getMeasure().getNumber() == 2);
		OK();
		log("checking selection ends at 4...");
		assertNotNull(selector.getEndBeat());
		assertTrue(selector.getEndBeat().getMeasure().getNumber() == 4);
		OK();
		doSwitchDocumentChecked(doc1);
		log("checking selection is active...");
		assertTrue(selector.isActive());
		OK();
		log("checking selection starts from 1...");
		assertNotNull(selector.getStartBeat());
		assertTrue(selector.getStartBeat().getMeasure().getNumber() == 1);
		OK();
		log("checking selection ends at 3...");
		assertNotNull(selector.getEndBeat());
		assertTrue(selector.getEndBeat().getMeasure().getNumber() == 3);
		OK();
		doMouseClickChecked(2, 0, 3);
		doSwitchDocumentChecked(doc2);
		doSwitchDocumentChecked(doc1);
		log("checking selection is inactive...");
		assertFalse(selector.isActive());
		OK();
	}
}
