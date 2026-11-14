package app.tuxguitar.audio;

import java.io.File;
import java.util.Arrays;

import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.vst3.TGVst3Exception;
import app.tuxguitar.vst3.TGVst3Hosting;
import app.tuxguitar.vst3.TGVst3Processors;

/**
 * The real VST3 host process and test plugins (the native build Maven runs before the tests, named by the system
 * property tg.vst3.native) hosted for a context, with the VST3 processors installed; and a song's channel played on a
 * synthesizer as the player sends it.
 */
final class TestPluginHosting {

	private TestPluginHosting() {
	}

	// configured, its folder scanned
	static TGVst3Hosting host(TGContext context) throws TGVst3Exception {
		File build = new File(System.getProperty("tg.vst3.native"));
		File windows = new File(new File(build, "bin"), "tuxguitar-vst3-host.exe");
		File host = (windows.isFile() ? windows : new File(new File(build, "bin"), "tuxguitar-vst3-host"));
		TGVst3Hosting hosting = TGVst3Hosting.getInstance(context);
		hosting.configure(host, Arrays.asList(new File(new File(build, "VST3"), "Release")));
		hosting.scan();
		TGVst3Processors.install(context);
		return hosting;
	}

	// the channel opened on the synthesizer, its parameters (its sound chain among them) sent
	static TGSynthChannel play(TGSynthModel synthesizer, TGSongManager songManager, TGSong song, TGChannel channel) throws MidiPlayerException {
		TGSynthChannel played = (TGSynthChannel) synthesizer.openChannel(channel.getChannelId());
		TGMidiTaps.sendParameters(synthesizer, songManager, song, channel.getChannelId());
		return played;
	}
}
