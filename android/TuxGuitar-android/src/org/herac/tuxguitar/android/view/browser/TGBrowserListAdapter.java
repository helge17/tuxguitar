package org.herac.tuxguitar.android.view.browser;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.application.TGApplicationUtil;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.util.error.TGErrorManager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TGBrowserListAdapter extends BaseAdapter {

	private Context context;
	private List<TGBrowserElement> elements;

	public TGBrowserListAdapter(Context context) {
		this.context = context;
		this.elements = new ArrayList<TGBrowserElement>();
	}

	public void clearElements() {
		this.elements.clear();
	}
	
	public void fillElements(List<TGBrowserElement> elements) {
		this.clearElements();
		this.elements.addAll(elements);
	}
	
	@Override
	public int getCount() {
		return this.elements.size();
	}

	@Override
	public Object getItem(int position) {
		return this.elements.get(position);
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
		TGBrowserElement element = this.elements.get(position);
		
		View view = (convertView != null ? convertView : getLayoutInflater().inflate(R.layout.view_browser_element, parent, false));
		view.setTag(element);
		
		try {
			TextView textView = (TextView) view.findViewById(R.id.tg_browser_element_name);
			textView.setText(element.getName());
			
			Drawable elementIcon = this.findElementIcon(element);
			if( elementIcon != null ) {
				ImageView imageView = (ImageView) view.findViewById(R.id.tg_browser_element_icon);
				imageView.setImageDrawable(elementIcon);
			}
		} catch (TGBrowserException e) {
			TGErrorManager.getInstance(TGApplicationUtil.findContext(this.context)).handleError(e);
		}
		return view;
	}
	
	public Drawable findElementIcon(TGBrowserElement element) throws TGBrowserException {
		Integer style = (element.isFolder() ? R.style.browserElementIconFolderStyle : R.style.browserElementIconFileStyle);
		TypedArray typedArray = this.context.obtainStyledAttributes(style, new int[] {android.R.attr.src});
		if( typedArray != null ) {
			return typedArray.getDrawable(0);
		}
		return null;
	}
}
