package org.herac.tuxguitar.io.gpx;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.graphics.control.TGNoteImpl;
import org.herac.tuxguitar.io.gpx.score.GPXAutomation;
import org.herac.tuxguitar.io.gpx.score.GPXBar;
import org.herac.tuxguitar.io.gpx.score.GPXBeat;
import org.herac.tuxguitar.io.gpx.score.GPXDocument;
import org.herac.tuxguitar.io.gpx.score.GPXDrumkit;
import org.herac.tuxguitar.io.gpx.score.GPXMasterBar;
import org.herac.tuxguitar.io.gpx.score.GPXNote;
import org.herac.tuxguitar.io.gpx.score.GPXRhythm;
import org.herac.tuxguitar.io.gpx.score.GPXTrack;
import org.herac.tuxguitar.io.gpx.score.GPXVoice;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.song.models.TGVoice;

public class GPXDocumentParser {
	
	private TGFactory factory;
	private GPXDocument document;
	
	public GPXDocumentParser(TGFactory factory, GPXDocument document){
		this.factory = factory;
		this.document = document;
	}
	
	public TGSong parse(){
		TGSong tgSong =  this.factory.newSong();
		
		this.parseScore(tgSong);
		this.parseTracks(tgSong);
		this.parseMasterBars(tgSong);
		
		return tgSong;
	}
	
	private void parseScore(TGSong tgSong){
		tgSong.setName(this.document.getScore().getTitle());
		tgSong.setArtist(this.document.getScore().getArtist());
		tgSong.setAlbum(this.document.getScore().getAlbum());
		tgSong.setAuthor(this.document.getScore().getWordsAndMusic());
		tgSong.setCopyright(this.document.getScore().getCopyright());
		tgSong.setWriter(this.document.getScore().getTabber());
		tgSong.setComments(this.document.getScore().getNotices());
	}
	
	private void parseTracks(TGSong tgSong){
		List tracks = this.document.getTracks();
		for( int i = 0 ; i < tracks.size(); i ++ ){
			GPXTrack gpTrack = (GPXTrack) this.document.getTracks().get(i);
			
			TGChannel tgChannel = this.factory.newChannel();
			tgChannel.setProgram((short)gpTrack.getGmProgram());
			tgChannel.setChannel((short)gpTrack.getGmChannel1());
			tgChannel.setEffectChannel((short)gpTrack.getGmChannel2());
			
			for( int c = 0 ; c < tgSong.countChannels() ; c ++ ){
				TGChannel tgChannelAux = tgSong.getChannel(c);
				if( tgChannelAux.getChannel() == tgChannel.getChannel() ){
					tgChannel.setChannelId(tgChannelAux.getChannelId());
				}
			}
			if( tgChannel.getChannelId() <= 0 ){
				tgChannel.setChannelId( tgSong.countChannels() + 1 );
				tgChannel.setName(("#" + tgChannel.getChannelId()));
				tgSong.addChannel(tgChannel);
			}
			
			TGTrack tgTrack = this.factory.newTrack();
			tgTrack.setNumber( i + 1 );
			tgTrack.setName(gpTrack.getName());
			tgTrack.setChannelId(tgChannel.getChannelId());
			
			if( gpTrack.getTunningPitches() != null ){
				for( int s = 1; s <= gpTrack.getTunningPitches().length ; s ++ ){
					TGString tgString = this.factory.newString();
					tgString.setNumber(s);
					tgString.setValue(gpTrack.getTunningPitches()[ gpTrack.getTunningPitches().length - s ]);
					tgTrack.getStrings().add(tgString);
				}
			}else if( tgChannel.isPercussionChannel() ){
				for( int s = 1; s <= 6 ; s ++ ){
					tgTrack.getStrings().add(TGSongManager.newString(this.factory, s, 0));
				}
			}else{
				tgTrack.getStrings().add(TGSongManager.newString(this.factory,1, 64));
				tgTrack.getStrings().add(TGSongManager.newString(this.factory,2, 59));
				tgTrack.getStrings().add(TGSongManager.newString(this.factory,3, 55));
				tgTrack.getStrings().add(TGSongManager.newString(this.factory,4, 50));
				tgTrack.getStrings().add(TGSongManager.newString(this.factory,5, 45));
				tgTrack.getStrings().add(TGSongManager.newString(this.factory,6, 40));
			}
			if( gpTrack.getColor() != null && gpTrack.getColor().length == 3 ){
				tgTrack.getColor().setR(gpTrack.getColor()[0]);
				tgTrack.getColor().setG(gpTrack.getColor()[1]);
				tgTrack.getColor().setB(gpTrack.getColor()[2]);
			}
			tgSong.addTrack(tgTrack);
		}
	}
	
