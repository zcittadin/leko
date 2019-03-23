package zct.sistemas.leko.constants;

public enum Unidades {

	CENTIMETROS("CM", "CENTÍMETROS"),
	GRAMAS("G", "GRAMAS"), 
	KILOGRAMAS("KG", "KILOGRAMAS"), 
	METROS("MT", "METROS"),
	PEÇAS("PÇ", "PEÇAS"),
	POLEGADAS("POL", "POLEGADAS"),
	UNIDADES("UN", "UNIDADES");

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
