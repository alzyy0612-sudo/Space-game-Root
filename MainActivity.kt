package com.spacegame.rootx1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup

class MainActivity : Activity() {
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(24), dp(24), dp(24))
            setBackgroundColor(Color.rgb(8, 13, 27))
        }
        val title = TextView(this).apply {
            text = "SPACE GAME ROOT X1"
            textSize = 26f
            setTextColor(Color.rgb(66, 232, 255))
            gravity = Gravity.CENTER
        }
        val subtitle = TextView(this).apply {
            text = "Floating Popup + Gaming Dashboard\nKernelSU / Root optional"
            textSize = 15f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, dp(24))
        }
        val rootStatus = TextView(this).apply {
            text = "Status root: memeriksa..."
            textSize = 14f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }
        val start = Button(this).apply {
            text = "AKTIFKAN FLOATING POPUP"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    val i = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName"))
                    startActivity(i)
                } else {
                    startForegroundService(Intent(this@MainActivity, FloatingService::class.java))
                }
            }
        }
        val stop = Button(this).apply {
            text = "MATIKAN POPUP"
            setOnClickListener { stopService(Intent(this@MainActivity, FloatingService::class.java)) }
        }
        val note = TextView(this).apply {
            text = "Mode performa memakai Android Game Mode jika tersedia. Tidak mengubah voltase/frekuensi dan tidak mematikan thermal protection."
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, dp(22), 0, 0)
        }
        layout.addView(title, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, -2))
        layout.addView(subtitle)
        layout.addView(rootStatus)
        layout.addView(start, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, -2))
        layout.addView(stop, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, -2))
        layout.addView(note)
        setContentView(layout)
        Thread {
            val rooted = try {
                val p = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
                val result = p.inputStream.bufferedReader().readText()
                p.waitFor() == 0 && result.contains("uid=0")
            } catch (_: Exception) { false }
            runOnUiThread { rootStatus.text = if (rooted) "Status root: TERDETEKSI" else "Status root: tidak terdeteksi / belum diberi izin" }
        }.start()
    }
}
