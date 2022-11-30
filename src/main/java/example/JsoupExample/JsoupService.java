package example.JsoupExample;

import example.JsoupExample.dto.ResultDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class JsoupService {

    /*
     * 크롤링을 하기 위해 필요한 URL 주소 및 Jsoup 객체들을 선언
     * 크롤링해서 가져올 사이트 정보
     * 실제로 사용할 때는 api를 통해서 가져온 link를 사용할 예정
     *  */
    private String source_id;

    @GetMapping("/")
    public String crawlService(Model model) {

        final String movieUrl = "https://movie.naver.com/movie/bi/mi/basic.naver?code=201641";
        Connection conn = Jsoup.connect(movieUrl);

        final String pointUrl = "https://movie.naver.com/movie/bi/mi/point.naver?code=201641";
        Connection pointConn = Jsoup.connect(pointUrl);

        List<ResultDto> result_list = new ArrayList<>();
        try {
            Document document = conn.get();
            Document pointDocument = pointConn.get();

            // 영화 기본 링크에서 가져올 수 있는 한줄평(5개)
            Elements conElements = document.select("div.score_reple > p");
            Elements scoreElements = document.select("div.score_result > ul > li > div.star_score > em");

            for(int i = 0; i < conElements.size(); i++) {
                String text = conElements.get(i).text();
                String score = scoreElements.get(i).text();
                log.info("한줄평 = {}", text);
                log.info("점수 = {}", score);
                ResultDto result = new ResultDto(score, text);
                result_list.add(result);
            }

            model.addAttribute("result_list", result_list);

            // 기자, 평론가 html
            Elements reporterHtmlElements = pointDocument.select("div.reporter");
            String html = reporterHtmlElements.html();
            model.addAttribute("reporter_html", html);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return"service/servicePage";
    }
}
