package it.lmarchi.kindletonotionsyncer;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;

/** Interceptor that adds authentication to all requests sent to Notion. */
final class NotionAuthInterceptor implements Interceptor {
  private final String notionApiKey;

  NotionAuthInterceptor(@Value("${notion.api-key}") String notionApiKey) {
    this.notionApiKey = notionApiKey;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    return chain.proceed(
        chain
            .request()
            .newBuilder()
            .addHeader("Authorization", "Bearer " + notionApiKey)
            .addHeader("Notion-Version", "2022-02-22")
            .build());
  }
}
