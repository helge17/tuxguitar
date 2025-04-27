package app.tuxguitar.song.helpers;

import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.models.TGMeasure;

public class TGMeasureError {
	
	private TGMeasure measure;
	private int voiceIndex;
	private int errCode;
	
	public TGMeasureError(TGMeasure measure, int voiceIndex, int errCode) {
		this.measure = measure;
		this.voiceIndex = voiceIndex;
		this.errCode = errCode;
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
	
	public boolean canBeFixed() {
		int code = this.errCode;
		code ^= (code & (TGMeasureManager.VOICE_TOO_LONG |TGMeasureManager.VOICE_TOO_SHORT | TGMeasureManager.VOICE_OVERLAP));
		return (code == TGMeasureManager.VOICE_OK);
	}

	public boolean isEqualTo(TGMeasureError err) {
		if (err == null) return false;
		return ( this.measure.equals(err.getMeasure())
				&& (this.voiceIndex == err.getVoiceIndex())
				&& (this.errCode == err.getErrCode()) );
	}
	
}