	private void parseMasterBars(TGSong tgSong){
		long tgStart = TGDuration.QUARTER_TIME;
		
		List masterBars = this.document.getMasterBars();
		for( int i = 0 ; i < masterBars.size() ; i ++ ){
			GPXMasterBar mbar = (GPXMasterBar) masterBars.get(i);
			GPXAutomation gpTempoAutomation = this.document.getAutomation("Tempo", i);
			
			TGMeasureHeader tgMeasureHeader = this.factory.newHeader();
			tgMeasureHeader.setStart(tgStart);
			tgMeasureHeader.setNumber( i + 1 );
			tgMeasureHeader.setRepeatOpen(mbar.isRepeatStart());
			tgMeasureHeader.setRepeatClose(mbar.getRepeatCount());
			if( mbar.getTime() != null && mbar.getTime().length == 2){
				tgMeasureHeader.getTimeSignature().setNumerator(mbar.getTime()[0]);
				tgMeasureHeader.getTimeSignature().getDenominator().setValue(mbar.getTime()[1]);
			}
			if( gpTempoAutomation != null && gpTempoAutomation.getValue().length == 2 ){
				int tgTempo = gpTempoAutomation.getValue()[0];
				if( gpTempoAutomation.getValue()[1] == 1 ){
					tgTempo = (tgTempo / 2);
				}else if( gpTempoAutomation.getValue()[1] == 3 ){
					tgTempo = (tgTempo + (tgTempo / 2));
				}else if( gpTempoAutomation.getValue()[1] == 4 ){
					tgTempo = (tgTempo * 2);
				}else if( gpTempoAutomation.getValue()[1] == 5 ){
					tgTempo = (tgTempo + (tgTempo * 2));
				}
				tgMeasureHeader.getTempo().setValue( tgTempo );
			}
			tgSong.addMeasureHeader(tgMeasureHeader);
			
			for( int t = 0 ; t < tgSong.countTracks() ; t ++ ){
				TGTrack tgTrack = tgSong.getTrack(t);
				TGMeasure tgMeasure = this.factory.newMeasure(tgMeasureHeader);
				
				tgTrack.addMeasure(tgMeasure);
				
				int gpMasterBarIndex = i;
				GPXBar gpBar = ( t < mbar.getBarIds().length ? this.document.getBar( mbar.getBarIds()[t] ) : null );
				while( gpBar != null && gpBar.getSimileMark() != null ){
					String gpMark = gpBar.getSimileMark();
					if( gpMark.equals("Simple") ){
						gpMasterBarIndex = (gpMasterBarIndex - 1);
					}else if((gpMark.equals("FirstOfDouble") || gpMark.equals("SecondOfDouble")) ){
						gpMasterBarIndex = (gpMasterBarIndex - 2);
					}
					if( gpMasterBarIndex >= 0 ){
						GPXMasterBar gpMasterBarCopy = (GPXMasterBar) masterBars.get(gpMasterBarIndex);
						gpBar = (t < gpMasterBarCopy.getBarIds().length ? this.document.getBar(gpMasterBarCopy.getBarIds()[t]) : null);
					}else{
						gpBar = null;
					}
				}
				
				if( gpBar != null ){
					this.parseBar( gpBar , tgMeasure );
				}
			}
			
			tgStart += tgMeasureHeader.getLength();
		}
	}
	
