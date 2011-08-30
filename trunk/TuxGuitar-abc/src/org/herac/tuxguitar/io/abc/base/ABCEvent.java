/**
 * 
 */
package org.herac.tuxguitar.io.abc.base;

import java.util.ArrayList;

/**
 * @author peter
 *
 */
public class ABCEvent implements Comparable {
	// types
	public static final int BAR = 0;
	public static final int BAR_BAR = 1;
	public static final int BAR_FATBAR = 2;
	public static final int FATBAR_BAR = 3;
	public static final int REPEAT_BEGIN = 4;
	public static final int REPEAT_END = 5;
	public static final int REPEAT_END_AND_START = 6;
	public static final int VARIANT = 7;
	public static final int TIME = 8;
	public static final int TEMPO = 9;
	public static final int CHORD_SYMBOL = 10;
	public static final int ANNOTATION = 11;
	public static final int CHORD_BEGIN = 12;
	public static final int REST = 13;
	public static final int NOTE = 14;
	public static final int CHORD_CLOSE = 15;
	public static final int DECORATION = 16;
	public static final int LINE_BREAK = 17;
	public static final int NOT_RELEVANT = 18;
	// decoration (stored in the pitch)
	public static final int TRILL = 1;
	public static final int LOWERMORDENT = 2;
	public static final int UPPERMORDENT = 3;
	public static final int ACCENT = 4;
	public static final int FERMATA = 5;
	public static final int INVERTEDFERMATA = 6;
	public static final int TENUTO = 7;
	public static final int FINGERING_0 = 8;
	public static final int FINGERING_1 = 9;
	public static final int FINGERING_2 = 10;
	public static final int FINGERING_3 = 11;
	public static final int FINGERING_4 = 12;
	public static final int FINGERING_5 = 13;
	public static final int PLUS = 14;
	public static final int WEDGE = 15;
	public static final int OPEN = 16;
	public static final int THUMB = 17;
	public static final int TURN = 18;
	public static final int ROLL = 19;
	public static final int BREATH = 20;
	public static final int SHORTPHRASE = 21;
	public static final int MEDIUMPHRASE = 22;
	public static final int LONGPHRASE = 23;
	public static final int SEGNO = 24;
	public static final int DS = 25;
	public static final int DSS = 26;
	public static final int DC = 27;
	public static final int DACODA = 28;
	public static final int DACAPO = 29;
	public static final int ALCODA = 30;
	public static final int TOCODA = 31;
	public static final int ALFINE = 32;
	public static final int FINE = 33;
	public static final int CODA = 34;
	public static final int STARTCRESCENDO = 35;
	public static final int ENDCRESCENDO = 36;
	public static final int STARTDIMINUENDO = 37;
	public static final int ENDDIMINUENDO = 38;
	public static final int PPPP = 39;
	public static final int PPP = 40;
	public static final int PP = 41;
	public static final int P = 42;
	public static final int MP = 43;
	public static final int MF = 44;
	public static final int F = 45;
	public static final int FF = 46;
	public static final int FFF = 47;
	public static final int FFFF = 48;
	public static final int SFZ = 49;
	public static final int UPBOW = 50;
	public static final int DOWNBOW = 51;
	public static final int SLIDE = 52;
	public static final int TURNX = 53;
	public static final int INVERTEDTURN = 54;
	public static final int INVERTEDTURNX = 55;
	public static final int ARPEGGIO = 56;
	public static final int STARTTRILL = 57;
	public static final int ENDTRILL = 58;
	public static final int STACATODOT = 59;
	public static final int GCHORDON = -1;
	public static final int GCHORDOFF = -2;
	public static final int GCHORD = -3;
	public static final int CHORDPROG = -4;
	public static final int CHORDVOL = -5;
	public static final int BASSPROG = -6;
	public static final int BASSVOL = -7;
	public static final int DRUMON = -8;
	public static final int DRUMOFF = -9;
	public static final int DRUM = -10;
	public static final int DRONEON = -11;
	public static final int DRONEOFF = -12;
	public static final int DRONE = -13;
	
