package org.herac.tuxguitar.android.menu.controller;

import android.view.Menu;
import android.view.MenuInflater;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGMenuContextualInflater {

	private TGMenuController controller;

	private TGMenuContextualInflater() {
		super();
	}

	public void setController(TGMenuController controller) {
		this.controller = controller;
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		if( this.controller != null ) {
			this.controller.inflate(menu, inflater);
			this.controller = null;
		}
	}

	public static TGMenuContextualInflater getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMenuContextualInflater.class.getName(), new TGSingletonFactory<TGMenuContextualInflater>() {
			public TGMenuContextualInflater createInstance(TGContext context) {
				return new TGMenuContextualInflater();
			}
		});
	}
}
