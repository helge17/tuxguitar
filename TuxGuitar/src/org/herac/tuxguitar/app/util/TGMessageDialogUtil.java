package org.herac.tuxguitar.app.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.dialog.message.TGMessageDialog;
import org.herac.tuxguitar.app.view.dialog.message.TGMessageDialogController;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

public class TGMessageDialogUtil {
	
	public static void infoMessage(TGContext context, String title, String message){
		showMessage(context, TGWindow.getInstance(context).getShell(), title, message, SWT.ICON_INFORMATION);
	}
	
	public static void infoMessage(TGContext context, Shell shell, String title, String message){
		showMessage(context, shell, title, message, SWT.ICON_INFORMATION);
	}
	
	public static void errorMessage(TGContext context, Shell shell, String message){
		showMessage(context, shell, TuxGuitar.getProperty("error"), message, SWT.ICON_ERROR);
	}
	
	public static void showMessage(TGContext context, Shell shell, String title, String message, int style) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGMessageDialogController());
		tgActionProcessor.setAttribute(TGViewContext.ATTRIBUTE_PARENT, shell);
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_STYLE, style);
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_TITLE, title);
		tgActionProcessor.setAttribute(TGMessageDialog.ATTRIBUTE_MESSAGE, message);
		tgActionProcessor.process();
	}
}
