package org.herac.tuxguitar.android.view.dialog.browser.filesystem;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.browser.filesystem.TGFsBrowserSettings;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.android.view.dialog.message.TGMessageDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

import java.io.File;

public class TGBrowserSettingsDialog extends TGModalFragment {

	public TGBrowserSettingsDialog() {
		super(R.layout.view_browser_settings_fs_dialog);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.browser_settings_fs_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.action_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if( TGBrowserSettingsDialog.this.createSettings() ) {
					TGBrowserSettingsDialog.this.close();
				}
				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.fillListView();
		this.fillDefaultNameValue();
	}

	public void fillListView() {
		TGBrowserSettingsMountPoint mountPoint = this.getAttribute(TGBrowserSettingsDialogController.ATTRIBUTE_MOUNT_POINT);
		TGBrowserSettingsFolderAdapter tgAdapter = new TGBrowserSettingsFolderAdapter(this.getView().getContext(), mountPoint);
		tgAdapter.setListener(new TGBrowserSettingsFolderAdapterListener() {
			public void onPathChanged(File path) {
				fillPathPreview(path != null ? path.getAbsolutePath() : "");
			}
		});
		
		ListView listView = (ListView) this.getView().findViewById(R.id.browser_settings_fs_path_value_selector);
		listView.setAdapter(tgAdapter);
	}

	public void fillDefaultNameValue() {
		TGBrowserSettingsMountPoint mountPoint = this.getAttribute(TGBrowserSettingsDialogController.ATTRIBUTE_MOUNT_POINT);

		this.setTextFieldValue(R.id.browser_settings_fs_name_value, mountPoint.getLabel());
	}

	public void setTextFieldValue(int textFieldId, String value) {
		((EditText) this.getView().findViewById(textFieldId)).getText().append(value);
	}

	public void setTextViewValue(int textFieldId, String value) {
		((TextView) this.getView().findViewById(textFieldId)).setText(value);
	}
	
	public String getTextFieldValue(int textFieldId) {
		return ((EditText) this.getView().findViewById(textFieldId)).getText().toString();
	}

	public void fillPathPreview(String value) {
		setTextViewValue(R.id.browser_settings_fs_path_preview, value);
	}

	public String getNameValue() {
		return getTextFieldValue(R.id.browser_settings_fs_name_value);
	}

	public String getPathValue() {
		ListView listView = (ListView) this.getView().findViewById(R.id.browser_settings_fs_path_value_selector);
		File path = ((TGBrowserSettingsFolderAdapter) listView.getAdapter()).getPath();
		
		return (path != null ? path.getAbsolutePath() : null);
	}

	public boolean createSettings() {
		String name = this.getNameValue();
		String path = this.getPathValue();
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
