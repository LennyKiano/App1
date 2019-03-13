package com.leonkianoapps.leonk.app1

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.view.textclassifier.TextLinks
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.log

class MainActivity : AppCompatActivity() {


    //Notification Variables
    val notificationPendingInent = 1
    val notificationID = 101
    private val channelID = "com.leonkianoapps.leonk.app1"
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: Notification.Builder
    private val notificationMessage = "You have been notified by App1"

    private val requestTag1 = "requestTag1"
    private val requestTag2 = "requestTag2"
    private val jsonObjectRequest1 = "jsonObjectRequest1"

    private lateinit var requestQueue: RequestQueue

    private lateinit var timer: CountDownTimer


    //Networking Variable

    private val downloadURL = "https://httpbin.org/get"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Setting up ToolBar
        setSupportActionBar(main_toolBar)

        //Setting up Buttons
        showNotificationButton.setOnClickListener { showNotification() }

        doBackgroundServiceButton.setOnClickListener { doService() }


    }

    private fun doService() {


        //checking if there is active internet connection
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {  //There is internet connection

//            startNetworking()
            startNetworking2()

        } else {

            showSnack("Make sure you have active internet connection")

        }


    }

    private fun startNetworking2() {

        //get Status Code

        getStatusCode()

        //progress bar

        progress_circular.visibility = View.VISIBLE

        //using Volley Lib with JsonObjectRequest

        requestQueue = Volley.newRequestQueue(this@MainActivity)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,
            downloadURL, Response.Listener { response ->

                val serverResponse = response.getString("origin")  //getting the string from the Json Object

                progress_circular.visibility = View.INVISIBLE

                showToast(serverResponse)

                scheduledTimer()


            }
            , Response.ErrorListener {

                showToast("Error: Something Went Wrong")

                requestQueue.stop()

            }
        )


        jsonObjectRequest.tag = jsonObjectRequest1

        requestQueue.add(jsonObjectRequest)


    }

    private fun startNetworking() {

        //get Status Code

        getStatusCode()


        //progress bar

        progress_circular.visibility = View.VISIBLE

        //Using Volley Networking Lib

        requestQueue = Volley.newRequestQueue(this@MainActivity)

        val stringRequest = StringRequest(
            Request.Method.GET, downloadURL,

            Response.Listener { response ->

                try {

                    val jsonObject = JSONObject(response)   //creating JSON Object from server response

                    val serveResponse = jsonObject.getString("origin")  //getting the string


                    progress_circular.visibility = View.INVISIBLE

                    showToast(serveResponse)  //Toast of server response

                    scheduledTimer()   //start 5 second count down to do another network request


                } catch (e: JSONException) {
                    e.printStackTrace()
                }


            }, Response.ErrorListener {

                showToast("Error: something is wrong")
                requestQueue.stop()
            })

        stringRequest.tag = requestTag1

        requestQueue.add(stringRequest)


    }

    override fun onPause() {
        super.onPause()

        requestQueue.cancelAll(requestTag1)
        requestQueue.cancelAll(requestTag2)
        requestQueue.cancelAll(jsonObjectRequest1)

        timer.cancel()

    }

    override fun onStop() {
        super.onStop()

        requestQueue.cancelAll(requestTag1)

        requestQueue.cancelAll(requestTag2)

        requestQueue.cancelAll(jsonObjectRequest1)

        timer.cancel()


    }

    private fun getStatusCode() {


        val networkResponseRequest =
            NetworkResponseRequest(Request.Method.GET, downloadURL, Response.Listener { response ->

                val code = response.statusCode.toString()

                val stringResponse = NetworkResponseRequest.parseToString(response)

                Log.d("MainActivity Code", "Status code $code")

                Log.d("MainActivity stringRes", "response code $stringResponse")


            },
                Response.ErrorListener { }
            )

        requestQueue = Volley.newRequestQueue(this@MainActivity)

        networkResponseRequest.tag = requestTag2

        requestQueue.add(networkResponseRequest)


    }

    private fun showNotification() {


        val intent = Intent(applicationContext, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)// when the notification is tapped, it will bring the activity fourth to the user

        val pendingIntent = PendingIntent.getActivity(
            this@MainActivity,
            notificationPendingInent,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )



        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {            //Notification channel is available from sdk 26 hence the check

            notificationChannel =
                NotificationChannel(channelID, notificationMessage, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)  //if device has notification light
            notificationChannel.lightColor = Color.CYAN
            notificationChannel.enableVibration(false)


            builder = Notification.Builder(this@MainActivity, channelID)
                .setContentTitle("App1 Notification")
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.star_logo_blue)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.star_logo_blue))
                .setContentIntent(pendingIntent)


        } else {

            builder = Notification.Builder(this@MainActivity)
                .setContentTitle("App1 Notification")
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.star_logo_blue)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.star_logo_blue))
                .setContentIntent(pendingIntent)
        }

        notificationManager.notify(notificationID, builder.build())

        val messageInfo = "Check Your statusBar for the notification"

        showSnack(messageInfo)


    }

    private fun showToast(message: String) {

        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()

    }


    private fun showSnack(messageInfo: String) {

        val snack = Snackbar.make(coordinatorLayout, messageInfo, Snackbar.LENGTH_LONG)
            .setAction("OKAY", View.OnClickListener {
            })
        snack.show()

    }

    private fun scheduledTimer() {

        timer = object : CountDownTimer(5000, 1000) {
            override fun onFinish() {

                //do nothing

            }

            override fun onTick(p0: Long) {

                startNetworking2()
            }


        }.start()

    }
}
