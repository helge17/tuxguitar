package org.herac.tuxguitar.song.helpers;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;

public class TGTrackSegment {
	private int track;
	private List<TGMeasure> measures;
	
	public TGTrackSegment(int track, List<TGMeasure> measures){
		this.track = track;
		this.measures = measures;
	}
	
	public List<TGMeasure> getMeasures() {
		return this.measures;
	}
	
	public int getTrack() {
		return this.track;
	}
	
	public Object clone(TGFactory factory,List<TGMeasureHeader> headers){
		List<TGMeasure> measures = new ArrayList<TGMeasure>();
		for(int i = 0;i < getMeasures().size();i++){
			TGMeasure measure = getMeasures().get(i);
			measures.add(measure.clone(factory, headers.get(i)));
		}
		return new TGTrackSegment(getTrack(),measures);
	}
}
