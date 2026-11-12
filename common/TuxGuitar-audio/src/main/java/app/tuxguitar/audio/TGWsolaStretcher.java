package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.List;

/**
 * Time-stretching by waveform-similarity overlap-add (WSOLA). The output is built from frames of the input, a frame
 * every half frame, each overlapping the one before with a Hann window; each frame is read near where the stretch puts
 * it in the input, at the offset whose waveform best continues the frame before, so a tone keeps its pitch.
 *
 * Onsets (a sudden jump of more than 0.1 between two samples, a click for one) are kept once and on time: the input is
 * cut at each onset (a frame apart at least), each piece is stretched on its own to where the stretch puts the next
 * onset, and a piece's first frame plays its first half frame (11.6 ms) at full level and at its speed; the frames
 * after it read past that, so an attack is never repeated or smeared over the frames. A stretch too short for a frame
 * is resampled instead (its pitch changes).
 *
 * The search reaches 4.6 ms either side of where a frame should be read, so what happens inside the audio stays
 * within 5 ms of where it should be, and tones down to 108 Hz find a whole period to align on.
 *
 * A stereo input is cut and searched on the mean of its sides, and both sides are read at the places found there.
 */
public final class TGWsolaStretcher implements TGTimeStretcher {

	private static final float ONSET_JUMP = 0.1f;
	private static final float ONSET_SUDDENNESS = 4f;
	private static final double TOLERANCE_SECONDS = 0.0046;
	// the waveforms are compared on every second sample
	private static final int COMPARE_STEP = 2;

	private final int frame;
	private final int hop;
	private final int tolerance;
	private final float[] window;
	private final float[] firstWindow;

	public TGWsolaStretcher(int sampleRate) {
		this.frame = (sampleRate > 64000 ? 2048 : 1024);
		this.hop = this.frame / 2;
		this.tolerance = (int) Math.round(sampleRate * TOLERANCE_SECONDS);
		this.window = new float[this.frame];
		this.firstWindow = new float[this.frame];
		for (int i = 0; i < this.frame; i++) {
			this.window[i] = (float) (0.5 - 0.5 * Math.cos(2 * Math.PI * i / this.frame));
			this.firstWindow[i] = (i < this.hop ? 1f : this.window[i]);
		}
	}

	public float[][] stretch(float[][] input, int from, int to, int length) {
		float[][] output = new float[input.length][length];
		Piece piece = new Piece(input, from, to);
		List<Integer> onsets = onsets(piece.guide, 0, to - from);
		double scale = (double) length / (to - from);
		for (int i = 0; i < onsets.size(); i++) {
			int start = onsets.get(i);
			int end = (i + 1 < onsets.size() ? onsets.get(i + 1) : to - from);
			int outStart = (int) Math.round(start * scale);
			int outEnd = (int) Math.round(end * scale);
			stretchPiece(piece, start, end, output, outStart, outEnd - outStart);
		}
		return output;
	}

	// the stretch of the input being stretched: its sides, read from an offset, and the mean of its sides (the guide),
	// which is cut at onsets and searched; positions here count from the stretch's start
	private static final class Piece {
		private final float[][] sides;
		private final int offset;
		private final float[] guide;

		Piece(float[][] sides, int from, int to) {
			this.sides = sides;
			this.offset = from;
			this.guide = new float[to - from];
			for (int i = 0; i < this.guide.length; i++) {
				float sum = 0;
				for (float[] side : sides) {
					sum += side[from + i];
				}
				this.guide[i] = sum / sides.length;
			}
		}
	}

	// where the pieces start: the start, and every onset at least a frame after the one before and before the end
	List<Integer> onsets(float[] input, int from, int to) {
		List<Integer> onsets = new ArrayList<Integer>();
		onsets.add(from);
		for (int i = from + 2; i <= to - this.frame; i++) {
			if (isOnset(input, i) && i - onsets.get(onsets.size() - 1) >= this.frame) {
				onsets.add(i);
			}
		}
		return onsets;
	}