	private String name;
	private int type;
	private int pitch;
	private int ticks;
	private int string;
	private int fret;
	private int velocity;
	private boolean tied;
	private boolean stacato;
	private boolean grace;
	private boolean legato;
	private int sequence;
	private boolean triplet;
	private int tripletP;
	private int tripletQ;
	private int tripletR;
	private ArrayList lyrics;
	
	public ABCEvent(int type, String parm, int value) {
		this.type=type;
		this.name=parm;
		this.pitch=value;
		ticks=0;
		velocity=0;
		tied=false;
		grace=false;
		stacato=false;
		legato=false;
		sequence=0;
		triplet=false;
		lyrics=null;
	}

	public ABCEvent(ABCSong song, String name) {
		char[] a=(name+"  ").toCharArray();
		int i=0;
		pitch=0;
		ticks=0;
		velocity=0;
		type=REST;
		tied=false;
		grace=false;
		stacato=false;
		legato=false;
		sequence=0;
		triplet=false;
		switch(a[0]) {
		default: type=NOT_RELEVANT; i=1;break;
		case '|': 
			if(a[1]==':') {
				type=REPEAT_BEGIN;
				i=2;
				break;
			}
			if(a[1]=='|') {
				type=BAR_BAR;
				i=2;
				break;
			}
			if(a[1]==']') {
				type=BAR_FATBAR;
				i=2;
				break;
			}
			type=BAR;
			i=1;
			break;
		case '[': 
			if(a[1]=='|') {
				type=FATBAR_BAR;
				i=2;
				break;
			}
			if(a[1]==':') {
				type=REPEAT_BEGIN;
				i=2;
				break;
			}
			if("123456789".indexOf(a[1])>=0) {
				type=VARIANT;
				pitch=0;
				i=0;
				fret=0;
				ticks=0;
				while(i==0 || ",-".indexOf(a[i])>=0) {
					if(a[i]==',') fret=0;
					else fret=ticks;
					i++;
					ticks=0;
					while(i<a.length && "0123456789".indexOf(a[i])>=0) {
						ticks=ticks*10+"0123456789".indexOf(a[i]);
						i++;
					}
					if(fret==0) pitch |= (1<<(ticks-1));
					else {
						while(fret<=ticks) {
							pitch |= (1<<(fret-1));
							fret++;
						}
					}
					if(i==a.length) break;
				}
				fret=0;
				ticks=0;
				break;
			}
			type=CHORD_BEGIN;
			i=1;
			break;
		case ':': 
			if(a[1]==':') 
				type=REPEAT_END_AND_START; 
			else 
				type=REPEAT_END;
			i=2;
			break;
		case '"':// preceded by one of five symbols ^, _, <, > or @ which controls where the annotation is to be placed
			if("^_<>@".indexOf(a[1])<0) 
				type=CHORD_SYMBOL;
			else
				type=ANNOTATION;
			++i;
			while(i<a.length && a[i]!='"') ++i;
			if(i<a.length) ++i; else System.out.println("unbalanced quote (\") in music line:'"+name+"'");
			break;
		case '.':
			type=DECORATION;
			++i;
			break;
		case '+':
			type=DECORATION;
			++i;
			while(i<a.length && "+[:]| \t".indexOf(a[i])<0) ++i;
			if(i<a.length && a[i]=='+') ++i;
			else
				System.out.println("unbalanced decoration delimiter ("+a[0]+") in music line:'"+name+"'");
			break;
		case '!':
			type=DECORATION;
			++i;
			while(i<a.length && "![:]| \t".indexOf(a[i])<0) ++i;
			if(i<a.length && a[i]=='!') ++i;
			else {
				type=LINE_BREAK;	// old school ABC 
				i=1;
			}
			break;
		case '^':
		case '_':
		case '=':
		case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g':
		case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G':
			type=NOTE;
			i=song.computePitch(this,a);
			velocity=song.getTracks()[song.getTrack()].getVolume();
		case 'x':
		case 'z':
		case ']':// chord close...
			if(a[i]==']') type=CHORD_CLOSE;
			++i;
			this.ticks=(song.getDefaultNoteLength().getNumerator()*4*ABCSong.TICKS_PER_QUART)/song.getDefaultNoteLength().getDenominator();
			int multiplyer=1,denominator=1,n=0;
			while("0123456789".indexOf(a[i])>=0) {
				n=10*n+a[i]-'0';
				++i;
			}
			if(n>0) multiplyer=n;
			if(a[i]=='/') { 
				if("0123456789".indexOf(a[i+1])>=0) {
					n=0;
					++i;
					while("0123456789".indexOf(a[i])>=0) {
						n=10*n+a[i]-'0';
						++i;
					}
					if(n>0) denominator=n;
				}
				else {
					while(a[i]=='/') {
						denominator *= 2;
						++i;
					}
				}
			}
			this.ticks*=multiplyer;
			this.ticks/=denominator;
			break;
		}
		this.name=String.copyValueOf(a, 0, i);
		if(type==CHORD_SYMBOL) {
			if(!this.name.endsWith("\"")) this.name+="\"";
			pitch=song.addChord(this.name.substring(1, this.name.length()-1));
		}
		else if(type==NOTE) {
			int[] strings=song.getTracks()[song.getTrack()].getStrings();
			setPitchStringAndFret(pitch,strings);
		}
		else if(type==DECORATION) {
			if(this.name.equals(".")) 
				pitch=STACATODOT;
			else {
				if(!this.name.endsWith(this.name.substring(0, 1))) this.name+=this.name.substring(0, 1);
				pitch=decoration(this.name.substring(1,this.name.length()-1).toLowerCase());
			}
		}
		else if(type==ANNOTATION) {
			if(!this.name.endsWith(this.name.substring(0, 1))) this.name+=this.name.substring(0, 1);
			String s=this.name.substring(2, this.name.length()-1).trim().toLowerCase();
			pitch=0;
			if(s.matches("al +fine")) pitch=ALFINE;
			else if(s.matches("d\\.?s\\.?s\\.?")) pitch=DSS;
			else if(s.matches("to +coda")) pitch=TOCODA;
			else if(s.matches("al +coda")) pitch=ALCODA;
			if(pitch>0) type=DECORATION;
		}
	}

