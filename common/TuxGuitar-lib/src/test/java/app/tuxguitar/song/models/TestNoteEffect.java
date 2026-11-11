package app.tuxguitar.song.models;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.effects.TGEffectBend;

public class TestNoteEffect {

	private final TGFactory factory = new TGFactory();

	// every effect a note can carry, each set on its own
	private Map<String, Consumer<TGNoteEffect>> everyEffect() {
		Map<String, Consumer<TGNoteEffect>> effects = new LinkedHashMap<String, Consumer<TGNoteEffect>>();
		effects.put("bend", effect -> effect.setBend(bend()));
		effects.put("tremoloBar", effect -> effect.setTremoloBar(factory.newEffectTremoloBar()));
		effects.put("harmonic", effect -> effect.setHarmonic(factory.newEffectHarmonic()));
		effects.put("grace", effect -> effect.setGrace(factory.newEffectGrace()));
		effects.put("trill", effect -> effect.setTrill(factory.newEffectTrill()));
		effects.put("tremoloPicking", effect -> effect.setTremoloPicking(factory.newEffectTremoloPicking()));
		effects.put("vibrato", effect -> effect.setVibrato(true));
		effects.put("deadNote", effect -> effect.setDeadNote(true));
		effects.put("slide", effect -> effect.setSlide(true));
		effects.put("hammer", effect -> effect.setHammer(true));
		effects.put("ghostNote", effect -> effect.setGhostNote(true));
		effects.put("accentuatedNote", effect -> effect.setAccentuatedNote(true));
		effects.put("heavyAccentuatedNote", effect -> effect.setHeavyAccentuatedNote(true));
		effects.put("palmMute", effect -> effect.setPalmMute(true));
		effects.put("letRing", effect -> effect.setLetRing(true));
		effects.put("staccato", effect -> effect.setStaccato(true));
		effects.put("tapping", effect -> effect.setTapping(true));
		effects.put("slapping", effect -> effect.setSlapping(true));
		effects.put("popping", effect -> effect.setPopping(true));
		effects.put("fadeIn", effect -> effect.setFadeIn(true));
		return effects;
	}

	private TGEffectBend bend() {
		TGEffectBend bend = factory.newEffectBend();
		bend.addPoint(0, 4);
		return bend;
	}

	@Test
	public void aNewNoteHasNoEffect() {
		assertFalse(factory.newEffect().hasAnyEffect());
	}

	@Test
	public void eachEffectAloneCountsAsAnEffect() {
		for (Map.Entry<String, Consumer<TGNoteEffect>> entry : everyEffect().entrySet()) {
			TGNoteEffect effect = factory.newEffect();
			entry.getValue().accept(effect);
			assertTrue(effect.hasAnyEffect(), entry.getKey());
		}
	}

	@Test
	public void aBendWithoutPointsIsNoEffect() {
		TGNoteEffect effect = factory.newEffect();
		effect.setBend(factory.newEffectBend());
		assertFalse(effect.hasAnyEffect());
	}

	@Test
	public void anEffectSwitchedOffAgainIsNoEffect() {
		TGNoteEffect effect = factory.newEffect();
		effect.setPalmMute(true);
		effect.setPalmMute(false);
		assertFalse(effect.hasAnyEffect());
	}
}
