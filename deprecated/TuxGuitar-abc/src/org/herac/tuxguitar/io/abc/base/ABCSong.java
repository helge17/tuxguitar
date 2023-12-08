package org.herac.tuxguitar.io.abc.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGVelocities;

public class ABCSong {

	public static final int TICKS_PER_QUART = 192;

	// propagate_accidentals constants
	private static final int PITCH = 0;
	private static final int OCTAVE = 1;
	private static final int NOT = 2;
	
	// buddy track types
	private static final String DRONE = "drone";
	private static final String DRUMS = "drums";
	private static final String BASE = "base";
	private static final String CHORDS = "chords";

	private int strings;
	private int measures;
	private ABCInfo info;
	private int tempo;
	private ABCTimeSignature timeSignature;
	private ABCRepeat[] repeats;
	private ABCText[] texts;
	private ABCChord[] chords;
	private ABCTrack[] tracks;
	private List<ABCTimeSignatureChange> tsChanges;
	private ABCTimeSignature defaultNoteLength;
	private String key;
	private String parts;
	private String rhythm;
	private List<String> words;
	private List<ABCSymbol> redefinable;
	private List<ABCMacro> macro;
	private int x;
	private String part;
	private List<String> symbols;
	private List<String> lyrics;
	private List<ABCTrack> voice;
	private int track;
	private List<String> remarks;
	private int ticks;
	private int measure;
	private int ticksperbar;
	private List<ABCLocation> events;
	private int lineloc;
	private String scale;
	private String gchord;
	private String drum;
	private String drone;
	private int chordprog;
	private int chordvol;
	private int bassprog;
	private int bassvol;
	private boolean gchordon;
	private boolean drumon;
	private boolean droneon;
	private List<ABCChord> chordsList;
	private List<ABCLocation> voiceloc;
	private int keySignature;
	private ABCChord chord;
	private int tickspergchord;
	private int chordMeasure;
	private int chordTicks;
	private int instrumentOffset;
	private int basevoice;
	private int chordvoice;
	private int drumvoice;
	private int dronevoice;
	private boolean tied;
	private ABCEvent[] drumnotes;
	private List<ABCTempoChange> tmpsChanges;
	private ABCEvent[] dronenotes;
	private String beatstring;
	private int[] beat;
	private int propagate_accidentals;
	private ABCOctaveDatabase octaveDatabase;
	private boolean sorted;

	private int legato;
	
	public ABCSong(){
		this.tsChanges = new ArrayList<ABCTimeSignatureChange>();
		this.tmpsChanges = new ArrayList<ABCTempoChange>();
		this.part=null;
		this.track=0;
		this.measure=0;
		this.ticks=0;
		this.scale=" C D EF G A Bc d ef g a b ";
		this.tied=false;
		this.legato=0;
		this.chord=null;
		this.gchord=null;
		this.drum=null;
		this.drone=null;
		this.gchordon=true;
		this.droneon=false;
		this.drumon=false;
		this.chordvol=127;
		this.bassvol=127;
		basevoice=-1;
		chordvoice=-1;
		drumvoice=-1;
		dronevoice=-1;
		this.sorted=false;
	}
	
	public ABCChord[] getChords() {
		if(this.chords==null && this.chordsList==null) return null;
		if(this.chords==null) {
			this.chords=new ABCChord[this.chordsList.size()];
			for(int i=0;i<chords.length;i++) 
				this.chords[i]=(ABCChord) this.chordsList.get(i);
		}
		return this.chords;
	}
	
	public void setChords(int length) {
		this.chords = new ABCChord[length];
	}
	
	public void setChord(int index,ABCChord chord) {
		this.chords[index] = chord;
	}
	
	public ABCInfo getInfo() {
		return this.info;
	}
	
	public ABCInfo setInfo(ABCInfo info) {
		this.info = info;
		return this.info;
	}
	
	public ABCRepeat[] getRepeats() {
		return this.repeats;
	}
	
	public void setRepeats(int length) {
		this.repeats = new ABCRepeat[length];
	}
	
	public void setRepeat(int index,ABCRepeat repeat) {
		this.repeats[index] = repeat;
	}
	
	public ABCText[] getTexts() {
		return this.texts;
	}
	
	public void setTexts(int length) {
		this.texts = new ABCText[length];
	}
	
	public void setText(int index,ABCText text) {
		this.texts[index] = text;
	}
	
	public ABCTrack[] getTracks() {
		if(voice==null) addVoice("1 clef=treble");
		this.tracks = new ABCTrack[voice.size()];
		for(int i=0;i<tracks.length;i++) {
			tracks[i]=(ABCTrack)voice.get(i);
		}
		return this.tracks;
	}
	
	public void setTracks(int length) {
		this.tracks = new ABCTrack[length];
	}
	
	public void setTrack(int index,ABCTrack track) {
		this.tracks[index] = track;
	}
	
	public ABCTimeSignature getTimeSignature() {
		return this.timeSignature;
	}
	
	public void setTimeSignature(ABCTimeSignature timeSignature) {
		this.timeSignature = timeSignature;
		if(timeSignature==null) return;
		this.ticksperbar = (this.timeSignature.getNumerator() * 4 * TICKS_PER_QUART)/this.timeSignature.getDenominator();
		if(gchord==null) {
			String t=timeSignature.toString();
			// see abcplus_en-1.1.0.pdf page 66
			if(t.equals("4/4")) gchord="fzczfzcz";
			else if(t.equals("3/4")) gchord="fzczcz";
			else if(t.equals("6/8")) gchord="fzcfzc";
			else if(t.equals("9/8")) gchord="fzcfzcfzc";
			else if(t.equals("12/8")) gchord="fzcfzcfzcfzc";
		}
		this.tickspergchord=getTickspergchord();
		addTimeSignatureChange(new ABCTimeSignatureChange(this.measure,timeSignature));
	}
	
	private int getTickspergchord() {
		if(gchord==null) return 0;
		char[] g=gchord.toCharArray();
		int n=0;
		for(int i=0;i<g.length;i++) {
			if(g[i]>'1' && g[i]<='9') n+=g[i]-'1';
			else n++;
		}
		if(n<1) return 0;
		return ticksperbar/n;
	}

	public int getTempo() {
		return this.tempo;
	}
	
	public int getStrings() {
		return this.strings;
	}
	
	public void setStrings(int strings) {
		this.strings = strings;
	}
	
	public int getMeasures() {
		return this.measures;
	}
	
	public void addTempoChange(ABCTempoChange tChange) {
		for(int i=0;i<this.tmpsChanges.size();i++) {
			ABCTempoChange tc = (ABCTempoChange)this.tmpsChanges.get(i);
			if(tc.getMeasure()==tChange.getMeasure()) {
				if(tc.getTempo()!=tChange.getTempo()) {
					this.tmpsChanges.remove(i);
					this.tmpsChanges.add(i, tChange);
				}
				return;
			}
		}
		this.tmpsChanges.add(tChange);
	}
	
	public void addTimeSignatureChange(ABCTimeSignatureChange tsChange) {
		int n=tsChange.getTimeSignature().getNumerator();
		int d=tsChange.getTimeSignature().getDenominator();
		for(int i=0;i<this.tsChanges.size();i++) {
			ABCTimeSignatureChange tsc = (ABCTimeSignatureChange)this.tsChanges.get(i);
			if(tsc.getMeasure()==tsChange.getMeasure()) {
				int n1=tsc.getTimeSignature().getNumerator();
				int d1=tsc.getTimeSignature().getDenominator();
				if(n1!=n || d1!=d) {
					this.tsChanges.remove(i);
					this.tsChanges.add(i, tsChange);
				}
				return;
			}
		}
		this.tsChanges.add(tsChange);
	}
	
	public ABCTimeSignature getTimeSignature(int measure) {
		Iterator<ABCTimeSignatureChange> it = this.tsChanges.iterator();
		ABCTimeSignatureChange last = null;
		while(it.hasNext()){
			ABCTimeSignatureChange change = (ABCTimeSignatureChange)it.next();
			if(change.getMeasure() == measure){
				return change.getTimeSignature();
			}
			if(change.getMeasure() < measure) {
				if(last==null) last=change;
				else if(last.getMeasure() < change.getMeasure()) last=change;
			}
		}
		if(last==null)
			return getTimeSignature();
		return last.getTimeSignature();
	}
	
	public int getTempo(int measure) {
		Iterator<ABCTempoChange> it = this.tmpsChanges.iterator();
		ABCTempoChange last = null;
		while(it.hasNext()){
			ABCTempoChange change = (ABCTempoChange)it.next();
			if(change.getMeasure() == measure){
				return change.getTempo();
			}
			if(change.getMeasure() < measure) {
				if(last==null) last=change;
				else if(last.getMeasure() < change.getMeasure()) last=change;
			}
		}
		if(last==null)
			return getTempo();
		return last.getTempo();
	}

	public String toString(){
		String string = new String("[SONG] *** ABC file format ***\n");
		string +=  (this.getInfo().toString() + "\n");
		string +=  (new ABCTempo(this.getTempo()).toString() + "\n");
		for(int i = 0; i < this.repeats.length; i ++){
			string +=  (this.repeats[i].toString() + "\n");
		}
		for(int i = 0; i < this.texts.length; i ++){
			string +=  (this.texts[i].toString() + "\n");
		}
		for(int i = 0; i < this.chords.length; i ++){
			string +=  (this.chords[i].toString() + "\n");
		}
		for(int i = 0; i < this.tracks.length; i ++){
			string +=  (this.tracks[i].toString() + "\n");
		}
		return string;
	}

	public void setTitle(String string) {
		ABCInfo info=getInfo();
		if(info==null) setInfo(new ABCInfo(string,"",""));
		else info.setTitle(string);
	}

	public void setComments(String string) {
		ABCInfo info=getInfo();
		if(info==null) setInfo(new ABCInfo("","",string));
		else info.setComments(string);
	}

	public void setArtist(String string) {
		ABCInfo info=getInfo();
		if(info==null) info=setInfo(new ABCInfo("","",""));
		info.setArtist(string);
	}

	public void setComponist(String string) {
		ABCInfo info=getInfo();
		if(info==null) info=setInfo(new ABCInfo("","",""));
		info.setComponist(string);
	}

	public void setArea(String string) {
		ABCInfo info=getInfo();
		if(info==null) info=setInfo(new ABCInfo("","",""));
		info.setArea(string);
	}

	public void setBook(String string) {
		ABCInfo info=getInfo();
		if(info==null) info=setInfo(new ABCInfo("","",""));
		info.setBook(string);
	}

	public void setDiscography(String string) {
		ABCInfo info=getInfo();
		if(info==null) info=setInfo(new ABCInfo("","",""));
		info.setDiscography(string);
	}

	public void setDefaultNoteLength(ABCTimeSignature signature) {
		this.defaultNoteLength=signature;		
	}

	/**
	 * @return the defaultNoteLength
	 */
	public ABCTimeSignature getDefaultNoteLength() {
		return defaultNoteLength;
	}

	public void setFilename(String string) {
		ABCInfo info=getInfo();
		if(info==null) info=setInfo(new ABCInfo("","",""));
		info.setFilename(string);
	}

