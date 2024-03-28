package com.example.youtubeexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONObject
import java.net.URL



class UIThreadHelper : Runnable
{
    private var video : String = ""
    constructor(video : String)
    {
        this.video = video
    }
    override fun run()
    {
        //Update the webView, STACKOVERFLOW HERE
        var web = MainActivity.getInstance().findViewById<WebView>(R.id.web)
        val settings = web.getSettings()
        settings.setJavaScriptEnabled(true)
        settings.setDomStorageEnabled(true)
        settings.setMinimumFontSize(10)
        settings.setLoadWithOverviewMode(true)
        settings.setUseWideViewPort(true)
        settings.setBuiltInZoomControls(true)
        settings.setDisplayZoomControls(false)
        web.setVerticalScrollBarEnabled(false)
        settings.setDomStorageEnabled(true)
        web.setWebViewClient(WebViewClient())
        var str = "https://www.youtube.com/watch?v=" + video
        web.loadUrl(str)




    }
}


//Create the Helper class as shown below:
class Helper : Runnable
{
    private var url : String = ""
    private var song : String = ""
    private var artist : String = ""

    constructor(url : String, song:String, artist:String)
    {
        this.url = url
        this.song = song
        this.artist = artist
    }

    override fun run()
    {

        //when  running start THIS will run
        val data = URL(url).readText()
        println(data)

        //this will parse the data , look for video and tittle id
        var json = JSONObject(data)
        //array of 50 items
        var items = json.getJSONArray("items") // this is the "items: [ ] part

        var titles = ArrayList<String>()
        var videos = ArrayList<String>()
        for (i in 0 until items.length())
        {
            var videoObject = items.getJSONObject(i)
            //val title = videoObject.getString("title")
            //val videoId = videoObject.getString("id")
            println(videoObject)
            var idDict = videoObject.getJSONObject("id")
            println(idDict)
            var videoId = idDict.getString("videoId")
            println(videoId)
            var snippetDict = videoObject.getJSONObject("snippet")
            var title =  snippetDict.getString("title")
            println(title)
            titles.add(title)
            videos.add(videoId)
        }
        for (i in 0 until items.length())
        {
            //Get the ith item
            var videoObject = items.getJSONObject(i)

            //Extracth the id Hashmap
            var idDict = videoObject.getJSONObject("id")

            //Get the videoid using videoId key
            var videoId = idDict.getString("videoId")
            println(videoId)
            //Get the snippet Hashmap, find snippet and then grab its title
            var snippetDict = videoObject.getJSONObject("snippet")
            //Get the title
            var title =  snippetDict.getString("title")

            //Add the titles to the lists
            titles.add(title)
            videos.add(videoId)
        }
        //you have to write code to search for the right song, now we're just hardcoding it



        //here
        var selected_video : String = ""
        var selected_title : String = ""
        selected_video = videos[33]
        selected_title = titles[33]

        var helper1 = UIThreadHelper(selected_video)
        MainActivity.getInstance().runOnUiThread(helper1)


    }

}

class MainActivity : AppCompatActivity() {

    companion object
    {
        private var instance : MainActivity? = null
        public fun getInstance() : MainActivity
        {
            return instance!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setContentView(R.layout.activity_main)

        var song = "Please Please Me"
        song = song.replace(" ", "+")
        var origSong = "Please Please Me"

        //Set the artist
        var artist = "The Beatles"
        artist = artist.replace(" ","+")
        var origArtist = "The Beatles"

        //Encode search for YouTube
        val keywords = artist + "+" +  song
        val max = 50


        //Set the youtube search
        //Set the youtube search
        val string = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=$keywords&order=viewCount&maxResults=$max&type=video&videoCategory=Music&key=AIzaSyDtzKWgA0ne39VD_-i0oJwCd4WOdFKZy4I"

        var helper = Helper(string, origSong, origArtist)
        var thread = Thread(helper)
        thread.start()


    }
}