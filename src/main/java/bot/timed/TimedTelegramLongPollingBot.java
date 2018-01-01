package bot.timed;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.LongPollingBot;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by Luca Mosetti on 2017
 */
public abstract class TimedTelegramLongPollingBot extends TimedDefaultAbsSender implements LongPollingBot {

    public TimedTelegramLongPollingBot(long maxMessagesPerMinute) {
        this(ApiContext.getInstance(DefaultBotOptions.class), maxMessagesPerMinute);
    }

    public TimedTelegramLongPollingBot(DefaultBotOptions options, long maxMessagesPerMinute) {
        super(options, maxMessagesPerMinute);
    }

    public void clearWebhook() throws TelegramApiRequestException {
        try {
            CloseableHttpClient httpclient = HttpClientBuilder.create().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            Throwable var2 = null;

            try {
                String url = this.getOptions().getBaseUrl() + this.getBotToken() + "/" + "setwebhook";
                HttpGet httpGet = new HttpGet(url);
                httpGet.setConfig(this.getOptions().getRequestConfig());
                CloseableHttpResponse response = httpclient.execute(httpGet);
                Throwable var6 = null;

                try {
                    HttpEntity ht = response.getEntity();
                    BufferedHttpEntity buf = new BufferedHttpEntity(ht);
                    String responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(responseContent);
                    if (!jsonObject.getBoolean("ok")) {
                        throw new TelegramApiRequestException("Error removing old webhook", jsonObject);
                    }
                } catch (Throwable var36) {
                    var6 = var36;
                    throw var36;
                } finally {
                    if (response != null) {
                        if (var6 != null) {
                            try {
                                response.close();
                            } catch (Throwable var35) {
                                var6.addSuppressed(var35);
                            }
                        } else {
                            response.close();
                        }
                    }

                }
            } catch (Throwable var38) {
                var2 = var38;
                throw var38;
            } finally {
                if (httpclient != null) {
                    if (var2 != null) {
                        try {
                            httpclient.close();
                        } catch (Throwable var34) {
                            var2.addSuppressed(var34);
                        }
                    } else {
                        httpclient.close();
                    }
                }
            }
        } catch (JSONException var40) {
            throw new TelegramApiRequestException("Error deserializing setWebhook method response", var40);
        } catch (IOException var41) {
            throw new TelegramApiRequestException("Error executing setWebook method", var41);
        }
    }
}
