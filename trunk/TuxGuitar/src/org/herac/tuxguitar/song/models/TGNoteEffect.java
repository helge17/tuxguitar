/*
 * Created on 26-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloBar;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import org.herac.tuxguitar.song.models.effects.TGEffectTrill;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TGNoteEffect {
	private TGEffectBend bend;
	private TGEffectTremoloBar tremoloBar;
	private TGEffectHarmonic harmonic;
	private TGEffectGrace grace;
	private TGEffectTrill trill;
	private TGEffectTremoloPicking tremoloPicking;
	private boolean vibrato;
	private boolean deadNote;
	private boolean slide;
	private boolean hammer;
	private boolean ghostNote;
	private boolean accentuatedNote;
	private boolean heavyAccentuatedNote;
	private boolean palmMute;
	private boolean staccato;
	private boolean tapping;
	private boolean slapping;
	private boolean popping;
	private boolean fadeIn;
	
	public TGNoteEffect(){
		this.bend = null;
		this.tremoloBar = null;
		this.harmonic = null;
		this.grace = null;
		this.trill = null;
		this.tremoloPicking = null;
		this.vibrato = false;
		this.deadNote = false;
		this.slide = false;
		this.hammer = false;
		this.ghostNote = false;
		this.accentuatedNote = false;
		this.heavyAccentuatedNote = false;
		this.palmMute = false;
		this.staccato = false;
		this.tapping = false;
		this.slapping = false;
		this.popping = false;
		this.fadeIn = false;
	}
	
	public boolean isDeadNote() {
		return this.deadNote;
	}
	
	public void setDeadNote(boolean deadNote) {
		this.deadNote = deadNote;
		//si es true, quito los efectos incompatibles
		if(this.isDeadNote()){
			this.tremoloBar = null;
			this.bend = null;
			this.trill = null;
			this.slide = false;
			this.hammer = false;
		}
	}
	
	public boolean isVibrato() {
		return this.vibrato;
	}
	public void setVibrato(boolean vibrato) {
		this.vibrato = vibrato;
		//si no es null quito los efectos incompatibles
		if(this.isVibrato()){
			this.trill = null;
		}
	}
	
	public TGEffectBend getBend() {
		return this.bend;
	}
	
	public void setBend(TGEffectBend bend) {
		this.bend = bend;
		//si no es null quito los efectos incompatibles
		if(this.isBend()){
			this.tremoloBar = null;
			this.trill = null;
			this.deadNote = false;
			this.slide = false;
			this.hammer = false;
		}
	}
	
	public boolean isBend() {
		return (this.bend != null && !this.bend.getPoints().isEmpty());
	}
	
	public TGEffectTremoloBar getTremoloBar() {
		return this.tremoloBar;
	}
	
	public void setTremoloBar(TGEffectTremoloBar tremoloBar) {
		this.tremoloBar = tremoloBar;
		//si no es null quito los efectos incompatibles
		if(this.isTremoloBar()){
			this.bend = null;
			this.trill = null;
			this.deadNote = false;
			this.slide = false;
			this.hammer = false;
		}
	}
	
	public boolean isTremoloBar() {
		return (this.tremoloBar != null);
	}
	
	
	public TGEffectTrill getTrill() {
		return this.trill;
	}
	
	public void setTrill(TGEffectTrill trill) {
		this.trill = trill;
		//si es true, quito los efectos incompatibles
		if(this.isTrill()){
			this.bend = null;
			this.tremoloBar = null;
			this.tremoloPicking = null;
			this.slide = false;
			this.hammer = false;
			this.deadNote = false;
			this.vibrato = false;
		}
	}
	
	public boolean isTrill() {
		return (this.trill != null);
	}
	
	public TGEffectTremoloPicking getTremoloPicking() {
		return this.tremoloPicking;
	}
	
	public void setTremoloPicking(TGEffectTremoloPicking tremoloPicking) {
		this.tremoloPicking = tremoloPicking;
		//si es true, quito los efectos incompatibles
		if(this.isTremoloPicking()){
			this.trill = null;
			this.bend = null;
			this.tremoloBar = null;
			this.slide = false;
			this.hammer = false;
			this.deadNote = false;
			this.vibrato = false;
		}
	}
	
	public boolean isTremoloPicking() {
		return (this.tremoloPicking != null);
	}
	
	public boolean isHammer() {
		return this.hammer;
	}
	
	public void setHammer(boolean hammer) {
		this.hammer = hammer;
		//si es true, quito los efectos incompatibles
		if(this.isHammer()){
			this.trill = null;
			this.tremoloBar = null;
			this.bend = null;
			this.deadNote = false;
			this.slide = false;
		}
	}
	
	public boolean isSlide() {
		return this.slide;
	}
	
	public void setSlide(boolean slide) {
		this.slide = slide;
		//si es true, quito los efectos incompatibles
		if(this.isSlide()){
			this.trill = null;
			this.tremoloBar = null;
			this.bend = null;
			this.deadNote = false;
			this.hammer = false;
		}
	}
	
	public boolean isGhostNote() {
		return this.ghostNote;
	}
	
	public void setGhostNote(boolean ghostNote) {
		this.ghostNote = ghostNote;
		//si es true, quito los efectos incompatibles
		if(this.isGhostNote()){
			this.accentuatedNote = false;
			this.heavyAccentuatedNote = false;
		}
	}
	
	public boolean isAccentuatedNote() {
		return this.accentuatedNote;
	}
	
	public void setAccentuatedNote(boolean accentuatedNote) {
		this.accentuatedNote = accentuatedNote;
		//si es true, quito los efectos incompatibles
		if(this.isAccentuatedNote()){
			this.ghostNote = false;
			this.heavyAccentuatedNote = false;
		}
	}
	
	public boolean isHeavyAccentuatedNote() {
		return this.heavyAccentuatedNote;
	}
	
	public void setHeavyAccentuatedNote(boolean heavyAccentuatedNote) {
		this.heavyAccentuatedNote = heavyAccentuatedNote;
		//si es true, quito los efectos incompatibles
		if(this.isHeavyAccentuatedNote()){
			this.ghostNote = false;
			this.accentuatedNote = false;
		}
	}
	
	public void setHarmonic(TGEffectHarmonic harmonic) {
		this.harmonic = harmonic;
	}
	
	public TGEffectHarmonic getHarmonic() {
		return this.harmonic;
	}
	
	public boolean isHarmonic() {
		return (this.harmonic != null);
	}
	
	public TGEffectGrace getGrace() {
		return this.grace;
	}
	
	public void setGrace(TGEffectGrace grace) {
		this.grace = grace;
	}
	
	public boolean isGrace() {
		return (this.grace != null);
	}
	
	public boolean isPalmMute() {
		return this.palmMute;
	}
	
	public void setPalmMute(boolean palmMute) {
		this.palmMute = palmMute;
		//si es true, quito los efectos incompatibles
		if(this.isPalmMute()){
			this.staccato = false;
		}
	}
	
	public boolean isStaccato() {
		return this.staccato;
	}
	
	public void setStaccato(boolean staccato) {
		this.staccato = staccato;
		//si es true, quito los efectos incompatibles
		if(this.isStaccato()){
			this.palmMute = false;
		}
	}
	
	public boolean isPopping() {
		return this.popping;
	}
	
	public void setPopping(boolean popping) {
		this.popping = popping;
		//si es true, quito los efectos incompatibles
		if(this.isPopping()){
			this.tapping = false;
			this.slapping = false;
		}
	}
	
	public boolean isSlapping() {
		return this.slapping;
	}
	
	public void setSlapping(boolean slapping) {
		this.slapping = slapping;
		//si es true, quito los efectos incompatibles
		if(this.isSlapping()){
			this.tapping = false;
			this.popping = false;
		}
	}
	
	public boolean isTapping() {
		return this.tapping;
	}
	
	public void setTapping(boolean tapping) {
		this.tapping = tapping;
		//si es true, quito los efectos incompatibles
		if(this.isTapping()){
			this.slapping = false;
			this.popping = false;
		}
	}
	
	public boolean isFadeIn() {
		return this.fadeIn;
	}
	
	public void setFadeIn(boolean fadeIn) {
		this.fadeIn = fadeIn;
	}
	
	public boolean hasAnyEffect(){
		return (isBend() ||
				isTremoloBar() ||
				isHarmonic() ||
				isGrace() ||
				isTrill() ||
				isTremoloPicking() ||
				isVibrato() ||
				isDeadNote() ||
				isSlide() ||
				isHammer() ||
				isGhostNote() ||
				isAccentuatedNote() ||
				isHeavyAccentuatedNote() ||
				isPalmMute() ||
				isStaccato() ||
				isTapping() ||
				isSlapping() ||
				isPopping() ||
				isFadeIn());
	}
	
	public TGNoteEffect clone(TGFactory factory){
		TGNoteEffect effect = factory.newEffect();
		effect.setVibrato(isVibrato());
		effect.setDeadNote(isDeadNote());
		effect.setSlide(isSlide());
		effect.setHammer(isHammer());
		effect.setGhostNote(isGhostNote());
		effect.setAccentuatedNote(isAccentuatedNote());
		effect.setHeavyAccentuatedNote(isHeavyAccentuatedNote());
		effect.setPalmMute(isPalmMute());
		effect.setStaccato(isStaccato());
		effect.setTapping(isTapping());
		effect.setSlapping(isSlapping());
		effect.setPopping(isPopping());
		effect.setFadeIn(isFadeIn());
		effect.setBend(isBend()?(TGEffectBend)this.bend.clone(factory):null);
		effect.setTremoloBar(isTremoloBar()?(TGEffectTremoloBar)this.tremoloBar.clone(factory):null);
		effect.setHarmonic(isHarmonic()?(TGEffectHarmonic)this.harmonic.clone(factory):null);
		effect.setGrace(isGrace()?(TGEffectGrace)this.grace.clone(factory):null);
		effect.setTrill(isTrill()?(TGEffectTrill)this.trill.clone(factory):null);
		effect.setTremoloPicking(isTremoloPicking()?(TGEffectTremoloPicking)this.tremoloPicking.clone(factory):null);
		return effect;
	}
	
}
