package org.herac.tuxguitar.ui.jfx.widget;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import org.herac.tuxguitar.ui.jfx.resource.JFXSnapshotImage;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.widget.UITableItem;

public class JFXTableCellFactory<T> implements Callback<TableColumn<UITableItem<T>, JFXTableCellValue<T>>, TableCell<UITableItem<T>, JFXTableCellValue<T>>> {
	
	private JFXTable<T> control;
	private boolean checkable;
	
	public JFXTableCellFactory(JFXTable<T> control, boolean checkable) {
		this.control = control;
		this.checkable = checkable;
	}
	
	public TableCell<UITableItem<T>, JFXTableCellValue<T>> call(TableColumn<UITableItem<T>, JFXTableCellValue<T>> param) {
		return new TableCell<UITableItem<T>, JFXTableCellValue<T>>() {
			@Override
			public void updateItem(final JFXTableCellValue<T> item, boolean empty) {
				super.updateItem(item, empty);
				
				String text = null;
				Node graphic = null;
				
				if (item != null && !empty) {
					text = item.getText();
					graphic = item.getData(Node.class.getName());
					
					if( graphic == null ) {
						if( JFXTableCellFactory.this.checkable && item.isFirstColumn()) {
							CheckBox checkBox = new CheckBox();
							checkBox.setAlignment(Pos.CENTER_LEFT);
							checkBox.selectedProperty().bindBidirectional(item.checkedProperty());
							checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
								public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
									JFXTableCellFactory.this.control.onCellChecked(item);
								}
							});
							
							graphic = checkBox;
						} else {
							UIImage image = item.getImage();
							if( image != null ) {
								graphic = new ImageView(((JFXSnapshotImage) image).getHandle());
							}
						}
						item.setData(Node.class.getName(), graphic);
					}
				}
				this.setText(text);
				this.setAlignment(Pos.CENTER_LEFT);
				this.setGraphic(graphic);
			}
		};
	}
}
