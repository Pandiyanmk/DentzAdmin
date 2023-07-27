package com.app.dentzadmin.view

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.dentzadmin.R
import com.app.dentzadmin.data.model.MessageToFirebaseRead
import com.app.dentzadmin.data.model.QuestionRead
import com.app.dentzadmin.util.CommonUtil
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.Util
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import nl.dionsegijn.konfetti.xml.KonfettiView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Locale
import java.util.concurrent.TimeUnit


class UserHomePage : AppCompatActivity() {
    private val cu = CommonUtil()
    var isHasVideoUrl = ""
    private var loading: ProgressBar? = null
    private var donarList: RecyclerView? = null
    var textToSpeech: TextToSpeech? = null
    private var konfettiView: KonfettiView? = null
    var speak: FloatingActionButton? = null
    private var timer: CountDownTimer? = null


    private var play: FloatingActionButton? = null
    private var next: FloatingActionButton? = null
    private var prev: FloatingActionButton? = null
    private var replay: FloatingActionButton? = null
    private var forward: FloatingActionButton? = null

    private var textEnd: TextView? = null
    private var textStart: TextView? = null

    private var seekMusicBar: SeekBar? = null
    var imageView: ImageView? = null
    var mediaPlayer: MediaPlayer? = null
    var updateSeek: Thread? = null
    var stopThread = false
    private lateinit var simpleExoPlayer: ExoPlayer
    lateinit var playerView: StyledPlayerView

    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null