	public Object clone() {
		ABCEvent e=new ABCEvent(this.type, this.name, this.pitch);
		e.ticks=this.ticks;
		e.string=this.string;
		e.fret=this.fret;
		e.velocity=this.velocity;
		e.tied=this.tied;
		e.grace=this.grace;
		return e;
	}
	
	public void setPitchStringAndFret(int pitch, int[] strings) {
		int f=0;
		this.pitch=pitch;
		if(strings==null) return;
		this.string=-1;
		this.fret=127;
		for(int y=0;y<strings.length;y++) { 
			if(strings[y]<=pitch) {
				f=pitch-strings[y];
				if(f<this.fret) {
					this.string=y;
					this.fret=f;
				}
			}
		}
		if(this.string<0) {
			// note can not be mapped, forget it
			type=REST;
		}
	}

	private int decoration(String d) {
		if(d.equals("trill")) return TRILL; // "tr" (trill mark)
		if(d.equals("lowermordent")) return LOWERMORDENT; // short /|/|/ squiggle with a vertical line through it
		if(d.equals("uppermordent")) return UPPERMORDENT; // short /|/|/ squiggle
		if(d.equals("mordent")) return LOWERMORDENT; // same as +lowermordent+ 
		if(d.equals("pralltriller")) return UPPERMORDENT; // same as +uppermordent+ 
		if(d.equals("accent")) return ACCENT; // > mark
		if(d.equals(">")) return ACCENT; // same as +accent+ 
		if(d.equals("emphasis")) return ACCENT; // same as +accent+ 
		if(d.equals("fermata")) return FERMATA; // fermata or hold (arc above dot)
		if(d.equals("invertedfermata")) return INVERTEDFERMATA; // upside down fermata
		if(d.equals("tenuto")) return TENUTO; // horizontal line to indicate holding note for full duration
		if(d.equals("0")) return FINGERING_0; // fingerings
		if(d.equals("1")) return FINGERING_1; // fingerings
		if(d.equals("2")) return FINGERING_2; // fingerings
		if(d.equals("3")) return FINGERING_3; // fingerings
		if(d.equals("4")) return FINGERING_4; // fingerings
		if(d.equals("5")) return FINGERING_5; // fingerings
		if(d.equals("plus")) return PLUS; // left-hand pizzicato, or rasp for French horns
		if(d.equals("wedge")) return WEDGE; // small filled-in wedge mark
		if(d.equals("open")) return OPEN; // small circle above note indicating open string or harmonic
		if(d.equals("thumb")) return THUMB; // cello thumb symbol
		if(d.equals("snap")) return THUMB; // snap-pizzicato mark, visually similar to +thumb+ 
		if(d.equals("turn")) return TURN; // a turn mark
		if(d.equals("roll")) return ROLL; // a roll mark (arc) as used in Irish music
		if(d.equals("breath")) return BREATH; // a breath mark (apostrophe-like) after note
		if(d.equals("shortphrase")) return SHORTPHRASE; // vertical line on the upper part of the staff
		if(d.equals("mediumphrase")) return MEDIUMPHRASE; // same, but extending down to the centre line
		if(d.equals("longphrase")) return LONGPHRASE; // same, but extending 3/4 of the way down
		if(d.equals("segno")) return SEGNO; // 2 ornate s-like symbols separated by a diagonal line
		if(d.equals("coda")) return CODA; // a ring with a cross in it
		if(d.equalsIgnoreCase("D.S.")) return DS; // the letters D.S. (=Da Segno)
		if(d.equalsIgnoreCase("D.C.")) return DC; // the letters D.C. (=either Da Coda or Da Capo)
		if(d.equals("dacoda")) return DACODA; // the word "Da" followed by a Coda sign
		if(d.equals("dacapo")) return DACAPO; // the words "Da Capo"
		if(d.equals("fine")) return FINE; // the word "fine"
		if(d.equals("crescendo(")) return STARTCRESCENDO; // or +<(+  start of a < crescendo mark
		if(d.equals("<(")) return STARTCRESCENDO; // or +<(+  start of a < crescendo mark
		if(d.equals("crescendo)")) return ENDCRESCENDO; // or +<)+  end of a < crescendo mark, placed after the last note
		if(d.equals("<)")) return ENDCRESCENDO; // or +<)+  end of a < crescendo mark, placed after the last note
		if(d.equals("diminuendo(")) return STARTDIMINUENDO; // or +>(+  start of a > diminuendo mark
		if(d.equals(">(")) return STARTDIMINUENDO; // or +>(+  start of a > diminuendo mark
		if(d.equals("diminuendo)")) return ENDDIMINUENDO; // or +>)+  end of a > diminuendo mark, placed after the last note
		if(d.equals(">)")) return ENDDIMINUENDO; // or +>)+  end of a > diminuendo mark, placed after the last note
		if(d.equals("pppp")) return PPPP; // +ppp+ +pp+ +p+
		if(d.equals("ppp")) return PPP; // +ppp+ +pp+ +p+
		if(d.equals("pp")) return PP; // +ppp+ +pp+ +p+
		if(d.equals("p")) return P; // +ppp+ +pp+ +p+
		if(d.equals("mp")) return MP; // +mf+ +f+ +ff+
		if(d.equals("mf")) return MF; // +mf+ +f+ +ff+
		if(d.equals("f")) return F; // +ffff+ +sfz+     dynamics marks
		if(d.equals("ff")) return FF; // +ffff+ +sfz+     dynamics marks
		if(d.equals("fff")) return FFF; // +ffff+ +sfz+     dynamics marks
		if(d.equals("ffff")) return FFFF; // +ffff+ +sfz+     dynamics marks
		if(d.equals("sfz")) return SFZ; // +ffff+ +sfz+     dynamics marks
		if(d.equals("upbow")) return UPBOW; // V mark
		if(d.equals("downbow")) return DOWNBOW; // squared n mark
//		By extension, the following decorations have been added:
		if(d.equals("slide")) return SLIDE; 
		if(d.equals("turnx")) return TURNX; 
		if(d.equals("invertedturn")) return INVERTEDTURN; 
		if(d.equals("invertedturnx")) return INVERTEDTURNX; 
		if(d.equals("arpeggio")) return ARPEGGIO; 
		if(d.equals("trill(")) return STARTTRILL; 
		if(d.equals("trill)")) return ENDTRILL; 
		return 0;
	}

