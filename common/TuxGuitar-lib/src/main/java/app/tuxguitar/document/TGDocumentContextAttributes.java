package app.tuxguitar.document;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGLyric;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.song.models.TGTimeSignature;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;

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
	public static final String ATTRIBUTE_BEAT_RANGE = "beat-range";
	public static final String ATTRIBUTE_NOTE_RANGE = "note-range";
	public static final String ATTRIBUTE_KEEP_SELECTION = "keep-selection";
	public static final String ATTRIBUTE_SELECTION_IS_ACTIVE = "selection-is-active";
}
