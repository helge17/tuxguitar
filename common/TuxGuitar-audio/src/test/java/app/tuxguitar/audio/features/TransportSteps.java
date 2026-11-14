package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasureHeader;
import io.cucumber.java.en.Then;

/**
 * Steps on what VST3 plugins are told of the song (the transport): Test Tempo's report, one record per block (or part of
 * a block, from where a measure starts or the song jumps) it processed, in the render that played it.
 */
public class TransportSteps {

	// a record: tempo (double), numerator, denominator, playing (int32 each), musical position (double), samples (int64)
	private static final int RECORD = 36;

	private final AudioWorld world;

	public TransportSteps(AudioWorld world) {
		this.world = world;
	}

	private static final class Told {
		private double tempo;
		private String signature;
		private boolean playing;
		private double quarters;
	}

	// what the plugin's last instance was told, block after block
	private List<Told> told(String plugin) {
		List<byte[]> reports = world.reporting().of(plugin);
		assertFalse(reports.isEmpty(), plugin + " told nothing");
		ByteBuffer bytes = ByteBuffer.wrap(reports.get(reports.size() - 1)).order(ByteOrder.nativeOrder());
		List<Told> told = new ArrayList<Told>();
		while (bytes.remaining() >= RECORD) {
			Told record = new Told();
			record.tempo = bytes.getDouble();
			record.signature = bytes.getInt() + "/" + bytes.getInt();
			record.playing = (bytes.getInt() == 1);
			record.quarters = bytes.getDouble();
			bytes.getLong();
			told.add(record);
		}
		return told;
	}

	// the records whose musical position is in the measure: from its first quarter note to the next measure's
	private List<Told> in(List<Told> told, int measure) {
		TGMeasureHeader header = world.song().getMeasureHeader(measure - 1);
		double first = (header.getStart() - TGDuration.QUARTER_TIME) / (double) TGDuration.QUARTER_TIME;
		double next = first + header.getLength() / (double) TGDuration.QUARTER_TIME;
		List<Told> in = told.stream().filter(record -> record.quarters >= first && record.quarters < next).collect(Collectors.toList());
		assertFalse(in.isEmpty(), "nothing told in measure " + measure);
		return in;
	}

	private void assertToldIn(List<Told> told, int measure, int tempo, String signature) {
		for (Told record : in(told, measure)) {
			assertEquals(tempo, record.tempo, 1e-9, "tempo in measure " + measure);
			assertEquals(signature, record.signature, "time signature in measure " + measure);
		}
	}

	@Then("{string} was told {int} bpm in {int}\\/{int} in measure {int} and {int} bpm in {int}\\/{int} in measure {int}")
	public void wasToldTheTempos(String plugin, int tempo, int numerator, int denominator, int measure, int otherTempo, int otherNumerator,
			int otherDenominator, int otherMeasure) {
		List<Told> told = told(plugin);
		assertToldIn(told, measure, tempo, numerator + "/" + denominator);
		assertToldIn(told, otherMeasure, otherTempo, otherNumerator + "/" + otherDenominator);
	}

	@Then("{string} was told the song was playing in every measure")
	public void wasToldThePlayState(String plugin) {
		List<Told> told = told(plugin);
		for (int measure = 1; measure <= world.song().countMeasureHeaders(); measure++) {
			assertTrue(in(told, measure).stream().allMatch(record -> record.playing), "playing in measure " + measure);
		}
	}

	// the first record from the position on starts there (a block split where the tempo changes), at that tempo
	@Then("{string} was told {int} bpm from musical position {int}, within 1\\/22050 of a quarter note")
	public void wasToldTheTempoFrom(String plugin, int tempo, int position) {
		Told from = told(plugin).stream().filter(record -> record.quarters > position - 1 / 22050.0).findFirst().get();
		assertEquals(position, from.quarters, 1 / 22050.0, "first position told from " + position);
		assertEquals(tempo, from.tempo, 1e-9, "tempo told from " + position);
	}

	// a pass starts with the first record and wherever the position goes back
	@Then("{string} was told musical position {int} at the start of each pass, within 1\\/22050 of a quarter note")
	public void wasToldThePositionOfEachPass(String plugin, int position) {
		List<Told> told = told(plugin);
		List<Double> starts = new ArrayList<Double>();
		for (int i = 0; i < told.size(); i++) {
			if (i == 0 || told.get(i).quarters < told.get(i - 1).quarters) {
				starts.add(told.get(i).quarters);
			}
		}
		assertTrue(starts.size() >= 2, "passes start at " + starts);
		for (double start : starts) {
			assertEquals(position, start, 1 / 22050.0, "passes start at " + starts);
		}
	}
}
