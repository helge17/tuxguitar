package org.herac.tuxguitar.io.gpx;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.gm.GMChannelRoute;
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
import org.herac.tuxguitar.song.models.TGChannelParameter;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.song.models.TGText;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloBar;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import org.herac.tuxguitar.song.models.effects.TGEffectTrill;

public class GPXDocumentParser {
	
	private static final float GP_BEND_POSITION = 100f;
	private static final float GP_BEND_SEMITONE =  25f;
	private static final float GP_WHAMMY_BAR_POSITION = 100f;
	private static final float GP_WHAMMY_BAR_SEMITONE =  50f;
	
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
		List<GPXTrack> tracks = this.document.getTracks();
		for( int i = 0 ; i < tracks.size(); i ++ ){
			GPXTrack gpTrack = (GPXTrack) this.document.getTracks().get(i);
			
			TGChannel tgChannel = this.factory.newChannel();
			tgChannel.setProgram((short)gpTrack.getGmProgram());
			tgChannel.setBank( gpTrack.getGmChannel1() == 9 ? TGChannel.DEFAULT_PERCUSSION_BANK : TGChannel.DEFAULT_BANK);
			
			TGChannelParameter gmChannel1Param = this.factory.newChannelParameter();
			gmChannel1Param.setKey(GMChannelRoute.PARAMETER_GM_CHANNEL_1);
			gmChannel1Param.setValue(Integer.toString(gpTrack.getGmChannel1()));
			
			TGChannelParameter gmChannel2Param = this.factory.newChannelParameter();
			gmChannel2Param.setKey(GMChannelRoute.PARAMETER_GM_CHANNEL_2);
			gmChannel2Param.setValue(Integer.toString(gpTrack.getGmChannel1() != 9 ? gpTrack.getGmChannel2() : gpTrack.getGmChannel1()));
			
			for( int c = 0 ; c < tgSong.countChannels() ; c ++ ){
				TGChannel tgChannelAux = tgSong.getChannel(c);
				for( int n = 0 ; n < tgChannelAux.countParameters() ; n ++ ){
					TGChannelParameter channelParameter = tgChannelAux.getParameter( n );
					if( channelParameter.getKey().equals(GMChannelRoute.PARAMETER_GM_CHANNEL_1) ){
						if( Integer.toString(gpTrack.getGmChannel1()).equals(channelParameter.getValue()) ){
							tgChannel.setChannelId(tgChannelAux.getChannelId());
						}
					}
				}
			}
			if( tgChannel.getChannelId() <= 0 ){
				tgChannel.setChannelId( tgSong.countChannels() + 1 );
				tgChannel.setName(("#" + tgChannel.getChannelId()));
				tgChannel.addParameter(gmChannel1Param);
				tgChannel.addParameter(gmChannel2Param);
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
		
		List<GPXMasterBar> masterBars = this.document.getMasterBars();
		for( int i = 0 ; i < masterBars.size() ; i ++ ){
			GPXMasterBar mbar = (GPXMasterBar) masterBars.get(i);
			GPXAutomation gpTempoAutomation = this.document.getAutomation("Tempo", i);
			
			TGMeasureHeader tgMeasureHeader = this.factory.newHeader();
			tgMeasureHeader.setStart(tgStart);
			tgMeasureHeader.setNumber( i + 1 );
			tgMeasureHeader.setRepeatOpen(mbar.isRepeatStart());
			tgMeasureHeader.setRepeatClose(mbar.getRepeatCount());
			tgMeasureHeader.setTripletFeel(parseTripletFeel(mbar));
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
				
				int accidental = mbar.getAccidentalCount();
				if( accidental < 0 ){
					accidental = 7 - accidental; // translate -1 to 8, etc.
				}
				if( accidental >= 0 && accidental <= 14 ){
					tgMeasure.setKeySignature(accidental);
				}
				
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
		if (bar.getClef() != null) {
			String clef = bar.getClef();
			if (clef.equals("F4")){
				tgMeasure.setClef(TGMeasure.CLEF_BASS);
			} else if (clef.equals("C3")){
				tgMeasure.setClef(TGMeasure.CLEF_ALTO);
			} else if (clef.equals("C4")){
				tgMeasure.setClef(TGMeasure.CLEF_TENOR);
			}
		}
		
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
							tgBeat.getStroke().setDirection( this.parseStroke(beat) );

							if (beat.getText().length() > 0) {
								TGText text = this.factory.newText();
								text.setValue(beat.getText().trim());
								text.setBeat(tgBeat);
								tgBeat.setText(text);
							}
							
							this.parseRhythm(gpRhythm, tgVoice.getDuration());
							if( beat.getNoteIds() != null ){
								int tgVelocity = this.parseDynamic(beat);
								
								for( int n = 0 ; n < beat.getNoteIds().length; n ++ ){
									GPXNote gpNote = this.document.getNote( beat.getNoteIds()[n] );
									if( gpNote != null ){
										this.parseNote(gpNote, tgVoice, tgVelocity, beat);
									}
								}
							}
							
							tgStart += tgVoice.getDuration().getTime();
						}
					}
				}
			}
		}
		
		if( tgMeasure.getNumber() == 1 ){
			this.fixFirstMeasureStartPositions(tgMeasure);
		}
	}
	
	private void parseNote(GPXNote gpNote, TGVoice tgVoice, int tgVelocity, GPXBeat gpBeat){
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
			tgNote.getEffect().setFadeIn(gpBeat.isFadeIn());
			tgNote.getEffect().setVibrato(gpNote.isVibrato());
			tgNote.getEffect().setSlide(gpNote.isSlide());
			tgNote.getEffect().setDeadNote(gpNote.isMutedEnabled());
			tgNote.getEffect().setPalmMute(gpNote.isPalmMutedEnabled());
			tgNote.getEffect().setTapping(gpNote.isTapped());
			tgNote.getEffect().setHammer(gpNote.isHammer());
			tgNote.getEffect().setGhostNote(gpNote.isGhost());
			tgNote.getEffect().setSlapping(gpBeat.isSlapped());
			tgNote.getEffect().setPopping(gpBeat.isPopped());
			tgNote.getEffect().setStaccato(gpNote.getAccent() == 1);
			tgNote.getEffect().setHeavyAccentuatedNote(gpNote.getAccent() == 4);
			tgNote.getEffect().setAccentuatedNote(gpNote.getAccent() == 8);
			tgNote.getEffect().setTrill(parseTrill(gpNote));
			tgNote.getEffect().setTremoloPicking(parseTremoloPicking(gpBeat, gpNote));
			tgNote.getEffect().setHarmonic(parseHarmonic( gpNote ) );
			tgNote.getEffect().setBend(parseBend( gpNote ) );
			tgNote.getEffect().setTremoloBar(parseTremoloBar( gpBeat ));
			
			tgVoice.addNote( tgNote );
		}
	}
	
	private TGEffectTrill parseTrill(GPXNote gpNote){
		TGEffectTrill tr = null;
		if( gpNote.getTrill() > 0 ){
			// A trill from string E frets 3 to 4 returns : <Trill>68</Trill> and <XProperties><XProperty id="688062467"><Int>30</Int></XProperty></XProperties>
			// gpNote.getTrill() returns the MIDI note to trill this note with, TG wants a duration as well.
			tr = this.factory.newEffectTrill();
			tr.setFret(gpNote.getTrill());
			// TODO: add a duration
		}
		return tr;
	}
	
	private TGEffectTremoloPicking parseTremoloPicking(GPXBeat gpBeat, GPXNote gpNote){
		TGEffectTremoloPicking tp = null;
		if (gpBeat.getTremolo() != null && gpBeat.getTremolo().length == 2) {
			tp = this.factory.newEffectTremoloPicking();
			tp.getDuration().setValue((TGDuration.QUARTER * gpBeat.getTremolo()[1]));
		}
		return tp;
	}
	
	private TGEffectHarmonic parseHarmonic(GPXNote note){
		TGEffectHarmonic harmonic = null;
		if( note.getHarmonicType() != null && note.getHarmonicType().length() > 0 ){
			harmonic = this.factory.newEffectHarmonic();
			
			String type = note.getHarmonicType();
			if (type.equals("Artificial")){
				harmonic.setType(TGEffectHarmonic.TYPE_ARTIFICIAL);
			}else if (type.equals("Natural")){
				harmonic.setType(TGEffectHarmonic.TYPE_NATURAL);
			}else if (type.equals("Pinch")){
				harmonic.setType(TGEffectHarmonic.TYPE_PINCH);
			}else{
				// Default type.
				harmonic.setType(TGEffectHarmonic.TYPE_NATURAL);
			}
			
			int hFret = note.getHarmonicFret();
			
			// midi export does this, but not for natural harmonics
			// key = (orig + TGEffectHarmonic.NATURAL_FREQUENCIES[note.getEffect().getHarmonic().getData()][1]);
			
			if (hFret >= 0){
				for(int i = 0;i < TGEffectHarmonic.NATURAL_FREQUENCIES.length;i ++){
					if(hFret == (TGEffectHarmonic.NATURAL_FREQUENCIES[i][0] ) ){
						harmonic.setData(i);
						break;
					}
				}
			}
		}
		return harmonic;
	}
	
	private TGEffectBend parseBend(GPXNote note){
		TGEffectBend bend = null;
		if( note.isBendEnabled() && note.getBendOriginValue() != null && note.getBendDestinationValue() != null ){
			bend = this.factory.newEffectBend();
			
			// Add the first point
			bend.addPoint(0, parseBendValue(note.getBendOriginValue()));
			
			if( note.getBendOriginOffset() != null ){
				bend.addPoint(parseBendPosition(note.getBendOriginOffset()), parseBendValue(note.getBendOriginValue()));
			}
			if( note.getBendMiddleValue() != null ){
				Integer defaultMiddleOffset = new Integer(Math.round(GP_BEND_POSITION / 2));
				if( note.getBendMiddleOffset1() == null || note.getBendMiddleOffset1().intValue() != 12 ){
					Integer offset = (note.getBendMiddleOffset1() != null ? note.getBendMiddleOffset1() : defaultMiddleOffset);
					bend.addPoint(parseBendPosition(offset), parseBendValue(note.getBendMiddleValue()));
				}
				if( note.getBendMiddleOffset2() == null || note.getBendMiddleOffset2().intValue() != 12 ){
					Integer offset = (note.getBendMiddleOffset2() != null ? note.getBendMiddleOffset2() : defaultMiddleOffset);
					bend.addPoint(parseBendPosition(offset), parseBendValue(note.getBendMiddleValue()));
				}
			}
			if( note.getBendDestinationOffset() != null && note.getBendDestinationOffset().intValue() < GP_BEND_POSITION ){
				bend.addPoint(parseBendPosition(note.getBendDestinationOffset()), parseBendValue(note.getBendDestinationValue()));
			}
			
			// Add last point
			bend.addPoint(TGEffectBend.MAX_POSITION_LENGTH, parseBendValue(note.getBendDestinationValue()));
		}
		return bend;
	}
	
	private int parseBendValue( Integer gpValue ){
		return Math.round(gpValue.intValue() * (TGEffectBend.SEMITONE_LENGTH / GP_BEND_SEMITONE));
	}
	
	private int parseBendPosition( Integer gpOffset ){
		return Math.round(gpOffset.intValue() * (TGEffectBend.MAX_POSITION_LENGTH / GP_BEND_POSITION));
	}
	
	private TGEffectTremoloBar parseTremoloBar(GPXBeat beat){
		TGEffectTremoloBar tremoloBar = null;
		if( beat.isWhammyBarEnabled() && beat.getWhammyBarOriginValue() != null && beat.getWhammyBarDestinationValue() != null){
			tremoloBar = this.factory.newEffectTremoloBar();
			
			// Add the first point
			tremoloBar.addPoint(0, parseTremoloBarValue(beat.getWhammyBarOriginValue()));
			
			if( beat.getWhammyBarOriginOffset() != null ){
				tremoloBar.addPoint(parseTremoloBarPosition(beat.getWhammyBarOriginOffset()), parseTremoloBarValue(beat.getWhammyBarOriginValue()));
			}
			if( beat.getWhammyBarMiddleValue() != null ){
				boolean hiddenPoint = false;
				if( beat.getWhammyBarDestinationValue().intValue() != 0 ){
					if( beat.getWhammyBarMiddleValue().intValue() == Math.round(beat.getWhammyBarDestinationValue().intValue() / 2f) ){
						hiddenPoint = false;
					}
				}
				if(!hiddenPoint ){
					Integer defaultMiddleOffset = new Integer(Math.round(GP_WHAMMY_BAR_POSITION / 2));
					Integer offset1 = (beat.getWhammyBarMiddleOffset1() != null ? beat.getWhammyBarMiddleOffset1() : defaultMiddleOffset);
					if( beat.getWhammyBarOriginOffset() == null || offset1.intValue() >= beat.getWhammyBarOriginOffset().intValue() ){
						tremoloBar.addPoint(parseTremoloBarPosition(offset1), parseTremoloBarValue(beat.getWhammyBarMiddleValue()));
					}
					
					Integer offset2 = (beat.getWhammyBarMiddleOffset2() != null ? beat.getWhammyBarMiddleOffset2() : defaultMiddleOffset);
					if( beat.getWhammyBarOriginOffset() == null || offset1.intValue() >= beat.getWhammyBarOriginOffset().intValue() && offset2.intValue() > offset1.intValue() ){
						tremoloBar.addPoint(parseTremoloBarPosition(offset2), parseTremoloBarValue(beat.getWhammyBarMiddleValue()));
					}
				}
			}
			if( beat.getWhammyBarDestinationOffset() != null && beat.getWhammyBarDestinationOffset().intValue() < GP_WHAMMY_BAR_POSITION ){
				tremoloBar.addPoint(parseTremoloBarPosition(beat.getWhammyBarDestinationOffset()), parseTremoloBarValue(beat.getWhammyBarDestinationValue()));
			}
			
			// Add last point
			tremoloBar.addPoint(TGEffectTremoloBar.MAX_POSITION_LENGTH, parseTremoloBarValue(beat.getWhammyBarDestinationValue()));
		}
		return tremoloBar;
	}
	
	private int parseTremoloBarValue( Integer gpValue ){
		int value = Math.round(gpValue.intValue() * (1f / GP_WHAMMY_BAR_SEMITONE));
		if( value > TGEffectTremoloBar.MAX_VALUE_LENGTH ){
			value = TGEffectTremoloBar.MAX_VALUE_LENGTH;
		}
		if( value < (TGEffectTremoloBar.MAX_VALUE_LENGTH * -1) ){
			value = (TGEffectTremoloBar.MAX_VALUE_LENGTH * -1);
		}
		return value;
	}
	
	private int parseTremoloBarPosition( Integer gpOffset ){
		return Math.round(gpOffset.intValue() * (TGEffectTremoloBar.MAX_POSITION_LENGTH / GP_WHAMMY_BAR_POSITION));
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
	
	private int parseStroke(GPXBeat beat){
		int tgStroke = TGStroke.STROKE_NONE;
		String stroke = beat.getBrush(); 
		if ( stroke.equals("Down")){
			tgStroke = TGStroke.STROKE_DOWN;
		}else if ( stroke.equals("Up")){
			tgStroke = TGStroke.STROKE_UP;
		}
		return tgStroke;
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
	
	private int parseTripletFeel(GPXMasterBar gpMasterBar){
		if( gpMasterBar.getTripletFeel() != null ){
			if( gpMasterBar.getTripletFeel().equals("Triplet8th") ){
				return TGMeasureHeader.TRIPLET_FEEL_EIGHTH; 
			}
			if( gpMasterBar.getTripletFeel().equals("Triplet16th") ){
				return TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH;
			}
		}
		return TGMeasureHeader.TRIPLET_FEEL_NONE;
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
		List<TGString> strings = tgBeat.getMeasure().getTrack().getStrings();
		for(int i = 0;i < strings.size();i ++){
			TGString string = (TGString)strings.get(i);
			if(value >= string.getValue()){
				boolean emptyString = true;
				
				for(int v = 0; v < tgBeat.countVoices(); v ++){
					TGVoice voice = tgBeat.getVoice( v );
					Iterator<TGNote> it = voice.getNotes().iterator();
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
	
	private void fixFirstMeasureStartPositions(TGMeasure tgMeasure){
		if( tgMeasure.getNumber() == 1 ){
			long measureEnd = (tgMeasure.getStart() + tgMeasure.getLength());
			long maximumNoteEnd = 0;
			for( int b = 0 ; b < tgMeasure.countBeats(); b ++ ){
				TGBeat tgBeat = tgMeasure.getBeat(b);
				for( int v = 0 ; v < tgBeat.countVoices() ; v ++ ){
					TGVoice tgVoice = tgBeat.getVoice( v );
					if(!tgVoice.isEmpty() ){
						maximumNoteEnd = Math.max(maximumNoteEnd, (tgBeat.getStart() + tgVoice.getDuration().getTime()));
					}
				}
			}
			if( maximumNoteEnd < measureEnd ){
				long movement = (measureEnd - maximumNoteEnd);
				for( int b = 0 ; b < tgMeasure.countBeats(); b ++ ){
					TGBeat tgBeat = tgMeasure.getBeat(b);
					tgBeat.setStart( tgBeat.getStart() + movement );
				}
			}
		}
	}
}
