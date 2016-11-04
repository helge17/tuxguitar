package org.herac.tuxguitar.android.drawer;

import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.fragment.TGFragmentController;
import org.herac.tuxguitar.util.TGContext;

public class TGDrawerManager {
	
	private TGActivity activity;
	private TGDrawerViewBuilder drawerBuilder;
	private ViewGroup drawerView;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private boolean open;
	
	public TGDrawerManager(TGActivity activity) {
		this.activity = activity;
	}

	public void initialize() {
		this.drawerView = (ViewGroup) this.activity.findViewById(R.id.left_drawer);

		this.drawerLayout = (DrawerLayout) this.activity.findViewById(R.id.root_layout);
		this.drawerToggle = new ActionBarDrawerToggle(this.activity, this.drawerLayout, R.string.app_name, R.string.app_name) {
			public void onDrawerClosed(View view) {
				TGDrawerManager.this.onVisibilityChanged();
			}

			public void onDrawerOpened(View drawerView) {
				TGDrawerManager.this.onVisibilityChanged();
			}
		};
		this.drawerLayout.setDrawerListener(this.drawerToggle);
		
		this.appendListeners();
	}
	
	public void appendListeners() {
		TGDrawerEventListener drawerListener = new TGDrawerEventListener(this);
		TGDrawerActionInterceptor drawerInterceptor = new TGDrawerActionInterceptor(this);
		
		TGActionManager tgActionManager = TGActionManager.getInstance(this.findContext());
		tgActionManager.addPostExecutionListener(drawerListener);
		tgActionManager.addInterceptor(drawerInterceptor);
		
		this.activity.getNavigationManager().addNavigationListener(drawerListener);
	}
	
	public void syncState() {
		this.drawerToggle.syncState();
	}
	
	public void closeDrawer() {
		this.drawerLayout.closeDrawer(this.drawerView);
	}
	
	public void onConfigurationChanged(Configuration configuration) {
		this.drawerToggle.onConfigurationChanged(configuration);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if( this.drawerToggle.isDrawerIndicatorEnabled() ) {
			return this.drawerToggle.onOptionsItemSelected(item);
		}

		return this.activity.getNavigationManager().callOpenPreviousFragment();
	}
	
	public void onOpenFragment(TGFragmentController<?> controller) {
		if( this.drawerView.getChildCount() > 0 ) {
			this.drawerView.removeAllViews();
		}
		if( this.drawerBuilder != null ) {
			this.drawerBuilder.onOpenFragment(controller, this.drawerView);
		}

		boolean available = (this.drawerView.getChildCount() > 0);
		this.drawerToggle.setDrawerIndicatorEnabled(available);
		this.drawerLayout.setDrawerLockMode(available ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}
	
	public void onVisibilityChanged() {
		this.open = this.drawerLayout.isDrawerOpen(this.drawerView);
		
		if( this.open ) {
			this.activity.updateCache(true);
		}
	}

	public void setDrawerBuilder(TGDrawerViewBuilder drawerBuilder) {
		this.drawerBuilder = drawerBuilder;
	}

	public TGContext findContext() {
		return this.activity.findContext();
	}

	public boolean isOpen() {
		return open;
	}
}
