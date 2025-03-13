package app.tuxguitar.android.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import app.tuxguitar.android.R;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.drawer.TGDrawerViewBuilder;
import app.tuxguitar.android.fragment.TGFragmentController;
import app.tuxguitar.android.fragment.impl.TGMainFragmentController;
import app.tuxguitar.util.TGContext;

import java.util.HashMap;
import java.util.Map;

import androidx.drawerlayout.widget.DrawerLayout;

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
