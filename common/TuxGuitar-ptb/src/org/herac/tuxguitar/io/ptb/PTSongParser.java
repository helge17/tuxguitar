package org.herac.tuxguitar.io.ptb;

import java.util.Iterator;

import org.herac.tuxguitar.io.ptb.base.PTBar;
import org.herac.tuxguitar.io.ptb.base.PTBeat;
import org.herac.tuxguitar.io.ptb.base.PTComponent;
import org.herac.tuxguitar.io.ptb.base.PTGuitarIn;
import org.herac.tuxguitar.io.ptb.base.PTNote;
import org.herac.tuxguitar.io.ptb.base.PTPosition;
import org.herac.tuxguitar.io.ptb.base.PTSection;
import org.herac.tuxguitar.io.ptb.base.PTSong;
import org.herac.tuxguitar.io.ptb.base.PTSongInfo;
import org.herac.tuxguitar.io.ptb.base.PTTempo;
import org.herac.tuxguitar.io.ptb.base.PTTrack;
import org.herac.tuxguitar.io.ptb.base.PTTrackInfo;
import org.herac.tuxguitar.io.ptb.helper.TrackHelper;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;

public class PTSongParser {
	
	private TGSongManager manager;
	private TrackHelper helper;
	
	public PTSongParser(TGFactory factory){
		this.manager = new TGSongManager(factory);
		this.helper = new TrackHelper();
	}
	
	public TGSong parseSong(PTSong src){
		PTSong song = new PTSong();
		PTSongSynchronizerUtil.synchronizeTracks(src, song);
		
		TGSong tgSong = this.manager.getFactory().newSong();
		
		this.parseTrack(tgSong, song.getTrack1());
		this.parseTrack(tgSong, song.getTrack2());
		this.parseProperties(tgSong, song.getInfo());
		
		this.manager.orderBeats(tgSong);
		
		return tgSong;
	}
	
	private void parseProperties(TGSong tgSong, PTSongInfo info){
		if( info.getName() != null ){
			tgSong.setName( info.getName() );
		}
		if( info.getAlbum() != null ){
			tgSong.setAlbum( info.getAlbum() );
		}
		if( info.getAuthor() != null ){
			tgSong.setAuthor( info.getAuthor() );
		}
		if( info.getCopyright() != null ){
			tgSong.setCopyright( info.getCopyright() );
		}
		if( info.getArrenger() != null ){
			tgSong.setWriter( info.getArrenger() );
		}
		if( info.getGuitarTranscriber() != null || info.getBassTranscriber() != null ){
			String transcriber = new String(); 
			if(info.getGuitarTranscriber() != null ){
				transcriber += info.getGuitarTranscriber();
			}
			if(info.getBassTranscriber() != null ){
				if( transcriber.length() > 0 ){
					transcriber += (" - ");
				}
				transcriber += info.getBassTranscriber();
			}
			tgSong.setTranscriber( transcriber );
		}
		if( info.getGuitarInstructions() != null || info.getBassInstructions() != null ){
			String comments = new String(); 
			if(info.getGuitarInstructions() != null ){
				comments += info.getGuitarInstructions();
			}
			if(info.getBassInstructions() != null ){
				comments += info.getBassInstructions();
			}
			tgSong.setComments( comments );
		}
	}
	
	private void parseTrack(TGSong tgSong, PTTrack track){
		this.helper.reset( track.getDefaultInfo() );
		
		long start = TGDuration.QUARTER_TIME;
		for( int sIndex = 0; sIndex < track.getSections().size(); sIndex ++){
			PTSection section = (PTSection) track.getSections().get(sIndex);
			section.sort();
			
			//calculo el siguiente start del compas
			this.helper.getStartHelper().init(section.getNumber(),section.getStaffs());
			this.helper.getStartHelper().initVoices(start);
			
			//parseo las posiciones
			for( int pIndex = 0; pIndex < section.getPositions().size(); pIndex ++){
				PTPosition position = (PTPosition)section.getPositions().get(pIndex);
				parsePosition(tgSong, track, position);
			}
			
			//Calculo el start para la proxima seccion
			start = this.helper.getStartHelper().getMaxStart();
		}
	}
	
