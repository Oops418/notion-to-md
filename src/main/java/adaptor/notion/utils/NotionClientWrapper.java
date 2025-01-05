package adaptor.notion.utils;

import notion.api.v1.NotionClient;
import notion.api.v1.http.NotionHttpClient;
import notion.api.v1.json.NotionJsonSerializer;
import notion.api.v1.logging.NotionLogger;

import java.io.Closeable;

public class NotionClientWrapper implements Closeable {
    private String token;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private NotionHttpClient httpClient;
    private NotionLogger logger;
    private NotionJsonSerializer jsonSerializer;
    private String baseUrl;
    private volatile NotionClient notionClient;

    private NotionClientWrapper() {

    }

    public static Builder builder() {
        return new Builder();
    }

    public NotionClient getClient() {
        if (notionClient == null) {
            synchronized (this) {
                if (notionClient == null) {
                    notionClient = createClient();
                    if (httpClient != null) {
                        notionClient.setHttpClient(httpClient);
                    }
                    if (jsonSerializer != null) {
                        notionClient.setJsonSerializer(jsonSerializer);
                    }
                    if (baseUrl != null) {
                        notionClient.setBaseUrl(baseUrl);
                    }
                    if (logger != null) {
                        notionClient.setLogger(logger);
                    }
                }
            }
        }
        return notionClient;
    }

    private NotionClient createClient() {
        if (token != null) {
            return new NotionClient(token);
        } else if (clientId != null && clientSecret != null && redirectUri != null) {
            return new NotionClient(clientId, clientSecret, redirectUri);
        }
        throw new IllegalStateException("Either token or OAuth credentials must be set");
    }

    @Override
    public void close() {
        if (notionClient != null) {
            notionClient.close();
        }
    }

    public static class Builder {
        private final NotionClientWrapper wrapper;

        private Builder() {
            wrapper = new NotionClientWrapper();
        }

        public Builder token(String token) {
            wrapper.token = token;
            return this;
        }

        public Builder clientId(String clientId) {
            wrapper.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            wrapper.clientSecret = clientSecret;
            return this;
        }

        public Builder redirectUri(String redirectUri) {
            wrapper.redirectUri = redirectUri;
            return this;
        }

        public Builder httpClient(NotionHttpClient httpClient) {
            wrapper.httpClient = httpClient;
            return this;
        }

        public Builder logger(NotionLogger logger) {
            wrapper.logger = logger;
            return this;
        }

        public Builder jsonSerializer(NotionJsonSerializer jsonSerializer) {
            wrapper.jsonSerializer = jsonSerializer;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            wrapper.baseUrl = baseUrl;
            return this;
        }

        public NotionClientWrapper build() {
            return wrapper;
        }
    }
}