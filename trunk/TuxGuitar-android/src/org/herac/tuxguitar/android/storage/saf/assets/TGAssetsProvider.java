package org.herac.tuxguitar.android.storage.saf.assets;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.util.TGStreamUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@SuppressLint("NewApi")
public class TGAssetsProvider extends DocumentsProvider {

	private static final String ROOT_ID = "demo-songs";

	private static final String[] DEFAULT_ROOT_PROJECTION = new String[] {
			DocumentsContract.Root.COLUMN_ROOT_ID,
			DocumentsContract.Root.COLUMN_FLAGS,
			DocumentsContract.Root.COLUMN_TITLE,
			DocumentsContract.Root.COLUMN_DOCUMENT_ID
	};

	private static final String[] DEFAULT_DOCUMENT_PROJECTION = new String[] {
			DocumentsContract.Document.COLUMN_DOCUMENT_ID,
			DocumentsContract.Document.COLUMN_MIME_TYPE,
			DocumentsContract.Document.COLUMN_DISPLAY_NAME
	};

	private AssetManager assets;

	public TGAssetsProvider() {
		super();
	}

	@Override
	public Cursor queryRoots(String[] projection) throws FileNotFoundException {
		MatrixCursor result = new MatrixCursor(this.resolveProjection(projection, DEFAULT_ROOT_PROJECTION));
		MatrixCursor.RowBuilder row = result.newRow();

		row.add(DocumentsContract.Root.COLUMN_ROOT_ID, ROOT_ID);
		row.add(DocumentsContract.Root.COLUMN_SUMMARY, getContext().getString(R.string.storage_saf_assets_provider_title));
		row.add(DocumentsContract.Root.COLUMN_FLAGS, DocumentsContract.Root.FLAG_SUPPORTS_RECENTS | DocumentsContract.Root.FLAG_SUPPORTS_SEARCH);
		row.add(DocumentsContract.Root.COLUMN_TITLE, getContext().getString(R.string.storage_saf_assets_provider_title));
		row.add(DocumentsContract.Root.COLUMN_DOCUMENT_ID, ROOT_ID);

		return result;
	}

	@Override
	public Cursor queryChildDocuments(String parentDocumentId, String[] projection, String sortOrder) throws FileNotFoundException {
		try {
			MatrixCursor result = new MatrixCursor(this.resolveProjection(projection, DEFAULT_DOCUMENT_PROJECTION));
			if( this.assets != null ) {
				String[] assets = this.assets.list(parentDocumentId);
				if (assets != null) {
					for(String asset : assets) {
						this.createFileRow(result, parentDocumentId, asset);
					}
				}
			}

			return result;
		} catch(IOException e) {
			e.printStackTrace();

			throw new FileNotFoundException();
		}
	}

	@Override
	public Cursor queryDocument(String documentId, String[] projection) throws FileNotFoundException {
		MatrixCursor result = new MatrixCursor(this.resolveProjection(projection, DEFAULT_DOCUMENT_PROJECTION));
		this.createFileRow(result, documentId);
		return result;
	}

	@Override
	public ParcelFileDescriptor openDocument(String documentId, String mode, CancellationSignal cancellationSignal) throws FileNotFoundException {
		try {
			ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createPipe();
			if( this.assets != null ) {
				TGStreamUtil.write(this.assets.open(documentId), new ParcelFileDescriptor.AutoCloseOutputStream(pipe[1]));
			}
			return pipe[0];
		} catch(IOException e) {
			e.printStackTrace();

			throw new FileNotFoundException();
		}
	}

	@Override
	public boolean onCreate() {
		this.assets = this.getContext().getAssets();

		return (this.assets != null);
	}

	public String[] resolveProjection(String[] projection, String[] defaults) {
		return (projection != null ? projection : defaults);
	}

	public void createFileRow(MatrixCursor result, String parent, String asset) {
		this.createFileRow(result, (parent + File.separator + asset));
	}

	public void createFileRow(MatrixCursor result, String documentId) {
		MatrixCursor.RowBuilder row = result.newRow();

		row.add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, documentId);
		row.add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, this.getDocumentName(documentId));
		row.add(DocumentsContract.Document.COLUMN_MIME_TYPE, this.getMimeType(documentId));
	}

	public String getDocumentName(String documentId) {
		String paths[] = documentId.split(File.separator);
		if( paths != null && paths.length > 0 ) {
			return paths[paths.length - 1];
		}
		return documentId;
	}

	public String getMimeType(String documentId) {
		return (this.isDirectory(documentId) ? DocumentsContract.Document.MIME_TYPE_DIR : "*/*");
	}

	public boolean isDirectory(String documentId) {
		return (this.getDocumentName(documentId).indexOf(".") == -1);
	}
}
