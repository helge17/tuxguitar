package org.herac.tuxguitar.android.view.browser;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCdElementAction;
import org.herac.tuxguitar.android.browser.TGBrowserManager;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.io.base.TGFileFormatUtils;
import org.herac.tuxguitar.util.error.TGErrorManager;

public class TGBrowserItemListener implements OnItemClickListener {

	private TGBrowserView browserView;
	
	public TGBrowserItemListener(TGBrowserView browserView) {
		this.browserView = browserView;
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		this.processElementAction((TGBrowserElement) view.getTag());
	}

	public void processElementAction(TGBrowserElement element) {
		try {
			if (element.isFolder()) {
				this.processCdElementAction(element);
			}
			else {
				TGBrowserSession browserSession = TGBrowserManager.getInstance(this.browserView.findContext()).getSession();
				if( browserSession.getSessionType() == TGBrowserSession.READ_MODE) {
					this.processOpenElementAction(element);
				}
				if( browserSession.getSessionType() == TGBrowserSession.WRITE_MODE && element.isWritable()) {
					this.processSaveElementAction(element);
				}
			}
		} catch (TGBrowserException e) {
			TGErrorManager.getInstance(this.browserView.findContext()).handleError(e);
		}
	}
	
	public void processCdElementAction(TGBrowserElement element) {
		this.browserView.getActionHandler().createBrowserElementAction(TGBrowserCdElementAction.NAME, element).process();
	}

	public void processOpenElementAction(TGBrowserElement element) {
		String formatCode = TGFileFormatUtils.getFileFormatCode(element.getName());

		this.browserView.getActionHandler().createBrowserOpenElementAction(element, formatCode).process();
	}
	
	public void processSaveElementAction(final TGBrowserElement element) throws TGBrowserException {
		String confirmMessage = this.browserView.findActivity().getString(R.string.browser_file_overwrite_question);
		String formatCode = TGFileFormatUtils.getFileFormatCode(element.getName());
		TGActionProcessor actionProcessor = this.browserView.getActionHandler().createBrowserSaveElementAction(element, formatCode);
		
		this.browserView.getActionHandler().processConfirmableAction(actionProcessor, confirmMessage);
	}
}
