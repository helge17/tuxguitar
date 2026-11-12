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

	// the clicks: runs of samples above half the peak, a run ending where none is for CLICK_GAP samples; each at its
	// first sample above half the peak
	public static List<Integer> clicks(float[] samples, int from, int to) {
		float half = peak(samples, from, to) / 2;
		List<Integer> clicks = new ArrayList<Integer>();
		int lastAbove = Integer.MIN_VALUE / 2;
		for (int i = from; i < to; i++) {
			if (half > 0 && Math.abs(samples[i]) > half) {
				if (i - lastAbove > CLICK_GAP) {
					clicks.add(i);
				}
				lastAbove = i;
			}
		}
		return clicks;
	}
}
