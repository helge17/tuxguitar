package app.tuxguitar.audio;

/**
 * Converts audio from one sample rate to another, keeping its pitch: each output sample is the input's samples around
 * where it falls, weighted by a windowed sinc (ZEROS zero crossings on either side, a Blackman window) cut off at the
 * lower rate's Nyquist frequency, so nothing above it aliases when the rate goes down. Where the output samples fall
 * repeats: the rates' ratio reduces to up/down (160/147 from 44100 to 48000 Hz), so the weights of the up phases are
 * computed once, each phase's summing to 1 (a constant level passes unchanged). Input samples on the output's samples
 * (every 147th at 44100 Hz, every 160th at 48000) pass as they are. Outside the input is silence.
 */
final class TGResampler {

	static final int ZEROS = 16;

	private TGResampler() {
	}

	// as many output samples as fit in the input's duration; the input itself at its own rate
	static float[][] resample(float[][] input, int from, int to) {
		if (from == to) {
			return input;
		}
		int gcd = gcd(from, to);
		int up = to / gcd;
		int down = from / gcd;
		double cutoff = Math.min(1.0, (double) to / from);
		int half = (int) Math.ceil(ZEROS / cutoff);
		double[][] weights = weights(up, half, cutoff);
		float[][] output = new float[input.length][(int) ((long) input[0].length * up / down)];
		for (int side = 0; side < input.length; side++) {
			for (int i = 0; i < output[side].length; i++) {
				long position = (long) i * down;
				output[side][i] = sample(input[side], (int) (position / up) - half + 1, weights[(int) (position % up)]);
			}
		}
		return output;
	}

	// the input's samples from first on, weighted
	private static float sample(float[] input, int first, double[] weights) {
		double sum = 0;
		int from = Math.max(0, -first);
		int to = Math.min(weights.length, input.length - first);
		for (int j = from; j < to; j++) {
			sum += input[first + j] * weights[j];
		}
		return (float) sum;
	}

	// for each phase (the output sample phase/up of an input sample after one), the weights of the 2 * half input
	// samples around it, from the (half - 1)th before
	private static double[][] weights(int up, int half, double cutoff) {
		double[][] weights = new double[up][2 * half];
		for (int phase = 0; phase < up; phase++) {
			double sum = 0;
			for (int j = 0; j < 2 * half; j++) {
				double distance = (j - half + 1) - phase / (double) up;
				weights[phase][j] = sinc(cutoff * distance) * blackman(distance / half);
				sum += weights[phase][j];
			}
			for (int j = 0; j < 2 * half; j++) {
				weights[phase][j] /= sum;
			}
		}
		return weights;
	}

	private static double sinc(double x) {
		return (x == 0 ? 1 : Math.sin(Math.PI * x) / (Math.PI * x));
	}

	// 1 in the middle, 0 at either end (x from -1 to 1)
	private static double blackman(double x) {
		return 0.42 + 0.5 * Math.cos(Math.PI * x) + 0.08 * Math.cos(2 * Math.PI * x);
	}

	private static int gcd(int a, int b) {
		return (b == 0 ? a : gcd(b, a % b));
	}
}
