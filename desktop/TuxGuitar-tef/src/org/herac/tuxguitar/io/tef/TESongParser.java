package org.herac.tuxguitar.io.tef;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.herac.tuxguitar.io.tef.base.TEChord;
import org.herac.tuxguitar.io.tef.base.TEComponent;
import org.herac.tuxguitar.io.tef.base.TEComponentChord;
import org.herac.tuxguitar.io.tef.base.TEComponentNote;
import org.herac.tuxguitar.io.tef.base.TESong;
import org.herac.tuxguitar.io.tef.base.TETimeSignature;
import org.herac.tuxguitar.io.tef.base.TETrack;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;

public class TESongParser {
	
	private static final int[][] PERCUSSION_TUNINGS = new int[][]{
		new int[]{ 49, 41, 32 },
		new int[]{ 49, 51, 42, 50 },
		new int[]{ 49, 42, 50, 37, 32 },
		new int[]{ 49, 51, 42, 50, 45, 37 },
		new int[]{ 49, 51, 42, 50, 45, 37, 41 },
	};
	
	private TGSongManager manager;
	
	public TESongParser(TGFactory factory) {
		this.manager = new TGSongManager(factory);
	}
	
	public TGSong parseSong(TESong song){
		TGSong tgSong = this.manager.newSong();
		
		this.sortComponents(song);
		this.addTracksAndHeaders(tgSong, song.getTracks().length,song.getMeasures(),song.getTempo().getValue());
		this.addMeasureValues(tgSong, song);
		this.addTrackValues(tgSong, song.getTracks());
		this.addComponents(tgSong, song);
		
		return new TGSongAdjuster(this.manager, tgSong).process();
	}
	
	private void addTracksAndHeaders(TGSong song, int tracks,int measures,int tempo){
		this.manager.getFirstMeasureHeader(song).getTempo().setValue(tempo);
		
		while(song.countTracks() < tracks){
			this.manager.addTrack(song);
		}
		while(song.countMeasureHeaders() < measures){
			this.manager.addNewMeasureBeforeEnd(song);
		}
	}
	
	private void addMeasureValues(TGSong tgSong, TESong song){
		TGTimeSignature timeSignature = this.manager.getFactory().newTimeSignature();
		for(int i = 0; i < tgSong.countMeasureHeaders(); i ++){
			TGMeasureHeader header = tgSong.getMeasureHeader(i);
			TETimeSignature ts = song.getTimeSignature(i);
			timeSignature.setNumerator( ts.getNumerator() );
			timeSignature.getDenominator().setValue( ts.getDenominator() );
			this.manager.changeTimeSignature(tgSong, header, timeSignature,false);
		}
	}
	
	private void addTrackValues(TGSong tgSong, TETrack[] tracks){
		for(int i = 0; i < tracks.length; i ++){
			TGTrack track = tgSong.getTrack(i);
			
			TGChannel tgChannel = this.manager.addChannel(tgSong);
			tgChannel.setVolume((short)((  (15 - tracks[i].getVolume()) * 127) / 15));
			tgChannel.setBalance((short)(( tracks[i].getPan() * 127) / 15));
			tgChannel.setProgram((short)tracks[i].getInstrument());
			tgChannel.setBank( tracks[i].isPercussion() ? TGChannel.DEFAULT_PERCUSSION_BANK : TGChannel.DEFAULT_BANK);
			tgChannel.setName(this.manager.createChannelNameFromProgram(tgSong, tgChannel));
			
			track.setChannelId(tgChannel.getChannelId());
			
			track.getStrings().clear();
			int strings[] = tracks[i].getStrings();
			
			for(int j = 0; j < strings.length;j ++){
				if(j >= 7){
					break;
				}
				TGString string = this.manager.getFactory().newString();
				string.setNumber( (j + 1) );
				string.setValue( (tracks[i].isPercussion() ?0:(96 - strings[j])) );
				track.getStrings().add(string);
			}
		}
	}
	