	// a jump of more than 0.1 that is sudden: four times the step before it, so a loud high tone is no onset
	private static boolean isOnset(float[] input, int i) {
		float jump = Math.abs(input[i] - input[i - 1]);
		return (jump > ONSET_JUMP && jump > ONSET_SUDDENNESS * Math.abs(input[i - 1] - input[i - 2]));
	}

	// a piece played at its length is read sample for sample (resampling at rate 1 reads each sample whole)
	private void stretchPiece(Piece piece, int start, int end, float[][] output, int outStart, int length) {
		if (end - start == length || end - start < this.frame) {
			for (int side = 0; side < output.length; side++) {
				resample(piece.sides[side], piece.offset + start, piece.offset + end, output[side], outStart, length);
			}
		} else {
			this.overlapAdd(piece, start, end, output, outStart, length);
		}
	}

	private void overlapAdd(Piece piece, int start, int end, float[][] output, int outStart, int length) {
		float[][] sum = new float[output.length][length];
		float[] weight = new float[length];
		double rate = (double) (end - start) / length;
		int previous = start;
		for (int k = 0; k * this.hop < length; k++) {
			int ideal = start + (int) Math.round(k * this.hop * rate);
			int position = (k == 0 ? start : this.bestPosition(piece.guide, start, end, previous + this.hop, ideal));
			this.addFrame(piece, position, end, (k == 0 ? this.firstWindow : this.window), sum, weight, k * this.hop);
			previous = position;
		}
		for (int side = 0; side < output.length; side++) {
			for (int i = 0; i < length; i++) {
				output[side][outStart + i] = (weight[i] > 0 ? sum[side][i] / weight[i] : 0f);
			}
		}
	}

	// near the ideal position, the one whose waveform is most like the natural continuation of the frame before; past
	// what the first frame played at full level, so the piece's attack is played once, and leaving a whole frame
	// before its end
	int bestPosition(float[] input, int start, int end, int natural, int ideal) {
		int low = Math.max(start + this.hop, Math.min(ideal - this.tolerance, end - this.frame));
		int high = Math.max(low, Math.min(end - this.frame, ideal + this.tolerance));
		int best = low;
		double most = Double.NEGATIVE_INFINITY;
		for (int position = low; position <= high; position++) {
			double similarity = this.similarity(input, natural, position, end);
			if (similarity > most) {
				most = similarity;
				best = position;
			}
		}
		return best;
	}

	// the correlation of the two waveforms, divided by the candidate's energy, so a louder candidate is not preferred
	double similarity(float[] input, int natural, int position, int end) {
		double sum = 0;
		double energy = 1e-9;
		int count = Math.min(this.hop, Math.min(end - natural, end - position));
		for (int i = 0; i < count; i += COMPARE_STEP) {
			sum += input[natural + i] * input[position + i];
			energy += input[position + i] * input[position + i];
		}
		return sum / Math.sqrt(energy);
	}

	private void addFrame(Piece piece, int position, int end, float[] frameWindow, float[][] sum, float[] weight, int at) {
		int count = Math.min(this.frame, Math.min(end - position, weight.length - at));
		for (int i = 0; i < count; i++) {
			for (int side = 0; side < sum.length; side++) {
				sum[side][at + i] += piece.sides[side][piece.offset + position + i] * frameWindow[i];
			}
			weight[at + i] += frameWindow[i];
		}
	}

	// too short to hold a frame: read at the stretched rate, between the two nearest samples (the pitch changes)
	private static void resample(float[] input, int start, int end, float[] output, int outStart, int length) {
		double rate = (double) (end - start) / length;
		for (int i = 0; i < length; i++) {
			double position = start + i * rate;
			int before = (int) position;
			int after = Math.min(before + 1, end - 1);
			double fraction = position - before;
			output[outStart + i] = (float) (input[before] * (1 - fraction) + input[after] * fraction);
		}
	}
}
