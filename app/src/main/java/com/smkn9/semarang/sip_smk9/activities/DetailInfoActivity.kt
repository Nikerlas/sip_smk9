package com.smkn9.semarang.sip_smk9.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.helper.Constant
import kotlinx.android.synthetic.main.activity_detail_info.*
import org.jetbrains.anko.toast

class DetailInfoActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_info)

        val wvInfo = wv_info

        wvInfo.settings.javaScriptEnabled = true
        wvInfo.settings.domStorageEnabled = true
        val urlInfo = intent.getStringExtra(Constant.BUNDLE_DETAIL)



        wvInfo.webViewClient = object : WebViewClient() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
//                view?.loadUrl(urlInfo)
//
//                return true
                if(URLUtil.isNetworkUrl(request?.url.toString()))
                {
                    return false
                }
                try
                {
                    val shareIntent= Intent()
                    shareIntent.action= Intent.ACTION_VIEW
                    shareIntent.data= Uri.parse(request?.url.toString())
                    startActivity(shareIntent)
                }
                catch(e: ActivityNotFoundException)
                {
                    toast("Appropriate app not found")
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                pb_book.visibility = View.GONE
            }
        }
        if (urlInfo != null) {
            wvInfo.loadUrl(urlInfo)
        }

        wvInfo.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            //checking Runtime permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    //Do this, if permission granted
                    downloadDialog(url, userAgent, contentDisposition, mimetype)
                } else {
                    //Do this, if there is no permission
                    ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            1
                    )
                }
            } else {
                //Code for devices below API 23 or Marshmallow
                downloadDialog(url, userAgent, contentDisposition, mimetype)
            }
        }

    }

    fun downloadDialog(url:String,userAgent:String,contentDisposition:String,mimetype:String) {
        //getting file name from url
        val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)
        //Alertdialog
        val builder = AlertDialog.Builder(this)
        //title for AlertDialog
        builder.setTitle("Download")
        //message of AlertDialog
        builder.setMessage("Do you want to save $filename")
        //if YES button clicks
        builder.setPositiveButton("Yes") { dialog, which ->
            //DownloadManager.Request created with url.
            val request = DownloadManager.Request(Uri.parse(url))
            //cookie
            val cookie = CookieManager.getInstance().getCookie(url)
            //Add cookie and User-Agent to request
            request.addRequestHeader("Cookie", cookie)
            request.addRequestHeader("User-Agent", userAgent)
            //file scanned by MediaScannar
            request.allowScanningByMediaScanner()
            //Download is visible and its progress, after completion too.
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            //DownloadManager created
            val downloadmanager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            //Saving file in Download folder
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename)
            //download enqued
            downloadmanager.enqueue(request)
        }
        //If Cancel button clicks
        builder.setNegativeButton("Cancel")
        {dialog, which ->
            //cancel the dialog if Cancel clicks
            dialog.cancel()
        }
        val dialog: AlertDialog = builder.create()
        //alertdialog shows
        dialog.show()
    }
}