    var textContent: LinearLayout? = null
    var videoContent: LinearLayout? = null
    var audioContent: LinearLayout? = null
    var messagecontent: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_home_page)

        /* Hiding ToolBar */
        supportActionBar?.hide()

        // below line is used to get the
        // instance of our FIrebase database.
        firebaseDatabase = FirebaseDatabase.getInstance()

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase!!.getReference("MessageInfo")

        /* SetUp Views */
        loading = findViewById(R.id.loading)
        donarList = findViewById(R.id.donarList)
        messagecontent = findViewById<TextView>(R.id.messagecontent)
        textContent = findViewById(R.id.textContent)
        videoContent = findViewById(R.id.videoContent)
        audioContent = findViewById(R.id.audioContent)
        val profile = findViewById<ImageView>(R.id.profile)
        konfettiView = findViewById(R.id.konfettiView)
        playerView = findViewById(R.id.playerView)

        play = findViewById(R.id.playBtn)
        next = findViewById(R.id.nextBtn)
        prev = findViewById(R.id.prevBtn)
        replay = findViewById(R.id.replayBtn)
        forward = findViewById(R.id.forBtn)
        textStart = findViewById(R.id.txtSongStart)
        textEnd = findViewById(R.id.txtSongEnd)
        seekMusicBar = findViewById(R.id.seekBar)
        imageView = findViewById(R.id.img)

        profile.setOnClickListener {
            val moveToReset = Intent(this, ProfilePage::class.java)
            startActivity(moveToReset)
        }

        speak = findViewById(R.id.speak)

        startFetch()

        play!!.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                play!!.setImageResource(R.drawable.ic_play)
                mediaPlayer!!.pause()
            } else {
                play!!.setImageResource(R.drawable.ic_pause)
                mediaPlayer!!.start()
                val animation = TranslateAnimation(-25f, 25f, -25f, 25f)
                animation.interpolator = AccelerateInterpolator()
                animation.duration = 600
                animation.isFillEnabled = true
                animation.fillAfter = true
                animation.repeatMode = Animation.REVERSE
                animation.repeatCount = 1
                imageView!!.startAnimation(animation)
            }
        }
        replay!!.setOnClickListener { _: View? ->
            mediaPlayer!!.seekTo(
                mediaPlayer!!.currentPosition - 10000
            )
        }

        forward!!.setOnClickListener {
            mediaPlayer!!.seekTo(
                mediaPlayer!!.currentPosition + 10000
            )
        }

        speak!!.setOnClickListener {
            if (!textToSpeech!!.isSpeaking) {
                speak!!.setImageResource(R.drawable.stop)
                textToSpeech?.speak(
                    messagecontent!!.text.toString(), TextToSpeech.QUEUE_FLUSH, null
                )
                timer?.let {
                    it.cancel()
                    timer = null
                }
                timer = object : CountDownTimer(6000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {

                    }

                    override fun onFinish() {
                        speak!!.setImageResource(R.drawable.speak)
                        textToSpeech?.stop()
                    }
                }.start()

            } else {
                timer?.let {
                    it.cancel()
                    timer = null
                }
                speak!!.setImageResource(R.drawable.speak)
                textToSpeech?.stop()
            }
        }

        updateSeek = object : Thread() {
            override fun run() {
                while (!stopThread) {
                    try {
                        if (mediaPlayer != null) {
                            val currentPosition = mediaPlayer!!.currentPosition.toLong()
                            val format = String.format(
                                "%02d:%02d", java.lang.Long.valueOf(
                                    TimeUnit.MILLISECONDS.toMinutes(currentPosition)
                                ), java.lang.Long.valueOf(
                                    TimeUnit.MILLISECONDS.toSeconds(currentPosition) - TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(currentPosition)
                                    )
                                )
                            )
                            runOnUiThread { textStart!!.text = format }
                            seekMusicBar!!.progress = mediaPlayer!!.currentPosition
                            sleep(200)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val post = dataSnapshot.getValue(MessageToFirebaseRead::class.java)
                println("post.content" + post!!.content)
                post!!.content?.let {
                    updateContent(
                        it, post!!.questions as ArrayList<QuestionRead>
                    )
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        databaseReference!!.addValueEventListener(postListener)

    }

    private fun startFetch() {
        if (cu.isNetworkAvailable(this)) {

        } else {
            displayMessageInAlert(getString(R.string.no_internet))
            loading!!.visibility = View.GONE
        }
    }

    private fun displayMessageInAlert(message: String) {
        cu.showAlert(message, this)
    }

    private fun textTpSpeech() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech!!.language = Locale.ENGLISH
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: String?) {
        konfettiView!!.start(Presets.explode())
    }

    private fun initializePlayer(url: String) {

        val mediaDataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(this)

        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(url))

        val mediaSourceFactory = DefaultMediaSourceFactory(mediaDataSourceFactory)

        simpleExoPlayer = ExoPlayer.Builder(this).setMediaSourceFactory(mediaSourceFactory).build()

        simpleExoPlayer.addMediaSource(mediaSource)

        simpleExoPlayer.playWhenReady = true
        playerView.player = simpleExoPlayer
        playerView.requestFocus()
    }

    private fun releasePlayer() {
        simpleExoPlayer.release()
    }

    public override fun onResume() {
        super.onResume()
        if (isHasVideoUrl.isNotEmpty()) {
            initializePlayer(isHasVideoUrl)
        }
    }

    public override fun onPause() {
        super.onPause()
        if (isHasVideoUrl.isNotEmpty()) {
            simpleExoPlayer?.let {
                releasePlayer()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        textToSpeech?.let {
            it.stop()
            speak!!.setImageResource(R.drawable.speak)
        }
        mediaPlayer?.let {
            if (mediaPlayer!!.isPlaying) {
                play!!.setImageResource(R.drawable.ic_play)
                mediaPlayer!!.pause()
            }
        }
        if (isHasVideoUrl.isNotEmpty()) {
            simpleExoPlayer?.let {
                releasePlayer()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        if (isHasVideoUrl.isNotEmpty()) {
            if (Util.SDK_INT <= 23) initializePlayer(isHasVideoUrl)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.let {
            it.shutdown()
        }
        mediaPlayer?.let {
            it.stop()
            it.reset()
            it.release()
        }
    }

    fun updateContent(content: String, questionArray: ArrayList<QuestionRead>) {
        loading!!.visibility = View.GONE
        if (content.endsWith(".mp4")) {
            isHasVideoUrl = content
            videoContent!!.visibility = View.VISIBLE
            initializePlayer(content)
        } else if (content.endsWith(".mp3")) {
            audioContent!!.visibility = View.VISIBLE
            val uri = Uri.parse(content)
            mediaPlayer = MediaPlayer.create(applicationContext, uri)
            seekMusicBar!!.max = mediaPlayer!!.duration
            val duration = mediaPlayer!!.duration.toLong()
            textEnd!!.text = String.format(
                "%02d:%02d",
                java.lang.Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(duration)),
                java.lang.Long.valueOf(
                    TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(duration)
                    )
                )
            )
            updateSeek!!.start()
            stopThread = false
            mediaPlayer!!.setOnCompletionListener {
                if (mediaPlayer != null) {
                    play!!.setImageResource(R.drawable.ic_play)
                    mediaPlayer!!.seekTo(0)
                    mediaPlayer!!.start()
                    mediaPlayer!!.pause()
                }
            }
        } else {
            messagecontent!!.text = content
            textTpSpeech()
            speak!!.visibility = View.VISIBLE
            textContent!!.visibility = View.VISIBLE
        }
        val sharedPreference = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
        val isAnsweredContent = sharedPreference.getString("content", "")
        if (content != isAnsweredContent) {
            var editor = sharedPreference.edit()
            editor.putString("isAnswered", "")
            editor.putString("content", "")
            editor.commit()
        }

        donarList!!.layoutManager = LinearLayoutManager(this)
        val adapter = QuestionAdapter(this, questionArray, content)
        donarList!!.adapter = adapter
    }
}