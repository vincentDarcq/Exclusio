package wkv.exclusio.controllers;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import static wkv.exclusio.controllers.ConnexionController.SECRET_KEY;

@Controller
public class HomeController {
    @RequestMapping(value = "/{path:[^\\.]*}")
    public String index(@CookieValue(value = "auth-token", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return "redirect:/connexion.html";
        }

        try {
            Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token);

            return "redirect:/index.html";
        } catch (Exception e) {
            return "redirect:/connexion.html";
        }
    }
}