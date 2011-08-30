package org.herac.tuxguitar.io.abc;

import java.io.IOException;
import java.io.InputStream;

import org.herac.tuxguitar.io.abc.base.ABCSong;
import org.herac.tuxguitar.io.abc.base.ABCTimeSignature;
import org.herac.tuxguitar.io.base.TGFileFormatException;

public class ABCInputStream {
	
	private static final int OUTSIDE = 0;
	private static final int SKIPX = 1;
	private static final int INHEAD = 2;
	private static final int INBODY = 3;
	private static final int EOF = 4;
	private static final int CR = '\r';
	private static final int LF = '\n';
	private static final int TAB = '\t';
	private static final int EMPTY = -1;
	private ABCSong song;
	private InputStream stream;
	private int state;
	private String line;
	private int putback;
	private ABCSettings settings;
	
	public ABCInputStream(InputStream stream,ABCSettings settings){
		this.stream            = stream;
		this.putback           = EMPTY;
		this.settings=settings==null?new ABCSettings():settings;
	}

	public ABCSong readSong() throws TGFileFormatException{
		boolean firstMusicLine=false;
		this.song = new ABCSong();
		int skip=settings.getX();
		this.song.setInstrumentOffset(settings.isInstrumentsStartAt1()?1:0);
		this.song.initHead();
		this.state = OUTSIDE;
		while(this.state==SKIPX || this.state == OUTSIDE) {
			if(this.readLine()) {
				if(this.line.startsWith("X:")) {
					this.state=SKIPX;
					if(--skip == 0) {
						this.state = INHEAD;
						this.song.setX(Integer.parseInt(line.substring(2).trim(), 10));
					}
				}
				else { 
					if(this.state==OUTSIDE) {
						if(this.line.startsWith("U:")) this.song.addRedefinable(line.substring(2).trim());			
						else if(this.line.startsWith("m:")) this.song.addMacro(line.substring(2).trim());
					}
				}
			}
			else
				this.state = EOF;
		}
		while(this.state == INHEAD || this.state == INBODY) {
			if(this.readLine()) {
				if(this.line.length()==0) this.state = OUTSIDE;
				else if(this.line.startsWith("%%")) this.preprocessor();
				else {
					if(this.line.indexOf('%')>=0) this.removeComment();
					/*
					  A:  (Geographical) Area : eg A:Brittany or A:Sussex
					  B:  Book, eg B:Encyclopeadia Blowzabellica or B:O'Neill's
					  C:  Composer eg C:Andy Cutting or C:Trad
					  D:  Discography eg D:New Victory Band, One More Dance And Then
					  F:  File Name eg http://www.lesession.co.uk/woodenflute.abc
					  G:  Group eg G:Flute - this is used for the purpose of indexing tunes in software, NOT for naming the group / band you acquired the tune from (which should be recorded in the S: source field).
					  H:  History - Multiple H: fields may be used as needed to record text about the history of the tune. (Many people (including me) seem to tend to forget about the H: field and instead always put information like that in the N: notes field instead.)
					  I:  Information - used by certain software packages, NOT for historical information or notes (which should be recorded in the H: or N: fields).
					  K:  Key -see part one of this tutorial for further details
					  L:  Default note length -see part one of this tutorial for further details
					  M:  Meter :see part one of this tutorial for further details
					  N:  Notes : Multiple N: fields can be used as needed to record detailed text notes about, well, just about anything you want to say about the tune that won't go in any of the other fields really ...
					  O:  (Geographical) Origin : eg O:Irish or O:Swedish
					  P:  Parts -see below for further details
					  Q:  Tempo -see part one of this tutorial for further details
					  R:  Rhythm -see part one of this tutorial for further details
					  S:  Source - where you got the tune from eg S:Olio or S:Dave Praties
					  T:  Title -see part one of this tutorial for further details
					  U:  User defined symbol
					  W:  Words -see below for further details
					  X:  Tune reference number -see part one of this tutorial for further details
					  Z:  Transcription note - the identity of the transcriber or the source of the transcription, eg Z:Steve Mansfield
					  d:  body Decorations. d:!pp! * * !mf! * !ff!
					  s:  body inline symbols. s:+pp+ * * +mf+ * +ff+
					  w:  body inline lyrics. w:Help! I need...
					 */	
					if(this.line.startsWith("A:")) this.song.setArea(line.substring(2).trim());
					else if(this.line.startsWith("B:")) this.song.setBook(line.substring(2).trim());
					else if(this.line.startsWith("C:")) this.song.setComponist(line.substring(2).trim());
					else if(this.line.startsWith("D:")) this.song.setDiscography(line.substring(2).trim());
					else if(this.line.startsWith("E:")) { /* do nothing */ }
					else if(this.line.startsWith("F:")) this.song.setFilename(line.substring(2).trim());
					else if(this.line.startsWith("G:")) this.song.setGroup(line.substring(2).trim());
					else if(this.line.startsWith("H:")) this.song.addHistory(line.substring(2).trim());
					else if(this.line.startsWith("I:")) this.song.setInformation(line.substring(2).trim());
					else if(this.line.startsWith("J:")) { /* do nothing */ }
					else if(this.line.startsWith("K:")) {
						this.song.setKey(line.substring(2).trim());
						this.state = INBODY;
						firstMusicLine=true;
					}
					else if(this.line.startsWith("L:")) this.setDefaultNoteLength(line.substring(2).trim());
					else if(this.line.startsWith("M:")) this.setTimeSignature(line.substring(2).trim());
					else if(this.line.startsWith("N:")) this.song.addNote(line.substring(2).trim());
					else if(this.line.startsWith("O:")) this.song.setOrigin(line.substring(2).trim());
					else if(this.line.startsWith("P:")) {
						if(this.state == INHEAD)
							this.song.setParts(line.substring(2).trim());
						else
							this.song.setPart(line.substring(2).trim());
					}
					else if(this.line.startsWith("Q:")) this.song.setTempo(line.substring(2).trim());
					else if(this.line.startsWith("R:")) this.song.setRhythm(line.substring(2).trim());
					else if(this.line.startsWith("S:")) this.song.setSource(line.substring(2).trim());
					else if(this.line.startsWith("T:")) this.song.setTitle(line.substring(2).trim());
					else if(this.line.startsWith("U:")) this.song.addRedefinable(line.substring(2).trim());
					else if(this.line.startsWith("V:")) {
						if(this.state ==  INHEAD)
							this.song.addVoice(line.substring(2).trim());
						else
							this.song.setVoice(line.substring(2).trim());
					}
					else if(this.line.startsWith("W:")) this.song.addWords(line.trim());
					else if(this.line.startsWith("X:")) { /* do nothing */ }
					else if(this.line.startsWith("Y:")) { /* do nothing */ }
					else if(this.line.startsWith("Z:")) this.song.setTranscriptor(line.substring(2).trim());
					else if(this.line.startsWith("m:")) this.song.addMacro(line.substring(2).trim());
					else if(this.line.startsWith("r:")) this.song.addRemarks(line.substring(2).trim());
					else if(this.state == INBODY) { 
						if(firstMusicLine) this.song.initBody();
						firstMusicLine=false;
						if(this.line.startsWith("d:")) this.song.addSymbols(line.substring(2).trim());
						else if(this.line.startsWith("s:")) this.song.addSymbols(line.substring(2).trim());
						else if(this.line.startsWith("w:")) this.song.addLyrics(line.substring(2).trim());
						else if(this.line.matches("[a-z]:.*")) { /* do nothing */ }
						else {
							// must have a line with music here...
							this.song.addMusic(line+"  ");
						}
					}
				}
			}
			else
				this.state = EOF;
		}
		
		this.close();
		if(this.song.getEvents()==null) {
			throw new TGFileFormatException("no music body");
		}
		this.song.postprocessor();
		return this.song;
	}

