package com.scowluga.android.dawn

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log


class ReceiverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver)
    }

//    override fun onResume() {
//        super.onResume()
//        val player = MediaPlayer.create(this, R.raw.alarm)
//        player.isLooping = true
//
//        // Setting up Alarm
//        findViewById<Button>(R.id.receiverButton).setOnClickListener {
//            player.start()
//        }
//
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
////            player.stop()
////            Toast.makeText(this, "REE", Toast.LENGTH_SHORT).show()
//            Log.d("TAG", "REE")
//
//        }
//    }


    override fun onPause() {
        super.onPause()
        disableForegroundDispatch(this, NfcAdapter.getDefaultAdapter(this))
    }

    override fun onResume() {
        super.onResume()

        // foreground dispatch should be enabled here, as onResume is the guaranteed place where app
        // is in the foreground
        enableForegroundDispatch(this, NfcAdapter.getDefaultAdapter(this))
        receiveMessageFromDevice(intent)
    }

    override fun onNewIntent(intent: Intent) {
        receiveMessageFromDevice(intent)
        Log.d("TAG", "P2")
    }

    private fun receiveMessageFromDevice(intent: Intent) {
        Log.d("TAG", "P1")
        val action = intent.action

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            with(parcelables) {
                val inNdefMessage = this[0] as NdefMessage
                val inNdefRecords = inNdefMessage.records
                val ndefRecord_0 = inNdefRecords[0]

                val inMessage = String(ndefRecord_0.payload)
                Log.d("TAG", inMessage)
            }
        }
    }

    private fun enableForegroundDispatch(activity: AppCompatActivity, adapter: NfcAdapter?) {

        // here we are setting up receiving activity for a foreground dispatch
        // thus if activity is already started it will take precedence over any other activity or app
        // with the same intent filters

        val intent = Intent(activity.applicationContext, activity.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(activity.applicationContext, 0, intent, 0)

        val filters = arrayOfNulls<IntentFilter>(1)
        val techList = arrayOf<Array<String>>()

        filters[0] = IntentFilter()
        with(filters[0]) {
            this?.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
            this?.addCategory(Intent.CATEGORY_DEFAULT)
            try {
                this?.addDataType("application/com.scowluga.android.dawn")
            } catch (ex: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("Check your MIME type")
            }
        }

        adapter?.enableForegroundDispatch(activity, pendingIntent, filters, techList)
    }

    private fun disableForegroundDispatch(activity: AppCompatActivity, adapter: NfcAdapter?) {
        adapter?.disableForegroundDispatch(activity)
    }

}
