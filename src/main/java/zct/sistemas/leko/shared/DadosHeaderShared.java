package zct.sistemas.leko.shared;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import zct.sistemas.leko.model.DadosHeader;

public class DadosHeaderShared {

	public static SimpleObjectProperty<DadosHeader> dadosHeaderProperty = new SimpleObjectProperty<>();

	public static DadosHeader getDadosHeader() {
		return dadosHeaderProperty.get();
	}

	public static void setDadosHeader(DadosHeader dadosHeader) {
		dadosHeaderProperty.set(dadosHeader);
	}

	public static ObjectProperty<DadosHeader> getDadosHeaderProperty() {
		return dadosHeaderProperty;
	}

}
