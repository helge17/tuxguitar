package app.tuxguitar.app.action.impl.layout;

import java.util.Iterator;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.caret.TGMoveToAction;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.component.tab.TGControl;
import app.tuxguitar.app.view.component.tabfolder.TGTabFolder;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.graphics.control.TGBeatImpl;
import app.tuxguitar.graphics.control.TGMeasureImpl;
import app.tuxguitar.graphics.control.TGTrackImpl;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGContext;

/**
 * Moves the caret between score lines while preserving its approximate horizontal
 * and musical position. A score line is identified by the shared {@code posY} of
 * its measures; the search is deliberately restricted to the current track so a
 * multi-track layout cannot redirect the caret to another track.
 */
final class TGMoveCaretToAdjacentLine {

	private TGMoveCaretToAdjacentLine() {
		super();
	}

	/** Moves to the immediately adjacent score line. */
	public static void processLine(TGContext context, TGActionContext actionContext, int direction) {
		process(context, actionContext, direction, 0f);
	}

	/**
	 * Moves approximately one visible page. The actual canvas height is used instead
	 * of a fixed number of lines because score lines may have different heights.
	 */
	public static void processPage(TGContext context, TGActionContext actionContext, int direction) {
		TGControl control = TGTabFolder.getInstance(context).findSelectedControl();
		if (control != null && !control.isDisposed()) {
			process(context, actionContext, direction, control.getCanvas().getBounds().getHeight());
		}
	}

	private static void process(TGContext context, TGActionContext actionContext, int direction,
			float verticalDistance) {
		if (direction == 0) {
			return;
		}

		Tablature tablature = TablatureEditor.getInstance(context).getTablature();
		Caret caret = tablature.getCaret();
		TGMeasureImpl currentMeasure = caret.getMeasure();
		TGBeatImpl currentBeat = caret.getSelectedBeat();
		TGTrackImpl track = caret.getTrack();
		if (currentMeasure == null || currentBeat == null || track == null) {
			return;
		}
		TGControl pageControl = null;
		if (verticalDistance > 0f) {
			pageControl = TGTabFolder.getInstance(context).findSelectedControl();
			if (pageControl == null || pageControl.isDisposed()) {
				return;
			}
		}

		float currentY = currentMeasure.getPosY();
		Float targetY = null;
		float expectedY = currentY + (direction * verticalDistance);
		float bestYDistance = Float.MAX_VALUE;
		boolean targetWithinPage = false;

		/*
		 * For line movement, choose the nearest distinct Y coordinate. For page
		 * movement, prefer the line closest to the page boundary without crossing it.
		 * This keeps one previously visible line as orientation. If no line fits within
		 * the page (for example, an unusually tall line), the nearest line beyond the
		 * boundary is used so the action still makes progress.
		 */
		Iterator<TGMeasure> measures = track.getMeasures();
		while (measures.hasNext()) {
			float measureY = ((TGMeasureImpl) measures.next()).getPosY();
			/* Ignore the current line and every line opposite to the requested direction. */
			if ((direction < 0 && measureY < currentY) || (direction > 0 && measureY > currentY)) {
				/*
				 * Every directional candidate is valid for a one-line move. For a page
				 * move, a candidate is "within" the page if it has not crossed the
				 * ideal page boundary: not above expectedY when moving up, and not
				 * below expectedY when moving down.
				 */
				boolean withinPage = (verticalDistance == 0f
						|| (direction < 0 && measureY >= expectedY)
						|| (direction > 0 && measureY <= expectedY));
				/* Line mode compares against the current line; page mode compares
				 * against the ideal page boundary. */
				float distance = (verticalDistance > 0f
						? Math.abs(expectedY - measureY)
						: Math.abs(currentY - measureY));
				/*
				 * Prefer any candidate inside the page over one beyond its boundary.
				 * Within the same category, retain the candidate nearest to the
				 * relevant reference position. This selects the adjacent line in line
				 * mode and the last line before the boundary in page mode.
				 */
				if ((withinPage && !targetWithinPage)
						|| (withinPage == targetWithinPage && distance < bestYDistance)) {
					bestYDistance = distance;
					targetY = measureY;
					targetWithinPage = withinPage;
				}
			}
		}
		if (targetY == null) {
			return;
		}

		float currentMeasureX = currentMeasure.getPosX();
		TGMeasureImpl targetMeasure = null;
		float bestDistance = Float.MAX_VALUE;

		/* Preserve the visual column by choosing the measure whose X position most
		 * closely matches that of the current measure. */
		measures = track.getMeasures();
		while (measures.hasNext()) {
			TGMeasureImpl measure = (TGMeasureImpl) measures.next();
			if (measure.getPosY() == targetY.floatValue()) {
				float distance = Math.abs(currentMeasureX - measure.getPosX());
				if (distance < bestDistance) {
					bestDistance = distance;
					targetMeasure = measure;
				}
			}
		}

		TGBeat targetBeat = findCorrespondingBeat(currentMeasure, currentBeat,
				targetMeasure, caret.getVoice());
		if (targetBeat != null) {
			actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
			actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, targetMeasure);
			actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, targetBeat);
			actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, caret.getSelectedString());
			if (pageControl != null) {
				/*
				 * Register the scroll before TGMoveToAction. Its repaint may be synchronous;
				 * TGControl must therefore consume this one-shot offset instead of applying
				 * its normal jumpTo() afterwards, which would scroll the page a second time.
				 */
				pageControl.requestCaretVerticalScroll(
						Math.round(targetY.floatValue() - currentY));
			}
			TGActionManager.getInstance(context).execute(TGMoveToAction.NAME, actionContext);
		}
	}

	private static TGBeat findCorrespondingBeat(TGMeasureImpl currentMeasure, TGBeat currentBeat,
			TGMeasureImpl targetMeasure, int voice) {
		if (targetMeasure == null || targetMeasure.getBeats().isEmpty()) {
			return null;
		}

		/*
		 * Map the beat's relative position rather than its absolute time. This retains
		 * the musical column when source and target measures have different lengths or
		 * time signatures.
		 */
		double relativePosition = ((double) (currentBeat.getStart() - currentMeasure.getStart())
				/ Math.max(1L, currentMeasure.getLength()));
		long targetPosition = targetMeasure.getStart()
				+ Math.round(relativePosition * targetMeasure.getLength());
		/* Prefer content in the selected voice, but fall back to any beat so movement
		 * also works through measures where that voice is empty. */
		TGBeat targetBeat = findClosestBeat(targetMeasure, targetPosition, voice, true);
		return (targetBeat != null ? targetBeat
				: findClosestBeat(targetMeasure, targetPosition, voice, false));
	}

	private static TGBeat findClosestBeat(TGMeasureImpl measure, long position, int voice,
			boolean requireVoiceContent) {
		TGBeat targetBeat = null;
		long bestDistance = Long.MAX_VALUE;
		Iterator<TGBeat> beats = measure.getBeats().iterator();
		while (beats.hasNext()) {
			TGBeat beat = beats.next();
			if (!requireVoiceContent || !beat.getVoice(voice).isEmpty()) {
				long distance = Math.abs(position - beat.getStart());
				if (distance < bestDistance) {
					bestDistance = distance;
					targetBeat = beat;
				}
			}
		}
		return targetBeat;
	}
}
