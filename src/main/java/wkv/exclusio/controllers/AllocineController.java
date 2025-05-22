package wkv.exclusio.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import wkv.exclusio.dto.AlloPersonneResultDto;
import wkv.exclusio.dto.AlloResultSearchDto;

import java.util.List;

@Controller
@RequestMapping("allo")
@AllArgsConstructor
public class AllocineController {

    private final RestTemplate restTemplate;
    @GetMapping("/{person}")
    @ResponseBody
    public List<AlloPersonneResultDto> getPerson(@PathVariable String person) {
        AlloResultSearchDto resultSearchDto = this.restTemplate.getForObject("https://www.allocine.fr/_/autocomplete/" + person, AlloResultSearchDto.class);
        assert resultSearchDto != null;
        return resultSearchDto.getResults().stream()
                .filter(result -> "person".equals(result.getEntity_type()))
                .toList();
    }
}
