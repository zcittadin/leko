package zct.sistemas.leko.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;

import zct.sistemas.leko.model.DadosHeader;
import zct.sistemas.leko.util.HibernateUtil;

public class DadosHeaderDAO {

	public void updateItem(DadosHeader dados) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.update(dados);
		session.getTransaction().commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
	public List<DadosHeader> findDados() {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		String sql = "SELECT d FROM DadosHeader d ORDER BY d.id";
		Query query = session.createQuery(sql);
		List<DadosHeader> list = new ArrayList<>();
		list = query.getResultList();
		session.close();
		if (list == null || list.isEmpty())
			return null;
		return list;
	}
}
