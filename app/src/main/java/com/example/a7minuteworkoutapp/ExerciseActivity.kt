package com.example.a7minuteworkoutapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minuteworkoutapp.databinding.ActivityExerciseBinding
import com.example.a7minuteworkoutapp.databinding.DialogCurstomBackConfirmationBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding:ActivityExerciseBinding?=null

    private var restTimer:CountDownTimer?=null
    private var restProgress=0
    private var restTimerDuration:Long=1

    private var exerciseTimer:CountDownTimer?=null
    private var exerciseProgress=0
    private var exerciseTimerDuration:Long=1


    private var exerciseList:ArrayList<exerciseModel>?=null
    private var currentExercisePosition=-1


    private var tts:TextToSpeech?=null
    private var player:MediaPlayer?=null

    private var exerciseAdapter:ExerciseAdapterStatus?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityExerciseBinding.inflate(layoutInflater)

        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarExercise)

        if(supportActionBar!= null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }


        tts= TextToSpeech(this,this)
        exerciseList=Constants.defaultExerciseList()

        setRestView()
        setUpExerciseStatusRecyclerView()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
        //super.onBackPressed()
    }


    private fun customDialogForBackButton(){
        val customDialog=Dialog(this)
        val dialogBinding=DialogCurstomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.tvYes.setOnClickListener {

            this@ExerciseActivity.finish()
            customDialog.dismiss()

        }
        dialogBinding.tvNo.setOnClickListener {

            customDialog.dismiss()
        }
        customDialog.show()
    }


    private fun setUpExerciseStatusRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        exerciseAdapter= ExerciseAdapterStatus(exerciseList!!)
        binding?.rvExerciseStatus?.adapter=exerciseAdapter

    }



    private fun setRestView(){

        try{
            val soundURI= Uri.parse("android.resource://com.example.a7minuteworkoutapp/" + R.raw.press_start)

            player=MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping=false
            player?.start()

        }catch (e:Exception){
            e.printStackTrace()
        }

        binding?.flRestView?.visibility=View.VISIBLE
        binding?.tvTitle?.visibility=View.VISIBLE
        binding?.tvExerciseName?.visibility=View.INVISIBLE
        binding?.ivImage?.visibility=View.INVISIBLE
        binding?.flExerciseView?.visibility=View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility=View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility=View.VISIBLE

        if(restTimer!=null){
            restTimer?.cancel()
            restProgress=0
        }

        binding?.tvUpcomingExerciseName?.text=exerciseList!![currentExercisePosition + 1].getName()



        setRestProgressBar()
    }

    private fun setExerciseView(){
        binding?.flRestView?.visibility=View.INVISIBLE
        binding?.tvTitle?.visibility=View.INVISIBLE
        binding?.tvExerciseName?.visibility=View.VISIBLE
        binding?.ivImage?.visibility=View.VISIBLE
        binding?.flExerciseView?.visibility=View.VISIBLE
        binding?.tvUpcomingLabel?.visibility=View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility=View.INVISIBLE

        if(exerciseTimer!=null){
            exerciseTimer?.cancel()
            exerciseProgress=0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text=exerciseList!![currentExercisePosition].getName()

        setExerciseProgressBar()

    }

    private fun  setRestProgressBar(){
        binding?.Progressbar?.progress=restProgress

        restTimer=object :CountDownTimer(restTimerDuration*1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.Progressbar?.progress=10-restProgress
                binding?.tvTimer?.text=(10-restProgress).toString()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()

                setExerciseView()
            }

        }.start()
    }

    private fun  setExerciseProgressBar(){
        binding?.ProgressbarExercise?.progress=exerciseProgress

        exerciseTimer=object :CountDownTimer(exerciseTimerDuration*1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.ProgressbarExercise?.progress=exerciseTimerDuration.toInt()-exerciseProgress
                binding?.tvTimerExercise?.text=(exerciseTimerDuration.toInt()-exerciseProgress).toString()
            }

            override fun onFinish() {

                if(currentExercisePosition < exerciseList?.size!! -1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter?.notifyDataSetChanged()
                    setRestView()
                }else{
                    finish()
                    val intent= Intent(this@ExerciseActivity,ActivityFinish::class.java)
                    startActivity(intent)
                }
            }

        }.start()
    }




    override fun onDestroy(){


        if(restTimer!=null){
            restTimer?.cancel()
            restProgress=0
        }

        if(tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }
        if(player!=null){
            player!!.stop()
        }

        super.onDestroy()
        binding=null
    }

    override fun onInit(status: Int) {
        if(status==TextToSpeech.SUCCESS){
            val result=tts?.setLanguage(Locale.US)

            if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","The language specified is not supported!")
            }
        }else{
            Log.e("TTS","Initialisation failed")
        }
    }

    private fun speakOut(text:String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }

}