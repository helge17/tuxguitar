package org.herac.tuxguitar.android.error;

import android.os.Environment;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.message.TGMessageDialogController;
import org.herac.tuxguitar.util.error.TGErrorHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class TGErrorHandlerImpl implements TGErrorHandler{
	
	private static final String LOG_FILE = (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "TuxGuitar/log/tuxguitar.log");
	
	private static final String MSG_TITLE = "Error";
	private static final String EOL = "\r\n";
	
	private TGActivity activity;
	
	public TGErrorHandlerImpl(TGActivity activity) {
		this.activity = activity;
	}
	
	@Override
	public void handleError(Throwable throwable) {
		throwable.printStackTrace();
		
		this.logError(throwable);
		this.showUserMessage(throwable);
	}
	
	public void showUserMessage(Throwable throwable) {
		TGDialogContext tgDialogContext = new TGDialogContext();
		tgDialogContext.setAttribute(TGMessageDialogController.ATTRIBUTE_TITLE, MSG_TITLE);
		tgDialogContext.setAttribute(TGMessageDialogController.ATTRIBUTE_MESSAGE, this.createHumanErrorMessage(throwable));
		
		TGMessageDialogController tgMessageDialogController = new TGMessageDialogController();
		tgMessageDialogController.showDialog(this.activity, tgDialogContext);
	}
	
	public String createHumanErrorMessage(Throwable throwable) {
		String message = throwable.getMessage();
		if( message == null || message.trim().length() == 0 ) {
			message = this.activity.getString(R.string.global_error_message);
		}
		return message;
	}
	
	public String createFullErrorMessage(Throwable throwable) {
		StringBuffer message = new StringBuffer();
		
		message.append(throwable.getClass().getName());
		message.append(EOL);
		message.append(EOL);
		
		if( throwable.getMessage() != null ) {
			message.append(throwable.getMessage());
			message.append(EOL);
		}
		message.append(getStackTrace(throwable));
		
		return message.toString();
	}
	
	public String getStackTrace(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		
		throwable.printStackTrace(printWriter);
		
		return stringWriter.toString();
	}
	
	public void logError(Throwable throwable) {
		try {
			File logFile = new File(LOG_FILE);
			if(!logFile.exists() ) {
				logFile.getParentFile().mkdirs();
				logFile.createNewFile();
			}

			if( logFile.exists() && logFile.canWrite() ) {
				BufferedWriter buffer = new BufferedWriter(new FileWriter(logFile, true));
				buffer.append(new Date().toString());
				buffer.newLine();
				buffer.append(this.createFullErrorMessage(throwable));
				buffer.newLine();
				buffer.close();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
