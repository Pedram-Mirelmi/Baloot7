import ie.baloot6.model.Category;
import ie.baloot6.model.Commodity;
import ie.baloot6.model.Discount;
import ie.baloot6.model.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class HibernateTest {

    EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() {
        var registry = new StandardServiceRegistryBuilder().configure().build();
        entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @AfterEach
    void tearDown() {
//        registry
    }

    @Test
    void test1() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        entityManager.persist(new Discount("asdf", 10));

        entityManager.getTransaction().commit();

    }

    @Test
    void testManyToMany() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        String[] providerData = {"Peter Oven", "Allan Norman"};
        String[] categoryData = {"IT Project", "Networking Project"};
        Set<Category> categories = new HashSet<>();

        for (String proj : categoryData) {
            categories.add(new Category(proj));
        }

        for (String prov : providerData) {
            var provider = new Provider(prov.split(" ")[1], Calendar.getInstance().getTime());
            Commodity commodity = new Commodity(prov.split(" ")[0], provider, 200L, 1L);
            commodity.setCategorySet(categories);

            for (Category category : categories) {
                category.getCommoditySet().add(commodity);
            }

            entityManager.persist(provider);
            entityManager.persist(commodity);
        }
        entityManager.getTransaction().commit();
    }
}
