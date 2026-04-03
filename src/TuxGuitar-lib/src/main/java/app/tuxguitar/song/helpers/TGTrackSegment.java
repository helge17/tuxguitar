package app.tuxguitar.song.helpers;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;

public class TGTrackSegment {
	private int track;
	private List<TGMeasure> measures;
	private List<Integer> stringValues;
	private boolean isPercussionTrack;

	public TGTrackSegment(int track, List<TGMeasure> measures, List<Integer> stringValues, boolean isPercussionTrack){
		this.track = track;
		this.measures = measures;
		this.stringValues = new ArrayList<Integer>(stringValues);
		this.isPercussionTrack = isPercussionTrack;
	}

	public List<TGMeasure> getMeasures() {
		return this.measures;
	}

	public int getTrack() {
		return this.track;
	}

	public List<Integer> getStringValues() {
		return this.stringValues;
	}

	public boolean isPercussionTrack() {
		return this.isPercussionTrack;
	}

	public Object clone(TGFactory factory,List<TGMeasureHeader> headers){
		List<TGMeasure> measures = new ArrayList<TGMeasure>();
		for(int i = 0;i < getMeasures().size();i++){
			TGMeasure measure = getMeasures().get(i);
			measures.add(measure.clone(factory, headers.get(i)));
		}
		return new TGTrackSegment(getTrack(), measures, getStringValues(), isPercussionTrack());
	}
}
