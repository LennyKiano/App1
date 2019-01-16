package com.leonkianoapps.leonk.app1

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
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
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {


    //Notification Variables
    val notificationPendingInent = 1
    val notificationID = 101
    private val channelID = "com.leonkianoapps.leonk.app1"
    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private val notificationMessage = "You have been notified by App1"

    //Networking Variable

    val downloadURL = "https://httpbin.org/get"



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

            startNetworking()

        } else {

            showSnack("Make sure you have active internet connection")

        }


    }

    private fun startNetworking() {

        //progress bar

        progress_circular.visibility = View.VISIBLE

        //Using Volley Networking Lib

        val requestQueue = Volley.newRequestQueue(this@MainActivity)

        val stringRequest = StringRequest(
            Request.Method.GET, downloadURL,

            Response.Listener { response ->

                try {

                    val jsonObject = JSONObject(response)   //creating JSON Object from server response

                    val serveResponse = jsonObject.getString("origin")  //getting the string


                    progress_circular.visibility = View.INVISIBLE

                    Toast.makeText(applicationContext,serveResponse,Toast.LENGTH_LONG).show()  //Toast of server response

                    scheduledTimer()   //start 5 second count down to do another network request


                } catch (e: JSONException) {
                    e.printStackTrace()
                }


            }, Response.ErrorListener {

                Toast.makeText(applicationContext, "Error something is wrong", Toast.LENGTH_LONG).show()
                requestQueue.stop()
            })

        requestQueue.add(stringRequest)


    }

    private fun showNotification() {

        val intent = Intent(applicationContext,MainActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)// when the notification is tapped, it will bring the activity fourth to the user

        val pendingIntent = PendingIntent.getActivity(this@MainActivity,notificationPendingInent,intent,PendingIntent.FLAG_UPDATE_CURRENT)



        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {            //Notification channel is available from sdk 26 hence the check

            notificationChannel = NotificationChannel(channelID,notificationMessage,NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)  //if device has notification light
            notificationChannel.lightColor = Color.CYAN
            notificationChannel.enableVibration(false)


            builder = Notification.Builder(this@MainActivity, channelID)
                .setContentTitle("App1 Notification")
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.star_logo_blue)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.star_logo_blue))
                .setContentIntent(pendingIntent)


        }else{

            builder = Notification.Builder(this@MainActivity)
                .setContentTitle("App1 Notification")
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.star_logo_blue)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.star_logo_blue))
                .setContentIntent(pendingIntent)
        }

        notificationManager.notify(notificationID,builder.build())

        val messageInfo = "Check Your statusBar for the notification"

        showSnack(messageInfo)


    }


    private fun showSnack(messageInfo:String){

        val snack = Snackbar.make(coordinatorLayout,messageInfo,Snackbar.LENGTH_LONG)
            .setAction("OKAY", View.OnClickListener {
        })
        snack.show()

    }

    private fun scheduledTimer(){



        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                //do nothing
            }

            override fun onFinish() {

               startNetworking()


            }
        }.start()

    }
}
