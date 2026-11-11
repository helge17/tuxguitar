package app.tuxguitar.song.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

public class TGSongSegmentHelper {

	private TGSongManager sm;

	public TGSongSegmentHelper(TGSongManager sm){
		this.sm = sm;
	}

	public TGSongSegment copyMeasures(TGSong song, int m1, int m2){
		return copyMeasures(song, m1, m2, song.getTracks());
	}

	public TGSongSegment copyMeasures(TGSong song, int m1, int m2, TGTrack track){
		return copyMeasures(song, m1, m2, Collections.singletonList(track).iterator());
	}

	private TGSongSegment copyMeasures(TGSong song, int m1, int m2, Iterator<TGTrack> tracks){
		TGSongSegment segment = new TGSongSegment();
		int number1 = Math.max(1,m1);
		int number2 = Math.min(song.countMeasureHeaders(),m2);
		for(int number = number1; number <= number2;number ++){
			segment.getHeaders().add( this.sm.getMeasureHeader(song, number) );
		}
		while(tracks.hasNext()){
			TGTrack track = tracks.next();
			List<TGMeasure> measures = new ArrayList<TGMeasure>();
			for(int number = number1; number <= number2;number ++){
				measures.add(this.sm.getTrackManager().getMeasure(track, number));
			}
			segment.addTrack(track.getNumber(),measures, track.getStrings(), sm.isPercussionChannel(song, track.getChannelId()));
		}
		return segment.clone(this.sm.getFactory());
	}

	public void insertMeasures(TGSong song, TGSongSegment segment,int fromNumber,long move, int track){
		List<TGMeasureHeader> headers = segment.getHeaders();
		this.sm.moveMeasureHeaders(song, headers,move,0,false);
		renumberHeaders(headers, fromNumber);
		makeRoomFor(song, headers);
		for(TGMeasureHeader header : headers){
			this.sm.addMeasureHeader(song, header.getNumber() - 1,header);
		}

		Iterator<TGTrack> tracksIt = song.getTracks();
		while (tracksIt.hasNext()) {
			TGTrack currTrack = tracksIt.next();
			List<TGMeasure> measures = measuresFor(song, segment, currTrack, track, fromNumber);
			placeMeasures(measures, headers, move);
			insertMeasures(currTrack,measures);
		}
	}

	private static void renumberHeaders(List<TGMeasureHeader> headers, int fromNumber){
		for(int i = 0; i < headers.size(); i ++){
			headers.get(i).setNumber(fromNumber + i);
		}
	}

	// moves the song's measures from the first header's start to after the last header
	private void makeRoomFor(TGSong song, List<TGMeasureHeader> headers){
		TGMeasureHeader first = headers.get(0);
		TGMeasureHeader last = headers.get(headers.size() - 1);
		long start = first.getStart();
		long end = (last.getStart() + last.getLength());
		this.sm.moveMeasureHeaders(song, this.sm.getMeasureHeadersBeforeEnd(song, start),end - start,headers.size(),true);
	}

	// the segment's measures for the track, else empty ones (no segment track for it, or one of another kind)
	private List<TGMeasure> measuresFor(TGSong song, TGSongSegment segment, TGTrack track, int toTrack, int fromNumber){
		TGTrackSegment tSegment = segmentOf(segment, track.getNumber(), toTrack);
		if (tSegment == null || tSegment.isPercussionTrack() != this.sm.isPercussionChannel(song, track.getChannelId())) {
			return getEmptyMeasures(track, fromNumber, segment.getHeaders().size());
		}
		this.sm.getTrackManager().allocateMeasureNotesToStrings(tSegment.getStringValues(), tSegment.getMeasures(), track.getStrings(), track.getMaxFret());
		return tSegment.getMeasures();
	}

	// pasting a single track segment to a track (toTrack > 0) gives it to that track, else each one goes to its own
	private static TGTrackSegment segmentOf(TGSongSegment segment, int trackNumber, int toTrack){
		boolean toOneTrack = (toTrack > 0 && segment.getTracks().size() == 1);
		for(TGTrackSegment tSegment : segment.getTracks()){
			if( (toOneTrack ? toTrack : tSegment.getTrack()) == trackNumber ){
				return tSegment;
			}
		}
		return null;
	}

	// empty measures in the clef and key signature of the measure before the inserted ones, or after them at the song start
	private List<TGMeasure> getEmptyMeasures(TGTrack track, int fromNumber, int count){
		TGMeasure measure = this.sm.getTrackManager().getMeasure(track, (fromNumber > 1 ? (fromNumber - 1) : (fromNumber + count)));
		if( measure == null ){
			return getEmptyMeasures(count, TGMeasure.DEFAULT_CLEF, TGMeasure.DEFAULT_KEY_SIGNATURE);
		}
		return getEmptyMeasures(count, measure.getClef(), measure.getKeySignature());
	}

