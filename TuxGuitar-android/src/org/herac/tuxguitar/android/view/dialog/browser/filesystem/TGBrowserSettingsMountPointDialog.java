package org.herac.tuxguitar.android.view.dialog.browser.filesystem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.variables.TGVarEnvExternalStorageDirectory;
import org.herac.tuxguitar.android.variables.TGVarEnvSecondaryStorageDirectory;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TGBrowserSettingsMountPointDialog extends TGDialog {

	public TGBrowserSettingsMountPointDialog() {
		super();
	}

	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final List<TGBrowserSettingsMountPoint> mountPoints = this.findDefaultMountPoints();
		List<CharSequence> items = new ArrayList<CharSequence>();
		for(TGBrowserSettingsMountPoint mountPoint : mountPoints) {
			items.add(mountPoint.getLabel());
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.browser_settings_fs_mount_point_dlg_title);
		builder.setItems(items.toArray(new CharSequence[items.size()]) , new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if( which >= 0 && which < mountPoints.size() ) {
					openSettingsDialog(mountPoints.get(which));
				}
			}
		});

		return builder.create();
	}

	public List<TGBrowserSettingsMountPoint> findDefaultMountPoints() {
		List<TGBrowserSettingsMountPoint> children = new ArrayList<TGBrowserSettingsMountPoint>();

		File root = new File("/");
		if( this.isReadableDirectory(root) ) {
			children.add(new TGBrowserSettingsMountPoint(root, this.getActivity().getString(R.string.browser_settings_fs_root_folder_title)));
		}

		String externalStorageDirectory = new TGVarEnvExternalStorageDirectory().toString();
		if( this.isReadableDirectory(externalStorageDirectory) ) {
			children.add(new TGBrowserSettingsMountPoint(new File(externalStorageDirectory), this.getActivity().getString(R.string.browser_settings_fs_external_storage_title)));
		}

		String secondaryStorageDirectory = new TGVarEnvSecondaryStorageDirectory().toString();
		if( this.isReadableDirectory(secondaryStorageDirectory) ) {
			children.add(new TGBrowserSettingsMountPoint(new File(secondaryStorageDirectory), this.getActivity().getString(R.string.browser_settings_fs_secondary_storage_title)));
		}
		return children;
	}

	public boolean isReadableDirectory(String filename) {
		return (filename != null && this.isReadableDirectory(new File(filename)));
	}

	public boolean isReadableDirectory(File file) {
		return (file != null && file.exists() && file.canRead() && file.isDirectory());
	}

	public void openSettingsDialog(TGBrowserSettingsMountPoint mountPoint) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.findContext(), TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, new TGBrowserSettingsDialogController());
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.getActivity());
		tgActionProcessor.setAttribute(TGBrowserSettingsDialogController.ATTRIBUTE_HANDLER, this.getAttribute(TGBrowserSettingsDialogController.ATTRIBUTE_HANDLER));
		tgActionProcessor.setAttribute(TGBrowserSettingsDialogController.ATTRIBUTE_MOUNT_POINT, mountPoint);
		tgActionProcessor.process();
	}
}
