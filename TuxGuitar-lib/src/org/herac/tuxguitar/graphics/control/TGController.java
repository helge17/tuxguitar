package org.herac.tuxguitar.graphics.control;

import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

public interface TGController {
	
	public TGResourceFactory getResourceFactory();
	
	public TGResourceBuffer getResourceBuffer();
	
	public TGSongManager getSongManager();
	
	public TGSong getSong();
	
	public TGLayoutStyles getStyles();
	
	public int getTrackSelection();
	
	public boolean isRunning(TGBeat beat);
	
	public boolean isRunning(TGMeasure measure);
	
	public boolean isLoopSHeader(TGMeasureHeader measureHeader);
	
	public boolean isLoopEHeader(TGMeasureHeader measureHeader);
}
