package com.leonkianoapps.leonk.app1

import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import com.android.volley.toolbox.HttpHeaderParser
import android.R.attr.data
import com.android.volley.Request
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


open class NetworkResponseRequest(
    method: Int, url: String, private val mListener: Response.Listener<NetworkResponse>,
    errorListener: Response.ErrorListener
) : Request<NetworkResponse>(method, url, errorListener) {

    constructor(
        url: String,
        listener: Response.Listener<NetworkResponse>,
        errorListener: Response.ErrorListener
    ) : this(Method.GET, url, listener, errorListener) {
    }

     override fun deliverResponse(response: NetworkResponse) {
        mListener.onResponse(response)
    }

     override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
    }

    companion object {

        fun parseToString(response: NetworkResponse): String {
            var parsed: String
            try {
                parsed =String(
                    response.data ?: ByteArray(0),
                    Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
            } catch (e: UnsupportedEncodingException) {
                parsed = String(response.data)
            }

            return parsed
        }
    }
}