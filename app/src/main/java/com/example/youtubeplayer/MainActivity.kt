package com.example.youtubeplayer
import FullScreenClient
import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var linearLayout: LinearLayout
    private lateinit var contentLayout: LinearLayout
    private lateinit var webView: WebView


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayout = findViewById(R.id.linearLayout)
        contentLayout = findViewById(R.id.contentLayout)
        webView = findViewById(R.id.webView)

        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = width * 9 / 16

        webView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            height
        )

        webView.settings.javaScriptEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false

        webView.webChromeClient = FullScreenClient(linearLayout, contentLayout)

        val videoId = "3DGdQ4gdqT4"
        val html = """
            <html>
            <body style="margin:0">
            <iframe width="100%" height="100%" src="https://www.youtube.com/embed/$videoId?autoplay=0&controls=1&fs=1" frameborder="0" allowfullscreen></iframe>
            </body>
            </html>
        """
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

}