	private void addComponents(TGSong tgSong, TESong song){
		Iterator<TEComponent> it = song.getComponents().iterator();
		while(it.hasNext()){
			TEComponent component = (TEComponent)it.next();
			
			if(component.getMeasure() >= 0 && component.getMeasure() < tgSong.countMeasureHeaders()){
				int offset = 0;
				TETrack[] tracks = song.getTracks();
				for(int i = 0; i < tracks.length; i ++){
					int strings = tracks[i].getStrings().length;
					int string = (component.getString() - offset);
					if( string >= 0 && string <  strings && string < 7){
						TGTrack tgTrack = tgSong.getTrack(i);
						TGMeasure tgMeasure = tgTrack.getMeasure(component.getMeasure());
						if(component instanceof TEComponentNote){
							addNote(tracks[i], (TEComponentNote)component,string,strings,tgMeasure);
						}
						else if(component instanceof TEComponentChord){
							addChord(song.getChords(),(TEComponentChord)component,tgTrack,tgMeasure);
						}
					}
					offset += strings;
				}
			}
		}
	}
	
	private TGBeat getBeat(TGMeasure measure, long start){
		TGBeat beat = this.manager.getMeasureManager().getBeat(measure, start);
		if(beat == null){
			beat = this.manager.getFactory().newBeat();
			beat.setStart(start);
			measure.addBeat(beat);
		}
		return beat;
	}
	
	private long getStart(TGDuration duration, TGMeasure measure,int position){
		float fixedPosition = position;
		if(duration != null && !duration.getDivision().isEqual(TGDivisionType.NORMAL)){
			fixedPosition = (( fixedPosition - (fixedPosition % 64)) + ((((fixedPosition % 64) * 2) * 2) / 3) );
		}
		long start = ((long) (measure.getStart() + ( (fixedPosition * TGDuration.QUARTER_TIME)  / 64)) );
		
		return start;
	}
	
	private TGDuration getDuration(int duration){
		TGDuration tgDuration = this.manager.getFactory().newDuration();
		
		int value = TGDuration.WHOLE;
		for(int i = 0; i <  ( duration / 3); i ++){
			value = (value * 2);
		}
		if( (duration % 3) == 1){
			value = (value * 2);
			tgDuration.setDotted(true);
		}
		else if( (duration % 3) == 2){
			tgDuration.getDivision().setEnters(3);
			tgDuration.getDivision().setTimes(2);
		}
		tgDuration.setValue(value);
		
		return tgDuration;
	}
	
	private void addNote(TETrack track,TEComponentNote note,int string,int strings,TGMeasure tgMeasure){
		int value = note.getFret();
		if(track.isPercussion() ){
			int tuning = (Math.min( (strings - 2) ,(PERCUSSION_TUNINGS.length )) - 1);
			if(string >= 0 && string < PERCUSSION_TUNINGS[tuning].length){
				value += PERCUSSION_TUNINGS[tuning][string];
			}
		}
		
		TGNote tgNote = this.manager.getFactory().newNote();
		tgNote.setString( string + 1 );
		tgNote.setValue( value );
		
		TGDuration tgDuration = getDuration( note.getDuration() );
		TGBeat tgBeat = getBeat(tgMeasure, getStart(tgDuration, tgMeasure, note.getPosition()));
		tgBeat.getVoice(0).getDuration().copyFrom(tgDuration);
		tgBeat.getVoice(0).addNote(tgNote);
	}
	
	private void addChord(TEChord[] chords,TEComponentChord component,TGTrack tgTrack,TGMeasure tgMeasure){
		if(component.getChord() >= 0 && component.getChord() < chords.length){
			TEChord chord = chords[component.getChord()];
			byte[] strings = chord.getStrings();
			
			TGChord tgChord = this.manager.getFactory().newChord(tgTrack.stringCount());
			tgChord.setName(chord.getName());
			for(int i = 0; i < tgChord.countStrings(); i ++){
				int value = ( ( i < strings.length )?strings[i]:-1 );
				tgChord.addFretValue(i,value);
			}
			if(tgChord.countNotes() > 0){
				TGBeat tgBeat = getBeat(tgMeasure, getStart(null, tgMeasure, component.getPosition()));
				tgBeat.setChord(tgChord);
			}
		}
	}
	
