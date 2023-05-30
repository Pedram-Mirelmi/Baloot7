package ie.baloot7.controller;

import ie.baloot7.Utils.JWTUtility;
import ie.baloot7.data.IRepository;
import ie.baloot7.data.ISessionManager;
import ie.baloot7.exception.InvalidIdException;
import ie.baloot7.exception.InvalidValueException;
import ie.baloot7.DTO.CommodityDTO;
import ie.baloot7.model.Provider;
import ie.baloot7.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static ie.baloot7.Utils.Constants.*;

@RestController
public class ProviderController {
    final IRepository repository;
//    final ISessionManager sessionManager;

    public ProviderController(IRepository repository, ISessionManager sessionManager) {
        this.repository = repository;
//        this.sessionManager = sessionManager;
    }

    @GetMapping("/api/providers")
    public Provider getProvider(@RequestHeader(AUTH_TOKEN) String authToken, @RequestParam(PROVIDER_ID) long providerId) {
        try {
            return repository.getProvider(providerId).get();
        }
        catch (NoSuchElementException e) {
            throw new InvalidIdException("Invalid provider id");
        }
    }

    @GetMapping("/api/providers/commodities")
    public List<CommodityDTO> getProvidersCommodities(@RequestHeader(AUTH_TOKEN) String authToken,
                                                      @RequestParam(PROVIDER_ID) long providerId) {
        return repository.getProvidersCommoditiesList(providerId).stream().map(
                commodity -> new CommodityDTO(commodity,
                        repository.getCommodityRateCount(commodity.getCommodityId()))

            ).toList();
    }
}
