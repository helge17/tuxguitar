package org.herac.tuxguitar.android.drawer.main;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.herac.tuxguitar.android.R;

import java.util.ArrayList;
import java.util.List;

public class TGMainDrawerFileListAdapter extends TGMainDrawerListAdapter {

	private List<TGMainDrawerFileAction> actions;
	
	public TGMainDrawerFileListAdapter(TGMainDrawer mainDrawer) {
		super(mainDrawer);
		
		this.createActions();
	}
	
	public void createActions() {
		this.actions = new ArrayList<TGMainDrawerFileAction>();
		this.actions.add(new TGMainDrawerFileAction(R.string.menu_file_new, getMainDrawer().getActionHandler().createNewFileAction()));
		this.actions.add(new TGMainDrawerFileAction(R.string.menu_file_open, getMainDrawer().getActionHandler().createOpenFileAction()));
		this.actions.add(new TGMainDrawerFileAction(R.string.menu_file_save, getMainDrawer().getActionHandler().createSaveFileAction()));
		this.actions.add(new TGMainDrawerFileAction(R.string.menu_file_save_as, getMainDrawer().getActionHandler().createSaveFileAsAction()));
	}
	
	@Override
	public int getCount() {
		return this.actions.size();
	}

	@Override
	public Object getItem(int position) {
		return this.actions.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TGMainDrawerFileAction action = this.actions.get(position);
		
		View view = (convertView != null ? convertView : getLayoutInflater().inflate(R.layout.view_main_drawer_text_item, parent, false));
		view.setOnClickListener(action.getProcessor());
		
		TextView textView = (TextView) view.findViewById(R.id.main_drawer_text_item);
		textView.setText(action.getLabel());
		
		return view;
	}
}