	public void setGroup(String string) {
		ABCInfo info=getInfo();
		if(info==null) info=setInfo(new ABCInfo("","",""));
		info.setGroup(string);
	}

	public void addHistory(String string) {
		ABCInfo info=getInfo();
		if(info==null) info=setInfo(new ABCInfo("","",""));
		info.addHistory(string);
	}

	public void setInformation(String string) {
		if(string.toUpperCase().startsWith("MIDI"))
			midiCommand(string.replaceFirst("[mM][iI][dD][iI]\\s*=?"," ").trim());
		else {
			ABCInfo info=getInfo();
			if(info==null) info=setInfo(new ABCInfo("","",""));
			info.setInformation(string);
		}
	}
/*
Key Sig     Major   Minor    Mix     Dor     Phr     Lyd     Loc
            Ion     Aeo

7 sharps:   C#      A#m      G#Mix   D#Dor   E#Phr   F#Lyd   B#Loc
6 sharps:   F#      D#m      C#Mix   G#Dor   A#Phr   BLyd    E#Loc
5 sharps:   B       G#m      F#Mix   C#Dor   D#Phr   ELyd    A#Loc
4 sharps:   E       C#m      BMix    F#Dor   G#Phr   ALyd    D#Loc
3 sharps:   A       F#m      EMix    BDor    C#Phr   DLyd    G#Loc
2 sharps:   D       Bm       AMix    EDor    F#Phr   GLyd    C#Loc
1 sharp :   G       Em       DMix    ADor    BPhr    CLyd    F#Loc
0 sharps:   C       Am       GMix    DDor    EPhr    FLyd    BLoc
1 flat  :   F       Dm       CMix    GDor    APhr    BbLyd   ELoc
2 flats :   Bb      Gm       FMix    CDor    DPhr    EbLyd   ALoc
3 flats :   Eb      Cm       BbMix   FDor    GPhr    AbLyd   DLoc
4 flats :   Ab      Fm       EbMix   BbDor   CPhr    DbLyd   GLoc
5 flats :   Db      Bbm      AbMix   EbDor   FPhr    GbLyd   CLoc
6 flats :   Gb      Ebm      DbMix   AbDor   BbPhr   CbLyd   FLoc
7 flats :   Cb      Abm      GbMix   DbDor   EbPhr   FbLyd   BbLoc
 */
	public void setKey(String string) {
		this.key=string;
		resetScale();
	}
	
	public void resetScale() {
		final String[] scales={
				"7 sharps:   C#      A#m      G#Mix   D#Dor   E#Phr   F#Lyd   B#Loc ",
				"6 sharps:   F#      D#m      C#Mix   G#Dor   A#Phr   BLyd    E#Loc ",
				"5 sharps:   B       G#m      F#Mix   C#Dor   D#Phr   ELyd    A#Loc ",
				"4 sharps:   E       C#m      BMix    F#Dor   G#Phr   ALyd    D#Loc ",
				"3 sharps:   A       F#m      EMix    BDor    C#Phr   DLyd    G#Loc ",
				"2 sharps:   D       Bm       AMix    EDor    F#Phr   GLyd    C#Loc ",
				"1 sharp :   G       Em       DMix    ADor    BPhr    CLyd    F#Loc ",
				"0 sharps:   C       Am       GMix    DDor    EPhr    FLyd    BLoc  ",
				"1 flat  :   F       Dm       CMix    GDor    APhr    BbLyd   ELoc  ",
				"2 flats :   Bb      Gm       FMix    CDor    DPhr    EbLyd   ALoc  ",
				"3 flats :   Eb      Cm       BbMix   FDor    GPhr    AbLyd   DLoc  ",
				"4 flats :   Ab      Fm       EbMix   BbDor   CPhr    DbLyd   GLoc  ",
				"5 flats :   Db      Bbm      AbMix   EbDor   FPhr    GbLyd   CLoc  ",
				"6 flats :   Gb      Ebm      DbMix   AbDor   BbPhr   CbLyd   FLoc  ",
				"7 flats :   Cb      Abm      GbMix   DbDor   EbPhr   FbLyd   BbLoc "
		};
		final String[] keyscales={
				"7 sharps:  C D EF G A Bc d ef g a b",
				"6 sharps:  C D EF G AB c d ef g ab ",
				"5 sharps:  C DE F G AB c de f g ab ",
				"4 sharps:  C DE F GA B c de f ga b ",
				"3 sharps:  CD E F GA B cd e f ga b ",
				"2 sharps:  CD E FG A B cd e fg a b ",
				"1 sharp : C D E FG A Bc d e fg a b ",
				"0 sharps: C D EF G A Bc d ef g a b ",
				"1 flat  : C D EF G AB c d ef g ab  ",
				"2 flats : C DE F G AB c de f g a b ",
				"3 flats : C DE F GA B c de f ga b  ",
				"4 flats : CD E F GA B cd e f ga b  ",
				"5 flats : CD E FG A B cd e fg a b  ",
				"6 flats :C D E FG A Bc d e fg a b  ",
				"7 flats :C D EF G A Bc d ef g a b  ",
		};
		if(key.equals("none")) {
			this.scale = keyscales[7].substring("0 sharps:".length());
			this.keySignature=0;
		}
		else {
			for(int i=0;i<scales.length;i++) {
				if(scales[i].indexOf(key+" ")>0) {
					this.scale = keyscales[i].substring("0 sharps:".length());
					this.keySignature=i>7?i:7-i; // see TGMeasureimpl.java
					break;
				}
			}
		}
		if(octaveDatabase!=null) octaveDatabase.reset();
	}

	public void addNote(String string) {
		ABCInfo info=getInfo();
		if(info==null) info=setInfo(new ABCInfo("","",""));
		info.addNote(string);
	}

	public void setOrigin(String string) {
		ABCInfo info=getInfo();
		if(info==null) info=setInfo(new ABCInfo("","",""));
		info.setOrigin(string);
	}

	public void setParts(String string) {
		this.parts=string;
		ABCInfo info=getInfo();
		if(info==null) info=setInfo(new ABCInfo("","",""));
		info.addNote("P:"+this.parts);
	}

	public void setTempo(String string) {
		// TODO textual tempo's Moderato Largo Allegro, etc. etc.
		ABCTimeSignature beat=parseTimeSignature("1/4");
		int bpm=120;
		if(string.trim().matches("[0-9]+"))
			bpm=Integer.parseInt(string,10);
		else if(string.trim().matches("[0-9 /]*\\s*[0-9]+/[0-9]+\\s*=\\s*[0-9]+")) {
			String[] eq=string.trim().split("=");
			beat=parseTimeSignature(eq[0].trim());
			bpm=Integer.parseInt(eq[1].trim(),10);
		}
		else if(string.trim().matches("\".*\"\\s*[0-9 /]*\\s*[0-9]+/[0-9]+\\s*=\\s*[0-9]+")) {
			int i1=string.lastIndexOf('"')+1;
			String[] eq=string.substring(i1).trim().split("=");
			beat=parseTimeSignature(eq[0].trim());
			bpm=Integer.parseInt(eq[1].trim(),10);
		}
		else if(string.trim().matches("[0-9 /]*\\s*[0-9]+/[0-9]+\\s*=\\s*[0-9]+\\s*\".*\"")) {
			int i1=string.indexOf('"');
			String[] eq=string.substring(0,i1).trim().split("=");
			beat=parseTimeSignature(eq[0].trim());
			bpm=Integer.parseInt(eq[1].trim(),10);
		}
		else if(string.trim().matches("\".*\"")) {
			this.tempo=new ABCTempo(string.trim().substring(1,string.trim().length()-1).toLowerCase()).getValue();
			bpm=0;
		}
		if(bpm>0)
			this.tempo=(bpm*beat.getDenominator())/(4*beat.getNumerator());
		alignTicks();
		addTempoChange(new ABCTempoChange(this.measure,this.tempo));
	}

	private ABCTimeSignature parseTimeSignature(String string) {
		// There may be up to 4 beats in the definition, e.g: 
		//	Q:1/4 3/8 1/4 3/8=40
		// This means: play the tune as if Q:5/4=40 was written, but print the tempo indication using separate notes as specified by the user.
		String[] q=string.split("\\s+");
		int numerator=0;
		int denominator=1;
		for(int i=0;i<q.length;i++) {
			if(q[i].matches("[0-9]+/[0-9]+")) {
				int i1=q[i].indexOf('/');
				int n1=Integer.parseInt(q[i].substring(0, i1),10);
				int d1=Integer.parseInt(q[i].substring(i1+1),10);
				if(denominator!=d1) {
					numerator*=d1;
					n1*=denominator;
					denominator*=d1;
				}
				numerator+=n1;
			}
		}
		if(numerator==0) return defaultNoteLength;
		return new ABCTimeSignature(numerator,denominator,true);
	}
/**
 * Abc also includes a rhythm field, 
 * R:, which is used for cataloguing and sorting collections of abc tunes:
 * this is entirely free text (although there are obvious 'standard' entries eg R:reel, R:jig, R:schottische).
*/
	public void setRhythm(String string) {
		this.rhythm = string;
	}

	public void setSource(String string) {
		ABCInfo info=getInfo();
		if(info==null) info=setInfo(new ABCInfo("","",""));
		info.setSource(string);
	}

	public void addWords(String string) {
		if(this.words==null) this.words=new ArrayList<String>();
		this.words.add(string.substring(2).trim());
		addNote(string);
	}

	public void setTranscriptor(String string) {
		ABCInfo info=getInfo();
		if(info==null) info=setInfo(new ABCInfo("","",""));
		info.setTranscriptor(string);
	}

	public void addRedefinable(String string) {
		ABCSymbol symbol=new ABCSymbol(string);
		if(!symbol.isValid()) return;
		if(this.redefinable==null) this.redefinable=new ArrayList<ABCSymbol>();
		// delete eventual "old" symbol redefinition
		char c=symbol.getName();
		for(int i=0;i<this.redefinable.size();i++) {
			char a=((ABCSymbol)this.redefinable.get(i)).getName();
			if(a==c) {
				this.redefinable.remove(i);
				break;
			}
		}

		if(!symbol.isNil()) 
			this.redefinable.add(symbol);
	}

	public void addMacro(String string) {
		ABCMacro m=new ABCMacro(string);
		if(!m.isValid()) return;
		if(this.macro==null) this.macro=new ArrayList<ABCMacro>();
		this.macro.add(m);
	}

	public void setX(int i) {
		this.x = i;
		ABCInfo info=getInfo();
		if(info==null) info=setInfo(new ABCInfo("","",""));
		info.addNote("X:"+this.x);
	}

