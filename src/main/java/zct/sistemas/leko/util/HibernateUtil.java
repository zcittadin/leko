package zct.sistemas.leko.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import zct.sistemas.leko.model.Item;

public class HibernateUtil {

	private final static SessionFactory sessionFactory;
	private final static ServiceRegistry serviceRegistry;
	private static final Configuration configuration = new Configuration();

	static {
		getConfiguration();
		serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}

	public static Session openSession() {
		return sessionFactory.openSession();
	}

	public static void closeSessionFactory() throws Exception {
		sessionFactory.close();
	}

	private static void getConfiguration() {
		configuration.addPackage("zct.sistemas.leko.model");
		configuration.addAnnotatedClass(Item.class);
//		configuration.addAnnotatedClass(Veiculo.class);
//		configuration.addAnnotatedClass(Viagem.class);
//		configuration.addAnnotatedClass(ViagemItens.class);
		configuration.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
		configuration.setProperty("hibernate.connection.url",
				"jdbc:sqlite:C:/Users/usuario/Documents/DEV/Electron/leko/database/leko-database.db");
		configuration.setProperty("hibernate.connection.username", "");
		configuration.setProperty("hibernate.connection.password", "");
		configuration.setProperty("hibernate.connection.autocommit", "true");
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLiteDialect");
// 		configuration.setProperty("hibernate.show_sql", "true");
		configuration.setProperty("hibernate.flushMode", "ALWAYS");
		configuration.setProperty("hibernate.format_sql", "true");

//		configuration.setProperty("hibernate.c3p0.min_size", "1");
//		configuration.setProperty("hibernate.c3p0.max_size", "1");
//		configuration.setProperty("hibernate.c3p0.acquire_increment", "1");
//		configuration.setProperty("hibernate.c3p0.timeout", "1800");
	}

}
