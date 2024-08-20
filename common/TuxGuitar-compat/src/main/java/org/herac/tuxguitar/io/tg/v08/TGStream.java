package org.herac.tuxguitar.io.tg.v08;

import org.herac.tuxguitar.io.base.TGFileFormat;

public class TGStream {
	
	protected static final String TG_VERSION = ("TG_DEVEL-0.8");
	
	protected static final String TG_FORMAT_CODE = ("tg");
	
	protected static final TGFileFormat TG_FORMAT = new TGFileFormat("TuxGuitar 0.8", "audio/x-tuxguitar", new String[]{ TG_FORMAT_CODE });
	
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
	
	protected static final int DURATION_DOTTED = 0x01;
	
	protected static final int DURATION_DOUBLE_DOTTED = 0x02;
	
	protected static final int DURATION_TUPLETO = 0x04;
	
	protected static final int EFFECT_VIBRATO = 0x01;
	
	protected static final int EFFECT_BEND = 0x02;
	
	protected static final int EFFECT_DEAD_NOTE = 0x04;
	
	protected static final int EFFECT_SLIDE = 0x08;
	
	protected static final int EFFECT_HAMMER = 0x10;
	
}