	public void setPart(String string) {
		this.part = string;
	}
/**
 * @param string
 * <p>
 *	Adding many symbols to a line of music can make a tune difficult to read.<br>
 *	In such cases, a symbol line (a line that contains only +...+ decorations and "..." chord symbols or annotations) can be used, 
 *	analogous to a lyrics line.<br>
 *	A symbol line starts with s:, followed by a line of symbols.<br> 
 *	Matching of notes and symbols follows the rules defined in section Lyrics.<br>
 *<pre> 
 *	 Example: 
 *	   CDEF    | G'''AB'c
 *	s: "^slow" | +f+ ** +fff+
 *</pre>
 * @see #addLyrics(String)
 */
	public void addSymbols(String string) {
		if(this.symbols==null) this.symbols=new ArrayList<String>();
		this.symbols.add(string);
		if(events==null) return;
		String[] w=string.trim().split("\\s+");
		int x=0;
		boolean dx=false;
		for(int i=lineloc;i<events.size() && x<w.length;i++) {
			ABCLocation loc=(ABCLocation) events.get(i);
			ABCEvent e=loc.getEvent();
			if(loc.getTrack()==this.track && e.getTicks()>0 && e.isGrace()==false) {
				dx=false;
				if(w[x]=="|") {
					dx=true;
					--x;
				}
				else if(w[x].matches("\\*+")) {
					if(w[x].length()==1) dx=true;
					else w[x]=w[x].substring(1);
				}
				else {
					dx=true;
					String s=w[x].replace('~', ' ');
					if(s.startsWith("\"") && s.endsWith("\"")) insertAnnotation(i,s); 
					else if(s.startsWith("+") && s.endsWith("+")) insertDecoration(i,s); 
					else if(s.startsWith("!") && s.endsWith("!")) insertDecoration(i,s); 
					else e.setLyrics(s);
				}
				if(dx) {
					++x;
					while(x<w.length && w[x].equals("|")) {
						int m = loc.getMeasure();
						for(i+=1;i<events.size();i++) {
							loc=(ABCLocation) events.get(i);
							if(loc.getTrack()==this.track && loc.getMeasure()>m)
								break;
						}
						--i;
						++x;
					}
				}
					
			}
		}
	}

private void insertAnnotation(int i, String s) {
	ABCLocation loc=(ABCLocation) events.get(i);
	ABCEvent a=new ABCEvent(this, s+"  ");
	if(a.getType()==ABCEvent.CHORD_SYMBOL) {
		if(chordvoice<0) {
			// encountered first chord, set defaults
			if(voice==null) addVoice("1 clef=treble");
			chordvoice=chordTrack(this.track);
			basevoice=baseTrack(this.track);
			setGchordon(this.gchordon);
			setGchord(gchord);
			setChordprog(chordprog);
			setChordvol(chordvol);
			setBassprog(bassprog);
			setBassvol(bassvol);
		}
		this.chord=(ABCChord) chordsList.get(a.getChordnum());
		events.add(i,new ABCLocation(loc.getPart(),loc.getTrack(),loc.getMeasure(),loc.getTicks(),this.chord,a));
	}
	events.add(i,new ABCLocation(loc.getPart(),loc.getTrack(),loc.getMeasure(),loc.getTicks(),loc.getChord(),a));
}

private void insertDecoration(int i, String s) {
	ABCLocation loc=(ABCLocation) events.get(i);
	ABCEvent d=new ABCEvent(this, s+"  ");
	events.add(i,new ABCLocation(loc.getPart(),loc.getTrack(),loc.getMeasure(),loc.getTicks(),loc.getChord(),d));
}

/**
 * @param string
 * <p>
 *	 The w field (lowercase w) in the body, supplies a line of lyrics to be aligned syllable 
 *   by syllable below the previous line of notes.<br>
 *   Syllables are not aligned on grace notes and tied notes are treated as two separate notes;
 *   slurred or beamed notes are also treated as separate notes in this context. <br>
 *   Note that lyrics are always aligned to the beginning of the preceding music line.<br> 
 *	 It is possible for a music line to be followed by several w fields.<br>
 *   This can be used together with the part notation to create verses.<br>
 *   The first w field is used the first time that part is played, then the second and so on.<br> 
 *	 The lyrics lines are treated as an ABC string.<br>
 *   Within the lyrics, the words should be separated by one or more spaces
 *   and to correctly align them the following symbols may be used:
 *   <p>
 *   <table>
 *    <tr><th>Symbol</th><th>Meaning</th></tr> 
 *	  <tr><td>-    (hyphen)</td><td>break between syllables within a word</td></tr> 
 *	  <tr><td>_    (underscore)</td><td> last syllable is to be held for an extra note</td></tr> 
 *	  <tr><td>*    </td><td>one note is skipped (i.e. * is equivalent to a blank syllable) </td></tr>
 *	  <tr><td>~    </td><td>appears as a space; aligns multiple words under one note </td></tr>
 *	  <tr><td>\-   </td><td>appears as hyphen; aligns multiple syllables under one note </td></tr>
 *	  <tr><td>|    </td><td>advances to the next bar </td></tr>
 *   </table>
 *   <p>
 *	 Note that if - is preceded by a space or another hyphen, it is regarded as a separate syllable.<br> 
 *	 When an underscore is used next to a hyphen, the hyphen must always come first. <br>
 *	 If there are not as many syllables as notes in a measure, typing a | automatically advances to the next bar;
 *	 if there are enough syllables the '|' is just ignored. 
 *<p>
 *	 <b>Some examples:</b>
 *<pre> 
 *	w: syll-a-ble    is aligned with three notes
 *	w: syll-a--ble   is aligned with four notes
 *	w: syll-a -ble   (equivalent to the previous line)
 *	w: time__        is aligned with three notes
 *	w: of~the~day    is treated as one syllable (i.e. aligned with one note)
 *	                 but appears as three separate words
 *	 gf|e2dc B2A2|B2G2 E2D2|.G2.G2 GABc|d4 B2
 *	w: Sa-ys my au-l' wan to your aul' wan\
 *	   Will~ye come to the Wa-x-ies dar-gle?
 *</pre>
 *<p>
 *	 Please see section Continuation of input lines for the meaning of the backslash (\) character. 
 *<p>
 *	 If a word starts with a digit, this is interpreted as numbering of a stanza and is pushed forward a bit.<br> 
 *   In other words, use something like <br>
 *	   w: 1.~Three blind mice<br>
 *	 to put a number before Three.
 *<p>
 *@see #addSymbols(String)
 */
	 public void addLyrics(String string) {
		if(this.lyrics==null) this.lyrics=new ArrayList<String>();
		this.lyrics.add(string);
		if(events==null) return;
		String[] w=string.trim().split("\\s+");
		int x=0;
		boolean dx=false;
		for(int i=lineloc;i<events.size() && x<w.length;i++) {
			ABCLocation loc=(ABCLocation) events.get(i);
			ABCEvent e=loc.getEvent();
			if(loc.getTrack()==this.track && e.getTicks()>0 && e.getType()==ABCEvent.NOTE && !e.isGrace()) {
				dx=false;
				if(w[x].equals("|")) {
					dx=true;
					--x;
				}
				else if(w[x].matches("\\*+")) {
					if(w[x].length()==1) dx=true;
					else w[x]=w[x].substring(1);
				}
				else {
					String s=w[x].trim();
					int h=s.indexOf('-');
					int u=s.indexOf('_');
					if(h==0 && s.length()>1) {
						w[x]=s.substring(1);
						s="-";
					}
					else if(h>0 && h<s.length()-1) {
						w[x]=s.substring(h+1);
						s=s.substring(0, h+1);
					}
					else if(u==0 && s.length()>1) {
						w[x]=s.substring(1);
						s="_";
					}
					else if(u>0) {
						w[x]=s.substring(u);
						s=s.substring(0,u);
					}
					else
						dx=true;
					e.setLyrics(s.replace('~', ' '));
				}
				if(dx) {
					++x;
					while(x<w.length && w[x].equals("|")) {
						int m = loc.getMeasure();
						int j=i;
						for(i+=1;i<events.size();i++) {
							loc=(ABCLocation) events.get(i);
							if(loc.getTrack()==this.track) {
								if(loc.getMeasure()>m)
									break;
								j=i;
							}
						}
						i=j;
						++x;
					}
				}
					
			}
		}
	}

	public void addVoice(String string) {
		if(this.voice==null) {
			this.voice=new ArrayList<ABCTrack>();
			this.voiceloc=new ArrayList<ABCLocation>();
		}
		this.track=this.voice.size();
		this.voice.add(new ABCTrack(string.trim()));
		ABCLocation loc = new ABCLocation(this.part,this.track,0,0,null,null);
		loc.setTied(false);
		loc.setLegato(0);
		loc.setChord(null);
		loc.setTempo(this.tempo);
		this.voiceloc.add(loc);
	}

	public void setVoice(String string) {
		if(this.voice==null) {
			this.voice=new ArrayList<ABCTrack>();
			this.voice.add(new ABCTrack(string.trim()));
			this.voiceloc=new ArrayList<ABCLocation>();
			ABCLocation loc=new ABCLocation(this.part,this.track,this.measure,this.ticks,null,null);
			loc.setTied(this.tied);
			loc.setLegato(this.legato);
			loc.setChord(this.chord);
			loc.setTempo(this.tempo);
			this.voiceloc.add(loc);
		}
		else {
			ABCLocation loc=(ABCLocation) this.voiceloc.get(this.track);
			loc.setPart(this.part);
			loc.setMeasure(this.measure);
			loc.setTicks(this.ticks);
			loc.setTied(this.tied);
			loc.setLegato(this.legato);
			loc.setChord(this.chord);
			loc.setTempo(this.tempo);
		}
		String v=string.trim().split("\\s+")[0];
		for(int i=0;i<voice.size();i++) {
			ABCTrack trk=(ABCTrack)voice.get(i);
			String vi=trk.getName();
			if(vi.equals(v)) {
				this.track = i;
				ABCLocation loc=(ABCLocation) this.voiceloc.get(this.track);
				this.part    = loc.getPart();
				this.measure = loc.getMeasure();
				this.ticks   = loc.getTicks();
				this.tied    = loc.isTied();
				this.legato  = loc.getLegato();
				this.chord   = loc.getChord();
				this.tempo   = loc.getTempo();
				return;
			}
		}
		this.track = voice.size();
		this.voice.add(new ABCTrack(string.trim()));
		ABCLocation loc = new ABCLocation(this.part,this.track,0,0,this.chord,null);
		loc.setTied(false);
		loc.setLegato(0);
		loc.setTempo(this.tempo);
		this.voiceloc.add(loc);
		this.part    = loc.getPart();
		this.measure = loc.getMeasure();
		this.ticks   = loc.getTicks();
		this.tied    = loc.isTied();
		this.legato  = loc.getLegato();
	}

	public void addRemarks(String string) {
		if(this.remarks==null) this.remarks=new ArrayList<String>();
		this.remarks.add(string);
	}

