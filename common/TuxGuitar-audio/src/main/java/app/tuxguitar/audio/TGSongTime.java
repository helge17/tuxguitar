package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.player.base.MidiRepeatController;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;

/**
 * Song time: the measures as the song plays them, following repeats and alternate endings, as the MIDI sequence
 * parser does. With Play looped on, a pass is one time through the Loop Range.
 */
public final class TGSongTime {

	private TGSongTime() {
	}

	// the measures played from start until stop is reached in the last pass, or until that pass ends
	public static List<TGPlayedMeasure> play(TGSong song, MidiPlayerMode mode, long start, long stop, int passes) {
		List<TGMeasureHeader> pass = pass(song, mode);
		List<TGMeasureHeader> sequence = Collections.nCopies(passes, pass).stream().flatMap(List::stream).collect(Collectors.toList());
		int first = indexAt(sequence, start, 0);
		if (first < 0) {
			return new ArrayList<TGPlayedMeasure>();
		}
		int last = indexAt(sequence, stop, Math.max(first, sequence.size() - pass.size()));
		return played(sequence, first, (last < 0 ? sequence.size() - 1 : last), start, stop);
	}

	// the measures of the sequence from first to last: the first from start on, the last until stop when it holds it;
	// a measure that does not follow the one before it starts a stretch
	private static List<TGPlayedMeasure> played(List<TGMeasureHeader> sequence, int first, int last, long start, long stop) {
		List<TGPlayedMeasure> played = new ArrayList<TGPlayedMeasure>();
		for (int i = first; i <= last; i++) {
			TGMeasureHeader header = sequence.get(i);
			long from = (i == first ? start - header.getStart() : 0);
			long to = (i == last ? Math.min(header.getLength(), stop - header.getStart()) : header.getLength());
			boolean jump = (i > first && header.getNumber() != sequence.get(i - 1).getNumber() + 1);
			if (to > from) {
				played.add(new TGPlayedMeasure(header, from, to, jump));
			}
		}
		return played;
	}

	// one pass, in song order: the whole song, or the Loop Range when Play looped is on
	private static List<TGMeasureHeader> pass(TGSong song, MidiPlayerMode mode) {
		MidiRepeatController controller = (mode.isLoop() ? new MidiRepeatController(song, mode.getLoopSHeader(), mode.getLoopEHeader()) : new MidiRepeatController(song, -1, -1));
		List<TGMeasureHeader> headers = new ArrayList<TGMeasureHeader>();
		while (!controller.finished()) {
			int index = controller.getIndex();
			controller.process();
			if (controller.shouldPlay()) {
				headers.add(song.getMeasureHeader(index));
			}
		}
		return headers;
	}

	// the first measure of the sequence, from the given index on, that holds the tick; -1 when none does
	private static int indexAt(List<TGMeasureHeader> sequence, long tick, int fromIndex) {
		for (int i = fromIndex; i < sequence.size(); i++) {
			TGMeasureHeader header = sequence.get(i);
			if (tick >= header.getStart() && tick < header.getStart() + header.getLength()) {
				return i;
			}
		}
		return -1;
	}
}