	private void setDefaultNoteLength(String line) {
		if(line.matches("[0-9]+/[0-9]+.*")) {
			int numerator=0,denominator=0,i;
			for(i=0;line.charAt(i)!='/';i++) {
				numerator=numerator*10+line.charAt(i)-'0';
			}
			for(i++;i<line.length() && "0123456789".indexOf(line.charAt(i))>=0;i++) {
				denominator=denominator*10+line.charAt(i)-'0';
			}
			this.song.setDefaultNoteLength(new ABCTimeSignature(numerator,denominator,false));
		}
		else
			this.song.setDefaultNoteLength(new ABCTimeSignature(1,4,false));
	}

	private void setTimeSignature(String line) {
		if(line.matches("[0-9]+/[0-9]+.*")) {
			int numerator=0,denominator=0,i;
			for(i=0;line.charAt(i)!='/';i++) {
				numerator=numerator*10+line.charAt(i)-'0';
			}
			for(i++;i<line.length() && "0123456789".indexOf(line.charAt(i))>=0;i++) {
				denominator=denominator*10+line.charAt(i)-'0';
			}
			this.song.setTimeSignature(new ABCTimeSignature(numerator,denominator,true));
			return;
		}
		if(line.equals("C")) {
			this.song.setTimeSignature(new ABCTimeSignature(4,4,true));
			return;
		}
		if(line.equals("C|")) {
			this.song.setTimeSignature(new ABCTimeSignature(2,2,true));
			return;
		}
		this.song.setTimeSignature(new ABCTimeSignature(4,4,true));
	}

	private void removeComment() {
		int q=0;
		for(int i=0;i<line.length();i++) {
			switch(line.charAt(i)) {
			case '"':
				if(q=='"') q=0; else if(q==0) q='"';
				break;
			case '\'':
				if(q=='\'') q=0; else if(q==0) q='\'';
				break;
			case '%':
				if(q==0) {
					line=line.substring(0,i);
					return;
				}
				break;
			}
		}
	}

	private void preprocessor() {
		int i=line.indexOf('%', 2);
		if(i>0) line=line.substring(0, i).replaceAll("\\s+", " ").trim();
		if(line.startsWith("%%MIDI ")) song.midiCommand(line);
		else song.directive(line);
	}

	private boolean readLine() {
		this.line="";
		int b=this.putback==EMPTY?this.readByte():this.putback;
		if(b<=0) return false;
		boolean hadCR=false;
		this.putback=EMPTY;
		while(b!=0 && b!=LF) {
			switch(b) {
			case CR:
				hadCR=true;
				break;
			case TAB:
				b=' ';
			default:
				this.line+=(char)b;
				hadCR=false;
				break;
			}
			b=this.readByte();
			if(hadCR && b!=LF) {
				this.putback=b;
				b=LF;
			}
			if(b==LF && line.endsWith("\\")) {
				this.line=this.line.substring(0, this.line.length()-1)+' ';
				if(this.putback!=EMPTY) { 
					this.line+=(char)this.putback;
					this.putback=EMPTY;
				}
			}
		}
		while(this.line.endsWith(" "))
			this.line=this.line.substring(0, this.line.length()-1);
		return true;
	}

	protected int readByte(){
		try {
			return this.stream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	protected void close(){
		try {
			this.stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
