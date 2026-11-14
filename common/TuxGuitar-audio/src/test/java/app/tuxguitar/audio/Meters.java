package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.List;

/**
 * What the tests measure in audio, as the features README defines it: a tone's fundamental frequency, the level at a
 * frequency, clicks and their positions, peaks. Ranges are sample indexes, from included, to excluded.
 */
public final class Meters {

	// clicks closer than this are one (a time-stretched click is no longer one sample)
	private static final int CLICK_GAP = 441;

	private Meters() {
	}

	public static float peak(float[] samples, int from, int to) {
		float peak = 0;
		for (int i = from; i < to; i++) {
			peak = Math.max(peak, Math.abs(samples[i]));
		}
		return peak;
	}

	// the fundamental frequency, from the zero crossings going up through a tenth of the peak and back down; 0 when
	// there are fewer than two
	public static double frequency(float[] samples, int from, int to, int sampleRate) {
		float threshold = peak(samples, from, to) / 10;
		double first = -1;
		double last = -1;
		int crossings = 0;
		boolean low = true;
		for (int i = Math.max(from, 1); i < to; i++) {
			if (low && samples[i - 1] <= 0 && samples[i] > 0) {
				last = (i - 1) + samples[i - 1] / (samples[i - 1] - samples[i]);
				first = (crossings == 0 ? last : first);
				crossings++;
				low = false;
			} else if (samples[i] < -threshold) {
				low = true;
			}
		}
		return (crossings < 2 ? 0 : (crossings - 1) * (double) sampleRate / (last - first));
	}

	// the peak level of the sine at that frequency, by a Hann-windowed Fourier sum
	public static double level(float[] samples, int from, int to, double frequency, int sampleRate) {
		double re = 0;
		double im = 0;
		double weights = 0;
		int n = to - from;
		for (int i = 0; i < n; i++) {
			double w = 0.5 - 0.5 * Math.cos(2 * Math.PI * i / n);
			double phase = 2 * Math.PI * frequency * i / sampleRate;
			re += w * samples[from + i] * Math.cos(phase);
			im += w * samples[from + i] * Math.sin(phase);
			weights += w;
		}
		return 2 * Math.sqrt(re * re + im * im) / weights;
	}

	// of a tone among others (a chord's), the frequency between low and high, to a hundredth of a hertz, at which the
	// samples have their highest level: a tenth of a hertz apart first, then around the highest
	public static double peakFrequency(float[] samples, int from, int to, double low, double high, int sampleRate) {
		double coarse = highest(samples, from, to, low, high, 0.1, sampleRate);
		return highest(samples, from, to, coarse - 0.1, coarse + 0.1, 0.01, sampleRate);
	}

	private static double highest(float[] samples, int from, int to, double low, double high, double step, int sampleRate) {
		return java.util.stream.DoubleStream.iterate(low, frequency -> frequency <= high, frequency -> frequency + step).boxed()
			.max(java.util.Comparator.comparingDouble(frequency -> level(samples, from, to, frequency, sampleRate))).get();
	}

	// the clicks next to a tone: jumps of more than 0.1 between two samples, a click ending where none is for CLICK_GAP
	// samples; each at the sample after its first jump
	public static List<Integer> jumps(float[] samples, int from, int to) {
		List<Integer> clicks = new ArrayList<Integer>();
		int lastJump = Integer.MIN_VALUE / 2;
		for (int i = Math.max(from, 1); i < to; i++) {
			if (Math.abs(samples[i] - samples[i - 1]) > 0.1f) {
				if (i - lastJump > CLICK_GAP) {
					clicks.add(i);
				}
				lastJump = i;
			}
		}
		return clicks;
	}

	// the clicks: runs of samples above half the peak, a run ending where none is for CLICK_GAP samples; each at its
	// first sample above half the peak
	public static List<Integer> clicks(float[] samples, int from, int to) {
		return onsets(samples, from, to, peak(samples, from, to) / 2);
	}

	// where sounds start: runs of samples above the level, a run ending where none is for CLICK_GAP samples; each at its
	// first sample above the level (none at level 0)
	public static List<Integer> onsets(float[] samples, int from, int to, double level) {
		int[] above = above(samples, from, to, level);
		return java.util.stream.IntStream.range(0, above.length).filter(i -> i == 0 || above[i] - above[i - 1] > CLICK_GAP)
			.mapToObj(i -> above[i]).collect(java.util.stream.Collectors.toList());
	}

	// where a sound breaks off: the runs of samples at or below half the peak between the first sample above it and the
	// last, their first sample to their length, in order
	public static java.util.SortedMap<Integer, Integer> breaks(float[] samples) {
		int[] above = above(samples, 0, samples.length, peak(samples, 0, samples.length) / 2);
		java.util.SortedMap<Integer, Integer> breaks = new java.util.TreeMap<Integer, Integer>();
		java.util.stream.IntStream.range(1, above.length).filter(i -> above[i] - above[i - 1] > 1).forEach(i -> breaks.put(above[i - 1] + 1, above[i] - above[i - 1] - 1));
		return breaks;
	}

	// how long a sound lasts: from the first sample above half the peak to the last, both included (0 when silent)
	public static int sounding(float[] samples) {
		int[] above = above(samples, 0, samples.length, peak(samples, 0, samples.length) / 2);
		return (above.length > 0 ? above[above.length - 1] - above[0] + 1 : 0);
	}

	private static int[] above(float[] samples, int from, int to, double level) {
		return java.util.stream.IntStream.range(from, to).filter(i -> level > 0 && Math.abs(samples[i]) > level).toArray();
	}
}
