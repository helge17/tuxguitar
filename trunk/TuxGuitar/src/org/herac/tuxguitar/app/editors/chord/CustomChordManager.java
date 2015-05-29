package org.herac.tuxguitar.app.editors.chord;

import java.io.File;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.chord.xml.ChordXMLReader;
import org.herac.tuxguitar.app.editors.chord.xml.ChordXMLWriter;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.song.models.TGChord;

public class CustomChordManager {
	
	private long lastEdit;
	private List<TGChord> chords;
	
	public CustomChordManager() {
		this.chords = ChordXMLReader.getChords(getUserFileName());
		this.setLastEdit();
	}
	
	public int countChords() {
		return this.chords.size();
	}
	
	public TGChord getChord(int index) {
		if (index >= 0 && index < countChords()) {
			return ((TGChord) this.chords.get(index)).clone(TuxGuitar.getInstance().getSongManager().getFactory());
		}
		return null;
	}
	
	public void addChord(TGChord chord) {
		this.chords.add(chord);
		this.setLastEdit();
	}
	
	public void removeChord(int index) {
		if (index >= 0 && index < countChords()) {
			this.chords.remove(index);
			this.setLastEdit();
		}
	}
	
	public void renameChord(int index, String name) {
		if (index >= 0 && index < countChords()) {
			((TGChord) this.chords.get(index)).setName(name);
			this.setLastEdit();
		}
	}
	
	public boolean existOtherEqualCustomChord(String name, int index) {
		for (int i = 0; i < countChords(); i++) {
			TGChord chord = getChord(i);
			if (chord.getName().equals(name) && index != i) {
				return true;
			}
		}
		return false;
	}
	
	public void write() {
		ChordXMLWriter.setChords(this.chords, getUserFileName());
	}
	
	private static String getUserFileName() {
		return (TGFileUtils.PATH_USER_CONFIG + File.separator + "customchords.xml");
	}
	
	private void setLastEdit() {
		this.lastEdit = System.currentTimeMillis();
	}
	
	public long getLastEdit() {
		return this.lastEdit;
	}
}
