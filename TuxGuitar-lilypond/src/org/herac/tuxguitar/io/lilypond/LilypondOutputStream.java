package org.herac.tuxguitar.io.lilypond;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGTupleto;
import org.herac.tuxguitar.song.models.TGVoice;

public class LilypondOutputStream {
	
	private static final String LILYPOND_VERSION = "2.10.5";
	
	private static final String[] LILYPOND_SHARP_NOTES = new String[]{"c","cis","d","dis","e","f","fis","g","gis","a","ais","b"};
	private static final String[] LILYPOND_FLAT_NOTES = new String[]{"c","des","d","ees","e","f","ges","g","aes","a","bes","b"};
	
	private static final String[] LILYPOND_KEY_SIGNATURES = new String[]{ "c","g","d","a","e","b","fis","cis","f","bes","ees","aes", "des", "ges","ces" };
	
	private static final String INDENT = new String("   ");
	
	private TGSongManager manager;
	
	private PrintWriter writer;
	
	private LilypondSettings settings;
	
	private LilypondTempData temp;
	
	public LilypondOutputStream(OutputStream stream,LilypondSettings settings){
		this.writer = new PrintWriter(stream);
		this.temp = new LilypondTempData();
		this.settings = settings;
	}
	
	public void writeSong(TGSong song){
		this.manager = new TGSongManager();
		this.manager.setSong(song);
		
		this.addVersion();
		this.addPaper(song);
		this.addLayout();
		this.addSongDefinitions(song);
		this.addSong(song);
		
		this.writer.flush();
		this.writer.close();
	}
	
	private void addVersion(){
		this.writer.println("\\version \"" + LILYPOND_VERSION + "\"");
	}
	
	private void addPaper(TGSong song){
		this.writer.println("\\paper {");
		
		this.writer.println(indent(1) + "indent = #" + (this.addTrackTitleOnGroup(song) ? 30 : 0));
		this.writer.println(indent(1) + "printallheaders = #" + getLilypondBoolean(true));
		this.writer.println(indent(1) + "ragged-right = #" + getLilypondBoolean(false));
		this.writer.println(indent(1) + "ragged-bottom = #" + getLilypondBoolean(true));
		this.writer.println("}");
	}
	
	private void addHeader(TGSong song, String instrument, int indent){
		this.writer.println(indent(indent) + "\\header {");
		this.writer.println(indent(indent + 1) + "title = \"" + song.getName() + "\" ");
		this.writer.println(indent(indent + 1) + "composer = \"" + song.getAuthor() + "\" ");
		if(this.settings.isTrackNameEnabled() && !this.addTrackTitleOnGroup(song) && instrument != null){
			this.writer.println(indent(indent + 1) + "instrument = \"" + instrument + "\" ");
		}
		this.writer.println(indent(indent) + "}");
	}
	
	private void addLayout(){
		this.writer.println("\\layout {");
		this.writer.println(indent(1) + "\\context { \\Score");
		this.writer.println(indent(2) + "\\override MetronomeMark #'padding = #'5");
		this.writer.println(indent(1) + "}");
		this.writer.println(indent(1) + "\\context { \\Staff");
		this.writer.println(indent(2) + "\\override TimeSignature #'style = #'numbered");
		this.writer.println(indent(2) + "\\override StringNumber #'transparent = #" + getLilypondBoolean(true));
		this.writer.println(indent(1) + "}");
		this.writer.println(indent(1) + "\\context { \\TabStaff");
		this.writer.println(indent(2) + "\\override TimeSignature #'style = #'numbered");
		this.writer.println(indent(2) + "\\override Stem #'transparent = #" + getLilypondBoolean(this.settings.isScoreEnabled()));
		this.writer.println(indent(2) + "\\override Beam #'transparent = #" + getLilypondBoolean(this.settings.isScoreEnabled()));
		this.writer.println(indent(1) + "}");
		this.writer.println(indent(1) + "\\context { \\StaffGroup");
		this.writer.println(indent(2) + "\\consists \"Instrument_name_engraver\"");
		this.writer.println(indent(1) + "}");
		this.writer.println("}");
	}
	
