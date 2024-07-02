package search;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import search.biz.NaverApiService;
import search.model.SearchResult;
import search.model.SearchResult.Item;

public class Main {
	public static void main(String[] args) {
		// Naver API의 기본 URL 설정
		String baseUrl = "https://openapi.naver.com/";
		
		// Retrofit 인스턴스 생성
		// GsonConverterFactory를 사용하여 JSON 데이터를 객체로 변환
		Retrofit retrofit = new Retrofit.Builder()
				.addConverterFactory(GsonConverterFactory.create())
			    .baseUrl(baseUrl)
			    .build();
		
		// https://developers.naver.com/main/
		// https://developers.naver.com/apps/#/list -> Application 등록 -> 사용 API / 검색
		// 비로그인 오픈 API 서비스 환경 > 환경추가 > WEB 설정 > https://naver.com -> 등록하기
		// 내 애플리케이션 > 애플리케이션 정보 > Client ID / Client Secret
		// https://developers.naver.com/docs/serviceapi/search/blog/blog.md#%EB%B8%94%EB%A1%9C%EA%B7%B8
		
		// Naver API를 사용하기 위한 클라이언트 ID와 클라이언트 시크릿
		String clientID = "";
		String clientSecret = "";
		
		// NaverApiService 인터페이스의 구현체를 생성
		NaverApiService service = retrofit.create(NaverApiService.class);
		SearchResult searchResult = null;
		
		// 검색 키워드 설정
		String kwd = "뱅뱅사거리 맛집";
		try {
			// Naver API를 호출하여 검색 결과를 가져옴
			// execute() 메서드를 사용하여 동기적으로 API 호출
			searchResult = service.search(clientID, clientSecret, "blog", kwd, 100, 1).execute().body();
		} catch (IOException e) {
			// 예외 발생 시 스택 트레이스를 출력
			e.printStackTrace();
		}
		
		// BufferedWriter를 사용하여 파일에 검색 결과를 저장
		BufferedWriter bw = null;
		try {
			// 검색 키워드를 파일 이름으로 사용 (공백은 밑줄로 대체)
			bw = new BufferedWriter(new FileWriter(kwd.replace(" ", "_") + ".txt"));
			
			// 검색 결과의 각 아이템에 대해
			for (Item item : searchResult.items) {
				// 제목에서 HTML 태그(<b>, </b>) 제거
				String title = item.getTitle()
						.replace("<b>", "")
						.replace("</b>", "");
				
				// 제목 출력 및 파일에 저장
				System.out.println(title);
				bw.append(title);
				bw.newLine();
				
				// 링크 출력 및 파일에 저장
				System.out.println(item.link);
				bw.append(item.link);
				bw.newLine();
			}
		} catch (IOException e) {
			// 예외 발생 시 스택 트레이스를 출력
			e.printStackTrace();
		} finally {
			// BufferedWriter가 null이 아니면 닫기
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					// 예외 발생 시 스택 트레이스를 출력
					e.printStackTrace();
				}
			}
		}
	}
}
