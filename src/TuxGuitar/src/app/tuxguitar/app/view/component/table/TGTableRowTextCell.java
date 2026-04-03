package app.tuxguitar.app.view.component.table;

import app.tuxguitar.ui.event.UIMouseDoubleClickListener;
import app.tuxguitar.ui.event.UIMouseDownListener;
import app.tuxguitar.ui.event.UIMouseUpListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.widget.UILabel;

public class TGTableRowTextCell extends TGTableRowCell {

  private UILabel label;

  public TGTableRowTextCell(TGTableRow row) {
    super(row);
    TGTable table = row.getTable();
    this.label = table.getUIFactory().createLabel(getControl());
    getLayout().set(this.label, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
  }

  @Override
  public void setBgColor(UIColor background) {
    super.setBgColor(background);
    this.label.setBgColor(background);
  }

  @Override
  public void setFgColor(UIColor foreground) {
    super.setFgColor(foreground);
    this.label.setFgColor(foreground);
  }

  @Override
  public void setMenu(UIPopupMenu menu) {
    super.setMenu(menu);
    this.label.setPopupMenu(menu);
  }

  @Override
  public void addMouseDownListener(UIMouseDownListener listener) {
    super.addMouseDownListener(listener);
    this.label.addMouseDownListener(listener);
  }

  @Override
  public void addMouseUpListener(UIMouseUpListener listener) {
    super.addMouseUpListener(listener);
    this.label.addMouseUpListener(listener);
  }

  @Override
  public void addMouseDoubleClickListener(UIMouseDoubleClickListener listener) {
    super.addMouseDoubleClickListener(listener);
    this.label.addMouseDoubleClickListener(listener);
  }

  public void setText(String text) {
    this.label.setText(text);
  }

}