	public void addMusic(String line) {
		if(this.macro!=null) line=substituteMacros(line);
		if(this.redefinable!=null) line=substituteSymbols(line);
		if(this.events==null) this.events = new ArrayList<ABCLocation>();
		this.lineloc = this.events.size();
		char[] a=(line+"  ").toCharArray();
		int i=0;
		int p=1;
		int q=1;
		int r=0;
		int broken=0;
		boolean inchord=false;
		boolean ingrace=false;
		boolean stacato=false;
		int chordstart=0;
		int graceticks=0;
		int fermato=0;
		int lineMeasure=this.measure;
		ABCEvent e,lastnote=null;
		while(i<a.length) {
			switch(a[i]) {
			default:
				e=new ABCEvent(this,line.substring(i));
				e.setSequence(i);
				if(e.getType() == ABCEvent.NOTE) {
					if(ingrace) {
						e.setGrace(true);
					}
				}
				if(r>0 && e.getTicks()>0 && !inchord) {	// triplet recalculation of e.getTicks() for NOTE, REST & CHORD events
					switch(e.getType()) {
					case ABCEvent.NOTE:
					case ABCEvent.REST:
						e.setTriplet(p,q,r-1);
					}
				}
				if(e.getType()==ABCEvent.CHORD_SYMBOL) {
					if(chordvoice<0) {
						// encountered first chord, set defaults
						if(voice==null) addVoice("1 clef=treble");
						chordvoice=chordTrack(this.track);
						basevoice=baseTrack(this.track);
						setGchordon(this.gchordon);
						setGchord(gchord);
						setChordprog(chordprog);
						setChordvol(chordvol);
						setBassprog(bassprog);
						setBassvol(bassvol);
					}
					this.chord=(ABCChord) chordsList.get(e.getChordnum());
					events.add(new ABCLocation(this.part,chordTrack(this.track),this.measure,this.ticks,this.chord,e));
				}
				else {
					if(e.getType()==ABCEvent.NOTE) {
						e.setVelocity(beatVolume());
					}
					if(this.ticks==this.ticksperbar && (e.getType()==ABCEvent.ANNOTATION || e.getType() == ABCEvent.DECORATION))
						events.add(new ABCLocation(this.part,this.track,this.measure+1,0,this.chord,e));
					else
						events.add(new ABCLocation(this.part,this.track,this.measure,this.ticks,this.chord,e));
					if(e.getType()==ABCEvent.DECORATION) {
						switch(e.getDecoration()) {
						case ABCEvent.PPPP: setVolume(TGVelocities.PIANO_PIANISSIMO/2);break;
						case ABCEvent.PPP:  setVolume(TGVelocities.PIANO_PIANISSIMO);break;
						case ABCEvent.PP:   setVolume(TGVelocities.PIANISSIMO);break;
						case ABCEvent.P:    setVolume(TGVelocities.PIANO);break;
						case ABCEvent.MP:   setVolume(TGVelocities.MEZZO_PIANO);break;
						case ABCEvent.MF:   setVolume(TGVelocities.MEZZO_FORTE);break;
						case ABCEvent.SFZ:  setVolume((TGVelocities.MEZZO_FORTE+TGVelocities.FORTE)/2);break;
						case ABCEvent.F:    setVolume(TGVelocities.FORTE);break;
						case ABCEvent.FF:   setVolume(TGVelocities.FORTISSIMO);break;
						case ABCEvent.FFF:  setVolume((TGVelocities.FORTISSIMO+TGVelocities.FORTE_FORTISSIMO)/2);break;
						case ABCEvent.FFFF: setVolume(TGVelocities.FORTE_FORTISSIMO);break;
						case ABCEvent.FERMATA: fermato=tempo;setTempo(String.valueOf(tempo/2));break;
						case ABCEvent.STACATODOT: stacato=true;break;
						}
					}
					else if(e.getType()==ABCEvent.NOTE) {
						if(this.tied) { 
							e.setTied(true);
							if(!inchord) this.tied=false;
						}
						if(stacato) {
							e.setStacato(true);
							if(!inchord) stacato=false;
						}
						e.setLegato(this.legato>0);
					}
				}
				i+=e.getName().length();
				if(!inchord) {
					if(broken != 0 && e.getTicks() > 0) {
						e.setTicks(e.getTicks()+broken);
						broken=0;
					}
					if(!ingrace && e.getTicks()>0) {
						if(r>0) {
							--r;
							this.ticks+=(e.getTicks()*q)/p; // put p notes into the time of q for the next r notes
						}
						else
							this.ticks += e.getTicks();
						if(fermato>0) setTempo(String.valueOf(fermato));
						fermato=0;
					}
				}
				if(e.getTicks() > 0) lastnote=e;
				break;
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				i++;
				break;
			case '&':	// reset to begin of measure
				i++;
				this.ticks=0;
				break;
			case '-': // tied note
				i++;
				this.tied=true;
				break;
			case '>': // broken rhythm
				if(lastnote!=null) {
					int t=lastnote.getTicks();
					broken=0;
					while(a[i]=='>') {
						broken -= t/2;
						++i;
						t += t/2;
					}
					lastnote.setTicks(t);
					this.ticks -= broken;
				}
				else i++;	// no notes to be broken
				break;
			case '<': // broken rhythm
				if(lastnote!=null) {
					int t=lastnote.getTicks();
					int d=t;
					broken=0;
					while(a[i]=='<') {
						broken += t/2;
						++i;
						t += t/2;
					}
					lastnote.setTicks(d-broken);
					this.ticks -= broken;
				}
				else i++;	// no notes to be broken
				break;
			case '{':
				i++;
				ingrace=true;
				graceticks=this.ticks;
				break;
			case '}':
				i++;
				ingrace=false;
				this.ticks=graceticks;
				break;
			case '(':	// (p:q:r triplets, put p notes into the time of q for the next r notes
				i++;
				if(a[i]>'0' && a[i]<='9') {
					p=a[i]-'0';
					i++;
					while("0123456789".indexOf(a[i])>=0) {
						p=10*p+a[i]-'0';
						i++;
					}
					r=p;
//					Symbol					Meaning 
//					(2    2 notes in the time of 3 
//					  (3    3 notes in the time of 2 
//					  (4    4 notes in the time of 3 
//					  (5    5 notes in the time of n 
//					  (6    6 notes in the time of 2 
//					  (7    7 notes in the time of n 
//					  (8    8 notes in the time of 3 
//					  (9    9 notes in the time of n 
//					If the time signature is compound (6/8, 9/8, 12/8) then n is three, otherwise n is two.
					switch(p) {
					case 2: q=3;break;
					case 3: q=2;break;
					case 4: q=3;break;
					case 6: q=2;break;
					case 8: q=3;break;
					default: 
						if(this.timeSignature.getDenominator()==8 && this.timeSignature.getNumerator() % 3==0) q=3;
						else q=2;
					break;
					}
					if(a[i]==':') {
						i++;
						int n=0;
						while("0123456789".indexOf(a[i])>=0) {
							n=10*n+a[i]-'0';
							i++;
						}
						if(n>0) q=n;
						if(a[i]==':') {
							i++;
							n=0;
							while("0123456789".indexOf(a[i])>=0) {
								n=10*n+a[i]-'0';
								i++;
							}
							if(n>0) r=n;
						}
					}
				}
				else
					this.legato++;
				break;
			case ')':
				i++;
				if(this.legato>0)
					this.legato--;
				break;
			case '[':
				if(a[i+2]==':') {
					int j=line.indexOf(']', i+2);
					boolean bol=this.measure == lineMeasure;
					if(j<0) j=line.length();
					switch(a[i+1]) {
					case 'V':
						this.setVoice(line.substring(i+3, j).trim());
						if(bol) 
							lineMeasure=this.measure;
						break;
					case 'P':
						alignTicks();
						this.setPart(line.substring(i+3, j).trim());
						break;
					case 'I':
						if(line.substring(i+3, j).trim().toUpperCase().matches("MIDI\\s*=?.*")) {
							midiCommand("%%MIDI "+line.substring(i+3, j).replaceFirst("MIDI\\s*=?"," ").trim());
						}
						break;
					case 'M':
						alignTicks();
						ABCEvent et = timeCommand(line.substring(i+3, j).trim());
						events.add(new ABCLocation(this.part,this.track,this.measure,0,this.chord,et));
						if(chordvoice>=0 && isBuddy(CHORDS)) events.add(new ABCLocation(this.part,chordvoice,this.measure,0,this.chord,et));
						if(basevoice>=0 && isBuddy(BASE)) events.add(new ABCLocation(this.part,basevoice,this.measure,0,this.chord,et));
						if(drumvoice>=0 && isBuddy(DRUMS)) events.add(new ABCLocation(this.part,drumvoice,this.measure,0,this.chord,et));
						if(dronevoice>=0 && isBuddy(DRONE)) events.add(new ABCLocation(this.part,dronevoice,this.measure,0,this.chord,et));
						ABCTimeSignature ts=new ABCTimeSignature(et.getNumerator(),et.getDenominator(),true);
						addTimeSignatureChange(new ABCTimeSignatureChange(this.measure,ts));
						break;
					case 'Q':
						alignTicks();
						ABCEvent eq = tempoCommand(line.substring(i+3, j).trim());
						events.add(new ABCLocation(this.part,this.track,this.measure,this.ticks,this.chord,eq));
						break;
					case '|':	// a fatbar with a repeat begin "[|:", skip the "[" and let the "|:" code parse again
						j=i;	// consume the "[" only
						alignTicks();
						ABCEvent eb = new ABCEvent(ABCEvent.FATBAR_BAR,"[|",0);
						events.add(new ABCLocation(this.part,this.track,this.measure,this.ticks,this.chord,eb));
						break;
					}
					i=j+1;
				}
				else if(a[i+1]=='|') {
					alignTicks();
					resetScale();
					i+=2;
				}
				else if("123456789".indexOf(a[i+1])>=0) {
					// start of variant part
					ABCEvent ev=new ABCEvent(this,line.substring(i));
					alignTicks();
					events.add(new ABCLocation(this.part,this.track,this.measure,this.ticks,this.chord,ev));
					i+=ev.getName().length();
				}
				else {
					// start of chord
					inchord=true;
					++i;
					chordstart=events.size();
				}
				break;
			case ']':
				if(inchord) {
					remapChord(events.size()-chordstart);
					e=new ABCEvent(this,line.substring(i));
					i+=e.getName().length();
					int t=e.getTicks();
					for(int x=chordstart;x<events.size();x++) {
						ABCLocation l=(ABCLocation) events.get(x);
						e=l.getEvent();
						switch(e.getType()) {
						case ABCEvent.NOTE:
						case ABCEvent.REST:
							if(!e.isGrace()) {
								e.setTicks(t);
								if(r>0)
									e.setTriplet(p,q,r-1);
							}
							break;
						}
					}
					if(r>0) {	// triplet recalculation of e.getTicks() for NOTE, REST & CHORD events
						this.ticks += (t*q)/p; // put p notes into the time of q for the next r notes
						--r;
					}
					else 
						this.ticks+=t;
					if(fermato>0) setTempo(String.valueOf(fermato));
					fermato=0;
					inchord=false;
					this.tied=false;
					stacato=false;
					lastnote=null; // can not cope with broken rhythm on chords
				}
				break;
			case '|':
				alignTicks();
				if(a[i+1]==']' || a[i+1]=='|') {
					events.add(new ABCLocation(this.part,this.track,this.measure,this.ticks,this.chord,new ABCEvent(this,String.valueOf(a, i, 2))));
					resetScale();
					i+=2;
				}
				else if(a[i+1]==':') {
					resetScale();
					events.add(new ABCLocation(this.part,this.track,this.measure,this.ticks,this.chord,new ABCEvent(this,"|:")));
					i+=2;
				}
				else if("123456789".indexOf(a[i+1])>=0) {
					events.add(new ABCLocation(this.part,this.track,this.measure-1,this.ticks,this.chord,new ABCEvent(this,"|")));
					ABCEvent ev=new ABCEvent(this,"["+line.substring(i+1));
					events.add(new ABCLocation(this.part,this.track,this.measure,this.ticks,this.chord,ev));
					resetScale();
					i+=ev.getName().length();
				}
				else {
					events.add(new ABCLocation(this.part,this.track,this.measure-1,this.ticks,this.chord,new ABCEvent(this,String.valueOf(a, i, 1))));
					resetScale();
					i++;
				}
				break;
			case ':':
				if(a[i+1]=='|') {
					events.add(new ABCLocation(this.part,this.track,this.measure,this.ticks,this.chord,new ABCEvent(this,":|")));
					alignTicks();
					resetScale();
					i+=2;
					if("123456789".indexOf(a[i])>=0) {
						ABCEvent ev=new ABCEvent(this,"["+line.substring(i));
						events.add(new ABCLocation(this.part,this.track,this.measure,this.ticks,this.chord,ev));
						i+=ev.getName().length()-1;
					}
				}
				else if(a[i+1]==':') {
					events.add(new ABCLocation(this.part,this.track,this.measure,this.ticks,this.chord,new ABCEvent(this,"::")));
					alignTicks();
					resetScale();
					i+=2;
				}
				else if(a[i+1]==']') {
					events.add(new ABCLocation(this.part,this.track,this.measure,this.ticks,this.chord,new ABCEvent(this,":]")));
					alignTicks();
					i+=2;
				}
				else {
					e=new ABCEvent(this,line.substring(i));
					events.add(new ABCLocation(this.part,this.track,this.measure,this.ticks,this.chord,e));
					i+=e.getName().length();
					this.ticks += e.getTicks();
				}
				break;
			}
		}
		if(this.track==0 && this.measure>=this.measures)
			this.measures=this.measure+1;
	}

