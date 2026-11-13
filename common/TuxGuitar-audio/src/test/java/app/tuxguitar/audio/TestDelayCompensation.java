package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.midi.synth.TGProgramPropertiesUtil;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthChannelProperties;
import app.tuxguitar.midi.synth.impl.GervillProcessor;
import app.tuxguitar.midi.synth.impl.GervillProcessorFactory;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGPropertiesManager;

/**
 * Plugin delay compensation, offline: each tap is read its own chain's latency later, so the taps are together from
 * their samples, and the render says how much later the output sounds them: the slowest chain's latency, the General
 * MIDI drums' (Gervill's) counting whether or not they click.
 *
 * The song is one measure of 4/4 at 120 bpm whose guitar track plays General MIDI program 80 and A4 as a quarter note
 * on beat 1. Its sound chain can be a custom program (the channel's synth.program parameters): Gervill, saved on
 * program 0 (the channel's program replaces it), followed by an effect that delays by its latency and reports it.
 */
public class TestDelayCompensation {

	private final TGContext context = new TGContext();
	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();

	private final TGTrack track = song.getTrack(0);

	public TestDelayCompensation() {
		TGPropertiesManager.getInstance(this.context).setPropertiesFactory(MapProperties::new);
		DelayingEffect.register(this.context);
		this.channel().setProgram((short) 80);
		TGNote note = this.songManager.getFactory().newNote();
		note.setValue(5);
		note.setString(1);
		TGBeat beat = this.songManager.getFactory().newBeat();
		beat.setStart(this.track.getMeasure(0).getStart());
		beat.getVoice(0).addNote(note);
		this.track.getMeasure(0).addBeat(beat);
	}

	private TGChannel channel() {
		return this.songManager.getChannel(this.song, this.track.getChannelId());
	}

	private static TGProgramElement element(String type, String parameter, String value) {
		TGProgramElement element = new TGProgramElement();
		element.setId(type);
		element.setType(type);
		element.setParameter(parameter, value);
		return element;
	}

	// the track's sound chain: Gervill saved on program 0, then an effect delaying by the latency
	private void soundChain(int latency) {
		this.channel().removeParameters();
		TGProgram program = new TGProgram();
		program.setReceiver(element(GervillProcessorFactory.TYPE, "gervil.program", "0:0"));
		program.addOutput(element(DelayingEffect.TYPE, "latency", Integer.toString(latency)));
		TGSynthChannelProperties properties = new TGSynthChannelProperties();
		TGProgramPropertiesUtil.setProgram(properties, TGSynthChannel.CUSTOM_PROGRAM_PREFIX, program);
		properties.getProperties().forEach((key, value) -> {
			TGChannelParameter parameter = this.songManager.getFactory().newChannelParameter();
			parameter.setKey(key);
			parameter.setValue(value);
			this.channel().addParameter(parameter);
		});
	}

	private TGRender render(int sampleRate) throws MidiPlayerException {
		return render(sampleRate, true);
	}

	// synthesizer: the MIDI Port is the TuxGuitar Synthesizer
	private TGRender render(int sampleRate, boolean synthesizer) throws MidiPlayerException {
		TGAudioSession session = TGAudioSession.getInstance(this.context);
		session.setDevice(new SilentDevice() {
			public int getSampleRate() {
				return sampleRate;
			}
		});
		MidiPlayerMode mode = new MidiPlayerMode();
		List<TGPlayedMeasure> played = TGSongTime.play(this.song, mode, this.song.getMeasureHeader(0).getStart(), Long.MAX_VALUE, 1);
		return new TGRenderer(this.context, this.songManager, this.song, session).render(new TGTimeline(this.song, played, sampleRate), mode, false, synthesizer);
	}

	// Gervill's two control periods at the device's rate
	@Test
	public void aGeneralMidiTrackSoundsAsLateAsGervillAtTheDevicesRate() throws MidiPlayerException {
		assertEquals(GervillProcessor.latency(44100) + " " + GervillProcessor.latency(48000), render(44100).getLatency() + " " + render(48000).getLatency());
	}

	// the chain's latency, Gervill's and the effect's, is taken out of the tap: the note sounds from its sample as it does
	// with the effect delaying by nothing, sample for sample; the output is as late as the chain
	@Test
	public void aSoundChainsLatencyIsTakenOutOfItsTap() throws MidiPlayerException {
		this.soundChain(0);
		float[] plain = render(44100).getTap(this.track);
		this.soundChain(2048);
		TGRender delayed = render(44100);

		assertTrue(Meters.peak(plain, 0, 2048) > 0.01);
		assertArrayEquals(plain, delayed.getTap(this.track));
		assertEquals(GervillProcessor.latency(44100) + 2048, delayed.getLatency());
	}

	// a muted track's latency still counts, so muting it moves nothing
	@Test
	public void aMutedTracksLatencyStillCounts() throws MidiPlayerException {
		this.soundChain(256);
		this.track.setMute(true);
		assertEquals(GervillProcessor.latency(44100) + 256, render(44100).getLatency());
	}

	// with another MIDI Port nothing is heard here, so nothing is delayed
	@Test
	public void anotherMidiPortDelaysNothing() throws MidiPlayerException {
		assertEquals(0, render(44100, false).getLatency());
	}

	// an audio track alone: its takes have no latency, the drums Gervill's
	@Test
	public void theDrumsCountWithoutAGeneralMidiTrack() throws MidiPlayerException {
		this.songManager.addAudioTrack(this.song, TGAudioInput.mono(1));
		this.songManager.removeTrack(this.song, 1);
		assertEquals(GervillProcessor.latency(44100), render(44100).getLatency());
	}
}
