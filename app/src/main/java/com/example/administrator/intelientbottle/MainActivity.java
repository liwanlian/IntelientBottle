package com.example.administrator.intelientbottle;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.os.CountDownTimer;

public class MainActivity extends exitsystem {

    TextView daojishi,jump;//倒计时显示，跳过
    private CountDown cd;//自定义倒计时的类
    TextView t_exit;//退出

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        cd=new CountDown(1000*5,1000);//倒计时5s
        cd.start();
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Functiomain.class);
                startActivity(intent);
                cd.cancel();
            }
        });//点击跳过，跳转至主功能界面
        t_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }//点击退出，直接结束程序

    //监听是否按下手机硬件的返回按钮
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//当返回按键被按下
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("确定要退出吗?");//设置提示信息
            //设置确定按钮并监听
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setAction(exitsystem.SYSTEM_EXIT);
                    sendBroadcast(intent);
                }
            });
            //设置取消按钮并监听
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //这里什么也不用做
                }
            });
            dialog.show();
        }
        return false;
    }
    public void init(){
        daojishi=(TextView)findViewById(R.id.homepage_daojishi);
        jump=(TextView)findViewById(R.id.jumptext);
        t_exit=(TextView)findViewById(R.id.homepage_exitbutton);
    }//对页面元素进行初始化

    //倒计时类
    public class CountDown extends CountDownTimer {

        public CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//总时长   计时的时间间隔
        }
        @Override
        public void onTick(long millisUntilFinished) {
            daojishi.setText(millisUntilFinished/1000+""+"S");
        }//计时过程显示
        @Override
        public void onFinish() {
            Intent intent=new Intent(MainActivity.this,Functiomain.class);
            startActivity(intent);
        }//倒计时结束，跳转至主功能界面
    }
}
