package org.herac.tuxguitar.graphics.control;

/**
 * The goal of this class is to provide a configurable interface to drum
 * rendering mapping of rendering flags and notes.
 * 
 * @author simpoir@gmail.com, Theo Other
 *
 */
public class TGDrumMap {
	
	private static final int MAX_NOTES = 90;
	private static final int MAP_POSITION = 0;
	private static final int MAP_KIND = 1;
	private static final int MAP_LEN = 2;
	
	// master kinds
	public static final int KIND_CYMBAL = 0x1; // X note
	public static final int KIND_NOTE = 0x2; // round and black
	public static final int KIND_SLANTED_DIAMOND = 0x10; // slanted diamond - draw like harmonic
	public static final int KIND_TRIANGLE = 0x20; // triangle - like tambourine
	public static final int KIND_EFFECT_CYMBAL = 0x40; // weirdish-looking X
	
	// modifiers
	public static final int KIND_OPEN = 0x4; // small o above
	public static final int KIND_CLOSED = 0x8; // small + above
	public static final int KIND_CIRCLE_AROUND = 0x80; // circle around note head

	public static final int DEFAULT_KIND = KIND_NOTE;
	public static final int DEFAULT_POSITION = -1;
	
	// note#, [render position#, render flags]
	private int[][] mapping;
	
	public TGDrumMap() {
		this.createDefaultMapping();
	}

	private void createDefaultMapping() {
		this.mapping = new int[MAX_NOTES][MAP_LEN];
		
		// set all notes to default values
		for(int i = 0 ; i < mapping.length; i ++) {
			this.map(i, DEFAULT_POSITION, DEFAULT_KIND); 
		}
		
		// bass drums
		this.map(35, 7, KIND_NOTE); // acoustic bass drum 
		this.map(36, 6, KIND_NOTE); // bass drum 
		
		//snares
		this.map(37, 2, KIND_NOTE | KIND_CIRCLE_AROUND); //cross stick 
		this.map(38, 2, KIND_NOTE); // acoustic snare 
		this.map(40, 2, KIND_NOTE); // electric snare 
		
		//hi-hats
		this.map(42, -2, KIND_CYMBAL | KIND_CLOSED); // closed high hat 
		this.map(46, -2, KIND_CYMBAL | KIND_OPEN); // open high hat 
		this.map(44, 8, KIND_CYMBAL); // foot hi-hat
		
		//effect cymbals
		this.map(49, -3, KIND_EFFECT_CYMBAL); // crash cymbal 
		this.map(57, -4, KIND_EFFECT_CYMBAL); // crash cymbal 2 
		this.map(55, -5, KIND_EFFECT_CYMBAL); // splash cymbal 
		this.map(52, -5, KIND_EFFECT_CYMBAL | KIND_CIRCLE_AROUND); // china cymbal 
		
		//ride cymbals
		this.map(51, -1, KIND_CYMBAL); // ride cymbal 
		this.map(59, 1, KIND_CYMBAL); // ride cymbal 2 
		this.map(53, -1, KIND_SLANTED_DIAMOND); // ride bell 
		
		//toms
		this.map(41, 5, KIND_NOTE); // low floor tom 
		this.map(43, 4, KIND_NOTE); // high floor tom 
		this.map(45, 3, KIND_NOTE); // low tom
		this.map(47, 1, KIND_NOTE); // low-mid tom
		this.map(48, 0, KIND_NOTE); // hi-mid tom 
		this.map(50, -1, KIND_NOTE); // high tom 
		
		//misc percussion
		this.map(54, 3, KIND_TRIANGLE); // tambourine 
		this.map(56, 0, KIND_TRIANGLE); // cowbell 
		this.map(31, 3, KIND_CYMBAL); // click sticks
		this.map(77, 2, KIND_TRIANGLE); // low wood block
		this.map(76, 1, KIND_TRIANGLE); // high wood block
		this.map(81, -3, KIND_TRIANGLE); // open triangle
		this.map(80, -3, KIND_TRIANGLE | KIND_CLOSED); // mute triangle
	}
	
	private void map(int key, int position, int kind) {
		this.mapping[key][MAP_POSITION] = position;
		this.mapping[key][MAP_KIND] = kind;
	}
	
	public int getPosition(int value) {
		if( value >= 0 && value < mapping.length ) {
			return mapping[value][MAP_POSITION];
		}
		return DEFAULT_POSITION;
	}
	
	public int getRenderType(int value) {
		if( value >= 0 && value < mapping.length ) {
			return mapping[value][MAP_KIND];
		}
		return DEFAULT_KIND;
	}
}
