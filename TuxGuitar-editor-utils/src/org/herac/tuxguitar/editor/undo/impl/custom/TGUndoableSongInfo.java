package org.herac.tuxguitar.editor.undo.impl.custom;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeInfoAction;
import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
import org.herac.tuxguitar.editor.undo.TGCannotUndoException;
import org.herac.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGUndoableSongInfo extends TGUndoableEditBase {
	
	private int doAction;
	private String undoName;
	private String undoArtist;
	private String undoAlbum;
	private String undoAuthor;
	private String undoDate;
	private String undoCopyright;
	private String undoWriter;
	private String undoTranscriber;
	private String undoComments;
	private String redoName;
	private String redoArtist;
	private String redoAlbum;
	private String redoAuthor;
	private String redoDate;
	private String redoCopyright;
	private String redoWriter;
	private String redoTranscriber;
	private String redoComments;
	
	private TGUndoableSongInfo(TGContext context){
		super(context);
	}
	
	public void redo(TGActionContext actionContext) throws TGCannotRedoException {
		if(!canRedo()){
			throw new TGCannotRedoException();
		}
		this.changeInfo(actionContext, getSong(), this.redoName,this.redoArtist,this.redoAlbum,this.redoAuthor,this.redoDate,this.redoCopyright,this.redoWriter,this.redoTranscriber,this.redoComments);
		this.doAction = UNDO_ACTION;
	}
	
	public void undo(TGActionContext actionContext) throws TGCannotUndoException {
		if(!canUndo()){
			throw new TGCannotUndoException();
		}
		this.changeInfo(actionContext, getSong(), this.undoName,this.undoArtist,this.undoAlbum,this.undoAuthor,this.undoDate,this.undoCopyright,this.undoWriter,this.undoTranscriber,this.undoComments);
		this.doAction = REDO_ACTION;
	}
	
	public boolean canRedo() {
		return (this.doAction == REDO_ACTION);
	}
	
	public boolean canUndo() {
		return (this.doAction == UNDO_ACTION);
	}
	
	public static TGUndoableSongInfo startUndo(TGContext context){
		TGSong song = getSong(context);
		TGUndoableSongInfo undoable = new TGUndoableSongInfo(context);
		undoable.doAction = UNDO_ACTION;
		undoable.undoName = song.getName();
		undoable.undoArtist = song.getArtist();
		undoable.undoAlbum = song.getAlbum();
		undoable.undoAuthor = song.getAuthor();
		undoable.undoDate = song.getDate();
		undoable.undoCopyright = song.getCopyright();
		undoable.undoWriter = song.getWriter();
		undoable.undoTranscriber = song.getTranscriber();
		undoable.undoComments = song.getComments();		
		return undoable;
	}
	
	public TGUndoableSongInfo endUndo(){
		TGSong song = getSong();
		this.redoName = song.getName();
		this.redoArtist = song.getArtist();
		this.redoAlbum = song.getAlbum();
		this.redoAuthor = song.getAuthor();
		this.redoDate = song.getDate();
		this.redoCopyright = song.getCopyright();
		this.redoWriter = song.getWriter();
		this.redoTranscriber = song.getTranscriber();
		this.redoComments = song.getComments();
		return this;
	}
	
	public void changeInfo(TGActionContext context, TGSong song, String name,String artist,String album,String author,String date,String copyright,String writer,String transcriber,String comments) {
		TGActionProcessor tgActionProcessor = this.createByPassUndoableAction(TGChangeInfoAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_NAME, name);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_ARTIST, artist);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_ALBUM, album);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_AUTHOR, author);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_DATE, date);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_COPYRIGHT, copyright);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_WRITER, writer);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_TRANSCRIBER, transcriber);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_COMMENTS, comments);
		
		this.processByPassUndoableAction(tgActionProcessor, context);
	}
}
