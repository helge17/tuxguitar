package org.herac.tuxguitar.io.ascii;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ASCIIOutputStream {
	private PrintWriter writer;
	private int x;
	private int y;
	
	public ASCIIOutputStream(PrintStream stream){
		this.writer = new PrintWriter(stream);
	}
	
	public void drawNote(int fret){
		movePoint(getPosX() + ((fret >=10 )?2:1),getPosY());
		this.writer.print(fret);
	}
	
	public void drawStringSegments(int count){
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
