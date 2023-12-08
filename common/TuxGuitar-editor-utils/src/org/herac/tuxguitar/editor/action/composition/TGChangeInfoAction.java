package org.herac.tuxguitar.editor.action.composition;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGChangeInfoAction extends TGActionBase {
	
	public static final String NAME = "action.composition.change-info";
	
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_ARTIST = "artist";
	public static final String ATTRIBUTE_ALBUM = "album";
	public static final String ATTRIBUTE_AUTHOR = "author";
	public static final String ATTRIBUTE_DATE = "date";
	public static final String ATTRIBUTE_COPYRIGHT = "copyright";
	public static final String ATTRIBUTE_WRITER = "writer";
	public static final String ATTRIBUTE_TRANSCRIBER = "transcriber";
	public static final String ATTRIBUTE_COMMENTS = "comments";
	
	public TGChangeInfoAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		String name = (String) context.getAttribute(ATTRIBUTE_NAME);
		String artist = (String) context.getAttribute(ATTRIBUTE_ARTIST);
		String album = (String) context.getAttribute(ATTRIBUTE_ALBUM);
		String author = (String) context.getAttribute(ATTRIBUTE_AUTHOR);
		String date = (String) context.getAttribute(ATTRIBUTE_DATE);
		String copyright = (String) context.getAttribute(ATTRIBUTE_COPYRIGHT);
		String writer = (String) context.getAttribute(ATTRIBUTE_WRITER);
		String transcriber = (String) context.getAttribute(ATTRIBUTE_TRANSCRIBER);
		String comments = (String) context.getAttribute(ATTRIBUTE_COMMENTS);
		
		TGSongManager tgSongManager = getSongManager(context);
		tgSongManager.setProperties(song, name, artist, album, author, date, copyright, writer, transcriber, comments);
	}
}