	private void addSongDefinitions(TGSong song){
		for(int i = 0; i < song.countTracks(); i ++){
			TGTrack track = song.getTrack(i);
			String id = this.trackID(i,"");
			this.temp.reset();
			this.addMusic(track,id);
			this.addLyrics(track,id);
			this.addScoreStaff(track,id);
			this.addTabStaff(track,id);
			this.addStaffGroup(track,id);
		}
	}
	
	private void addSong(TGSong song){
		int trackCount = song.countTracks();
		if(this.settings.isTrackGroupEnabled() && trackCount > 1){
			this.writer.println("\\score {");
			if(this.settings.getTrack() == LilypondSettings.ALL_TRACKS){
				this.writer.println(indent(1) + "<<");
			}
		}
		
		for(int i = 0; i < trackCount; i ++){
			TGTrack track = song.getTrack(i);
			if(this.settings.getTrack() == LilypondSettings.ALL_TRACKS || this.settings.getTrack() == track.getNumber()){
				if(!this.settings.isTrackGroupEnabled() || trackCount == 1){
					this.writer.println("\\score {");
				}
				this.writer.println(indent(1) + "\\" + this.trackID(i,"StaffGroup"));
				if(!this.settings.isTrackGroupEnabled() || trackCount == 1){
					this.addHeader(song,track.getName(), 1);
					this.writer.println("}");
				}
			}
		}
		
		if(this.settings.isTrackGroupEnabled() && trackCount > 1){
			if(this.settings.getTrack() == LilypondSettings.ALL_TRACKS){
				this.writer.println(indent(1) + ">>");
			}
			this.addHeader(song, null, 1);
			this.writer.println("}");
		}
	}
	
	private void addMusic(TGTrack track,String id){
		this.writer.println(id + "Music = #(define-music-function (parser location inTab) (boolean?)");
		this.writer.println("#{");
		TGMeasure previous = null;
		int count = track.countMeasures();
		for(int i = 0; i < count; i ++){
			// TODO: Add multivoice support.
			TGMeasure srcMeasure = track.getMeasure(i);
			TGMeasure measure = new TGVoiceJoiner(this.manager.getFactory(),srcMeasure).process();
			
			int measureFrom = this.settings.getMeasureFrom();
			int measureTo = this.settings.getMeasureTo();
			if((measureFrom <= measure.getNumber() || measureFrom == LilypondSettings.FIRST_MEASURE) && (measureTo >= measure.getNumber() || measureTo == LilypondSettings.LAST_MEASURE )){
				this.addMeasure(measure,previous,1,(i == (count - 1)));
				previous = measure;
			}
		}
		this.writer.println(indent(1) + "\\bar \"|.\"");
		this.writer.println(indent(1) + "\\pageBreak");
		this.writer.println("#})");
	}
	
	private void addScoreStaff(TGTrack track,String id){
		boolean addLyrics = (this.settings.isLyricsEnabled() && !this.settings.isTablatureEnabled() && !track.getLyrics().isEmpty());
		boolean addChordDiagrams = this.settings.isChordDiagramEnabled();
		boolean addTexts = this.settings.isTextEnabled();
		
		this.writer.println(id + "Staff = \\new " + (addLyrics ? "Voice = \"" + id + "Staff\" <<" : "Staff {"));
		
		if(!addChordDiagrams){
			this.writer.println(indent(1) + "\\removeWithTag #'chords");
		}
		if(!addTexts){
			this.writer.println(indent(1) + "\\removeWithTag #'texts");
		}
		this.writer.println(indent(1) + "\\" + id + "Music #" + getLilypondBoolean( false ));
		if(addLyrics){
			this.writer.println(indent(1) + "\\new Lyrics \\lyricsto \"" + id + "Staff\" \\" + id + "Lyrics");
		}
		this.writer.println( (addLyrics ? ">>" : "}" ) );
	}
	
