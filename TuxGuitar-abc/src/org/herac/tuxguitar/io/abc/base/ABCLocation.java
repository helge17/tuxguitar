/**
 * 
 */
package org.herac.tuxguitar.io.abc.base;

/**
 * @author peter
 *
 */
public class ABCLocation implements Comparable {

	private String part;
	private int ticks;
	private int measure;
	private int track;
	private ABCEvent event;
	private ABCChord chord;
	private int tempo;
	private int legato;
	private boolean tied;

	public ABCLocation(String part, int track, int measure, int ticks, ABCChord chord, ABCEvent event) {
		this.part = part;
		this.track = track;
		this.measure = measure;
		this.ticks = ticks;
		this.chord = chord;
		this.event = event;
	}

	public String toString() {
		String s;
		if(part==null) s="";
		else s="Part "+part+" ";
		s+="Track "+String.valueOf(100+track).substring(1)+":";
		s+="Measure "+String.valueOf(100+measure).substring(1)+":";
		s+="Tick "+String.valueOf(1000+ticks).substring(1)+":";
		s+=event.getName();
		return s;
	}
	
	/**
	 * @return the measure
	 */
	public int getMeasure() {
		return measure;
	}

	/**
	 * @return the part
	 */
	public String getPart() {
		return part;
	}

	/**
	 * @return the ticks
	 */
	public int getTicks() {
		return ticks;
	}

	/**
	 * @return the track
	 */
	public int getTrack() {
		return track;
	}

	/**
	 * @return the event
	 */
	public ABCEvent getEvent() {
		return event;
	}

	public int compareTo(Object o) {
		if(o==null) return 1;
		if (o instanceof ABCLocation) {
			ABCLocation e = (ABCLocation) o;
			if(part==null || e.part==null) {
				if(part!=null) return 1;
				if(e.part!=null) return -1;
			}
			else {
				int i=part.compareTo(e.part);
				if(i!=0) return i;
			}
			int i=track - e.track;
			if(i!=0) return i;
			i=measure - e.measure;
			if(i!=0) return i;
			i=ticks - e.ticks;
			if(i!=0) return i;
			i = event.compareTo(e.event);
			if(i!=0) return i;
		}
		else return -1;
		return 0;
	}

	/**
	 * @param measure the measure to set
	 */
	public void setMeasure(int measure) {
		this.measure = measure;
	}

	/**
	 * @param part the part to set
	 */
	public void setPart(String part) {
		this.part = part;
	}

	/**
	 * @param ticks the ticks to set
	 */
	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public void setChord(ABCChord chord) {
		this.chord=chord;
	}

	/**
	 * @return the chord
	 */
	public ABCChord getChord() {
		return chord;
	}

	public void setTied(boolean tied) {
		this.tied=tied;
	}

	/**
	 * @return the tied
	 */
	public boolean isTied() {
		return tied;
	}

	public int getTempo() {
		return tempo;
	}

	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	/**
	 * @return the legato
	 */
	public int getLegato() {
		return legato;
	}

	/**
	 * @param legato the legato to set
	 */
	public void setLegato(int legato) {
		this.legato = legato;
	}

}
