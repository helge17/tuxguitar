package app.tuxguitar.app.document;

import java.net.URI;

import app.tuxguitar.editor.undo.TGUndoableBuffer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGSong;

public class TGDocument {

	private URI uri;
	private TGSong song;
	private TGUndoableBuffer undoableBuffer;
	private boolean unsaved;
	private boolean unwanted;
	private TGBeat caretBeat;
	private int caretString;
	private TGBeat selectionStart;
	private TGBeat selectionEnd;
	private MidiPlayerMode midiPlayerMode;

	public TGDocument() {
		super();
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public TGSong getSong() {
		return song;
	}

	public void setSong(TGSong song) {
		this.song = song;
	}

	public TGUndoableBuffer getUndoableBuffer() {
		return undoableBuffer;
	}

	public void setUndoableBuffer(TGUndoableBuffer undoableBuffer) {
		this.undoableBuffer = undoableBuffer;
	}

	public boolean isUnsaved() {
		return unsaved;
	}

	public void setUnsaved(boolean unsaved) {
		this.unsaved = unsaved;
	}

	public boolean isUnwanted() {
		return unwanted;
	}

	public void setUnwanted(boolean unwanted) {
		this.unwanted = unwanted;
	}

	public TGBeat getCaretBeat() {
		return caretBeat;
	}

	public void setCaretBeat(TGBeat caretBeat) {
		this.caretBeat = caretBeat;
	}

	public int getCaretString() {
		return caretString;
	}

	public void setCaretString(int caretString) {
		this.caretString = caretString;
	}

	public TGBeat getSelectionStart() {
		return selectionStart;
	}

	public void setSelectionStart(TGBeat selectionStart) {
		this.selectionStart = selectionStart;
	}

	public TGBeat getSelectionEnd() {
		return selectionEnd;
	}

	public void setSelectionEnd(TGBeat selectionEnd) {
		this.selectionEnd = selectionEnd;
	}

	public MidiPlayerMode getMidiPlayerMode() {
		return this.midiPlayerMode;
	}

	public void setMidiPlayerMode(MidiPlayerMode mode) {
		this.midiPlayerMode = mode;
	}

}
