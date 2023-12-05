package org.herac.tuxguitar.android.variables;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;

public class TGVarAdapter {

	public static void initialize(TGContext context) {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(TGVarAppName.NAME, new TGVarAppName());
		variables.put(TGVarAppVersion.NAME, new TGVarAppVersion());
		variables.put(TGVarSongName.NAME, new TGVarSongName(context));
		variables.put(TGVarSongAuthor.NAME, new TGVarSongAuthor(context));
		variables.put(TGVarSongAlbum.NAME, new TGVarSongAlbum(context));
		variables.put(TGVarSongArtist.NAME, new TGVarSongArtist(context));
		variables.put(TGVarEnvExternalStorageDirectory.NAME, new TGVarEnvExternalStorageDirectory());
		variables.put(TGVarEnvSecondaryStorageDirectory.NAME, new TGVarEnvSecondaryStorageDirectory());
		
		TGExpressionResolver.getInstance(context).addResolver(new TGExpressionResolver.MapPropertyResolver(variables));
	}
}
