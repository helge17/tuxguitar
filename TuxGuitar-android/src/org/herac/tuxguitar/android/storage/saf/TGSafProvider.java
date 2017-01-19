package org.herac.tuxguitar.android.storage.saf;

import android.content.Intent;
import android.net.Uri;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.android.storage.TGStorageProvider;
import org.herac.tuxguitar.android.view.dialog.chooser.TGChooserDialogHandler;
import org.herac.tuxguitar.android.view.dialog.chooser.TGChooserDialogOption;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGFileFormatUtils;
import org.herac.tuxguitar.util.TGAbstractContext;
import org.herac.tuxguitar.util.TGContext;

import java.util.ArrayList;
import java.util.List;

public class TGSafProvider implements TGStorageProvider {

	private static final String MIME_TYPE = "*/*";
	private static final String EXTRA_SHOW_ADVANCED = "android.content.extra.SHOW_ADVANCED";

	private TGContext context;
	private TGSafSession session;
	private TGSafActionHandler actionHandler;

	public TGSafProvider(TGContext context) {
		this.context = context;
		this.session = new TGSafSession();
		this.actionHandler = new TGSafActionHandler(this.context);
	}

	public void updateSession(TGAbstractContext source) {
		this.session.setUri((Uri) source.getAttribute(Uri.class.getName()));
		this.session.setFileFormat((TGFileFormat) source.getAttribute(TGFileFormat.class.getName()));
	}

	public void openDocument() {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType(MIME_TYPE);
		intent.putExtra(EXTRA_SHOW_ADVANCED, true);

		this.getActionHandler().callStartActivityForResult(intent, new TGSafOpenHandler(this));
	}

	public void saveDocumentAs(TGFileFormat fileFormat) {
		Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType(MIME_TYPE);
		intent.putExtra(EXTRA_SHOW_ADVANCED, true);
		intent.putExtra(Intent.EXTRA_TITLE, this.createDefaultFileName(fileFormat));

		this.getActionHandler().callStartActivityForResult(intent, new TGSafSaveHandler(this, fileFormat));
	}

	public void saveDocumentAs() {
		TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(this.context);
		List<TGChooserDialogOption<TGFileFormat>> options = new ArrayList<TGChooserDialogOption<TGFileFormat>>();

		List<TGFileFormat> commonFormats = fileFormatManager.findWriteFileFormats(true);
		for(TGFileFormat format : commonFormats) {
			options.add(new TGChooserDialogOption<TGFileFormat>(format.getName(), format));
		}

		List<TGFileFormat> nonCommonFormats = fileFormatManager.findWriteFileFormats(false);
		for(TGFileFormat format : nonCommonFormats) {
			options.add(new TGChooserDialogOption<TGFileFormat>(this.getActivity().getString(R.string.storage_export_to, format.getName()), format));
		}

		if( options.size() == 1 ) {
			this.saveDocumentAs(options.get(0).getValue());
		}
		else {
			String title = this.getActivity().getString(R.string.storage_saf_file_format_chooser_title);
			this.getActionHandler().callChooserDialog(title, options, new TGChooserDialogHandler<TGFileFormat>() {
				public void onChoose(TGFileFormat value) {
					if( value != null ) {
						saveDocumentAs(value);
					}
				}
			});
		}
	}

	public void saveDocument() {
		if( this.getSession().getUri() != null && this.getSession().getFileFormat() != null ) {
			this.getActionHandler().callWriteUri(this.getSession().getUri(), this.getSession().getFileFormat());
		} else {
			this.saveDocumentAs();
		}
	}

	public String createDefaultFileName(TGFileFormat format) {
		String prefix = this.getActivity().getString(R.string.storage_default_filename);
		String suffix = TGFileFormatUtils.getDefaultExtension(format);

		return (prefix + suffix);
	}

	public TGActivity getActivity() {
		return TGActivityController.getInstance(this.context).getActivity();
	}

	public TGSafActionHandler getActionHandler() {
		return this.actionHandler;
	}

	public TGSafSession getSession() {
		return this.session;
	}

	public TGContext getContext() {
		return this.context;
	}
}
