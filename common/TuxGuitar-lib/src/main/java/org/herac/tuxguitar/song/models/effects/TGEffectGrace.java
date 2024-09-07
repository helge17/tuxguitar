package org.herac.tuxguitar.song.models.effects;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGVelocities;

public abstract class TGEffectGrace {
	
	public static final int TRANSITION_NONE = 0;
	
	public static final int TRANSITION_SLIDE = 1;
	
	public static final int TRANSITION_BEND = 2;
	
	public static final int TRANSITION_HAMMER = 3;
	
	private int fret;
	private int duration;
	private int dynamic;
	private int transition;
	private boolean onBeat;
	private boolean dead;
	
	public TGEffectGrace() {
		this.fret = 0;
		this.duration = 1;
		this.dynamic = TGVelocities.DEFAULT;
		this.transition = TRANSITION_NONE;
		this.onBeat = false;
		this.dead = false;
	}
	
	public boolean isDead() {
		return this.dead;
	}
	
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public int getDynamic() {
		return this.dynamic;
	}
	
	public void setDynamic(int dynamic) {
		this.dynamic = dynamic;
	}
	
	public int getFret() {
		return this.fret;
	}
	
	public void setFret(int fret) {
		this.fret = fret;
	}
	
	public boolean isOnBeat() {
		return this.onBeat;
	}
	
	public void setOnBeat(boolean onBeat) {
		this.onBeat = onBeat;
	}
	
	public int getTransition() {
		return this.transition;
	}
	
	public void setTransition(int transition) {
		this.transition = transition;
	}
	
	public int getDurationTime(){
		//return (int)(((float)TGDuration.QUARTER_TIME / 16.00 ) * (float)getDuration());
		return (int)((TGDuration.QUARTER_TIME / 16.00 ) * getDuration());
	}
	
	public TGEffectGrace clone(TGFactory factory){
		TGEffectGrace effect = factory.newEffectGrace();
		effect.setFret(getFret());
		effect.setDuration(getDuration());
		effect.setDynamic(getDynamic());
		effect.setTransition(getTransition());
		effect.setOnBeat(isOnBeat());
		effect.setDead(isDead());
		return effect;
	}
	
}
