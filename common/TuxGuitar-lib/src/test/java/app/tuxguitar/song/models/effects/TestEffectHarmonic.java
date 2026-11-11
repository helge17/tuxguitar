package app.tuxguitar.song.models.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.factory.TGFactory;

/**
 * Pins the label of each harmonic type (TGEffectHarmonic.getKey), as drawn above the tablature.
 */
public class TestEffectHarmonic {

	@Test
	public void eachTypeHasItsLabelAndUnknownTypesNone() {
		StringBuilder keys = new StringBuilder();
		for (int type = -1; type <= TGEffectHarmonic.TYPE_SEMI + 1; type++) {
			TGEffectHarmonic harmonic = new TGFactory().newEffectHarmonic();
			harmonic.setType(type);
			keys.append(type).append('=').append(harmonic.getKey()).append(' ');
		}

		assertEquals("-1= 0= 1=N.H 2=A.H 3=T.H 4=P.H 5=S.H 6= ", keys.toString());
	}
}
