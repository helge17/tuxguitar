package app.tuxguitar.player.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVelocities;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.song.models.effects.TGEffectBend;
import app.tuxguitar.song.models.effects.TGEffectGrace;
import app.tuxguitar.song.models.effects.TGEffectHarmonic;
import app.tuxguitar.song.models.effects.TGEffectTremoloBar;
import app.tuxguitar.song.models.effects.TGEffectTrill;

public class MidiSequenceParser {

	public static final int NO_CONTROL_CHANGE_PROGRAM_CHANGE = 0;	// all flags inhibited
	public static final int ADD_DEFAULT_CONTROLS = 0x01;
	public static final int ADD_MIXER_MESSAGES = 0x02;
	public static final int ADD_BANK_SELECT = 0x04;
	public static final int ADD_METRONOME = 0x08;
	public static final int ADD_FIRST_TICK_MOVE = 0x10;
	public static final int DEFAULT_PLAY_FLAGS = (ADD_METRONOME);
	public static final int DEFAULT_EXPORT_FLAGS = (ADD_FIRST_TICK_MOVE | ADD_DEFAULT_CONTROLS | ADD_MIXER_MESSAGES);

	private static final int DEFAULT_METRONOME_KEY = 37;
	private static final int DEFAULT_DURATION_PM = 60;
	private static final int DEFAULT_DURATION_DEAD = 30;
	private static final int DEFAULT_BEND = 64;
	private static final float DEFAULT_BEND_SEMI_TONE = 2.75f;

	private TGSong song;
	private TGSongManager songManager;
	private int flags;
	private int infoTrack;
	private int metronomeTrack;
	private int metronomeChannelId;
	private int firstTickMove;
	private int tempoPercent;
	private int transpose;
	private int sHeader;
	private int eHeader;
	private TreeMap<Long, TGTempo> tempoMap;	// ordered map of ticks where tempo changes
	private TreeMap<Long, Long> timestampMap;	// ordered map of timestamps (in ms) where tempo changes

	public MidiSequenceParser(TGSong song, TGSongManager songManager, int flags) {
		this.song = song;
		this.songManager = songManager;
		this.flags = flags;
		this.tempoPercent = 100;
		this.transpose = 0;
		this.sHeader = -1;
		this.eHeader = -1;
		this.firstTickMove = (int) ((flags & ADD_FIRST_TICK_MOVE) != 0 ? -TGDuration.QUARTER_TIME : 0);
		this.tempoMap = new TreeMap<Long, TGTempo>();
		this.timestampMap = new TreeMap<Long, Long>();
	}

	public int getInfoTrack(){
		return this.infoTrack;
	}

	public int getMetronomeTrack(){
		return this.metronomeTrack;
	}

	public TreeMap<Long, TGTempo> getTempoMap() {
		return this.tempoMap;
	}

	public TreeMap<Long, Long> getTimestampMap() {
		return this.timestampMap;
	}

	private long getTick(long tick){
		return (tick + this.firstTickMove);
	}

	public void setSHeader(int header) {
		this.sHeader = header;
	}

	public void setEHeader(int header) {
		this.eHeader = header;
	}

	public void setMetronomeChannelId(int metronomeChannelId) {
		this.metronomeChannelId = metronomeChannelId;
	}

	public void setTempoPercent(int tempoPercent) {
		this.tempoPercent = tempoPercent;
	}

	public void setTranspose(int transpose) {
		this.transpose = transpose;
	}

	public void parse(MidiSequenceHandler sequence) {
		this.infoTrack = 0;
		this.metronomeTrack = (sequence.getTracks() - 1);

		MidiSequenceHelper helper = new MidiSequenceHelper(sequence);
		MidiRepeatController controller = new MidiRepeatController(this.song, this.sHeader, this.eHeader);
		while(!controller.finished()){
			int index = controller.getIndex();
			long move = controller.getRepeatMove();
			controller.process();
			if( controller.shouldPlay()){
				helper.addMeasureHelper(new MidiMeasureHelper(index, move));
			}
		}

		this.addDefaultMessages(helper, this.song);

		boolean existsSoloTrack = false;
		for (int i = 0; i < this.song.countTracks(); i++) {
			existsSoloTrack |= this.song.getTrack(i).isSolo();
		}
		for (int i = 0; i < this.song.countTracks(); i++) {
			TGTrack track = this.song.getTrack(i);
			addTrack(helper, track, !track.isMute() && (!existsSoloTrack || track.isSolo()));
		}
		sequence.notifyFinish();
	}

	private void addDefaultMessages(MidiSequenceHelper sh, TGSong tgSong) {
		if( (this.flags & ADD_DEFAULT_CONTROLS) != 0) {
			Iterator<TGChannel> it = tgSong.getChannels();
			while ( it.hasNext() ){
				int channelId = it.next().getChannelId();
				sh.getSequence().addControlChange(getTick(TGDuration.QUARTER_TIME), getInfoTrack(), channelId, MidiControllers.RPN_MSB, 0);
				sh.getSequence().addControlChange(getTick(TGDuration.QUARTER_TIME), getInfoTrack(), channelId, MidiControllers.RPN_LSB, 0);
				sh.getSequence().addControlChange(getTick(TGDuration.QUARTER_TIME), getInfoTrack(), channelId, MidiControllers.DATA_ENTRY_MSB, 12);
				sh.getSequence().addControlChange(getTick(TGDuration.QUARTER_TIME), getInfoTrack(), channelId, MidiControllers.DATA_ENTRY_LSB, 0);
			}
		}
	}

