package org.herac.tuxguitar.android.menu.controller.impl.fragment;

import android.view.Menu;
import android.view.MenuInflater;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.android.menu.controller.TGMenuController;
import org.herac.tuxguitar.editor.action.channel.TGAddNewChannelAction;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGChannelListMenu implements TGMenuController {
	
	private TGContext context;

	private TGChannelListMenu(TGContext context) {
		this.context = context;
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_channel_list, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		menu.findItem(R.id.action_channel_list_add).setOnMenuItemClickListener(createActionProcessor(TGAddNewChannelAction.NAME));
	}
	
	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(getContext(), actionId);
	}

	public TGContext getContext() {
		return context;
	}

	public TGActivity getActivity() {
		return TGActivityController.getInstance(this.context).getActivity();
	}

	public static TGChannelListMenu getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGChannelListMenu.class.getName(), new TGSingletonFactory<TGChannelListMenu>() {
			public TGChannelListMenu createInstance(TGContext context) {
				return new TGChannelListMenu(context);
			}
		});
	}
}
