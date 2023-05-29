package ie.baloot6.controller;

import static ie.baloot6.Utils.Constants.*;

import ie.baloot6.Utils.Constants;
import ie.baloot6.data.IRepository;
import ie.baloot6.data.ISessionManager;
import ie.baloot6.exception.InvalidRequestParamsException;
import ie.baloot6.exception.InvalidIdException;
import ie.baloot6.model.User;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class AuthController {

    final IRepository repository;
    final ISessionManager sessionManager;

    public AuthController(IRepository repository, ISessionManager sessionManager) throws NoSuchAlgorithmException {
        this.repository = repository;
        this.sessionManager = sessionManager;
        sessionManager.addSession("amir", "1234");
    }

    @GetMapping("/api/logout")
    public Map<String, String> logout(@RequestHeader(Constants.AUTH_TOKEN) String authToken) {
        sessionManager.removeSession(authToken);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return response;
    }

    @PostMapping("/api/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) throws NoSuchAlgorithmException {
        String username = body.get("username");
        String password = body.get("password");
        if(username == null || password == null) {
            return null;
        }
        String authToken = sessionManager.addSession(username, password);
        return Map.of(AUTH_TOKEN, authToken,
                      STATUS, SUCCESS);
    }

    @PostMapping("/api/register")
    public Map<String, String> register(@RequestBody Map<String, String> body) {
        try {
            String username = Objects.requireNonNull(body.get(USERNAME));
            String password = Objects.requireNonNull(body.get(PASSWORD));
            String email = Objects.requireNonNull(body.get(EMAIL));
            Date birthDate = Date.valueOf(body.get(BIRTHDATE));
            String address = Objects.requireNonNull(body.get(ADDRESS));

            repository.addUser(username, password, email, birthDate, address, 0);
            String authToken = sessionManager.addSession(username, password);
            return Map.of(STATUS, SUCCESS,
                    AUTH_TOKEN, authToken);
        } catch (NullPointerException e)
        {
            throw new InvalidRequestParamsException("All the Fields are required");
        }
        catch (InvalidIdException | NoSuchAlgorithmException e) {
            throw new InvalidIdException("User already exists");
        }
    }

}
