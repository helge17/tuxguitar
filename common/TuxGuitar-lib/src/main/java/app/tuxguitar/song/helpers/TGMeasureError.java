package app.tuxguitar.song.helpers;

import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;

public class TGMeasureError {

	public final static int OK = 0;

	// invalid measure duration errors
	public final static int TYPE_VOICE_DURATION_ERROR = 1;
	public final static int VOICE_DISCONTINUOUS = 0x01;		// some gaps present between notes/rests of voice
	public final static int VOICE_OVERLAP = 0x02;			// some notes/rests overlap in voice
	public final static int VOICE_EARLY_START = 0x04;		// first note/rest in voice starts before measure (should never happen)
	public final static int VOICE_TOO_SHORT = 0x08;			// last note/rest in voice finishes before measure end
	public final static int VOICE_TOO_LONG = 0x10;			// last note/rest in voice finishes after measure end

	// invalid tied note errors
	public final static int TYPE_TIED_NOTE_ERROR = 2;
	
	private int errorType;
	private TGMeasure measure;
	private int voiceIndex;
	private int errCode;
	private TGNote invalidTiedNote;
	
	public TGMeasureError(TGMeasure measure, int voiceIndex, int errCode) {
		this.errorType = TYPE_VOICE_DURATION_ERROR;
		this.measure = measure;
		this.voiceIndex = voiceIndex;
		this.errCode = errCode;
	}
	
	public TGMeasureError(TGMeasure measure, TGNote invalidTiedNote) {
		this.errorType = TYPE_TIED_NOTE_ERROR;
		this.measure = measure;
		this.invalidTiedNote = invalidTiedNote;
		this.voiceIndex = invalidTiedNote.getVoice().getIndex();
	}
	
	public int getErrorType() {
		return this.errorType;
	}
	
	public TGMeasure getMeasure() {
		return this.measure;
	}
	
	public int getVoiceIndex() {
		return this.voiceIndex;
	}
	public int getErrCode() {
		return this.errCode;
	}
	

	public TGNote getInvalidTiedNote() {
		return this.invalidTiedNote;
	}


	public boolean isEqualTo(TGMeasureError err) {
		if (err == null) return false;
		return ( this.measure.equals(err.getMeasure())
				&& (this.voiceIndex == err.getVoiceIndex())
				&& (this.errCode == err.getErrCode()) );
	}
	
	public boolean canBeFixed() {
		if (this.errorType == TYPE_TIED_NOTE_ERROR) {
			return true;
		}
		int code = this.errCode;
		code ^= (code & (VOICE_TOO_LONG | VOICE_TOO_SHORT | VOICE_OVERLAP));
		return (code == OK);
	}
}