	private void setVolume(int volume) {
		if(voice==null) addVoice("1 clef=treble");
		for(int i=this.track;i<voice.size();i++) {
			ABCTrack trk=(ABCTrack) voice.get(i);
			if(i==this.track || trk.getVolume()<0)
				trk.setVolume(volume);
		}
	}

	private void alignTicks() {
		if(this.ticks % this.ticksperbar > 0) {
			int n=this.timeSignature.getNumerator();
			int d=this.timeSignature.getDenominator();
			int ticksperbeat=this.ticksperbar / n;
			while(this.ticks<ticksperbeat) {
				ticksperbeat /= 2;
				d *= 2;
			}
			while((ticksperbeat % 2)==0 &&(this.ticks % ticksperbeat)>0) {
				ticksperbeat /= 2;
				d *= 2;
			}
			if(ticksperbeat>0) {
				n=this.ticks/ticksperbeat;
				ABCEvent et = new ABCEvent(ABCEvent.TIME,n+"/"+d+" \"generated to align\"",0);
				et.setToEnd(false);
				et.setNumerator(n);
				et.setDenominator(d);
				events.add(new ABCLocation(this.part,this.track,this.measure,0,this.chord,et));
				if(chordvoice>=0 && isBuddy(CHORDS)) events.add(new ABCLocation(this.part,chordvoice,this.measure,0,this.chord,et));
				if(basevoice>=0 && isBuddy(BASE)) events.add(new ABCLocation(this.part,basevoice,this.measure,0,this.chord,et));
				if(drumvoice>=0 && isBuddy(DRUMS)) events.add(new ABCLocation(this.part,drumvoice,this.measure,0,this.chord,et));
				if(dronevoice>=0 && isBuddy(DRONE)) events.add(new ABCLocation(this.part,dronevoice,this.measure,0,this.chord,et));
				ABCTimeSignature ts=new ABCTimeSignature(n,d,false);
				addTimeSignatureChange(new ABCTimeSignatureChange(this.measure,ts));
				addTimeSignatureChange(new ABCTimeSignatureChange(this.measure+1,this.timeSignature));
			}
		}
		if(this.ticks>0)
			++this.measure;
		this.ticks=0;
	}

	private boolean isBuddy(String type) {
		ABCTrack ttrk=(ABCTrack) voice.get(this.track);
		String v=ttrk.getName()+"\t"+type;
		for(int i=0;i<voice.size();i++) {
			ABCTrack trk=(ABCTrack)voice.get(i);
			if(v.equalsIgnoreCase(trk.getName())) return true;
		}
		return false;
	}

	private String substituteMacros(String line) {
		for(int i=0;i<macro.size();i++) {
			line=((ABCMacro)macro.get(i)).execute(line);
		}
		return line;
	}

	private String substituteSymbols(String line) {
		for(int i=0;i<redefinable.size();i++) {
			line=((ABCSymbol)redefinable.get(i)).execute(line);
		}
		return line;
	}

	private int beatVolume() {
		if(voice==null) addVoice("1 clef=treble");
		ABCTrack trk=(ABCTrack) voice.get(this.track);
		int v=trk.getVolume();
		int tpb=ticksperbar/timeSignature.getNumerator();
		int b=ticks/tpb;
		if(v<0) v=TGVelocities.DEFAULT;
		if(b<beatstring.length()) {
			char fmp=beatstring.charAt(b);
			int perc=beat["fmp".indexOf(fmp)];
			v *= perc;
			v /= 100;
			if(v>127) v=127;
		}
		return v;
	}

	private ABCEvent tempoCommand(String string) {
		int bpm=120;
		if(string.trim().matches("[0-9]+"))
			bpm=Integer.parseInt(string,10);
		else if(string.trim().matches("[0-9 /]*\\s[0-9]+/[0-9]+\\s*=\\s*[0-9]+")) {
			String eq=string.trim().split("=")[1].trim();
			bpm=Integer.parseInt(eq,10);
		}
		else if(string.trim().matches("\".*\"\\s*[0-9 /]*\\s[0-9]+/[0-9]+\\s*=\\s*[0-9]+")) {
			int i1=string.lastIndexOf('"')+1;
			String eq=string.substring(i1).trim().split("=")[1].trim();
			bpm=Integer.parseInt(eq,10);
		}
		else if(string.trim().matches("[0-9 /]*\\s[0-9]+/[0-9]+\\s*=\\s*[0-9]+\\s*\".*\"")) {
			int i1=string.indexOf('"');
			String eq=string.substring(0,i1).trim().split("=")[1].trim();
			bpm=Integer.parseInt(eq,10);
		}
		else if(string.trim().matches("\".*\"")) {
			ABCTempo tmp = new ABCTempo(string.trim().substring(1,string.trim().length()-1).toLowerCase());
			bpm=tmp.getValue();
		}
		return new ABCEvent(ABCEvent.TEMPO,string,bpm);
	}

	private ABCEvent timeCommand(String string) {
		ABCEvent e=new ABCEvent(ABCEvent.TIME,string,0);
		e.setToEnd(true);
		if(string.matches("[0-9]+/[0-9]+.*")) {
			int numerator=0,denominator=0,i;
			for(i=0;string.charAt(i)!='/';i++) {
				numerator=numerator*10+string.charAt(i)-'0';
			}
			for(i++;i<string.length() && "0123456789".indexOf(string.charAt(i))>=0;i++) {
				denominator=denominator*10+string.charAt(i)-'0';
			}
			e.setNumerator(numerator);
			e.setDenominator(denominator);
			return e;
		}
		if(string.equals("M:C")) {
			e.setNumerator(4);
			e.setDenominator(4);
			return e;
		}
		if(string.equals("M:C|")) {
			e.setNumerator(2);
			e.setDenominator(2);
			return e;
		}
		e.setNumerator(4);
		e.setDenominator(4);
		return e;
	}

	private void advancegchordticks(int t) {
		if(gchordon && t>0 && this.tickspergchord>0 && this.chord != null && this.gchord != null) {
			int i;
			char[] g=this.gchord.toCharArray();
			int n=this.ticks;
			for(i=0;n>0 && i<g.length;i++) {
				if(g[i]>'1' && g[i]<='9') n-=(g[i]-'1')*this.tickspergchord;
				else n-=this.tickspergchord;
			}
			if(i<g.length) {
				if(n==0) {
					generatechordnotes(this.ticks,g,i);
				}
				n+=this.tickspergchord;
				i++;
				if(i<g.length && g[i]>'1' && g[i]<='9') {
					n+=(g[i]-'1')*this.tickspergchord;
					i++;
				}
				n+=this.ticks;
				while(i<g.length && n<t) {
					generatechordnotes(n,g,i);
					n+=this.tickspergchord;
					i++;
					if(i<g.length && g[i]>'1' && g[i]<='9') {
						n+=(g[i]-'1')*this.tickspergchord;
						i++;
					}
				}
			}
		}
		this.ticks = t;
	}

	private void generatechordnotes(int t, char[] g, int i) {
		int saveTrack=this.track;
		boolean newchord=this.chordMeasure==this.measure && this.chordTicks==t;
		ABCEvent e;
		int len=this.tickspergchord;
		char n=g[i];
		++i;
		if(i<g.length &&  g[i]>'1' && g[i]<='9') len+=(g[i]-'1')*this.tickspergchord;
		switch(n) {
		case 'z':
			this.track=chordTrack(saveTrack);
			e=new ABCEvent(this,newchord?this.chord.getNote('f'):"z");
			e.setTicks(len);
			e.setVelocity(0);
			events.add(new ABCLocation(this.part,this.track,this.measure,t,this.chord,e));
			this.track=baseTrack(saveTrack);
			e=new ABCEvent(this,"z");
			e.setTicks(len);
			e.setVelocity(bassvol);
			events.add(new ABCLocation(this.part,this.track,this.measure,t,this.chord, e));
			break;
		case 'f':
			this.track=chordTrack(saveTrack);
			e=new ABCEvent(this,newchord?this.chord.getNote('f'):"z");
			e.setTicks(len);
			e.setVelocity(0);
			events.add(new ABCLocation(this.part,this.track,this.measure,t,this.chord,e));
			this.track=baseTrack(saveTrack);
			e=new ABCEvent(this,this.chord.getNote('f')+",");
			e.setTicks(len);
			e.setVelocity(bassvol);
			events.add(new ABCLocation(this.part,this.track,this.measure,t,this.chord,e));
			break;
		case 'g':
		case 'h':
		case 'i':
		case 'j':
			this.track=chordTrack(saveTrack);
			e=new ABCEvent(this,this.chord.getNote(n));
			e.setTicks(len);
			e.setVelocity(chordvol);
			events.add(new ABCLocation(this.part,this.track,this.measure,t,this.chord,e));
			this.track=baseTrack(saveTrack);
			e=new ABCEvent(this,"z");
			e.setTicks(len);
			events.add(new ABCLocation(this.part,this.track,this.measure,t,this.chord,e));
			break;
		case 'G':
		case 'H':
		case 'I':
		case 'J':
			this.track=chordTrack(saveTrack);
			e=new ABCEvent(this,newchord?this.chord.getNote('f'):"z");
			e.setTicks(len);
			e.setVelocity(0);
			events.add(new ABCLocation(this.part,this.track,this.measure,t,this.chord,e));
			this.track=baseTrack(saveTrack);
			e=new ABCEvent(this,this.chord.getNote(n));
			e.setTicks(len);
			e.setVelocity(bassvol);
			events.add(new ABCLocation(this.part,this.track,this.measure,t,this.chord,e));
			break;
		case 'b':
			this.track=baseTrack(saveTrack);
			e=new ABCEvent(this,this.chord.getNote('f')+",");
			e.setTicks(len);
			e.setVelocity(bassvol);
			events.add(new ABCLocation(this.part,this.track,this.measure,t,this.chord,e));
		case 'c':
			if(n=='c') {
				this.track=baseTrack(saveTrack);
				e=new ABCEvent(this,"z");
				e.setTicks(len);
				events.add(new ABCLocation(this.part,this.track,this.measure,t,this.chord,e));
			}
			this.track=chordTrack(saveTrack);
			for(int c=0;c<this.chord.size();c++) {
				e=new ABCEvent(this,this.chord.getNote((char) ('g'+c)));
				e.setTicks(len);
				e.setVelocity(chordvol);
				events.add(new ABCLocation(this.part,this.track,this.measure,t,this.chord,e));
			}
			remapChord(this.chord.size());
		}
		this.track=saveTrack;
	}

