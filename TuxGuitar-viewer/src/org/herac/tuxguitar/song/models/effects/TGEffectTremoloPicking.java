package org.herac.tuxguitar.song.models.effects;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGDuration;

public abstract class TGEffectTremoloPicking {
	
	private TGDuration duration;
	
	public TGEffectTremoloPicking(TGFactory factory) {
		this.duration = factory.newDuration();
	}
	
	public TGDuration getDuration() {
		return this.duration;
	}
	
	public void setDuration(TGDuration duration) {
		this.duration = duration;
	}
	
	public TGEffectTremoloPicking clone(TGFactory factory){
		TGEffectTremoloPicking effect = factory.newEffectTremoloPicking();
		effect.getDuration().setValue(getDuration().getValue());
		effect.getDuration().setDotted(getDuration().isDotted());
		effect.getDuration().setDoubleDotted(getDuration().isDoubleDotted());
		effect.getDuration().getDivision().setEnters(getDuration().getDivision().getEnters());
		effect.getDuration().getDivision().setTimes(getDuration().getDivision().getTimes());
		return effect;
	}
	
}
