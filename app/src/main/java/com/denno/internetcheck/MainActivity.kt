package com.denno.internetcheck

import android.app.Service
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var connectivityDisposable: Disposable? = null
    private var internetDisposable: Disposable? = null

    var context = this
    var connectivity : ConnectivityManager? = null
    var info : NetworkInfo? = null

    companion object {
        private val TAG = "ConnectivityCheck"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->


            connectivity = context.getSystemService(Service.CONNECTIVITY_SERVICE)

                    as ConnectivityManager

            if ( connectivity != null)
            {
                info = connectivity!!.activeNetworkInfo

                if (info != null)
                {
                    if (info!!.state == NetworkInfo.State.CONNECTED)
                    {
//                        Toast.makeText(context, "CONNECTED", Toast.LENGTH_LONG).show()
                        Snackbar.make(view, "CONNECTED", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()

                    }
                }
                else
                {
//                    Toast.makeText(context, "NOT CONNECTED", Toast.LENGTH_LONG).show()
                    Snackbar.make(view, "NOT CONNECTED", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }


//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onResume() {
        super.onResume()

        connectivityDisposable = ConnectivityCheck.observeNetworkConnectivity(applicationContext)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { connectivity ->
                Log.d(TAG, connectivity.toString())
                val state = connectivity.state()
                val name = connectivity.typeName()
                connectivity_status.text = String.format("state: %s, typeName: %s", state, name)
            }

        internetDisposable = ConnectivityCheck.observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isConnectedToInternet ->
                internet_status.text = isConnectedToInternet.toString()
            }
    }

    override fun onPause() {
        super.onPause()
        safelyDispose(connectivityDisposable)
        safelyDispose(internetDisposable)
    }

    private fun safelyDispose(disposable: Disposable?) {
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
