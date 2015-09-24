package org.herac.tuxguitar.android.drawer.menu;

import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.view.expandableList.TGExpandableGroup;
import org.herac.tuxguitar.android.view.expandableList.TGExpandableHandler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TGExpandableGroupHandler implements TGExpandableHandler {
	
	private TGExpandableGroup resource;
	
	public TGExpandableGroupHandler(TGExpandableGroup resource) {
		this.resource = resource;
	}
	
	@Override
	public View getView(LayoutInflater layoutInflater, boolean isLast, View convertView, ViewGroup parent) {
		View view = layoutInflater.inflate(R.layout.view_drawer_group, parent, false);
		
		TextView textView = (TextView) view.findViewById(R.id.tg_drawer_group);
		if( textView != null ) {
			textView.setText(this.resource.getText());
			textView.setEnabled(this.resource.isEnabled());
		}
		return view;
	}
}
