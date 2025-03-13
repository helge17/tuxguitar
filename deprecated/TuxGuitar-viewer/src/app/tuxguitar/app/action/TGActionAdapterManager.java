package app.tuxguitar.app.action;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionContextFactory;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.installer.TGActionInstaller;
import app.tuxguitar.app.action.listener.cache.TGUpdateListener;
import app.tuxguitar.app.action.listener.error.TGActionErrorHandler;
import app.tuxguitar.app.action.listener.lock.TGLockableActionListener;
import app.tuxguitar.app.action.listener.thread.TGSyncThreadInterceptor;
import app.tuxguitar.app.action.listener.transport.TGDisableOnPlayInterceptor;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGActionAdapterManager {

	private TGContext context;
	private TGActionContextFactory actionContextFactory;
	private TGDisableOnPlayInterceptor disableOnPlayInterceptor;
	private List<String> keyBindingActionIds;

	private TGSyncThreadInterceptor syncThreadInterceptor;
	private TGLockableActionListener lockableActionListener;
	private TGUpdateListener updatableActionListener;
	private TGActionErrorHandler errorHandler;

	private TGActionAdapterManager(TGContext context){
		this.context = context;
		this.actionContextFactory = new TGActionContextFactoryImpl();
		this.keyBindingActionIds = new ArrayList<String>();
		this.disableOnPlayInterceptor = new TGDisableOnPlayInterceptor(context);
		this.syncThreadInterceptor = new TGSyncThreadInterceptor(context);
		this.lockableActionListener = new TGLockableActionListener(context);
		this.updatableActionListener = new TGUpdateListener(this);
		this.errorHandler = new TGActionErrorHandler(context);
	}

	public void initialize(){
		this.initializeHandlers();
		this.initializeDefaultActions();
	}

	private void initializeHandlers(){
		TGActionManager tgActionManager = TGActionManager.getInstance(this.context);
		tgActionManager.setActionContextFactory(this.actionContextFactory);
		tgActionManager.addInterceptor(this.disableOnPlayInterceptor);
		tgActionManager.addInterceptor(this.syncThreadInterceptor);
		tgActionManager.addInterceptor(this.lockableActionListener);

		tgActionManager.addPreExecutionListener(this.errorHandler);
		tgActionManager.addPreExecutionListener(this.lockableActionListener);
		tgActionManager.addPreExecutionListener(this.updatableActionListener);

		tgActionManager.addPostExecutionListener(this.updatableActionListener);
		tgActionManager.addPostExecutionListener(this.lockableActionListener);
		tgActionManager.addPostExecutionListener(this.errorHandler);

		tgActionManager.addErrorListener(this.lockableActionListener);
		tgActionManager.addErrorListener(this.errorHandler);
	}

	private void initializeDefaultActions() {
		TGActionInstaller tgActionInstaller = new TGActionInstaller(this);
		tgActionInstaller.installDefaultActions();
	}

	public List<String> getKeyBindingActionIds() {
		return this.keyBindingActionIds;
	}

	public TGDisableOnPlayInterceptor getDisableOnPlayInterceptor() {
		return this.disableOnPlayInterceptor;
	}

	public TGSyncThreadInterceptor getSyncThreadInterceptor() {
		return syncThreadInterceptor;
	}

	public TGLockableActionListener getLockableActionListener() {
		return lockableActionListener;
	}

	public TGUpdateListener getUpdatableActionListener() {
		return updatableActionListener;
	}

	public TGContext getContext() {
		return context;
	}

	public static TGActionAdapterManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGActionAdapterManager.class.getName(), new TGSingletonFactory<TGActionAdapterManager>() {
			public TGActionAdapterManager createInstance(TGContext context) {
				return new TGActionAdapterManager(context);
			}
		});
	}
}
