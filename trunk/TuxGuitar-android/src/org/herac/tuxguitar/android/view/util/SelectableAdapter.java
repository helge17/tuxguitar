package org.herac.tuxguitar.android.view.util;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SelectableAdapter extends ArrayAdapter<SelectableItem> {

	public SelectableAdapter(Context context, int resource, List<SelectableItem> items) {
		super(context, resource, items);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		SelectableItem item = this.getItem(position);
		if( item != null ) {
			this.updateTextView(view, item.getLabel());
		}
		
		return view;
	}
	
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = super.getDropDownView(position, convertView, parent);
		
		SelectableItem item = this.getItem(position);
		if( item != null ) {
			this.updateTextView(view, item.getDropDownLabel());
		}
		
		return view;
	}
	
	public void updateTextView(View view, String text) {
		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		if( textView != null ) {
			textView.setText(text);
		}
	}
}
