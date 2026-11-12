package app.tuxguitar.io.tg.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import app.tuxguitar.song.models.TGNoteEffect;

/**
 * The 3 byte header of a legacy .tg (1.0 - 1.5) note effect: one bit per effect the note
 * has, as the bits of the format that owns them. The bits are given in the order of
 * EFFECTS; a format that stores fewer effects (1.0 and 1.1 have no let ring) gives fewer bits.
 */
public class TGNoteEffectFlags {

	private static final List<Predicate<TGNoteEffect>> EFFECTS = Arrays.asList(
			TGNoteEffect::isBend, TGNoteEffect::isTremoloBar, TGNoteEffect::isHarmonic, TGNoteEffect::isGrace,
			TGNoteEffect::isTrill, TGNoteEffect::isTremoloPicking, TGNoteEffect::isVibrato, TGNoteEffect::isDeadNote,
			TGNoteEffect::isSlide, TGNoteEffect::isHammer, TGNoteEffect::isGhostNote, TGNoteEffect::isAccentuatedNote,
			TGNoteEffect::isHeavyAccentuatedNote, TGNoteEffect::isPalmMute, TGNoteEffect::isStaccato, TGNoteEffect::isTapping,
			TGNoteEffect::isSlapping, TGNoteEffect::isPopping, TGNoteEffect::isFadeIn, TGNoteEffect::isLetRing);

	private final int[] bits;

	/**
	 * The bits of bend, tremolo bar, harmonic, grace, trill, tremolo picking, vibrato, dead note,
	 * slide, hammer, ghost note, accentuated, heavy accentuated, palm mute, staccato, tapping,
	 * slapping, popping, fade in and let ring, in that order.
	 */
	public TGNoteEffectFlags(int... bits) {
		this.bits = bits;
	}

	public int create(TGNoteEffect effect) {
		int header = 0;
		for(int i = 0; i < this.bits.length; i ++) {
			if( EFFECTS.get(i).test(effect) ) {
				header |= this.bits[i];
			}
		}
		return header;
	}
}
