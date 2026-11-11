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
		List<TGBeat> beats = this.findBeats(context);
		int voiceIndexToRemove = this.findLastUnusedVoice(beats);
		if (voiceIndexToRemove >= 0) {
			TGMeasureManager measureMgr = getSongManager(context).getMeasureManager();
			List<TGMeasure> measuresDone = new ArrayList<TGMeasure>();
			for (TGBeat beat : beats) {
				TGMeasure measure = beat.getMeasure();
				if (!measuresDone.contains(measure)) {
					measureMgr.removeMeasureVoices( measure, voiceIndexToRemove );
					measuresDone.add(measure);
				}
			}
		}
	}

	// the selected beats, else the current beat
	private List<TGBeat> findBeats(TGActionContext context){
		TGBeatRange beatRange = (TGBeatRange) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		if ((beatRange != null) && !beatRange.isEmpty()) {
			return beatRange.getBeats();
		}
		List<TGBeat> beats = new ArrayList<TGBeat>();
		beats.add((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
		return beats;
	}

	// voice to remove: the last voice unused in all beats, -1 if none (or no beats)
	private int findLastUnusedVoice(List<TGBeat> beats){
		if (!beats.isEmpty()) {
			for (int voiceIndex = TGBeat.MAX_VOICES - 1; voiceIndex >= 0; voiceIndex--) {
				if (isUnused(beats, voiceIndex)) {
					return voiceIndex;
				}
			}
		}
		return -1;
	}

	private static boolean isUnused(List<TGBeat> beats, int voiceIndex){
		for (TGBeat beat : beats) {
			TGVoice voice = beat.getVoice(voiceIndex);
			if (!voice.isEmpty() && !voice.isRestVoice()) {
				return false;
			}
		}
		return true;
	}
}
