package ie.baloot7.controller;

import ie.baloot7.Utils.JWTUtility;
import ie.baloot7.data.IRepository;
import ie.baloot7.data.ISessionManager;
import ie.baloot7.exception.InvalidIdException;
import ie.baloot7.exception.InvalidRequestParamsException;
import ie.baloot7.exception.InvalidValueException;
import ie.baloot7.model.Commodity;
import ie.baloot7.DTO.CommodityDTO;
import ie.baloot7.model.User;
import ie.baloot7.DTO.RateDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import static ie.baloot7.Utils.Constants.*;

import java.util.*;

@RestController
public class CommodityController {
    final IRepository repository;
//    final ISessionManager sessionManager;

    public CommodityController(IRepository repository, ISessionManager sessionManager) {
        this.repository = repository;
//        this.sessionManager = sessionManager;
    }

    @GetMapping("/api/commodities/{commodityId}")
    public CommodityDTO getSingleCommodity(@RequestHeader(value = AUTH_TOKEN, required = false) String authToken,
                                           @PathVariable("commodityId") int commodityId) {
        User user = repository.getUserByUsername(JWTUtility.getUsernameFromToken(authToken));
        try {
            return new CommodityDTO(repository.getCommodityById(commodityId).get(),
                    repository.getInShoppingListCount(user.getUsername(), commodityId));
        }
        catch (NoSuchElementException e) {
            throw new InvalidIdException("Invalid commodity Id");
        }
    }

    @GetMapping("/api/commodities")
    public List<CommodityDTO> getCommodities(@RequestHeader(value = AUTH_TOKEN, required = false) String authToken,
                                             @RequestParam(QUERY) String query,
                                             @RequestParam(SORT_BY) String sortBy,
                                             @RequestParam(SEARCH_BY) String searchBy,
                                             @RequestParam(AVAILABLE) boolean onlyAvailable) {
        User user = repository.getUserByUsername(JWTUtility.getUsernameFromToken(authToken));
        try {
            List<Commodity> result;
            // searching
            result = doSearch(query, searchBy);
            // sorting
            result = doSort(result, sortBy);
            // adding inCart field
            var stream = result.stream().map(
                    commodity -> new CommodityDTO(commodity,
                            repository.getInShoppingListCount(user.getUsername(), commodity.getCommodityId()))
            );
            if (onlyAvailable) {
                stream = stream.filter(commodityDTO -> commodityDTO.getInStock() > 0);
            }
            return stream.toList();
        }
        catch (NoSuchElementException e) {
            throw new InvalidIdException("Some id is wrong");
        }
    }

    @PostMapping("/api/commoditiesRates")
    public RateDTO rateCommodity(@RequestHeader(AUTH_TOKEN) String authToken,
                                 @RequestBody Map<String, String> body) {
        User user = repository.getUserByUsername(JWTUtility.getUsernameFromToken(authToken));

        long commodityId = Long.parseLong(body.get(COMMODITY_ID));
        double rate = Float.parseFloat(body.get(RATING));
        double newRating = repository.addRating(user.getUsername(), commodityId, rate);
        return new RateDTO(SUCCESS, newRating, repository.getCommodityRateCount(commodityId));
    }

    @GetMapping("/api/recommended")
    public List<CommodityDTO> getRecommended(@RequestHeader(AUTH_TOKEN) String authToken,
                                             @RequestParam(COMMODITY_ID) long commodityId) {
        User user = repository.getUserByUsername(JWTUtility.getUsernameFromToken(authToken));

        return repository.getRecommendedCommodities(user.getUsername(), commodityId).stream().map(
                commodity -> new CommodityDTO(commodity,
                        repository.getCommodityRateCount(commodity.getCommodityId()))

        ).toList();
   }

    private List<Commodity> doSort(@NotNull List<Commodity> result, String sortBy) {
        if (sortBy.equals(NAME)) {
            return result.stream().sorted(Comparator.comparing(Commodity::getName)).toList();
        } else if (sortBy.equals(PRICE)) {
            return result.stream().sorted(Comparator.comparing(Commodity::getPrice)).toList();
        } else {
            throw new InvalidRequestParamsException("Invalid sort-by parameter");
        }
    }

    private List<Commodity> doSearch(String query, String searchBy) {
        if (searchBy.equals(NAME)) {
            return repository.searchCommoditiesByName(query);
        } else if (searchBy.equals(CATEGORY)) {
            if (query.isBlank())
                return repository.getCommodityList();
            return repository.getCommoditiesByCategory(query);
        }
        throw new InvalidRequestParamsException("Invalid search-by parameter");
    }
}
