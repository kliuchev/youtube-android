import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.widget.FrameLayout

class FullScreenClient(
    private val parent: ViewGroup,
    private val content: ViewGroup
) : WebChromeClient() {

    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        if (customView != null) {
            onHideCustomView()
            return
        }

        customView = view
        customViewCallback = callback

        view?.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        parent.addView(view)
        content.visibility = View.GONE
    }

    override fun onHideCustomView() {
        customView?.let {
            parent.removeView(it)
            customView = null
        }
        content.visibility = View.VISIBLE
        customViewCallback?.onCustomViewHidden()
    }
}
