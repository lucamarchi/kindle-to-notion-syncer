package it.lmarchi.kindletonotionsyncer;

import it.lmarchi.kindletonotionsyncer.model.notion.CreateNotionBlockRequest;
import it.lmarchi.kindletonotionsyncer.model.notion.CreateNotionPageRequest;
import it.lmarchi.kindletonotionsyncer.model.notion.NotionPaginatedRequest;
import it.lmarchi.kindletonotionsyncer.model.notion.NotionPaginatedResponse;
import javax.annotation.Nullable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/** Retrofit interface for interacting with Notion APIs. */
interface NotionClient {
  @POST("databases/{id}/query")
  Call<NotionPaginatedResponse> queryDatabase(
      @Path("id") String id, @Body NotionPaginatedRequest request);

  @POST("pages")
  Call<Void> createPage(@Body CreateNotionPageRequest request);

  @GET("blocks/{id}/children")
  Call<NotionPaginatedResponse> getBlocks(
      @Path("id") String id, @Nullable @Query("start_cursor") String startCursor);

  @PATCH("blocks/{id}/children")
  Call<Void> addBlocks(@Path("id") String id, @Body CreateNotionBlockRequest request);
}
