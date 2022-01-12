package com.segunfrancis.notekeeper.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.segunfrancis.notekeeper.R
import com.segunfrancis.notekeeper.databinding.ActivityMainBinding
import com.segunfrancis.notekeeper.util.initDataBackup
import com.segunfrancis.notekeeper.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val navController: NavController by lazy {
        Navigation.findNavController(
            this,
            R.id.fragmentContainerView
        )
    }

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (firebaseAuth.currentUser?.uid != null) {
            //initDataBackup()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
