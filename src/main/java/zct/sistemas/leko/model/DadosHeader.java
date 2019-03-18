package zct.sistemas.leko.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DADOS_HEADER")
public class DadosHeader implements Serializable {

	private static final long serialVersionUID = -2613219041950326913L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	@Column(name = "NOME_EMPRESA")
	private String nomeEmpresa;
	@Column(name = "ENDERECO")
	private String endereco;
	@Column(name = "CIDADE")
	private String cidade;
	@Column(name = "ESTADO")
	private String estado;
	@Column(name = "PAIS")
	private String pais;
	@Column(name = "TELEFONE")
	private String telefone;
	@Column(name = "EMAIL")
	private String email;
	@Column(name = "CEP")
	private String cep;
	@Column(name = "LOGO")
	private String logo;

	public DadosHeader() {

	}

	public DadosHeader(Long id, String nomeEmpresa, String endereco, String cidade, String estado, String pais,
			String telefone, String email, String cep, String logo) {
		this.id = id;
		this.nomeEmpresa = nomeEmpresa;
		this.endereco = endereco;
		this.cidade = cidade;
		this.estado = estado;
		this.pais = pais;
		this.telefone = telefone;
		this.email = email;
		this.cep = cep;
		this.logo = logo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeEmpresa() {
		return nomeEmpresa;
	}

	public void setNomeEmpresa(String nomeEmpresa) {
		this.nomeEmpresa = nomeEmpresa.toUpperCase();
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco.toUpperCase();
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade.toUpperCase();
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado.toUpperCase();
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais.toUpperCase();
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Override
	public String toString() {
		return "DadosHeader [id=" + id + ", nomeEmpresa=" + nomeEmpresa + ", endereco=" + endereco + ", cidade="
				+ cidade + ", estado=" + estado + ", pais=" + pais + ", telefone=" + telefone + ", email=" + email
				+ ", cep=" + cep + ", logo=" + logo + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		DadosHeader other = (DadosHeader) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
