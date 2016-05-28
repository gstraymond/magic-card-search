package fr.gstraymond.biz;

import android.content.Context;
import android.text.TextUtils;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SearchEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.card.search.commons.json.MapperUtil;
import com.magic.card.search.commons.log.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

import fr.gstraymond.db.json.JsonHistoryDataSource;
import fr.gstraymond.search.model.request.Request;
import fr.gstraymond.search.model.response.SearchResult;
import fr.gstraymond.tools.VersionUtils;

public class ElasticSearchClient {

    private static final String CONTENT_ENCODING = "Content-Encoding";
    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    private static final String GZIP = "gzip";
    private static final String ENCODING = "UTF-8";

    private URL baseUrl;
    private MapperUtil<SearchResult> mapperUtil;
    private String appName;
    private JsonHistoryDataSource historyDataSource;

    private Log log = new Log(this);

    public ElasticSearchClient(URL baseUrl, ObjectMapper objectMapper, Context context, JsonHistoryDataSource jsonHistoryDataSource) {
        this.baseUrl = baseUrl;
        this.mapperUtil = MapperUtil.fromType(objectMapper, SearchResult.class);
        this.appName = VersionUtils.getAppName(context);
        this.historyDataSource = jsonHistoryDataSource;
    }

    public interface CallBacks {
        void start();

        void buildRequest();

        void getResponse();

        void end();
    }

    public SearchResult process(SearchOptions options, CallBacks callbacks) {
        callbacks.start();
        log.d("options as json : %s", options);
        Request request = new Request(options);
        String queryAsJson = mapperUtil.asJsonString(request);
        log.d("query as json : %s", queryAsJson);
        callbacks.buildRequest();

        Answers.getInstance().logSearch(buildSearchEvent(options));

        HttpURLConnection urlConnection = null;
        SearchResult searchResult = null;
        try {
            String query = URLEncoder.encode(queryAsJson, ENCODING);
            urlConnection = buildRequest(query);

            if (options.isAddToHistory()) {
                log.i("add to history : " + options);
                historyDataSource.appendHistory(options);
            }

            InputStream inputStream = getInputStream(urlConnection);
            callbacks.getResponse();
            searchResult = parse(inputStream);
            callbacks.end();
        } catch (SocketException e) {
            log.w("Socket exception: %s", e.getMessage());
        } catch (UnknownHostException e) {
            log.w("unknown host: %s", e.getMessage());
        } catch (Exception e) {
            if (e.getMessage().contains("ETIMEDOUT")) {
                log.w("timeout:" + e.getMessage());
            } else {
                log.e("process", e);
            }
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return searchResult;
    }

    private SearchEvent buildSearchEvent(SearchOptions options) {
        SearchEvent searchEvent = new SearchEvent()
                .putQuery(options.getQuery())
                .putCustomAttribute("okGoogle", options.isFromOkGoogle() + "");

        if (options.getFacets().isEmpty())
            return searchEvent;

        List<String> facets = new ArrayList<>(options.getFacets().keySet());
        Collections.sort(facets);
        return searchEvent
                .putCustomAttribute("facets", TextUtils.join(" - ", facets));
    }

    private HttpURLConnection buildRequest(String query) throws IOException {
        URL url = new URL(baseUrl.toString() + "?source=" + query);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("connection", "close");
        urlConnection.setRequestProperty(ACCEPT_ENCODING, GZIP);
        urlConnection.setRequestProperty("User-Agent", "Android Java/" + VersionUtils.getOsVersion());
        urlConnection.setRequestProperty("Referer", appName + " - " + VersionUtils.getAppVersion());
        return urlConnection;
    }

    private InputStream getInputStream(HttpURLConnection connection) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
        String contentEncoding = connection.getHeaderField(CONTENT_ENCODING);
        if (GZIP.equalsIgnoreCase(contentEncoding)) {
            return new GZIPInputStream(bis);
        }
        return bis;
    }

    private SearchResult parse(InputStream stream) {
        long now = System.currentTimeMillis();
        SearchResult searchResult = mapperUtil.read(stream);
        log.i("parse took " + (System.currentTimeMillis() - now) + "ms");
        return searchResult;
    }
}
