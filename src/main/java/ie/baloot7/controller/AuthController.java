package ie.baloot7.controller;

import static ie.baloot7.Utils.Constants.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ie.baloot7.Utils.Constants;
import ie.baloot7.Utils.JWTUtility;
import ie.baloot7.Utils.Secret;
import ie.baloot7.data.IRepository;
import ie.baloot7.data.ISessionManager;
import ie.baloot7.exception.InvalidRequestParamsException;
import ie.baloot7.exception.InvalidIdException;
import ie.baloot7.exception.UnauthorizedException;
import ie.baloot7.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class AuthController {

    final IRepository repository;
//    final ISessionManager sessionManager;

    public AuthController(IRepository repository, ISessionManager sessionManager) throws NoSuchAlgorithmException {
        this.repository = repository;
//        this.sessionManager = sessionManager;
        sessionManager.addSession("amir", "1234");
    }

    @PostMapping("/api/github-oauth")
    public Map<String, String> oauthWithGithub(@RequestParam("code") String code) throws IOException {

        String githubUserToken = getUserTokenFromGithub(code);
        JsonObject userInfo = getUserInfoFromGithub(githubUserToken);
//        var sdf = new DateTimeFormatter("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String username = userInfo.get("login").getAsString();
        String email = userInfo.get("email").getAsString();
        Date birthDate = Date.valueOf(LocalDate.parse(userInfo.get("created_at").getAsString()).minusYears(18));
        try {
            User user = repository.getUserByEmail(email);
            repository.updateUser(user.getUserId(), username, birthDate);
        } catch (InvalidIdException e) { // new User
            repository.addUser(username, null, email, birthDate, null, 0);
        }
        return Map.of(STATUS, SUCCESS,
                AUTH_TOKEN, JWTUtility.generateToken(username),
                USERNAME, username);
    }

    @GetMapping("/api/logout")
    public Map<String, String> logout(@RequestHeader(Constants.AUTH_TOKEN) String authToken) {
//        sessionManager.removeSession(authToken);
//        Map<String, String> response = new HashMap<>();
//        response.put("status", "success");
        return Map.of(STATUS, SUCCESS);
    }

    @PostMapping("/api/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) throws NoSuchAlgorithmException {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            throw new UnauthorizedException("Empty username or password");
        }
        if (repository.authenticate(username, password)) {
            var authToken = JWTUtility.generateToken(username);
            return Map.of(AUTH_TOKEN, authToken,
                    STATUS, SUCCESS);
        }
        throw new UnauthorizedException("Username or password was wrong");
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
//            String authToken = sessionManager.addSession(username, password);
            var authToken = JWTUtility.generateToken(username);
            return Map.of(STATUS, SUCCESS,
                    AUTH_TOKEN, authToken);
        } catch (NullPointerException e) {
            throw new InvalidRequestParamsException("All the Fields are required");
        } catch (InvalidIdException e) {
            throw new InvalidIdException("User already exists");
        }
    }


    //
    private String getUserTokenFromGithub(String code) throws IOException {
        return getResource("https://github.com/login/oauth/access_token?client_id="
                + Secret.clientId + "&client_secret=" + Secret.secret + "&code=" + code, Map.of())
                .get("access_token")
                .getAsString();
    }

    private JsonObject getUserInfoFromGithub(String githubUserToken) throws IOException {
        return getResource("https://api.github.com/user", Map.of("Accept", "application/vnd.github+json",
                "Authorization", "Bearer " + githubUserToken,
                "X-GitHub-Api-Version", "2022-11-28"));
    }

    private JsonObject getResource(@NotNull String url, Map<String, String> headers) throws IOException {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        for (var entry :
                headers.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }
        int responseCode = con.getResponseCode();
        if (responseCode != 200)
            throw new IOException("foreign api sent a response with status code " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }
        return new Gson().fromJson(sb.toString(), JsonObject.class);
    }
}
