package com.example.a7minuteworkoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import com.example.a7minuteworkoutapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding:ActivityMainBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        //val flstartButton:FrameLayout=findViewById(R.id.fl_start)
        binding?.flStart?.setOnClickListener{
            val intent= Intent(this,ExerciseActivity::class.java)
            startActivity(intent)
        }
        binding?.flBMI?.setOnClickListener{
            val intent= Intent(this,BMI_Activity::class.java)
            startActivity(intent)
        }
        binding?.flHistory?.setOnClickListener{
            val intent= Intent(this,History_activity::class.java)
            startActivity(intent)
        }


    }

    override fun onDestroy() {
        super.onDestroy()

        binding=null
    }
}