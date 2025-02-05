package org.herac.tuxguitar.io.ascii;

import java.io.PrintStream;
import java.io.PrintWriter;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;

public class ASCIIOutputStream {
	private PrintWriter writer;
	private int x;
	private int y;

	public ASCIIOutputStream(PrintStream stream){
		this.writer = new PrintWriter(stream);
	}

	public int drawNote(TGNote note, TGNote nextNote, boolean printNote){
		int printWidth=0;
		if (note!=null) {
			StringBuffer noteString=new StringBuffer();
			int fret=note.getValue();
			if (note.getEffect().isDeadNote()) {
				noteString.append("X");
				++printWidth;
			} else {
				noteString.append(fret);
				printWidth+=(fret >=10 )?2:1;
			}
			if (note.getEffect().isHammer() && (nextNote !=null)) {
				if (nextNote.getValue()>fret) {
					noteString.append("h");
				} else {
					noteString.append("p");
				}
				++printWidth;
			}
			if (note.getEffect().isSlide() && (nextNote !=null)) {
				if (nextNote.getValue()>fret) {
					noteString.append("/");
				} else {
					noteString.append("\\");
				}
				++printWidth;
			}
			if (note.getEffect().isBend()) {
				TGEffectBend bend=note.getEffect().getBend();
				// bend.getMovements().size()==0 means hold bend, not handled yet
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
					++printWidth;
					printWidth+=(bendNote >=10 )?2:1;
				}
			}

			if (printNote) {
				this.writer.print(noteString.toString());
				movePoint(getPosX() + printWidth,getPosY());
			}
		}
		return printWidth;
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

	public void drawSpace(){
		movePoint(getPosX() + 1,getPosY());
		this.writer.print(" ");
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
