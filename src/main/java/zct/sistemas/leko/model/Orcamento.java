package zct.sistemas.leko.model;

import java.io.Serializable;
import java.util.List;

public class Orcamento implements Serializable {

	private static final long serialVersionUID = 3965732394779250542L;

	private String maoDeObra;
	private String descricaoServicos;
	private List<OrcamentoItem> itens;

	public Orcamento() {

	}

	public Orcamento(String maoDeObra, String descricaoServicos, List<OrcamentoItem> itens) {
		this.maoDeObra = maoDeObra;
		this.descricaoServicos = descricaoServicos;
		this.itens = itens;
	}

	public String getMaoDeObra() {
		return maoDeObra;
	}

	public void setMaoDeObra(String maoDeObra) {
		this.maoDeObra = maoDeObra;
	}

	public String getDescricaoServicos() {
		return descricaoServicos;
	}

	public void setDescricaoServicos(String descricaoServicos) {
		this.descricaoServicos = descricaoServicos;
	}

	public List<OrcamentoItem> getItens() {
		return itens;
	}

	public void setItens(List<OrcamentoItem> itens) {
		this.itens = itens;
	}

	@Override
	public String toString() {
		return "Orcamento [maoDeObra=" + maoDeObra + ", descricaoServicos=" + descricaoServicos + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((descricaoServicos == null) ? 0 : descricaoServicos.hashCode());
		result = prime * result + ((itens == null) ? 0 : itens.hashCode());
		result = prime * result + ((maoDeObra == null) ? 0 : maoDeObra.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Orcamento other = (Orcamento) obj;
		if (descricaoServicos == null) {
			if (other.descricaoServicos != null)
				return false;
		} else if (!descricaoServicos.equals(other.descricaoServicos))
			return false;
		if (itens == null) {
			if (other.itens != null)
				return false;
		} else if (!itens.equals(other.itens))
			return false;
		if (maoDeObra == null) {
			if (other.maoDeObra != null)
				return false;
		} else if (!maoDeObra.equals(other.maoDeObra))
			return false;
		return true;
	}

}
