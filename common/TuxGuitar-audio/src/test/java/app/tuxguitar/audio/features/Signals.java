package app.tuxguitar.audio.features;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.LongToDoubleFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;

/**
 * The signals the features write, as a value per sample of a clock counting from the start of the song, at the song's
 * first tempo and measure length: "clicks of -0.25 on every beat", "a 440 Hz tone peaking at 0.4" (0.4 when no peak is
 * given), "a constant level of 0.4", "a tone per measure of 220, 330 and 440 Hz" (one tone per measure from the first
 * measure of the take, silence after the last).
 */
final class Signals {

	static final String PATTERN = "clicks of -?[\\d.]+ on every beat|a [\\d.]+ Hz tone(?: peaking at [\\d.]+)?|a constant level of -?[\\d.]+"
		+ "|a tone per measure of [\\d, ]+(?: and \\d+)? Hz|one click of -?[\\d.]+ at measure \\d+ beat [\\d.]+";

	private interface Signal {
		LongToDoubleFunction parse(Matcher matcher, TGSong song, int sampleRate, int firstMeasure);
	}

	private static final Map<Pattern, Signal> SIGNALS = new LinkedHashMap<Pattern, Signal>();

	static {
		SIGNALS.put(Pattern.compile("clicks of (-?[\\d.]+) on every beat"), Signals::clicks);
		SIGNALS.put(Pattern.compile("a ([\\d.]+) Hz tone(?: peaking at ([\\d.]+))?"), Signals::tone);
		SIGNALS.put(Pattern.compile("a constant level of (-?[\\d.]+)"), Signals::level);
		SIGNALS.put(Pattern.compile("a tone per measure of ([\\d, ]+(?: and \\d+)?) Hz"), Signals::tonePerMeasure);
		SIGNALS.put(Pattern.compile("one click of (-?[\\d.]+) at (measure \\d+ beat [\\d.]+)"), Signals::oneClick);
	}

	// at the song's first tempo, from the start of the song
	private static LongToDoubleFunction oneClick(Matcher matcher, TGSong song, int sampleRate, int firstMeasure) {
		double value = Double.parseDouble(matcher.group(1));
		long ticks = SongPosition.parse(matcher.group(2)).tick(song) - song.getMeasureHeader(0).getStart();
		long at = Math.round(ticks * sampleRate * 60.0 / (960.0 * song.getMeasureHeader(0).getTempo().getQuarterValue()));
		return sample -> (sample == at ? value : 0);
	}

	private Signals() {
	}

	static LongToDoubleFunction parse(String text, TGSong song, int sampleRate, int firstMeasure) {
		for (Map.Entry<Pattern, Signal> signal : SIGNALS.entrySet()) {
			Matcher matcher = signal.getKey().matcher(text);
			if (matcher.matches()) {
				return signal.getValue().parse(matcher, song, sampleRate, firstMeasure);
			}
		}
		throw new IllegalArgumentException(text);
	}

	private static LongToDoubleFunction clicks(Matcher matcher, TGSong song, int sampleRate, int firstMeasure) {
		double value = Double.parseDouble(matcher.group(1));
		long beat = Math.round(sampleRate * 60.0 / song.getMeasureHeader(0).getTempo().getQuarterValue());
		return sample -> (sample % beat == 0 ? value : 0);
	}

	private static LongToDoubleFunction tone(Matcher matcher, TGSong song, int sampleRate, int firstMeasure) {
		double frequency = Double.parseDouble(matcher.group(1));
		double peak = (matcher.group(2) == null ? 0.4 : Double.parseDouble(matcher.group(2)));
		return sine(frequency, peak, sampleRate);
	}

	private static LongToDoubleFunction level(Matcher matcher, TGSong song, int sampleRate, int firstMeasure) {
		double value = Double.parseDouble(matcher.group(1));
		return sample -> value;
	}

	private static LongToDoubleFunction tonePerMeasure(Matcher matcher, TGSong song, int sampleRate, int firstMeasure) {
		double[] frequencies = Arrays.stream(matcher.group(1).split(", | and ")).mapToDouble(Double::parseDouble).toArray();
		TGMeasureHeader first = song.getMeasureHeader(0);
		long measure = Math.round(sampleRate * 60.0 * first.getLength() / (first.getTempo().getQuarterValue() * 960.0));
		return sample -> {
			int index = (int) (sample / measure) - (firstMeasure - 1);
			return (index >= 0 && index < frequencies.length ? sine(frequencies[index], 0.4, sampleRate).applyAsDouble(sample) : 0);
		};
	}

	private static LongToDoubleFunction sine(double frequency, double peak, int sampleRate) {
		return sample -> peak * Math.sin(2 * Math.PI * frequency * sample / sampleRate);
	}
}
