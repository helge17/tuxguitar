package app.tuxguitar.editor.undo.impl.custom;

import static app.tuxguitar.editor.action.SongActionFixture.reads;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.composition.TGChangeClefAction;
import app.tuxguitar.editor.action.composition.TGChangeKeySignatureAction;
import app.tuxguitar.editor.action.composition.TGChangeTripletFeelAction;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * Pins the undoable edits of the clef, the key signature and the triplet feel (TGUndoableClef,
 * TGUndoableKeySignature, TGUndoableTripletFeel), each changing the second of four measures to "c". Undo puts the
 * second measure back as it was, and when the change went to the end, the measures after it too; redo changes them
 * again. An edit cannot be undone twice, nor redone before it is undone.
 *
 * The four measures start as "a | a | b | b"; the song reads back after the change, the undo and the redo as
 * "measures / measures / measures".
 */
public class TestUndoableMeasureSetting {

	/** A measure setting: its action and undo controller, the attributes they read, and how a measure holds it. */
	private static class Setting {
		final String name;
		final Function<TGContext, TGActionBase> action;
		final Supplier<TGUndoableActionController> controller;
		final String attribute;
		final String applyToEnd;
		final ToIntFunction<TGMeasure> read;
		final ObjIntConsumer<TGMeasure> write;
		final int[] abc;

		Setting(String name, Function<TGContext, TGActionBase> action, Supplier<TGUndoableActionController> controller, String attribute, String applyToEnd,
				ToIntFunction<TGMeasure> read, ObjIntConsumer<TGMeasure> write, int... abc) {
			this.name = name;
			this.action = action;
			this.controller = controller;
			this.attribute = attribute;
			this.applyToEnd = applyToEnd;
			this.read = read;
			this.write = write;
			this.abc = abc;
		}
	}

	private static final Setting CLEF = new Setting("clef", TGChangeClefAction::new, TGUndoableClefController::new,
			TGChangeClefAction.ATTRIBUTE_CLEF, TGChangeClefAction.ATTRIBUTE_APPLY_TO_END, TGMeasure::getClef, TGMeasure::setClef,
			TGMeasure.CLEF_TREBLE, TGMeasure.CLEF_BASS, TGMeasure.CLEF_TENOR);

	private static final List<Setting> SETTINGS = Arrays.asList(
		CLEF,
		new Setting("key signature", TGChangeKeySignatureAction::new, TGUndoableKeySignatureController::new,
				TGChangeKeySignatureAction.ATTRIBUTE_KEY_SIGNATURE, TGChangeKeySignatureAction.ATTRIBUTE_APPLY_TO_END, TGMeasure::getKeySignature,
				TGMeasure::setKeySignature, 0, 3, 5),
		new Setting("triplet feel", TGChangeTripletFeelAction::new, TGUndoableTripletFeelController::new,
				TGChangeTripletFeelAction.ATTRIBUTE_TRIPLET_FEEL, TGChangeTripletFeelAction.ATTRIBUTE_APPLY_TO_END, measure -> measure.getHeader().getTripletFeel(),
				(measure, value) -> measure.getHeader().setTripletFeel(value),
				TGMeasureHeader.TRIPLET_FEEL_NONE, TGMeasureHeader.TRIPLET_FEEL_EIGHTH, TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH));

	/** The four measures of a document song, and the action context on the second one. */
	private static class Song {
		final SongActionFixture fixture = SongActionFixture.onDocument();
		final Setting setting;

		Song(Setting setting) {
			this.setting = setting;
			fixture.measureCount(4);
			TGTrack track = fixture.song.getTrack(0);
			for (int m = 0; m < 4; m++) {
				setting.write.accept(track.getMeasure(m), setting.abc[m / 2]);
			}
			TGMeasure second = track.getMeasure(1);
			fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
			fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, second);
			fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, second.getHeader());
		}

		TGUndoableEdit change(boolean applyToEnd) {
			fixture.actionContext.setAttribute(setting.attribute, setting.abc[2]);
			fixture.actionContext.setAttribute(setting.applyToEnd, applyToEnd);
			return fixture.executeUndoable(setting.action.apply(fixture.context), setting.controller.get());
		}

		String changeUndoRedo(boolean applyToEnd) {
			change(applyToEnd);
			String changed = measures();
			fixture.undo();
			String undone = measures();
			fixture.redo();
			return changed + " / " + undone + " / " + measures();
		}

		String measures() {
			return fixture.measures(measure -> String.valueOf((char) ('a' + indexOf(setting.read.applyAsInt(measure)))));
		}

		int indexOf(int value) {
			for (int i = 0; i < setting.abc.length; i++) {
				if (setting.abc[i] == value) {
					return i;
				}
			}
			return -1;
		}
	}

	@TestFactory
	public List<DynamicTest> undoAndRedo() {
		List<DynamicTest> tests = new ArrayList<DynamicTest>();
		for (Setting each : SETTINGS) {
			tests.add(reads(each.name + " on one measure", "a | c | b | b / a | a | b | b / a | c | b | b",
					() -> new Song(each).changeUndoRedo(false)));
			tests.add(reads(each.name + " to the end", "a | c | c | c / a | a | b | b / a | c | c | c",
					() -> new Song(each).changeUndoRedo(true)));
		}
		return tests;
	}

	@TestFactory
	public List<DynamicTest> anEditCannotBeRedoneBeforeItIsUndone() {
		List<DynamicTest> tests = new ArrayList<DynamicTest>();
		for (Setting each : SETTINGS) {
			tests.add(DynamicTest.dynamicTest(each.name, () -> {
				TGUndoableEdit edit = new Song(each).change(false);
				assertThrows(TGCannotRedoException.class, () -> edit.redo(null));
			}));
		}
		return tests;
	}

	@TestFactory
	public List<DynamicTest> anEditCannotBeUndoneTwice() {
		List<DynamicTest> tests = new ArrayList<DynamicTest>();
		for (Setting each : SETTINGS) {
			tests.add(DynamicTest.dynamicTest(each.name, () -> {
				Song song = new Song(each);
				TGUndoableEdit edit = song.change(false);
				song.fixture.undo();
				assertThrows(TGCannotUndoException.class, () -> edit.undo(null));
			}));
		}
		return tests;
	}
}