	public void sortComponents(TESong song){
		Collections.sort(song.getComponents(),new Comparator<TEComponent>() {
			public int compare(TEComponent c1, TEComponent c2) {
				if( c1 != null && c2 != null ){
					if ( c1.getMeasure() < c2.getMeasure() ){
						return -1;
					}
					if ( c1.getMeasure() > c2.getMeasure() ){
						return 1;
					}
					if ( c1.getPosition() < c2.getPosition() ){
						return -1;
					}
					if ( c1.getPosition() > c2.getPosition() ){
						return 1;
					}
					if(  ( c1 instanceof TEComponentNote ) && !( c2 instanceof TEComponentNote ) ){
						return -1;
					}
					if(  ( c2 instanceof TEComponentNote ) && !( c1 instanceof TEComponentNote ) ){
						return 1;
					}
				}
				return 0;
			}
		});
	}
}

class TGSongAdjuster{
	
	protected TGSong song;
	protected TGSongManager manager;
	
	public TGSongAdjuster(TGSongManager manager, TGSong song){
		this.manager = manager;
		this.song = song;
	}
	
	public TGSong process(){
		Iterator<TGTrack> tracks = this.song.getTracks();
		while(tracks.hasNext()){
			TGTrack track = (TGTrack)tracks.next();
			Iterator<TGMeasure> measures = track.getMeasures();
			while(measures.hasNext()){
				TGMeasure measure = (TGMeasure)measures.next();
				this.process(measure);
			}
		}
		return this.song;
	}
	
	public void process(TGMeasure measure){
		this.manager.getMeasureManager().orderBeats(measure);
		this.adjustBeats(measure);
	}
	
	public void adjustBeats(TGMeasure measure){
		TGBeat previous = null;
		boolean finish = true;
		
		long measureStart = measure.getStart();
		long measureEnd = (measureStart + measure.getLength());
		for(int i = 0;i < measure.countBeats();i++){
			TGBeat beat = measure.getBeat( i );
			long beatStart = beat.getStart();
			long beatLength = beat.getVoice(0).getDuration().getTime();
			if(previous != null){
				long previousStart = previous.getStart();
				long previousLength = previous.getVoice(0).getDuration().getTime();
				
				// check for a chord in a rest beat
				if( beat.getVoice(0).isRestVoice() && beat.isChordBeat() ){
					TGBeat candidate = null;
					TGBeat next = this.manager.getMeasureManager().getFirstBeat( measure.getBeats() );
					while( next != null ){
						if( candidate != null && next.getStart() > beat.getStart() ){
							break;
						}
						if(! next.getVoice(0).isRestVoice() && !next.isChordBeat() ){
							candidate = next;
						}
						next = this.manager.getMeasureManager().getNextBeat(measure.getBeats(), next);
					}
					if(candidate != null){
						candidate.setChord( beat.getChord() );
					}
					measure.removeBeat(beat);
					finish = false;
					break;
				}
				
				// check the duration
				if(previousStart < beatStart && (previousStart + previousLength) > beatStart){
					if(beat.getVoice(0).isRestVoice()){
						measure.removeBeat(beat);
						finish = false;
						break;
					}
					TGDuration duration = TGDuration.fromTime(this.manager.getFactory(), (beatStart - previousStart) );
					previous.getVoice(0).getDuration().copyFrom( duration );
				}
			}
			if( (beatStart + beatLength) > measureEnd ){
				if(beat.getVoice(0).isRestVoice()){
					measure.removeBeat(beat);
					finish = false;
					break;
				}
				TGDuration duration = TGDuration.fromTime(this.manager.getFactory(), (measureEnd - beatStart) );
				beat.getVoice(0).getDuration().copyFrom( duration );
			}
			previous = beat;
		}
		if(!finish){
			adjustBeats(measure);
		}
	}
}
