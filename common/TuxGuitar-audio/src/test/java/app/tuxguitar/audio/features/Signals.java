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
 * The signals the features write, as a value per sample of the device's clock (which counts from the start of the
 * song). Clicks fall on the beats being played (at the song's first tempo when nothing is); the others are at the
 * song's first tempo and measure length: "clicks of -0.25 on every beat", "a 440 Hz tone peaking at 0.4" (0.4 when no
 * peak is given; "at -6 dBFS" is a peak of 10^(-6/20)), "a constant level of 0.4", "a tone per measure of 220, 330 and
 * 440 Hz" (one tone per measure from the first measure of the take, silence after the last). "<signal> for N samples,
 * then <signal>" plays the first for the first N samples of the clock, then the second.
 */
final class Signals {

	private static final String ONE = "(?:clicks of -?[\\d.]+ on every beat|a [\\d.]+ Hz tone(?: peaking at -?[\\d.]+(?: dBFS)?)?"
		+ "|a constant level of -?[\\d.]+|a tone per measure of [\\d, ]+(?: and \\d+)? Hz|one click of -?[\\d.]+ at measure \\d+ beat [\\d.]+)";
	static final String PATTERN = ONE + "(?: for \\d+ samples?, then " + ONE + ")?";

	private static final Pattern ONE_THEN_ANOTHER = Pattern.compile("(.+) for (\\d+) samples?, then (.+)");

	private interface Signal {
		LongToDoubleFunction parse(Matcher matcher, TGSong song, TestInterface device, int firstMeasure);
	}

	private static final Map<Pattern, Signal> SIGNALS = new LinkedHashMap<Pattern, Signal>();

	static {
		SIGNALS.put(Pattern.compile("clicks of (-?[\\d.]+) on every beat"), Signals::clicks);
		SIGNALS.put(Pattern.compile("a ([\\d.]+) Hz tone(?: peaking at (-?[\\d.]+)( dBFS)?)?"), Signals::tone);
		SIGNALS.put(Pattern.compile("a constant level of (-?[\\d.]+)"), Signals::level);
		SIGNALS.put(Pattern.compile("a tone per measure of ([\\d, ]+(?: and \\d+)?) Hz"), Signals::tonePerMeasure);
		SIGNALS.put(Pattern.compile("one click of (-?[\\d.]+) at (measure \\d+ beat [\\d.]+)"), Signals::oneClick);
	}

	// at the song's first tempo, from the start of the song
	private static LongToDoubleFunction oneClick(Matcher matcher, TGSong song, TestInterface device, int firstMeasure) {
		int sampleRate = device.getSampleRate();
		double value = Double.parseDouble(matcher.group(1));
		long ticks = SongPosition.parse(matcher.group(2)).tick(song) - song.getMeasureHeader(0).getStart();
		long at = Math.round(ticks * sampleRate * 60.0 / (960.0 * song.getMeasureHeader(0).getTempo().getQuarterValue()));
		return sample -> (sample == at ? value : 0);
	}

	private Signals() {
	}

	static LongToDoubleFunction parse(String text, TGSong song, TestInterface device, int firstMeasure) {
		Matcher sequence = ONE_THEN_ANOTHER.matcher(text);
		if (sequence.matches()) {
			LongToDoubleFunction first = parse(sequence.group(1), song, device, firstMeasure);
			LongToDoubleFunction then = parse(sequence.group(3), song, device, firstMeasure);
			long samples = Long.parseLong(sequence.group(2));
			return sample -> (sample < samples ? first : then).applyAsDouble(sample);
		}
		for (Map.Entry<Pattern, Signal> signal : SIGNALS.entrySet()) {
			Matcher matcher = signal.getKey().matcher(text);
			if (matcher.matches()) {
				return signal.getValue().parse(matcher, song, device, firstMeasure);
			}
		}
		throw new IllegalArgumentException(text);
	}

	private static LongToDoubleFunction clicks(Matcher matcher, TGSong song, TestInterface device, int firstMeasure) {
		int sampleRate = device.getSampleRate();
		double value = Double.parseDouble(matcher.group(1));
		long beat = Math.round(sampleRate * 60.0 / song.getMeasureHeader(0).getTempo().getQuarterValue());
		return sample -> ((device.isPlaying() ? device.isBeat(sample) : sample % beat == 0) ? value : 0);
	}

	private static LongToDoubleFunction tone(Matcher matcher, TGSong song, TestInterface device, int firstMeasure) {
		int sampleRate = device.getSampleRate();
		double frequency = Double.parseDouble(matcher.group(1));
		double peak = (matcher.group(2) == null ? 0.4 : Double.parseDouble(matcher.group(2)));
		peak = (matcher.group(3) == null ? peak : Math.pow(10, peak / 20));
		return sine(frequency, peak, sampleRate);
	}

	private static LongToDoubleFunction level(Matcher matcher, TGSong song, TestInterface device, int firstMeasure) {
		int sampleRate = device.getSampleRate();
		double value = Double.parseDouble(matcher.group(1));
		return sample -> value;
	}

	private static LongToDoubleFunction tonePerMeasure(Matcher matcher, TGSong song, TestInterface device, int firstMeasure) {
		int sampleRate = device.getSampleRate();
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
