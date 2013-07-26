package org.herac.tuxguitar.song.helpers;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;

public class TGTrackSegment {
	private int track;
	private List measures;
	
	public TGTrackSegment(int track,List measures){
		this.track = track;
		this.measures = measures;
	}
	
	public List getMeasures() {
		return this.measures;
	}
	
	public int getTrack() {
		return this.track;
	}
	
	public Object clone(TGFactory factory,List headers){
		List measures = new ArrayList();
		for(int i = 0;i < getMeasures().size();i++){
			TGMeasure measure = (TGMeasure)getMeasures().get(i);
			measures.add(measure.clone(factory,(TGMeasureHeader)headers.get(i)));
		}
		return new TGTrackSegment(getTrack(),measures);
	}
}
