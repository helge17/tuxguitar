package org.herac.tuxguitar.android.drawer.menu;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.view.expandableList.TGExpandableGroup;
import org.herac.tuxguitar.android.view.expandableList.TGExpandableItem;

public class TGExpandableMenu {
	
	public TGExpandableMenu() {
		super();
	}
	
	public List<TGExpandableGroup> buildGroups() {
		List<TGExpandableGroup> groups = new ArrayList<TGExpandableGroup>();
		groups.add(createGroup(R.string.menu_file, R.layout.view_file_tool));
		groups.add(createGroup(R.string.menu_transport, R.layout.view_transport_tool));
		groups.add(createGroup(R.string.menu_composition, R.layout.view_composition_tool));
		groups.add(createGroup(R.string.menu_track, R.layout.view_track_tool));
		groups.add(createGroup(R.string.menu_measure, R.layout.view_measure_tool));
		groups.add(createGroup(R.string.menu_beat, R.layout.view_beat_tool));
		groups.add(createGroup(R.string.menu_view, R.layout.view_view_tool));
		return groups;
	}

	public TGExpandableGroup createGroup(int label, int layout) {
		TGExpandableItem tgExpandableItem = new TGExpandableItem();
		tgExpandableItem.setHandler(new TGExpandableItemHandler(layout));
		
		TGExpandableGroup tgExpandableGroup = new TGExpandableGroup(label);
		tgExpandableGroup.setHandler(new TGExpandableGroupHandler(tgExpandableGroup));
		tgExpandableGroup.getItems().add(tgExpandableItem);
		
		return tgExpandableGroup;
	}
}
