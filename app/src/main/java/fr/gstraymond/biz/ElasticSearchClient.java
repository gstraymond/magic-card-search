package fr.gstraymond.biz;

import android.widget.ProgressBar;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.db.HistoryDataSource;
import fr.gstraymond.search.model.request.Request;
import fr.gstraymond.search.model.response.SearchResult;
import fr.gstraymond.tools.DisplaySizeUtil;
import fr.gstraymond.tools.Log;
import fr.gstraymond.tools.MapperUtil;
import fr.gstraymond.tools.VersionUtils;

public class ElasticSearchClient {

    private static final String CONTENT_ENCODING = "Content-Encoding";
    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    private static final String GZIP = "gzip";
    private static final String ENCODING = "UTF-8";

    private URL baseUrl;
    private CustomApplication application;
    private MapperUtil<SearchResult> mapperUtil;
    private String appVersion;
    private String osVersion;
    private Log log = new Log(this);

    public ElasticSearchClient(URL baseUrl, ObjectMapper objectMapper, CustomApplication application) {
        this.baseUrl = baseUrl;
        this.application = application;
        this.mapperUtil = new MapperUtil<>(objectMapper, SearchResult.class);
        this.appVersion = VersionUtils.getAppVersion(application);
        this.osVersion = VersionUtils.getOsVersion();
    }

    public SearchResult process(SearchOptions options, ProgressBar progressBar) {
        Request request = new Request(options);
        String queryAsJson = mapperUtil.asJsonString(request);
        log.d("query as json : " + queryAsJson);

        HttpURLConnection urlConnection = null;
        SearchResult searchResult = null;
        try {
            String query = URLEncoder.encode(queryAsJson, ENCODING);
            urlConnection = buildRequest(query);
            if (urlConnection == null) return null;

            long now = System.currentTimeMillis();
            String fileSize = getResponseSize(urlConnection);
            log.i("downloaded " + fileSize + " in " + (System.currentTimeMillis() - now) + "ms");

            progressBar.setProgress(33);

            // historique
            if (options.isAddToHistory()) {
                log.w("add to history : " + options);
                new HistoryDataSource(application).appendHistory(options);
            }

            searchResult = parse(getInputStream(urlConnection), progressBar);
        } catch (IOException e) {
            log.e("process", e);
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return searchResult;
    }

    private HttpURLConnection buildRequest(String query) {
        try {
            URL url = new URL(baseUrl.toString() + "?source=" + query);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty(ACCEPT_ENCODING, GZIP);
            urlConnection.setRequestProperty("User-Agent", "Android Java/" + osVersion);
            urlConnection.setRequestProperty("Referer", "Magic Card Search - " + appVersion);
            return urlConnection;
        } catch (IOException e) {
            log.e("buildRequest", e);
        }
        return null;
    }

    private InputStream getInputStream(HttpURLConnection connection) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
        String contentEncoding = connection.getHeaderField(CONTENT_ENCODING);
        if (contentEncoding != null && GZIP.equalsIgnoreCase(contentEncoding)) {
            return new GZIPInputStream(bis);
        }
        return bis;
    }

    private String getResponseSize(HttpURLConnection urlConnection) {
        String contentLength = urlConnection.getHeaderField("Content-Length");
        long length = 0; // Long.parseLong(contentLength); //FIXME
        return DisplaySizeUtil.getFileSize(length);
    }

    private SearchResult parse(InputStream stream, ProgressBar progressBar) {
        long now = System.currentTimeMillis();
        SearchResult searchResult = mapperUtil.read(stream);
        log.i("parse took " + (System.currentTimeMillis() - now) + "ms");
        progressBar.setProgress(66);
        return searchResult;
    }
}