	private void addTrack(MidiSequenceHelper sh, TGTrack track, boolean shallPlay) {
		TGChannel tgChannel = this.songManager.getChannel(this.song, track.getChannelId() );
		if( tgChannel != null ){
			TGMeasure previous = null;

			this.addBend(sh,track.getNumber(),TGDuration.QUARTER_TIME,DEFAULT_BEND, tgChannel.getChannelId(), -1, false);
			this.makeTrackChannel(sh, tgChannel, track);

			int mCount = sh.getMeasureHelpers().size();
			for( int mIndex = 0 ; mIndex < mCount ; mIndex++ ){
				MidiMeasureHelper mh = sh.getMeasureHelper( mIndex );

				TGMeasure measure = track.getMeasure(mh.getIndex());
				if(track.getNumber() == 1){
					addTimeSignature(sh,measure, previous, mh.getMove());
					addTempo(sh,measure, previous, mh.getMove());
					addMetronome(sh,measure.getHeader(), mh.getMove() );
				}
				if (shallPlay) {
					//agrego los pulsos
					addBeats( sh, tgChannel, track, measure, mIndex, mh.getMove() );
				}
				previous = measure;
			}
		}
	}

	private void addBeats(MidiSequenceHelper sh, TGChannel channel, TGTrack track, TGMeasure measure, int mIndex, long startMove) {
		int[] stroke = new int[track.stringCount()];
		TGBeat previous = null;
		for (int bIndex = 0; bIndex < measure.countBeats(); bIndex++) {
			TGBeat beat = measure.getBeat(bIndex);
			addNotes( sh, channel, track, beat, measure.getTempo(), mIndex, bIndex, startMove, getStroke(beat, previous, stroke) );
			previous = beat;
		}
	}

	private void addNotes( MidiSequenceHelper sh, TGChannel tgChannel, TGTrack track, TGBeat beat, TGTempo tempo, int mIndex,int bIndex, long startMove, int[] stroke) {
		for( int vIndex = 0; vIndex < beat.countVoices(); vIndex ++ ){
			TGVoice voice = beat.getVoice(vIndex);

			MidiTickHelper th = checkTripletFeel(voice,bIndex);
			for (int noteIdx = 0; noteIdx < voice.countNotes(); noteIdx++) {
				TGNote note = voice.getNote(noteIdx);
				if (!note.isTiedNote()) {
					PlayedNote played = new PlayedNote(sh, tgChannel, track, note, tempo, mIndex, bIndex, startMove);
					played.key = (this.transpose + fretKey(track, note, note.getValue()));
					played.start = applyStrokeStart(note, (th.getStart() + startMove) , stroke);
					played.duration = applyStrokeDuration(note, getRealNoteDuration(sh, track, note, tempo, th.getDuration(), mIndex,bIndex), stroke);
					played.velocity = getRealVelocity(sh, note, track, tgChannel, mIndex, bIndex);
					addPlayedNote(played);
				}
			}
		}
	}

	// key of a fret on the note's string, without transposition
	private int fretKey(TGTrack track, TGNote note, int fret) {
		return (track.getOffset() + fret + track.getStrings().get(note.getString() - 1).getValue());
	}

	private void addPlayedNote(PlayedNote played) {
		TGNoteEffect effect = played.note.getEffect();
		if (effect.isFadeIn() && ((this.flags & ADD_MIXER_MESSAGES) != 0)) {
			addFadeIn(played.sh, played.track.getNumber(), played.start, played.duration, played.tgChannel.getVolume(), played.channel);
		}
		if (effect.isGrace()) {
			addGrace(played);
		}
		// trill and tremolo picking replace the note
		if (effect.isTrill() && !played.percussion) {
			TGEffectTrill trill = effect.getTrill();
			addRepeatedNote(played, fretKey(played.track, played.note, trill.getFret()), trill.getDuration().getTime());
			return;
		}
		if (effect.isTremoloPicking()) {
			addRepeatedNote(played, played.key, effect.getTremoloPicking().getDuration().getTime());
			return;
		}
		addPitchEffect(played);
		int key = addHarmonic(played);
		addNote(played, Math.min(127, key), played.start, played.duration, played.velocity);
	}

