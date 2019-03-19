package zct.sistemas.leko.shared;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import zct.sistemas.leko.model.Item;

public class ItensShared {

	public static SimpleListProperty<Item> itensProperty = new SimpleListProperty<>();

	public static ObservableList<Item> getItens() {
		return itensProperty.get();
	}

	public static void setItens(ObservableList<Item> itens) {
		ItensShared.itensProperty.set(itens);
	}

	public static ListProperty<Item> getListProperty() {
		return itensProperty;
	}

}
