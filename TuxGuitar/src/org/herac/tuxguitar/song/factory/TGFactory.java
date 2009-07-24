package org.herac.tuxguitar.song.factory;

import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGLyric;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGNoteEffect;
import org.herac.tuxguitar.song.models.TGScale;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGText;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloBar;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import org.herac.tuxguitar.song.models.effects.TGEffectTrill;

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
			//TGColor Implementation
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
