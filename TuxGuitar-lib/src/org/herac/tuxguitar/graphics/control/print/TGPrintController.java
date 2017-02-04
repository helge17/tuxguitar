package org.herac.tuxguitar.graphics.control.print;

import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.TGController;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
import org.herac.tuxguitar.graphics.control.TGResourceBuffer;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

public class TGPrintController implements TGController {
	
	private TGSong song;
	private TGSongManager songManager;
	private TGResourceFactory resourceFactory;
	private TGResourceBuffer resourceBuffer;
	private TGLayoutStyles styles;
	
	public TGPrintController(TGSong song, TGSongManager songManager, TGResourceFactory resourceFactory, TGLayoutStyles styles){
		this.song = song;
		this.songManager = songManager;
		this.styles = styles;
		this.resourceFactory = resourceFactory;
		this.resourceBuffer = new TGResourceBuffer();
	}
	
	public TGSong getSong() {
		return song;
	}

	public TGSongManager getSongManager() {
		return this.songManager;
	}
	
	public TGResourceFactory getResourceFactory(){
		return this.resourceFactory;
	}
	
	public TGResourceBuffer getResourceBuffer() {
		return this.resourceBuffer;
	}
	
	public TGLayoutStyles getStyles(){
		return this.styles;
	}
	
	public int getTrackSelection() {
		return -1;
	}
	
	public boolean isRunning(TGBeat beat) {
		return false;
	}
	
	public boolean isRunning(TGMeasure measure) {
		return false;
	}
	
	public boolean isLoopSHeader(TGMeasureHeader measureHeader) {
		return false;
	}
	
	public boolean isLoopEHeader(TGMeasureHeader measureHeader) {
		return false;
	}
}
