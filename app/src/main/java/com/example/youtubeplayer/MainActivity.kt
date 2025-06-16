package com.example.youtubeplayer
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.net.URL

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val image = findViewById<ImageView>(R.id.youtube_thumbnail)

        image.setOnClickListener {
            val dialog = FullscreenDialogFragment()
            dialog.show(supportFragmentManager, "fullscreenDialog")

        }

        loadThumbnail(imageView = image, videoId = "3DGdQ4gdqT4")
    }

    fun loadThumbnail(imageView: ImageView, videoId: String) {
        val url = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
        object : AsyncTask<Void, Void, Bitmap?>() {
            override fun doInBackground(vararg params: Void?): Bitmap? {
                return try {
                    val input = URL(url).openStream()
                    BitmapFactory.decodeStream(input)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            override fun onPostExecute(result: Bitmap?) {
                if (result != null) {
                    imageView.setImageBitmap(result)
                }
            }
        }.execute()
    }

}
