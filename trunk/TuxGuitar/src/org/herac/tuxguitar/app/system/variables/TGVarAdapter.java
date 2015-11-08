package org.herac.tuxguitar.app.system.variables;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;

public class TGVarAdapter {

	public static void initialize(TGContext context) {
		TGExpressionResolver tgExpressionResolver = TGExpressionResolver.getInstance(context);
		tgExpressionResolver.setVariable(TGVarAppName.NAME, new TGVarAppName());
		tgExpressionResolver.setVariable(TGVarAppVersion.NAME, new TGVarAppVersion());
		tgExpressionResolver.setVariable(TGVarFileName.NAME, new TGVarFileName(context));
		tgExpressionResolver.setVariable(TGVarFilePath.NAME, new TGVarFilePath(context));
		tgExpressionResolver.setVariable(TGVarSongName.NAME, new TGVarSongName(context));
		tgExpressionResolver.setVariable(TGVarSongAuthor.NAME, new TGVarSongAuthor(context));
		tgExpressionResolver.setVariable(TGVarSongAlbum.NAME, new TGVarSongAlbum(context));
		tgExpressionResolver.setVariable(TGVarSongArtist.NAME, new TGVarSongArtist(context));
		tgExpressionResolver.setVariable(TGHomePath.NAME, new TGHomePath());
		tgExpressionResolver.setVariable(TGUserSharePath.NAME, new TGUserSharePath());
		tgExpressionResolver.setVariable(TGConfigPath.NAME, new TGConfigPath());
		tgExpressionResolver.setVariable(TGPluginConfigPath.NAME, new TGPluginConfigPath());
	}
}
