package com.yoavst.quickapps.news

import android.content.Context
import android.net.ConnectivityManager

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.yoavst.quickapps.R
import com.yoavst.quickapps.news.types.AuthResponse
import com.yoavst.quickapps.news.types.Entry

import org.scribe.builder.ServiceBuilder
import org.scribe.exceptions.OAuthConnectionException
import org.scribe.model.OAuthRequest
import org.scribe.model.Token
import org.scribe.model.Verb
import org.scribe.oauth.OAuthService

import java.util.ArrayList
import java.util.concurrent.TimeUnit
import com.yoavst.quickapps.NewsPrefManager
import kotlin.properties.Delegates
import com.yoavst.util.typeToken
import com.yoavst.mashov.AsyncJob

/**
 * Created by Yoav.
 */
public class DownloadManager(val context: Context) {
    private var service: OAuthService? = null
    private val prefs: NewsPrefManager by Delegates.lazy { NewsPrefManager(context) }
    private val listType = typeToken<ArrayList<Entry>>()
    private var isDownloadingNow = false

    synchronized public fun saveToken(token: Token) {
        val response = Gson().fromJson<AuthResponse>(token.getRawResponse(), javaClass<AuthResponse>())
        prefs.userId().put(response.getId()).apply()
        prefs.refreshToken().put(response.getRefreshToken()).apply()
        prefs.accessToken().put(response.getAccessToken()).apply()
        prefs.rawResponse().put(token.getRawResponse()).apply()
    }

    synchronized public fun getTokenFromPrefs(): Token? {
        if (prefs.accessToken().getOr("-1") != "-1") {
            try {
                return Token(prefs.accessToken().getOr(""), "", prefs.rawResponse().getOr(""))
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
        AsyncJob.doInBackground {
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
                    val request = OAuthRequest(Verb.GET, "https://cloud.feedly.com/v3/streams/contents?streamId=user/" + prefs.userId().getOr("") + "/category/global.all")
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
                                    prefs.feed().put(Gson().toJson(entries, listType))
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
        return if (prefs.feed().getOr("-1") != "-1") Gson().fromJson(prefs.feed().getOr("[]"), listType) else null
    }

    public fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.getActiveNetworkInfo()
        return activeNetworkInfo != null && activeNetworkInfo.isConnected()
    }

    public trait DownloadingCallback {
        public fun onFail(error: DownloadError)

        public fun onSuccess(entries: ArrayList<Entry>)
    }

    public enum class DownloadError {
        Login
        Internet
        Other
    }
}
