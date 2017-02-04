//package org.herac.tuxguitar.io.pdf;
//
//import org.herac.tuxguitar.graphics.TGResourceFactory;
//import org.herac.tuxguitar.graphics.control.TGController;
//import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
//import org.herac.tuxguitar.graphics.control.TGResourceBuffer;
//import org.herac.tuxguitar.song.managers.TGSongManager;
//import org.herac.tuxguitar.song.models.TGBeat;
//import org.herac.tuxguitar.song.models.TGMeasure;
//import org.herac.tuxguitar.song.models.TGMeasureHeader;
//import org.herac.tuxguitar.song.models.TGSong;
//
//public class PDFController implements TGController {
//	
//	private TGSong song;
//	private TGSongManager songManager;
//	private TGResourceFactory resourceFactory;
//	private TGResourceBuffer resourceBuffer;
//	
//	public PDFController(TGSong song, TGSongManager songManager, TGResourceFactory resourceFactory){
//		this.song = song;
//		this.songManager = songManager;
//		this.resourceFactory = resourceFactory;
//		this.resourceBuffer = new TGResourceBuffer();
//	}
//	
//	public TGSong getSong() {
//		return song;
//	}
//
//	public TGSongManager getSongManager() {
//		return this.songManager;
//	}
//	
//	public TGResourceFactory getResourceFactory(){
//		return this.resourceFactory;
//	}
//	
//	public TGResourceBuffer getResourceBuffer() {
//		return this.resourceBuffer;
//	}
//	
//	public int getTrackSelection() {
//		return -1;
//	}
//	
//	public boolean isRunning(TGBeat beat) {
//		return false;
//	}
//	
//	public boolean isRunning(TGMeasure measure) {
//		return false;
//	}
//	
//	public boolean isLoopSHeader(TGMeasureHeader measureHeader) {
//		return false;
//	}
//	
//	public boolean isLoopEHeader(TGMeasureHeader measureHeader) {
//		return false;
//	}
//	
//	public TGLayoutStyles getStyles() {
//		return new PDFControllerStyles();
//	}
//}
