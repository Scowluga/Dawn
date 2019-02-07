package com.scowluga.android.dawn

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.tech.NfcA
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import me.rishabhkhanna.customtogglebutton.CustomToggleButton


class MainActivity : AppCompatActivity() {

    fun displayError(a: Activity) {
        Toast.makeText(a, "Please Connect to Dawn Device", Toast.LENGTH_SHORT).show()
    }

    lateinit var refreshBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // set status bar transparent for parallax toolbar
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

        // Refresh Next Alarm
        val nextTV: TextView = findViewById(R.id.nextTV)
        refreshBtn = findViewById(R.id.refreshDisplay)
        refreshBtn.setOnClickListener(View.OnClickListener {
            val nextAlarm = Settings.System.getString(getContentResolver(),
                    Settings.System.NEXT_ALARM_FORMATTED)
            if (nextAlarm.isEmpty())
                nextTV.text = "Next Alarm: N/A"
            else
                nextTV.text = "Next Alarm: $nextAlarm"
        })
        refreshBtn.performClick()

        // Toggle Button
        val toggle: CustomToggleButton = findViewById(R.id.toggleButton)
        toggle.setOnCheckedChangeListener({ buttonView, isChecked ->
            refreshBtn.performClick()
            if (isChecked) { // ON
                val layout: LinearLayout = findViewById(R.id.alarm_ll)
                (0 until layout.childCount)
                        .map { layout.getChildAt(it) }
                        .forEach {it.isEnabled = true}
                displayError(this)
            } else { // OFF
                val layout: LinearLayout = findViewById(R.id.alarm_ll)
                (0 until layout.childCount)
                        .map {layout.getChildAt(it)}
                        .forEach {it.isEnabled = false}

                Toast.makeText(this, "Blind Opening Cancelled", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        refreshBtn.performClick()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when {
            item.itemId == R.id.action_sender -> {
                // Open Sender Activity
                val intent = Intent(this@MainActivity, SenderActivity::class.java)
                startActivity(intent)
            }
            item.itemId == R.id.action_receiver -> {
                // Start Receiver Activity
                val intent = Intent(this@MainActivity, ReceiverActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }
}
