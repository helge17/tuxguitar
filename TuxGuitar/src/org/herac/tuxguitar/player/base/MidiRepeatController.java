package org.herac.tuxguitar.player.base;

import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

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
		this.lastIndex = -1;
		this.shouldPlay = true;
		this.repeatOpen = true;
		this.repeatAlternative = 0;
		this.repeatStart = TGDuration.QUARTER_TIME;
		this.repeatEnd = 0;
		this.repeatMove = 0;
		this.repeatStartIndex = 0;
		this.repeatNumber = 0;
	}
	
	public void process(){
		TGMeasureHeader header = this.song.getMeasureHeader(this.index);
		
		//Verifica si el compas esta dentro del rango.
		if( (this.sHeader != -1 && header.getNumber() < this.sHeader) || ( this.eHeader != -1 && header.getNumber() > this.eHeader ) ){
			this.shouldPlay = false;
			this.index ++;
			return;
		}
		
		//Abro repeticion siempre para el primer compas.
		if( (this.sHeader != -1 && header.getNumber() == this.sHeader ) || header.getNumber() == 1 ){
			this.repeatStartIndex = this.index;
			this.repeatStart = header.getStart();
			this.repeatOpen = true;
		}
		
		//Por defecto el compas deberia sonar
		this.shouldPlay = true;
		
		//En caso de existir una repeticion nueva,
		//guardo el indice de el compas donde empieza una repeticion
		if (header.isRepeatOpen()) {
			this.repeatStartIndex = this.index;
			this.repeatStart = header.getStart();
			this.repeatOpen = true;
			
			//Si es la primer vez que paso por este compas
			//Pongo numero de repeticion y final alternativo en cero
			if(this.index > this.lastIndex){
				this.repeatNumber = 0;
				this.repeatAlternative = 0;
			}
		}
		else{
			//verifico si hay un final alternativo abierto
			if(this.repeatAlternative == 0){
				this.repeatAlternative = header.getRepeatAlternative();
			}
			//Si estoy en un final alternativo.
			//el compas solo puede sonar si el numero de repeticion coincide con el numero de final alternativo.
			if (this.repeatOpen && (this.repeatAlternative > 0) && ((this.repeatAlternative & (1 << (this.repeatNumber))) == 0)){
				this.repeatMove -= header.getLength();
				if (header.getRepeatClose() >0){
					this.repeatAlternative = 0;
				}
				this.shouldPlay = false;
				this.index ++;
				return;
			}
		}
		
		//antes de ejecutar una posible repeticion
		//guardo el indice del ultimo compas tocado 
		this.lastIndex = Math.max(this.lastIndex,this.index);
		
		//si hay una repeticion la hago
		if (this.repeatOpen && header.getRepeatClose() > 0) {
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
		
		this.index ++;
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
