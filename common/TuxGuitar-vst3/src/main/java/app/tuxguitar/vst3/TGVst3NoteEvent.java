package app.tuxguitar.vst3;

/**
 * A note event of a block, on a sample of it (its offset): a note starting or stopping, or a note's tuning (a bend of
 * that note alone, in semitones). The note id ties the events of one note together.
 */
public final class TGVst3NoteEvent {

	public static final int NOTE_ON = 1;
	public static final int NOTE_OFF = 2;
	public static final int TUNING = 3;

	private final int type;
	private final int offset;
	private final int pitch;
	private final int noteId;
	private final float value;

	private TGVst3NoteEvent(int type, int offset, int pitch, int noteId, float value) {
		this.type = type;
		this.offset = offset;
		this.pitch = pitch;
		this.noteId = noteId;
		this.value = value;
	}

	// velocity from 0 to 1
	public static TGVst3NoteEvent noteOn(int offset, int pitch, int noteId, float velocity) {
		return new TGVst3NoteEvent(NOTE_ON, offset, pitch, noteId, velocity);
	}

	public static TGVst3NoteEvent noteOff(int offset, int pitch, int noteId) {
		return new TGVst3NoteEvent(NOTE_OFF, offset, pitch, noteId, 0f);
	}

	public static TGVst3NoteEvent tuning(int offset, int noteId, float semitones) {
		return new TGVst3NoteEvent(TUNING, offset, 0, noteId, semitones);
	}

	public int getType() {
		return this.type;
	}

	public int getOffset() {
		return this.offset;
	}

	public int getPitch() {
		return this.pitch;
	}

	public int getNoteId() {
		return this.noteId;
	}

	public float getValue() {
		return this.value;
	}
}
