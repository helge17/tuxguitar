package org.herac.tuxguitar.io.musicxml;

import java.io.OutputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongExporter;
import org.herac.tuxguitar.song.models.TGSong;

public class MusicXMLSongExporter implements TGSongExporter{

	public String getExportName() {
		return "MusicXML";
	}

	public TGFileFormat getFileFormat() {
		return new TGFileFormat("MusicXML","*.xml");
	}	
	
	public boolean configure(boolean setDefaults) {
		return true;
	}	
	
	public void exportSong(OutputStream stream, TGSong song) throws TGFileFormatException {
		new MusicXMLWriter(stream).writeSong(song);
	}

}