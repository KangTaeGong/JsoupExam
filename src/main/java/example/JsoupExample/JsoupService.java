package example.JsoupExample;

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

        List<String> list = new ArrayList<>();

        try {
            Document document = conn.get();
            Document pointDocument = pointConn.get();

            // 영화 기본 링크에서 가져올 수 있는 한줄평(5개)
            Elements conElements = document.select("div.score_reple > p");
            for(int i = 0; i < conElements.size(); i++) {
                String text = conElements.get(i).text();
                log.info("한줄평 = {}", text);
            }

            Elements reporterNameElements = pointDocument.select("dl.p_review > dt > a");
            Elements reporterJobElements = pointDocument.select("dl.p_review > dt");
            Elements reporterCommentElements = pointDocument.select("dl.p_review > dd");
            Elements tx_reportElements = pointDocument.select("p.tx_report");
            Elements reporterHtmlElements = pointDocument.select("div.reporter > ul > li");
            for(int i = 0; i < reporterNameElements.size(); i++) {
                String html = reporterHtmlElements.get(i).html();
                log.info("기자, 평론가 관련 html = {}", html);
                String reporter_name = reporterNameElements.get(i).text();
                String reporter_info = reporterJobElements.get(i).text();
                String reporter_job = getReporterJob(reporter_info, reporter_name);
                String reporter_comment = reporterCommentElements.get(i).text();
                String reporter_reple = tx_reportElements.get(i).text();
                log.info("기자, 평론가 이름 = {}", reporter_name);
                log.info("기자, 평론가 직업 = {}", reporter_job);
                log.info("기자, 평론가 코멘트 = {}", reporter_comment);
                log.info("기자, 평론가의 리플 = {}", reporter_reple);
                list.add(html);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.addAttribute("reporter_html", list);
        return"service/servicePage";
    }

    // 기자, 평론가의 정보가 이름과 직업이 붙어있어 substring으로 이름부분만 제거해서 리턴
    private String getReporterJob(String reporter_info, String reporter_name) {
        String str = reporter_info.substring(reporter_name.length());
        return str;
    }
}
