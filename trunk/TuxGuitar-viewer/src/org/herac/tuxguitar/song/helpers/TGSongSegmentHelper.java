package org.herac.tuxguitar.song.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.managers.TGTrackManager;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGTrack;

public class TGSongSegmentHelper {
	
	private TGSongManager sm;
	
	public TGSongSegmentHelper(TGSongManager sm){
		this.sm = sm;
	}
	
	public TGSongSegment copyMeasures(int m1, int m2){
		TGSongSegment segment = new TGSongSegment();
		int number1 = Math.max(1,m1);
		int number2 = Math.min(this.sm.getSong().countMeasureHeaders(),m2);
		for(int number = number1; number <= number2;number ++){
			segment.getHeaders().add( this.sm.getMeasureHeader(number) );
		}
		Iterator it = this.sm.getSong().getTracks();
		while(it.hasNext()){
			TGTrack track = (TGTrack)it.next();
			List measures = new ArrayList();
			for(int number = number1; number <= number2;number ++){
				measures.add(this.sm.getTrackManager().getMeasure(track, number));
			}
			segment.addTrack(track.getNumber(),measures);
		}
		return segment.clone(this.sm.getFactory());
	}
	
	public TGSongSegment copyMeasures(int m1, int m2,TGTrack track){
		TGSongSegment segment = new TGSongSegment();
		List measures = new ArrayList();
		int number1 = Math.max(1,m1);
		int number2 = Math.min(this.sm.getSong().countMeasureHeaders(),m2);
		for(int number = number1; number <= number2;number ++){
			segment.getHeaders().add( this.sm.getMeasureHeader(number) );
			measures.add(this.sm.getTrackManager().getMeasure(track, number));
		}
		segment.addTrack(track.getNumber(),measures);
		return segment.clone(this.sm.getFactory());
	}
	
	public void insertMeasures(TGSongSegment segment,int fromNumber,long move, int track){
		List headers = new ArrayList();
		this.sm.moveMeasureHeaders(segment.getHeaders(),move,0,false);
		
		int headerNumber = fromNumber;
		Iterator it = segment.getHeaders().iterator();
		while(it.hasNext()){
			TGMeasureHeader header = (TGMeasureHeader)it.next();
			header.setNumber(headerNumber);
			headers.add(header);
			headerNumber ++;
		}
		long start = ((TGMeasureHeader)headers.get(0)).getStart();
		long end = ((TGMeasureHeader)headers.get(headers.size() - 1)).getStart() + ((TGMeasureHeader)headers.get(headers.size() - 1)).getLength();
		List headersBeforeEnd = this.sm.getMeasureHeadersBeforeEnd(start);
		this.sm.moveMeasureHeaders(headersBeforeEnd,end - start,headers.size(),true);
		
		it = segment.getHeaders().iterator();
		while(it.hasNext()){
			TGMeasureHeader header = (TGMeasureHeader)it.next();
			this.sm.addMeasureHeader(header.getNumber() - 1,header);
		}
		
		it = this.sm.getSong().getTracks();
		while (it.hasNext()) {
			TGTrack currTrack = (TGTrack) it.next();
			List measures = null;
			
			Iterator tracks = segment.getTracks().iterator();
			while(tracks.hasNext()){
				TGTrackSegment tSegment = (TGTrackSegment)tracks.next();
				
				if(  ((track > 0 && segment.getTracks().size() == 1)?track:tSegment.getTrack()) == currTrack.getNumber()){
					measures = tSegment.getMeasures();
					break;
				}
			}
			if(measures == null){
				TGTrackManager tm = this.sm.getTrackManager();
				TGMeasure measure = (fromNumber > 1 ? tm.getMeasure(currTrack , (fromNumber - 1) ) : tm.getMeasure(currTrack, headerNumber ));
				int clef = ( measure != null ? measure.getClef() : TGMeasure.DEFAULT_CLEF );
				int keySignature = ( measure != null ? measure.getKeySignature() : TGMeasure.DEFAULT_KEY_SIGNATURE );
				measures = getEmptyMeasures(segment.getHeaders().size(), clef, keySignature);
			}
			
			for(int i = 0;i < measures.size();i++){
				TGMeasure measure = (TGMeasure)measures.get(i);
				measure.setHeader((TGMeasureHeader)headers.get(i));
				this.sm.getMeasureManager().moveAllBeats(measure,move);
			}
			
			insertMeasures(currTrack,measures);
		}
	}
	
