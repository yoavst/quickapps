package com.yoavst.quickapps.news;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;

/**
 * Created by Yoav.
 */
public class FeedlyApi extends DefaultApi20 {
	public static String AUTHORIZE_URL = "https://cloud.feedly.com/v3/auth/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=%s";
	public static String ACCESS_TOKEN_URL = "https://cloud.feedly.com/v3/auth/token?grant_type=authorization_code";
	public static final String SCOPE = "https://cloud.feedly.com/subscriptions";

	@Override
	public String getAccessTokenEndpoint() {
		return ACCESS_TOKEN_URL;
	}

	@Override
	public String getAuthorizationUrl(OAuthConfig config) {
		return String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()), config.getScope());
	}

	@Override
	public Verb getAccessTokenVerb() {
		return Verb.POST;
	}

	@Override
	public AccessTokenExtractor getAccessTokenExtractor() {
		return new JsonTokenExtractor();
	}
}
