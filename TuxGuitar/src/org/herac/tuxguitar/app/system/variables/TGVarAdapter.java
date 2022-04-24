package org.herac.tuxguitar.app.system.variables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;

public class TGVarAdapter {

	public static void initialize(TGContext context) {
		Map<String, Object> variables = new HashMap<String, Object>();
		
		variables.put(TGVarAppName.NAME, new TGVarAppName());
		variables.put(TGVarAppVersion.NAME, new TGVarAppVersion());
		variables.put(TGVarFileName.NAME, new TGVarFileName(context));
		variables.put(TGVarFilePath.NAME, new TGVarFilePath(context));
		variables.put(TGVarSongName.NAME, new TGVarSongName(context));
		variables.put(TGVarSongAuthor.NAME, new TGVarSongAuthor(context));
		variables.put(TGVarSongAlbum.NAME, new TGVarSongAlbum(context));
		variables.put(TGVarSongArtist.NAME, new TGVarSongArtist(context));
		variables.put(TGHomePath.NAME, new TGHomePath());
		variables.put(TGUserSharePath.NAME, new TGUserSharePath());
		variables.put(TGConfigPath.NAME, new TGConfigPath());
		variables.put(TGPluginConfigPath.NAME, new TGPluginConfigPath());
		
		List<TGVarColorAppearance> tgVarColorAppearances = TGVarColorAppearance.createVars(context);
		for(TGVarColorAppearance tgVarColorAppearance : tgVarColorAppearances) {
			variables.put(tgVarColorAppearance.getName(), tgVarColorAppearance);
		}
		
		TGExpressionResolver.getInstance(context).addResolver(new TGExpressionResolver.MapPropertyResolver(variables));
	}
}