	private void parseBar(GPXBar bar , TGMeasure tgMeasure){
		int[] voiceIds = bar.getVoiceIds();
		for( int v = 0; v < TGBeat.MAX_VOICES; v ++ ){
			if( voiceIds.length > v ){
				if( voiceIds[v] >= 0 ){
					GPXVoice voice = this.document.getVoice( voiceIds[v] );
					if( voice != null ){
						long tgStart = tgMeasure.getStart();
						for( int b = 0 ; b < voice.getBeatIds().length ; b ++){
							GPXBeat beat = this.document.getBeat( voice.getBeatIds()[b] );
							GPXRhythm gpRhythm = this.document.getRhythm( beat.getRhythmId() );
							
							TGBeat tgBeat = getBeat(tgMeasure, tgStart);
							TGVoice tgVoice = tgBeat.getVoice( v % tgBeat.countVoices() );
							tgVoice.setEmpty(false);
							
							this.parseRhythm(gpRhythm, tgVoice.getDuration());
							if( beat.getNoteIds() != null ){
								int tgVelocity = this.parseDynamic(beat);
								
								for( int n = 0 ; n < beat.getNoteIds().length; n ++ ){
									GPXNote gpNote = this.document.getNote( beat.getNoteIds()[n] );
									if( gpNote != null ){
										this.parseNote(gpNote, tgVoice, tgVelocity);
									}
								}
							}
							
							tgStart += tgVoice.getDuration().getTime();
						}
					}
				}
			}
		}
	}
	
	private void parseNote(GPXNote gpNote, TGVoice tgVoice, int tgVelocity){
		int tgValue = -1;
		int tgString = -1;
		
		if( gpNote.getString() >= 0 && gpNote.getFret() >= 0 ){
			tgValue = gpNote.getFret();
			tgString = (tgVoice.getBeat().getMeasure().getTrack().stringCount() - gpNote.getString());
		}else{
			int gmValue = -1;
			if( gpNote.getMidiNumber() >= 0 ){
				gmValue = gpNote.getMidiNumber();
			}else if( gpNote.getTone() >= 0 && gpNote.getOctave() >= 0 ){
				gmValue = (gpNote.getTone() + ((12 * gpNote.getOctave()) - 12));
			}else if( gpNote.getElement() >= 0 ){
				for( int i = 0 ; i < GPXDrumkit.DRUMKITS.length ; i ++ ){
					if( GPXDrumkit.DRUMKITS[i].getElement() == gpNote.getElement() && GPXDrumkit.DRUMKITS[i].getVariation() == gpNote.getVariation() ){
						gmValue = GPXDrumkit.DRUMKITS[i].getMidiValue();
					}
				}
			}
			
			if( gmValue >= 0 ){
				TGString tgStringAlternative = getStringFor(tgVoice.getBeat(), gmValue );
				if( tgStringAlternative != null ){
					tgValue = (gmValue - tgStringAlternative.getValue());
					tgString = tgStringAlternative.getNumber();
				}
			}
		}
		
		if( tgValue >= 0 && tgString > 0 ){
			TGNote tgNote = this.factory.newNote();
			tgNote.setValue(tgValue);
			tgNote.setString(tgString);
			tgNote.setTiedNote(gpNote.isTieDestination());
			tgNote.setVelocity(tgVelocity);
			tgNote.getEffect().setVibrato(gpNote.isVibrato());
			tgNote.getEffect().setSlide(gpNote.isSlide());
			tgNote.getEffect().setDeadNote(gpNote.isMutedEnabled());
			tgNote.getEffect().setPalmMute(gpNote.isPalmMutedEnabled());
			
			tgVoice.addNote( tgNote );
		}
	}
	
