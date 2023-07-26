package com.app.dentzadmin.view

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.dentzadmin.R
import com.app.dentzadmin.data.model.Group
import com.app.dentzadmin.data.model.Message
import com.app.dentzadmin.data.model.Question
import com.app.dentzadmin.data.model.SendData
import com.app.dentzadmin.repository.MainRepository
import com.app.dentzadmin.util.CommonUtil
import com.app.dentzadmin.view.HomePage.ProgressTracker.PositionListener
import com.app.dentzadmin.viewModel.CommonViewModel
import com.app.dentzadmin.viewModel.CommonViewModelFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.Util
import com.google.android.material.floatingactionbutton.FloatingActionButton
import nl.dionsegijn.konfetti.xml.KonfettiView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Locale
import java.util.concurrent.TimeUnit


class HomePage : AppCompatActivity() {
    private val cu = CommonUtil()
    var isStart = false
    var maxSelect = 1
    var isHasVideoUrl = ""
    private var loading: ProgressBar? = null
    private var donarList: RecyclerView? = null
    private var groupList: RecyclerView? = null
    private var messageList: RecyclerView? = null
    private lateinit var aboutPageViewModel: CommonViewModel
    var textToSpeech: TextToSpeech? = null
    private var konfettiView: KonfettiView? = null
    var speak: FloatingActionButton? = null
    private var timer: CountDownTimer? = null
    var groupListData: ArrayList<Group>? = null
    var messageListData: ArrayList<Message>? = null
    private var play: FloatingActionButton? = null
    private var next: FloatingActionButton? = null
    private var prev: FloatingActionButton? = null
    private var replay: FloatingActionButton? = null
    private var forward: FloatingActionButton? = null
    private var send: FloatingActionButton? = null

    private var textContent: LinearLayout? = null
    private var loaderView: RelativeLayout? = null
    private var videoContent: LinearLayout? = null
    private var audioContent: LinearLayout? = null

    private var textEnd: TextView? = null
    private var textStart: TextView? = null
    private var messagecontent: TextView? = null

    private var seekMusicBar: SeekBar? = null
    private lateinit var simpleExoPlayer: ExoPlayer
    lateinit var playerView: StyledPlayerView
    lateinit var groupAdapter: GroupAdapter
    lateinit var messageAdapter: MessageAdapter
    var tracker: ProgressTracker? = null

    var audioExoPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        /* Hiding ToolBar */
        supportActionBar?.hide()

        /* ViewModel Initialization */
        aboutPageViewModel = ViewModelProvider(
            this, CommonViewModelFactory(MainRepository())
        )[CommonViewModel::class.java]

        /* SetUp Views */
        loading = findViewById(R.id.loading)
        donarList = findViewById(R.id.donarList)
        groupList = findViewById(R.id.groupList)
        messageList = findViewById(R.id.messageList)
        messagecontent = findViewById<TextView>(R.id.messagecontent)
        textContent = findViewById(R.id.textContent)
        videoContent = findViewById(R.id.videoContent)
        audioContent = findViewById(R.id.audioContent)
        val profile = findViewById<ImageView>(R.id.profile)
        konfettiView = findViewById(R.id.konfettiView)
        playerView = findViewById(R.id.playerView)
        loaderView = findViewById(R.id.loaderView)
        send = findViewById(R.id.send)

        val choosemessage = findViewById<TextView>(R.id.choosemessage)
        val choosegroup = findViewById<TextView>(R.id.choosegroup)
        val choosoptions = findViewById<TextView>(R.id.choosoptions)

        play = findViewById(R.id.playBtn)
        next = findViewById(R.id.nextBtn)
        prev = findViewById(R.id.prevBtn)
        replay = findViewById(R.id.replayBtn)
        forward = findViewById(R.id.forBtn)
        textStart = findViewById(R.id.txtSongStart)
        textEnd = findViewById(R.id.txtSongEnd)
        seekMusicBar = findViewById(R.id.seekBar)

