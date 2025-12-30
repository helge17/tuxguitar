package app.tuxguitar.app.tools.custom.tuner;

import java.util.Iterator;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.util.TGContext;

/**
 * @author Nikola Kolarovic <nikola.kolarovic at gmail.com>
 *
 */
public class TGTunerPlugin extends app.tuxguitar.app.tools.custom.TGToolItemPlugin {

	public static final String MODULE_ID = "tuxguitar-tuner";

	protected void doAction(TGContext context) {
		List<TGString> strings = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().getStrings();
		Iterator<TGString> it = strings.iterator();
		int[] tuning = new int[strings.size()];
		int i=0;
		while (it.hasNext()) {
			TGString current = it.next();
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
		return "tuner.tuner";
	}

}
