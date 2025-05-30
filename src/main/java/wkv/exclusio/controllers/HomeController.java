package wkv.exclusio.controllers;

import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import static wkv.exclusio.controllers.ConnexionController.SECRET_KEY;

@Controller
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    @RequestMapping(value = {"/", "/{path:[^\\.]*}"})
    public String index(@CookieValue(value = "auth-token", required = false) String token) {
        if (token == null || token.isEmpty()) {
            log.info("Requesting connexion.html");
            return "redirect:/connexion.html";
        }

        try {
            Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token);
            log.info("Requesting index.html");
            return "redirect:/index.html";
        } catch (Exception e) {
            log.info("Requesting connexion.html");
            return "redirect:/connexion.html";
        }
    }
}