	public String toString() {
		return name;
	}
	
	public int compareTo(Object o) {
		if(o==null) return 1;
		if (o instanceof ABCEvent) {
			ABCEvent e = (ABCEvent) o;
			int i=type - e.type;
			if(i!=0) return i;
			if(grace && !e.grace) return 1;
			if(e.grace && !grace) return -1;
			if(grace) {
				i=sequence - e.sequence;
				if(i!=0) return i;
			}
			i=pitch - e.pitch;
			if(i!=0) return i;
			i=ticks - e.ticks;
			if(i!=0) return i;
			i = string - e.string;
			if(i!=0) return i;
			i = fret - e.fret;
			if(i!=0) return i;
		}
		else return -1;
		return 0;
	}

	/**
	 * @return the fret
	 */
	public int getFret() {
		return fret;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the name
	 */
	public String getParm() {
		return name;
	}

	/**
	 * @return the pitch
	 */
	public int getPitch() {
		return pitch;
	}

	/**
	 * @return the pitch
	 */
	public int getValue() {
		return pitch;
	}

	/**
	 * @return the string
	 */
	public int getString() {
		return string;
	}

	/**
	 * @return the ticks
	 */
	public int getTicks() {
		return ticks;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param ticks the ticks to set
	 */
	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public int getDecoration() {
		return pitch;
	}

	public int getChordnum() {
		return pitch;
	}

	public void alterString(int string, int[] strings) {
		if(string>=0 && string<strings.length) {
			this.string=string;
			this.fret=(byte) (pitch-strings[string]);
		}
	}

	public void setType(int type) {
		this.type=type;
	}

	public void setValue(int value) {
		this.pitch=value;
	}

	public void setNumerator(int value) {
		this.pitch=value;
	}

	public void setDenominator(int d) {
		this.fret=d;
	}

	public int getVelocity() {
		return this.velocity;
	}

	/**
	 * @param velocity the velocity to set
	 */
	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}

	public void setTied(boolean b) {
		tied=b;
	}

	/**
	 * @return the tied
	 */
	public boolean isTied() {
		return tied;
	}

	public void setToEnd(boolean b) {
		tied=b;
	}

	/**
	 * @return the toEnd
	 */
	public boolean isToEnd() {
		return tied;
	}

	public int getVariant() {
		return pitch;
	}

	public int getNumerator() {
		return pitch;
	}

	public int getDenominator() {
		return fret;
	}

	public void setGrace(boolean b) {
		this.grace=b;
	}

	/**
	 * @return the grace
	 */
	public boolean isGrace() {
		return grace;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public boolean isTriplet() {
		return triplet;
	}

	public void setTriplet(boolean triplet) {
		this.triplet = triplet;
	}

	public int getTripletP() {
		return tripletP;
	}

	public void setTripletP(int tripletP) {
		this.tripletP = tripletP;
	}

	public int getTripletQ() {
		return tripletQ;
	}

	public void setTripletQ(int tripletQ) {
		this.tripletQ = tripletQ;
	}

	public int getTripletR() {
		return tripletR;
	}

	public void setTripletR(int tripletR) {
		this.tripletR = tripletR;
	}

	public void setTriplet(int p, int q, int r) {
		triplet=true;
		tripletP=p;
		tripletQ=q;
		tripletR=r;
	}

	public String[] getLyrics() {
		if(this.lyrics==null) return null;
		String[] s=new String[lyrics.size()];
		for(int i=0;i<s.length;i++) s[i]=(String)lyrics.get(i);
		return s;
	}

	public void setLyrics(String lyrics) {
		if(this.lyrics==null) this.lyrics=new ArrayList();
		this.lyrics.add(lyrics);
	}

	/**
	 * @return the stacato
	 */
	public boolean isStacato() {
		return stacato;
	}

	/**
	 * @param stacato the stacato to set
	 */
	public void setStacato(boolean stacato) {
		this.stacato = stacato;
	}

	/**
	 * @return the legato
	 */
	public boolean isLegato() {
		return legato;
	}

	/**
	 * @param legato the legato to set
	 */
	public void setLegato(boolean legato) {
		this.legato = legato;
	}

}
