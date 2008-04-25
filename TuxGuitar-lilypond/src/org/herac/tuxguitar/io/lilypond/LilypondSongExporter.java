package org.herac.tuxguitar.io.lilypond;

import java.io.OutputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGSongExporter;
import org.herac.tuxguitar.song.models.TGSong;

public class LilypondSongExporter implements TGSongExporter{
	
	private LilypondSettings settings;
	
	public String getExportName() {
		return "Lilypond";
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("Lilypond","*.ly");
	}
	
	public boolean configure(boolean setDefaults) {
		this.settings = (setDefaults ? LilypondSettings.getDefaults() : new LilypondSettingsDialog().open());
		return (this.settings != null);
	}
	
	public void exportSong(OutputStream stream, TGSong song) {
		if(this.settings != null){
			new LilypondOutputStream(stream,this.settings).writeSong(song);
		}
	}
}