	private void parsePosition(TGSong tgSong, PTTrack track,PTPosition position){
		for(int i = 0; i < position.getComponents().size(); i ++){
			PTComponent component = (PTComponent)position.getComponents().get(i);
			if(component instanceof PTBar){
				parseBar((PTBar)component);
			}
			else if(component instanceof PTGuitarIn){
				parseGuitarIn(tgSong, track,(PTGuitarIn)component);
			}
			else if(component instanceof PTTempo){
				parseTempo(tgSong, (PTTempo)component);
			}
			else if(component instanceof PTBeat){
				parseBeat(tgSong, (PTBeat)component);
			}
		}
	}
	
	private void parseBar(PTBar bar){
		this.helper.getStartHelper().initVoices( this.helper.getStartHelper().getMaxStart() );
		
		if(bar.getNumerator() > 0 && bar.getDenominator() > 0 ){
			this.helper.getStartHelper().setBarStart( this.helper.getStartHelper().getMaxStart() );
			this.helper.getStartHelper().setBarLength((long)(bar.getNumerator() *( TGDuration.QUARTER_TIME * ( 4.0f / bar.getDenominator()))));
		}
	}
	
	private void parseGuitarIn(TGSong tgSong, PTTrack track,PTGuitarIn guitarIn){
		PTTrackInfo info = track.getInfo(guitarIn.getTrackInfo());
		if(info != null){
			
			// Remove used tracks after guitarIn staff.
			while( this.helper.getInfoHelper().countStaffTracks() > guitarIn.getStaff() ){
				this.helper.getInfoHelper().removeStaffTrack( this.helper.getInfoHelper().countStaffTracks() - 1 );
			}
			
			// If track was already created, but it's not in use
			Iterator<TGTrack> it = tgSong.getTracks();
			while( it.hasNext() ){
				TGTrack tgTrack = (TGTrack )it.next();
				if( hasSameInfo(tgSong, tgTrack , info)){
					boolean exists = false;
					for( int i = 0 ; i < this.helper.getInfoHelper().countStaffTracks() ; i ++ ){
						TGTrack existent = this.helper.getInfoHelper().getStaffTrack(i);
						if(existent != null && existent.getNumber() == tgTrack.getNumber() ){
							exists = true;
						}
					}
					if(!exists){
						this.helper.getInfoHelper().addStaffTrack( tgTrack );
						return;
					}
				}
			}
			
			// Create track if not exists.
			this.createTrack(tgSong, info);
		}
	}
	
	private void parseTempo(TGSong tgSong, PTTempo tempo){
		TGMeasure measure = getMeasure(tgSong, 1,this.helper.getStartHelper().getMaxStart());
		measure.getTempo().setValue( tempo.getTempo() );
		measure.getHeader().setTripletFeel(tempo.getTripletFeel());
	}
	
