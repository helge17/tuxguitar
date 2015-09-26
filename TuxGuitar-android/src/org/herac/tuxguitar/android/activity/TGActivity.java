package org.herac.tuxguitar.android.activity;

import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.action.impl.gui.TGBackAction;
import org.herac.tuxguitar.android.application.TGApplication;
import org.herac.tuxguitar.android.drawer.TGDrawerManager;
import org.herac.tuxguitar.android.fragment.TGMainFragment;
import org.herac.tuxguitar.android.menu.context.TGContextMenuController;
import org.herac.tuxguitar.android.navigation.TGNavigationManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;

public class TGActivity extends ActionBarActivity {

	private TGContextMenuController contextMenu;
	private TGNavigationManager navigationManager;
	private TGDrawerManager drawerManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.initializeTuxGuitar();
		this.setContentView(R.layout.activity_tg);
		
		this.registerForContextMenu(findViewById(R.id.root_layout));
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(true);
        
        this.navigationManager = new TGNavigationManager(this);
        this.drawerManager = new TGDrawerManager(this);
        this.loadDefaultFragment();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		this.destroyTuxGuitar();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		this.connectPlugins();
		this.drawerManager.syncState();
	}
	
	@Override
	public void onBackPressed() {
		this.callBackAction();
	}
	
	@Override
	public void onConfigurationChanged(Configuration configuration) {
		super.onConfigurationChanged(configuration);
		
		this.drawerManager.onConfigurationChanged(configuration);
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if( this.drawerManager.onOptionsItemSelected(item) ) {
            return true;
        }
    	
        return super.onOptionsItemSelected(item);
    }
    
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (this.contextMenu != null) {
			this.contextMenu.inflate(menu, getMenuInflater());
		}
	}

	public void openContextMenu(TGContextMenuController contextMenu) {
		this.contextMenu = contextMenu;
		this.openContextMenu(this.findViewById(R.id.root_layout));
	}
	
	public void initializeTuxGuitar() {
		TuxGuitar.getInstance(findContext()).initialize(this);
	}
	
	public void destroyTuxGuitar() {
		TuxGuitar.getInstance(findContext()).destroy();
	}
	
	public void connectPlugins() {
		TuxGuitar.getInstance(findContext()).connectPlugins();
	}
	
	public TGDrawerManager getDrawerManager() {
		return drawerManager;
	}

	public TGNavigationManager getNavigationManager() {
		return navigationManager;
	}

	public TGContext findContext() {
		return ((TGApplication) getApplication()).getContext();
	}
	
	public void loadDefaultFragment() {
		this.getNavigationManager().callOpenFragment(TGMainFragment.getInstance(findContext()));
	}
	
	public void callBackAction() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGBackAction.NAME);
		tgActionProcessor.setAttribute(TGBackAction.ATTRIBUTE_ACTIVITY, this);
		tgActionProcessor.process();
	}
}
