package wkv.exclusio.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Controller
@RequestMapping("connexion")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ConnexionController {

    public static final String SECRET_KEY = Base64.getEncoder().encodeToString("UnePhraseSecrèteTrèsLongueEtComplexe123!".getBytes(StandardCharsets.UTF_8));
    public static final String CONNEXION_ID = "uTLhewpTC12;";

    @GetMapping()
    @ResponseBody
    public ResponseEntity<String> connexion(HttpServletResponse response, @RequestParam String id) {
        if(id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }else if(id.equals(CONNEXION_ID)){
            String token = generateToken(id);

            ResponseCookie cookie = ResponseCookie.from("auth-token", token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ofHours(1))
                    .sameSite("None").secure(true)
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok("");
        }else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("auth-token", "")
                .httpOnly(true)
                .secure(false) // ou true si HTTPS
                .path("/")
                .maxAge(0) // <= expire immédiatement
                .sameSite("Strict")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        return ResponseEntity.noContent().build();
    }

    private String generateToken(String userId) {
        // Exemple de JWT simple (durée de vie : 1 heure)
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1h
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
