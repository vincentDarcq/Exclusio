package wkv.exclusio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AlloPersonneResultDto {
    private String entity_type;
    private AlloPersonneDataDto data;
}
