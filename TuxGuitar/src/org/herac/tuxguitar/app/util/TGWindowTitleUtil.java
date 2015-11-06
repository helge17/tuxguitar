package org.herac.tuxguitar.app.util;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.document.TGDocumentFileManager;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;
import org.herac.tuxguitar.util.TGVersion;

public class TGWindowTitleUtil {
	
	public static final String VAR_APP_NAME = "appname";
	public static final String VAR_APP_VERSION = "appversion";
	public static final String VAR_FILE_NAME = "filename";
	public static final String VAR_FILE_PATH = "filepath";
	public static final String VAR_SONG_NAME = "songname";
	public static final String VAR_SONG_AUTHOR = "songauthor";
	public static final String VAR_SONG_ALBUM = "songalbum";
	public static final String VAR_SONG_ARTIST = "songartist";
	
	public static String parseTitle(TGContext context){
		TGWindowTitleUtil.fillVariables(context);
		
		String titleProperty = TGConfigManager.getInstance(context).getStringValue(TGConfigKeys.WINDOW_TITLE);
		String title = TGExpressionResolver.getInstance(context).resolve(titleProperty);
		
		return ((title == null) ? TuxGuitar.APPLICATION_NAME : title);
	}
	
	private static void fillVariables(TGContext context) {
		TGExpressionResolver tgExpressionResolver = TGExpressionResolver.getInstance(context);
		tgExpressionResolver.setVariable(VAR_APP_NAME, TuxGuitar.APPLICATION_NAME);
		tgExpressionResolver.setVariable(VAR_APP_VERSION, TGVersion.CURRENT.getVersion());
		tgExpressionResolver.setVariable(VAR_FILE_NAME, TGDocumentFileManager.getInstance(context).getCurrentFileName(TGFileChooser.DEFAULT_SAVE_FILENAME));
		tgExpressionResolver.setVariable(VAR_FILE_PATH, TGDocumentFileManager.getInstance(context).getCurrentFilePath());
		tgExpressionResolver.setVariable(VAR_SONG_NAME, TGDocumentManager.getInstance(context).getSong().getName());
		tgExpressionResolver.setVariable(VAR_SONG_AUTHOR, TGDocumentManager.getInstance(context).getSong().getAuthor());
		tgExpressionResolver.setVariable(VAR_SONG_ALBUM, TGDocumentManager.getInstance(context).getSong().getAlbum());
		tgExpressionResolver.setVariable(VAR_SONG_ARTIST, TGDocumentManager.getInstance(context).getSong().getArtist());
	}
}