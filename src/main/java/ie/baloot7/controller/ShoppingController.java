package ie.baloot7.controller;

import ie.baloot7.DTO.CommodityDTO;
import ie.baloot7.Utils.JWTUtility;
import ie.baloot7.data.IRepository;
import ie.baloot7.data.ISessionManager;
import ie.baloot7.exception.InvalidIdException;
import ie.baloot7.exception.InvalidRequestParamsException;
import ie.baloot7.exception.InvalidValueException;
import ie.baloot7.exception.NotEnoughAmountException;
import ie.baloot7.model.Discount;
import ie.baloot7.model.User;
import org.springframework.web.bind.annotation.*;

import static ie.baloot7.Utils.Constants.*;

import java.util.*;


@RestController
public class ShoppingController {
    final IRepository repository;

    public ShoppingController(IRepository repository) {
        this.repository = repository;
    }


    @PostMapping("/api/shoppingList/add")
    public Map<String, Object> addToShoppingList(@RequestHeader(AUTH_TOKEN) String authToken, @RequestBody Map<String, Long> body) throws InvalidIdException, NotEnoughAmountException {
        User user = repository.getUserByUsername(JWTUtility.getUsernameFromToken(authToken));
        try {
            long commodityId = Objects.requireNonNull(body.get(COMMODITY_ID));
            long count = Objects.requireNonNull(body.get(COUNT));
            repository.addToBuyList(user.getUsername(), commodityId, count);
            return Map.of(STATUS, SUCCESS, "count", repository.getInShoppingListCount(user.getUsername(), commodityId));
        } catch (NullPointerException e) {
            throw new InvalidRequestParamsException("Invalid commodity id or count");
        } catch (NoSuchElementException e) {
            throw new InvalidValueException("Authentication token invalid");
        }
    }

    @PostMapping("/api/shoppingList/remove")
    public Map<String, Object> removeFromShoppingList(@RequestHeader(AUTH_TOKEN) String authToken, @RequestBody Map<String, Long> body) throws NotEnoughAmountException {
        User user = repository.getUserByUsername(JWTUtility.getUsernameFromToken(authToken));
        try {
            long commodityId = Objects.requireNonNull(body.get(COMMODITY_ID));
            long count = Objects.requireNonNull(body.get(COUNT));
            repository.removeFromBuyList(user.getUsername(), commodityId, count);
            return Map.of(STATUS, SUCCESS, "count", repository.getInShoppingListCount(user.getUsername(), commodityId));
        }
        catch (NullPointerException e) {
            throw new InvalidRequestParamsException("Invalid commodityId or count");
        }
        catch (NoSuchElementException e) {
            throw new InvalidValueException("Authentication token invalid");
        }
    }

    @GetMapping("/api/shoppingList")
    public List<CommodityDTO> getShoppingList(@RequestHeader(AUTH_TOKEN) String authToken) throws InvalidIdException {
        User user = repository.getUserByUsername(JWTUtility.getUsernameFromToken(authToken));
        try {
            return repository.getShoppingList(user.getUsername()).stream().map(
                    commodity -> new CommodityDTO(commodity,
                            repository.getInShoppingListCount(user.getUsername(), commodity.getCommodityId()))
            ).toList();
        } catch (NoSuchElementException e) {
            throw new InvalidValueException("Authentication token invalid");
        }
    }

    @GetMapping("/api/purchasedList")
    public List<CommodityDTO> getPurchasedList(@RequestHeader(AUTH_TOKEN) String authToken) throws InvalidIdException {
        User user = repository.getUserByUsername(JWTUtility.getUsernameFromToken(authToken));
        try {
            return repository.getPurchasedList(user.getUsername()).stream().map(
                    si -> new CommodityDTO(si.getCommodity(),
                            repository.getInPurchasedListCount(user.getUsername(), si.getCommodity().getCommodityId()))
            ).toList();
        } catch (NoSuchElementException e) {
            throw new InvalidValueException("Authentication token invalid");
        }
    }

    @GetMapping("/api/pay")
    public Map<String, String> purchase(@RequestHeader(AUTH_TOKEN) String authToken, @RequestParam("discountCode") Optional<String> discountCode) throws InvalidIdException, NotEnoughAmountException {
        User user = repository.getUserByUsername(JWTUtility.getUsernameFromToken(authToken));
        try {
            float discount = 1.0F;
            if (discountCode.isPresent() && repository.getDiscount(discountCode.get()).isPresent()) {
                discount = repository.getDiscount(discountCode.get()).get().getDiscountAmount() / 100F;
            }
            repository.purchase(user.getUsername(), discount);
            return Map.of(STATUS, SUCCESS);
        } catch (NoSuchElementException e) {
            throw new InvalidValueException("Authentication token invalid");
        }
    }

    @GetMapping("/api/discount")
    public Map<String, String> validateDiscountCode(@RequestHeader(AUTH_TOKEN) String authToken,
                                                    @RequestParam("discountCode") String discountCode) {
            Optional<Discount> discount = repository.getDiscount(discountCode);
            return discount.isPresent() ?
                    Map.of(CODE_STATUS, VALID, DISCOUNT, String.valueOf(discount.get().getDiscountAmount())) :
                    Map.of(CODE_STATUS, INVALID);
    }

}
