package app.tuxguitar.graphics.control;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.resource.UIResourceFactory;

public interface TGController {

	public UIResourceFactory getResourceFactory();

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