	private void addTabStaff(TGTrack track,String id){
		boolean addLyrics = (this.settings.isLyricsEnabled() && !track.getLyrics().isEmpty());
		boolean addChordDiagrams = (this.settings.isChordDiagramEnabled() && !this.settings.isScoreEnabled());
		boolean addTexts = (this.settings.isTextEnabled() && !this.settings.isScoreEnabled());
		
		this.writer.println(id + "TabStaff = \\new " + (addLyrics ? "TabVoice = \"" + id + "TabStaff\" <<" : "TabStaff {" ));
		
		this.addTuning(track,1);
		
		if(!addChordDiagrams){
			this.writer.println(indent(1) + "\\removeWithTag #'chords");
		}
		if(!addTexts){
			this.writer.println(indent(1) + "\\removeWithTag #'texts");
		}
		this.writer.println(indent(1) + "\\" + id + "Music #" + getLilypondBoolean( true ));
		if(addLyrics){
			this.writer.println(indent(1) + "\\new Lyrics \\lyricsto \"" + id + "TabStaff\" \\" + id + "Lyrics");
		}
		this.writer.println( (addLyrics ? ">>" : "}" ) );
	}
	
	private void addLyrics(TGTrack track,String id){
		this.writer.println(id + "Lyrics = \\lyricmode {");
		this.writer.println(indent(1) + "\\set ignoreMelismata = #" + getLilypondBoolean(true));
		int skippedCount = this.temp.getSkippedLyricBeats().size();
		if(skippedCount > 0){
			this.writer.print(indent(1));
			for(int i = 0 ; i <  skippedCount ; i ++){
				this.writer.print("\\skip " + ((String)this.temp.getSkippedLyricBeats().get(i)) + " ");
			}
			this.writer.println();
		}
		this.writer.println(indent(1) + track.getLyrics().getLyrics());
		this.writer.println(indent(1) + "\\unset ignoreMelismata");
		this.writer.println("}");
	}
	
	private void addTuning(TGTrack track, int indent){
		this.writer.print(indent(indent) + "\\set TabStaff.stringTunings = #'(");
		Iterator strings = track.getStrings().iterator();
		while(strings.hasNext()){
			TGString string = (TGString)strings.next();
			//Lilypond relates string tuning to MIDI middle C (note 60)
			this.writer.print( (string.getValue() - 60) + " ");
		}
		this.writer.println(")");
	}
	
	private void addStaffGroup(TGTrack track,String id){
		this.writer.println(id + "StaffGroup = \\new StaffGroup <<");
		if(this.addTrackTitleOnGroup(track.getSong())){
			this.writer.println(indent(1) + "\\set StaffGroup.instrumentName = #\"" + track.getName()  + "\"");
		}
		if(this.settings.isScoreEnabled()){
			this.writer.println(indent(1) + "\\" + id + "Staff");
		}
		if(this.settings.isTablatureEnabled()){
			this.writer.println(indent(1) + "\\" + id + "TabStaff");
		}
		this.writer.println(">>");
	}
	
	private void addMeasure(TGMeasure measure,TGMeasure previous,int indent,boolean isLast){
		if(previous == null || measure.getTempo().getValue() != previous.getTempo().getValue()){
			this.addTempo(measure.getTempo(),indent);
		}
		
		if(previous == null || measure.getClef() != previous.getClef()){
			this.addClef(measure.getClef(),indent);
		}
		if(previous == null || measure.getKeySignature() != previous.getKeySignature()){
			this.addKeySignature(measure.getKeySignature(),indent);
		}
		
		if(previous == null || !measure.getTimeSignature().isEqual(previous.getTimeSignature())){
			this.addTimeSignature(measure.getTimeSignature(),indent);
		}
		
		// Open repeat
		if(measure.isRepeatOpen()){
			this.addRepeatOpen(measure.getHeader(),indent);
		}
		// If is first measure, and it don't have a repeat-open,
		// We check on next measures if should open it.
		else if(measure.getNumber() == 1){
			this.checkRepeatCount( measure.getHeader() );
			if(this.temp.getRepeatCount() > 0 ){
				this.addRepeatOpen(measure.getHeader(),indent);
			}
		}
		// Open a repeat alternative only if this measure isn't who openned the repeat.
		if(!measure.isRepeatOpen() && measure.getHeader().getRepeatAlternative() > 0){
			this.addRepeatAlternativeOpen(indent);
		}
		
		this.addMeasureComponents(measure,(this.temp.isRepeatOpen() || this.temp.isRepeatAlternativeOpen() ? (indent + 1): indent));
		
		// If is last alternative, we can close it now
		if(this.temp.isRepeatAlternativeOpen() && this.temp.getRepeatAlternativeNumber() >= this.temp.getRepeatCount()){
			this.addRepeatClose(indent);
			this.addRepeatAlternativeClose(indent);
		}
		// Close repeat
		if(measure.getRepeatClose() > 0){
			this.addRepeatClose(indent);
		}
		// If is last, we close any openned repeat
		if(isLast){
			this.addRepeatClose(indent);
			this.addRepeatAlternativeClose(indent);
		}
	}
	
