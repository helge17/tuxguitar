package app.tuxguitar.editor.action.measure;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

public class TGRemoveUnusedVoiceAction extends TGActionBase {

	public static final String NAME = "action.measure.general.remove-unused-voice";

	public TGRemoveUnusedVoiceAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGBeatRange beatRange = (TGBeatRange) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		if ((beatRange != null) && !beatRange.isEmpty()) {
			int nbVoices = TGBeat.MAX_VOICES;
			// looking for an unused voice in all measures
			boolean[] unused = new boolean[nbVoices];
			for (int voiceIndex=0; voiceIndex<nbVoices; voiceIndex++) {
				unused[voiceIndex] = true;
			}
			for (TGBeat beat : beatRange.getBeats()) {
				for (int voiceIndex=0; voiceIndex<nbVoices; voiceIndex++) {
					TGVoice voice = beat.getVoice(voiceIndex);
					unused[voiceIndex] &= (voice.isEmpty() || voice.isRestVoice());
				}
			}
			// voice to remove: the last unused voice
			int voiceIndexToRemove = -1;
			for (int voiceIndex=0; voiceIndex<nbVoices; voiceIndex++) {
				if (unused[voiceIndex]) voiceIndexToRemove = voiceIndex;
			}
			if (voiceIndexToRemove >= 0) {
				TGMeasureManager measureMgr = getSongManager(context).getMeasureManager();
				List<TGMeasure> measuresDone = new ArrayList<TGMeasure>();
				for (TGBeat beat : beatRange.getBeats()) {
					TGMeasure measure = beat.getMeasure();
					if (!measuresDone.contains(measure)) {
						measureMgr.removeMeasureVoices( measure, voiceIndexToRemove );
						measuresDone.add(measure);
					}
				}
			}
		}
	}
}
