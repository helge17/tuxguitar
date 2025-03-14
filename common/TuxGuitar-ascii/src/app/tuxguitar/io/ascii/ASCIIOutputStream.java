package app.tuxguitar.io.ascii;

import java.io.PrintStream;
import java.io.PrintWriter;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.effects.TGEffectBend;

public class ASCIIOutputStream {
	private PrintWriter writer;
	private int x;
	private int y;

	public ASCIIOutputStream(PrintStream stream){
		this.writer = new PrintWriter(stream);
	}

	public int drawChord(TGChord chord) {
		String chordName=(chord!=null ? chord.getName() : "");
		this.writer.print(chordName);
		movePoint(getPosX() + chordName.length(),getPosY());
		return chordName.length();
	}

	public int drawNote(TGNote note, TGNote nextNote, boolean printNote){
		StringBuffer noteString=new StringBuffer();
		if (note!=null) {
			int fret=note.getValue();
			if (note.getEffect().isDeadNote()) {
				noteString.append("X");
			} else if (note.getEffect().isBend() && (note.getEffect().getBend().getMovements().size()==0)) { // bend.getMovements().size()==0 means hold bend
				noteString.append("H");
			} else {
				noteString.append(fret);
			}
			if (note.getEffect().isHammer() && (nextNote !=null)) {
				if (nextNote.getValue()>fret) {
					noteString.append("h");
				} else {
					noteString.append("p");
				}
			}
			if (note.getEffect().isSlide() && (nextNote !=null)) {
				if (nextNote.getValue()>fret) {
					noteString.append("/");
				} else {
					noteString.append("\\");
				}
			}
			if (note.getEffect().isBend()) {
				TGEffectBend bend=note.getEffect().getBend();
				// bend.getMovements().size()==0 means hold bend and is handled above
				if (bend.getMovements().size()>0) {
					int movement=bend.getMovements().get(0);
					// rounding of division by 2 is intentional to avoid fractional frets like 12b12.5 in the ASCII notation
					int bendNote=fret+Math.abs(movement)/2;
					if (movement<0) {
						noteString.insert(0, "r");
						noteString.insert(0, bendNote);
					} else {
						noteString.append("b");
						noteString.append(bendNote);
					}
				}
			}

			if (printNote) {
				this.writer.print(noteString.toString());
				movePoint(getPosX() + noteString.length(),getPosY());
			}
		}

		return noteString.length();
	}

	public void drawStringSegments(int count){
		if (count<0) {
			count=0;
		}
		movePoint(getPosX() + count,getPosY());
		for(int i = 0; i < count;i ++){
			this.writer.print("-");
		}
	}

	public void drawTuneSegment(String tune,int maxLength){
		for(int i = tune.length();i < maxLength;i ++){
			drawSpace();
		}
		movePoint(getPosX() + tune.length(),getPosY());
		this.writer.print(tune);
	}

	public void drawBarSegment(){
		movePoint(getPosX() + 1,getPosY());
		this.writer.print("|");
	}

	public void nextLine(){
		movePoint(0,getPosY() + 1);
		this.writer.println("");
	}

	public void drawStringLine(String s){
		movePoint(0,getPosY() + 1);
		this.writer.println(s);
	}

	public void drawSpace(int numSpaces){
		movePoint(getPosX() + numSpaces,getPosY());
		for (int i = 0; i < numSpaces; i++) {
			this.writer.print(' ');
		}
	}

	public void drawSpace(){
		drawSpace(1);
	}

	private void movePoint(int x,int y){
		this.x = x;
		this.y = y;
	}

	public int getPosX(){
		return this.x;
	}

	public int getPosY(){
		return this.y;
	}

	public void flush(){
		this.writer.flush();
	}

	public void close(){
		this.writer.close();
	}
}
