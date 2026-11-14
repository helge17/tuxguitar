package app.tuxguitar.midi.synth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.util.TGContext;

/**
 * A synthesizer opening plays through the first output an app registered that takes it, else through its own. Each
 * output writes down what it was asked.
 */
public class TestSynthOutput {

	private final TGContext context = new TGContext();
	private final TGSynthModel synthesizer = new TGSynthModel(this.context);
	private final List<String> asked = new ArrayList<String>();

	private final class Output implements TGSynthOutput {
		private final String name;
		private final boolean takes;

		Output(String name, boolean takes) {
			this.name = name;
			this.takes = takes;
		}

		public boolean play(TGSynthModel played) {
			asked.add(this.name + " play " + (played == synthesizer));
			return this.takes;
		}

		public void stop(TGSynthModel stopped) {
			asked.add(this.name + " stop");
		}
	}

	private TGSynthOutput register(String name, boolean takes) {
		TGSynthOutput output = new Output(name, takes);
		TGSynthManager.getInstance(this.context).addExtension(TGSynthOutput.class, output);
		return output;
	}

	@Test
	void withNoOutputRegisteredItPlaysThroughItsOwn() {
		TGSynthOutput own = new Output("own", true);
		assertSame(own, TGSynthOutput.open(this.context, this.synthesizer, own));
		assertEquals(List.of("own play true"), this.asked);
	}

	@Test
	void itPlaysThroughTheFirstRegisteredOutputThatTakesIt() {
		register("refusing", false);
		TGSynthOutput taking = register("taking", true);
		register("later", true);
		assertSame(taking, TGSynthOutput.open(this.context, this.synthesizer, new Output("own", true)));
		assertEquals(List.of("refusing play true", "taking play true"), this.asked);
	}

	@Test
	void whenNoRegisteredOutputTakesItItPlaysThroughItsOwn() {
		register("refusing", false);
		TGSynthOutput own = new Output("own", true);
		assertSame(own, TGSynthOutput.open(this.context, this.synthesizer, own));
		assertEquals(List.of("refusing play true", "own play true"), this.asked);
	}
}
