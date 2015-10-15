package org.herac.tuxguitar.android.menu.options;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.editor.action.channel.TGAddNewChannelAction;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

import android.view.Menu;

public class TGChannelListMenu {
	
	private TGContext context;
	private TGActivity activity;
	private Menu menu;
	
	private TGChannelListMenu(TGContext context) {
		this.context = context;
	}
	
	public void initialize(TGActivity activity, Menu menu) {
		this.activity = activity;
		this.menu = menu;
		this.initializeItems();
	}
	
	public TGContext getContext() {
		return context;
	}

	public TGActivity getActivity() {
		return activity;
	}

	public Menu getMenu() {
		return menu;
	}

	public void initializeItems() {
		this.getMenu().findItem(R.id.menu_channel_list_add).setOnMenuItemClickListener(createActionProcessor(TGAddNewChannelAction.NAME));
	}
	
	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(getContext(), actionId);
	}
	
	public static TGChannelListMenu getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGChannelListMenu.class.getName(), new TGSingletonFactory<TGChannelListMenu>() {
			public TGChannelListMenu createInstance(TGContext context) {
				return new TGChannelListMenu(context);
			}
		});
	}
}