	private void parseBeat(TGSong tgSong, PTBeat beat){
		if(beat.isGrace()){
			//TODO: agrear el efecto a las notas del siguiente beat
			return;
		}
		
		if( beat.getMultiBarRest() > 1){
			// Multibar Rests, must allways have measure duration.
			long start = this.helper.getStartHelper().getBarStart();
			long duration = (beat.getMultiBarRest() * this.helper.getStartHelper().getBarLength());
			
			this.helper.getStartHelper().setVoiceStart(beat.getStaff(),beat.getVoice(),(start + duration));
			
			return ;
		}
		
		long start = this.helper.getStartHelper().getVoiceStart(beat.getStaff(),beat.getVoice());
		
		TGMeasure measure = getMeasure(tgSong, getStaffTrack(tgSong, beat.getStaff()) , start );
		TGBeat tgBeat = getBeat(measure, start);
		TGVoice tgVoice = tgBeat.getVoice(beat.getVoice());
		tgVoice.setEmpty(false);
		tgVoice.getDuration().setValue(beat.getDuration());
		tgVoice.getDuration().setDotted(beat.isDotted());
		tgVoice.getDuration().setDoubleDotted(beat.isDoubleDotted());
		tgVoice.getDuration().getDivision().setTimes(beat.getTimes());
		tgVoice.getDuration().getDivision().setEnters(beat.getEnters());
		
		Iterator<PTNote> it = beat.getNotes().iterator();
		while(it.hasNext()){
			PTNote ptNote = (PTNote)it.next();
			if( ptNote.getString() <= measure.getTrack().stringCount() && ptNote.getValue() >= 0 ){
				TGNote note = this.manager.getFactory().newNote();
				note.setString(ptNote.getString());
				note.setValue(ptNote.getValue());
				note.setTiedNote( ptNote.isTied() );
				note.getEffect().setVibrato( beat.isVibrato() );
				note.getEffect().setDeadNote( ptNote.isDead() );
				note.getEffect().setHammer( ptNote.isHammer() );
				note.getEffect().setSlide( ptNote.isSlide() );
				note.getEffect().setBend( makeBend(ptNote.getBend()));
				tgVoice.addNote(note);
			}
		}
		
		if( beat.isArpeggioUp() ){
			tgBeat.getStroke().setDirection( TGStroke.STROKE_DOWN );
			tgBeat.getStroke().setValue( TGDuration.SIXTEENTH );
		}else if( beat.isArpeggioDown() ){
			tgBeat.getStroke().setDirection( TGStroke.STROKE_UP );
			tgBeat.getStroke().setValue( TGDuration.SIXTEENTH );
		}
		
		this.helper.getStartHelper().checkBeat( tgVoice.isRestVoice() );
		
		// If it's a rest measure, duration must fill the measure.
		long duration = tgVoice.getDuration().getTime();
		
		if(tgVoice.isRestVoice() && tgBeat.getStart() == this.helper.getStartHelper().getBarStart() && duration > this.helper.getStartHelper().getBarLength()){
			duration = this.helper.getStartHelper().getBarLength();
		}
		this.helper.getStartHelper().setVoiceStart(beat.getStaff(),beat.getVoice(),(tgBeat.getStart() + duration));
	}
	
	private TGEffectBend makeBend(int value){
		if(value >= 1 && value <= 8){
			TGEffectBend bend = this.manager.getFactory().newEffectBend();
			if(value == 1){
				bend.addPoint(0,0);
				bend.addPoint(6,(TGEffectBend.SEMITONE_LENGTH * 4));
				bend.addPoint(12,(TGEffectBend.SEMITONE_LENGTH * 4));
			}
			else if(value == 2){
				bend.addPoint(0,0);
				bend.addPoint(3,(TGEffectBend.SEMITONE_LENGTH * 4));
				bend.addPoint(6,(TGEffectBend.SEMITONE_LENGTH * 4));
				bend.addPoint(9,0);
				bend.addPoint(12,0);
			}
			else if(value == 3){
				bend.addPoint(0,0);
				bend.addPoint(6,(TGEffectBend.SEMITONE_LENGTH * 4));
				bend.addPoint(12,(TGEffectBend.SEMITONE_LENGTH * 4));
			}
			else if(value == 4){
				bend.addPoint(0,(TGEffectBend.SEMITONE_LENGTH * 4));
				bend.addPoint(12,(TGEffectBend.SEMITONE_LENGTH * 4));
			}
			else if(value == 5){
				bend.addPoint(0,(TGEffectBend.SEMITONE_LENGTH * 4));
				bend.addPoint(4,(TGEffectBend.SEMITONE_LENGTH * 4));
				bend.addPoint(8,0);
				bend.addPoint(12,0);
			}
			else if(value == 6){
				bend.addPoint(0,8);
				bend.addPoint(12,8);
			}
			else if(value == 7){
				bend.addPoint(0,(TGEffectBend.SEMITONE_LENGTH * 4));
				bend.addPoint(4,(TGEffectBend.SEMITONE_LENGTH * 4));
				bend.addPoint(8,0);
				bend.addPoint(12,0);
			}
			else if(value == 8){
				bend.addPoint(0,(TGEffectBend.SEMITONE_LENGTH * 4));
				bend.addPoint(4,(TGEffectBend.SEMITONE_LENGTH * 4));
				bend.addPoint(8,0);
				bend.addPoint(12,0);
			}
			return bend;
		}
		return null;
	}
	
