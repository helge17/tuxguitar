package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import app.tuxguitar.audio.AudioFiles;
import app.tuxguitar.audio.Meters;
import app.tuxguitar.audio.TGAudioExporter;
import app.tuxguitar.audio.TGExportFormat;
import app.tuxguitar.audio.TGRender;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps on exporting audio: the song exported as a file, which is then read back as any player reads it (left: track
 * 1's clicks, right: track 2's), on the timeline of the song as written.
 */
public class ExportSteps {

	// how far from a click its own samples go
	private static final int CLICK = 32;

	private final AudioWorld world;

	public ExportSteps(AudioWorld world) {
		this.world = world;
	}

	@ParameterType("output|file")
	public Heard sound(String sound) {
		return (sound.equals("file") ? world.exported() : PlaySteps.heard(world));
	}

	@ParameterType("WAVE|AIFF")
	public TGExportFormat.Type fileType(String type) {
		return TGExportFormat.Type.valueOf(type);
	}

	@ParameterType("16 bits|24 bits|32-bit float")
	public TGExportFormat.Samples samples(String samples) {
		return (samples.startsWith("32") ? TGExportFormat.Samples.FLOAT_32 : TGExportFormat.Samples.valueOf("PCM_" + samples.substring(0, 2)));
	}

	// the file read back is of the format chosen
	@When("I export the song as a {fileType} file at {int} Hz in {samples}")
	public void export(TGExportFormat.Type type, int sampleRate, TGExportFormat.Samples samples) throws Exception {
		TGExportFormat format = new TGExportFormat(type, sampleRate, samples);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new TGAudioExporter(world.fixture().context, world.fixture().songManager, world.song()).export(format, out);
		AudioFiles file = AudioFiles.read(out.toByteArray());
		String bits = (samples == TGExportFormat.Samples.FLOAT_32 ? "32 bits PCM_FLOAT" : samples.name().substring(4) + " bits PCM_SIGNED");
		assertEquals(type + " " + sampleRate + " Hz " + bits, file.describe());
		float[][] read = file.getSamples();
		TGRender render = new TGRender(TGAudioExporter.timeline(world.song(), sampleRate), Collections.emptyMap(), new float[2][read[0].length], read, 0, 0);
		world.setExported(new Heard(render, world.song()));
	}

	// each side's click nearest to where the position sounds
	@Then("the file's left and right clicks of {position} are at sample {int} within 1 sample")
	public void theClicksAreAt(SongPosition position, int sample) {
		Heard file = world.exported();
		int at = file.sampleAt(position, 0);
		for (int side = 0; side < 2; side++) {
			List<Integer> clicks = SyncSteps.clicks(file, side);
			int nearest = clicks.stream().min((a, b) -> Integer.compare(Math.abs(a - at), Math.abs(b - at))).orElse(-1);
			assertEquals(sample, nearest, 1, "side " + side + " clicks at " + clicks);
		}
	}

	@Then("the file's right clicks peak at {float} within 1%")
	public void theRightClicksPeakAt(float peak) {
		float[] right = world.exported().right();
		assertEquals(peak, Meters.peak(right, 0, right.length), peak / 100);
	}

	@Then("the file holds the echo of the click of {position}, {int} samples after it, within 1 sample")
	public void theFileHoldsTheEcho(SongPosition position, int samples) {
		SyncSteps.assertClickAt(world.exported(), world.exported().sampleAt(position, 0) + samples);
	}

	@Then("the file's right side holds {int} clicks, each within 1 sample of a beat in song time")
	public void theRightSideHoldsClicksOnTheBeats(int count) {
		List<Integer> clicks = SyncSteps.clicks(world.exported(), 1);
		assertEquals(count, clicks.size(), "clicks at " + clicks);
		ListeningSteps.assertOnBeats(clicks, world.exported().beats(), 1);
	}

	// the clicks, and below -60 dBFS everywhere more than a few samples from one
	@Then("the file's left side holds {int} clicks and its right side {int} clicks, and nothing else above -60 dBFS")
	public void theFileHoldsClicksAndNothingElse(int left, int right) {
		Heard file = world.exported();
		SyncSteps.assertClicks(file, left, right);
		for (int side = 0; side < 2; side++) {
			float[] rest = file.output(side).clone();
			for (int click : SyncSteps.clicks(file, side)) {
				java.util.Arrays.fill(rest, Math.max(0, click - CLICK), Math.min(rest.length, click + CLICK), 0f);
			}
			assertTrue(Meters.peak(rest, 0, rest.length) < 0.001f, "side " + side + " peaks at " + Meters.peak(rest, 0, rest.length));
		}
	}

	@Then("the file lasts {int} samples, within 1 sample")
	public void theFileLasts(int samples) {
		assertEquals(samples, world.exported().left().length, 1);
	}

	@Then("on every beat the file's left and right clicks are within 1 sample of each other")
	public void togetherWithinASample() {
		SyncSteps.assertTogether(world.exported(), 1);
	}
}
