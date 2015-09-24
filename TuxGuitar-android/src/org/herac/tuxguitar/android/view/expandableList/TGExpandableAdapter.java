package org.herac.tuxguitar.android.view.expandableList;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

public class TGExpandableAdapter extends BaseExpandableListAdapter {

	private List<TGExpandableGroup> groups;
	private LayoutInflater layoutInflater;
	
	public TGExpandableAdapter(LayoutInflater layoutInflater, List<TGExpandableGroup> groups) {
		this.layoutInflater = layoutInflater;
		this.groups = groups;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return this.groups.get(groupPosition).getItems().get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		TGExpandableGroup group = this.groups.get(groupPosition);
		TGExpandableItem item = group.getItems().get(childPosition);
		
		return item.getHandler().getView(this.layoutInflater, isLastChild, convertView, parent);
	}

	public int getChildrenCount(int groupPosition) {
		return this.groups.get(groupPosition).getItems().size();
	}

	public Object getGroup(int groupPosition) {
		return this.groups.get(groupPosition);
	}

	public int getGroupCount() {
		return this.groups.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		TGExpandableGroup group = this.groups.get(groupPosition);
		
		return group.getHandler().getView(this.layoutInflater, false, convertView, parent);
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}
}