	private void addRepeatOpen(TGMeasureHeader measure,int indent){
		// Close any existent first
		this.addRepeatClose(indent);
		this.addRepeatAlternativeClose(indent);
		
		this.checkRepeatCount(measure);
		this.writer.println(indent(indent) + "\\repeat volta " + this.temp.getRepeatCount() + " {");
		this.temp.setRepeatOpen(true);
	}
	
	private void addRepeatClose(int indent){
		if(this.temp.isRepeatOpen()){
			this.writer.println(indent(indent) + "}");
		}
		this.temp.setRepeatOpen(false);
		if(!this.temp.isRepeatAlternativeOpen()){
			this.temp.setRepeatCount( 0 );
		}
	}
	
	private void addRepeatAlternativeOpen( int indent){
		if(this.temp.isRepeatOpen() && !this.temp.isRepeatAlternativeOpen()){
			this.temp.setRepeatAlternativeOpen( true );
			this.addRepeatClose(indent);
			this.writer.println(indent(indent) + "\\alternative {");
		}
		if(this.temp.isRepeatAlternativeOpen()){
			if(this.temp.getRepeatAlternativeNumber() > 0){
				this.writer.println(indent(indent) + "}");
			}
			this.writer.println(indent(indent) + "{");
			this.temp.setRepeatAlternativeNumber( this.temp.getRepeatAlternativeNumber() + 1 );
		}
	}
	
	private void addRepeatAlternativeClose(int indent){
		if(this.temp.isRepeatAlternativeOpen()){
			if(this.temp.getRepeatAlternativeNumber() > 0){
				this.writer.println(indent(indent) + "}");
			}
			this.writer.println(indent(indent) + "}");
		}
		this.temp.setRepeatAlternativeOpen(false);
		this.temp.setRepeatAlternativeNumber( 0 );
		if(!this.temp.isRepeatOpen()){
			this.temp.setRepeatCount( 0 );
		}
	}
	
	private void addTempo(TGTempo tempo,int indent){
		this.writer.println(indent(indent) + "\\tempo 4=" + tempo.getValue());
	}
	
	private void addTimeSignature(TGTimeSignature ts,int indent){
		this.writer.println(indent(indent) + "\\time " + ts.getNumerator() + "/" + ts.getDenominator().getValue());
	}
	
	private void addKeySignature(int keySignature,int indent){
		if(keySignature >= 0 && keySignature < LILYPOND_KEY_SIGNATURES.length){
			this.writer.println(indent(indent) + "\\key " + LILYPOND_KEY_SIGNATURES[keySignature] + " \\major");
		}
	}
	
	private void addClef(int clef,int indent){
		String clefName = "";
		if(clef == TGMeasure.CLEF_TREBLE){
			clefName = "treble";
		}
		else if(clef == TGMeasure.CLEF_BASS){
			clefName = "bass";
		}
		else if(clef == TGMeasure.CLEF_ALTO){
			clefName = "alto";
		}
		else if(clef == TGMeasure.CLEF_TENOR){
			clefName = "tenor";
		}
		if(clefName!=""){
			this.writer.println(indent(indent) + "\\clef #(if $inTab \"tab\" \"" + clefName + "_8\")");
		}
	}
	
