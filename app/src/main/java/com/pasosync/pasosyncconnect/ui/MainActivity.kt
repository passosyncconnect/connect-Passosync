package com.pasosync.pasosyncconnect.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.pasosync.pasosyncconnect.R
import com.pasosync.pasosyncconnect.newsdb.ArticleDatabase
import com.pasosync.pasosyncconnect.repositories.MainRepository
import com.pasosync.pasosyncconnect.ui.viewmodels.MainViewModelProvidefactory
import com.pasosync.pasosyncconnect.ui.viewmodels.MainViewModels
import kotlinx.android.synthetic.main.activity_main.*
class MainActivity : AppCompatActivity() {
    lateinit var viewmodel: MainViewModels
    private var mAuth: FirebaseAuth? = null
    private val mCurrentUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar as Toolbar?)
        FirebaseMessaging.getInstance().subscribeToTopic("general")
        (toolbar as Toolbar?)?.setLogo(R.drawable.logologin)

        (toolbar as Toolbar?)?.setTitleTextColor(Color.WHITE)
        val repository= MainRepository(ArticleDatabase(this))
        val viewModelProvidefactory= MainViewModelProvidefactory(application,repository)
        viewmodel= ViewModelProvider(this,viewModelProvidefactory).get(MainViewModels::class.java)


        mAuth = FirebaseAuth.getInstance();

       bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
}