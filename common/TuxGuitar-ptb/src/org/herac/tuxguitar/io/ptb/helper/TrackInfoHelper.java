package org.herac.tuxguitar.io.ptb.helper;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.io.ptb.base.PTTrackInfo;
import org.herac.tuxguitar.song.models.TGTrack;

public class TrackInfoHelper {
	
	private List<TGTrack> staffTracks = new ArrayList<TGTrack>();
	private PTTrackInfo defaultInfo;
	
	public TrackInfoHelper(){
		this.staffTracks = new ArrayList<TGTrack>();
	}
	
	public void reset(PTTrackInfo defaultInfo){
		this.defaultInfo = defaultInfo;
		this.staffTracks.clear();
	}
	
	public PTTrackInfo getDefaultInfo() {
		return this.defaultInfo;
	}
	
	public TGTrack getStaffTrack(int staff) {
		if(staff >= 0 && staff < this.staffTracks.size() ){
			return (TGTrack)this.staffTracks.get( staff );
		}
		return null;
	}
	
	public int countStaffTracks() {
		return this.staffTracks.size();
	}
	
	public void addStaffTrack(TGTrack track) {
		this.staffTracks.add( track );
	}
	
	public void removeStaffTrack(int staff) {
		this.staffTracks.remove( staff );
	}
}
