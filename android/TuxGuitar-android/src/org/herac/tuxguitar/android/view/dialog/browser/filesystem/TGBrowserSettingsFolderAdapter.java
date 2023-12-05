package org.herac.tuxguitar.android.view.dialog.browser.filesystem;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.herac.tuxguitar.android.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TGBrowserSettingsFolderAdapter extends BaseAdapter {

	private Context context;
	private File path;
	private TGBrowserSettingsMountPoint mountPoint;
	private List<TGBrowserSettingsFolderAdapterItem> items;
	private TGBrowserSettingsFolderAdapterListener listener;
	
	public TGBrowserSettingsFolderAdapter(Context context, TGBrowserSettingsMountPoint mountPoint) {
		this.context = context;
		this.mountPoint = mountPoint;
		this.items = new ArrayList<TGBrowserSettingsFolderAdapterItem>();
		this.updatePath(this.mountPoint.getPath());
	}
	
	public File getPath() {
		return this.path;
	}

	public void setListener(TGBrowserSettingsFolderAdapterListener listener) {
		this.listener = listener;
	}

	@Override
	public int getCount() {
		return this.items.size();
	}

	@Override
	public Object getItem(int position) {
		return this.items.get(position);
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
		TGBrowserSettingsFolderAdapterItem item = this.items.get(position);
		
		View view = (convertView != null ? convertView : getLayoutInflater().inflate(R.layout.view_browser_element, parent, false));
		view.setTag(item);
		
		TextView textView = (TextView) view.findViewById(R.id.tg_browser_element_name);
		textView.setText(item.getLabel());
		
		Drawable styledIcon = this.findStyledFolderIcon();
		if( styledIcon != null ) {
			ImageView imageView = (ImageView) view.findViewById(R.id.tg_browser_element_icon);
			imageView.setImageDrawable(styledIcon);
		}
		
		view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				updatePath(((TGBrowserSettingsFolderAdapterItem) v.getTag()).getFile());
			}
		});
		
		return view;
	}
	
	public Drawable findStyledFolderIcon() {
		TypedArray typedArray = this.context.obtainStyledAttributes(R.style.browserElementIconFolderStyle, new int[] {android.R.attr.src});
		if( typedArray != null ) {
			return typedArray.getDrawable(0);
		}
		return null;
	}
	
	public void updatePath(File path) {
		this.path = path;
		this.items.clear();
		if( this.path != null && this.path.exists() && this.path.isDirectory() ) {
			
			if( this.path.getParentFile() != null && !this.path.equals(this.mountPoint.getPath())) {
				this.items.add(new TGBrowserSettingsFolderAdapterItem("../", this.path.getParentFile()));
			}
			
			List<File> directoryFiles = this.getDirectoryFiles(this.path);
			
			this.sortFiles(directoryFiles);
			
			for(File file : directoryFiles) {
				this.items.add(new TGBrowserSettingsFolderAdapterItem(file.getName(), file));
			}
		}
		this.notifyDataSetChanged();
		
		if( this.listener != null ) {
			this.listener.onPathChanged(this.path);
		}
	}
	
	public List<File> getDirectoryFiles(File parent) {
		List<File> directoryFiles = new ArrayList<File>();
		File[] files = parent.listFiles();
		if( files != null ) {
			for(File file : files) {
				if( file.isDirectory() ) {
					directoryFiles.add(file);
				}
			}
		}
		return directoryFiles;
	}
	
	public void sortFiles(List<File> files) {
		Collections.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				if( f1 == f2 ) {
					return 0;
				}
				if( f1 == null ) {
					return -1;
				}
				if( f2 == null ) {
					return 1;
				}
				return f1.getName().compareTo(f2.getName());
			}
		});
	}
}
