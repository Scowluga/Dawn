package com.scowluga.android.dawn

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

class SenderActivity : AppCompatActivity(), OutcomingNfcManager.NfcActivity {
    override fun getOutcomingMessage(): String =
            "WHEE"


    override fun signalResult() {
        // this will be triggered when NFC message is sent to a device.
        // should be triggered on UI thread. We specify it explicitly
        // cause onNdefPushComplete is called from the Binder thread
        runOnUiThread {
            Toast.makeText(this, "DONE", Toast.LENGTH_SHORT).show()
        }
    }

    var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sender)

        // Setting up NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_SHORT).show()
            finish()
        }

        if (!nfcAdapter?.isEnabled!!) {
            Toast.makeText(this, "NFC is currently disabled, please turn on", Toast.LENGTH_SHORT).show()
            finish()
        }

        val outcomingNfcCallback = OutcomingNfcManager(this)
        this.nfcAdapter?.setOnNdefPushCompleteCallback(outcomingNfcCallback, this)
        this.nfcAdapter?.setNdefPushMessageCallback(outcomingNfcCallback, this)    }
}
