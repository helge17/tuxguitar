package org.herac.tuxguitar.io.lilypond;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGTupleto;

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
		this.addHeader(song);
		this.addLayout();
		this.addSongDefinitions(song);
		this.addSong(song);
		
		this.writer.flush();
		this.writer.close();
	}

	private void addVersion(){
		this.writer.println("\\version \"" + LILYPOND_VERSION + "\"");
	}	
	
	private void addHeader(TGSong song){
		this.writer.println("\\header {");
		this.writer.println(indent(1) + "title = \"" + song.getName() + "\" ");
		this.writer.println(indent(1) + "composer = \"" + song.getAuthor() + "\" ");
		this.writer.println("}");
	}	
	
	private void addLayout(){
		this.writer.println("\\layout {");
		this.writer.println(indent(1) + "\\context { \\Score");
		this.writer.println(indent(2) + "\\override MetronomeMark #'padding = #'5");
		this.writer.println(indent(1) + "}");
		this.writer.println(indent(1) + "\\context { \\Staff");
		this.writer.println(indent(2) + "\\override TimeSignature #'style = #'numbered");
		this.writer.println(indent(2) + "\\override StringNumber #'transparent = ##t");
		this.writer.println(indent(1) + "}");
		this.writer.println(indent(1) + "\\context { \\TabStaff");
		this.writer.println(indent(2) + "\\override TimeSignature #'style = #'numbered");
		this.writer.println(indent(2) + "\\override Stem #'transparent = ##t");
		this.writer.println(indent(2) + "\\override Beam #'transparent = ##t");
		this.writer.println(indent(1) + "}");
		this.writer.println(indent(1) + "\\context { \\StaffGroup");
		this.writer.println(indent(2) + "\\consists \"Instrument_name_engraver\"");
		this.writer.println(indent(1) + "}");
		this.writer.println("}");
	}
	
	private void addSongDefinitions(TGSong song){
		for(int i = 0; i < song.countTracks(); i ++){
			TGTrack track = song.getTrack(i);
			String id = this.trackID(track.getName(),i,"");
			this.addMusic(track,id);
			this.addStaff(id);
			this.addTabStaff(track,id);
			this.addStaffGroup(track,id);
		}
	}
	
	private void addSong(TGSong song){
		if(this.settings.isTrackGroupEnabled()){
			this.writer.println("<<");
		}else{
			this.writer.println("{");
		}
		for(int i = 0; i < song.countTracks(); i ++){
			TGTrack track = song.getTrack(i);
			if(this.settings.getTrack() == LilypondSettings.ALL_TRACKS || this.settings.getTrack() == track.getNumber()){
				this.writer.println(indent(1) + "\\" + this.trackID(track.getName(),i,"StaffGroup"));
			}
		}
		if(this.settings.isTrackGroupEnabled()){
			this.writer.println(">>");
		}else{
			this.writer.println("}");
		}
	}	
	
	private void addMusic(TGTrack track,String id){
		this.writer.println(id + "Music = #(define-music-function (parser location inTab) (boolean?)");
		this.writer.println("#{");
		TGMeasure previous = null;		
		int count = track.countMeasures();
		for(int i = 0; i < count; i ++){
			TGMeasure measure = track.getMeasure(i);
			int measureFrom = this.settings.getMeasureFrom();
			int measureTo = this.settings.getMeasureTo();
			if((measureFrom <= measure.getNumber() || measureFrom == LilypondSettings.FIRST_MEASURE) && (measureTo >= measure.getNumber() || measureTo == LilypondSettings.LAST_MEASURE )){
				this.addMeasure(measure,previous,1,(i == (count - 1)));				
				previous = measure;
			}
		}
		this.writer.println("#})");
	}
	
	private void addStaff(String id){
	    this.writer.println(id + "Staff = \\new Staff {");
	    this.writer.println(indent(1) + "\\" + id + "Music ##f");
		this.writer.println("}");
	}
	
	private void addTabStaff(TGTrack track,String id){
	    this.writer.println(id + "TabStaff = \\new TabStaff {");
	    this.addTuning(track,1);
	    this.writer.println(indent(1) + "\\" + id + "Music ##t");
		this.writer.println("}");
	}
	
	private void addTuning(TGTrack track, int indent){
		this.writer.print(indent(indent) + "\\set TabStaff.stringTunings = #'(");
		Iterator strings = track.getStrings().iterator();
		while(strings.hasNext()){
			TGString string = (TGString)strings.next();
			//Lilypond relates string tuning to MIDI middle C (note 60)
			int value = string.getValue() - 60;
			this.writer.print(value + " ");
		}
		this.writer.println(")");
	}
	
	private void addStaffGroup(TGTrack track,String id){
		this.writer.println(id + "StaffGroup = \\new StaffGroup <<");
		if(this.settings.isTrackNameEnabled()){
			this.writer.println(indent(1) + "\\set StaffGroup.instrumentName = #\"" + track.getName()  + "\"");
		}
		this.writer.println(indent(1) + "\\" + id + "Staff");
		this.writer.println(indent(1) + "\\" + id + "TabStaff");
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
		if(measure.isRepeatOpen()){
			this.addRepeatOpen(indent);
		}

		this.addMeasureComponents(measure,indent);
		
		if(measure.getRepeatClose() > 0 || isLast){
			this.addRepeatClose(indent);
		}
	}
	
	private void addRepeatOpen(int indent){
		if(this.temp.isRepeatOpen()){
			this.writer.print(" }");
		}
		this.writer.println(indent(indent) + "\\repeat volta 2 {");
		this.temp.setRepeatOpen(true);
	}
	
	private void addRepeatClose(int indent){
		if(this.temp.isRepeatOpen()){
			this.writer.println(indent(indent) + "}");
		}
		this.temp.setRepeatOpen(false);
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
			TGTupleto tupleto = beat.getDuration().getTupleto();
			
			if(previous != null && this.temp.isTupletOpen() && !tupleto.isEqual( previous.getDuration().getTupleto() )){
				this.writer.print("} ");
				this.temp.setTupletOpen(false);
			}
			
			if(!this.temp.isTupletOpen() && !tupleto.isEqual(TGTupleto.NORMAL)){
				this.writer.print("\\times " + tupleto.getTimes() + "/" + tupleto.getEnters() + " {");
				this.temp.setTupletOpen(true);
			}

			addBeat(key, beat);
			
			previous = beat;
		}
		

		if(this.temp.isTupletOpen()){
			this.writer.print("} ");
			this.temp.setTupletOpen(false);
		}	
	}	

	private void addBeat(int key,TGBeat beat){
		if(beat.isRestBeat()){
			this.writer.print("r");
			this.addDuration( beat.getDuration() );
		}
		else{
			
			int size = beat.countNotes();
			if(size > 1){
				this.writer.print("<");
			}
			for(int i = 0 ; i < size ; i ++){
				TGNote note = beat.getNote(i);

				int note_value = (note.getBeat().getMeasure().getTrack().getString(note.getString()).getValue() + note.getValue());
				this.addKey(key, note_value);
				if(!(size > 1)){
				    this.addDuration( beat.getDuration() );
				}
				this.addString(note.getString());
				if(this.noteIsTiedTo(note)){
					this.writer.print("~");
				}
			
				if(size > 1){
					this.writer.print(" ");
				}
			}
				
			if(size > 1){
				this.writer.print(">");
				this.addDuration( beat.getDuration() );
			}
			this.writer.print(" ");
		}
	}	

	private void addKey(int key,int value){
		String[] LILYPOND_NOTES;
		if(key <= 7)
		{
			LILYPOND_NOTES = LILYPOND_SHARP_NOTES;
		}
		else
		{
			LILYPOND_NOTES = LILYPOND_FLAT_NOTES;
		}
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
		this.writer.print(duration.getValue());
		if(duration.isDotted()){
			this.writer.print(".");
		}
		else if(duration.isDoubleDotted()){
			this.writer.print("..");
		}
	}
	
	private boolean noteIsTiedTo(TGNote note){
		TGMeasure measure = note.getBeat().getMeasure();
		TGTrack track = measure.getTrack();
		TGNote next = null;
		next = this.manager.getMeasureManager().getNextNote(measure, note.getBeat().getStart(), note.getString());
		if(next != null){
			return next.isTiedNote();
		}
		measure = this.manager.getTrackManager().getMeasure(track,measure.getNumber() + 1);
		if(measure != null){
			next = this.manager.getMeasureManager().getNextNote(measure, note.getBeat().getStart(), note.getString());
			if(next != null){
				return next.isTiedNote();
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

	private String toBase26(int value){
		String s = "";
		int i = value;
		while(i > 25){
		    int r = i % 26;
		    i = i / 26 - 1;
		    s = (char)(r + 'A') + s;
		}
		s = (char)(i + 'A') + s;
		return s;
	}
	
	private String stripNonAscii(String s){
		return s.replaceAll("[^A-Za-z]+","");
	}
	
	private String trackID(String name, int index, String suffix){
		//Lilypond identifiers must be alphabetic. Base26 of the track index is used for uniqueness.
		return this.stripNonAscii(name) + this.toBase26(index) + this.stripNonAscii(suffix);
	}
	
	protected class LilypondTempData{

		private boolean repeatOpen;
		private boolean tupletOpen;		
		
		protected LilypondTempData(){
			this.repeatOpen = false;
			this.tupletOpen = false;
		}

		public boolean isRepeatOpen() {
			return this.repeatOpen;
		}

		public void setRepeatOpen(boolean repeatOpen) {
			this.repeatOpen = repeatOpen;
		}

		public boolean isTupletOpen() {
			return this.tupletOpen;
		}

		public void setTupletOpen(boolean tupletOpen) {
			this.tupletOpen = tupletOpen;
		}
	}
}