	public void insertMeasures(TGTrack track,List measures){
		if(!measures.isEmpty()){
			Iterator it = measures.iterator();
			while(it.hasNext()){
				TGMeasure measure = (TGMeasure)it.next();
				this.sm.getMeasureManager().removeNotesAfterString(measure, track.stringCount());
				this.sm.getTrackManager().addMeasure(track,(measure.getNumber() - 1),measure);
			}
		}
	}
	
	public void replaceMeasures(TGSongSegment segment,long move, int track) {
		boolean replaceHeader = ( track == 0 || this.sm.getSong().countTracks() == 1 );
		
		List measureHeaders = new ArrayList();
		this.sm.moveMeasureHeaders(segment.getHeaders(),move,0,false);
		Iterator sHeaders = segment.getHeaders().iterator();
		while(sHeaders.hasNext()){
			TGMeasureHeader header = (TGMeasureHeader)sHeaders.next();
			TGMeasureHeader replace = (replaceHeader ? this.sm.replaceMeasureHeader(header) : this.sm.getMeasureHeaderAt(header.getStart()));
			
			Iterator nextHeaders = this.sm.getMeasureHeadersAfter(replace.getNumber()).iterator();
			long nextStart =  (replace.getStart() + replace.getLength());
			while(nextHeaders.hasNext()){
				TGMeasureHeader next = (TGMeasureHeader)nextHeaders.next();
				this.sm.moveMeasureComponents(next, (nextStart - next.getStart() ));
				this.sm.moveMeasureHeader(next, (nextStart - next.getStart() ) , 0);
				nextStart = (next.getStart() + next.getLength());
			}
			measureHeaders.add(replace);
		}
		
		Iterator sTracks = segment.getTracks().iterator();
		while(sTracks.hasNext()){
			TGTrackSegment tSegment = (TGTrackSegment)sTracks.next();
			TGTrack currTrack = this.sm.getTrack( (track > 0 && segment.getTracks().size() == 1)?track:tSegment.getTrack());
			if(currTrack != null){
				for(int i = 0;i < tSegment.getMeasures().size();i++){
					TGMeasure measure = (TGMeasure)tSegment.getMeasures().get(i);
					measure.setHeader((TGMeasureHeader)measureHeaders.get(i));
					//this.sm.getMeasureManager().removeNotesAfterString(measure, currTrack.stringCount());
					//this.sm.getMeasureManager().moveAllBeats(measure,move);
					this.sm.getMeasureManager().moveAllBeats(measure,move);
					this.sm.getMeasureManager().removeVoicesOutOfTime(measure);
					this.sm.getMeasureManager().removeNotesAfterString(measure, currTrack.stringCount());
					this.sm.getTrackManager().replaceMeasure(currTrack,measure);
				}
			}
		}
	}
	
	private List getEmptyMeasures(int count, int clef, int keySignature) {
		List measures = new ArrayList();
		for(int i = 0 ; i < count ; i ++ ){
			TGMeasure measure = this.sm.getFactory().newMeasure(null);
			measure.setClef(clef);
			measure.setKeySignature(keySignature);
			measures.add(measure);
		}
		return measures;
	}
	
	public TGSongSegment createSegmentCopies( TGSongSegment srcSegment , int count ){
		TGSongSegment segment = srcSegment.clone( this.sm.getFactory() );
		
		int mCount = segment.getHeaders().size();
		int tCount = segment.getTracks().size();
		
		TGMeasureHeader fMeasure = (TGMeasureHeader)segment.getHeaders().get( 0 );
		TGMeasureHeader lMeasure = (TGMeasureHeader)segment.getHeaders().get( mCount - 1 );
		
		long mMove = ( ( lMeasure.getStart() + lMeasure.getLength() ) - fMeasure.getStart() );
		for( int i = 1 ; i < count ; i ++ ){
			for(int m = 0; m < mCount; m++ ){
				TGMeasureHeader header = ( (TGMeasureHeader)segment.getHeaders().get(m) ).clone( this.sm.getFactory() );
				segment.getHeaders().add( header );
				this.sm.moveMeasureHeader(header,(mMove * i),(mCount * i));
				for( int t = 0 ; t < tCount ; t ++ ){
					TGTrackSegment track = (TGTrackSegment)segment.getTracks().get( t );
					TGMeasure measure = ((TGMeasure)track.getMeasures().get( m )).clone(this.sm.getFactory(), header );
					track.getMeasures().add( measure );
					this.sm.getMeasureManager().moveAllBeats(measure, (mMove * i));
				}
			}
		}
		
		return segment;
	}
}
