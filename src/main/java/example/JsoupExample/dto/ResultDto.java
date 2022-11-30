package example.JsoupExample.dto;

import lombok.Getter;

// 한줄평 값을 넣기 위한 DTO
@Getter
public class ResultDto {

    private String score_image;
    private String reple_text;

    public ResultDto(String score_image, String reple_text) {
        this.score_image = score_image;
        this.reple_text = reple_text;
    }
}
