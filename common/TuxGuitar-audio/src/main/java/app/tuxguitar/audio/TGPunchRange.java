package app.tuxguitar.audio;

import app.tuxguitar.song.models.TGSong;

/**
 * The measures, by number, outside which a recording keeps nothing. Immutable.
 */
public final class TGPunchRange {

	private final int first;
	private final int last;

	public TGPunchRange(int first, int last) {
		this.first = first;
		this.last = last;
	}

	public int getFirst() {
		return this.first;
	}

	public int getLast() {
		return this.last;
	}

	public boolean contains(int measure) {
		return (measure >= this.first && measure <= this.last);
	}

	// the range cut at the song's last measure; null when the song ends before it starts
	public TGPunchRange endingWith(TGSong song) {
		int count = song.countMeasureHeaders();
		if (this.first > count) {
			return null;
		}
		return new TGPunchRange(this.first, Math.min(this.last, count));
	}
}