	private void remapChord(int sz) {
		ABCEvent[] e=new ABCEvent[sz];
		int[] snares=getTracks()[this.track].getStrings();
		for(int i=0;i<sz;i++) e[i]=((ABCLocation)events.get(events.size()-sz+i)).getEvent();
		int[] frets=new int[snares.length];
		for(int i=0;i<frets.length;i++) frets[i]=-1;
		for(int i=0;i<sz;i++) {
			if(e[i].getType()==ABCEvent.NOTE) placeNote(i,e,frets, snares);
		}
	}

	private void placeNote(int i, ABCEvent[] e, int[] frets, int[] strings) {
			int s=e[i].getString();
			if(frets[s]<0) frets[s]=i;
			else {
				int j=frets[s];
				int fi=e[i].getFret();
				int fj=e[j].getFret();
				int fn=5;
				if(s==2) fn=4;
				if(fi<fj) {
					if(s>0 && fj>=fn && frets[s-1]<0) {
						frets[s]=i;
						frets[s-1]=j;
						e[j].alterString(s-1, strings);
					}
					else if(s<frets.length-1) {
						e[i].alterString(s+1, strings);
						placeNote(i,e,frets, strings);
					}
					else e[i].setType(ABCEvent.NOT_RELEVANT);
				}
				else {
					if(s>0 && fi>=fn && frets[s-1]<0) {
						frets[s-1]=i;
						e[i].alterString(s-1, strings);
					}
					else if(s<strings.length-1) {
						frets[s]=i;
						e[j].alterString(s+1, strings);
						placeNote(j,e,frets, strings);
					}
					else e[j].setType(ABCEvent.NOT_RELEVANT);
				}
			}
		
	}

	private int baseTrack(int t) {
		if(basevoice<0) basevoice=buddyTrack(t,BASE,4, TGMeasure.CLEF_BASS, getBassprog());
		return basevoice;
	}

	private int chordTrack(int t) {
		if(chordvoice<0) chordvoice=buddyTrack(t,CHORDS,6, TGMeasure.CLEF_TREBLE, getChordprog());
		return chordvoice;
	}
	
	private int drumTrack(int t) {
		if(drumvoice<0) drumvoice=buddyTrack(t,DRUMS,6, TGMeasure.CLEF_BASS, 0);
		return drumvoice;
	}
	
	private int droneTrack(int t) {
		if(dronevoice<0) dronevoice=buddyTrack(t,DRONE,2, TGMeasure.CLEF_BASS, getDroneprog());
		ABCTrack ttrk=(ABCTrack) voice.get(dronevoice);
		final int[] s={ 40, 28 };
		ttrk.setStrings(s);
		return dronevoice;
	}
	
	private int buddyTrack(int t, String type, int strings, int cleftype, int instrument) {
		ABCTrack ttrk=(ABCTrack) voice.get(t);
		String v=ttrk.getName()+"\t"+type;
		for(int i=0;i<voice.size();i++) {
			ABCTrack trk=(ABCTrack)voice.get(i);
			if(v.equalsIgnoreCase(trk.getName())) return i;
		}
		int i=voice.size();
		ttrk=ttrk.clone(v,strings);
		ttrk.setClefType(cleftype);
		ttrk.setInstrument(instrument);
		this.voice.add(ttrk);
		return i;
	}

	public int getVoice(String string) {
		if(this.voice==null) {
			this.voice=new ArrayList<ABCTrack>();
			this.voiceloc=new ArrayList<ABCLocation>();
		}
		for(int i=0;i<voice.size();i++) {
			ABCTrack trk=(ABCTrack)voice.get(i);
			if(string.equalsIgnoreCase(trk.getName())) return i;
		}
		this.voice.add(new ABCTrack(string.trim()));
		ABCLocation loc = new ABCLocation(this.part,this.track,0,0,null,null);
		loc.setTied(false);
		loc.setChord(null);
		this.voiceloc.add(loc);
		return voice.size()-1;
	}

	public String getBarkey() {
		return this.scale;
	}

	public void setBarkey(String scale) {
		this.scale=scale;
	}

	/**
	 * @return the events
	 */
	public List<ABCLocation> getEvents() {
		return events;
	}

	public int addChord(String name) {
		if(this.chordsList==null) this.chordsList=new ArrayList<ABCChord>();
		for(int i=0;i<chordsList.size();i++) {
			ABCChord chord=(ABCChord) chordsList.get(i);
			if(chord.getName().equals(name))
				return i;
		}
		chordsList.add(new ABCChord(name));
		return chordsList.size()-1;
	}

	/**
	 * @return the bassprog
	 */
	public int getBassprog() {
		return bassprog;
	}

	/**
	 * @param bassprog the bassprog to set
	 */
	public void setBassprog(int bassprog) {
		this.bassprog = bassprog;
		if(basevoice<0) return;
		ABCEvent e=new ABCEvent(ABCEvent.BASSPROG,"",bassprog);
		events.add(new ABCLocation(this.part,baseTrack(this.track),this.measure,this.ticks,this.chord,e));
	}

	/**
	 * @return the bassvol
	 */
	public int getBassvol() {
		return bassvol;
	}

	/**
	 * @param bassvol the bassvol to set
	 */
	public void setBassvol(int bassvol) {
		this.bassvol = bassvol;
		if(basevoice<0) return;
		ABCEvent e=new ABCEvent(ABCEvent.BASSVOL,"",bassvol);
		events.add(new ABCLocation(this.part,baseTrack(this.track),this.measure,this.ticks,this.chord,e));
	}

	/**
	 * @return the chordprog
	 */
	public int getChordprog() {
		return chordprog;
	}

	/**
	 * @param chordprog the chordprog to set
	 */
	public void setChordprog(int chordprog) {
		this.chordprog = chordprog;
		if(chordvoice<0) return;
		ABCEvent e=new ABCEvent(ABCEvent.CHORDPROG,"",chordprog);
		events.add(new ABCLocation(this.part,chordTrack(this.track),this.measure,this.ticks,this.chord,e));
	}

	/**
	 * @return the chordvol
	 */
	public int getChordvol() {
		return chordvol;
	}

	/**
	 * @param chordvol the chordvol to set
	 */
	public void setChordvol(int chordvol) {
		this.chordvol = chordvol;
		if(chordvoice<0) return;
		ABCEvent e=new ABCEvent(ABCEvent.CHORDVOL,"",chordvol);
		events.add(new ABCLocation(this.part,chordTrack(this.track),this.measure,this.ticks,this.chord,e));
	}

	/**
	 * @return the drone
	 */
	public String getDrone() {
		return drone;
	}

	/**
	 * @param drone the drone to set
	 */
	public void setDrone(String drone) {
		this.drone = drone;
		if(dronevoice<0) return;
		ABCEvent e=new ABCEvent(ABCEvent.DRONE,drone,0);
		events.add(new ABCLocation(this.part,dronevoice,this.measure,this.ticks,this.chord,e));
	}

	/**
	 * @return the droneon
	 */
	public boolean isDroneon() {
		return droneon;
	}

	/**
	 * @param droneon the droneon to set
	 */
	public void setDroneon(boolean droneon) {
		this.droneon = droneon;
		if(dronevoice<0) return;
		if(dronevoice<0) {
			if(!droneon) return;
			dronevoice=droneTrack(this.track);
			if(drone!=null) setDrone(drone);
		}
		ABCEvent e=new ABCEvent(droneon?ABCEvent.DRONEON:ABCEvent.DRONEOFF,"[I:MIDI=droneo"+(gchordon?"n]":"ff]"),0);
		if(this.events==null) this.events = new ArrayList<ABCLocation>();
		events.add(new ABCLocation(this.part,dronevoice,this.measure,this.ticks,this.chord,e));
	}

	private int getDroneprog() {
		if(drone!=null) {
			String[] a=drone.split("\\s+");
			if(a.length>0 && a[0].matches("[0-9]+")) {
				int i=Integer.parseInt(a[0], 10)-instrumentOffset;
				if(i<0) {
					i=0;
					instrumentOffset=0;
				}
				if(i>127) {
					i=127;
					instrumentOffset=1;
				}
				return i;
			}
		}
		return 0;
	}

	/**
	 * @return the drum
	 */
	public String getDrum() {
		return drum;
	}

	/**
	 * @param drum the drum to set
	 * <p>
	 * %%MIDI drum dzddd2dz 35 39 39 35 39 127 80 80 127 80
	 * % Bass Drum 1 + Electric Snare
	 */
	public void setDrum(String drum) {
		this.drum = drum;
		this.drumnotes=initDrumnotes();
		if(drumnotes==null) return;
		if(drumvoice<0) {
			if(!drumon) return;
			if(events==null) return;
			drumvoice=drumTrack(this.track);
			setDrumon(true);
		}
		ABCEvent e=new ABCEvent(ABCEvent.DRUM,drum,0);
		if(events==null) events=new ArrayList<ABCLocation>();
		events.add(new ABCLocation(this.part,drumvoice,this.measure,this.ticks,this.chord,e));
	}

	/**
	 * @return the drumon
	 */
	public boolean isDrumon() {
		return drumon;
	}

	/**
	 * @param drumon the drumon to set
	 */
	public void setDrumon(boolean drumon) {
		this.drumon = drumon;
		if(drumvoice<0) {
			if(!drumon) return;
			drumvoice=drumTrack(this.track);
			if(drum!=null) setDrum(drum);
		}
		ABCEvent e=new ABCEvent(drumon?ABCEvent.DRUMON:ABCEvent.DRUMOFF,"[I:MIDI=drumo"+(gchordon?"n]":"ff]"),0);
		events.add(new ABCLocation(this.part,drumvoice,this.measure,this.ticks,this.chord,e));
	}

	/**
	 * @return the gchord
	 */
	public String getGchord() {
		return gchord;
	}

