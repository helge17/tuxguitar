package app.tuxguitar.editor.action.transport;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;

/**
 * TGTransportModeAction sets the player's Play Mode to the one given, starting it over (so its Percentage of Tempo is
 * its first one), and reports success, so a refused change can be told from a done one.
 */
public class TestTransportModeAction {

	private final SongActionFixture fixture = new SongActionFixture();

	@Test
	public void thePlayModeIsSetAndTheActionSucceeds() {
		MidiPlayerMode mode = new MidiPlayerMode();
		mode.setType(MidiPlayerMode.TYPE_SIMPLE);
		mode.setSimplePercent(80);
		fixture.actionContext.setAttribute(TGTransportModeAction.ATTRIBUTE_PLAYER_MODE, mode);
		fixture.execute(new TGTransportModeAction(fixture.context));

		assertEquals(80, MidiPlayer.getInstance(fixture.context).getMode().getCurrentPercent());
		assertEquals("success", fixture.outcome());
	}
}
