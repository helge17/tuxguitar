package app.tuxguitar.app.view.component.table;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.widgets.TGIconCheckButton;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import app.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.event.*;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.util.TGContext;

public class TGTableRowButtonsCell extends TGTableRowCell {

  private TGIconCheckButton soloButton;
  private TGIconCheckButton muteButton;
  private TGContext context;

  public TGTableRowButtonsCell(final TGTableRow row) {
    super(row);
    this.context = row.getTable().getContext();
    final TGTable table = row.getTable();

    this.soloButton = new TGIconCheckButton(table.getUIFactory(), getControl());
    this.muteButton = new TGIconCheckButton(table.getUIFactory(), getControl());
    table.appendListeners(this.soloButton.getControl());
    table.appendListeners(this.muteButton.getControl());
    this.soloButton.addSelectionListener(createClickListener(TGChangeTrackSoloAction.NAME));
    this.muteButton.addSelectionListener(createClickListener(TGChangeTrackMuteAction.NAME));
    getLayout().set(this.soloButton.getControl(), 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false, 1, 1, null, null, 2f);
    getLayout().set(this.muteButton.getControl(), 1, 2, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false, 1, 1, null, null, 2f);
    loadProperties();
    loadIcons();
  }

  private UISelectionListener createClickListener(final String action) {
    return event -> {
      final TGTable table = getRow().getTable();
      final Tablature tablature = table.getViewer().getEditor().getTablature();
      final TGTrack track = tablature.getSongManager().getTrack(tablature.getSong(), table.getRowIndex(getRow()) + 1);

      TGActionProcessor processor = new TGActionProcessor(context, action);
      processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
      processor.process();
    };
  }

  @Override
  public void setBgColor(UIColor background) {
    super.setBgColor(background);
    this.soloButton.getControl().setBgColor(background);
    this.muteButton.getControl().setBgColor(background);
  }

  @Override
  public void setFgColor(UIColor foreground) {
    super.setFgColor(foreground);
    this.soloButton.getControl().setFgColor(foreground);
    this.muteButton.getControl().setFgColor(foreground);
  }

  @Override
  public void setMenu(UIPopupMenu menu) {
    super.setMenu(menu);
    this.soloButton.getControl().setPopupMenu(menu);
    this.muteButton.getControl().setPopupMenu(menu);
  }

  @Override
  public void addMouseDownListener(UIMouseDownListener listener) {
    super.addMouseDownListener(listener);
    this.soloButton.getControl().addMouseDownListener(listener);
    this.muteButton.getControl().addMouseDownListener(listener);
  }

  @Override
  public void addMouseUpListener(UIMouseUpListener listener) {
    super.addMouseUpListener(listener);
    this.soloButton.getControl().addMouseUpListener(listener);
    this.muteButton.getControl().addMouseUpListener(listener);
  }

  public void setSolo(boolean solo) {
    this.soloButton.setSelected(solo);
  }

  public void setMute(boolean mute) {
    this.muteButton.setSelected(mute);
  }

  private void loadProperties() {
    this.soloButton.getControl().setToolTipText(TuxGuitar.getProperty("track.solo"));
    this.muteButton.getControl().setToolTipText(TuxGuitar.getProperty("track.mute"));
  }

  public void loadIcons() {
    TGIconManager iconManager = TGIconManager.getInstance(context);
    this.soloButton.setIcon(iconManager.getSoloDisabledDim());
    this.soloButton.setSelectedIcon(iconManager.getSoloDim());
    this.soloButton.setHoveredIcon(iconManager.getSoloDisabled());
    this.soloButton.setSelectedHoveredIcon(iconManager.getSolo());
    this.muteButton.setIcon(iconManager.getMuteDisabledDim());
    this.muteButton.setSelectedIcon(iconManager.getMuteDim());
    this.muteButton.setHoveredIcon(iconManager.getMuteDisabled());
    this.muteButton.setSelectedHoveredIcon(iconManager.getMute());
  }
}
