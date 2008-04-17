package org.herac.tuxguitar.gui.editors.tab;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGLyric;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGText;
import org.herac.tuxguitar.song.models.TGTrack;

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
