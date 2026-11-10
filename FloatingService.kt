package com.spacegame.rootx1

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.*
import android.widget.*

class FloatingService : Service() {
    private lateinit var wm: WindowManager
    private var bubble: View? = null
    private var panel: View? = null
    private val channelId = "space_game_root_x1"

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onCreate() {
        super.onCreate()
        createNotification()
        startForeground(7, notification())
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        if (Settings.canDrawOverlays(this)) showBubble()
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            val ch = NotificationChannel(channelId, "SPACE GAME Popup", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(ch)
        }
    }

    private fun notification(): Notification {
        val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        return if (Build.VERSION.SDK_INT >= 26)
            Notification.Builder(this, channelId).setContentTitle("SPACE GAME ROOT X1")
                .setContentText("Floating gaming panel aktif").setSmallIcon(android.R.drawable.ic_menu_view)
                .setContentIntent(pi).build()
        else @Suppress("DEPRECATION") Notification.Builder(this)
            .setContentTitle("SPACE GAME ROOT X1").setContentText("Floating gaming panel aktif")
            .setSmallIcon(android.R.drawable.ic_menu_view).setContentIntent(pi).build()
    }

    private fun params(w: Int, h: Int): WindowManager.LayoutParams {
        val type = if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                   else WindowManager.LayoutParams.TYPE_PHONE
        return WindowManager.LayoutParams(w, h, type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT).apply { gravity = Gravity.TOP or Gravity.START; x = dp(18); y = dp(180) }
    }

    private fun background(color: Int, radius: Int = 18): GradientDrawable =
        GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat(); setStroke(dp(1), Color.rgb(66,232,255)) }

    private fun showBubble() {
        if (bubble != null) return
        val b = TextView(this).apply {
            text = "SG"
            textSize = 15f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            background = background(Color.rgb(66,232,255), 40)
            setOnClickListener { if (panel == null) showPanel() else hidePanel() }
        }
        bubble = b
        val p = params(dp(54), dp(54)).apply { flags = flags or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL }
        wm.addView(b, p)
        b.setOnTouchListener(object : View.OnTouchListener {
            var sx = 0f; var sy = 0f; var ox = 0; var oy = 0; var moved = false
            override fun onTouch(v: View, e: MotionEvent): Boolean {
                val lp = v.layoutParams as WindowManager.LayoutParams
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> { sx=e.rawX; sy=e.rawY; ox=lp.x; oy=lp.y; moved=false; return true }
                    MotionEvent.ACTION_MOVE -> {
                        val dx=(e.rawX-sx).toInt(); val dy=(e.rawY-sy).toInt()
                        if (kotlin.math.abs(dx)+kotlin.math.abs(dy)>dp(5)) moved=true
                        lp.x=ox+dx; lp.y=oy+dy; wm.updateViewLayout(v,lp); return true
                    }
                    MotionEvent.ACTION_UP -> { if (!moved) { if (panel == null) showPanel() else hidePanel() }; return true }
                }
                return false
            }
        })
    }

    private fun hidePanel() {
        panel?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        panel = null
    }

    private fun showPanel() {
        if (panel != null) return
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = background(Color.rgb(10, 18, 38), 20)
        }
        val heading = TextView(this).apply {
            text = "SPACE GAME  •  X1"
            textSize = 18f; setTextColor(Color.rgb(66,232,255)); gravity = Gravity.CENTER
        }
        box.addView(heading)
        box.addView(label("Pilih game:"))
        val games = listOf("Free Fire Global" to "com.dts.freefireth", "Free Fire MAX" to "com.dts.freefiremax")
        val spinner = Spinner(this)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, games.map { it.first })
        box.addView(spinner)
        box.addView(actionButton("⚡ PERFORMANCE", Color.rgb(66,232,255)) {
            val pkg = games[spinner.selectedItemPosition].second
            runGameMode(pkg, "performance")
            toast("Meminta Game Mode Performance")
        })
        box.addView(actionButton("⚖ BALANCED", Color.LTGRAY) {
            val pkg = games[spinner.selectedItemPosition].second
            runGameMode(pkg, "standard")
            toast("Meminta Game Mode Standard")
        })
        box.addView(actionButton("❄ GPU COOLER (SAFE)", Color.rgb(120,220,255)) {
            toast("Thermal protection tetap aktif. Tutup game/istirahat bila panas.")
        })
        box.addView(actionButton("🧠 RAM CHECK", Color.WHITE) {
            val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val mi = ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)
            toast("RAM tersedia: ${mi.availMem / (1024*1024)} MB")
        })
        box.addView(actionButton("TUTUP PANEL", Color.WHITE) { hidePanel() })
        panel = box
        wm.addView(box, params(dp(270), WindowManager.LayoutParams.WRAP_CONTENT).apply { x=dp(78); y=dp(180) })
    }

    private fun label(s: String) = TextView(this).apply {
        text=s; textSize=13f; setTextColor(Color.WHITE); setPadding(0, dp(10), 0, dp(4))
    }

    private fun actionButton(s: String, color: Int, action: () -> Unit) =
        Button(this).apply {
            text=s; setTextColor(Color.BLACK); textSize=12f
            setBackgroundColor(color)
            setOnClickListener { action() }
        }

    private fun runGameMode(pkg: String, mode: String) {
        Thread {
            try {
                val p = Runtime.getRuntime().exec(arrayOf("sh", "-c", "cmd game mode $mode $pkg"))
                p.waitFor()
            } catch (_: Exception) {}
        }.start()
    }

    private fun toast(s: String) { android.os.Handler(mainLooper).post { Toast.makeText(this, s, Toast.LENGTH_SHORT).show() } }

    override fun onDestroy() {
        hidePanel()
        bubble?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        bubble = null
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
