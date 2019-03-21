package zct.sistemas.leko.model;

import java.io.Serializable;
import java.util.List;

public class Orcamento implements Serializable {

	private static final long serialVersionUID = 7626936463383256951L;

	private String cliente;
	private String maoDeObra;
	private String descricaoServicos;
	private String total;
	private List<OrcamentoItem> itens;

	public Orcamento() {

	}

	public Orcamento(String cliente, String maoDeObra, String total, String descricaoServicos,
			List<OrcamentoItem> itens) {
		this.cliente = cliente.toUpperCase();
		this.maoDeObra = maoDeObra;
		this.total = total;
		this.descricaoServicos = descricaoServicos.toUpperCase();
		this.itens = itens;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente.toUpperCase();
	}

	public String getMaoDeObra() {
		return maoDeObra;
	}

	public void setMaoDeObra(String maoDeObra) {
		this.maoDeObra = maoDeObra;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getDescricaoServicos() {
		return descricaoServicos.toUpperCase();
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
		return "Orcamento [cliente=" + cliente + ", maoDeObra=" + maoDeObra + ", descricaoServicos=" + descricaoServicos
				+ ", total=" + total + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cliente == null) ? 0 : cliente.hashCode());
		result = prime * result + ((descricaoServicos == null) ? 0 : descricaoServicos.hashCode());
		result = prime * result + ((itens == null) ? 0 : itens.hashCode());
		result = prime * result + ((maoDeObra == null) ? 0 : maoDeObra.hashCode());
		result = prime * result + ((total == null) ? 0 : total.hashCode());
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
		if (cliente == null) {
			if (other.cliente != null)
				return false;
		} else if (!cliente.equals(other.cliente))
			return false;
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
		if (total == null) {
			if (other.total != null)
				return false;
		} else if (!total.equals(other.total))
			return false;
		return true;
	}

}
