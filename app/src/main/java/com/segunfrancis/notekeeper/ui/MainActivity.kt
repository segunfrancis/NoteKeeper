package com.segunfrancis.notekeeper.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.segunfrancis.notekeeper.R
import com.segunfrancis.notekeeper.databinding.ActivityMainBinding
import com.segunfrancis.notekeeper.util.viewBinding
import com.segunfrancis.notekeeper.work_manager.NoteWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val navController: NavController by lazy {
        Navigation.findNavController(
            this,
            R.id.fragmentContainerView
        )
    }
    private val workManager: WorkManager by lazy { WorkManager.getInstance(this.applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = PeriodicWorkRequestBuilder<NoteWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .addTag("BACKUP_WORKER_TAG")
            .build()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
}