package com.chris.myqq.activity;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chris.myqq.R;
import com.chris.myqq.adapter.CommonFragmentPagerAdapter;
import com.chris.myqq.fragment.ChatEmotionFragment;
import com.chris.myqq.fragment.ChatFunctionFragment;
import com.chris.myqq.manager.EmotionInputDetector;
import com.chris.myqq.view.NoScrollViewPager;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import java.util.ArrayList;
import java.util.List;
import com.chris.myqq.adapter.ChatAdapter;
import com.chris.myqq.listener.MymessageListener;


/**
 * 聊天页面
 * 1 加载聊天记录
 * 2 监听消息
 */
public class ChatActivity extends BaseActivity implements TextWatcher, TextView.OnEditorActionListener {

    private String chat_to;
    private Button btnSend;
    private EditText etChat;
    private RecyclerView mRecycleView;
    private ImageView ivAdd;
    private TextView tvTitle;
    private ArrayList<EMMessage> messages = new ArrayList<EMMessage>();
    private ChatAdapter chatAdapter;
    private MymessageListener listener;
    private Button btnMore;
    private EmotionInputDetector mDetector;
    private ArrayList<Fragment> fragments;
    private ChatEmotionFragment chatEmotionFragment;
    private ChatFunctionFragment chatFunctionFragment;
    private CommonFragmentPagerAdapter chatAdapter2;
    private NoScrollViewPager viewpager;
    private RelativeLayout emotionLayout;
    private ImageView ivEmoji;
    private Button btnVoice;
    private TextView tvVoiceText;

    @Override
    protected void initData() {
        //获取当前要聊天的对象
        chat_to = getIntent().getStringExtra("chat_to");
        tvTitle.setText("正在与" + chat_to + "聊天");

        //加载聊天消息
        loadMessages();
        // 监听消息
        initMessageReceiveListener();
    }

    /**
     * 初始化消息接收监听
     */
    private void initMessageReceiveListener() {
        //适配器设计模式
        //收到新消息
        listener = new MymessageListener(){
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                //收到新消息
                toast("接收到新消息");
                //更新消息列表
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadMessages();
                    }
                });
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(listener);
    }

    /**
     * 移除接收消息监听
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除接收消息监听
        EMClient.getInstance().chatManager().removeMessageListener(listener);
    }

    /**
     * 加载聊天记录
     */
    private void loadMessages() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(chat_to);
        //获取此会话的所有消息
        if (conversation == null) {
            return;
        }

        //标注所有消息为已读
        conversation.markAllMessagesAsRead();

        // 通过 conversation 获得消息
        List<EMMessage> loadMessages = conversation.getAllMessages();
        messages.clear();
        messages.addAll(loadMessages);
        //进行更新
        chatAdapter.notifyDataSetChanged();
        //移动到最后一条
        mRecycleView.scrollToPosition(messages.size() - 1);
    }

    @Override
    protected void initListener() {
        etChat.addTextChangedListener(this);
        etChat.setOnEditorActionListener(this);
        btnSend.setOnClickListener(this);
        chatAdapter = new ChatAdapter(messages);
        mRecycleView.setAdapter(chatAdapter);

        mRecycleView.post(new Runnable() {
            @Override
            public void run() {
                int height = mRecycleView.getHeight();
                System.out.println("变化前的高度：" + height);
            }
        });

        //添加输入框焦点变化监听
        etChat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //获取变化后的列表高度
                mRecycleView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onGlobalLayout() {
                        //移除监听
                        mRecycleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        System.out.println("列表变化后的高度：" + mRecycleView.getHeight());
                        //列表滑动到最后一条
                        mRecycleView.scrollToPosition(messages.size() - 1);
                    }
                });
            }
        });

        //设置输入框点击事件
        etChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("执行了点击事件");
                mRecycleView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onGlobalLayout() {
                        mRecycleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mRecycleView.scrollToPosition(messages.size() - 1);
                    }
                });
            }
        });
    }

    @Override
    protected void initView() {
        tvTitle = (TextView) findViewById(R.id.my_title);
        ivAdd = (ImageView) findViewById(R.id.add);
        ivAdd.setVisibility(View.INVISIBLE);
        mRecycleView = (RecyclerView) findViewById(R.id.chat_recycleView);
        //设置成列表展示
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        etChat = (EditText) findViewById(R.id.chat_edit);
        btnSend = (Button) findViewById(R.id.chat_send);
        btnMore = (Button) findViewById(R.id.btn_more);

        viewpager = (NoScrollViewPager) findViewById(R.id.viewpager);
        emotionLayout = (RelativeLayout) findViewById(R.id.emotion_layout);
        ivEmoji = (ImageView) findViewById(R.id.iv_face_checked);
        btnVoice = (Button) findViewById(R.id.btn_set_mode_voice);
        tvVoiceText = (TextView) findViewById(R.id.voiceText);

        performEmoji();
    }

    /*
     * 添加表情
     */
    private void performEmoji() {
        fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        fragments.add(chatEmotionFragment);
        chatFunctionFragment = new ChatFunctionFragment();
        fragments.add(chatFunctionFragment);
        chatAdapter2 = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(chatAdapter2);
        viewpager.setCurrentItem(0);

        mDetector = EmotionInputDetector.with(this)
                .setEmotionView(emotionLayout) // 表情的 ViewPager 的父布局-- RelativeLayout
                .setViewPager(viewpager) // 没有高度,gone，表情的 ViewPager
                .bindToContent(mRecycleView) // 消息列表
                .bindToEditText(etChat) // 输入框
                .bindToEmotionButton(ivEmoji) // 点击显示表情
                .bindToAddButton(btnMore) // 文件按钮
                .bindToSendButton(btnSend) // 发送按钮
                .bindToVoiceButton(btnVoice) // 语音按钮
                .bindToVoiceText(tvVoiceText) // 语音文本
                .build();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void processClick(View v) {
        if (v.getId() == R.id.chat_send) {
            sendMsg();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0){
            btnMore.setVisibility(View.GONE);
            btnSend.setVisibility(View.VISIBLE);
        }else {
            btnSend.setVisibility(View.GONE);
            btnMore.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        //当前是否内容为空
        if (TextUtils.isEmpty(s.toString().trim())) {
            btnSend.setEnabled(false);
        } else {
            btnSend.setEnabled(true);
        }
    }

    //输入法点击监听
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.chat_edit && actionId == EditorInfo.IME_ACTION_SEND) {
            String msg = etChat.getText().toString().trim();
            if (TextUtils.isEmpty(msg)) {
                toast("不能发送空消息");
            } else {
                sendMsg();
            }
        }
        return true;
    }

    //发送消息
    private void sendMsg() {
        toast("执行发送消息操作");
        String msg = etChat.getText().toString().trim();

        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(msg, chat_to);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                toast("发送成功");
                //更新界面
                notifiDateSetChange();
            }

            @Override
            public void onError(int i, String s) {
                toast("发送失败");
                //更新界面
                notifiDateSetChange();
            }

            @Override
            public void onProgress(int i, String s) {
                toast("正在发送");
                //更新界面
                notifiDateSetChange();
            }
        });
        //添加到消息集合中
        messages.add(message);

        chatAdapter.notifyDataSetChanged();
        //滚动到最后一条消息
        mRecycleView.scrollToPosition(messages.size() - 1);
        //清空输入框
        etChat.getText().clear();
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    protected void notifiDateSetChange() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

}
