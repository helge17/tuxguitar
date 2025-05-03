package app.tuxguitar.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.file.TGExitAction;
import app.tuxguitar.app.action.impl.transport.TGTransportPlayPauseAction;
import app.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.ui.swt.resource.SWTImage;
import app.tuxguitar.ui.swt.widget.SWTWindow;
import app.tuxguitar.util.TGContext;

public class TGTrayMenu {

	private TGContext context;
	private Menu menu;
	private MenuItem play;
	private MenuItem stop;
	private MenuItem exit;

	public TGTrayMenu(TGContext context) {
		this.context = context;
	}

	public void make(){
		this.menu = new Menu(((SWTWindow) TGWindow.getInstance(this.context).getWindow()).getControl(), SWT.POP_UP);

		this.play = new MenuItem(this.menu,SWT.PUSH);
		this.play.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new TGActionProcessor(TGTrayMenu.this.context, TGTransportPlayPauseAction.NAME).process();
			}
		});

		this.stop = new MenuItem(this.menu, SWT.PUSH);
		this.stop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new TGActionProcessor(TGTrayMenu.this.context, TGTransportStopAction.NAME).process();
			}
		});

		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);

		this.exit = new MenuItem(this.menu, SWT.PUSH);
		this.exit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new TGActionProcessor(TGTrayMenu.this.context, TGExitAction.NAME).process();
			}
		});
	}

	public void loadProperties(){
		if(this.menu != null && !this.menu.isDisposed()){
			if(TuxGuitar.getInstance().getPlayer().isRunning()){
				this.play.setText(TuxGuitar.getProperty("transport.pause"));
			} else {
				this.play.setText(TuxGuitar.getProperty("transport.start"));
			}
			this.stop.setText(TuxGuitar.getProperty("transport.stop"));
			this.exit.setText(TuxGuitar.getProperty("file.exit"));
		}
	}

	public void loadIcons(){
		if(this.menu != null && !this.menu.isDisposed()){
			this.stop.setImage(((SWTImage) TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRANSPORT_ICON_STOP)).getHandle());
			if(TuxGuitar.getInstance().getPlayer().isRunning()){
				this.play.setImage(((SWTImage) TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRANSPORT_ICON_PAUSE)).getHandle());
			} else {
				this.play.setImage(((SWTImage) TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRANSPORT_ICON_PLAY)).getHandle());
			}
		}
	}

	public void show(){
		if(this.menu != null && !this.menu.isDisposed()){
			this.loadProperties();
			this.loadIcons();
			this.menu.setVisible(true);
		}
	}

	public void dispose(){
		if(this.menu != null && !this.menu.isDisposed()){
			this.menu.dispose();
		}
	}

}
