package zct.sistemas.leko.constants;

public enum Unidades {

	UNIDADES("un", "UNIDADES"), METROS("m", "METROS"), KILOGRAMAS("Kg", "KILOGRAMAS");

	private final String simbolo;
	private final String descricao;

	Unidades(String simbolo, String descricao) {
		this.simbolo = simbolo;
		this.descricao = descricao;
	}

	public String getSimbolo() {
		return simbolo;
	}

	public String getDescricao() {
		return descricao;
	}

	@Override
	public String toString() {
		return descricao + " - (" + simbolo + ")";
	}
}
