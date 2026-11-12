package app.tuxguitar.editor.undo.impl.track;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.channel.TGAddChannelAction;
import app.tuxguitar.editor.action.track.TGAddTrackAction;
import app.tuxguitar.editor.action.track.TGCloneTrackAction;
import app.tuxguitar.editor.action.track.TGCopyTrackFromAction;
import app.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import app.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import app.tuxguitar.editor.action.track.TGRemoveTrackAction;
import app.tuxguitar.editor.action.track.TGSetTrackChannelAction;
import app.tuxguitar.editor.action.track.TGSetTrackInfoAction;
import app.tuxguitar.editor.action.track.TGSetTrackLyricsAction;
import app.tuxguitar.editor.action.track.TGSetTrackMuteAction;
import app.tuxguitar.editor.action.track.TGSetTrackSoloAction;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGColor;
import app.tuxguitar.song.models.TGLyric;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public abstract class TGUndoableTrackBase extends TGUndoableEditBase{

	protected TGUndoableTrackBase(TGContext context){
		super(context);
	}

	// runs the action by name, bypassing undo, with the attributes given as key, value, key, value...
	private void process(TGActionContext context, String actionName, Object... attributes) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(actionName);
		for(int i = 0 ; i < attributes.length ; i += 2) {
			tgActionProcessor.setAttribute((String) attributes[i], attributes[i + 1]);
		}
		this.processByPassUndoableAction(tgActionProcessor, context);
	}

	// runs the action by name, bypassing undo, on the track of the song
	private void processOnTrack(TGActionContext context, String actionName, TGSong song, TGTrack track) {
		this.process(context, actionName, TGDocumentContextAttributes.ATTRIBUTE_SONG, song, TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
	}

	public void addTrack(TGActionContext context, TGSong song, TGTrack track) {
		this.processOnTrack(context, TGAddTrackAction.NAME, song, track);
	}

	// adds the track back with the channel of its own it had, when it is an audio track (ownChannel is then not null)
	public void addTrack(TGActionContext context, TGSong song, TGTrack track, TGChannel ownChannel) {
		if( ownChannel != null ) {
			this.process(context, TGAddChannelAction.NAME, TGDocumentContextAttributes.ATTRIBUTE_SONG, song,
				TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, ownChannel.clone(getSongManager().getFactory()));
		}
		this.addTrack(context, song, track);
	}

	// a copy of the channel of its own an audio track has, which goes when the track goes; null for any other track
	public static TGChannel findOwnChannel(TGContext context, TGTrack track) {
		if(!track.isAudio()) {
			return null;
		}
		TGSongManager songManager = getSongManager(context);
		return songManager.getChannel(getSong(context), track.getChannelId()).clone(songManager.getFactory());
	}

	public void removeTrack(TGActionContext context, TGSong song, TGTrack track) {
		this.processOnTrack(context, TGRemoveTrackAction.NAME, song, track);
	}

	public void cloneTrack(TGActionContext context, TGSong song, TGTrack track) {
		this.processOnTrack(context, TGCloneTrackAction.NAME, song, track);
	}

	public void moveTrackUp(TGActionContext context, TGSong song, TGTrack track) {
		this.processOnTrack(context, TGMoveTrackUpAction.NAME, song, track);
	}

	public void moveTrackDown(TGActionContext context, TGSong song, TGTrack track) {
		this.processOnTrack(context, TGMoveTrackDownAction.NAME, song, track);
	}

	public void copyTrackFrom(TGActionContext context, TGSong song, TGTrack track, TGTrack from) {
		this.process(context, TGCopyTrackFromAction.NAME, TGDocumentContextAttributes.ATTRIBUTE_SONG, song,
			TGDocumentContextAttributes.ATTRIBUTE_TRACK, track, TGCopyTrackFromAction.ATTRIBUTE_FROM, from);
	}

	public void setTrackSolo(TGActionContext context, TGTrack track, Boolean solo) {
		this.process(context, TGSetTrackSoloAction.NAME, TGDocumentContextAttributes.ATTRIBUTE_TRACK, track, TGSetTrackSoloAction.ATTRIBUTE_SOLO, solo);
	}

	public void setTrackMute(TGActionContext context, TGTrack track, Boolean mute) {
		this.process(context, TGSetTrackMuteAction.NAME, TGDocumentContextAttributes.ATTRIBUTE_TRACK, track, TGSetTrackMuteAction.ATTRIBUTE_MUTE, mute);
	}

	public void setTrackInfo(TGActionContext context, TGTrack track, String name, Integer offset, TGColor color) {
		this.process(context, TGSetTrackInfoAction.NAME, TGDocumentContextAttributes.ATTRIBUTE_TRACK, track,
			TGSetTrackInfoAction.ATTRIBUTE_TRACK_NAME, name, TGSetTrackInfoAction.ATTRIBUTE_TRACK_OFFSET, offset,
			TGSetTrackInfoAction.ATTRIBUTE_TRACK_COLOR, color);
	}

	public void setTrackChannel(TGActionContext context, TGTrack track, TGChannel channel) {
		this.process(context, TGSetTrackChannelAction.NAME, TGDocumentContextAttributes.ATTRIBUTE_TRACK, track, TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
	}

	public void setTrackLyrics(TGActionContext context, TGTrack track, TGLyric lyrics) {
		this.process(context, TGSetTrackLyricsAction.NAME, TGDocumentContextAttributes.ATTRIBUTE_TRACK, track, TGDocumentContextAttributes.ATTRIBUTE_LYRIC, lyrics);
	}
}
