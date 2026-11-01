package com.minimal.tvlauncher

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvClock = findViewById<TextView>(R.id.tvClock)
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        tvClock.text = sdf.format(Date())

        val rvApps = findViewById<RecyclerView>(R.id.rvApps)
        rvApps.layoutManager = GridLayoutManager(this, 5)

        val apps = loadInstalledApps()
        rvApps.adapter = AppAdapter(apps) { app ->
            val launchIntent = packageManager.getLaunchIntentForPackage(app.packageName)
            launchIntent?.let { startActivity(it) }
        }
    }

    private fun loadInstalledApps(): List<AppModel> {
        val appList = mutableListOf<AppModel>()
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = packageManager.queryIntentActivities(intent, 0)

        for (info in resolveInfos) {
            if (info.activityInfo.packageName != packageName) {
                val name = info.loadLabel(packageManager).toString()
                val icon = info.loadIcon(packageManager)
                appList.add(AppModel(name, info.activityInfo.packageName, icon))
            }
        }
        return appList.sortedBy { it.name.lowercase() }
    }
}