	private void addMeasureComponents(TGMeasure measure,int indent){
		this.writer.print(indent(indent));
		this.addComponents(measure);
		this.writer.println();
	}
	
	private void addComponents(TGMeasure measure){
		int key = measure.getKeySignature();
		TGBeat previous = null;
		
		for(int i = 0 ; i < measure.countBeats() ; i ++){
			TGBeat beat = measure.getBeat( i );
			TGVoice voice = beat.getVoice(0);
			TGTupleto tupleto = voice.getDuration().getTupleto();
			
			if(previous != null && this.temp.isTupletOpen() && !tupleto.isEqual( previous.getVoice(0).getDuration().getTupleto() )){
				this.writer.print("} ");
				this.temp.setTupletOpen(false);
			}
			
			if(!this.temp.isTupletOpen() && !tupleto.isEqual(TGTupleto.NORMAL)){
				this.writer.print("\\times " + tupleto.getTimes() + "/" + tupleto.getEnters() + " {");
				this.temp.setTupletOpen(true);
			}
			
			this.addBeat(key, beat);
			
			previous = beat;
		}
		
		if(this.temp.isTupletOpen()){
			this.writer.print("} ");
			this.temp.setTupletOpen(false);
		}
	}
	
	private void addBeat(int key,TGBeat beat){
		TGVoice voice = beat.getVoice(0);
		if(voice.isRestVoice()){
			this.writer.print("r");
			this.addDuration( voice.getDuration() );
		}
		else{
			int size = voice.countNotes();
			if(size > 1){
				this.writer.print("<");
			}
			for(int i = 0 ; i < size ; i ++){
				TGNote note = voice.getNote(i);
				
				int note_value = (beat.getMeasure().getTrack().getString(note.getString()).getValue() + note.getValue());
				this.addKey(key, note_value);
				if(!(size > 1)){
					this.addDuration( voice.getDuration() );
				}
				this.addString(note.getString());
				if(this.isAnyTiedTo(note)){
					this.writer.print("~");
				}
				
				if(size > 1){
					this.writer.print(" ");
				}
			}
			if(size > 1){
				this.writer.print(">");
				this.addDuration( voice.getDuration() );
			}
			
			if(beat.isChordBeat()){
				this.writer.print("-\\tag #'chords ^\\markup \\fret-diagram #\"");
				TGChord chord = beat.getChord();
				for( int i = 0; i < chord.countStrings(); i ++){
					this.writer.print((i + 1) + "-" + getLilypondChordFret(chord.getFretValue( i )) + ";");
				}
				this.writer.print("\"");
			}
			
			if(beat.isTextBeat()){
				this.writer.print("-\\tag #'texts ^\\markup {\"" + beat.getText().getValue() + "\"}");
			}
			
			if(beat.getMeasure().getTrack().getLyrics().getFrom() > beat.getMeasure().getNumber()){
				this.temp.addSkippedLyricBeat( getLilypondDuration(voice.getDuration()));
			}
			
			this.writer.print(" ");
		}
	}
	
	private void addKey(int key,int value){
		String[] LILYPOND_NOTES = (key <= 7 ? LILYPOND_SHARP_NOTES : LILYPOND_FLAT_NOTES );
		this.writer.print(LILYPOND_NOTES[ value % 12 ]);
		for(int i = 4; i < (value / 12); i ++){
			this.writer.print("'");
		}
		for(int i = (value / 12); i < 4; i ++){
			this.writer.print(",");
		}
	}
	
	private void addString(int string){
		this.writer.print("\\" + string);
	}
	
	private void addDuration(TGDuration duration){
		this.writer.print(getLilypondDuration(duration));
	}
	
	private void checkRepeatCount(TGMeasureHeader header){
		boolean alternativePresent = false;
		TGMeasureHeader next = header;
		while( next != null ){
			if( next.isRepeatOpen() && next.getNumber() != header.getNumber()){
				break;
			}
			if(next.getNumber() > header.getNumber() && next.getRepeatAlternative() > 0){
				alternativePresent = true;
				this.temp.setRepeatCount( (this.temp.getRepeatCount() + 1 ) );
			}else if(!alternativePresent && next.getRepeatClose() > 0){
				this.temp.setRepeatCount( (next.getRepeatClose() + 1 ));
				break;
			}
			next = this.manager.getNextMeasureHeader(next);
		}
	}
	
