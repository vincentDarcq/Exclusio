package wkv.exclusio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ImdbResponseDto {
    private List<ImdbResultDto> d;
}