	// the grace note is played before the note, or on the beat (delaying the note) when asked or when there is no room before
	private void addGrace(PlayedNote played) {
		TGEffectGrace grace = played.note.getEffect().getGrace();
		played.bendMode = !played.percussion;
		int graceKey = fretKey(played.track, played.note, grace.getFret());
		int graceLength = grace.getDurationTime();
		long graceDuration = (grace.isDead() ? applyStaticDuration(played.tempo, DEFAULT_DURATION_DEAD, graceLength) : graceLength);
		if (grace.isOnBeat() || (played.start - graceLength) < TGDuration.QUARTER_TIME) {
			played.start += graceLength;
			played.duration -= graceLength;
		}
		addNote(played, graceKey, played.start - graceLength, graceDuration, grace.getDynamic());
	}

	// repeats the note every length ticks, alternating its key with alternateKey, until less than 10 ticks of it remain
	private void addRepeatedNote(PlayedNote played, int alternateKey, long length) {
		long end = (played.start + played.duration);
		boolean realKey = true;
		for (long tick = played.start; tick + 10 < end; tick += length) {
			if ((tick + length) >= end) {
				length = ((end - tick) - 1);
			}
			addNote(played, (realKey ? played.key : alternateKey), tick, length, played.velocity);
			realKey = !realKey;
		}
	}

	// bend, tremolo bar, slide and vibrato exclude each other, in that order of precedence
	private void addPitchEffect(PlayedNote played) {
		if (played.percussion) {
			return;
		}
		TGNoteEffect effect = played.note.getEffect();
		int track = played.track.getNumber();
		if (effect.isBend()) {
			played.bendMode = true;
			addBend(played.sh, track, played.start, played.duration, effect.getBend(), played.channel, played.midiVoice, played.bendMode);
		} else if (effect.isTremoloBar()) {
			played.bendMode = true;
			addTremoloBar(played.sh, track, played.start, played.duration, effect.getTremoloBar(), played.channel, played.midiVoice, played.bendMode);
		} else if (effect.isSlide()) {
			played.bendMode = true;
			addSlide(played.sh, played.note, played.track, played.mIndex, played.bIndex, played.startMove, played.channel, played.midiVoice, played.bendMode);
		} else if (effect.isVibrato()) {
			played.bendMode = true;
			addVibrato(played.sh, track, played.start, played.duration, played.channel, played.midiVoice, played.bendMode);
		}
	}

	// plays the octave below the harmonic (and the fretted note for a semi harmonic), returns the key to play the note with
	private int addHarmonic(PlayedNote played) {
		TGNoteEffect effect = played.note.getEffect();
		if (!effect.isHarmonic() || played.percussion) {
			return played.key;
		}
		TGEffectHarmonic harmonic = effect.getHarmonic();
		int key;
		if (harmonic.isNatural()) {
			key = naturalHarmonicKey(played.key, played.note.getValue());
		} else {
			// artificial, tapped, pinch, semi
			if (harmonic.isSemi()) {
				addNote(played, Math.min(127, played.key), played.start, played.duration, Math.max(TGVelocities.MIN_VELOCITY, played.velocity - (TGVelocities.VELOCITY_INCREMENT * 3)));
			}
			key = (played.key + TGEffectHarmonic.NATURAL_FREQUENCIES[harmonic.getData()][1]);
		}
		if ((key - 12) > 0) {
			addNote(played, (key - 12), played.start, played.duration, Math.max(TGVelocities.MIN_VELOCITY, played.velocity - (TGVelocities.VELOCITY_INCREMENT * 4)));
		}
		return key;
	}

	private int naturalHarmonicKey(int key, int fret) {
		for (int[] frequency : TGEffectHarmonic.NATURAL_FREQUENCIES) {
			if ((fret % 12) == (frequency[0] % 12)) {
				return ((key + frequency[1]) - fret);
			}
		}
		return key;
	}

	private void addNote(PlayedNote played, int key, long start, long duration, int velocity) {
		addNote(played.sh, played.track.getNumber(), key, start, duration, velocity, played.channel, played.midiVoice, played.bendMode);
	}

	private void addNote(MidiSequenceHelper sh,int track, int key, long start, long duration, int velocity, int channel, int midiVoice, boolean bendMode) {
		sh.getSequence().addNoteOn(getTick(start),track,channel,fix(key),fix(velocity), midiVoice, bendMode);
		if( duration > 0 ){
			sh.getSequence().addNoteOff(getTick(start + duration),track,channel,fix(key),fix(velocity), midiVoice, bendMode);
		}
	}

