package it.lmarchi.readly;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BASIC;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Import({
  AmazonKindleHighlightParser.class,
  NotionAuthInterceptor.class,
  NotionProvider.class,
  NotionSyncService.class
})
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
    Logger log = LoggerFactory.getLogger(OkHttpClient.class);
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(log::debug);
    loggingInterceptor.setLevel(BASIC);

    return new OkHttpClient()
        .newBuilder()
        .addInterceptor(authInterceptor)
        .addNetworkInterceptor(loggingInterceptor)
        .build();
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
