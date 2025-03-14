package app.tuxguitar.cocoa.opendoc;


/* Register a listener to receive OpenDocument events from SWT
 * When an event is caught, need to wait for TuxGuitar to be ready before launching open document action:
 * - wait for all other plugins to be initialized (many file importers are plugins)
 * - at application start, wait for default song to be created, or else 2 tabs are open when double-clicking 1 single file
 * This is done by setting a TGActionInterceptor
 *
 * Assumptions
 * - if TuxGuitar is called from command line with an argument, TuxGuitar.java launches TGReadURLAction action.
 *   in this case, OpenDocument events from SWT are never received when double-clicking a file.
 *   so, don't even try to catch them, in this scenario this plugin is useless (and stops itself)
 * - if TuxGuitar is launched either by Finder (double-click either on app icon or on a file), or from command line without argument,
 *   then TuxGuitar.java launches TGLoadTemplateAction to create default song.
 *   Not possible to know if application was launched from command line or not. Just wait for SWT events.
 */

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionInterceptor;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.file.TGReadURLAction;
import app.tuxguitar.app.document.TGDocumentListAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.file.TGLoadTemplateAction;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;
import app.tuxguitar.util.error.TGErrorManager;
import app.tuxguitar.util.plugin.TGPluginException;

import java.util.ArrayList;
import java.util.List;

public class OpenDocListener implements Listener, TGActionInterceptor {

	private List<String> eventsText;
	private boolean enabled;
	private TGContext context;

	public OpenDocListener(TGContext context) throws TGPluginException {
		// shall be called from UI thread, or registration of listener will fail
		if (Display.getCurrent()==null) {
			throw new TGPluginException();
		}
		this.eventsText = new ArrayList<String>();
		this.enabled = false;
		this.context = context;
		Display.getCurrent().addListener(SWT.OpenDocument, this);
		TGActionManager.getInstance(context).addInterceptor(this);
	}

	public void disconnect() {
		this.enabled = false;
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			// need to executeLater for 2 reasons:
			// - remove swt event listener: shall be done from UI thread
			// - remove action interceptor: cannot be done when an action is currently intercepted and action is continued
			public void run() {
				try {
					Display.getDefault().removeListener(SWT.OpenDocument, OpenDocListener.this);
					TGActionManager.getInstance(OpenDocListener.this.context).removeInterceptor(OpenDocListener.this);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	// intercept action launched when application is started
	@Override
	public boolean intercept(String id, TGActionContext actionContext) throws TGActionException {
		boolean handled = false;
		if (id.equals(TGReadURLAction.NAME)) {
			// TuxGuitar was launched with an argument. SWT events will never be received
			disconnect();
			// and let action be processed normally
		}
		else if (id.equals(TGLoadTemplateAction.NAME)) {
			// nothing more to be intercepted
			TGActionManager.getInstance(OpenDocListener.this.context).removeInterceptor(OpenDocListener.this);
			// TuxGuitar was launched without any argument
			// Wait for action to be fully executed, then enable processing of events
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGLoadTemplateAction.NAME);
			tgActionProcessor.setAttribute(TGDocumentListAttributes.ATTRIBUTE_UNWANTED, true);
			tgActionProcessor.setOnFinish(new Runnable() {
				public void run() {
					OpenDocListener.this.processEvents();
				}
			});
			tgActionProcessor.process();
			handled = true;
		}
		return handled;
	}

	// receive SWT OpenDocument events: store events, and process only if enabled
	@Override
	public void handleEvent(Event event) {
		if (event.text != null) {
			synchronized (this.getClass()) {
				this.eventsText.add(event.text);
			}
		}
		if (this.enabled) {
			this.processEvents();
		}
	}

	// process events: launch open document action(s)
	private void processEvents() {
		this.enabled = true;
		synchronized(this.getClass()) {
			if (!this.eventsText.isEmpty()) {
				TuxGuitar.getInstance().getPlayer().reset();
			}
			while (!this.eventsText.isEmpty()) {
				try {
					TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGReadURLAction.NAME);
					tgActionProcessor.setAttribute(TGReadURLAction.ATTRIBUTE_URL, new File(this.eventsText.get(0)).toURI().toURL());
					tgActionProcessor.process();
				} catch (Throwable throwable) {
					TGErrorManager.getInstance(context).handleError(throwable);
				}
				eventsText.remove(0);
			}
		}
	}

}