	/**
	 * @param gchord the gchord to set
	 */
	public void setGchord(String gchord) {
		this.gchord = gchord;
		this.tickspergchord=getTickspergchord();
		if(chordvoice<0) return;
		ABCEvent e=new ABCEvent(ABCEvent.GCHORD,gchord,0);
		events.add(new ABCLocation(this.part,chordTrack(this.track),this.measure,this.ticks,this.chord,e));
	}

	/**
	 * @return the gchordon
	 */
	public boolean isGchordon() {
		return gchordon;
	}

	/**
	 * @param gchordon the gchordon to set
	 */
	public void setGchordon(boolean gchordon) {
		this.gchordon = gchordon;
		if(chordvoice<0) return;
		ABCEvent e=new ABCEvent(gchordon?ABCEvent.GCHORDON:ABCEvent.GCHORDOFF,"[I:MIDI=gchordo"+(gchordon?"n]":"ff]"),0);
		events.add(new ABCLocation(this.part,chordvoice,this.measure,this.ticks,this.chord,e));
	}

	public int getTrack() {
		return this.track;
	}

	/**
	 * @return the keySignature
	 */
	public int getKeySignature() {
		return keySignature;
	}

	public void midiCommand(String line) {
		String[] a=line.split("\\s+");
		int v=-1;
		int i=1;
		while(i<a.length) {
			if(a[i].equalsIgnoreCase("voice")) {
				if(i<a.length-1) v=getVoice(a[i+1]);
				++i;
			}
			if(a[i].equalsIgnoreCase("program")) {
				if(i<a.length-2 && a[i+1].matches("[0-9]+")) {
					v=Integer.parseInt(a[i+1], 10)-1;
					++i;
				}
				if(v<0) v=getTrack();
				if(v>=0 && i<a.length-1 && a[i+1].matches("[0-9]+")) {
					int instrument=Integer.parseInt(a[i+1], 10) - instrumentOffset;	// ABC instruments start at 1
					if(instrument<0) {
						instrument=0;
						instrumentOffset=0;
					}
					if(instrument>127) {
						instrument=127;
						instrumentOffset=1;
					}
					getTracks()[v].setInstrument(instrument); 
				}
				++i;
			}
			else if(v>=0 && a[i].toLowerCase().matches("instrument=[0-9]+")) {
				int instrument=Integer.parseInt(a[i].split("=")[1], 10) - instrumentOffset;	// ABC instruments start at 1
				if(instrument<0) {
					instrument=0;
					instrumentOffset=0;
				}
				if(instrument>127) {
					instrument=127;
					instrumentOffset=1;
				}
				getTracks()[v].setInstrument(instrument); 
			}
			else if(a[i].equalsIgnoreCase("gchord")) {
				if(i<a.length-1) setGchord(a[i+1]);
				++i;
			}
			else if(a[i].equalsIgnoreCase("chordprog")) {
				if(i<a.length-1 && a[i+1].matches("[0-9]+")) {
					int instrument=Integer.parseInt(a[i+1], 10) - instrumentOffset;	// ABC instruments start at 1
					if(instrument<0) {
						instrument=0;
						instrumentOffset=0;
					}
					if(instrument>127) {
						instrument=127;
						instrumentOffset=1;
					}
					setChordprog(instrument);
				}
				++i;
			}
			else if(a[i].equalsIgnoreCase("chordvol")) {
				if(i<a.length-1 && a[i+1].matches("[0-9]+")) setChordvol(Integer.parseInt(a[i+1], 10));
				++i;
			}
			else if(a[i].equalsIgnoreCase("bassprog")) {
				if(i<a.length-1 && a[i+1].matches("[0-9]+")) {
					int instrument=Integer.parseInt(a[i+1], 10) - instrumentOffset;	// ABC instruments start at 1
					if(instrument<0) {
						instrument=0;
						instrumentOffset=0;
					}
					if(instrument>127) {
						instrument=127;
						instrumentOffset=1;
					}
					setBassprog(instrument);
				}
				++i;
			}
			else if(a[i].equalsIgnoreCase("bassvol")) {
				if(i<a.length-1 && a[i+1].matches("[0-9]+")) setBassvol(Integer.parseInt(a[i+1], 10));
				++i;
			}
			else if(a[i].equalsIgnoreCase("drum")) {
				String d="";
				for(i+=1;i<a.length;i++) d+=a[i]+" ";
				setDrum(d.trim());
			}
			else if(a[i].equalsIgnoreCase("beat")) {
				String d="";
				for(i+=1;i<a.length;i++) d+=a[i]+" ";
				setBeat(d.trim());
			}
			else if(a[i].equalsIgnoreCase("beatstring")) {
				if(i<a.length-1 && a[i+1].toLowerCase().matches("[fmp]+")) setBeatstring(a[i+1].toLowerCase());
				++i;
			}
			else if(a[i].equalsIgnoreCase(DRONE)) {
				String d="";
				for(i+=1;i<a.length;i++) d+=a[i]+" ";
				setDrone(d.trim());
			}
			else if(a[i].equalsIgnoreCase("gchordon")) setGchordon(true);
			else if(a[i].equalsIgnoreCase("gchordoff")) setGchordon(false);
			else if(a[i].equalsIgnoreCase("drumon")) setDrumon(true);
			else if(a[i].equalsIgnoreCase("drumoff")) setDrumon(false);
			else if(a[i].equalsIgnoreCase("droneon")) setDroneon(true);
			else if(a[i].equalsIgnoreCase("droneoff")) setDroneon(false);
			++i;
		}
	}

	/*	
	 * MIDI files usually sound artificial and expressionless, but there are several ways to improve them. 
	 * The command %%MIDI beatstring fmp provides a way of specifying where the strong, medium and weak
	 * stresses fall within a bar.
	 *   f indicates a strong beat, m a medium beat, and p a soft beat.
	 * For example, let's consider an Irish jig, which has a 6/8 time.
	 * The corresponding fmp sequence would be fppmpp.
	 */ 
	private void setBeatstring(String string) {
		if(string.matches("[fmp]+")) beatstring=string;
	}

	/*	
	 * To fine-grain the volume of the single notes in a measure, the
	 *  %%MIDI beat <vol1> <vol2> <vol3> <pos>
	 * command can be used. vol1, vol2, and vol3 specify the volume of notes that fall on a strong,
	 * medium, and weak beat, while pos indicates the position of strong beats in the measure.
	 * We interpret it as percentages of the track volume, the first beat (pos=0) is always a 
	 * strong beat and pos indicates the medium beat
	*/
	private void setBeat(String string) {
		if(string.matches("[0-9]+\\s+[0-9]+\\s+[0-9]+\\s+[0-9]+")) {
			if(beat==null) beat=new int[4];
			String[] a=string.split("\\s+");
			for(int i=0;i<4;i++) beat[i]=Integer.parseInt(a[i],10);
			if(beatstring!=null && beat[3]>0 && beatstring.length()>beat[3])
				beatstring=beatstring.substring(0,beat[3])+"m"+beatstring.substring(beat[3]+1);
		}
	}

	/**
	 * @param instrumentOffset the instrumentOffset to set
	 */
	public void setInstrumentOffset(int instrumentOffset) {
		this.instrumentOffset = instrumentOffset;
	}

	public void postprocessor() {
		if(events==null) return;
		int sz=events.size();
		if(this.ticks==0) this.measures--;
		((ABCTrack)voice.get(0)).setPan(8);
		if(chordvoice>=0) generatechordtracks(sz);
		if(dronevoice>=0) generatedronetrack(sz);
		if(drumvoice>=0) generatedrumtrack(sz);
	}

	public void sortEvents() {
		if(getEvents()==null) return;
		if(this.sorted) return;
		Collections.sort(getEvents(),new Comparator<ABCLocation>() {
			public int compare(ABCLocation c1, ABCLocation c2) {
				if( c1 != null ){
					return c1.compareTo(c2);
				}
				if( c2 != null ){
					return 0 - c2.compareTo(c1);
				}
				return 0;
			}
		});
		this.sorted=true;
	}

	private void generatedronetrack(int sz) {
		sortEvents();
		measure=0;
		ticks=0;
		this.dronenotes=initDronenotes();
		part=null;
		droneon=false;
		tied=false;
		for(int i=0;i<sz;i++) {
			ABCLocation loc=(ABCLocation) events.get(i);
			if(loc.getTrack()==dronevoice) {
				ABCEvent e=loc.getEvent();
				if(droneon && drone!=null) {
					int m=loc.getMeasure();
					while(measure<m) {
						advancedroneticks(ticksperbar);
						++measure;
						ticks=0;
					}
					part=loc.getPart();
					advancedroneticks(loc.getTicks());
				}
				else {
					measure=loc.getMeasure();
					ticks=loc.getTicks();
				}
				switch(e.getType()) {
				case ABCEvent.DRONEOFF: droneon=false;tied=false;break;
				case ABCEvent.DRONEON:  droneon=true;break;
				case ABCEvent.DRONE:
					drone=e.getParm();
					this.dronenotes=initDronenotes();
					break;
				case ABCEvent.TIME:
					ticksperbar = (e.getNumerator() * 4 * TICKS_PER_QUART)/e.getDenominator();
					this.dronenotes=initDronenotes();
					break;
				}
			}
		}
		if(droneon && drone!=null) {
			while(measure<measures-1) {
				advancedroneticks(ticksperbar);
				++measure;
				ticks=0;
			}
		}
	}

	private void advancedroneticks(int t) {
		if(droneon && t>0 && this.dronenotes != null) {
			for(int i=0;i<this.dronenotes.length;i++) {
				ABCEvent e=(ABCEvent) dronenotes[i].clone();
				e.setTicks(t-this.ticks);
				e.setTied(this.tied);
				events.add(new ABCLocation(this.part,dronevoice,this.measure,this.ticks,null,e));
			}
			this.tied=true;
		}
		this.ticks = t;
	}

	private ABCEvent[] initDronenotes() {
		if(drone==null || !drone.matches("[0-9]+\\s+[0-9]+\\s+[0-9]+\\s+[0-9]+\\s+[0-9]+")) return null;
		ABCEvent[] e=new ABCEvent[2];
		String[] a=this.drone.split("\\s+");
		int[] strings=((ABCTrack)voice.get(dronevoice)).getStrings();
		int i=Integer.parseInt(a[0],10)-instrumentOffset;
		if(i<0) {
			i=0;
			instrumentOffset=0;
		}
		if(i>127) {
			i=127;
			instrumentOffset=1;
		}
		((ABCTrack)voice.get(dronevoice)).setInstrument(i);
		for(int n=0;n<e.length;n++) {
			e[n]=new ABCEvent(ABCEvent.NOTE,"D",0);
			e[n].setPitchStringAndFret(Integer.parseInt(a[1+n],10), strings);
			e[n].setVelocity(Integer.parseInt(a[3+n],10));
			e[n].setTicks(this.ticksperbar);
			e[n].setTied(this.tied);
		}
		return e;
	}

