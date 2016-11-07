package org.herac.tuxguitar.android.view.dialog.browser.filesystem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.browser.filesystem.TGFsBrowserSettings;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.dialog.message.TGMessageDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

import java.io.File;

public class TGBrowserSettingsDialog extends TGDialog {

	public TGBrowserSettingsDialog() {
		super();
	}

	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_browser_settings_fs_dialog, null);

		this.fillListView(view);
		this.fillDefaultNameValue(view);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.browser_settings_fs_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, null);
		builder.setNegativeButton(R.string.global_button_cancel, null);
		
		final AlertDialog dialog = builder.create();
		
		dialog.setOnShowListener(new OnShowListener() {
			public void onShow(DialogInterface dlg) {
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if( createSettings(view) ) {
							dialog.dismiss();
						}
					}
				});
			}
		});

		return dialog;
	}

	public void fillListView(final View view) {
		TGBrowserSettingsMountPoint mountPoint = this.getAttribute(TGBrowserSettingsDialogController.ATTRIBUTE_MOUNT_POINT);
		TGBrowserSettingsFolderAdapter tgAdapter = new TGBrowserSettingsFolderAdapter(view.getContext(), mountPoint);
		tgAdapter.setListener(new TGBrowserSettingsFolderAdapterListener() {
			public void onPathChanged(File path) {
				fillPathPreview(view, path != null ? path.getAbsolutePath() : "");
			}
		});
		
		ListView listView = (ListView) view.findViewById(R.id.browser_settings_fs_path_value_selector);
		listView.setAdapter(tgAdapter);
	}

	public void fillDefaultNameValue(View view) {
		TGBrowserSettingsMountPoint mountPoint = this.getAttribute(TGBrowserSettingsDialogController.ATTRIBUTE_MOUNT_POINT);

		this.setTextFieldValue(view, R.id.browser_settings_fs_name_value, mountPoint.getLabel());
	}

	public void setTextFieldValue(View view, int textFieldId, String value) {
		((EditText) view.findViewById(textFieldId)).getText().append(value);
	}

	public void setTextViewValue(View view, int textFieldId, String value) {
		((TextView) view.findViewById(textFieldId)).setText(value);
	}
	
	public String getTextFieldValue(View view, int textFieldId) {
		return ((EditText) view.findViewById(textFieldId)).getText().toString();
	}

	public void fillPathPreview(View view, String value) {
		setTextViewValue(view, R.id.browser_settings_fs_path_preview, value);
	}

	public String getNameValue(View view) {
		return getTextFieldValue(view, R.id.browser_settings_fs_name_value);
	}

	public String getPathValue(View view) {
		ListView listView = (ListView) view.findViewById(R.id.browser_settings_fs_path_value_selector);
		File path = ((TGBrowserSettingsFolderAdapter) listView.getAdapter()).getPath();
		
		return (path != null ? path.getAbsolutePath() : null);
	}

	public boolean createSettings(View view) {
		String name = this.getNameValue(view);
		String path = this.getPathValue(view);
		if (name == null || name.length() == 0) {
			this.showErrorMessage(R.string.browser_settings_fs_error_empty_name);
			return false;
		}
		if (path == null || path.length() == 0) {
			this.showErrorMessage(R.string.browser_settings_fs_error_empty_path);
			return false;
		}

		File file = new File(path);
		if (!file.exists()) {
			this.showErrorMessage(R.string.browser_settings_fs_error_nonexistent_path);
			return false;
		}
		if (!file.isDirectory()) {
			this.showErrorMessage(R.string.browser_settings_fs_error_nonfolder_path);
			return false;
		}

		TGBrowserFactorySettingsHandler tgBrowserFactorySettingsHandler = this.getAttribute(TGBrowserSettingsDialogController.ATTRIBUTE_HANDLER);
		tgBrowserFactorySettingsHandler.onCreateSettings(new TGFsBrowserSettings(name, path).toBrowserSettings());

		return true;
	}

	public void showErrorMessage(int message) {
		this.showErrorMessage(R.string.browser_settings_fs_error_title, message);
	}

	public void showErrorMessage(int title, int message) {
		this.showErrorMessage(getString(title), getString(message));
	}

	public void showErrorMessage(String title, String message) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.findContext(), TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, getActivity());
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, new TGMessageDialogController());
		tgActionProcessor.setAttribute(TGMessageDialogController.ATTRIBUTE_TITLE, title);
		tgActionProcessor.setAttribute(TGMessageDialogController.ATTRIBUTE_MESSAGE, message);
		tgActionProcessor.process();
	}
}
