package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.audio.action.TGArmTrackAction;
import app.tuxguitar.audio.device.TGAudioDeviceException;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.player.base.MidiChannel;
import app.tuxguitar.player.base.MidiOutputPort;
import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiSynthesizer;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGTrack;

/**
 * While the live device runs, TGLiveInput keeps an armed track monitored while stopped: when its channel is not open on
 * the TuxGuitar Synthesizer, it has the player open the song's channels (at install, and after each action). The MIDI
 * Ports' synthesizer writes down the channels it opens. The song has a guitar track and an audio track.
 */
public class TestLiveInput {

	private final SongActionFixture fixture = SongActionFixture.onDocument();
	private final MidiPlayer player = MidiPlayer.getInstance(this.fixture.context);
	private final TGAudioSession session = TGAudioSession.getInstance(this.fixture.context);
	private final TGTrack audio = this.fixture.songManager.addAudioTrack(this.fixture.song, TGAudioInput.mono(1));
	private final List<Integer> opened = new ArrayList<Integer>();
	private final TestBackend backend = new TestBackend(TestLiveDevice.RAMPS, 0, 0);

	private final class Port implements MidiOutputPort, MidiSynthesizer {
		private final String key;

		Port(String key) {
			this.key = key;
		}

		public String getKey() { return this.key; }
		public String getName() { return this.key; }
		public void open() { }
		public void close() { }
		public void check() { }
		public MidiSynthesizer getSynthesizer() { return this; }
		public MidiChannel openChannel(int channelId) { opened.add(channelId); return null; }
		public void closeChannel(MidiChannel midiChannel) { }
		public boolean isChannelOpen(MidiChannel midiChannel) { return true; }
		public boolean isBusy() { return false; }
	}

	public TestLiveInput() throws MidiPlayerException {
		List<MidiOutputPort> ports = Arrays.<MidiOutputPort>asList(new Port(TGAudioSession.SYNTHESIZER_PORT_KEY), new Port("other"));
		this.player.addOutputPortProvider(new MidiOutputPortProvider() {
			public List<MidiOutputPort> listPorts() { return ports; }
			public void closeAll() { }
		});
		this.player.openOutputPort(TGAudioSession.SYNTHESIZER_PORT_KEY);
	}

	private void arm(TGMonitoring monitoring) {
		this.session.arm(this.audio);
		this.session.setMonitoring(this.audio, monitoring);
	}

	// the channels opened while TGLiveInput was installed on a live device
	private List<Integer> install(Runnable meanwhile) throws TGAudioDeviceException {
		try (TGLiveDevice device = TGLiveDevice.open(this.backend, 0, 2, 0); TGLiveInput input = TGLiveInput.install(this.fixture.context, device)) {
			meanwhile.run();
		}
		return this.opened;
	}

	// an action that succeeds: arming the audio track
	private void armByAction() {
		this.fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, this.audio);
		this.fixture.execute(new TGArmTrackAction(this.fixture.context));
	}

	@Test
	public void installedItOpensTheSongsChannelsForAnArmedTrackMonitoredAuto() throws TGAudioDeviceException {
		arm(TGMonitoring.AUTO);
		assertEquals(List.of(this.fixture.song.getTrack(0).getChannelId(), this.audio.getChannelId()), install(() -> { }));
	}

	@Test
	public void aTrackMonitoredOffOpensNothing() throws TGAudioDeviceException {
		arm(TGMonitoring.OFF);
		assertEquals(List.of(), install(() -> { }));
	}

	@Test
	public void whileThePlayerRunsItOpensNothing() throws TGAudioDeviceException {
		arm(TGMonitoring.ALWAYS);
		this.player.setRunning(true);
		assertEquals(List.of(), install(() -> { }));
	}

	@Test
	public void throughAnotherMidiPortItOpensNothing() throws TGAudioDeviceException {
		this.player.openOutputPort("other");
		arm(TGMonitoring.ALWAYS);
		assertEquals(List.of(), install(() -> { }));
	}

	@Test
	public void afterAnActionItOpensTheChannelsOfATrackArmedSince() throws TGAudioDeviceException {
		this.session.setMonitoring(this.audio, TGMonitoring.AUTO);
		assertEquals(2, install(this::armByAction).size());
	}

	@Test
	public void closedItOpensNothingMore() throws TGAudioDeviceException {
		this.session.setMonitoring(this.audio, TGMonitoring.AUTO);
		install(() -> { });
		armByAction();
		assertEquals(List.of(), this.opened);
	}
}
