package org.herac.tuxguitar.io.abc;

import java.util.Iterator;

import org.herac.tuxguitar.io.abc.base.ABCChord;
import org.herac.tuxguitar.io.abc.base.ABCEvent;
import org.herac.tuxguitar.io.abc.base.ABCLocation;
import org.herac.tuxguitar.io.abc.base.ABCSong;
import org.herac.tuxguitar.io.abc.base.ABCTimeSignature;
import org.herac.tuxguitar.io.abc.base.ABCTrack;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.base.TGSongReaderHandle;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGNoteEffect;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGText;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;

public class ABCSongReader extends ABCFileFormat implements TGSongReader {
	
	public static final int[][] PERCUSSION_TUNINGS = new int[][]{
		new int[]{ 32 },
		new int[]{ 49, 32 },
		new int[]{ 49, 41, 32 },
		new int[]{ 49, 51, 42, 32 },
		new int[]{ 49, 42, 50, 37, 32 },
		new int[]{ 49, 51, 42, 50, 45, 37 },
		new int[]{ 49, 51, 42, 50, 45, 37, 41 },
	};
	
	private TGSongManager manager;
	
	public ABCSongReader() {
		super();
	}
	
	public void read(TGSongReaderHandle handle) throws TGFileFormatException {
		try {
			this.manager = new TGSongManager(handle.getFactory());
			
			ABCSettings settings = handle.getContext().getAttribute(ABCSettings.class.getName());
			if( settings == null ) {
				settings = ABCSettings.getDefaults();
			}
			
			handle.setSong(this.parseSong(new ABCInputStream(handle.getInputStream(), settings).readSong()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new TGFileFormatException();
	}
	
	private TGSong parseSong(ABCSong song){
		song.sortEvents();
		
		TGSong tgSong = newTGSong(song);
		this.addMeasureValues(tgSong, song);
		this.addTrackValues(tgSong, song.getTracks());
		this.addComponents(tgSong, song);
		
		return new TGSongAdjuster(this.manager).process(tgSong);
	}
	
	private TGSong newTGSong(ABCSong song) {
		TGSong tgSong = this.manager.newSong();
		
		int tracks=song.getTracks().length;
		int measures=song.getMeasures();
		int tempo=song.getTempo(0);
		
		this.manager.getFirstMeasureHeader(tgSong).getTempo().setValue(tempo);
		
		while(tgSong.countTracks() < tracks){
			this.manager.addTrack(tgSong);
		}
		while(tgSong.countMeasureHeaders() < measures){
			this.manager.addNewMeasureBeforeEnd(tgSong);
		}
		tgSong.setCopyright("GPL");
		if(song.getInfo()!=null) {
			String s=song.getInfo().getBook();
			if(s!=null) tgSong.setAlbum(s);
			s=song.getInfo().getArtist();
			if(s!=null) tgSong.setArtist(s);
			s=song.getInfo().getComponist();
			if(s!=null) tgSong.setAuthor(s);
			s=song.getInfo().getTitle();
			if(s!=null) tgSong.setName(s);
			else {
				s=song.getInfo().getSource();
				if(s!=null) tgSong.setName(s);
				else {
					s=song.getInfo().getOrigin();
					if(s!=null) tgSong.setName(s);
					else {
						s=song.getInfo().getFilename();
						if(s!=null) tgSong.setName(s);
					}
				}
			}
			s=song.getInfo().getTranscriptor();
			if(s!=null) tgSong.setTranscriber(s);
			else {
				s=song.getInfo().getDiscography();
				if(s!=null) tgSong.setTranscriber(s);
			}
			if(song.getInfo().getComments()!=null || song.getInfo().getNotes()!=null) {
				s="";
				if(song.getInfo().getComments()!=null) s+=song.getInfo().getComments();
				if(song.getInfo().getNotes()!=null) s+=song.getInfo().getNotes();
				tgSong.setComments(s);
			}
		}
		for(int t=0;t<tracks;t++) {
			TGTrack trk = this.manager.getTrack(tgSong, t+1);
			int clef=song.getTracks()[t].getClefType();
			int key=song.getKeySignature();
			for(int m=0;m<measures;m++) {
				trk.getMeasure(m).setClef(clef);
				trk.getMeasure(m).setKeySignature(key);
			}
		}
		return tgSong;
	}
	
	private void addMeasureValues(TGSong tgSong, ABCSong song){
		for(int i = 0; i < tgSong.countMeasureHeaders(); i ++){
			TGTimeSignature timeSignature = this.manager.getFactory().newTimeSignature();
			TGTempo tempo=manager.getFactory().newTempo();
			TGMeasureHeader header = tgSong.getMeasureHeader(i);
			ABCTimeSignature ts = song.getTimeSignature(i);
			int t = song.getTempo(i);
			timeSignature.setNumerator( ts.getNumerator() );
			timeSignature.getDenominator().setValue( ts.getDenominator() );
			tempo.setValue(t);
			this.manager.changeTimeSignature(tgSong, header, timeSignature,ts.isToEnd());
			this.manager.changeTempos(tgSong, header, tempo, true);
		}
		if(song.isHornpipe()) {
			TGMeasureHeader header = tgSong.getMeasureHeader(0);
			this.manager.changeTripletFeel(tgSong, header, TGMeasureHeader.TRIPLET_FEEL_EIGHTH, true);
		}
	}
	
	private void addTrackValues(TGSong tgSong, ABCTrack[] tracks){
		for(int i = 0; i < tracks.length; i ++){
			TGTrack track = tgSong.getTrack(i);
			
			TGChannel tgChannel = this.manager.addChannel(tgSong);
			tgChannel.setVolume((short) 127);
			tgChannel.setBalance((short)(( tracks[i].getPan() * 127) / 15));
			tgChannel.setProgram((short)tracks[i].getInstrument());
			tgChannel.setBank( tracks[i].isPercussion() ? TGChannel.DEFAULT_PERCUSSION_BANK : TGChannel.DEFAULT_BANK);
			
			track.setChannelId(tgChannel.getChannelId());
			track.setName(tracks[i].getName());
			track.getStrings().clear();
			int strings[] = tracks[i].getStrings();
			
			for(int j = 0; j < strings.length;j ++){
				if(j >= 7){
					break;
				}
				TGString string = this.manager.getFactory().newString();
				string.setNumber( (j + 1) );
				if(tracks[i].isPercussion()) string.setValue(0);
				else if(tracks[i].isBagpipe()) string.setValue(strings[j]);
				else string.setValue(strings[j]-24);
				track.getStrings().add(string);
			}
		}
	}
	
	private void addComponents(TGSong tgSong, ABCSong song){
		ABCTrack[] tracks = song.getTracks();
		Iterator<ABCLocation> it = song.getEvents().iterator();
		while(it.hasNext()){
			ABCLocation component = it.next();
			
			if(component.getMeasure() >= 0 && component.getMeasure() < tgSong.countMeasureHeaders()){
				for(int i = 0; i < tracks.length; i ++){
					if( component.getTrack()==i ) {
						int strings = tracks[i].getStrings().length;
						int string = component.getEvent().getString();
						TGTrack tgTrack = tgSong.getTrack(i);
						TGMeasure tgMeasure = tgTrack.getMeasure(component.getMeasure());
						TGMeasureHeader header = tgSong.getMeasureHeader(component.getMeasure());
						switch(component.getEvent().getType()) {
						case ABCEvent.NOTE:
							if( string >= 0 && string <  strings && string < 7)
								addNote(tracks[i], component,string,strings,tgMeasure);
							break;
						case ABCEvent.CHORD_SYMBOL:
							addChord(song.getChords(),component,tgTrack,tgMeasure);
							break;
						case ABCEvent.DECORATION:
							addDecoration(component,tgMeasure);
							break;
						case ABCEvent.REPEAT_BEGIN:
							header.setRepeatOpen(true);
							break;
						case ABCEvent.REPEAT_END:
							header.setRepeatClose(1);
							break;
						case ABCEvent.REPEAT_END_AND_START:
							header.setRepeatClose(1);
							header = tgSong.getMeasureHeader(component.getMeasure()+1);
							header.setRepeatOpen(true);
							break;
						case ABCEvent.VARIANT:
							header.setRepeatAlternative(component.getEvent().getVariant());
							break;
						}
					}
				}
			}
		}
	}

	private void addDecoration(ABCLocation component, TGMeasure tgMeasure) {
		TGBeat tgBeat = this.manager.getMeasureManager().getBeat(tgMeasure, getStart(null, tgMeasure, component.getTicks()));
		int n=tgBeat==null ? 0 : tgBeat.getVoice(0).countNotes();
		TGNote[] note=new TGNote[n];
		TGNoteEffect[] effect=new TGNoteEffect[n];
		TGFactory factory=manager.getFactory();
		for(int i=0;i<n;i++) {
			note[i] = tgBeat.getVoice(0).getNote(i);
			effect[i]=note[i].getEffect();
		}
		TGMeasureHeader header = tgMeasure.getHeader();
		TGMarker marker=factory.newMarker();
		marker.setMeasure(component.getMeasure()+1);
		switch(component.getEvent().getDecoration()) {
		case ABCEvent.ARPEGGIO:
			if(n<2) return;
			TGDuration duration=tgBeat.getVoice(0).getDuration();
			long t=duration.getTime()/n;
			n=TGDuration.QUARTER;
			if(t<TGDuration.QUARTER_TIME) {
				n=TGDuration.EIGHTH;
				if(t*2<TGDuration.QUARTER_TIME) {
					n=TGDuration.SIXTEENTH;
					if(t*4<TGDuration.QUARTER_TIME) {
						n=TGDuration.THIRTY_SECOND;
						if(t*8<TGDuration.QUARTER_TIME) {
							n=TGDuration.SIXTY_FOURTH;
						}
					}
				}
			}
			tgBeat.getStroke().setDirection(TGStroke.STROKE_DOWN);
			tgBeat.getStroke().setValue(n);
			break;
		case ABCEvent.ACCENT:
			for(int i=0;i<n;i++) {
				if(effect[i]==null) effect[i]=factory.newEffect();
				effect[i].setAccentuatedNote(true);
				note[i].setEffect(effect[i].clone(factory));
			}
			break;
		case ABCEvent.TRILL:
			for(int i=0;i<n;i++) {
				if(effect[i]==null) effect[i]=factory.newEffect();
				effect[i].setTrill(factory.newEffectTrill());
				note[i].setEffect(effect[i].clone(factory));
			}
			break;
		case ABCEvent.STACATODOT:
			for(int i=0;i<n;i++) {
				if(effect[i]==null) effect[i]=factory.newEffect();
				effect[i].setStaccato(true);
				note[i].setEffect(effect[i].clone(factory));
			}
			break;
		case ABCEvent.SEGNO:
			marker.setTitle("$ Segno");
			header.setMarker(marker.clone(factory));
			break;
		case ABCEvent.DS:
			marker.setTitle("D.S.");
			header.setMarker(marker.clone(factory));
			break;
		case ABCEvent.DSS:
			marker.setTitle("D.S.S.");
			header.setMarker(marker.clone(factory));
			break;
		case ABCEvent.DC:
		case ABCEvent.DACAPO:
			marker.setTitle("D.C.");
			header.setMarker(marker.clone(factory));
			break;
		case ABCEvent.ALCODA:
			if(header.getMarker()!=null) {
				marker.setTitle(header.getMarker().getTitle()+" al Coda");
			}
			else
				marker.setTitle("D.C. al Coda");
			header.setMarker(marker.clone(factory));
			break;
		case ABCEvent.DACODA:
			marker.setTitle("Da Coda");
			header.setMarker(marker.clone(factory));
			break;
		case ABCEvent.ALFINE:
			if(header.getMarker()!=null) {
				marker.setTitle(header.getMarker().getTitle()+" al Fine");
			}
			else
				marker.setTitle("D.C. al Fine");
			header.setMarker(marker.clone(factory));
			break;
		case ABCEvent.FINE:
			marker.setTitle("Fine");
			header.setMarker(marker.clone(factory));
			break;
		case ABCEvent.TOCODA:
			marker.setTitle("To Coda");
			header.setMarker(marker.clone(factory));
			break;
		case ABCEvent.CODA:
			if(header.getMarker()!=null) {
				String m=header.getMarker().getTitle();
				if(m.equalsIgnoreCase("to coda"))
					marker.setTitle(m);
				else
					marker.setTitle(m+" | Coda");
				
			}
			else
				marker.setTitle("Coda");
			header.setMarker(marker.clone(factory));
			break;
		case ABCEvent.PPPP:
		case ABCEvent.PPP:
		case ABCEvent.PP:
		case ABCEvent.P:
		case ABCEvent.MP:
		case ABCEvent.MF:
		case ABCEvent.SFZ:
		case ABCEvent.F:
		case ABCEvent.FF:
		case ABCEvent.FFF:
		case ABCEvent.FFFF:
			String dynamic=component.getEvent().toString();
			dynamic=dynamic.substring(1, dynamic.length()-1);
			TGColor color=factory.newColor();
			color.setR(32);
			color.setG(192);
			color.setB(32);
			if(header.getMarker()!=null) {
				String m=header.getMarker().getTitle();
				marker.setTitle(m+" "+dynamic);
				color=marker.getColor();
			}
			else
				marker.setTitle(dynamic);
			marker.setColor(color.clone(factory));
			header.setMarker(marker.clone(factory));
			break;
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
		float fixedPosition = (position*64)/ABCSong.TICKS_PER_QUART;
		if(duration != null && !duration.getDivision().isEqual(TGDivisionType.NORMAL)){
			fixedPosition = (( fixedPosition - (fixedPosition % 64)) + ((((fixedPosition % 64) * 2) * 2) / 3) );
		}
		long start = ((long) (measure.getStart() + ( (fixedPosition * TGDuration.QUARTER_TIME)  / 64)) );
		
		return start;
	}
	
	private TGDuration getDuration(int duration){
		return TGDuration.fromTime(this.manager.getFactory(), ticksToTime(duration));
	}
	
	private int ticksToTime(int tcks) {
		return (int) ((tcks*TGDuration.QUARTER_TIME)/ABCSong.TICKS_PER_QUART);
	}

	private void addNote(ABCTrack track,ABCLocation component,int string,int strings,TGMeasure tgMeasure){
		ABCEvent e=component.getEvent();
		int value    = e.getFret();
		int velocity = e.getVelocity();
		if(track.isPercussion() ){
			int tuning = (Math.min( (strings - 1) ,(PERCUSSION_TUNINGS.length )) - 1);
			if(string >= 0 && string < PERCUSSION_TUNINGS[tuning].length){
				value += PERCUSSION_TUNINGS[tuning][string];
			}
		}
		TGFactory factory=this.manager.getFactory();
		TGNote tgNote = factory.newNote();
		tgNote.setString( string + 1 );
		tgNote.setValue( value );
		tgNote.setVelocity(velocity);
		tgNote.setTiedNote(e.isTied());
		TGDuration tgDuration = getDuration(e.getTicks());
		TGNoteEffect effect = factory.newEffect();
		if(e.isGrace()) {
			TGEffectGrace grace = factory.newEffectGrace();
			int dur=4;
			switch(tgDuration.getValue()) {
			case TGDuration.QUARTER: dur=4;break;
			case TGDuration.EIGHTH: dur=2;break;
			case TGDuration.SIXTEENTH: dur=1;break;
			case TGDuration.HALF: dur=4;break;
			case TGDuration.WHOLE: dur=4;break;
			default:
				dur=4;
				break;
			}
			grace.setOnBeat(false);
			grace.setFret(e.getFret());
			grace.setDuration(dur);
			grace.setTransition(TGEffectGrace.TRANSITION_HAMMER);
			effect.setGrace(grace.clone(factory));
		}
		if(track.isBagpipe()) {
			effect.setVibrato(true);
		}
		effect.setStaccato(e.isStacato() && !e.isLegato());
		tgNote.setEffect(effect.clone(factory));
		TGBeat tgBeat = getBeat(tgMeasure, getStart(tgDuration, tgMeasure, component.getTicks()));
		if(e.isGrace()) {
			for(int i=0;i<tgBeat.getVoice(0).countNotes();i++) {
				TGNote n=tgBeat.getVoice(0).getNote(i);
				if(n.getString()==tgNote.getString()) 
					n.setEffect(tgNote.getEffect().clone(factory));
			}
		}
		else {
			if(e.getLyrics()!=null) {
				String[] lyrics = component.getEvent().getLyrics();
				TGText text = factory.newText();
				text.setValue(lyrics[0]);
				tgBeat.setText(text);
			}
			tgBeat.getVoice(0).getDuration().copyFrom(tgDuration);
			tgBeat.getVoice(0).addNote(tgNote);
		}
	}
	
	private void addChord(ABCChord[] chords,ABCLocation component,TGTrack tgTrack,TGMeasure tgMeasure){
		if(component.getEvent().getChordnum() >= 0 && component.getEvent().getChordnum() < chords.length){
			ABCChord chord = chords[component.getEvent().getChordnum()];
			byte[] strings = chord.getStrings();
			
			TGChord tgChord = this.manager.getFactory().newChord(tgTrack.stringCount());
			tgChord.setName(chord.getName());
			for(int i = 0; i < tgChord.countStrings(); i ++){
				int value = ( ( i < strings.length )?strings[i]:-1 );
				tgChord.addFretValue(i,value);
			}
			if(tgChord.countNotes() > 0){
				TGBeat tgBeat = getBeat(tgMeasure, getStart(null, tgMeasure, component.getTicks()));
				tgBeat.setChord(tgChord);
			}
		}
	}
	
}

class TGSongAdjuster{
	
	protected TGSongManager manager;
	
	public TGSongAdjuster(TGSongManager manager){
		this.manager = manager;
	}
	
	public TGSong process(TGSong song){
		Iterator<TGTrack> tracks = song.getTracks();
		while(tracks.hasNext()){
			TGTrack track = (TGTrack)tracks.next();
			Iterator<TGMeasure> measures = track.getMeasures();
			while(measures.hasNext()){
				TGMeasure measure = (TGMeasure)measures.next();
				this.process(measure);
			}
		}
		return song;
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