	private boolean addTrackTitleOnGroup(TGSong song){
		if(this.settings.isTrackNameEnabled() && this.settings.isTrackGroupEnabled()){
			if(this.settings.getTrack() == LilypondSettings.ALL_TRACKS && song.countTracks() > 1){
				return true;
			}
		}
		return false;
	}
	
	private boolean isAnyTiedTo(TGNote note){
		TGMeasure measure = note.getVoice().getBeat().getMeasure();
		TGBeat beat = this.manager.getMeasureManager().getNextBeat( measure.getBeats(), note.getVoice().getBeat());
		while( measure != null){
			while( beat != null ){
				TGVoice voice = beat.getVoice(0);
				
				// If is a rest beat, all voice sounds must be stopped.
				if(voice.isRestVoice()){
					return false;
				}
				// Check if is there any note at same string.
				Iterator it = voice.getNotes().iterator();
				while( it.hasNext() ){
					TGNote current = (TGNote) it.next();
					if(current.getString() == note.getString()){
						return current.isTiedNote();
					}
				}
				beat = this.manager.getMeasureManager().getNextBeat( measure.getBeats(), beat);
			}
			measure = this.manager.getTrackManager().getNextMeasure(measure);
			if( measure != null ){
				beat = this.manager.getMeasureManager().getFirstBeat( measure.getBeats() );
			}
		}
		return false;
	}
	
	private String indent(int level){
		String indent = new String();
		for(int i = 0; i < level; i ++){
			indent += INDENT;
		}
		return indent;
	}
	
	private String getLilypondBoolean(boolean value){
		return (value ? "#t" : "#f");
	}
	
	private String getLilypondDuration(TGDuration value){
		String duration = Integer.toString(value.getValue());
		if(value.isDotted()){
			duration += (".");
		}
		else if(value.isDoubleDotted()){
			duration += ("..");
		}
		return duration;
	}
	
	private String getLilypondChordFret(int value){
		if(value < 0){
			return ("x");
		}
		if(value == 0){
			return ("o");
		}
		return Integer.toString(value);
	}
	
	private String toBase26(int value){
		String result = new String();
		int base = value;
		while(base > 25){
			result = ( (char)( (base % 26) + 'A') + result);
			base = base / 26 - 1;
		}
		return ((char)(base + 'A') + result);
	}
	
	private String trackID(int index, String suffix){
		//Lilypond identifiers must be alphabetic. Base26 of the track index is used for uniqueness.
		return ("Track" + this.toBase26(index) + suffix);
	}
	
	protected class LilypondTempData{
		
		private int repeatCount;
		private int repeatAlternativeNumber;
		private boolean repeatOpen;
		private boolean repeatAlternativeOpen;
		private boolean tupletOpen;
		private List skippedLyricBeats;
		
		protected LilypondTempData(){
			this.skippedLyricBeats = new ArrayList();
			this.reset();
		}
		
		public void reset(){
			this.repeatCount = 0;
			this.repeatOpen = false;
			this.tupletOpen = false;
			this.skippedLyricBeats.clear();
		}
		
		public int getRepeatCount() {
			return this.repeatCount;
		}
		
		public void setRepeatCount(int repeatCount) {
			this.repeatCount = repeatCount;
		}
		
		public boolean isRepeatOpen() {
			return this.repeatOpen;
		}
		
		public void setRepeatOpen(boolean repeatOpen) {
			this.repeatOpen = repeatOpen;
		}
		
		public int getRepeatAlternativeNumber() {
			return this.repeatAlternativeNumber;
		}
		
		public void setRepeatAlternativeNumber(int repeatAlternativeNumber) {
			this.repeatAlternativeNumber = repeatAlternativeNumber;
		}
		
		public boolean isRepeatAlternativeOpen() {
			return this.repeatAlternativeOpen;
		}
		
