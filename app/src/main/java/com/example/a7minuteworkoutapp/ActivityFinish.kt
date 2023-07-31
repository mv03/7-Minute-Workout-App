package com.example.a7minuteworkoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.a7minuteworkoutapp.databinding.ActivityExerciseBinding
import com.example.a7minuteworkoutapp.databinding.ActivityFinishBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ActivityFinish : AppCompatActivity() {

    private var binding:ActivityFinishBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarFinishActivity)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarFinishActivity?.setNavigationOnClickListener {
            onBackPressed()
        }


        binding?.btnFinish?.setOnClickListener {
            finish()
        }

        val dao=(application as WorkOutApp).db.historyDao()
        addDatetoDatabse(dao)
    }

    private fun addDatetoDatabse(historyDao: HistoryDao){

        val c=Calendar.getInstance()
        val dataTime=c.time
        Log.e("Date: ","" + dataTime)

        val sdf=SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
        val date=sdf.format(dataTime)
        Log.e("Formatted Date: ","" + dataTime)

        lifecycleScope.launch{
            historyDao.insert(HistoryEntity(date))
            Log.e(
                "Date: ",
                "Added : "
            )
        }

    }
}