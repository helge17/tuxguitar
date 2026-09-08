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

final class TGMoveCaretToAdjacentLine {

	private TGMoveCaretToAdjacentLine() {
		super();
	}

	public static void processLine(TGContext context, TGActionContext actionContext, int direction) {
		process(context, actionContext, direction, 0f);
	}

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
		Iterator<TGMeasure> measures = track.getMeasures();
		while (measures.hasNext()) {
			float measureY = ((TGMeasureImpl) measures.next()).getPosY();
			if ((direction < 0 && measureY < currentY) || (direction > 0 && measureY > currentY)) {
				boolean withinPage = (verticalDistance == 0f
						|| (direction < 0 && measureY >= expectedY)
						|| (direction > 0 && measureY <= expectedY));
				float distance = (verticalDistance > 0f
						? Math.abs(expectedY - measureY)
						: Math.abs(currentY - measureY));
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

		double relativePosition = ((double) (currentBeat.getStart() - currentMeasure.getStart())
				/ Math.max(1L, currentMeasure.getLength()));
		long targetPosition = targetMeasure.getStart()
				+ Math.round(relativePosition * targetMeasure.getLength());
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
