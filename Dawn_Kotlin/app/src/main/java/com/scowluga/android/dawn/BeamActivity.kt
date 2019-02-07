package com.scowluga.android.dawn

import android.app.Activity
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NdefRecord.createMime
import android.nfc.NfcAdapter
import android.nfc.NfcAdapter.CreateNdefMessageCallback
import android.nfc.NfcEvent
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.scowluga.android.dawn.R
import java.nio.charset.Charset

class BeamActivity : AppCompatActivity(), NfcAdapter.CreateNdefMessageCallback {

    private var mNfcAdapter: NfcAdapter? = null
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beam)
        textView = findViewById(R.id.beamTV)
        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        // Register callback
        mNfcAdapter?.setNdefPushMessageCallback(this, this)
    }

    override fun createNdefMessage(event: NfcEvent): NdefMessage {
        val text = "Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis()
        return NdefMessage(
                arrayOf(
                        createMime("application/vnd.com.scowluga.android.dawn", text.toByteArray())
                )
                /**
                 * The Android Application Record (AAR) is commented out. When a device
                 * receives a push with an AAR in it, the application specified in the AAR
                 * is guaranteed to run. The AAR overrides the tag dispatch system.
                 * You can add it back in to guarantee that this
                 * activity starts when receiving a beamed message. For now, this code
                 * uses the tag dispatch system.
                 *///,NdefRecord.createApplicationRecord("com.example.android.beam")
        )
    }

    override fun onResume() {
        super.onResume()
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            processIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent)
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    private fun processIntent(intent: Intent) {
        textView = findViewById(R.id.beamTV)
        // only one message sent during the beam
        intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMsgs ->
            (rawMsgs[0] as NdefMessage).apply {
                // record 0 contains the MIME type, record 1 is the AAR, if present
                textView.text = String(records[0].payload)
            }
        }
    }
}