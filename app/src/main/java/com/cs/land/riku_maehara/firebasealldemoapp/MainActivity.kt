package com.cs.land.riku_maehara.firebasealldemoapp

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.cs.land.riku_maehara.firebasealldemoapp.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ValueEventListener {

    lateinit private var mFirebaseAuth: FirebaseAuth
    lateinit private var mFirebaseDB: FirebaseDatabase
    lateinit private var mRef: DatabaseReference
    lateinit private var messageEditText: EditText
    lateinit private var mUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseDB = FirebaseDatabase.getInstance()

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener(this)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        messageEditText = bindView(R.id.message_edit_text)

        mUserId = mFirebaseAuth.currentUser!!.uid
        mRef = mFirebaseDB.getReference("message")
        mRef.addValueEventListener(this)
        mRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError?) {
                Timber.e(databaseError?.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                getMessagesFromDataSnapshot(dataSnapshot!!).map {
                    Timber.d(it.message)
                }
            }
        })
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCancelled(databaseError: DatabaseError?) {
        Timber.e("Failed to read value" + databaseError?.message)
    }

    override fun onDataChange(dataSnapshot: DataSnapshot?) {
        if (dataSnapshot!!.childrenCount.toInt() == 0) return
        getMessagesFromDataSnapshot(dataSnapshot).map {
            Timber.d(it.message)
        }
    }

    override fun onClick(v: View?) {
        val message = Message(messageEditText.text.toString(), mUserId, System.currentTimeMillis())
        writeNewMessage(message)
        messageEditText.setText("")
    }

    private fun writeNewMessage(message: Message) {
        val key = mFirebaseDB.reference.child("message").push().key
        val messageValues = message.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates.put("/posts/" + key, messageValues)
        childUpdates.put("/user-posts/" + message.name + "/" + key, messageValues)
        mRef.updateChildren(childUpdates)
    }

    private fun getMessagesFromDataSnapshot(dataSnapshot: DataSnapshot): ArrayList<Message> {
        val oldMessages = ArrayList<Message>()

        dataSnapshot.child("posts").children.mapTo(oldMessages) {
            it.getValue(Message::class.java)
        }
        return oldMessages
    }
}
