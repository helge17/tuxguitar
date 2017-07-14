package org.herac.tuxguitar.android.view.dialog.track;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class TGTrackTuningAdapter extends BaseAdapter {

	private Context context;
	private TGTrackTuningDialog dialog;

	public TGTrackTuningAdapter(TGTrackTuningDialog dialog, Context context) {
		this.dialog = dialog;
		this.context = context;
	}

	@Override
	public int getCount() {
		return this.dialog.getTuning().size();
	}

	@Override
	public Object getItem(int position) {
		return this.dialog.getTuning().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public LayoutInflater getLayoutInflater() {
		return (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final TGTrackTuningModel tuning = this.dialog.getTuning().get(position);
		
		View view = (convertView != null ? convertView : getLayoutInflater().inflate(android.R.layout.simple_list_item_activated_1, parent, false));
		view.setTag(tuning);
		
		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		textView.setText(tuning.getName());
		textView.setOnLongClickListener(this.dialog.getActionHandler().createTuningModelMenuAction(tuning, view));

		return view;
	}
}
