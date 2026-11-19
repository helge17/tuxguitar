package app.tuxguitar.io.tef;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

public class TGSongAdjuster {

	protected TGSong song;
	protected TGSongManager manager;

	public TGSongAdjuster(TGSongManager manager, TGSong song) {
		this.manager = manager;
		this.song = song;
	}

	public TGSong process() {
		Iterator<TGTrack> tracks = this.song.getTracks();
		while (tracks.hasNext()) {
			TGTrack track = tracks.next();
			Iterator<TGMeasure> measures = track.getMeasures();
			while (measures.hasNext()) {
				TGMeasure measure = measures.next();
				this.process(measure);
			}
		}
		return this.song;
	}

	public void process(TGMeasure measure) {
		this.manager.getMeasureManager().orderBeats(measure);
		this.adjustBeats(measure);
		this.autoCompleteSilences(measure);
	}

	public void adjustBeats(TGMeasure measure) {
		TGBeat previous = null;
		boolean finish = true;

		long measureStart = measure.getStart();
		long measureEnd = (measureStart + measure.getLength());
		for (int i = 0; i < measure.countBeats(); i++) {
			TGBeat beat = measure.getBeat(i);
			long beatStart = beat.getStart();
			long beatLength = beat.getVoice(0).getDuration().getTime();
			if (previous != null) {
				long previousStart = previous.getStart();
				long previousLength = previous.getVoice(0).getDuration().getTime();

				// check for a chord in a rest beat
				if (beat.getVoice(0).isRestVoice() && beat.isChordBeat()) {
					TGBeat candidate = null;
					TGBeat next = this.manager.getMeasureManager().getFirstBeat(measure.getBeats());
					while (next != null) {
						if (candidate != null && next.getStart() > beat.getStart()) {
							break;
						}
						if (!next.getVoice(0).isRestVoice() && !next.isChordBeat()) {
							candidate = next;
						}
						next = this.manager.getMeasureManager().getNextBeat(measure.getBeats(), next);
					}
					if (candidate != null) {
						candidate.setChord(beat.getChord());
					}
					measure.removeBeat(beat);
					finish = false;
					break;
				}

				// check the duration
				if (previousStart < beatStart && (previousStart + previousLength) > beatStart) {
					// overlap between current and previous
					// assuming current beat position is correct: shorten previous one
					try {
						// there's no reason for this operation to fail, since TablEdit only manipulates
						// "regular" duration (no 7-triplet of double-dotted 32nd)
						previous.getVoice(0).getDuration()
								.setPreciseValue(TGDuration.toPreciseTime(beatStart - previousStart));
					} catch (IllegalArgumentException e) {
						// but we never know, in case of failure it's better to lose a few notes than
						// the complete song
						this.manager.getMeasureManager().cleanVoiceNotes(previous.getVoice(0));
					}
				}
			}
			if ((beatStart + beatLength) > measureEnd) {
				if (beat.getVoice(0).isRestVoice()) {
					measure.removeBeat(beat);
					finish = false;
					break;
				}
				TGDuration duration = TGDuration.fromTime(this.manager.getFactory(), (measureEnd - beatStart));
				beat.getVoice(0).getDuration().copyFrom(duration);
			}
			previous = beat;
		}
		if (!finish) {
			adjustBeats(measure);
		}
	}

	// not using the standard method from TGMeasureManager, as this simplified
	// version can give better results
	// e.g. (1*half + 1*1/8) instead of (5*1/8)
	// it should be possible to switch to standard method when it merges and aligns
	// rests in a better way
	private void autoCompleteSilences(TGMeasure measure) {
		TGMeasureManager measureManager = this.manager.getMeasureManager();
		if (measure.getBeats().isEmpty()) {
			measureManager.autoCompleteSilences(measure);
			return;
		}

		TGBeat firstBeat = measure.getBeats().get(0);
		// no voice: fill measure and return
		if (firstBeat.getVoice(0) == null) {
			measureManager.autoCompleteSilences(measure);
			return;
		}

		// complete silences before first note
		List<TGBeat> newBeats = this.createSilences(measure.getPreciseStart(), firstBeat.getPreciseStart());

		TGBeat previousBeat = null;
		for (TGBeat beat : measure.getBeats()) {
			if (previousBeat != null) {
				newBeats.addAll(this.createSilences(
						previousBeat.getPreciseStart() + previousBeat.getVoice(0).getDuration().getPreciseTime(),
						beat.getPreciseStart()));
			}
			previousBeat = beat;
		}
		for (TGBeat newBeat : newBeats) {
			this.manager.getMeasureManager().addBeat(measure, newBeat);
		}

	}

	private List<TGBeat> createSilences(long from, long to) {
		List<TGBeat> newBeats = new ArrayList<TGBeat>();
		if (to <= from) {
			return newBeats;
		}
		long preciseStart = from;
		List<TGDuration> durations = TGDuration.splitPreciseDurationApproximately(to - from,
				TGDuration.WHOLE_PRECISE_DURATION, this.manager.getFactory(), null, 3);
		for (TGDuration duration : durations) {
			TGBeat beat = this.manager.getFactory().newBeat();
			beat.setPreciseStart(preciseStart);
			beat.getVoice(0).setEmpty(false);
			beat.getVoice(0).getDuration().copyFrom(duration);
			newBeats.add(beat);
			preciseStart += duration.getPreciseTime();
		}
		return newBeats;
	}
}
