package app.tuxguitar.graphics.control;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGLyric;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGText;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;

public class TGFactoryImpl extends TGFactory{

	public TGFactoryImpl(){
		super();
	}

	public TGMeasureHeader newHeader(){
		return new TGMeasureHeaderImpl(this);
	}

	public TGTrack newTrack(){
		return new TGTrackImpl(this);
	}

	public TGMeasure newMeasure(TGMeasureHeader header){
		return new TGMeasureImpl(header);
	}

	public TGNote newNote(){
		return new TGNoteImpl(this);
	}

	public TGBeat newBeat(){
		return new TGBeatImpl(this);
	}

	public TGVoice newVoice(int index){
		return new TGVoiceImpl(this, index);
	}

	public TGLyric newLyric(){
		return new TGLyricImpl();
	}

	public TGChord newChord(int length){
		return new TGChordImpl(length);
	}

	public TGText newText(){
		return new TGTextImpl();
	}
}
