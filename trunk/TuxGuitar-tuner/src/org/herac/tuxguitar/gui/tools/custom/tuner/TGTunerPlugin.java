package org.herac.tuxguitar.gui.tools.custom.tuner;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.song.models.TGString;

/**
 * @author Nikola Kolarovic <nikola.kolarovic at gmail.com>
 *
 */
public class TGTunerPlugin extends org.herac.tuxguitar.gui.system.plugins.base.TGToolItemPlugin {

	
	protected void doAction() {
		List strings = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getTrack().getStrings();
		Iterator it = strings.iterator();
		int[] tuning = new int[strings.size()];
		int i=0;
		while (it.hasNext()) {
			TGString current = (TGString)it.next();
			tuning[i] = current.getValue();
			i++;
		}
		TGTunerDialog dialog = new TGTunerDialog(tuning);
		dialog.show();
		
	}

	protected String getItemName() {
		return "Guitar Tuner";
	}

	public String getName() {
		return "GuitarTuner";
	}
	
	public String getAuthor() {
		return "Nikola Kolarovic";
	}

	public String getDescription() {
		return "Visual tuner that analyses the most dominant frequency from your microphone" +
			   " and displays it on the tuner scale.";
	}

	public String getVersion() {
		return "0.01b";
	}	
	

}
