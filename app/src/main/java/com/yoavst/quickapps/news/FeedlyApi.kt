package com.yoavst.quickapps.news

import org.scribe.builder.api.DefaultApi20
import org.scribe.extractors.AccessTokenExtractor
import org.scribe.extractors.JsonTokenExtractor
import org.scribe.model.OAuthConfig
import org.scribe.model.Verb
import org.scribe.utils.OAuthEncoder

/**
 * Created by Yoav.
 */
public class FeedlyApi : DefaultApi20() {

    override fun getAccessTokenEndpoint(): String {
        return ACCESS_TOKEN_URL
    }

    override fun getAuthorizationUrl(config: OAuthConfig): String {
        return AUTHORIZE_URL.format(config.getApiKey(), OAuthEncoder.encode(config.getCallback()), config.getScope())
    }

    override fun getAccessTokenVerb(): Verb {
        return Verb.POST
    }

    override fun getAccessTokenExtractor(): AccessTokenExtractor {
        return JsonTokenExtractor()
    }

    companion object {
        public var AUTHORIZE_URL: String = "https://cloud.feedly.com/v3/auth/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=%s"
        public var ACCESS_TOKEN_URL: String = "https://cloud.feedly.com/v3/auth/token?grant_type=authorization_code"
        public val SCOPE: String = "https://cloud.feedly.com/subscriptions"
    }
}