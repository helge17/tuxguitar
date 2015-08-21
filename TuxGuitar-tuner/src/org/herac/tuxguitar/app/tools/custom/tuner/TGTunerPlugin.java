package org.herac.tuxguitar.app.tools.custom.tuner;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.util.TGContext;

/**
 * @author Nikola Kolarovic <nikola.kolarovic at gmail.com>
 *
 */
public class TGTunerPlugin extends org.herac.tuxguitar.app.tools.custom.TGToolItemPlugin {
	
	public static final String MODULE_ID = "tuxguitar-tuner";
	
	protected void doAction(TGContext context) {
		List<TGString> strings = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().getStrings();
		Iterator<TGString> it = strings.iterator();
		int[] tuning = new int[strings.size()];
		int i=0;
		while (it.hasNext()) {
			TGString current = (TGString)it.next();
			tuning[i] = current.getValue();
			i++;
		}
		TGTunerDialog dialog = new TGTunerDialog(context, tuning);
		dialog.show();
		
	}

	public String getModuleId(){
		return MODULE_ID;
	}
	
	protected String getItemName() {
		return "Guitar Tuner";
	}
}
