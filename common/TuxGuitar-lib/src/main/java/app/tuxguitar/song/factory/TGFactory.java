package app.tuxguitar.song.factory;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGColor;
import app.tuxguitar.song.models.TGDivisionType;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGLyric;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.song.models.TGPickStroke;
import app.tuxguitar.song.models.TGScale;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.song.models.TGText;
import app.tuxguitar.song.models.TGTimeSignature;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.song.models.effects.TGEffectBend;
import app.tuxguitar.song.models.effects.TGEffectGrace;
import app.tuxguitar.song.models.effects.TGEffectHarmonic;
import app.tuxguitar.song.models.effects.TGEffectTremoloBar;
import app.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import app.tuxguitar.song.models.effects.TGEffectTrill;

public class TGFactory {

	public TGSong newSong(){
		return new TGSong() {
			//TGSong Implementation
		};
	}

	public TGLyric newLyric(){
		return new TGLyric(){
			//TGLyric Implementation
		};
	}

	public TGMarker newMarker(){
		return new TGMarker(this){
			//TGMarker Implementation
		};
	}

	public TGChord newChord(int length){
		return new TGChord(length){
			//TGChord Implementation
		};
	}

	public TGScale newScale(){
		return new TGScale(){
			//TGScale Implementation
		};
	}

	public TGColor newColor(){
		return new TGColor(){
			//UIColor Implementation
		};
	}

	public TGDuration newDuration(){
		return new TGDuration(this){
			//TGDuration Implementation
		};
	}

	public TGDivisionType newDivisionType(){
		return new TGDivisionType(){
			//TGDivisionType Implementation
		};
	}

	public TGTimeSignature newTimeSignature(){
		return new TGTimeSignature(this){
			//TGTimeSignature Implementation
		};
	}

	public TGTempo newTempo(){
		return new TGTempo(){
			//TGTempo Implementation
		};
	}

	public TGChannel newChannel(){
		return new TGChannel(){
			//TGChannel Implementation
		};
	}

	public TGChannelParameter newChannelParameter(){
		return new TGChannelParameter(){
			//TGChannelParameter Implementation
		};
	}

	public TGTrack newTrack(){
		return new TGTrack(this){
			//TGTrack Implementation
		};
	}

	public TGMeasureHeader newHeader(){
		return new TGMeasureHeader(this){
			//TGMeasureHeader Implementation
		};
	}

	public TGMeasure newMeasure(TGMeasureHeader header){
		return new TGMeasure(header){
			//TGMeasure Implementation
		};
	}

	public TGBeat newBeat(){
		return new TGBeat(this){
			//TGBeat Implementation
		};
	}

	public TGVoice newVoice(int index){
		return new TGVoice(this, index){
			//TGVoice Implementation
		};
	}

	public TGNote newNote(){
		return new TGNote(this){
			//TGNote Implementation
		};
	}

	public TGString newString(){
		return new TGString(){
			//TGString Implementation
		};
	}

	public TGStroke newStroke(){
		return new TGStroke(){
			//TGString Implementation
		};
	}

	public TGPickStroke newPickStroke(){
		return new TGPickStroke(){
			//TGPickStroke Implementation
		};
	}

	public TGText newText(){
		return new TGText(){
			//TGString Implementation
		};
	}

	public TGNoteEffect newEffect(){
		return new TGNoteEffect(){
			//TGNoteEffect Implementation
		};
	}

	public TGEffectBend newEffectBend(){
		return new TGEffectBend(){
			//TGEffectBend Implementation
		};
	}

	public TGEffectTremoloBar newEffectTremoloBar(){
		return new TGEffectTremoloBar(){
			//TGEffectTremoloBar Implementation
		};
	}

	public TGEffectGrace newEffectGrace(){
		return new TGEffectGrace(){
			//TGEffectGrace Implementation
		};
	}

	public TGEffectHarmonic newEffectHarmonic(){
		return new TGEffectHarmonic(){
			//TGEffectHarmonic Implementation
		};
	}

	public TGEffectTrill newEffectTrill(){
		return new TGEffectTrill(this){
			//TGEffectTrill Implementation
		};
	}

	public TGEffectTremoloPicking newEffectTremoloPicking(){
		return new TGEffectTremoloPicking(this){
			//TGEffectTremoloPicking Implementation
		};
	}
}
