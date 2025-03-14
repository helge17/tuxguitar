package app.tuxguitar.android.menu.controller.impl.fragment;

import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.action.TGActionProcessorListener;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.activity.TGActivityController;
import app.tuxguitar.android.menu.controller.TGMenuController;
import app.tuxguitar.editor.action.channel.TGAddNewChannelAction;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

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
