## Jsoup

Java에서 html 파싱을 할 수 있도록 도와주는 라이브러리.

<br>

## Project

특정 페이지에 있는 데이터를 가져와 원하는 대로 사용하기 위해 Jsoup을 사용.<br>
현재 코드는 네이버 영화 페이지에서 원하는 정보를 가져오는 예제 프로젝트이며 최종적으로 가공해서 출력하는 것 까지가 목표이다.

<br>

## Code

**사용 전 `build.gradle`에 Jsoup 관련 코드 추가**
```gradle
dependencies {
	implementation 'org.jsoup:jsoup:1.15.3'
}
```

### 주요 코드

```java
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
        
        /*
        * 기본 영화 URL은 제공받기 때문에 거기서 특정 부분만 수정하여 리뷰 페이지 링크를 제작
        * 관람객 한줄평 더보기 링크에 걸어줌
        * */
        String pointUrl = movieUrl.replace("basic", "point");
        Connection pointConn = Jsoup.connect(pointUrl);

        // 여러개의 한줄평을 list에 저장
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
```
- `movieUrl` 변수에 데이터를 가져오고 싶은 경로 입력.
  - 실제로 사용할 때는 동적으로 URL 입력
- Jsoup을 이용해 URL 주소에 연결하고, 다시 `Connection.get()` 메서드를 통해 실제로 값을 추출하거나 조작할 수 있는 Document 객체 생성
- 해당 페이지에서 가져오고 싶은 데이터의 html 코드를 `select()` 메서드의 argument로 넣어 데이터를 추출.
