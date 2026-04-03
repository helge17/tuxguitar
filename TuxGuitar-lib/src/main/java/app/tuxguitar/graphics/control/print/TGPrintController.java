package app.tuxguitar.graphics.control.print;

import app.tuxguitar.graphics.control.TGController;
import app.tuxguitar.graphics.control.TGLayoutStyles;
import app.tuxguitar.graphics.control.TGResourceBuffer;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.resource.UIResourceFactory;

public class TGPrintController implements TGController {

	private TGSong song;
	private TGSongManager songManager;
	private UIResourceFactory resourceFactory;
	private TGResourceBuffer resourceBuffer;
	private TGLayoutStyles styles;

	public TGPrintController(TGSong song, TGSongManager songManager, UIResourceFactory resourceFactory, TGLayoutStyles styles){
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

	public UIResourceFactory getResourceFactory(){
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
