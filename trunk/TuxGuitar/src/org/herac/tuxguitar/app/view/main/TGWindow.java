package org.herac.tuxguitar.app.view.main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.system.TGDisposeAction;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.view.component.tabfolder.TGTabFolder;
import org.herac.tuxguitar.app.view.component.table.TGTableViewer;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import org.herac.tuxguitar.app.view.toolbar.TGToolBar;
import org.herac.tuxguitar.app.view.util.TGCursorController;
import org.herac.tuxguitar.app.view.util.TGSyncProcess;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGExpressionResolver;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGWindow implements TGEventListener {
	
	public static final int MARGIN_WIDTH = 5;
	
	private TGContext context;
	private TGSyncProcess loadTitleProcess;
	private TGCursorController cursorController;
	
	private Shell shell;
	private Sash sash;
	private Composite sashComposite;
	
	public TGWindow(TGContext context) {
		this.context = context;
		this.createSyncProcesses();
	}
	
	public void open() {
		if( this.shell != null ) {
			this.shell.open();
		}
	}
	
	public void createShell(Display display) {
		this.shell = new Shell(display);
		this.shell.setLayout(createShellLayout());
		this.shell.addShellListener(new TGActionProcessorListener(this.context, TGDisposeAction.NAME));
		
		this.createShellComposites();
		this.createShellListeners();
		this.loadIcons();
	}
	
	private void createShellComposites() {
		TGToolBar tgToolBar = TGToolBar.getInstance(this.context);
		tgToolBar.createToolBar(this.shell);
		
		FormData data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(tgToolBar.getControl(), 0);
		data.bottom = new FormAttachment(100,0);
		this.sashComposite = new Composite(this.shell, SWT.NONE);
		this.sashComposite.setLayout(new FormLayout());
		this.sashComposite.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.bottom = new FormAttachment(100,-150);
		data.height = MARGIN_WIDTH;
		this.sash = new Sash(this.sashComposite, SWT.HORIZONTAL);
		this.sash.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(0,0);
		data.bottom = new FormAttachment(this.sash, 0);
		
		TGTabFolder tgTabFolder = TGTabFolder.getInstance(this.context);
		tgTabFolder.init(this.sashComposite);
		tgTabFolder.getControl().setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(this.sash,0);
		data.bottom = new FormAttachment(100,0);
		
		TGTableViewer tgTableViewer = TGTableViewer.getInstance(this.context);
		tgTableViewer.init(this.sashComposite);
		tgTableViewer.getComposite().setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(this.sashComposite,0);
		data.bottom = new FormAttachment(100,0);
		
		Composite footer = new Composite(this.shell, SWT.NONE);
		footer.setLayout(new FormLayout());
		footer.setLayoutData(data);
		
		TGFretBoardEditor tgFretBoardEditor = TGFretBoardEditor.getInstance(this.context);
		tgFretBoardEditor.showFretBoard(footer);
		
		this.sash.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				TGWindow.this.sashComposite.layout(true,true);
			}
		});
		this.sash.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int maximumHeight = (TGWindow.this.sashComposite.getBounds().height - TGWindow.this.sash.getBounds().height);
				int height = (maximumHeight - event.y);
				height = Math.max(height,0);
				height = Math.min(height,maximumHeight);
				((FormData) TGWindow.this.sash.getLayoutData()).bottom = new FormAttachment(100, -height);
			}
		});
		this.sash.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(MouseEvent e) {
				TGWindow.this.sash.setCursor(TGWindow.this.sash.getDisplay().getSystemCursor( SWT.CURSOR_SIZENS ) );
			}
		});
		this.sashComposite.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event arg0) {
				FormData data = ((FormData) TGWindow.this.sash.getLayoutData());
				int height = -data.bottom.offset;
				int maximumHeight = (TGWindow.this.sashComposite.getBounds().height - TGWindow.this.sash.getBounds().height);
				if(height > maximumHeight){
					data.bottom = new FormAttachment(100, -maximumHeight);
				}
			}
		});
	}
	
	private void createShellListeners() {
		TGIconManager tgIconManager = TGIconManager.getInstance(this.context);
		tgIconManager.addLoader(this);
	}
	
	private FormLayout createShellLayout(){
		FormLayout layout = new FormLayout();
		layout.marginWidth = MARGIN_WIDTH;
		layout.marginHeight = MARGIN_WIDTH;
		return layout;
	}
	
	public void setTableHeight(int value){
		TGTableViewer tgTableViewer = TGTableViewer.getInstance(this.context);
		
		int offset = ((FormData) tgTableViewer.getComposite().getLayoutData()).top.offset;
		int sashHeight = this.sash.getBounds().height;
		int maximumHeight = (this.sashComposite.getBounds().height - sashHeight);
		int height = (value + offset);
		height = Math.max( height,0);
		height = Math.min( height,maximumHeight);
		((FormData) this.sash.getLayoutData()).bottom = new FormAttachment(100, -height);
		
		this.sashComposite.layout(true,true);
	}
	
	public void updateShellFooter(int offset, int minimumWith, int minimumHeight){
		FormData data = ((FormData) this.sashComposite.getLayoutData());
		data.bottom.offset = -offset;
		getShell().setMinimumSize(Math.max(640,minimumWith),Math.max(480,minimumHeight));
		getShell().layout(true,true);
		getShell().redraw();
	}
	
	public void loadDefaultCursor() {
		this.loadCursor(SWT.CURSOR_ARROW);
	}
	
	public void loadBusyCursor() {
		this.loadCursor(SWT.CURSOR_WAIT);
	}
	
	public void loadCursor(int cursorStyle) {
		if(!this.isDisposed()) {
			if( this.cursorController == null || !this.cursorController.isControlling(this.shell) ) {
				this.cursorController = new TGCursorController(this.context, this.shell);
			}
			this.cursorController.loadCursor(cursorStyle);
		}
	}
	
	public void moveToTop() {
		if(!this.isDisposed()) {
			this.shell.setMinimized(false);
			this.shell.forceActive();
		}
	}
	
	public boolean isDisposed() {
		return (this.shell == null || this.shell.isDisposed());
	}
	
	public Shell getShell() {
		return shell;
	}

	public Composite getSashComposite() {
		return sashComposite;
	}

	public void loadTitle() {
		this.loadTitleProcess.process();
	}
	
	public void loadTitleInCurrentThread() {
		if(!this.isDisposed()) {
			String titleLayout = TGConfigManager.getInstance(this.context).getStringValue(TGConfigKeys.WINDOW_TITLE);
			String title = TGExpressionResolver.getInstance(this.context).resolve(titleLayout);
			
			this.getShell().setText(title != null ? title : TuxGuitar.APPLICATION_NAME);
		}
	}
	
	public void loadIcons() {
		if(!this.isDisposed()) {
			this.getShell().setImage(TGIconManager.getInstance(this.context).getAppIcon());
			this.getShell().layout(true);
		}
	}
	
	public void createSyncProcesses() {
		this.loadTitleProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				TGWindow.this.loadTitleInCurrentThread();
			}
		});
	}
	
	public void processEvent(final TGEvent event) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
					loadIcons();
				}
			}
		});
	}
	
	public static TGWindow getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGWindow.class.getName(), new TGSingletonFactory<TGWindow>() {
			public TGWindow createInstance(TGContext context) {
				return new TGWindow(context);
			}
		});
	}
}
