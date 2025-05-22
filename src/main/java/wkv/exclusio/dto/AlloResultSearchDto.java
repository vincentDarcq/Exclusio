package wkv.exclusio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class AlloResultSearchDto {

    private boolean error;
    private String message;
    private List<AlloPersonneResultDto> results;

}
