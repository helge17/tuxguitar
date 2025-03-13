package org.herac.tuxguitar.song.helpers.tuning;

import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.song.helpers.tuning.xml.TuningReader;
import org.herac.tuxguitar.song.helpers.tuning.xml.TuningWriter;
import org.herac.tuxguitar.song.models.TGTuning;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGUserFileUtils;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

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
			String groupPrefix = (prefix.equals("") ? "" : (prefix + " / ")) + subGroup.getName();
			list.addAll(getTuningsInGroup(groupPrefix, subGroup, isPrioritized));
		}
		for (TuningPreset preset : group.getTunings()) {
			TuningPreset tgTuning = new TuningPreset(null, prefix + " / " + preset.getName(), preset.getValues());

			// Add every tunings if we don't want a priority list.
			if (!isPrioritized)
				list.add((TGTuning)tgTuning);

			// If we want a priority list, we only add the ones that have priority value,
			// discard ones that don't
			if (isPrioritized && preset.getPriority() != null) {
				tgTuning.setPriority(preset.getPriority());
				list.add((TGTuning)tgTuning);
			}
		}
		return(list);
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