	private void makeTrackChannel(MidiSequenceHelper sh, TGChannel channel, TGTrack track) {
		long tick = getTick(TGDuration.QUARTER_TIME);
		int trackNum = track.getNumber();
		if((this.flags & ADD_MIXER_MESSAGES) != 0){
			int channelId = channel.getChannelId();
			sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.VOLUME,fix(channel.getVolume()));
			sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.BALANCE,fix(channel.getBalance()));
			sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.CHORUS,fix(channel.getChorus()));
			sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.REVERB,fix(channel.getReverb()));
			sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.PHASER,fix(channel.getPhaser()));
			sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.TREMOLO,fix(channel.getTremolo()));
			sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.EXPRESSION, 127);
			if((this.flags & ADD_BANK_SELECT) != 0) {
				sh.getSequence().addControlChange(tick,trackNum,channelId,MidiControllers.BANK_SELECT, fix(channel.getBank(), 0, 128));
			}
			sh.getSequence().addProgramChange(tick,trackNum,channelId,fix(channel.getProgram()));
		}
		sh.getSequence().addTrackName(tick,trackNum,track.getName());
	}

	public void addMetronome(MidiSequenceHelper sh,TGMeasureHeader header, long startMove){
		if( (this.flags & ADD_METRONOME) != 0) {
			if( this.metronomeChannelId >= 0 ){
				long start = (startMove + header.getStart());
				long length = header.getTimeSignature().getDenominator().getTime();
				for(int i = 1; i <= header.getTimeSignature().getNumerator();i ++){
					addNote(sh,getMetronomeTrack(),DEFAULT_METRONOME_KEY,start,length,TGVelocities.DEFAULT,this.metronomeChannelId,-1,false);
					start += length;
				}
			}
		}
	}


	private void addBend(MidiSequenceHelper sh,int track, long tick,int bend, int channel, int midiVoice, boolean bendMode) {
		sh.getSequence().addPitchBend(getTick(tick), track, channel, fix(bend), midiVoice, bendMode);
	}

	public void addVibrato(MidiSequenceHelper sh,int track,long start, long duration,int channel, int midiVoice, boolean bendMode){
		long nextStart = start;
		long end = nextStart + duration;

		while(nextStart < end){
			nextStart = ((nextStart + 160 > end)?end:nextStart + 160);
			addBend(sh, track, nextStart, DEFAULT_BEND, channel, midiVoice, bendMode);
			nextStart = ((nextStart + 160 > end)?end:nextStart + 160);
			addBend(sh, track, nextStart, DEFAULT_BEND + (int)(DEFAULT_BEND_SEMI_TONE / 2.0f), channel, midiVoice, bendMode);
		}
		addBend(sh, track, nextStart, DEFAULT_BEND, channel, midiVoice, bendMode);
	}

	public void addBend(MidiSequenceHelper sh,int track,long start, long duration, TGEffectBend bend, int channel, int midiVoice, boolean bendMode){
		List<PitchPoint> curve = pitchCurve(bend.getPoints(), point -> start + point.getTime(duration),
				point -> DEFAULT_BEND + (int)(point.getValue() * DEFAULT_BEND_SEMI_TONE / TGEffectBend.SEMITONE_LENGTH));
		addPitchCurve(sh, track, curve, start + duration, channel, midiVoice, bendMode);
	}

	// a tremolo bar point's value counts whole tones
	public void addTremoloBar(MidiSequenceHelper sh,int track,long start, long duration, TGEffectTremoloBar effect, int channel, int midiVoice, boolean bendMode){
		List<PitchPoint> curve = pitchCurve(effect.getPoints(), point -> start + point.getTime(duration),
				point -> DEFAULT_BEND + (int)(point.getValue() * (DEFAULT_BEND_SEMI_TONE * 2)));
		addPitchCurve(sh, track, curve, start + duration, channel, midiVoice, bendMode);
	}

	// the points of a bend or tremolo bar as a pitch curve: when each point is, and its pitch bend value
	private <P> List<PitchPoint> pitchCurve(List<P> points, ToLongFunction<P> start, ToIntFunction<P> pitch) {
		List<PitchPoint> curve = new ArrayList<PitchPoint>();
		for(P point : points){
			curve.add(new PitchPoint(start.applyAsLong(point), pitch.applyAsInt(point)));
		}
		return curve;
	}

	// plays each point's pitch then steps towards the next point's, and returns to the normal pitch at the end
	private void addPitchCurve(MidiSequenceHelper sh, int track, List<PitchPoint> curve, long end, int channel, int midiVoice, boolean bendMode) {
		for(int i = 0; i < curve.size(); i++){
			PitchPoint point = curve.get(i).played();
			addBend(sh, track, point.start, point.pitch, channel, midiVoice, bendMode);
			if(i + 1 < curve.size()){
				addPitchSteps(sh, track, point, curve.get(i + 1), channel, midiVoice, bendMode);
			}
		}
		addBend(sh, track, end, DEFAULT_BEND, channel, midiVoice, bendMode);
	}

	// one pitch bend per value from a point's pitch to the next point's, a whole number of ticks apart
	private void addPitchSteps(MidiSequenceHelper sh, int track, PitchPoint from, PitchPoint to, int channel, int midiVoice, boolean bendMode) {
		int steps = Math.abs(to.pitch - from.pitch);
		int direction = Integer.signum(to.pitch - from.pitch);
		long width = (to.start - from.start) / Math.max(steps, 1);
		for(int step = 1; step <= steps; step++){
			addBend(sh, track, from.start + (width * step), from.pitch + (direction * step), channel, midiVoice, bendMode);
		}
	}

	private void addSlide(MidiSequenceHelper sh, TGNote note, TGTrack track, int mIndex, int bIndex, long startMove ,int channel, int midiVoice, boolean bendMode){
		MidiNoteHelper nextNote = this.getNextNote(sh, note, track, mIndex, bIndex, true);
		if( nextNote != null ){
			int value1 = note.getValue();
			int value2 = nextNote.getNote().getValue();

			long tick1 = note.getVoice().getBeat().getStart() + startMove;
			long tick2 = nextNote.getNote().getVoice().getBeat().getStart() + nextNote.getMeasure().getMove();

			// Make the Slide
			this.addSlide(sh, track.getNumber(), tick1, value1, tick2, value2, channel, midiVoice, bendMode);
			// Normalize the Bend
			this.addBend(sh,track.getNumber(), tick2 ,DEFAULT_BEND, channel, midiVoice, bendMode);
		}
	}

	public void addSlide(MidiSequenceHelper sh,int track,long tick1,int value1,long tick2,int value2,int channel, int midiVoice, boolean bendMode){
		long distance = (value2 - value1);
		long length = (tick2 - tick1);
		int points = (int)(length / (TGDuration.QUARTER_TIME / 8));
		for(int i = 1;i <= points; i ++){
			float tone = ((((length / points) * (float)i) * distance) / length);
			int bend = (DEFAULT_BEND + (int)(tone * (DEFAULT_BEND_SEMI_TONE * 2)));
			addBend(sh, track, tick1 + ( (length / points) * i), bend, channel, midiVoice, bendMode);
		}
	}

	private void addFadeIn(MidiSequenceHelper sh,int track,long start,long duration,int volume3,int channel){
		int expression = 31;
		int expressionIncrement = 1;
		long tick = start;
		long tickIncrement = (duration / ((127 - expression) / expressionIncrement));
		while( tick < (start + duration) && expression < 127 ) {
			sh.getSequence().addControlChange(getTick(tick),track,channel,MidiControllers.EXPRESSION, fix(expression));
			tick += tickIncrement;
			expression += expressionIncrement;
		}
		sh.getSequence().addControlChange(getTick((start + duration)),track,channel, MidiControllers.EXPRESSION, 127);
	}

	private void addTimeSignature(MidiSequenceHelper sh, TGMeasure currMeasure, TGMeasure prevMeasure, long startMove){
		boolean addTimeSignature = false;
		if (prevMeasure == null) {
			addTimeSignature = true;
		} else {
			int currNumerator = currMeasure.getTimeSignature().getNumerator();
			int currValue = currMeasure.getTimeSignature().getDenominator().getValue();
			int prevNumerator = prevMeasure.getTimeSignature().getNumerator();
			int prevValue = prevMeasure.getTimeSignature().getDenominator().getValue();
			if (currNumerator != prevNumerator || currValue != prevValue) {
				addTimeSignature = true;
			}
		}
		if (addTimeSignature) {
			sh.getSequence().addTimeSignature(getTick(currMeasure.getStart() + startMove), getInfoTrack(), currMeasure.getTimeSignature());
		}
	}

	private void addTempo(MidiSequenceHelper sh,TGMeasure currMeasure, TGMeasure prevMeasure,long startMove){
		boolean addTempo = false;
		if (prevMeasure == null) {
			addTempo = true;
		} else {
			if (currMeasure.getTempo().getInUSQ() != prevMeasure.getTempo().getInUSQ()) {
				addTempo = true;
			}
		}
		if (addTempo) {
			long tick = getTick(currMeasure.getStart() + startMove);
			int usq = (int)(currMeasure.getTempo().getInUSQ() * 100.00 / this.tempoPercent );
			sh.getSequence().addTempoInUSQ(tick, getInfoTrack(), usq);
			if (this.timestampMap.isEmpty()) {
				this.timestampMap.put(tick, 0l);
			} else {
				long t = this.timestampMap.lastEntry().getValue();
				Map.Entry<Long,TGTempo> lastTempoEntry = this.tempoMap.lastEntry();
				t += lastTempoEntry.getValue().getTicksInMillis(tick - lastTempoEntry.getKey());
				this.timestampMap.put(tick,t);
			}
			this.tempoMap.put(tick, currMeasure.getTempo());
		}
	}

	private long getRealNoteDuration(MidiSequenceHelper sh, TGTrack track, TGNote note, TGTempo tempo, long duration,int mIndex, int bIndex) {
		boolean letRing = (note.getEffect().isLetRing());
		boolean letRingBeatChanged = false;
		long lastEnd = (note.getVoice().getBeat().getStart() + note.getVoice().getDuration().getTime() + sh.getMeasureHelper(mIndex).getMove());
		long realDuration = duration;
		int nextBIndex = (bIndex + 1);
		int mCount = sh.getMeasureHelpers().size();
		for (int m = mIndex; m < mCount; m++) {
			MidiMeasureHelper mh = sh.getMeasureHelper( m );
			TGMeasure measure = track.getMeasure( mh.getIndex() );

			int beatCount = measure.countBeats();
			for (int b = nextBIndex; b < beatCount; b++) {
				TGBeat beat = measure.getBeat(b);
				TGVoice voice = beat.getVoice(note.getVoice().getIndex());
				if(!voice.isEmpty()){
					if(voice.isRestVoice()){
						return applyDurationEffects(note, tempo, realDuration);
					}
					int noteCount = voice.countNotes();
					for (int n = 0; n < noteCount; n++) {
						TGNote nextNote = voice.getNote( n );
						if (!nextNote.equals(note) || mIndex != m ) {
							if (letRing) {
								// letRing does not survive a repeat close
								if ((mIndex != m) && (note.getVoice().getBeat().getMeasure().getRepeatClose() > 0)) {
									return applyDurationEffects(note, tempo, realDuration);
								}
								// end of letRing chain?
								if (!nextNote.getEffect().isLetRing()) {
									realDuration += voice.getDuration().getTime();
									return applyDurationEffects(note, tempo, realDuration);
								}
							}
							if (nextNote.getString() == note.getString()) {
								if (nextNote.isTiedNote()) {
									realDuration += (mh.getMove() + beat.getStart() - lastEnd) + (nextNote.getVoice().getDuration().getTime());
									lastEnd = (mh.getMove() + beat.getStart() + voice.getDuration().getTime());
									letRing = (nextNote.getEffect().isLetRing());
									letRingBeatChanged = true;
								} else {
									return applyDurationEffects(note, tempo, realDuration);
								}
							}
						}
					}
					if(letRing && !letRingBeatChanged){
						realDuration += ( voice.getDuration().getTime() );
						lastEnd += voice.getDuration().getTime();
					}
					letRingBeatChanged = false;
				}
			}
			nextBIndex = 0;
		}
		return applyDurationEffects(note, tempo, realDuration);
	}

	private long applyDurationEffects(TGNote note, TGTempo tempo, long duration){
		//dead note
		if(note.getEffect().isDeadNote()){
			return applyStaticDuration(tempo, DEFAULT_DURATION_DEAD, duration);
		}
		//palm mute
		if(note.getEffect().isPalmMute()){
			return applyStaticDuration(tempo, DEFAULT_DURATION_PM, duration);
		}
		//staccato
		if(note.getEffect().isStaccato()){
			return (long)(duration * 50.00 / 100.00);
		}
		return duration;
	}

	private long applyStaticDuration(TGTempo tempo, long duration, long maximum ){
		long value = ( tempo.getQuarterValue() * duration / 60 );
		return (value < maximum ? value : maximum );
	}

	private int getRealVelocity(MidiSequenceHelper sh, TGNote note, TGTrack tgTrack, TGChannel tgChannel, int mIndex,int bIndex){
		int velocity = note.getVelocity();

		//Check for Hammer effect
		if(!tgChannel.isPercussionChannel()){
			MidiNoteHelper previousNote = getPreviousNote(sh, note,tgTrack,mIndex,bIndex,false);
			if(previousNote != null && previousNote.getNote().getEffect().isHammer()){
				velocity = Math.max(TGVelocities.MIN_VELOCITY,(velocity - 25));
			}
		}

		//Check for GhostNote effect
		if(note.getEffect().isGhostNote()){
			velocity = Math.max(TGVelocities.MIN_VELOCITY,(velocity - TGVelocities.VELOCITY_INCREMENT));
		}else if(note.getEffect().isAccentuatedNote()){
			velocity = Math.max(TGVelocities.MIN_VELOCITY,(velocity + TGVelocities.VELOCITY_INCREMENT));
		}else if(note.getEffect().isHeavyAccentuatedNote()){
			velocity = Math.max(TGVelocities.MIN_VELOCITY,(velocity + (TGVelocities.VELOCITY_INCREMENT * 2)));
		}

		return ((velocity > 127)?127:velocity);
	}

	private int[] getStroke(TGBeat beat, TGBeat previous, int[] stroke){
		int direction = beat.getStroke().getDirection();
		if( previous == null || !(direction == TGStroke.STROKE_NONE && previous.getStroke().getDirection() == TGStroke.STROKE_NONE)){
			if( direction == TGStroke.STROKE_NONE ){
				for( int i = 0 ; i < stroke.length ; i ++ ){
					stroke[ i ] = 0;
				}
			}else{
				int stringUseds = 0;
				int stringCount = 0;
				for( int vIndex = 0; vIndex < beat.countVoices(); vIndex ++ ){
					TGVoice voice = beat.getVoice(vIndex);
					for (int nIndex = 0; nIndex < voice.countNotes(); nIndex++) {
						TGNote note = voice.getNote(nIndex);
						if( !note.isTiedNote() ){
							stringUseds |= 0x01 << ( note.getString() - 1 );
							stringCount ++;
						}
					}
				}
				if( stringCount > 0 ){
					int strokeMove = 0;
					int strokeIncrement = beat.getStroke().getIncrementTime(beat);
					for( int i = 0 ; i < stroke.length ; i ++ ){
						int index = ( direction == TGStroke.STROKE_DOWN ? (stroke.length - 1) - i : i );
						if( (stringUseds & ( 0x01 << index ) ) != 0 ){
							stroke[ index ] = strokeMove;
							strokeMove += strokeIncrement;
						}
					}
				}
			}
		}
		return stroke;
	}

	private long applyStrokeStart( TGNote note, long start , int[] stroke){
		return (start + stroke[ note.getString() - 1 ]);
	}

	private long applyStrokeDuration( TGNote note, long duration , int[] stroke){
		return (duration > stroke[note.getString() - 1] ? (duration - stroke[ note.getString() - 1 ]) : duration );
	}

	private MidiTickHelper checkTripletFeel(TGVoice voice,int bIndex){
		int tripletFeel = voice.getBeat().getMeasure().getTripletFeel();
		if (tripletFeel == TGMeasureHeader.TRIPLET_FEEL_EIGHTH) {
			return applyTripletFeel(voice, bIndex, TGDuration.EIGHTH, TGDuration.QUARTER_TIME);
		}
		if (tripletFeel == TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH) {
			return applyTripletFeel(voice, bIndex, TGDuration.SIXTEENTH, (TGDuration.QUARTER_TIME / 2));
		}
		return new MidiTickHelper(voice.getBeat().getStart(), voice.getDuration().getTime());
	}

	// a pair of notes of the given duration value, the first one starting on a multiple of pairLength,
	// is played as a triplet quarter followed by a triplet eighth (relative to that value)
	private MidiTickHelper applyTripletFeel(TGVoice voice, int bIndex, int value, long pairLength){
		long bStart = voice.getBeat().getStart();
		long bDuration = voice.getDuration().getTime();
		if (!voice.getDuration().isEqual(newDuration(value))) {
			return new MidiTickHelper(bStart, bDuration);
		}
		long tripletTime = newTripletDuration(value).getTime();
		if ((bStart % pairLength) == 0) {
			// first time
			if (isFirstOfTripletPair(voice, bIndex, value)) {
				bDuration = (tripletTime * 2);
			}
		} else if ((bStart % (pairLength / 2)) == 0 && isSecondOfTripletPair(voice, bIndex, value)) {
			// second time
			bStart = ((bStart - bDuration) + (tripletTime * 2));
			bDuration = tripletTime;
		}
		return new MidiTickHelper(bStart, bDuration);
	}

	// no next note in the voice, or it starts after a gap, or it has the same duration
	private boolean isFirstOfTripletPair(TGVoice voice, int bIndex, int value){
		TGVoice next = getNextBeat(voice, bIndex);
		return (next == null || next.getBeat().getStart() > (voice.getBeat().getStart() + voice.getDuration().getTime()) || next.getDuration().isEqual(newDuration(value)));
	}

	// no previous note in the voice, or it starts more than one such duration earlier, or it has the same duration
	private boolean isSecondOfTripletPair(TGVoice voice, int bIndex, int value){
		TGVoice previous = getPreviousBeat(voice, bIndex);
		return (previous == null || previous.getBeat().getStart() < (voice.getBeat().getStart() - voice.getDuration().getTime()) || previous.getDuration().isEqual(newDuration(value)));
	}

	private TGDuration newTripletDuration(int value){
		TGDuration duration = newDuration(value);
		duration.getDivision().setEnters(3);
		duration.getDivision().setTimes(2);
		return duration;
	}

	private TGDuration newDuration(int value){
		TGDuration duration = this.songManager.getFactory().newDuration();
		duration.setValue(value);
		return duration;
	}

	private TGVoice getPreviousBeat(TGVoice beat,int bIndex){
		TGVoice previous = null;
		for (int b = bIndex - 1; b >= 0; b--) {
			TGBeat current = beat.getBeat().getMeasure().getBeat( b );
			if(current.getStart() < beat.getBeat().getStart() && !current.getVoice(beat.getIndex()).isEmpty()){
				if(previous == null || current.getStart() > previous.getBeat().getStart()){
					previous = current.getVoice(beat.getIndex());
				}
			}
		}
		return previous;
	}

	private TGVoice getNextBeat(TGVoice beat,int bIndex){
		TGVoice next = null;
		for (int b = bIndex + 1; b < beat.getBeat().getMeasure().countBeats(); b++) {
			TGBeat current = beat.getBeat().getMeasure().getBeat( b );
			if(current.getStart() > beat.getBeat().getStart() && !current.getVoice(beat.getIndex()).isEmpty()){
				if(next == null || current.getStart() < next.getBeat().getStart()){
					next = current.getVoice(beat.getIndex());
				}
			}
		}
		return next;
	}

	private MidiNoteHelper getNextNote(MidiSequenceHelper sh, TGNote note,TGTrack track, int mIndex, int bIndex, boolean breakAtRest){
		int nextBIndex = (bIndex + 1);
		int measureCount = sh.getMeasureHelpers().size();
		for (int m = mIndex; m < measureCount; m++) {
			MidiMeasureHelper mh = sh.getMeasureHelper( m );

			TGMeasure measure = track.getMeasure( mh.getIndex() );
			int beatCount = measure.countBeats();
			for (int b = nextBIndex; b < beatCount; b++) {
				TGBeat beat = measure.getBeat( b );
				TGVoice voice = beat.getVoice( note.getVoice().getIndex() );
				if( !voice.isEmpty() ){
					int noteCount = voice.countNotes();
					for (int n = 0; n < noteCount; n++) {
						TGNote nextNote = voice.getNote( n );
						if(nextNote.getString() == note.getString()){
							return new MidiNoteHelper(mh,nextNote);
						}
					}
					if( breakAtRest ){
						return null;
					}
				}
			}
			nextBIndex = 0;
		}
		return null;
	}

	private MidiNoteHelper getPreviousNote(MidiSequenceHelper pHelper, TGNote note,TGTrack track, int mIndex, int bIndex, boolean breakAtRest){
		int nextBIndex = bIndex;
		for (int m = mIndex; m >= 0; m--) {
			MidiMeasureHelper mh = pHelper.getMeasureHelper( m );

			TGMeasure measure = track.getMeasure( mh.getIndex() );
			if( this.sHeader == -1 || this.sHeader <= measure.getNumber() ){
				nextBIndex = (nextBIndex < 0 ? measure.countBeats() : nextBIndex);
				for (int b = (nextBIndex - 1); b >= 0; b--) {
					TGBeat beat = measure.getBeat( b );
					TGVoice voice = beat.getVoice( note.getVoice().getIndex() );
					if( !voice.isEmpty() ){
						int noteCount = voice.countNotes();
						for (int n = 0; n < noteCount; n ++) {
							TGNote current = voice.getNote( n );
							if(current.getString() == note.getString()){
								return new MidiNoteHelper(mh,current);
							}
						}
						if( breakAtRest ){
							return null;
						}
					}
				}
			}
			nextBIndex = -1;
		}
		return null;
	}

	private int fix(int value, int minimum, int maximum) {
		return (value >= minimum ? value <= maximum ? value : maximum : minimum);
	}

	private int fix(int value) {
		return this.fix(value, 0, 127);
	}

	// a point of a pitch curve: when it is, and its pitch bend value, which may lie outside the MIDI range
	private class PitchPoint {
		private final long start;
		private final int pitch;

		public PitchPoint(long start, int pitch){
			this.start = start;
			this.pitch = pitch;
		}

		// the point as played, its pitch within the MIDI range
		public PitchPoint played() {
			return new PitchPoint(this.start, fix(this.pitch));
		}
	}

	private class MidiTickHelper{
		private long start;
		private long duration;

		public MidiTickHelper(long start,long duration){
			this.start = start;
			this.duration = duration;
		}

		public long getDuration() {
			return this.duration;
		}

		public long getStart() {
			return this.start;
		}
	}

	// a (not tied) note being turned into MIDI events: where it is, and the values its effects adjust
	private class PlayedNote {
		private final MidiSequenceHelper sh;
		private final TGChannel tgChannel;
		private final TGTrack track;
		private final TGNote note;
		private final TGTempo tempo;
		private final int mIndex;
		private final int bIndex;
		private final long startMove;
		private final int channel;
		private final int midiVoice;
		private final boolean percussion;
		private int key;
		private long start;
		private long duration;
		private int velocity;
		private boolean bendMode;

		public PlayedNote(MidiSequenceHelper sh, TGChannel tgChannel, TGTrack track, TGNote note, TGTempo tempo, int mIndex, int bIndex, long startMove){
			this.sh = sh;
			this.tgChannel = tgChannel;
			this.track = track;
			this.note = note;
			this.tempo = tempo;
			this.mIndex = mIndex;
			this.bIndex = bIndex;
			this.startMove = startMove;
			this.channel = tgChannel.getChannelId();
			this.midiVoice = note.getString();
			this.percussion = tgChannel.isPercussionChannel();
		}
	}

	private class MidiNoteHelper {

		private MidiMeasureHelper measure;
		private TGNote note;

		public MidiNoteHelper(MidiMeasureHelper measure, TGNote note){
			this.measure = measure;
			this.note = note;
		}

		public MidiMeasureHelper getMeasure() {
			return this.measure;
		}

		public TGNote getNote() {
			return this.note;
		}
	}

	private class MidiMeasureHelper {

		private int index;
		private long move;

		public MidiMeasureHelper(int index,long move){
			this.index = index;
			this.move = move;
		}

		public int getIndex() {
			return this.index;
		}

		public long getMove() {
			return this.move;
		}
	}

	private class MidiSequenceHelper {

		private List<MidiMeasureHelper> measureHeaderHelpers;
		private MidiSequenceHandler sequence;

		public MidiSequenceHelper(MidiSequenceHandler sequence){
			this.sequence = sequence;
			this.measureHeaderHelpers = new ArrayList<MidiMeasureHelper>();
		}

		public MidiSequenceHandler getSequence(){
			return this.sequence;
		}

		public void addMeasureHelper( MidiMeasureHelper helper ){
			this.measureHeaderHelpers.add( helper );
		}

		public List<MidiMeasureHelper> getMeasureHelpers(){
			return this.measureHeaderHelpers;
		}

		public MidiMeasureHelper getMeasureHelper( int index ){
			return this.measureHeaderHelpers.get( index );
		}

	}
}
