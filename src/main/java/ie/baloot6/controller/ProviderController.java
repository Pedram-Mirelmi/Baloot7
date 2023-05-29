package ie.baloot6.controller;

import ie.baloot6.data.IRepository;
import ie.baloot6.data.ISessionManager;
import ie.baloot6.exception.InvalidIdException;
import ie.baloot6.exception.InvalidValueException;
import ie.baloot6.DTO.CommodityDTO;
import ie.baloot6.model.Provider;
import ie.baloot6.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static ie.baloot6.Utils.Constants.*;

@RestController
public class ProviderController {
    final IRepository repository;
    final ISessionManager sessionManager;

    public ProviderController(IRepository repository, ISessionManager sessionManager) {
        this.repository = repository;
        this.sessionManager = sessionManager;
    }

    @GetMapping("/api/providers")
    public Provider getProvider(@RequestHeader(AUTH_TOKEN) String authToken, @RequestParam(PROVIDER_ID) long providerId) {
        if(sessionManager.isValidToken(authToken)) {
            try {
                return repository.getProvider(providerId).get();
            }
            catch (NoSuchElementException e) {
                throw new InvalidIdException("Invalid provider id");
            }
        }
        throw new InvalidValueException("Invalid authentication token");
    }

    @GetMapping("/api/providers/commodities")
    public List<CommodityDTO> getProvidersCommodities(@RequestHeader(AUTH_TOKEN) String authToken, @RequestParam(PROVIDER_ID) long providerId) {
        if(sessionManager.isValidToken(authToken)) {
            User user = sessionManager.getUser(authToken).get();
            return repository.getProvidersCommoditiesList(providerId).stream().map(
                    commodity -> new CommodityDTO(commodity,
                            repository.getCommodityRateCount(commodity.getCommodityId()))

            ).toList();
        }
        throw new InvalidValueException("Invalid authentication token");
    }
}