	private void generatedrumtrack(int sz) {
		sortEvents();
		measure=0;
		ticks=0;
		drum=null;
		part=null;
		drumon=true;
		for(int i=0;i<sz;i++) {
			ABCLocation loc=(ABCLocation) events.get(i);
			if(loc.getTrack()==drumvoice) {
				ABCEvent e=loc.getEvent();
				if(drumon && drum!=null) {
					int m=loc.getMeasure();
					while(measure<m) {
						advancedrumticks(ticksperbar);
						++measure;
						ticks=0;
					}
					part=loc.getPart();
					advancedrumticks(loc.getTicks());
				}
				else {
					measure=loc.getMeasure();
					ticks=loc.getTicks();
				}
				switch(e.getType()) {
				case ABCEvent.DRUMOFF: drumon=false;break;
				case ABCEvent.DRUMON:  drumon=true;break;
				case ABCEvent.DRUM:
					drum=e.getParm();
					this.drumnotes=initDrumnotes();
					break;
				case ABCEvent.TIME:
					ticksperbar = (e.getNumerator() * 4 * TICKS_PER_QUART)/e.getDenominator();
					this.drumnotes=initDrumnotes();
					break;
				}
			}
		}
		if(drumon && drum!=null) {
			while(measure<measures-1) {
				advancedrumticks(ticksperbar);
				++measure;
				ticks=0;
			}
		}
	}

	private ABCEvent[] initDrumnotes() {
		if(drum==null) return null;
		String[] a=drum.split("\\s+");
		for(int i=1;i<a.length;i++) {
			if(!a[i].matches("[0-9]+"))
				return null;
		}
		char[] d=a[0].toCharArray();
		int n=0;
		int notes=0;
		int events=0;
		for(int i=0;i<d.length;i++) {
			if(d[i]>'1' && d[i]<='9') n+=d[i]-'1';
			else {
				n++;
				events++;
				if(d[i]=='d') notes++;
			}
		}
		if(n<1 || notes<1 || a.length < 2*notes+1) return null;
		int ticksperdrum=ticksperbar/n;
		ABCEvent[] e=new ABCEvent[events];
		int[] drumstrings;
		if(drumvoice<0) drumstrings=new ABCTrack("dummy\tdrums").getStrings(); 
		else drumstrings=((ABCTrack)voice.get(drumvoice)).getStrings();
		n=0;
		int note=0;
		events=0;
		for(int i=0;i<d.length;i++) {
			if(d[i]>'1' && d[i]<='9') {
				n=d[i]-'0';
				e[events-1].setTicks(ticksperdrum*n);
			}
			else {
				if(d[i]=='d') {
					e[events]=new ABCEvent(this,"d");
					e[events].setPitchStringAndFret(Integer.parseInt(a[note+1], 10)+9,drumstrings);
					e[events].setVelocity(Integer.parseInt(a[note+notes+1], 10));
					note++;
				}
				else
					e[events]=new ABCEvent(this,"z");
				e[events].setTicks(ticksperdrum);
				events++;
			}
		}
		return e;
	}

	private void advancedrumticks(int t) {
		if(drumon && t>0 && this.drumnotes != null) {
			int i;
			int n=this.ticks;
			for(i=0;n>0 && i<drumnotes.length;i++) {
				n-=drumnotes[i].getTicks();
			}
			if(i<drumnotes.length) {
				if(n==0) {
					events.add(new ABCLocation(this.part,drumvoice,this.measure,this.ticks,null,drumnotes[i]));
				}
				n+=drumnotes[i].getTicks();
				n+=this.ticks;
				i++;
				while(i<drumnotes.length && n<t) {
					events.add(new ABCLocation(this.part,drumvoice,this.measure,n,null,drumnotes[i]));
					n+=drumnotes[i].getTicks();
					i++;
				}
			}
		}
		this.ticks = t;
	}

	private void generatechordtracks(int sz) {
		ABCTrack cTrk=(ABCTrack) voice.get(chordvoice);
		ABCTrack bTrk=(ABCTrack) voice.get(basevoice);
		sortEvents();
		measure=0;
		ticks=0;
		chord=null;
		part=null;
		gchordon=true;
		for(int i=0;i<sz;i++) {
			ABCLocation loc=(ABCLocation) events.get(i);
			if(loc.getTrack()==chordvoice) {
				ABCEvent e=loc.getEvent();
				if(gchordon && chord!=null && gchord!=null) {
					int m=loc.getMeasure();
					while(measure<m) {
						advancegchordticks(ticksperbar);
						++measure;
						ticks=0;
					}
					part=loc.getPart();
					advancegchordticks(loc.getTicks());
				}
				else {
					measure=loc.getMeasure();
					ticks=loc.getTicks();
				}
				switch(e.getType()) {
				case ABCEvent.CHORD_SYMBOL:
					if(gchordon) {
						chord=(ABCChord) chordsList.get(e.getChordnum());
						chordMeasure = this.measure;
						chordTicks = this.ticks;
					}
					break;
				case ABCEvent.GCHORDOFF: gchordon=false;break;
				case ABCEvent.GCHORDON:  gchordon=true;break;
				case ABCEvent.GCHORD:    gchord=e.getParm();tickspergchord=getTickspergchord();break;
				case ABCEvent.CHORDPROG: chordprog=e.getValue();break;
				case ABCEvent.CHORDVOL:  chordvol=e.getValue();cTrk.setVolume(chordvol);break;
				case ABCEvent.BASSPROG:  bassprog=e.getValue();break;
				case ABCEvent.BASSVOL:   bassvol=e.getValue();bTrk.setVolume(bassvol);break;
				case ABCEvent.TIME:
					ticksperbar = (e.getNumerator() * 4 * TICKS_PER_QUART)/e.getDenominator();
					tickspergchord=getTickspergchord();
					break;
				}
			}
		}
		if(gchordon && chord!=null && gchord!=null) {
			while(measure<measures-1) {
				advancegchordticks(ticksperbar);
				++measure;
				ticks=0;
			}
		}
	}

	public void initBody() {
		if(droneon && dronevoice<0) {
			dronevoice=droneTrack(this.track);
			setDroneon(true);
		}
		if(drumon && drumvoice<0) {
			drumvoice=drumTrack(this.track);
			setDrumon(true);
			if(drum!=null) setDrum(drum);
		}
		if(this.rhythm==null) {
			int n=this.timeSignature.getNumerator();
			int d=this.timeSignature.getDenominator();
			if(n==6 && d==8) setRhythm("jig");
			else if(n==4 && d==4) setRhythm("reel");
			else if(n==3 && d==4) setRhythm("waltz");
			else if(n==2 && d==4) setRhythm("mars");
		}
		if(this.beat==null) {
			int n=this.timeSignature.getNumerator();
			switch(n) {
			case 6: setBeat("105 90 60 3");break; // jig
			case 4: setBeat("105 90 60 2");break; // reel
			case 3: setBeat("105 90 60 1");break; // waltz
			case 2: setBeat("105 90 60 0");break;	// mars
			case 5: setBeat("105 90 60 2");break;
			case 12: setBeat("105 90 60 6");break;
			default: setBeat("105 90 60 0");break;
			}
		}
		if(this.beatstring==null) {
			int n=this.timeSignature.getNumerator();
			switch(n) {
			case 6: setBeatstring("fppmpp");break;
			case 4: setBeatstring("fpmp");break;
			case 3: setBeatstring("fmp");break;
			case 2: setBeatstring("fp");break;
			case 5: setBeatstring("fpmpp");break;
			case 12: setBeatstring("fppmppmppmpp");break;
			default:
				String s="";
				for(;n>3;n-=3) s+="mpp";
				setBeatstring("fpp".substring(0,n)+s);
				break;
			}
			if(beat!=null && beat[3]>0 && beatstring.length()>beat[3])
				beatstring=beatstring.substring(0,beat[3])+"m"+beatstring.substring(beat[3]+1);
		}
	}

	public boolean isHornpipe() {
		return "hornpipe".equalsIgnoreCase(this.rhythm);
	}
	
	public void initHead() {
		this.setTimeSignature(new ABCTimeSignature(4,4,true));
		this.setDefaultNoteLength(new ABCTimeSignature(1,8,false));
		this.setTempo("1/4=120");
		this.beatstring=null;
		this.beat=null;
		addRedefinable("~ = +roll+");
		addRedefinable("T = +trill+");
		addRedefinable("H = +fermata+");
		addRedefinable("L = +emphasis+");
		addRedefinable("M = +lowermordent+");
		addRedefinable("P = +uppermordent+");
		addRedefinable("S = +segno+");
		addRedefinable("O = +coda+");
		addRedefinable("u = +upbow+");
		addRedefinable("v = +downbow+");
		this.drone="71 45 33 80 80"; // Bassoon A,, A,,, vol1 vol2
	}

	public int computePitch(ABCEvent event, char[] a) {
		int i=0;
		int p=0;
		int pitch;
		int propagate=NOT;
		String barkey=getBarkey();
		while(a[i]=='^') {
			propagate=this.propagate_accidentals;
			i++;
			p++;
		}
		while(a[i]=='_') {
			propagate=this.propagate_accidentals;
			i++;
			p--;
		}
		while(a[i]=='=') {
			propagate=this.propagate_accidentals;
			i++;
			p=0;
		}
		boolean hasaccidentals=i>0;
		int x=barkey.indexOf(a[i]);
		int y=p+" C D EF G A Bc d ef g a b ".indexOf(a[i]);
		if(propagate==PITCH || x<0) {
			if(x!=y) {
				char[] b=barkey.toCharArray();
				if(x>=0) b[x]=' ';
				b[y]=a[i];
				if("abcdefg".indexOf(a[i])<0) {
					if(x>=0) b[x+12]=' ';
					b[y+12]=String.copyValueOf(a, i, 1).toLowerCase().charAt(0);
				}
				else {
					if(x>=0) b[x-12]=' ';
					b[y-12]=String.copyValueOf(a, i, 1).toUpperCase().charAt(0);
				}
				barkey=String.copyValueOf(b);
				setBarkey(barkey);
			}
		}
		if(hasaccidentals) pitch=y+71;
		else	pitch=x+71;
		++i;
		while(a[i]=='\'') {
			pitch+=12;
			i++;
		}
		while(a[i]==',') {
			pitch-=12;
			i++;
		}
		if(propagate_accidentals==OCTAVE) {
			if(octaveDatabase==null) octaveDatabase=new ABCOctaveDatabase();
			if(hasaccidentals) octaveDatabase.store(y-x,pitch);
			else pitch+=octaveDatabase.recall(pitch);
		}
		event.setPitchStringAndFret(pitch, null);
		return i-1;
	}

	/**
	 * @param line
	 */
	public void directive(String line) {
		if(line.startsWith("%%propagate-accidentals")) {
//			%%propagate-accidentals not | octave | pitch
//			When set to not, accidentals apply only to the note they're attached to.
//			When set to octave, accidentals also apply to all the notes of the same pitch in the same octave up to the end of the bar. 
//			When set to pitch, accidentals also apply to all the notes of the same pitch in all octaves up to the end of the bar. 
//			The default value is pitch.
			String[] option=line.split("\\s+");
			if(option.length>1) {
				if(option[1].equals("not")) this.propagate_accidentals = NOT;
				else if(option[1].equals("octave")) this.propagate_accidentals = OCTAVE;
				else if(option[1].equals("pitch")) this.propagate_accidentals = PITCH;
			}
		}
	}

}