        send!!.setOnClickListener {
            var getCount: List<Group> = groupListData!!.filter { it.status == 1 }
            if (getCount.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_group), Toast.LENGTH_SHORT)
                    .show()
            } else {
                val groupBuilder = StringBuilder()
                for (i in 0 until getCount!!.size) {
                    groupBuilder.append(getCount!![i].name + ",")
                }
                var commaremovedGroup = ""
                if (groupBuilder.toString().endsWith(",")) {
                    commaremovedGroup = groupBuilder.substring(0, groupBuilder.length - 1)
                }
                var messageSelected: List<Message> = messageListData!!.filter { it.status == 1 }


                Toast.makeText(this, getString(R.string.sent), Toast.LENGTH_SHORT).show()

                val sendData =
                    SendData(message = messageSelected[0].content, groups = commaremovedGroup)
                aboutPageViewModel.insertData(sendData, this)


            }
        }

        profile.setOnClickListener {
            val moveToReset = Intent(this, ProfilePage::class.java)
            startActivity(moveToReset)
        }

        speak = findViewById(R.id.speak)

        startFetch()

        aboutPageViewModel.errorMessage.observe(this) { errorMessage ->
            cu.showAlert(errorMessage, this)
            loading!!.visibility = View.GONE

        }

        aboutPageViewModel.inserted.observe(this) { message ->
            resetAllGroupData()
            setgroupData(message)
        }

        play!!.setOnClickListener {
            if (audioExoPlayer!!.isPlaying) {
                audioExoPlayer!!.pause()
                play!!.setImageResource(R.drawable.ic_play)
            } else {
                play!!.setImageResource(R.drawable.ic_pause)
                audioExoPlayer!!.play()
                val animation = TranslateAnimation(-25f, 25f, -25f, 25f)
                animation.interpolator = AccelerateInterpolator()
                animation.duration = 600
                animation.isFillEnabled = true
                animation.fillAfter = true
                animation.repeatMode = Animation.REVERSE
                animation.repeatCount = 1
            }
        }
        replay!!.setOnClickListener { _: View? ->
            audioExoPlayer!!.seekTo(
                audioExoPlayer!!.currentPosition - 10000
            )
        }

        forward!!.setOnClickListener {
            audioExoPlayer!!.seekTo(
                audioExoPlayer!!.currentPosition + 10000
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

        aboutPageViewModel.groupName.observe(this) { groupName ->
            for (i in 0 until groupListData!!.size) {
                if (groupName.contains(groupListData!![i].name)) {
                    val mData = Group(groupListData!![i].name, 2)
                    groupListData!![i] = mData
                }
            }
            groupData(groupName)
        }

        aboutPageViewModel.responseContent.observe(this) { result ->
            loading!!.visibility = View.GONE
            choosemessage!!.visibility = View.VISIBLE
            choosegroup!!.visibility = View.VISIBLE
            choosoptions!!.visibility = View.VISIBLE

            maxSelect = result.maxselect
            groupListData = result.group
            groupList!!.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


            messageListData = result.message
            messageList!!.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            messageAdapter = MessageAdapter(this, messageListData!!)
            messageList!!.adapter = messageAdapter

            val mData = Message(messageListData!![0].content, 1, messageListData!![0].questions)
            messageListData!![0] = mData
            messageAdapter.notifyDataSetChanged()

            questionListData(messageListData!![0].questions)

            setgroupData(messageListData!![0].content)

            if (messageListData!!.size > 0) {
                if (messageListData!![0].content.endsWith("mp3")) {
                    setContent("audio", messageListData!![0].content)
                } else if (messageListData!![0].content.endsWith("mp4")) {
                    setContent("video", messageListData!![0].content)
                } else {
                    setContent("text", messageListData!![0].content)
                }
            }
        }
    }

    private fun startFetch() {
        if (cu.isNetworkAvailable(this)) {
            aboutPageViewModel.getResponseContent(this)
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
        if (event.equals("done")) {
            konfettiView!!.start(Presets.explode())
        } else if (event!!.startsWith("message")) {
            val splitValue = event!!.split(",")

            val postion = splitValue[1].toInt()
            for (i in 0 until messageListData!!.size) {
                val mData = Message(messageListData!![i].content, 0, messageListData!![i].questions)
                messageListData!![i] = mData
            }
            val mData =
                Message(messageListData!![postion].content, 1, messageListData!![postion].questions)
            messageListData!![postion] = mData
            messageAdapter.notifyDataSetChanged()

            questionListData(messageListData!![postion].questions)

            if (messageListData!![postion].content.endsWith("mp3")) {
                setContent("audio", messageListData!![postion].content)
            } else if (messageListData!![postion].content.endsWith("mp4")) {
                setContent("video", messageListData!![postion].content)
            } else {
                setContent("text", messageListData!![postion].content)
            }
            resetAllGroupData()
            setgroupData(messageListData!![postion].content)
        } else {
            val splitValue = event!!.split(",")
            val gData = Group(groupListData!![splitValue[0].toInt()].name, splitValue[1].toInt())
            groupListData!![splitValue[0].toInt()] = gData
            groupAdapter.notifyDataSetChanged()
        }
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
        audioExoPlayer?.let {
            if (audioExoPlayer!!.isPlaying) {
                play!!.setImageResource(R.drawable.ic_play)
                audioExoPlayer!!.pause()
                stopAudio()
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
        audioExoPlayer?.let {
            it.stop()
            it.release()
        }
    }

    private fun setContent(type: String, content: String) {
        when (type) {
            "text" -> {
                stopAudio()
                stopVideoPlayer()
                textTpSpeech()
                speak!!.visibility = View.VISIBLE
                textContent!!.visibility = View.VISIBLE
                videoContent!!.visibility = View.GONE
                audioContent!!.visibility = View.GONE
                messagecontent!!.text = content
            }

            "audio" -> {
                textEnd!!.text = ""
                stopAudio()
                stopVideoPlayer()
                play!!.setImageResource(R.drawable.ic_play)
                loaderView!!.visibility = View.VISIBLE
                textContent!!.visibility = View.GONE
                videoContent!!.visibility = View.GONE
                audioContent!!.visibility = View.VISIBLE

                val mediaDataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(this)

                val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(content))

                val mediaSourceFactory = DefaultMediaSourceFactory(mediaDataSourceFactory)

                audioExoPlayer =
                    ExoPlayer.Builder(this).setMediaSourceFactory(mediaSourceFactory).build()
                audioExoPlayer?.let {
                    it.addMediaSource(mediaSource)
                    it.prepare()

                    it.addListener(object : Player.Listener {
                        override fun onPlayerStateChanged(
                            playWhenReady: Boolean, playbackState: Int
                        ) {
                            when (playbackState) {
                                ExoPlayer.STATE_BUFFERING -> {}
                                ExoPlayer.STATE_ENDED -> {
                                    play!!.setImageResource(R.drawable.ic_play)
                                    it.seekTo(0)
                                    it.playWhenReady = true
                                    it.pause()
                                }

                                ExoPlayer.STATE_IDLE -> {}
                                ExoPlayer.STATE_READY -> {
                                    val totalSeconds = it.duration
                                    if (totalSeconds > 1) {
                                        loaderView!!.visibility = View.GONE
                                    }
                                    seekMusicBar!!.max = it.duration.toInt() / 1000
                                    textEnd!!.text = String.format(
                                        "%02d:%02d", java.lang.Long.valueOf(
                                            TimeUnit.MILLISECONDS.toMinutes(
                                                totalSeconds
                                            )
                                        ), java.lang.Long.valueOf(
                                            TimeUnit.MILLISECONDS.toSeconds(totalSeconds) - TimeUnit.MINUTES.toSeconds(
                                                TimeUnit.MILLISECONDS.toMinutes(totalSeconds)
                                            )
                                        )
                                    )
                                    if (!isStart) {
                                        isStart = true
                                        var tracker =
                                            ProgressTracker(it, object : PositionListener {
                                                override fun progress(position: Long) {
                                                    val currentPosition =
                                                        audioExoPlayer!!.currentPosition
                                                    val format = String.format(
                                                        "%02d:%02d", java.lang.Long.valueOf(
                                                            TimeUnit.MILLISECONDS.toMinutes(
                                                                currentPosition
                                                            )
                                                        ), java.lang.Long.valueOf(
                                                            TimeUnit.MILLISECONDS.toSeconds(
                                                                currentPosition
                                                            ) - TimeUnit.MINUTES.toSeconds(
                                                                TimeUnit.MILLISECONDS.toMinutes(
                                                                    currentPosition
                                                                )
                                                            )
                                                        )
                                                    )
                                                    runOnUiThread { textStart!!.text = format }
                                                    seekMusicBar!!.progress =
                                                        audioExoPlayer!!.currentPosition.toInt() / 1000
                                                }
                                            })
                                    }
                                }

                                else -> {
                                }
                            }
                        }
                    })
                }

            }

            "video" -> {
                stopVideoPlayer()
                stopAudio()
                isStart = false
                isHasVideoUrl = content
                textContent!!.visibility = View.GONE
                videoContent!!.visibility = View.VISIBLE
                audioContent!!.visibility = View.GONE
                initializePlayer(content)
            }
        }
    }


    class ProgressTracker(
        private val player: Player, private val positionListener: PositionListener
    ) : Runnable {
        interface PositionListener {
            fun progress(position: Long)
        }

        private val handler: Handler = Handler()

        init {
            handler.post(this)
        }

        override fun run() {
            val position = player.currentPosition
            positionListener.progress(position)
            handler.postDelayed(this, 500)
        }

        fun purgeHandler() {
            handler.removeCallbacks(this)
        }
    }

    private fun stopAudio() {
        if (isStart) {
            tracker?.let {
                it.purgeHandler()
            }
            audioExoPlayer?.let {
                it.pause()
            }
            isStart = false
        }
    }

    fun stopVideoPlayer() {
        if (isHasVideoUrl.isNotEmpty()) {
            simpleExoPlayer?.let {
                releasePlayer()
            }
        }
    }

    fun questionListData(questions: ArrayList<Question>) {
        donarList!!.layoutManager = LinearLayoutManager(this)
        val adapter = DonarAdapter(this, questions)
        donarList!!.adapter = adapter
    }

    private fun groupData(groupName: String) {
        groupAdapter = GroupAdapter(this, groupListData!!, maxSelect, groupName)
        groupList!!.adapter = groupAdapter
    }

    private fun setgroupData(messageContent: String) {
        aboutPageViewModel.getGroups(messageContent, this)
    }

    private fun resetAllGroupData() {
        for (i in 0 until groupListData!!.size) {
            val mData = Group(groupListData!![i].name, 0)
            groupListData!![i] = mData
        }
        groupAdapter.notifyDataSetChanged()
    }
}