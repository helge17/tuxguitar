package org.herac.tuxguitar.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.TGActionAdapterManager;
import org.herac.tuxguitar.android.action.impl.gui.TGBackAction;
import org.herac.tuxguitar.android.action.impl.gui.TGFinishAction;
import org.herac.tuxguitar.android.action.impl.intent.TGProcessIntentAction;
import org.herac.tuxguitar.android.drawer.TGDrawerManager;
import org.herac.tuxguitar.android.error.TGErrorHandlerImpl;
import org.herac.tuxguitar.android.fragment.impl.TGMainFragmentController;
import org.herac.tuxguitar.android.menu.context.TGContextMenuController;
import org.herac.tuxguitar.android.navigation.TGNavigationManager;
import org.herac.tuxguitar.android.properties.TGPropertiesAdapter;
import org.herac.tuxguitar.android.resource.TGResourceLoaderImpl;
import org.herac.tuxguitar.android.synchronizer.TGSynchronizerControllerImpl;
import org.herac.tuxguitar.android.transport.TGTransportAdapter;
import org.herac.tuxguitar.android.variables.TGVarAdapter;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.file.TGLoadTemplateAction;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.util.TGAbstractContext;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGLock;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.plugin.TGPluginManager;

public class TGActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

	private boolean destroyed;
	private TGContext context;
	private TGContextMenuController contextMenu;
	private TGNavigationManager navigationManager;
	private TGDrawerManager drawerManager;
	private TGActivityResultManager resultManager;
	private TGActivityPermissionResultManager permissionResultManager;

	public TGActivity() {
		this.resultManager = new TGActivityResultManager();
		this.permissionResultManager = new TGActivityPermissionResultManager();
		this.navigationManager = new TGNavigationManager(this);
		this.drawerManager = new TGDrawerManager(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.destroyed = false;
		this.clearContext();
		this.attachInstance();
		this.createModules();
		this.setContentView(R.layout.activity_tg);
		
		this.registerForContextMenu(findViewById(R.id.root_layout));
		this.getActionBar().setLogo(R.drawable.ic_launcher);
		this.getActionBar().setIcon(R.drawable.ic_launcher);
		this.getActionBar().setDisplayUseLogoEnabled(true);
		this.getActionBar().setDisplayShowHomeEnabled(true);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setHomeButtonEnabled(true);

		this.resultManager.initialize();
		this.permissionResultManager.initialize();
		this.navigationManager.initialize();
		this.drawerManager.initialize();
		this.loadDefaultFragment();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		this.detachInstance();
		this.clearContext();
		this.destroyed = true;
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		this.connectPlugins();
		this.loadDefaultSong();
		this.drawerManager.syncState();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.clear();
	}

	@Override
	public void onBackPressed() {
		this.callBackAction();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		this.setIntent(intent);
		this.callProcessIntent();
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		this.resultManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		this.permissionResultManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	
	public void attachInstance() {
		TGActivityController.getInstance(findContext()).setActivity(this);
	}
	
	public void detachInstance() {
		TGActivityController.getInstance(findContext()).setActivity(null);
	}
	
	public void createModules() {
		TGContext context = this.findContext();
		TGSynchronizer.getInstance(context).setController(new TGSynchronizerControllerImpl(context));
		TGErrorManager.getInstance(context).addErrorHandler(new TGErrorHandlerImpl(this));
		TGResourceManager.getInstance(context).setResourceLoader(new TGResourceLoaderImpl(this));
		TGActionAdapterManager.getInstance(context).initialize(this);
		TGEditorManager.getInstance(context).setLockControl(new TGLock());
		TGVarAdapter.initialize(context);
		TGPropertiesAdapter.initialize(context, this);
		TGTransportAdapter.getInstance(context).initialize();
	}
	
	public void connectPlugins() {
		TGPluginManager.getInstance(this.context).connectEnabled();
	}
	
	public void disconnectPlugins() {
		TGPluginManager.getInstance(this.context).disconnectAll();
	}
	
	public void destroyEditor() {
		TGEditorManager.getInstance(this.context).destroy(null);
	}
	
	public void destroy() {
		this.disconnectPlugins();
		this.destroyEditor();
		this.callFinishAction();
	}
	
	public void updateCache(boolean updateItems){
		this.updateCache(updateItems, null);
	}
	
	public void updateCache(boolean updateItems, TGAbstractContext sourceContext){
		TGEditorManager editorManager = TGEditorManager.getInstance(this.context);
		if( updateItems ){
			editorManager.updateSelection(sourceContext);
		}
		editorManager.redraw(sourceContext);
	}
	
	public TGDrawerManager getDrawerManager() {
		return drawerManager;
	}

	public TGNavigationManager getNavigationManager() {
		return navigationManager;
	}

	public TGActivityResultManager getResultManager() {
		return resultManager;
	}

	public TGActivityPermissionResultManager getPermissionResultManager() {
		return permissionResultManager;
	}
	
	public TGContext findContext() {
		if( this.context == null ) {
			this.context = new TGContext();
		}
		return context;
	}
	
	public void clearContext() {
		this.findContext().clear();
	}
	
	public void loadDefaultFragment() {
		this.getNavigationManager().callOpenFragment(TGMainFragmentController.getInstance(findContext()));
	}
	
	public void loadDefaultSong() {
		Intent intent = this.getIntent();
		if( intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
			this.callProcessIntent();
		} else {
			this.callLoadDefaultSong();
		}
	}
	
	public void callLoadDefaultSong() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGLoadTemplateAction.NAME);
		tgActionProcessor.process();
	}
	
	public void callProcessIntent() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGProcessIntentAction.NAME);
		tgActionProcessor.setAttribute(TGProcessIntentAction.ATTRIBUTE_ACTIVITY, this);
		tgActionProcessor.process();
	}
	
	public void callBackAction() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGBackAction.NAME);
		tgActionProcessor.setAttribute(TGBackAction.ATTRIBUTE_ACTIVITY, this);
		tgActionProcessor.process();
	}
	
	public void callFinishAction() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGFinishAction.NAME);
		tgActionProcessor.setAttribute(TGFinishAction.ATTRIBUTE_ACTIVITY, this);
		tgActionProcessor.process();
	}

	public boolean isDestroyed() {
		return this.destroyed;
	}
}
