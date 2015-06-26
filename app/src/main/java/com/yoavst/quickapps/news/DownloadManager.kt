package com.yoavst.quickapps.news

import android.content.Context
import android.net.ConnectivityManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.yoavst.kotlin.async
import com.yoavst.quickapps.R
import com.yoavst.quickapps.news.types.AuthResponse
import com.yoavst.quickapps.news.types.Entry
import com.yoavst.quickapps.tools.*
import org.scribe.builder.ServiceBuilder
import org.scribe.exceptions.OAuthConnectionException
import org.scribe.model.OAuthRequest
import org.scribe.model.Token
import org.scribe.model.Verb
import org.scribe.oauth.OAuthService
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * Created by Yoav.
 */
public class DownloadManager(val context: Context) {
    private var service: OAuthService? = null
    private val listType = typeToken<ArrayList<Entry>>()
    private var isDownloadingNow = false

    synchronized public fun saveToken(token: Token) {
        val response = Gson().fromJson<AuthResponse>(token.getRawResponse(), javaClass<AuthResponse>())
        context.userId = response.getId()
        context.refreshToken = response.getRefreshToken()
        context.accessToken = response.getAccessToken()
        context.rawResponse = token.getRawResponse()
    }

    synchronized public fun getTokenFromPrefs(): Token? {
        if (context.accessToken.isNotEmpty()) {
            try {
                return Token(context.accessToken, "", context.rawResponse)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return null
    }

    synchronized public fun getService(): OAuthService {
        if (service == null) {
            service = ServiceBuilder().provider(javaClass<FeedlyApi>())
                    .apiKey(context.getResources().getString(R.string.client_id))
                    .apiSecret(context.getResources().getString(R.string.client_secret))
                    .callback("http://localhost:8080")
                    .scope(FeedlyApi.SCOPE)
                    .build()
        }
        return service!!
    }

    public fun download(callback: DownloadingCallback?) {
        async {
            if (!isNetworkAvailable()) {
                callback?.onFail(DownloadError.Internet)
            } else {
                var shouldStop = false
                val accessToken = getTokenFromPrefs()
                if (accessToken == null) {
                    callback?.onFail(DownloadError.Login)
                } else if (!isDownloadingNow) {
                    isDownloadingNow = true
                    // Init the service
                    getService()
                    val request = OAuthRequest(Verb.GET, "https://cloud.feedly.com/v3/streams/contents?streamId=user/" + context.userId + "/category/global.all")
                    request.addHeader("Authorization", accessToken.getToken())
                    request.setConnectTimeout(10, TimeUnit.SECONDS)
                    service!!.signRequest(accessToken, request)
                    try {
                        val response = request.send()
                        var jsonObject: JsonObject? = JsonObject()
                        try {
                            jsonObject = JsonParser().parse(response.getBody()).getAsJsonObject()
                            if (jsonObject == null) throw JsonParseException("Object is null")
                        } catch (exception: JsonParseException) {
                            callback?.onFail(DownloadError.Other)
                            shouldStop = true
                        }
                        if (!shouldStop) {
                            var items: String = ""
                            try {
                                items = jsonObject!!.get("items").toString()
                            } catch (e: Exception) {
                                callback!!.onFail(DownloadError.Other)
                                shouldStop = true
                            }
                            if (!shouldStop) {
                                try {
                                    val entries = Gson().fromJson<ArrayList<Entry>>(items, listType)
                                    callback?.onSuccess(entries)
                                    context.feed = Gson().toJson(entries, listType)
                                } catch (exception: JsonParseException) {
                                    callback?.onFail(DownloadError.Other)
                                }
                            }
                        }

                    } catch (exception: OAuthConnectionException) {
                        exception.printStackTrace()
                        callback?.onFail(DownloadError.Internet)
                    } finally {
                        isDownloadingNow = false
                    }
                }
            }
        }
    }

    public fun getFeedFromPrefs(): ArrayList<Entry>? {
        return if (context.feed.isNotEmpty()) Gson().fromJson<ArrayList<Entry>>(context.feed, listType) else null
    }

    public fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.getActiveNetworkInfo()
        return activeNetworkInfo != null && activeNetworkInfo.isConnected()
    }

    public interface DownloadingCallback {
        public fun onFail(error: DownloadError)

        public fun onSuccess(entries: ArrayList<Entry>)
    }

    public enum class DownloadError {
        Login,
        Internet,
        Other
    }
}
