package org.herac.tuxguitar.android.view.expandableList;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class TGExpandableList extends ExpandableListView implements OnGroupExpandListener {

	public TGExpandableList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	protected void onFinishInflate() {
		this.setOnGroupExpandListener(this);
	}
	
	public void onGroupExpand(int groupPosition) {
		this.collapseUnselectedGroups(groupPosition);
	}
	
	public void collapseUnselectedGroups(int selectedGroup) {
		for(int group = 0 ; group < this.getCount() ; group ++) {
			if( group != selectedGroup ) {
				this.collapseGroup(group);
			}
		}
	}
}
