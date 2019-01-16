# App1
App test for InterIntel Kenya
**The test is as follows**
Test
the app should have 3 modules
1. Core
- Should have a background service that can be started from a button click in either app1 or app2 below,
when started, the service should make 5 seconds scheduled network requests to https://httpbin.org/get and
Toast the `origin` from the response
2. app1
- have an activity with a button that can be clicked to show a notification
- Have a custom font that looks different from app2
3. app2
- have inputs of saving users into the database
- have a list view of displaying the users
- have a custom view named TimeElement that displays the current date and time
app1 should be compile-able to an apk named App1
app2 should be compile-able to an apk named App2
core should not be compile-able to an apk
app1 and 2 should have different launcher icons
app1 and 2 should have different launcher activities extending a base activity implemented in core

**SOLUTION APP1**
1. The Launcher for app1 is a splash screen that has a custom logo imageView and textView below it as a title of the app
-the imageView is set with an animation that rotates for 5 seconds in which it is the duration for the launcher to open the mainActivity is 6 seconds
and at the same time the title textView is fading in by setting the intial alpha to 0 and at the end of 5 seconds the alpha is set to 1, producing a fading in effect

2. On the MainActivity the notable features are the two buttons labelled "Show Notification" and "Do background service"  
  *-"Show notification" runs a method that has the notificationManger that handles showing the notification.
  *- There is a notication channel used and also a check since Notification channel is available from sdk 26
  
3. The Core. Here I am using a volley networking library that will to a GET string request that fetches the JSON data from https://httpbin.org/get
    and creating a JSONObject to get the contents of the string "origin"
    *- A  5 countdown timer method handles the schedulling  of the network request.
    *- The logic is, the "Do background service" button is tapped which immediatley launches the stringRequest, once the response is gotten,
    the count down method is run and after 5 seconds is done it will call the networking method and so on in an endless loop
    *- There is also a check to see if there is active internet
    
    **END OF SOLUTION APP1**

FOR THE SOLUTION OF APP 2 READ THE README FILE IN MY APP2 REPO HERE: https://github.com/LennyKiano/App2

//Happing Codding

   
