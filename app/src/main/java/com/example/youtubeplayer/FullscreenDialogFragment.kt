package com.example.youtubeplayer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.DialogFragment

class FullscreenDialogFragment: DialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.dialog_fullscreen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val videoId = "3DGdQ4gdqT4"

        val webView = view.findViewById<WebView>(R.id.web_view)

        webView.settings.javaScriptEnabled = true

        webView.addJavascriptInterface(JSBridge(object : JSBridge.JSBridgeListener {
            override fun onProgress(current: Double, total: Double, buffered: Double) {
                Log.d("JSBridge", "Progress: $current / $total ($buffered buffered)")
            }

            override fun onReady() {
                Log.d("JSBridge", "Player is ready")
            }

            override fun onStateChange(state: Int) {
                Log.d("JSBridge", "State changed: $state")
            }

            override fun onError(code: Int, message: String) {
                Log.e("JSBridge", "Error $code: $message")
            }
        }), "AndroidInterface")

        webView.settings.mediaPlaybackRequiresUserGesture = false


        val controlsEnabled = true

        val html = """
            <html>
              <head>
                <meta
                  name="viewport"
                  content="width=device-width, initial-scale=1.0, maximum-scale=1.0"
                />
                <style>
                  html,
                  body {
                    margin: 0;
                    padding: 0;
                    height: 100%;
                    background-color: black;
                  }
                  #player {
                    width: 100%;
                    height: 100%;
                  }
                </style>
              </head>
              <body style="margin: 0">
                <div id="player"></div>

                <script>
                  var tag = document.createElement('script');

                  tag.src = "https://www.youtube.com/iframe_api";
                  var firstScriptTag = document.getElementsByTagName('script')[0];
                  firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                  var player;
                  var progressIntervalId;

                  function startProgressInterval() {
                    if (progressIntervalId) clearInterval(progressIntervalId)
                    progressIntervalId = setInterval(function() {
                        if (player && player.getCurrentTime) {
                            const currentTime = player.getCurrentTime();
                            const duration = player.getDuration();
                            const buffered = player.getVideoLoadedFraction();

                            window.AndroidInterface.postMessage(JSON.stringify({
                                type: "onProgress",
                                current: currentTime,
                                total: duration,
                                buffered: buffered,
                            }));
                        }
                    }, 1000);
                  }

                  function onYouTubeIframeAPIReady() {
                    player = new YT.Player('player', {
                      videoId: '${videoId}',
                      playerVars: {
                        rel: 0, // deprecated
                        playsinline: 1, // a iOS specific var - allows to play video inline
                        controls: ${if (controlsEnabled) 1 else 0 },
                        autoplay: 1, 
                        modestbranding: 1, // deprecated
                        fs: 1 // full screen button disabled
                      },
                      events: {
                        onReady: function(event) {
                            window.AndroidInterface.postMessage(JSON.stringify({ type: 'onReady' }));
                            startProgressInterval()
                        },
                        onStateChange: function(event) {
                            window.AndroidInterface.postMessage(JSON.stringify({
                                type: "onStateChange",
                                state: event.data
                            }));
                        },
                        onError: function(event) {
                            let errorMessages = {
                                '2': "Invalid parameter",
                                '5': "HTML5 player error",
                                '100': "Video not found or removed",
                                '101': "Embedding not allowed",
                                '150': "Embedding not allowed (same as 101)"
                            };

                            let errorCode = event.data;
                            let errorText = errorMessages[errorCode] || "Unknown error";

                            window.AndroidInterface.postMessage(JSON.stringify({
                                type: "onError",
                                error: errorCode,
                                message: `Error occurred` + errorText
                            }));
                        }
                      }
                    });
                  }
                </script>
              </body>
            </html>
        """
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)

        /**
         * How to pause/play/seek and stop video
         *
         * Play:
         * webView.evaluateJavascript("player.playVideo();", {})
         *
         * Pause:
         * webView.evaluateJavascript("player.pauseVideo();", {})
         *
         * Stop:
         * webView.evaluateJavascript("player.stop();", {})
         *
         * Seek: - The first parameters is seconds the second is ability to seek to unbuffered part
         * webView.evaluateJavascript("player.seekTo(123, true);", {})
         */


    }
}

