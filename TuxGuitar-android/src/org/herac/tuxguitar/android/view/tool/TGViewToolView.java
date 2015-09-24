package org.herac.tuxguitar.android.view.tool;

import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.fragment.TGChannelListFragment;

import android.content.Context;
import android.util.AttributeSet;

public class TGViewToolView extends TGToolView {
	
	public TGViewToolView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void addListeners() {
		this.findViewById(R.id.menu_view_channel_list).setOnClickListener(this.createFragmentActionListener(TGChannelListFragment.getInstance(findContext())));
	}
	
	public void updateItems() {
		this.updateItem(R.id.menu_view_channel_list, true);
	}
}
