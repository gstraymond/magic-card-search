package fr.gstraymond.network;

import android.content.Context;

import com.magic.card.search.commons.json.MapperUtil;
import com.magic.card.search.commons.log.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;

import fr.gstraymond.tools.VersionUtils;


public class ElasticSearchConnector<A> {
    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    private static final String GZIP = "gzip";
    private static final String ENCODING = "UTF-8";
    private static final String SEARCH_SERVER_HOST = "engine.magic-card-search.com:8080";
//	private static final String SEARCH_SERVER_HOST = "local-gsr:9200";

    private String appName;
    private MapperUtil<A> mapperUtil;

    private Log log = new Log(this);

    public ElasticSearchConnector (Context context, MapperUtil<A> mapperUtil) {
        this.appName = VersionUtils.getAppName(context);
        this.mapperUtil = mapperUtil;
    }

    public Result<A> connect(String path, String query) {
        HttpURLConnection connection = null;
        Result<A> result = null;
        try {
            log.d("query : %s", query);
            String q = URLEncoder.encode(query, ENCODING);
            URL url = new URL(String.format("http://%s/%s?source=%s", SEARCH_SERVER_HOST, path, q));
            log.d("full query : %s", url);
            connection = buildRequest(url);

            Long httpNow = System.currentTimeMillis();
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            Long httpDuration = System.currentTimeMillis() - httpNow;
            log.i("http took %s ms", httpDuration);

            Long parseNow = System.currentTimeMillis();
            A a = mapperUtil.read(new GZIPInputStream(bis));
            Long parseDuration = System.currentTimeMillis() - parseNow;
            log.i("parse took %s ms", parseDuration);

            result = new Result<>(a, httpDuration, parseDuration);
        } catch (SocketException e) {
            log.w("Socket exception: %s", e.getMessage());
        } catch (UnknownHostException e) {
            log.w("unknown host: %s", e.getMessage());
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("ETIMEDOUT")) {
                log.w("timeout:" + e.getMessage());
            } else {
                log.e("process", e);
            }
        } finally {
            if (connection != null) connection.disconnect();
        }
        return result;
    }

    private HttpURLConnection buildRequest(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("connection", "close");
        connection.setRequestProperty(ACCEPT_ENCODING, GZIP);
        connection.setRequestProperty("User-Agent", "Android Java/" + VersionUtils.getOsVersion());
        connection.setRequestProperty("Referer", appName + " - " + VersionUtils.getAppVersion());
        return connection;
    }
}
