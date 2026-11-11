package app.tuxguitar.song.helpers.tuning;

import app.tuxguitar.resource.TGResourceManager;
import app.tuxguitar.song.helpers.tuning.xml.TuningReader;
import app.tuxguitar.song.helpers.tuning.xml.TuningWriter;
import app.tuxguitar.song.models.TGTuning;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGUserFileUtils;
import app.tuxguitar.util.error.TGErrorManager;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TuningManager {

	static String TG_TUNING_FILE = "tunings" + File.separator + "tunings.xml";

	private TGContext context;

	private TuningGroup tgTuningsGroup;
	private TuningGroup customTuningsGroup;

	private TuningManager(TGContext context){
		this.context = context;
		this.tgTuningsGroup = new TuningGroup();
		this.customTuningsGroup = new TuningGroup();
		this.loadTunings();
	}

	public TuningGroup getTgTuningsGroup() {
		return this.tgTuningsGroup;
	}

	public TuningGroup getCustomTuningsGroup() {
		return this.customTuningsGroup;
	}

	private List<TGTuning> getTuningsInGroup(String prefix, TuningGroup group, boolean isPrioritized) {
		List<TGTuning> list = new ArrayList<TGTuning>();
		for (TuningGroup subGroup : group.getGroups()) {
			list.addAll(getTuningsInGroup(getGroupPrefix(prefix, subGroup), subGroup, isPrioritized));
		}
		for (TuningPreset preset : group.getTunings()) {
			// a priority list only keeps the tunings that have a priority value
			if (!isPrioritized || preset.getPriority() != null) {
				list.add(copyTuning(prefix, preset, isPrioritized));
			}
		}
		return(list);
	}

	private static String getGroupPrefix(String prefix, TuningGroup subGroup) {
		return (prefix.equals("") ? "" : (prefix + " / ")) + subGroup.getName();
	}

	private static TGTuning copyTuning(String prefix, TuningPreset preset, boolean withPriority) {
		TuningPreset tgTuning = new TuningPreset(null, prefix + " / " + preset.getName(), preset.getValues());
		if (withPriority) {
			tgTuning.setPriority(preset.getPriority());
		}
		return tgTuning;
	}

	// flat list of tunings, prefixed with group names (recursively)
	public List<TGTuning> getTgTunings() {
		return (getTuningsInGroup("", tgTuningsGroup, false));
	}

	public List<TGTuning> getPriorityTgTunings() {
		List<TGTuning> priorityTunings = getTuningsInGroup("", tgTuningsGroup, true);
		Collections.sort(priorityTunings);

		return priorityTunings;
	}

	public void saveCustomTunings(TuningGroup group) {
		TuningWriter.write(group, TGUserFileUtils.PATH_USER_TUNINGS);
	}

	private void loadTunings(){
		try{
			TuningReader tuningReader = new TuningReader();
			tuningReader.loadTunings(this.tgTuningsGroup, TGResourceManager.getInstance(this.context).getResourceAsStream(TG_TUNING_FILE) );
			File file = new File(TGUserFileUtils.PATH_USER_TUNINGS);
			if (TGUserFileUtils.isExistentAndReadable(file)) {
				tuningReader.loadTunings(this.customTuningsGroup, new FileInputStream(TGUserFileUtils.PATH_USER_TUNINGS));
			}
		} catch (Throwable e) {
			TGErrorManager.getInstance(this.context).handleError(e);
		}
	}

	public static TuningManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TuningManager.class.getName(), new TGSingletonFactory<TuningManager>() {
			public TuningManager createInstance(TGContext context) {
				return new TuningManager(context);
			}
		});
	}
}
