package it.lmarchi.readly;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Import({AmazonKindleHighlightParser.class, NotionAuthInterceptor.class, NotionProvider.class})
@EnableConfigurationProperties
final class HighlightNotionSyncServiceConfig {
  @Bean
  ObjectMapper objectMapper() {
    return new ObjectMapper()
        .disable(FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(FAIL_ON_INVALID_SUBTYPE)
        .registerModule(new Jdk8Module())
        .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
  }

  @Bean
  OkHttpClient notionHttpClient(NotionAuthInterceptor authInterceptor) {
    return new OkHttpClient().newBuilder().addInterceptor(authInterceptor).build();
  }

  @Bean
  Retrofit notionRetrofit(
      OkHttpClient notionHttpClient,
      ObjectMapper objectMapper,
      @Value("${notion.base-url:https://api.notion.com/v1/}") String notionBaseUrl) {
    return new Retrofit.Builder()
        .baseUrl(notionBaseUrl)
        .client(notionHttpClient)
        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
        .build();
  }

  @Bean
  NotionClient notionClient(Retrofit notionRetrofit) {
    return notionRetrofit.create(NotionClient.class);
  }
}
