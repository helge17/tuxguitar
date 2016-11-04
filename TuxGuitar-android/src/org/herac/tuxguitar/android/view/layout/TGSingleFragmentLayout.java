package org.herac.tuxguitar.android.view.layout;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.drawer.TGDrawerViewBuilder;
import org.herac.tuxguitar.android.fragment.TGFragmentController;
import org.herac.tuxguitar.android.fragment.impl.TGMainFragmentController;
import org.herac.tuxguitar.util.TGContext;

import java.util.HashMap;
import java.util.Map;

public class TGSingleFragmentLayout extends DrawerLayout implements TGDrawerViewBuilder {

	private Map<TGFragmentController<?>, Integer> fragmentDrawerLayouts;

	public TGSingleFragmentLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.fragmentDrawerLayouts = new HashMap<TGFragmentController<?>, Integer>();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		this.createFragmentDrawerLayouts();
		this.findActivity().getDrawerManager().setDrawerBuilder(this);
	}

	@Override
	public void onOpenFragment(TGFragmentController<?> controller, ViewGroup drawerView) {
		if (this.fragmentDrawerLayouts.containsKey(controller)) {
			this.findActivity().getLayoutInflater().inflate(this.fragmentDrawerLayouts.get(controller), drawerView);
		}
	}

	public void createFragmentDrawerLayouts() {
		TGContext context = this.findActivity().findContext();

		this.fragmentDrawerLayouts.clear();
		this.fragmentDrawerLayouts.put(TGMainFragmentController.getInstance(context), R.layout.view_main_drawer);
	}

	public TGActivity findActivity() {
		return (TGActivity) getContext();
	}
}
