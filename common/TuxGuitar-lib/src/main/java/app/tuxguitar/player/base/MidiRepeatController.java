package app.tuxguitar.player.base;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;

public class MidiRepeatController {

	private TGSong song;
	private int count;
	private int index;
	private int lastIndex;
	private boolean shouldPlay;
	private boolean repeatOpen;
	private long repeatStart;
	private long repeatEnd;
	private long repeatMove;
	private int repeatStartIndex;
	private int repeatNumber;
	private int repeatAlternative;
	private int sHeader;
	private int eHeader;

	public MidiRepeatController(TGSong song, int sHeader , int eHeader){
		this.song = song;
		this.sHeader = sHeader;
		this.eHeader = eHeader;
		this.count = song.countMeasureHeaders();
		this.index = 0;
		this.lastIndex = -1;		// index of last played measure
		this.shouldPlay = true;
		this.repeatOpen = true;
		this.repeatAlternative = 0;
		this.repeatStart = TGDuration.QUARTER_TIME;
		this.repeatEnd = 0;
		this.repeatMove = 0;	// offset (nb of ticks) to add to beat start because of repeats (right shift)
		this.repeatStartIndex = 0;
		this.repeatNumber = 0;
	}

	public void process(){
		TGMeasureHeader header = this.song.getMeasureHeader(this.index);

		// parsing is already finished if measure is after range
		if( this.isAfterRange(header) ) {
			this.shouldPlay = false;
			this.index ++;
			return;
		}

		//Abro repeticion siempre para el primer compas.
		// first measure is default open repeat
		if( this.isRangeStart(header) ){
			this.openRepeat(header);
		}

		//Por defecto el compas deberia sonar
		// by default measure shall be played
		this.shouldPlay = true;

		if (header.isRepeatOpen()) {
			this.startRepeat(header);
		}
		else{
			this.checkAlternative(header);
		}

		//antes de ejecutar una posible repeticion
		//guardo el indice del ultimo compas tocado
		if (this.shouldPlay) {
			this.lastIndex = Math.max(this.lastIndex,this.index);
			this.closeRepeat(header);
		}
		this.index ++;
		this.applyRange(header);
	}

	//Verifica si el compas esta dentro del rango.
	// check measure is in loop range (if any loop defined)
	private void applyRange(TGMeasureHeader header) {
		if( this.isBeforeRange(header) || this.isAfterRange(header) ){
			this.shouldPlay = false;
		}
		// no repeat move before starting loop (if any)
		if( this.isBeforeRange(header) ) {
			this.repeatMove = 0;
		}
	}

	// the first measure, or the first of the range, opens a repeat
	private boolean isRangeStart(TGMeasureHeader header) {
		return ((this.sHeader >= 0 && header.getNumber() == this.sHeader ) || header.getNumber() == 1);
	}

	private boolean isBeforeRange(TGMeasureHeader header) {
		return (this.sHeader >= 0 && header.getNumber() < this.sHeader);
	}

	private boolean isAfterRange(TGMeasureHeader header) {
		return (this.eHeader >= 0 && header.getNumber() > this.eHeader);
	}

	// the repeat now starts at this measure
	private void openRepeat(TGMeasureHeader header) {
		this.repeatStartIndex = this.index;
		this.repeatStart = header.getStart();
		this.repeatOpen = true;
	}

	//En caso de existir una repeticion nueva,
	//guardo el indice de el compas donde empieza una repeticion
	// if repeat opens, store where it starts
	private void startRepeat(TGMeasureHeader header) {
		this.openRepeat(header);

		//Si es la primer vez que paso por este compas
		//Pongo numero de repeticion y final alternativo en cero
		// reset repeat counter if this is the first time this measure is parsed
		if(this.index > this.lastIndex){
			this.repeatNumber = 0;
			this.repeatAlternative = 0;
		}
	}

	private void checkAlternative(TGMeasureHeader header) {
		//verifico si hay un final alternativo abierto
		// update current repeat alternative
		if(this.repeatAlternative == 0){
			this.repeatAlternative = header.getRepeatAlternative();
		}
		//Si estoy en un final alternativo.
		//el compas solo puede sonar si el numero de repeticion coincide con el numero de final alternativo.
		// if in an alternative repeat, measure shall only be played if it corresponds to current repeat number
		if (this.repeatOpen && (this.repeatAlternative > 0) && ((this.repeatAlternative & (1 << (this.repeatNumber))) == 0)){
			this.repeatMove -= header.getLength();
			if (header.getRepeatClose() >0){
				this.repeatAlternative = 0;
			}
			this.shouldPlay = false;
		}
	}

	//si hay una repeticion la hago
	private void closeRepeat(TGMeasureHeader header) {
		if (!this.repeatClosesAt(header)) {
			return;
		}
		if (this.repeatNumber < header.getRepeatClose() || (this.repeatAlternative > 0)) {
			this.repeatEnd = header.getStart() + header.getLength();
			this.repeatMove += this.repeatEnd - this.repeatStart;
			this.index = this.repeatStartIndex - 1;
			this.repeatNumber++;
		} else{
			this.repeatStart = 0;
			this.repeatNumber = 0;
			this.repeatEnd = 0;
			this.repeatOpen = false;
		}
		this.repeatAlternative = 0;
	}

	// repeat close (ignored if it's the last measure in the loop, UNLESS the repeat open is strictly inside the loop)
	private boolean repeatClosesAt(TGMeasureHeader header) {
		boolean isLastMeasureOfLoop = (this.eHeader >= 0 && header.getNumber() == this.eHeader);
		boolean repeatOpenInsideLoop = (this.song.getMeasureHeader(this.repeatStartIndex).getNumber() > this.sHeader);
		boolean ignoreRepeatClose = (isLastMeasureOfLoop && !repeatOpenInsideLoop);
		return (this.repeatOpen && (header.getRepeatClose() > 0) && !ignoreRepeatClose);
	}

	public boolean finished(){
		return (this.index >= this.count);
	}

	public boolean shouldPlay(){
		return this.shouldPlay;
	}

	public int getIndex(){
		return this.index;
	}

	public long getRepeatMove(){
		return this.repeatMove;
	}
}
