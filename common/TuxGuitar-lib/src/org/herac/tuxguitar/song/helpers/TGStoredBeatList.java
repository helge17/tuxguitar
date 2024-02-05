package org.herac.tuxguitar.song.helpers;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGString;

import java.util.ArrayList;
import java.util.List;

public class TGStoredBeatList {
	private List<TGBeat> beats;
	private long length = 0;
	private List<Integer> stringValues;
	private boolean isPercussionTrack;
	
	public TGStoredBeatList(List<TGBeat> range, List<TGString> strings, boolean isPercussionTrack, TGFactory factory) {
		this.beats = new ArrayList<>();
		long first = -1;
		for (TGBeat beat : range) {
			if (first == -1) {
				first = beat.getStart();
			}
			beat = beat.clone(factory);
			beat.setStart(beat.getStart() - first);
			beat.setMeasure(null);
			this.length += beat.getVoice(0).getDuration().getTime();
			this.beats.add(beat);
		}
		this.stringValues = new ArrayList<Integer>();
		if (strings!=null) {
			for (TGString string : strings) {
				this.stringValues.add(string.getValue());
			}
		}
		this.isPercussionTrack = isPercussionTrack;
	}
	
	public List<TGBeat> getBeats() {
		return beats;
	}
	public long getLength() {
		return length;
	}
	
	public List<Integer> getStringValues() {
		return stringValues;
	}
	
	public boolean isPercussionTrack() {
		return isPercussionTrack;
	}
	
	public TGStoredBeatList clone(TGFactory factory) {
		TGStoredBeatList clone = new TGStoredBeatList(this.beats, null, this.isPercussionTrack, factory);
		clone.stringValues = new ArrayList<Integer>(this.stringValues);
		return clone;
	}
}
