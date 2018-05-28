package org.herac.tuxguitar.android.view.dialog.info;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeInfoAction;
import org.herac.tuxguitar.song.models.TGSong;

public class TGSongInfoDialog extends TGModalFragment {

	public TGSongInfoDialog() {
		super(R.layout.view_song_info);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(true, false, R.string.song_info_dlg_title);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.menu_modal_fragment_ok, menu);
		menu.findItem(R.id.action_ok).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				TGSongInfoDialog.this.updateSongInfo();
				TGSongInfoDialog.this.close();

				return true;
			}
		});
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.fillSongInfo();
	}

	public void setTextFieldValue(int textFieldId, String value) {
		((EditText) this.getView().findViewById(textFieldId)).getText().append(value);
	}
	
	public String getTextFieldValue(int textFieldId) {
		return ((EditText) this.getView().findViewById(textFieldId)).getText().toString();
	}
	
	public void fillSongInfo() {
		TGSong song = this.getSong();
		setTextFieldValue(R.id.song_info_dlg_name_value, song.getName());
		setTextFieldValue(R.id.song_info_dlg_artist_value, song.getArtist());
		setTextFieldValue(R.id.song_info_dlg_album_value, song.getAlbum());
		setTextFieldValue(R.id.song_info_dlg_author_value, song.getAuthor());
		setTextFieldValue(R.id.song_info_dlg_date_value, song.getDate());
		setTextFieldValue(R.id.song_info_dlg_copyright_value, song.getCopyright());
		setTextFieldValue(R.id.song_info_dlg_writer_value, song.getWriter());
		setTextFieldValue(R.id.song_info_dlg_transcriber_value, song.getTranscriber());
		setTextFieldValue(R.id.song_info_dlg_comments_value, song.getComments());
	}
	
	public void updateSongInfo() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeInfoAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.getSong());
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_NAME, getTextFieldValue(R.id.song_info_dlg_name_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_ARTIST, getTextFieldValue(R.id.song_info_dlg_artist_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_ALBUM, getTextFieldValue(R.id.song_info_dlg_album_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_AUTHOR, getTextFieldValue(R.id.song_info_dlg_author_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_DATE, getTextFieldValue(R.id.song_info_dlg_date_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_COPYRIGHT, getTextFieldValue(R.id.song_info_dlg_copyright_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_WRITER, getTextFieldValue(R.id.song_info_dlg_writer_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_TRANSCRIBER, getTextFieldValue(R.id.song_info_dlg_transcriber_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_COMMENTS, getTextFieldValue(R.id.song_info_dlg_comments_value));
		tgActionProcessor.processOnNewThread();
	}

	public TGSong getSong() {
		return getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
	}
}
