package app.tuxguitar.song.helpers.tuning;

import app.tuxguitar.resource.TGResourceManager;
import app.tuxguitar.song.helpers.tuning.xml.TuningReader;
import app.tuxguitar.song.helpers.tuning.xml.TuningWriter;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGTuning;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGUserFileUtils;
import app.tuxguitar.util.error.TGErrorManager;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TuningManager {

	private static final int TOP_TUNING_PRIORITY = 1;
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

	public String getDefaultTuningName() {
		TuningPreset defaultTuning = findFirstTuningWithPriority(TOP_TUNING_PRIORITY, this.tgTuningsGroup);
		if (defaultTuning != null) {
			return defaultTuning.getName();
		}
		throw new IllegalStateException("No tuning with priority " + TOP_TUNING_PRIORITY + " found");
	}

	public String getTuningNameForTrack(TGTrack track) {
		if (track == null || track.isPercussion() || track.stringCount() == 0) {
			return null;
		}
		int[] values = new int[track.stringCount()];
		for (int i = 0; i < track.stringCount(); i++) {
			values[i] = track.getString(i + 1).getValue();
		}
		return findTuningName(values);
	}

	public String findTuningName(int[] values) {
		String name = findTuningNameInGroup(values, this.customTuningsGroup);
		if (name != null) {
			return name;
		}
		return findTuningNameInGroup(values, this.tgTuningsGroup);
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

	private String findTuningNameInGroup(int[] values, TuningGroup group) {
		if (values == null || group == null) {
			return null;
		}
		for (TuningGroup subGroup : group.getGroups()) {
			String name = findTuningNameInGroup(values, subGroup);
			if (name != null) {
				return name;
			}
		}
		for (TuningPreset preset : group.getTunings()) {
			if (Arrays.equals(values, preset.getValues())) {
				return preset.getName();
			}
		}
		return null;
	}

	private TuningPreset findFirstTuningWithPriority(int priority, TuningGroup group) {
		for (TuningGroup subGroup : group.getGroups()) {
			TuningPreset tuning = findFirstTuningWithPriority(priority, subGroup);
			if (tuning != null) {
				return tuning;
			}
		}
		for (TuningPreset preset : group.getTunings()) {
			if (Objects.equals(priority, preset.getPriority())) {
				return preset;
			}
		}
		return null;
	}

	public static TuningManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TuningManager.class.getName(), new TGSingletonFactory<TuningManager>() {
			public TuningManager createInstance(TGContext context) {
				return new TuningManager(context);
			}
		});
	}
}
