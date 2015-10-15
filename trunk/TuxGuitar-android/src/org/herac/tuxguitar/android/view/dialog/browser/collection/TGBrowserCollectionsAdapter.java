package org.herac.tuxguitar.android.view.dialog.browser.collection;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.browser.TGBrowserCollection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TGBrowserCollectionsAdapter extends BaseAdapter {

	private Context context;
	private TGBrowserCollectionsDialog dialog;
	private List<TGBrowserCollection> collections;

	public TGBrowserCollectionsAdapter(TGBrowserCollectionsDialog dialog, Context context) {
		this.dialog = dialog;
		this.context = context;
		this.collections = new ArrayList<TGBrowserCollection>();
	}

	public void clearCollections() {
		this.collections.clear();
	}
	
	public void addCollection(TGBrowserCollection collection) {
		this.collections.add(collection);
	}
	
	@Override
	public int getCount() {
		return this.collections.size();
	}

	@Override
	public Object getItem(int position) {
		return this.collections.get(position);
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
		final TGBrowserCollection collection = this.collections.get(position);
		
		View view = (convertView != null ? convertView : getLayoutInflater().inflate(R.layout.view_browser_collections_item, parent, false));
		view.setTag(collection);
		
		TextView textView = (TextView) view.findViewById(R.id.browser_collections_item_name);
		textView.setText(collection.getSettings().getTitle());
		
		ImageView imageView = (ImageView) view.findViewById(R.id.browser_collections_item_icon);
		imageView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TGBrowserCollectionsAdapter.this.dialog.removeCollection(collection);
			}
		});
		
		return view;
	}
}
