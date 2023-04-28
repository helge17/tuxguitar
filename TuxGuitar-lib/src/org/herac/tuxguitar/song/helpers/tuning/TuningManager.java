package org.herac.tuxguitar.song.helpers.tuning;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.song.helpers.tuning.xml.TuningReader;
import org.herac.tuxguitar.song.models.TGTuning;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TuningManager {
	
	private TGContext context;
	
	private List<TGTuning> allTunings;
	private TuningGroup tuningsRoot;
	private int treeDepth;

	private TuningManager(TGContext context){
		this.context = context;
		this.allTunings = new ArrayList<TGTuning>();
		this.tuningsRoot = new TuningGroup();
		this.loadTunings();
	}
	
	public List<TGTuning> getAllTunings() {
		return this.allTunings;
	}
	
	public TuningGroup getTuningsRoot() {
		return this.tuningsRoot;
	}
	public int getTreeDepth() { return this.treeDepth; }
	
	private void addTuningsToAll(int depth, String prefix, TuningGroup group) {
		if (!group.getTunings().isEmpty()) {
			this.treeDepth = Math.max(depth, this.treeDepth);
		}
		for (TuningPreset tuning : group.getTunings()) {
			TGTuning prefixedTuning = new TGTuning();
			prefixedTuning.setName(prefix + tuning.getName());
			prefixedTuning.setValues(tuning.getValues());
			this.allTunings.add(prefixedTuning);
		}
		for (TuningGroup subGroup : group.getGroups()) {
			addTuningsToAll(depth + 1, prefix + subGroup.getName() + " / ", subGroup);
		}
	}
	
	private void loadTunings(){
		try{
			new TuningReader().loadTunings(this.tuningsRoot, TGResourceManager.getInstance(this.context).getResourceAsStream("tunings/tunings.xml") );
			addTuningsToAll(1, "", this.tuningsRoot);
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
