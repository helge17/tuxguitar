package org.herac.tuxguitar.android.variables;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;

public class TGVarAdapter {

	public static void initialize(TGContext context) {
		TGExpressionResolver tgExpressionResolver = TGExpressionResolver.getInstance(context);
		tgExpressionResolver.setVariable(TGVarAppName.NAME, new TGVarAppName());
		tgExpressionResolver.setVariable(TGVarAppVersion.NAME, new TGVarAppVersion());
		tgExpressionResolver.setVariable(TGVarSongName.NAME, new TGVarSongName(context));
		tgExpressionResolver.setVariable(TGVarSongAuthor.NAME, new TGVarSongAuthor(context));
		tgExpressionResolver.setVariable(TGVarSongAlbum.NAME, new TGVarSongAlbum(context));
		tgExpressionResolver.setVariable(TGVarSongArtist.NAME, new TGVarSongArtist(context));
		tgExpressionResolver.setVariable(TGVarEnvExternalStorageDirectory.NAME, new TGVarEnvExternalStorageDirectory());
		tgExpressionResolver.setVariable(TGVarEnvSecondaryStorageDirectory.NAME, new TGVarEnvSecondaryStorageDirectory());
	}
}