	private void parseRhythm(GPXRhythm gpRhythm , TGDuration tgDuration){
		tgDuration.setDotted(gpRhythm.getAugmentationDotCount() == 1);
		tgDuration.setDoubleDotted(gpRhythm.getAugmentationDotCount() == 2);
		tgDuration.getDivision().setTimes(gpRhythm.getPrimaryTupletDen());
		tgDuration.getDivision().setEnters(gpRhythm.getPrimaryTupletNum());
		if(gpRhythm.getNoteValue().equals("Whole")){
			tgDuration.setValue(TGDuration.WHOLE);
		}else if(gpRhythm.getNoteValue().equals("Half")){
			tgDuration.setValue(TGDuration.HALF);
		}else if(gpRhythm.getNoteValue().equals("Quarter")){
			tgDuration.setValue(TGDuration.QUARTER);
		}else if(gpRhythm.getNoteValue().equals("Eighth")){
			tgDuration.setValue(TGDuration.EIGHTH);
		}else if(gpRhythm.getNoteValue().equals("16th")){
			tgDuration.setValue(TGDuration.SIXTEENTH);
		}else if(gpRhythm.getNoteValue().equals("32nd")){
			tgDuration.setValue(TGDuration.THIRTY_SECOND);
		}else if(gpRhythm.getNoteValue().equals("64th")){
			tgDuration.setValue(TGDuration.SIXTY_FOURTH);
		}
	}
	
	private int parseDynamic(GPXBeat beat){
		int tgVelocity = TGVelocities.DEFAULT;
		if( beat.getDynamic() != null ){
			if( beat.getDynamic().equals("PPP")) {
				tgVelocity = TGVelocities.PIANO_PIANISSIMO;
			}else if( beat.getDynamic().equals("PP")) {
				tgVelocity = TGVelocities.PIANISSIMO;
			}else if( beat.getDynamic().equals("P")) {
				tgVelocity = TGVelocities.PIANO;
			}else if( beat.getDynamic().equals("MP")) {
				tgVelocity = TGVelocities.MEZZO_PIANO;
			}else if( beat.getDynamic().equals("MF")) {
				tgVelocity = TGVelocities.MEZZO_FORTE;
			}else if( beat.getDynamic().equals("F")) {
				tgVelocity = TGVelocities.FORTE;
			}else if( beat.getDynamic().equals("FF")) {
				tgVelocity = TGVelocities.FORTISSIMO;
			}else if( beat.getDynamic().equals("FFF")) {
				tgVelocity = TGVelocities.FORTE_FORTISSIMO;
			}
		}
		return tgVelocity;
	}
	
	private TGBeat getBeat(TGMeasure measure, long start){
		int count = measure.countBeats();
		for(int i = 0 ; i < count ; i ++ ){
			TGBeat beat = measure.getBeat( i );
			if( beat.getStart() == start ){
				return beat;
			}
		}
		TGBeat beat = this.factory.newBeat();
		beat.setStart(start);
		measure.addBeat(beat);
		return beat;
	}
	
	private TGString getStringFor(TGBeat tgBeat, int value ){
		List strings = tgBeat.getMeasure().getTrack().getStrings();
		for(int i = 0;i < strings.size();i ++){
			TGString string = (TGString)strings.get(i);
			if(value >= string.getValue()){
				boolean emptyString = true;
				
				for(int v = 0; v < tgBeat.countVoices(); v ++){
					TGVoice voice = tgBeat.getVoice( v );
					Iterator it = voice.getNotes().iterator();
					while (it.hasNext()) {
						TGNoteImpl note = (TGNoteImpl) it.next();
						if (note.getString() == string.getNumber()) {
							emptyString = false;
							break;
						}
					}
				}
				if(emptyString){
					return string;
				}
			}
		}
		return null;
	}
}
