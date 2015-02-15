package org.herac.tuxguitar.community.io;

import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGShareSongExporter implements TGRawExporter { 
	
	private TGContext context;
	
	public TGShareSongExporter(TGContext context){
		this.context = context;
	}
	
	public void exportSong(TGSong srcSong) throws TGFileFormatException {
		final TGSong song = srcSong.clone(new TGFactory());
		new Thread( new Runnable() {
			public void run() {
				new TGShareSong(getContext()).process( song );
			}
		} ).start();
	}
	
	public String getExportName() {
		return ("Share with the Community");
	}
	
	public TGContext getContext() {
		return context;
	}
}
