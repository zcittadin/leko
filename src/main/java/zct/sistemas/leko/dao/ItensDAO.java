package zct.sistemas.leko.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;

import zct.sistemas.leko.model.Item;
import zct.sistemas.leko.util.HibernateUtil;

public class ItensDAO {

	public void saveItem(Item item) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.save(item);
		session.getTransaction().commit();
		session.close();
	}

	public void updateItem(Item item) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.update(item);
		session.getTransaction().commit();
		session.close();
	}

	public void removeItem(Item item) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.remove(item);
		session.getTransaction().commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
	public List<Item> findItens() {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		String sql = "SELECT it FROM Item it ORDER BY it.descricao ASC";
		Query query = session.createQuery(sql);
		List<Item> list = new ArrayList<>();
		list = query.getResultList();
		session.close();
		if (list == null || list.isEmpty())
			return null;
		return list;
	}
}
