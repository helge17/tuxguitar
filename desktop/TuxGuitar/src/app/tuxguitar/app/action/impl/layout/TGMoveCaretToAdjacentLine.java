package app.tuxguitar.app.action.impl.layout;

import java.util.Iterator;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.caret.TGMoveToAction;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
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

	public static void process(TGContext context, TGActionContext actionContext, int direction) {
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

		float currentY = currentMeasure.getPosY();
		Float targetY = null;
		Iterator<TGMeasure> measures = track.getMeasures();
		while (measures.hasNext()) {
			float measureY = ((TGMeasureImpl) measures.next()).getPosY();
			if ((direction < 0 && measureY < currentY
					&& (targetY == null || measureY > targetY.floatValue()))
					|| (direction > 0 && measureY > currentY
					&& (targetY == null || measureY < targetY.floatValue()))) {
				targetY = measureY;
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
