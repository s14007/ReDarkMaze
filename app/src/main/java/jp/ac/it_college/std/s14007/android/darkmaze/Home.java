package jp.ac.it_college.std.s14007.android.darkmaze;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Home extends AppCompatActivity implements jp.ac.it_college.std.s14007.android.darkmaze.View.Callback {
    private jp.ac.it_college.std.s14007.android.darkmaze.View view;
    public int dungeonLevel;

    private Chronometer chronometer;
    private Timer mainTimer;					//タイマー用
    private MainTimerTask mainTimerTask;
    private TextView countText;					//テキストビュー
    private long start;
    private long stop;
    private int count = 0;						//カウント
    private int minuteCount = 0;
    private int hourCount = 0;
    private Handler mHandler = new Handler();   //UI Threadへのpost用ハンドラ
    private int seed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seed = getIntent().getIntExtra("key_seed", 0);
        setContentView(R.layout.activity_home);

        SharedPreferences prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        int exp = prefs.getInt("exp", 0);
        int level = prefs.getInt("level", 0);

        Log.e("log :", "" + level);
        Log.e("log :", "" + exp);

        ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar1);
        bar.setMax(100);

        TextView labelLevel = (TextView)findViewById(R.id.label_level);
        SharedPreferences.Editor editor = prefs.edit();
        while (true) {
            ++level;
            exp -= 100;
            if (exp < 100) {
                break;
            }
            Log.e("level :", "plus" + exp);
//            view.setSeed(seed);
        }


        editor.putInt("level", level);
        labelLevel.setText(String.valueOf(level));
//        editor.putInt("level", 0);
        int a = exp % 100;
        bar.setProgress(a);

        Button button_hard = (Button)findViewById(R.id.button_hard);
        button_hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dungeonLevel = 2;
                view = new jp.ac.it_college.std.s14007.android.darkmaze.View(Home.this, dungeonLevel);
                view.setCallback(Home.this);
                setContentView(view);
            }
        });

        Button button_normal = (Button)findViewById(R.id.button_normal);
        button_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dungeonLevel = 1;
                view = new jp.ac.it_college.std.s14007.android.darkmaze.View(Home.this, dungeonLevel);
                view.setCallback(Home.this);
                setContentView(view);
            }
        });

        Button button_easy = (Button)findViewById(R.id.button_easy);
        button_easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dungeonLevel = 0;
                view = new jp.ac.it_college.std.s14007.android.darkmaze.View(Home.this, dungeonLevel);
                view.setCallback(Home.this);
                setContentView(view);
            }
        });

        Button button_menu = (Button)findViewById(R.id.button_menu);
        button_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dungeonLevel = 3;
                view = new jp.ac.it_college.std.s14007.android.darkmaze.View(Home.this, dungeonLevel);
                view.setCallback(Home.this);
                setContentView(view);
            }
        });
    }

    @Override
    public void timer() {
        start = System.currentTimeMillis();
//        mainTimer = new Timer();
//        mainTimerTask = new MainTimerTask();
//        //タイマースケジュール設定＆開始
//        mainTimer.schedule(mainTimerTask, 1000, 1000);
        //テキストビュー
//        countText = (TextView)findViewById(R.id.clear_time);
    }

    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            //ここに定周期で実行したい処理を記述します
            mHandler.post(new Runnable() {
                public void run() {

                    //実行間隔分を加算処理
                    count += 1;
                    if (count % 60 == 0) {
                        count = 0;
                        minuteCount += 1;
                    }

                    if (minuteCount % 60 == 0) {
                        minuteCount = 0;
                        hourCount += 1;
                    }
                    //画面にカウントを表示
//                    countText.setText(String.valueOf(count));
                }
            });
        }
    }


    @Override
    public void onGoal() {
        Toast.makeText(this, "Goal!!", Toast.LENGTH_SHORT).show();
        view.stopDrawThread();

        stop = System.currentTimeMillis();
//        mainTimer.cancel();
        Log.e("log :", "isFinished");
        Random rand = new Random();
        Home.newIntent(this, seed + rand.nextInt(10));

        Intent intent = new Intent(this, Result.class);
        intent.putExtra("time", start);
        intent.putExtra("minute", stop);
        intent.putExtra("hour", hourCount);
        intent.putExtra("dungeonLevel", dungeonLevel);
        startActivity(intent);
//        chronometer.stop();
        if (view.isFinished) {
            return;                                                                                                                                                                                                                                                                                                                                                             
        }
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.finish();
        Log.e("Home :", "finishしました");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        DialogFragment newFragment = new TestDialogFragment(this);
        newFragment.show(getFragmentManager(), "test");
        return super.onKeyDown(keyCode, event);
    }

    public static class TestDialogFragment extends DialogFragment {
        private Context context;
        public TestDialogFragment(Context context) {
            this.context = context;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("タイトルに戻りますか？")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                            Intent intent = new Intent(context, Title.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    private static Intent newIntent(Context context, int seed) {
        Intent intent = new Intent(context, Home.class);
        intent.putExtra("key_seed", seed);
        return intent;
    }
}
