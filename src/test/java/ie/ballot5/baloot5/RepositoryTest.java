package ie.ballot5.baloot5;

import com.google.common.collect.Lists;
import ie.baloot6.data.IRepository;
import ie.baloot6.model.User;
import ie.baloot6.service.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {Repository.class})
class RepositoryTest {

	IRepository repository;

	@BeforeEach
	void setUp() throws ParseException {
		repository = new Repository();
	}

	@Test
	void testAddUser() {
		repository.addUser("Pedram",
				"pass1",
				"asdf@ad",
				Date.valueOf(LocalDate.now()),
				"addr1",
				1000);
	}

	@Test
	void testGetUser() {
		assertEquals(1, repository.getUserByUsername("Pedram").getUserId());
	}

	@Test
	void testAddProvider() {
		repository.addProvider(1, "provider 1", Date.valueOf(LocalDate.now()));
	}

	@Test
	void testGetProvider() {
		assertEquals(1, repository.getProvider(1).get().getProviderId());
	}

	@Test
	void testAddCategory() {
		repository.addCategory("cat2");
	}

	@Test
	void testAddCommodity() {
		repository.addCommodity(1, "c1", 1, 2000, 26, List.of("cat3", "cat2"));
	}

	@Test
	void testGetCommodity() {
		var c = repository.getCommodityById(1).get();
		assertEquals("c1", c.getName());
		assertEquals(2000, c.getPrice());
	}

	@Test
	void testAddDiscount() {
		repository.addDiscount("d1", 46);
	}

	@Test
	void testGetDiscount() {
		assertEquals(46, repository.getDiscount("d1").get().getDiscountAmount());
	}

	@Test
	void testValidateDiscount() {
		assertFalse(repository.hasUserUsedDiscount("d1", "Pedram"));
	}

	@Test
	void testGetAllCommodities() {
		var commodities = repository.getCommodityList();
		assertEquals(1, commodities.size());
		var c = commodities.get(0);
		assertEquals("c1", c.getName());
		assertEquals(2000, c.getPrice());
	}

	@Test
	void testGetProvidersCommoditiesList() {
		var commodities = repository.getProvidersCommoditiesList(1);
		assertEquals(1, commodities.size());
		var c = commodities.get(0);
		assertEquals("c1", c.getName());
		assertEquals(2000, c.getPrice());
	}



	@Test
	void testGetCommodityByCategory() {
		var commodities = repository.getCommoditiesByCategory("cat1");
		assertEquals(1, commodities.size());
		var c = commodities.get(0);
		assertEquals("c1", c.getName());
		assertEquals(2000, c.getPrice());
	}

	@Test
	void testGetCommoditiesByName() {
		var commodities = repository.getCommoditiesByName("c1");
		assertEquals(1, commodities.size());
		var c = commodities.get(0);
		assertEquals("c1", c.getName());
		assertEquals(2000, c.getPrice());
	}

	@Test
	void testAddToBuyList() {
		repository.addToBuyList("Pedram", 1, 4);
		repository.addToBuyList("Pedram", 1, 5);
	}

	@Test
	void testRemoveFromBuyList() {
//		repository.removeFromBuyList("Pedram", 1, 6);
//		var shoppingList = repository.getShoppingList("Pedram");
//		assertEquals(1, shoppingList.size());
//		assertEquals(3, shoppingList.get(0).getCount());
//		assertEquals(1, shoppingList.get(0).getCommodity().getCommodityId());
	}

	@Test
	void testAddRating() {
		repository.addRating("Pedram", 1, 7.7F);
		assertEquals(1, repository.getCommodityRateCount(1));
		repository.getCommodityRating(1).get().equals(7.7);
	}

	@Test
	void addVote() {
		repository.addVote("Pedram", 1, 1);
		assertEquals(1, repository.getLikes(1));
		assertEquals(0, repository.getDislikes(1));
		repository.addVote("Pedram", 1, 0);
		assertEquals(0, repository.getLikes(1));
		assertEquals(0, repository.getDislikes(1));
	}

	@Test
	void testGetCommentsForCommodity() {
		var comments = repository.getCommentsForCommodity(1);
		assertEquals(1, comments.size());
		assertEquals("this is the best", comments.get(0).getText());
	}

	@Test
	void addCredit() {
		User user = repository.getUserByUsername("Pedram");
		repository.addCredit("Pedram", 100000);
		User richUser = repository.getUserByUsername("Pedram");
		assertEquals(user.getCredit() + 100000, richUser.getCredit());
	}

	@Test
	void testPurchase() {
		User user =  repository.getUserByUsername("Pedram");
		repository.purchase("Pedram", 0.5F);
		User purchasedUser = repository.getUserByUsername("Pedram");
		assertEquals(100_000, purchasedUser.getCredit());
	}

	@Test
	void testGetUserRating() {
		assertEquals(7.7, repository.getUserRating("Pedram", 1).get());
	}

	@Test
	void testCalculateTotalShoppingList() {
		testAddToBuyList();
		assertEquals(18_000, repository.calculateTotalBuyListPrice("Pedram"));
	}

	@Test
	void testGetPurchasedList() {
		var purchased = repository.getPurchasedList("Pedram");
		assertEquals(1, purchased.size());
		assertEquals(1, purchased.get(0).getCount());
		assertEquals("c1", purchased.get(0).getCommodity().getName());
	}

	@Test
	void testAuthenticate() {
		assertTrue(repository.authenticate("Pedram", "123"));
	}

}
