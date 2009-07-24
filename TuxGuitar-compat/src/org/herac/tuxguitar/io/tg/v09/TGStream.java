package org.herac.tuxguitar.io.tg.v09;

public class TGStream {
	
	protected static final String TG_VERSION = ("TuxGuitar File Format - 0.9");
	
	protected static final int TRACK_LYRICS = 0x01;
	
	protected static final int CHANNEL_SOLO = 0x01;
	
	protected static final int CHANNEL_MUTE = 0x02;
	
	protected static final int MEASURE_HEADER_TIMESIGNATURE = 0x01;
	
	protected static final int MEASURE_HEADER_TEMPO = 0x02;
	
	protected static final int MEASURE_HEADER_OPEN_REPEAT = 0x04;
	
	protected static final int MEASURE_HEADER_CLOSE_REPEAT = 0x08;
	
	protected static final int MEASURE_HEADER_MARKER = 0x10;
	
	protected static final int MEASURE_HEADER_TRIPLET_FEEL = 0x20;
	
	protected static final int MEASURE_CLEF = 0x01;
	
	protected static final int MEASURE_KEYSIGNATURE = 0x02;
	
	protected static final int COMPONENT_NOTE = 0x01;
	
	protected static final int COMPONENT_SILENCE = 0x02;
	
	protected static final int COMPONENT_TIEDNOTE = 0x04;
	
	protected static final int COMPONENT_EFFECT = 0x08;
	
	protected static final int COMPONENT_NEXT_BEAT = 0x10;
	
	protected static final int COMPONENT_NEXT_DURATION = 0x20;
	
	protected static final int COMPONENT_VELOCITY = 0x40;
	
	protected static final int DURATION_DOTTED = 0x01;
	
	protected static final int DURATION_DOUBLE_DOTTED = 0x02;
	
	protected static final int DURATION_NO_TUPLET = 0x04;
	
	protected static final int EFFECT_BEND = 0x01;
	
	protected static final int EFFECT_TREMOLO_BAR = 0x02;
	
	protected static final int EFFECT_HARMONIC = 0x04;
	
	protected static final int EFFECT_GRACE = 0x08;
	
	protected static final int EFFECT_TRILL = 0x010;
	
	protected static final int EFFECT_TREMOLO_PICKING = 0x020;
	
	protected static final int EFFECT_VIBRATO = 0x040;
	
	protected static final int EFFECT_DEAD = 0x080;
	
	protected static final int EFFECT_SLIDE = 0x0100;
	
	protected static final int EFFECT_HAMMER = 0x0200;
	
	protected static final int EFFECT_GHOST = 0x0400;
	
	protected static final int EFFECT_ACCENTUATED = 0x0800;
	
	protected static final int EFFECT_HEAVY_ACCENTUATED = 0x01000;
	
	protected static final int EFFECT_PALM_MUTE = 0x02000;
	
	protected static final int EFFECT_STACCATO = 0x04000;
	
	protected static final int EFFECT_TAPPING = 0x08000;
	
	protected static final int EFFECT_SLAPPING = 0x010000;
	
	protected static final int EFFECT_POPPING = 0x020000;
	
	protected static final int EFFECT_FADE_IN = 0x040000;
	
	protected static final int GRACE_FLAG_DEAD = 0x01;
	
	protected static final int GRACE_FLAG_ON_BEAT = 0x02;
}
