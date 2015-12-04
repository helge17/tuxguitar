package org.herac.tuxguitar.android.view.util;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TGSelectableAdapter extends ArrayAdapter<TGSelectableItem> {

	public TGSelectableAdapter(Context context, int resource, List<TGSelectableItem> items) {
		super(context, resource, items);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		TGSelectableItem item = this.getItem(position);
		if( item != null ) {
			this.updateTextView(view, item.getLabel());
		}
		
		return view;
	}
	
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = super.getDropDownView(position, convertView, parent);
		
		TGSelectableItem item = this.getItem(position);
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
