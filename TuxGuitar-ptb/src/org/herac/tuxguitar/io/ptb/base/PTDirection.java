package org.herac.tuxguitar.io.ptb.base;

public class PTDirection implements PTComponent{
	
	public static final int DIRECTION_CODA = 0;
	public static final int DIRECTION_DOUBLE_CODA = 1;
	public static final int DIRECTION_SEGNO = 2;
	public static final int DIRECTION_SEGNO_SEGNO = 3;
	public static final int DIRECTION_FINE = 4;
	public static final int DIRECTION_DA_CAPO = 5;
	public static final int DIRECTION_DAL_SEGNO = 6;
	public static final int DIRECTION_DAL_SEGNO_SEGNO = 7;
	public static final int DIRECTION_TO_CODA = 8;
	public static final int DIRECTION_TO_DOUBLE_CODA = 9;
	public static final int DIRECTION_DA_CAPO_AL_CODA = 10;
	public static final int DIRECTION_DA_CAPO_AL_DOUBLE_CODA = 11;
	public static final int DIRECTION_DAL_SEGNO_AL_CODA = 12;
	public static final int DIRECTION_DAL_SEGNO_AL_DOUBLE_CODA = 13;
	public static final int DIRECTION_DAL_SEGNO_SEGNO_AL_CODA = 14;
	public static final int DIRECTION_DAL_SEGNO_SEGNO_AL_DOUBLE_CODA = 15;
	public static final int DIRECTION_DA_CAPO_AL_FINE = 16;
	public static final int DIRECTION_DAL_SEGNO_AL_FINE = 17;
	public static final int DIRECTION_DAL_SEGNO_SEGNO_AL_FINE = 18;
	
	public static final int ACTIVE_SYMBOL_DC  = 1;
	public static final int ACTIVE_SYMBOL_DS  = 2;
	public static final int ACTIVE_SYMBOL_DSS = 3;
	
	private int direction;
	private int activeSymbol;
	private int repeat;
	
	public PTDirection(int direction, int activeSymbol, int repeat){
		this.direction = direction;
		this.activeSymbol = activeSymbol;
		this.repeat = repeat;
	}
	
	public int getDirection() {
		return this.direction;
	}
	
	public int getActiveSymbol() {
		return this.activeSymbol;
	}
	
	public int getRepeat() {
		return this.repeat;
	}
	
	public PTComponent getClone(){
		return new PTDirection( getDirection() , getActiveSymbol(), getRepeat() );
	}
}