		public void setRepeatAlternativeOpen(boolean repeatAlternativeOpen) {
			this.repeatAlternativeOpen = repeatAlternativeOpen;
		}
		
		public boolean isTupletOpen() {
			return this.tupletOpen;
		}
		
		public void setTupletOpen(boolean tupletOpen) {
			this.tupletOpen = tupletOpen;
		}
		
		public void addSkippedLyricBeat( String duration ){
			this.skippedLyricBeats.add( duration );
		}
		
		public List getSkippedLyricBeats(){
			return this.skippedLyricBeats;
		}
	}
	
	public class TGVoiceJoiner {
		private TGFactory factory;
		private TGMeasure measure;
		
		public TGVoiceJoiner(TGFactory factory,TGMeasure measure){
			this.factory = factory;
			this.measure = measure.clone(factory, measure.getHeader());
			this.measure.setTrack( measure.getTrack() );
		}
		
		public TGMeasure process(){
			this.orderBeats();
			this.joinBeats();
			return this.measure;
		}
		
		public void joinBeats(){
			TGBeat previous = null;
			boolean finish = true;
			
			long measureStart = this.measure.getStart();
			long measureEnd = (measureStart + this.measure.getLength());
			for(int i = 0;i < this.measure.countBeats();i++){
				TGBeat beat = this.measure.getBeat( i );
				TGVoice voice = beat.getVoice(0);
				for(int v = 1; v < beat.countVoices(); v++ ){
					TGVoice currentVoice = beat.getVoice(v);
					if(!currentVoice.isEmpty()){
						for(int n = 0 ; n < currentVoice.countNotes() ; n++ ){
							TGNote note = currentVoice.getNote( n );
							voice.addNote( note );
						}
					}
				}
				if( voice.isEmpty() ){
					this.measure.removeBeat(beat);
					finish = false;
					break;
				}
				
				long beatStart = beat.getStart();
				if(previous != null){
					long previousStart = previous.getStart();
					
					TGDuration previousBestDuration = null;
					for(int v = /*1*/0; v < previous.countVoices(); v++ ){
						TGVoice previousVoice = previous.getVoice(v);
						if(!previousVoice.isEmpty()){
							long length = previousVoice.getDuration().getTime();
							if( (previousStart + length) <= beatStart){
								if( previousBestDuration == null || length > previousBestDuration.getTime() ){
									previousBestDuration = previousVoice.getDuration();
								}
							}
						}
					}
					
					if(previousBestDuration != null){
						previousBestDuration.copy( previous.getVoice(0).getDuration() );
					}else{
						if(voice.isRestVoice()){
							this.measure.removeBeat(beat);
							finish = false;
							break;
						}
						TGDuration duration = TGDuration.fromTime(this.factory, (beatStart - previousStart) );
						duration.copy( previous.getVoice(0).getDuration() );
					}
				}
				
				TGDuration beatBestDuration = null;
				for(int v = /*1*/0; v < beat.countVoices(); v++ ){
					TGVoice currentVoice = beat.getVoice(v);
					if(!currentVoice.isEmpty()){
						long length = currentVoice.getDuration().getTime();
						if( (beatStart + length) <= measureEnd ){
							if( beatBestDuration == null || length > beatBestDuration.getTime() ){
								beatBestDuration = currentVoice.getDuration();
							}
						}
					}
				}
				
				if(beatBestDuration == null){
					if(voice.isRestVoice()){
						this.measure.removeBeat(beat);
						finish = false;
						break;
					}
					TGDuration duration = TGDuration.fromTime(this.factory, (measureEnd - beatStart) );
					duration.copy( voice.getDuration() );
				}
				previous = beat;
			}
			if(!finish){
				joinBeats();
			}
		}
		
		public void orderBeats(){
			for(int i = 0;i < this.measure.countBeats();i++){
				TGBeat minBeat = null;
				for(int j = i;j < this.measure.countBeats();j++){
					TGBeat beat = this.measure.getBeat(j);
					if(minBeat == null || beat.getStart() < minBeat.getStart()){
						minBeat = beat;
					}
				}
				this.measure.moveBeat(i, minBeat);
			}
		}
	}
}
