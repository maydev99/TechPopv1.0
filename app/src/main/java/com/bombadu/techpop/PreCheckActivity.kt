package com.bombadu.techpop

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.concurrent.TimeUnit

class PreCheckActivity : AppCompatActivity() {

    private var rootRef = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth
    private var dataUpdateRef: DatabaseReference? = null
    private var updateCycleRef: DatabaseReference? = null
    private var lastUpdate: String? = null //for update dialog testing
    private var updateCycleStr: String? = null //for update dialog testing
    private var isUpdating = false

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_check)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        updateCycleRef = rootRef.child("update_cycle")
        auth = FirebaseAuth.getInstance()
        dataUpdateRef = rootRef.child("data_update")

        checkUpdateCycle()

    }

    //Gets the last Update timeStamp from Firebase
    private fun checkUpdateCycle() {

        val updateCycleListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //nothing

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val updateCycle = dataSnapshot.value.toString().toLong()
                updateCycleStr = updateCycle.toString()
                checkUpdate(updateCycle)

            }
        }
        updateCycleRef?.addValueEventListener(updateCycleListener)
    }

    //Checks if Update is needed based on difference between current time and last updated time
    //If update is needed- calls FetchData Function
    private fun checkUpdate(updateCycle: Long) {
        //var isUpdating = false
        val currentTimeStamp = timeStamp().toLong()
        val updateListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //nothing
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lastUpdateTimeStamp = dataSnapshot.value.toString().toLong()
                lastUpdate = lastUpdateTimeStamp.toString()
                val diff = currentTimeStamp - lastUpdateTimeStamp //Time Difference
                if (diff > updateCycle) { //if difference is greater than update cycle, trigger update(Update Activity)
                    isUpdating = true
                }

                controlFlow()
            }
        }

        dataUpdateRef?.addValueEventListener(updateListener)
    }

    private fun controlFlow(){

        if (isUpdating){
            startActivity(Intent(this, UpdateActivity::class.java))
            finish()
            isUpdating = false

        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun timeStamp(): String {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toString()
    }
}