	private TGMeasure getMeasure(TGSong tgSong, int trackNumber,long start){
		return getMeasure(tgSong, getTrack(tgSong, trackNumber) , start);
	}
	
	private TGMeasure getMeasure(TGSong tgSong, TGTrack track,long start){
		TGMeasure measure = null;
		while( (measure = this.manager.getTrackManager().getMeasureAt(track, start)) == null){
			this.manager.addNewMeasureBeforeEnd(tgSong);
		}
		return measure;
	}
	
	private TGTrack getTrack(TGSong tgSong, int number){
		TGTrack track = null;
		while( (track = this.manager.getTrack(tgSong, number)) == null){
			track = createTrack(tgSong);
		}
		return track;
	}
	
	public TGTrack getStaffTrack(TGSong tgSong, int staff){
		TGTrack track = this.helper.getInfoHelper().getStaffTrack( staff );
		return ( track != null ? track : createTrack(tgSong) );
	}
	
	private TGTrack createTrack(TGSong tgSong){
		return createTrack(tgSong, this.helper.getInfoHelper().getDefaultInfo());
	}
	
	private TGTrack createTrack(TGSong tgSong, PTTrackInfo info){
		TGTrack track = this.manager.addTrack(tgSong);
		this.helper.getInfoHelper().addStaffTrack( track );
		this.setTrackInfo(tgSong, track, info );
		return track;
	}
	
	private void setTrackInfo(TGSong tgSong, TGTrack tgTrack , PTTrackInfo info){
		TGChannel tgChannel = this.manager.addChannel(tgSong);
		tgChannel.setProgram((short) info.getInstrument() );
		tgChannel.setVolume((short) info.getVolume() );
		tgChannel.setBalance((short) info.getBalance() );
		tgChannel.setName(this.manager.createChannelNameFromProgram(tgSong, tgChannel));
		
		tgTrack.setName( info.getName() );
		tgTrack.setChannelId(tgChannel.getChannelId());
		tgTrack.getStrings().clear();
		for(int i = 0; i < info.getStrings().length; i ++){
			TGString string = this.manager.getFactory().newString();
			string.setNumber( (i + 1) );
			string.setValue( info.getStrings()[i] );
			tgTrack.getStrings().add(string);
		}
	}
	
	private boolean hasSameInfo(TGSong tgSong, TGTrack track , PTTrackInfo info){
		TGChannel tgChannel = this.manager.getChannel(tgSong, track.getChannelId());
		if( tgChannel == null ){
			return false;
		}
		if( !info.getName().equals( track.getName() ) ){
			return false;
		}
		if( info.getInstrument() != tgChannel.getProgram() ){
			return false;
		}
		if( info.getVolume() != tgChannel.getVolume() ){
			return false;
		}
		if( info.getBalance() != tgChannel.getBalance() ){
			return false;
		}
		if( info.getStrings().length != track.stringCount() ){
			return false;
		}
		for(int i = 0; i < info.getStrings().length; i ++){
			if( info.getStrings()[i] != track.getString( (i + 1) ).getValue() ){
				return false;
			}
		}
		return true;
	}
	
	private TGBeat getBeat(TGMeasure measure, long start){
		int count = measure.countBeats();
		for(int i = 0 ; i < count ; i ++ ){
			TGBeat beat = measure.getBeat( i );
			if( beat.getStart() == start ){
				return beat;
			}
		}
		TGBeat beat = this.manager.getFactory().newBeat();
		beat.setStart(start);
		measure.addBeat(beat);
		return beat;
	}
}