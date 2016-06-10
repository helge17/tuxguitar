package org.herac.tuxguitar.editor.undo.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.track.TGAddTrackAction;
import org.herac.tuxguitar.editor.action.track.TGCloneTrackAction;
import org.herac.tuxguitar.editor.action.track.TGCopyTrackFromAction;
import org.herac.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import org.herac.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import org.herac.tuxguitar.editor.action.track.TGRemoveTrackAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackChannelAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackInfoAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackLyricsAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackMuteAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackSoloAction;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGLyric;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGUndoableTrackBase extends TGUndoableEditBase{
	
	protected TGUndoableTrackBase(TGContext context){
		super(context);
	}
	
	public void addTrack(TGActionContext context, TGSong song, TGTrack track) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGAddTrackAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	public void removeTrack(TGActionContext context, TGSong song, TGTrack track) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGRemoveTrackAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	public void cloneTrack(TGActionContext context, TGSong song, TGTrack track) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGCloneTrackAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	public void moveTrackUp(TGActionContext context, TGSong song, TGTrack track) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGMoveTrackUpAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	public void moveTrackDown(TGActionContext context, TGSong song, TGTrack track) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGMoveTrackDownAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	public void copyTrackFrom(TGActionContext context, TGSong song, TGTrack track, TGTrack from) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGCopyTrackFromAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGCopyTrackFromAction.ATTRIBUTE_FROM, from);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	public void setTrackSolo(TGActionContext context, TGTrack track, Boolean solo) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGSetTrackSoloAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGSetTrackSoloAction.ATTRIBUTE_SOLO, solo);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	public void setTrackMute(TGActionContext context, TGTrack track, Boolean mute) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGSetTrackMuteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGSetTrackMuteAction.ATTRIBUTE_MUTE, mute);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	public void setTrackInfo(TGActionContext context, TGTrack track, String name, Integer offset, TGColor color) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGSetTrackInfoAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_NAME, name);
		tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_OFFSET, offset);
		tgActionProcessor.setAttribute(TGSetTrackInfoAction.ATTRIBUTE_TRACK_COLOR, color);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	public void setTrackChannel(TGActionContext context, TGTrack track, TGChannel channel) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGSetTrackChannelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
	
	public void setTrackLyrics(TGActionContext context, TGTrack track, TGLyric lyrics) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGSetTrackLyricsAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_LYRIC, lyrics);
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
