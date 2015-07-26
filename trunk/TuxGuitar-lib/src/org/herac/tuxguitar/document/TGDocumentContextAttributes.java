package org.herac.tuxguitar.document;

import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGLyric;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;

public final class TGDocumentContextAttributes {

	public static final String ATTRIBUTE_SONG_MANAGER = TGSongManager.class.getName();
	public static final String ATTRIBUTE_SONG = TGSong.class.getName();
	public static final String ATTRIBUTE_TRACK = TGTrack.class.getName();
	public static final String ATTRIBUTE_HEADER = TGMeasureHeader.class.getName();
	public static final String ATTRIBUTE_MEASURE = TGMeasure.class.getName();
	public static final String ATTRIBUTE_BEAT = TGBeat.class.getName();
	public static final String ATTRIBUTE_VOICE = TGVoice.class.getName();
	public static final String ATTRIBUTE_NOTE = TGNote.class.getName();
	public static final String ATTRIBUTE_STRING = TGString.class.getName();
	public static final String ATTRIBUTE_DURATION = TGDuration.class.getName();
	public static final String ATTRIBUTE_CHANNEL = TGChannel.class.getName();
	public static final String ATTRIBUTE_TEMPO = TGTempo.class.getName();
	public static final String ATTRIBUTE_TIME_SIGNATURE = TGTimeSignature.class.getName();
	public static final String ATTRIBUTE_LYRIC = TGLyric.class.getName();
	public static final String ATTRIBUTE_CHORD = TGChord.class.getName();
	public static final String ATTRIBUTE_MARKER = TGMarker.class.getName();
	
	public static final String ATTRIBUTE_VELOCITY = "velocity";
	public static final String ATTRIBUTE_POSITION = "position";
	public static final String ATTRIBUTE_FRET = "fret";
	public static final String ATTRIBUTE_VALUE = "value";
}
