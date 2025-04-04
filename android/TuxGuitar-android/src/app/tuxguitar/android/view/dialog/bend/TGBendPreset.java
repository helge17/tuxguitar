package app.tuxguitar.android.view.dialog.bend;

import app.tuxguitar.song.models.effects.TGEffectBend;

public class TGBendPreset{

	private String name;
	private TGEffectBend bend;

	public TGBendPreset(String name,TGEffectBend bend){
		this.name = name;
		this.bend = bend;
	}

	public TGEffectBend getBend() {
		return this.bend;
	}

	public String getName() {
		return this.name;
	}
}