	private void placeMeasures(List<TGMeasure> measures, List<TGMeasureHeader> headers, long move){
		for(int i = 0;i < measures.size();i++){
			TGMeasure measure = measures.get(i);
			measure.setHeader(headers.get(i));
			this.sm.getMeasureManager().moveAllBeats(measure,move);
		}
	}

	public void insertMeasures(TGTrack track,List<TGMeasure> measures){
		if(!measures.isEmpty()){
			Iterator<TGMeasure> it = measures.iterator();
			while(it.hasNext()){
				TGMeasure measure = it.next();
				this.sm.getMeasureManager().removeNotesAfterString(measure, track.stringCount());
				this.sm.getTrackManager().addMeasure(track,(measure.getNumber() - 1),measure);
			}
		}
	}

	public void replaceMeasures(TGSong song, TGSongSegment segment,long move, int track) {
		List<TGMeasureHeader> measureHeaders = replaceHeaders(song, segment.getHeaders(), move, ( track == 0 || song.countTracks() == 1 ));
		boolean toOneTrack = (track > 0 && segment.getTracks().size() == 1);
		for(TGTrackSegment tSegment : segment.getTracks()){
			TGTrack currTrack = this.sm.getTrack(song, (toOneTrack ? track : tSegment.getTrack()));
			if(currTrack != null){
				replaceTrackMeasures(song, tSegment, currTrack, measureHeaders, move);
			}
		}
	}

	// moves the headers onto the song headers at their start, which take them when asked to; returns those song headers
	private List<TGMeasureHeader> replaceHeaders(TGSong song, List<TGMeasureHeader> headers, long move, boolean replaceHeader){
		List<TGMeasureHeader> measureHeaders = new ArrayList<TGMeasureHeader>();
		this.sm.moveMeasureHeaders(song, headers,move,0,false);
		for(TGMeasureHeader header : headers){
			TGMeasureHeader replace = (replaceHeader ? this.sm.replaceMeasureHeader(song, header) : this.sm.getMeasureHeaderAt(song, header.getStart()));
			moveHeadersAfter(song, replace);
			measureHeaders.add(replace);
		}
		return measureHeaders;
	}

	// the headers after one follow it without gap
	private void moveHeadersAfter(TGSong song, TGMeasureHeader header){
		long nextStart =  (header.getStart() + header.getLength());
		for(TGMeasureHeader next : this.sm.getMeasureHeadersAfter(song, header.getNumber())){
			long move = (nextStart - next.getStart());
			this.sm.moveMeasureHeader(next, move, 0);
			this.sm.moveMeasureComponents(song, next, move);
			nextStart = (next.getStart() + next.getLength());
		}
	}

	// the track's measures take the segment's ones, unless they are of another kind (percussion or not)
	private void replaceTrackMeasures(TGSong song, TGTrackSegment tSegment, TGTrack track, List<TGMeasureHeader> headers, long move){
		boolean sameKind = (tSegment.isPercussionTrack() == this.sm.isPercussionChannel(song, track.getChannelId()));
		placeMeasures(tSegment.getMeasures(), headers, move);
		for(TGMeasure measure : tSegment.getMeasures()){
			this.sm.getMeasureManager().removeVoicesOutOfTime(measure);
			if (sameKind) {
				this.sm.getTrackManager().allocateMeasureNotesToStrings(tSegment.getStringValues(), measure, track.getStrings(), track.getMaxFret());
				this.sm.getTrackManager().replaceMeasure(track,measure);
			}
		}
	}

	private List<TGMeasure> getEmptyMeasures(int count, int clef, int keySignature) {
		List<TGMeasure> measures = new ArrayList<TGMeasure>();
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

		TGMeasureHeader fMeasure = segment.getHeaders().get( 0 );
		TGMeasureHeader lMeasure = segment.getHeaders().get( mCount - 1 );

		long mMove = ( ( lMeasure.getStart() + lMeasure.getLength() ) - fMeasure.getStart() );
		for( int i = 1 ; i < count ; i ++ ){
			for(int m = 0; m < mCount; m++ ){
				TGMeasureHeader header = segment.getHeaders().get(m).clone( this.sm.getFactory() );
				segment.getHeaders().add( header );
				this.sm.moveMeasureHeader(header,(mMove * i),(mCount * i));
				for( int t = 0 ; t < tCount ; t ++ ){
					TGTrackSegment track = segment.getTracks().get( t );
					TGMeasure measure = track.getMeasures().get( m ).clone(this.sm.getFactory(), header );
					track.getMeasures().add( measure );
					this.sm.getMeasureManager().moveAllBeats(measure, (mMove * i));
				}
			}
		}

		return segment;
	}
}
