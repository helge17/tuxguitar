package app.tuxguitar.app.view.dialog.browser.main;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.document.TGDocumentListAttributes;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.system.icons.TGSkinEvent;
import app.tuxguitar.app.system.icons.TGSkinManager;
import app.tuxguitar.app.system.language.TGLanguageEvent;
import app.tuxguitar.app.tools.browser.TGBrowserCollection;
import app.tuxguitar.app.tools.browser.TGBrowserConnection;
import app.tuxguitar.app.tools.browser.TGBrowserConnectionHandler;
import app.tuxguitar.app.tools.browser.TGBrowserFactoryListener;
import app.tuxguitar.app.tools.browser.TGBrowserManager;
import app.tuxguitar.app.tools.browser.base.TGBrowser;
import app.tuxguitar.app.tools.browser.base.TGBrowserCallBack;
import app.tuxguitar.app.tools.browser.base.TGBrowserElement;
import app.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import app.tuxguitar.app.tools.browser.base.TGBrowserFactoryHandler;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.util.TGMessageDialogUtil;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.util.TGCursorController;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.file.TGReadSongAction;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.io.base.TGFileFormatUtils;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIMouseDoubleClickListener;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UICursor;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.widget.UIContainer;
import app.tuxguitar.ui.widget.UITable;
import app.tuxguitar.ui.widget.UITableItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGException;
import app.tuxguitar.util.TGSynchronizer;
import app.tuxguitar.util.error.TGErrorHandler;
import app.tuxguitar.util.error.TGErrorManager;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGBrowserDialog implements TGBrowserFactoryListener, TGBrowserConnectionHandler,TGEventListener{

	private static final int SHELL_WIDTH = 500;
	private static final int SHELL_HEIGHT = 350;

	private TGContext context;
	private TGBrowserCollection collection;
	private TGBrowserConnection connection;
	private UIWindow dialog;
	private UITable<TGBrowserElement> table;
	private List<TGBrowserElement> elements;
	private TGBrowserMenuBar menu;
	private TGBrowserToolBar toolBar;
	private TGCursorController cursorController;

	public TGBrowserDialog(TGContext context){
		this.context = context;
		this.connection = new TGBrowserConnection(context, this);
		this.menu = new TGBrowserMenuBar(this);
		this.toolBar = new TGBrowserToolBar(this);
	}

	public TGContext getContext() {
		return context;
	}

	public TGBrowserConnection getConnection(){
		return this.connection;
	}

	public TGBrowserCollection getCollection() {
		return this.collection;
	}

	public void setCollection(TGBrowserCollection collection) {
		this.collection = collection;
	}

	public UIWindow getWindow(){
		return this.dialog;
	}

	public void exit(){
		this.closeCollection(true);

		TGBrowserManager.getInstance(this.context).writeCollections();
		TGSkinManager.getInstance(this.context).removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
	}

	public void show() {
		this.collection = null;
		this.elements = null;

		this.dialog = getUIFactory().createWindow(TGWindow.getInstance(this.context).getWindow(), false, true);
		this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());

		this.menu.createMenuBar(getWindow());
		this.toolBar.createToolBar(getWindow());
		this.initTable(this.dialog);
		this.createLayout();

		this.updateCollections(null);
		this.updateTable();

		this.dialog.setBounds(new UIRectangle(0, 0, SHELL_WIDTH,SHELL_HEIGHT));
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				exit();
			}
		});

		this.loadProperties();
		this.updateBars();

		TGBrowserManager.getInstance(this.context).setFactoryHandler(this);
		TGSkinManager.getInstance(this.context).addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_LAYOUT);
	}

	public void createLayout() {
		UITableLayout uiLayout = new UITableLayout();
		uiLayout.set(this.toolBar.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false, 1, 1, null, null, 0f);
		uiLayout.set(this.table, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);
		uiLayout.set(this.table, UITableLayout.PACKED_WIDTH, 10f);
		uiLayout.set(this.table, UITableLayout.PACKED_HEIGHT, 10f);

		this.dialog.setLayout(uiLayout);
	}

	public void initTable(UIContainer parent){
		this.table = getUIFactory().createTable(parent, false);
		this.table.setColumns(1);
		this.table.addMouseDoubleClickListener(new UIMouseDoubleClickListener() {
			public void onMouseDoubleClick(UIMouseEvent event) {
				openElement();
			}
		});
	}

	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed());
	}

	public void dispose(){
		if(!isDisposed()){
			this.dialog.dispose();
		}
	}

	public void updateTable(){
		if(!isDisposed()){
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() {
					if(!isDisposed()){
						TGBrowserDialog.this.table.removeItems();
						if( TGBrowserDialog.this.elements != null ){
							Iterator<TGBrowserElement> it = TGBrowserDialog.this.elements.iterator();
							while(it.hasNext()){
								TGBrowserElement element = it.next();
								TGIconManager iconManager = TGIconManager.getInstance(TGBrowserDialog.this.context);

								UITableItem<TGBrowserElement> item = new UITableItem<TGBrowserElement>(element);
								item.setImage(element.isFolder() ? iconManager.getBrowserFolder(): iconManager.getBrowserFile());
								item.setText(0, element.getName());
								TGBrowserDialog.this.table.addItem(item);
							}
						}
					}
				}
			});
		}
	}

	public void updateBars(){
		if(!isDisposed()){
			this.menu.updateItems();
			this.toolBar.updateItems();
		}
	}

	public void updateBarsLater(){
		if(!this.isDisposed()){
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() {
					TGBrowserDialog.this.updateBars();
				}
			});
		}
	}

	public void updateCollections(final TGBrowserCollection selection){
		if(!isDisposed()){
			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() {
					if(!isDisposed()){
						TGBrowserDialog.this.menu.updateCollections(selection);
						TGBrowserDialog.this.toolBar.updateCollections(selection);
						TGBrowserDialog.this.dialog.layout();
					}
				}
			});
		}
	}

	public void removeElements(){
		this.elements = null;
	}

	public void addElements(List<TGBrowserElement> elements){
		this.elements = elements;
	}

	public void removeCollection(TGBrowserCollection collection){
		if(collection != null){
			TGBrowserManager.getInstance(this.context).removeCollection(collection);
			if( getCollection() != null && getCollection().equals(collection)){
				this.closeCollection();
			}else{
				this.updateCollections( getCollection() );
			}
		}
	}

	public void openCollection(){
		if(!isDisposed() && getCollection() != null){
			TGBrowserFactory tgBrowserFactory = TGBrowserManager.getInstance(this.context).getFactory(getCollection().getType());
			if( tgBrowserFactory != null ) {
				tgBrowserFactory.createBrowser(new TGBrowserFactoryHandler() {
					public void onCreateBrowser(TGBrowser browser) {
						getConnection().open(new TGAbstractBrowserCallBack<Object>() {
							public void onSuccess(Object data) {
								onOpenCollection();
							}
						}, browser);
					}

					public void handleError(Throwable throwable) {
						TGBrowserDialog.this.notifyError(throwable);
					}
				}, getCollection().getData());
			} else {
				this.closeCollection();
			}
		}
	}

	public void closeCollection() {
		this.closeCollection(false);
	}

	public void closeCollection(boolean force) {
		if((!this.isDisposed() || force) && getCollection() != null) {
			this.getConnection().close(new TGAbstractBrowserCallBack<Object>() {
				public void onSuccess(Object data) {
					onCloseCollection();
				}
			}, force);
		}
	}

	public void cdRoot() {
		if(!this.isDisposed()) {
			this.getConnection().cdRoot(new TGAbstractBrowserCallBack<Object>() {
				public void onSuccess(Object data) {
					onCd();
				}
			});
		}
	}

	public void cdUp() {
		if(!this.isDisposed()) {
			this.getConnection().cdUp(new TGAbstractBrowserCallBack<Object>() {
				public void onSuccess(Object data) {
					onCd();
				}
			});
		}
	}

	public void cdElement(final TGBrowserElement element) {
		if(!this.isDisposed()) {
			this.getConnection().cdElement(new TGAbstractBrowserCallBack<Object>() {
				public void onSuccess(Object data) {
					onCd();
				}
			}, element);
		}
	}

	public void listElements() {
		if(!this.isDisposed()) {
			this.getConnection().listElements(new TGAbstractBrowserCallBack<List<TGBrowserElement>>() {
				public void onSuccess(List<TGBrowserElement> elements) {
					notifyElements(elements);
				}
			});
		}
	}

	public void openStream(final TGBrowserElement element) {
		if(!this.isDisposed()) {
			this.getConnection().openStream(new TGAbstractBrowserCallBack<InputStream>() {
				public void onSuccess(InputStream stream) {
					onOpenStream(stream, element);
				}
			}, element);
		}
	}

	public void openElement() {
		if(!this.isDisposed() && this.getConnection().isOpen()) {
			TGBrowserElement element = this.table.getSelectedValue();
			if( element != null ) {
				if( element.isFolder() ) {
					this.cdElement(element);
				} else {
					this.openStream(element);
				}
			}
		}
	}

	public void onOpenCollection() {
		if(!this.isDisposed()) {
			this.removeElements();
			this.updateTable();
			this.updateCollections(getCollection());
			this.listElements();
		}
	}

	public void onCloseCollection() {
		if(!this.isDisposed()) {
			this.setCollection(null);
			this.removeElements();
			this.updateCollections(getCollection());
			this.updateTable();
		}
	}

	public void onCd() {
		if(!this.isDisposed()) {
			this.listElements();
		}
	}

	public void notifyElements(List<TGBrowserElement> elements) {
		if(!this.isDisposed()) {
			this.addElements(elements);
			this.updateTable();
		}
	}

	public void onOpenStream(final InputStream stream, final TGBrowserElement element) {
		loadCursor(UICursor.WAIT);

		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGReadSongAction.NAME);
		tgActionProcessor.setAttribute(TGReadSongAction.ATTRIBUTE_INPUT_STREAM, stream);
		tgActionProcessor.setAttribute(TGReadSongAction.ATTRIBUTE_FORMAT_CODE, TGFileFormatUtils.getFileFormatCode(element.getName()));
		tgActionProcessor.setAttribute(TGDocumentListAttributes.ATTRIBUTE_DOCUMENT_URI, this.createElementURI(element));
		tgActionProcessor.setOnFinish(new Runnable() {
			public void run() {
				loadCursor(UICursor.NORMAL);
			}
		});
		tgActionProcessor.setAttribute(TGErrorHandler.class.getName(), new TGErrorHandler() {
			public void handleError(Throwable throwable) {
				loadCursor(UICursor.NORMAL);

				TGMessageDialogUtil.errorMessage(getContext(), getWindow(), TuxGuitar.getProperty("file.open.error", new String[]{element.getName()}));
			}
		});
		tgActionProcessor.process();
	}

	public URI createElementURI(TGBrowserElement element) {
		try {
			return new URI("browser:///" + URLEncoder.encode(element.getName(), "UTF-8"));
		} catch (URISyntaxException e) {
			throw new TGException(e);
		} catch (UnsupportedEncodingException e) {
			throw new TGException(e);
		}
	}

	public void loadIcons() {
		if(!isDisposed()){
			this.getWindow().setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
			this.reload();
		}
	}

	public void loadProperties() {
		if(!isDisposed()){
			this.dialog.setText(TuxGuitar.getProperty("browser.dialog"));
			this.menu.loadProperties();
			this.toolBar.loadProperties();
		}
	}

	public void loadCursor(UICursor cursor) {
		if(!this.isDisposed()) {
			if( this.cursorController == null || !this.cursorController.isControlling(this.dialog) ) {
				this.cursorController = new TGCursorController(this.context, this.dialog);
			}
			this.cursorController.loadCursor(cursor);
		}
	}

	public void notifyAdded() {
		if(!this.isDisposed()){
			this.reload();
		}
	}

	public void notifyRemoved() {
		if(!this.isDisposed()){
			if( this.getCollection() != null){
				this.closeCollection();
			}
			this.reload();
		}
	}

	public void notifyLockStatusChanged() {
		if(!this.isDisposed()){
			this.updateBarsLater();
			this.loadCursor(getConnection().isLocked() ? UICursor.WAIT : UICursor.NORMAL);
		}
	}

	public void notifyError(Throwable throwable){
		if(!this.isDisposed()){
			this.updateTable();

			TGErrorManager.getInstance(this.context).handleError(throwable);
		}
	}

	public void reload(){
		if(!isDisposed()){
			this.menu.reload(getWindow());
			this.toolBar.reload();
			this.updateTable();
			this.updateCollections(getCollection());
			this.createLayout();
			this.getWindow().layout();
		}
	}

	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}

	public void processEvent(TGEvent event) {
		if( TGSkinEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIcons();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadProperties();
		}
	}

	public static TGBrowserDialog getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGBrowserDialog.class.getName(), new TGSingletonFactory<TGBrowserDialog>() {
			public TGBrowserDialog createInstance(TGContext context) {
				return new TGBrowserDialog(context);
			}
		});
	}

	private abstract class TGAbstractBrowserCallBack<T> implements TGBrowserCallBack<T> {

		public void handleError(Throwable throwable) {
			TGBrowserDialog.this.notifyError(throwable);
		}
	}
}
