package org.herac.tuxguitar.io.tg.v10;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.util.TGVersion;

public class TGStream {
	
	public static final String TG_FORMAT_NAME = ("TuxGuitar File Format");
	
	public static final String TG_FORMAT_VERSION = (TG_FORMAT_NAME + " - " + new TGVersion(1,0,0).getVersion() );
	
	public static final String TG_FORMAT_EXTENSION = (".tg");
	
	protected static final int TRACK_LYRICS = 0x01;
	
	protected static final int CHANNEL_SOLO = 0x01;
	
	protected static final int CHANNEL_MUTE = 0x02;
	
	protected static final int MEASURE_HEADER_TIMESIGNATURE = 0x01;
	
	protected static final int MEASURE_HEADER_TEMPO = 0x02;
	
	protected static final int MEASURE_HEADER_REPEAT_OPEN = 0x04;
	
	protected static final int MEASURE_HEADER_REPEAT_CLOSE = 0x08;
	
	protected static final int MEASURE_HEADER_REPEAT_ALTERNATIVE = 0x10;
	
	protected static final int MEASURE_HEADER_MARKER = 0x20;
	
	protected static final int MEASURE_HEADER_TRIPLET_FEEL = 0x40;
	
	protected static final int MEASURE_CLEF = 0x01;
	
	protected static final int MEASURE_KEYSIGNATURE = 0x02;
	
	protected static final int BEAT_HAS_NEXT = 0x01;
	
	protected static final int BEAT_NEXT_DURATION = 0x02;
	
	protected static final int BEAT_HAS_NOTES = 0x04;
	
	protected static final int BEAT_HAS_CHORD = 0x08;
	
	protected static final int BEAT_HAS_TEXT = 0x10;
	
	protected static final int NOTE_HAS_NEXT = 0x01;
	
	protected static final int NOTE_TIED = 0x02;
	
	protected static final int NOTE_EFFECT = 0x04;
	
	protected static final int NOTE_VELOCITY = 0x08;
	
	protected static final int DURATION_DOTTED = 0x01;
	
	protected static final int DURATION_DOUBLE_DOTTED = 0x02;
	
	protected static final int DURATION_NO_TUPLE = 0x04;
	
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
	
	protected class TGBeatData {
		private long start;
		private int velocity;
		private TGDuration duration;
		
		protected TGBeatData(TGMeasure measure){
			this.init(measure);
		}
		
		private void init(TGMeasure measure){
			this.setStart(measure.getStart());
			this.setVelocity(TGVelocities.DEFAULT);
			this.setDuration(new TGFactory().newDuration());
		}
		
		public TGDuration getDuration() {
			return this.duration;
		}
		
		public void setDuration(TGDuration duration) {
			this.duration = duration;
		}
		
		public long getStart() {
			return this.start;
		}
		
		public void setStart(long start) {
			this.start = start;
		}
		
		public int getVelocity() {
			return this.velocity;
		}
		
		public void setVelocity(int velocity) {
			this.velocity = velocity;
		}
	}
}
