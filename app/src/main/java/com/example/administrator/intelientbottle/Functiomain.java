package com.example.administrator.intelientbottle;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.widget.TimePicker;
import android.widget.Toast;
import android.os.Handler;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import android.app.Notification;
import android.app.NotificationManager;


/**
 * Created by Administrator on 2018/10/21.
 */

public class Functiomain extends exitsystem
{
    TextView lianjie;//连接
    TextView quxian;//曲线图
    Button finshpercent;//完成的百分比
    TextView date;//日期
    TextView standardwater;//设置今天的喝水量
    TextView standtemperture;//设置标准的温度
    TextView an_water;//已喝水
    TextView an_temp;//显示当前水的温度
    TextView an_time;//显示距离可以喝水的时间
    TextView tlocation;//网络定位当前的位置
    Switch  onoff;//滑动开发按钮
    TextView bluetoothname, bluetoothaddress;//蓝牙名字，地址
    //定时
    TextView timing;//定时
    Calendar cal;//获取当前的详细日期

    TextView send;//测试发送
    EditText e_content;//发送的内容

    AlertDialog bluetoothlist;//显示蓝牙列表
    AlertDialog notconnect;//断开蓝牙的弹窗
    AlertDialog alte;
    ArrayAdapter adapter;//适配器对象的定义
    AlertDialog intentzhu;//判断是否可以跳转到柱状图的界面

    int flag = 0;//蓝牙是否被连接上的标记    0没有连接    1成功连接

    // 蓝牙部分
    BluetoothAdapter bTAdatper;//适配器
    BluetoothDevice device;//周围设备
    BluetoothSocket socket;
    ArrayList<BluetoothDevice> devices = new ArrayList<>();//蓝牙设备的对象的集合
    ArrayList<String> deviceNames = new ArrayList<>(); //设备的名称集合
    public static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //手机蓝牙的UUID固定值
    private static final int REQUEST_LOCATION = 1000;//手机动态请求权限的请求码
    private static final int REQUEST_ENABLE = 1001;//启动蓝牙设备的请求码
    //  private static final int REQUEST_DISCOVER_MYSELF = 1002;//设置自身蓝牙设备可被发现的请求码
    private ClientThread mClientThread;//客户端线程

    //本地数据库
    private DBManager yy;
    String sql_date;//当天日期
    String sql_address;//蓝牙地址
    String sql_name;//蓝牙名字

    //数据处理部分
    double waterA = 0, waterB = 0;//用于计算是否将接收回来的水重放入到数据库的两个参数
    StringBuffer stringBuffer;
    Readtask readtask;//读线程

    //定位部分
    public LocationClient mLocationClient = null; //初始化LocationClient类
    public MyLocationListener myListener = new MyLocationListener();

    //手机通知栏
    private NotificationManager mNManager;
    private NotificationManager mNManager1;

    //温馨提示---根据以下参数弹出不同的内容的温馨提示
    int[] properid;//数据库中属于该蓝牙的id，存放在此数组
    int[] standardw;//该数据库中属于该蓝牙的标准喝水量
    int[] water;//该数据库中属于该蓝牙的已喝水量
    int[] istand;//选择的标准喝水量
    int[] iwater;//选择的已喝水

    int  choice;//设置标准水量   方式的选择
    double  time_todrink;//距离可以喝水的时间
    String string_time_todrink;
    int onofffla=0;//滑动开关滑动的次数记录，初始化为0

    //监听手机本地蓝牙的打开
    private Context mContext;
    private String bluetoothStatus="off";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.function_main);
        mContext = getApplicationContext();
        mContext.registerReceiver(mReceiver, makeFilter());
        init();
        textfunction();
        switchfunction();
        System.out.print("bta="+bTAdatper.EXTRA_STATE);
    }

    public void init() {
        quxian = (TextView) findViewById(R.id.t_quxian);
        date = (TextView) findViewById(R.id.t_date);
        lianjie = (TextView) findViewById(R.id.denglu);
        finshpercent=(Button)findViewById(R.id.bu_waterpercent);
        standardwater=(TextView)findViewById(R.id.an_thefirst);
        standtemperture=(TextView)findViewById(R.id.tempresult);
        an_time = (TextView) findViewById(R.id.an_fourth);
        an_temp = (TextView) findViewById(R.id.an_third);
        an_water = (TextView) findViewById(R.id.an_thesecond);
        bluetoothname = (TextView) findViewById(R.id.t_bluetoothname);
        bluetoothaddress = (TextView) findViewById(R.id.t_bluetoothaddress);
//        send = (TextView) findViewById(R.id.sure);
     //   e_content = (EditText) findViewById(R.id.e_sendcontent);

        bTAdatper = BluetoothAdapter.getDefaultAdapter();
        yy = new DBManager(this);
        stringBuffer = new StringBuffer();

        tlocation = (TextView) findViewById(R.id.an_theseven);//定位
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener); //注册监听函数
        setLocationOption(); //定义setLocationOption()方法
        mLocationClient.start(); //执行定位

        onoff=(Switch)findViewById(R.id.ss);//开关

        //手机通知栏提示
        mNManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNManager1 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //定时
        timing=(TextView)findViewById(R.id.timing);
    }
    //提示连接蓝牙
    public void connecttip(){
        intentzhu = new AlertDialog.Builder(Functiomain.this)
                .setTitle("提示")
                .setMessage("请先连接上蓝牙！！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intentzhu.dismiss();
                    }//断开连接蓝牙
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intentzhu.dismiss();
                    }
                }).create();
        intentzhu.show();
    }

    //开关按钮监听
    public void switchfunction(){
        onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onofffla++;
                if (isChecked){
                    if (flag==1)
                    {
                        if (onofffla==1)
                            ;
                        else
                            write("O");
                    }
                    else
                    {
                        onoff.setChecked(false);
                        connecttip();
                    }
                }
                else{
                    write("C");
                }

            }
        });//对滑动开关按钮的使用
    }
    //重写onKeyDown方法,对按键(不一定是返回按键)监听
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//当返回按键被按下
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);//新建一个对话框
            dialog.setMessage("确定要退出吗?");//设置提示信息
            //设置确定按钮并监听
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setAction(exitsystem.SYSTEM_EXIT);
                    sendBroadcast(intent);
                    shutdownClient();
                }
            });
            //设置取消按钮并监听
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //这里什么也不用做
                }
            });
            dialog.show();//最后不要忘记把对话框显示出来
        }
        return false;
    }
    //将绑定过的蓝牙，展示在列表
    public void createview() {
        View bottomView = View.inflate(Functiomain.this, R.layout.dialoglistview, null);//填充ListView布局
        ListView listview = (ListView) bottomView.findViewById(R.id.dia_list);//初始化ListView控件
        //创建适配器,使用系统布局
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceNames);
        //给ListView设置适配器
        listview.setAdapter(adapter);
        //给ListView设置点击事件，点击对应的条目就创建对应的客户端，并经行数据的读取和写入
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
//                String result = parent.getItemAtPosition(position).toString();//获取当前按下的蓝牙的名称
                //获取搜索到的蓝牙设备列表中的蓝牙设备及其状态

                device = devices.get(position);
                mClientThread = new ClientThread();
                mClientThread.start();
            }
        });
        bluetoothlist = new AlertDialog.Builder(this)
                .setTitle("当前检测到的蓝牙如下：").setView(bottomView)//在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bluetoothlist.dismiss();
                    }
                }).create();
        bluetoothlist.show();
    }
    //设置标准喝水量   手动输入
    public  void createeditdialog(){
        String pastcontent=standardwater.getText().toString().substring(0,standardwater.getText().toString().length()-2);
        final EditText et = new EditText(this);
        et.setText(pastcontent);
        et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);//限制输入数字和小数点
        new AlertDialog.Builder(this).setTitle("当前设置的标准喝水量：")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String result=et.getText().toString();
                        double Estimate=Double.valueOf(result);//最新设置的当天喝水量
                        yy.updatestand(sql_address,sql_date.toString(),Estimate);
                        finshstatus();
                        standardwater.setText(result+"ml");
                    }
                }).setNegativeButton("取消",null).show();
    }
    //标准水温的设置
    public  void createeditdialogs(){
     final   String pastcontent=standtemperture.getText().toString().substring(0,standtemperture.getText().toString().length()-1);
        final EditText et = new EditText(this);
        et.setText(pastcontent);
     //   et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);//限制输入数字和小数点
        et.setKeyListener(new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                char[] numberChars={'1','2','3','4','5','6','7','8','9','0'};
                return numberChars;
            }
            @Override
            public int getInputType() {
                return android.text.InputType.TYPE_CLASS_PHONE;
            }         });

        new AlertDialog.Builder(this).setTitle("当前设置的标准水温：")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        String result=et.getText().toString();
//                        StringBuffer sendtemp=new StringBuffer();
//                        sendtemp.append("T").append(result);
//                        standtemperture.setText(result+"℃");
//                        double updatetemp=Double.valueOf(result);
//                        yy.updatetemptuare(sql_address,sql_date.toString(),updatetemp);
//                        write(sendtemp.toString());
//                        account_timetodrink();
                        int y=0;
                        String result=et.getText().toString();
                        if (result.length()==0){
                            Toast.makeText(Functiomain.this,"输入的值不能为空",Toast.LENGTH_SHORT).show();
                            standtemperture.setText(pastcontent+"℃");
                        }
                        else{
                            double resultdata=Double.valueOf(result);
                            if (resultdata<=0||resultdata>100){
                                Toast.makeText(Functiomain.this,"设置的水温只能在0-100之间(不包含0）",Toast.LENGTH_LONG).show();
                                standtemperture.setText(pastcontent+"℃");
                            }
                            else{
                                StringBuffer sendtemp=new StringBuffer();
                                sendtemp.append("T").append(result);
                                standtemperture.setText(result+"℃");
                                int datelength=result.length();
                                if (datelength<3){
                                    StringBuffer databuffer=new StringBuffer();
                                    if (datelength==1){
                                        databuffer.append("0").append("0").append(result);
                                        result=databuffer.toString();
                                    }
                                    else if (datelength==2){
                                        databuffer.append("0").append(result);
                                        result=databuffer.toString();
                                    }
                                  //  Toast.makeText(Functiomain.this,result,Toast.LENGTH_SHORT).show();
                                }
                                int updatetemp=Integer.valueOf(result);
                                yy.updatetemptuare(sql_address,sql_date.toString(),updatetemp);
                                write("T");
                                for (y=0;y<(result.length());y++){
                                    String ct=null;
                                    ct=result.substring(y,y+1);
                                    System.out.println("ct="+ct);
                                    write(ct);
                                }
                                //         write("℃");
                                account_timetodrink();
                            }
                        }

                    }
                }).setNegativeButton("取消",null).show();
    }
    //计算可以喝水的时间
    public  void account_timetodrink(){
        String timea=an_temp.getText().toString().substring(0,an_temp.getText().toString().length()-1);//当前水温的转换
        double dtime=Double.parseDouble(timea);
        String timeb=standtemperture.getText().toString().substring(0,standtemperture.getText().toString().length()-1);//标准水温的转换
        double dtime1=Double.parseDouble(timeb);
        time_todrink=dtime1-dtime;
        if (time_todrink>0)
        {
            string_time_todrink=String.valueOf(time_todrink);+++++
            an_time.setText(string_time_todrink+"min");
        }
        else{
            an_time.setText("0"+"min");
        }
    }
    //判断断开连接的弹窗
    public void createviewd() {
        notconnect = new AlertDialog.Builder(this)
                .setMessage("确定要断开连接吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        flag = 0;
                        shutdownClient();
                        lianjie.setText("连接");
                        bluetoothname.setText("蓝牙名字");
                        bluetoothaddress.setText("蓝牙地址");
                        finshpercent.setText("已完成");
                        standardwater.setText("2000ml");
                        standtemperture.setText("35℃");
                        an_water.setText("0ml");
                        an_temp.setText("0℃");
                        an_time.setText("0min");
                        onoff.setChecked(false);
                        onofffla=0;
                        mNManager.cancel(1);
                        notconnect.dismiss();

                    }//断开连接蓝牙
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notconnect.dismiss();
                    }
                }).create();
        notconnect.show();
    }

    private void textfunction() {
        //点击连接文本
        lianjie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waterA = 0;
                waterB = 0;
                if (flag == 0) {
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE);
                   // listener = new BluetoothStateListener();
                    createview();
                }
                else if (flag == 1) {
                    createviewd();
                }

            }
        });

        //显示日期
        Calendar calendar = Calendar.getInstance();//获取当前日期
        stringBuffer.append(calendar.get(Calendar.YEAR)).append("年").append((calendar.get(Calendar.MONTH)) + 1).append("月").append(calendar.get(Calendar.DAY_OF_MONTH)).append("日");
        date.setText(stringBuffer.toString());
        sql_date = stringBuffer.toString();

        //进入单双柱状图的分析
        quxian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==1)
                {
                    Intent intent = new Intent(Functiomain.this, QuXianActivity.class);
                    intent.putExtra("date",sql_date.toString());
                    intent.putExtra("address",sql_address.toString());
                    startActivity(intent);
                }
                else{
                    connecttip();
                }
            }
        });
        //标准水温的设置
        standtemperture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag==1)
                    createeditdialogs();
                else{
                    connecttip();
                }
            }
        });
//设置当天的喝水量
        standardwater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag==1)
                    choose();
                else
                {
                    connecttip();
                }
            }
        });

//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                  String ect = ""+e_content.getText().toString();
//                //   String ect="A1230C0025";
//                write(ect);
//            }
//        });//测试接收数据

        //定时
        timing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==0){
                    connecttip();
                }
                else{
                    cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    int mHour = cal.get(Calendar.HOUR_OF_DAY);
                    int mMinute = cal.get(Calendar.MINUTE);
                    new TimePickerDialog(Functiomain.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view,
                                                      int hourOfDay, int minute) {
                                    // TODO Auto-generated method stub
                                    cal.setTimeInMillis(System.currentTimeMillis());
                                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    cal.set(Calendar.MINUTE, minute);
                                    cal.set(Calendar.SECOND, 0);
                                    cal.set(Calendar.MILLISECOND, 0);
                                    // 建立Intent和PendingIntent来调用目标组件
                                    Intent intent = new Intent(Functiomain.this, AlarmReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(Functiomain.this, 0, intent, 0);
                                    // 获取闹钟管理的实例
                                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    // 设置闹钟
                                    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                                    System.out.println(cal.getTimeInMillis());

                                }
                            }, mHour, mMinute, true).show();
                }

            }
        });
    }
    //设置标准喝水量的方式选择
    public  void choose(){
        String[] Arry = new String[] { "自定义设置", "输入体重，帮你推算" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// 自定义对话框
        builder.setSingleChoiceItems(Arry, 0, new DialogInterface.OnClickListener() {// 默认的选中
            @Override
            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                choice=which;
            }
        })
                .setTitle("请选择设置的方式")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (choice==0){
                            createeditdialog();//手动设置
                        }
                        else{
                            createdd();//输入体重，系统推算
                        }
                    }
                }).create();
        builder.show();// 让弹出框显示
    }
    //设置当天喝水量----输入体重 ，系统推算出合适的喝水量
    public void createdd(){
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);//限制输入数字和小数点
        new AlertDialog.Builder(this).setTitle("请输入你的体重（Kg）：")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String result=et.getText().toString();
                        int id=yy.countid();
                        double Estimate=Double.valueOf(result)*40.0;
                        yy.updatestand(sql_address,sql_date.toString(),Estimate);
                        finshstatus();
                        result=String.valueOf(Estimate);
                        standardwater.setText(result+"ml");
                    }
                }).setNegativeButton("取消",null).show();
    }
    // 客户端线程
    private class ClientThread extends Thread {
        public void run() {
            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
                Message msg = new Message();
                msg.obj = "请稍候，正在连接蓝牙:" + device.getAddress();
                msg.what = 1;
                mHandler.sendMessage(msg);
                socket.connect();

                msg = new Message();
                msg.obj = "已经连接上蓝牙！";
                msg.what = 2;
                mHandler.sendMessage(msg);
                // 启动接受数据
                readtask = new Readtask();  //连接成功后开启读取数据的线程
                readtask.start();
            } catch (IOException e) {
                Message msg = new Message();
                msg.obj = "连接蓝牙异常！断开连接重新试一试。";
                msg.what = 0;
                mHandler.sendMessage(msg);
            }
        }
    }

    /**
     * 信息处理
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            boolean status;
            bluetoothlist.dismiss();
            String watered;
            double  watered_result=0.0;
            String   standard_water;
            double  standard_waterdata;
            String standard_tempture;
            double standard_tempturedata;
            if (msg.what==2) {
                sql_name = device.getName();
                sql_address = device.getAddress();
                bluetoothaddress.setText(sql_address);
                bluetoothname.setText(sql_name);
                lianjie.setText("断开");
                flag=1;
                onoff.setChecked(true);
                status=yy.judge_existence(sql_address,sql_date.toString());
                if (status){
                    //已存在数据条，不用添加数据条
                }
                else{
                    //添加数据条
                    int id=yy.countid();
                    yy.addnewdata(id, sql_address, sql_name, sql_date.toString(), 2000,31);
                }
                finshstatus();//任务百分比的展示
                watered_result=yy.searchdata(sql_address,sql_date.toString(),6);
                watered=String.valueOf(watered_result);
                an_water.setText(watered+"ml");//已喝水显示
                standard_waterdata=yy.searchdata(sql_address,sql_date.toString(),5);
                standard_water=String.valueOf(standard_waterdata);
                standardwater.setText(standard_water+"ml");//标准喝水量的显示
                standard_tempturedata=yy.searchdata(sql_address,sql_date.toString(),4);
                int standarddata_int=(int)standard_tempturedata;
                standard_tempture=String.valueOf(standarddata_int);
                standtemperture.setText(standard_tempture+"℃");//标准水温的显示
                String timea=an_temp.getText().toString().substring(0,an_temp.getText().toString().length()-1);
                double dtime=Double.parseDouble(timea);
                String timeb=standtemperture.getText().toString().substring(0,standtemperture.getText().toString().length()-1);
                double dtime1=Double.parseDouble(timeb);
                time_todrink=dtime1-dtime;//距离可以喝水的时间
                if (time_todrink>0)
                {
                    string_time_todrink=String.valueOf(time_todrink);
                    an_time.setText(string_time_todrink+"min");
                }
                else{
                    an_time.setText("0"+"min");
                }
                Notification notification = new NotificationCompat.Builder(Functiomain.this)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentTitle("连接成功~").setContentText("IntelientBottle").setSmallIcon(R.drawable.bottle)
                        .setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.bottle).setTicker("IntelientBottle发来一条新消息").build();
                mNManager.notify(1, notification);
                dealwith();
                write("S");
            }
            else if (msg.what==0){
                Toast.makeText(Functiomain.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
            }
            else {
                //     Toast.makeText(Functiomain.this,info,Toast.LENGTH_SHORT).show();
            }
        }

    };

    /* ?停止客户端连接 */
    private void shutdownClient() {
        new Thread() {
            public void run() {
                if (mClientThread != null) {
                    mClientThread.interrupt();
                    mClientThread = null;
                }
                if (readtask != null) {
                    readtask.interrupt();
                    readtask = null;
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        }.start();
   }
//    /* 监听socker连接 */
//    private void jiantingsocket() {
//        new Thread() {
//            public void run() {
//                if (socket != null) {
////                    Message m = new Message();
////                    m.obj = "1";
////                    mhandler.sendMessage(m);
//                    if (mClientThread.isInterrupted()){
//                        Message m = new Message();
//                        m.obj = "2";
//                        mhandler.sendMessage(m);
//                    }
////                  if (socket.isConnected()){
////                      Message m = new Message();
////                    m.obj = "1";
////                    mhandler.sendMessage(m);
////                  }
//                  else{
//                      Message m = new Message();
//                      m.obj = "1";
//                      mhandler.sendMessage(m);
//                  }
//                }
//            }
//
//            ;
//        }.start();
//    }
//    private Handler mhandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            String info = (String) msg.obj;
//            if (info.equals("1")) {
//                Toast.makeText(Functiomain.this,"连接成功",Toast.LENGTH_SHORT).show();
//            }
//            else if (info.equals("2"))
//                Toast.makeText(Functiomain.this,"连接断开",Toast.LENGTH_SHORT).show();
//        }
//    };
    //任务百分比的显示
    public  void finshstatus(){
        double a,b,c;
        String result;
        DecimalFormat df = new DecimalFormat("#####0.00");//保留小数点后两位
        a=yy.searchdata(sql_address,sql_date.toString(),5);
        b=yy.searchdata(sql_address,sql_date.toString(),6);
        c=b/a*100;
        if (c>100)
            c=100;
        result=df.format(c);
        finshpercent.setText("已完成"+"\n"+result+"%");
    }
    /**
     * 数据的传递
     */
    public void write(String ff) {
        OutputStream outputStream = null;

        //String text = " " + ff;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (outputStream != null) {
            // 以utf-8的格式发送出去
            try {
                System.out.print("ff1="+ff);
                outputStream.write(ff.getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //  Toast.makeText(Functiomain.this, "消息已发出，等待服务端接收" + text, Toast.LENGTH_SHORT).show();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String econtent;
            double waterD = 0;
            econtent = (String) msg.obj;//获取接收的内容
           // Toast.makeText(Functiomain.this,econtent,Toast.LENGTH_SHORT).show();
         //    String ll=econtent.substring(econtent.length()-1,econtent.length());
            if ((econtent.substring(econtent.length()-4,econtent.length())).toString().equals("open")){
                Toast.makeText(Functiomain.this,"已开",Toast.LENGTH_SHORT).show();
                write("S");
            }
            else  if ((econtent.substring(econtent.length()-5,econtent.length())).toString().equals("close")){
                Toast.makeText(Functiomain.this,"已关",Toast.LENGTH_SHORT).show();
                write("S");
            }
            else  if (econtent.substring(0, 1).equals("A")) {
                String  resulta=econtent.substring(1,5);
                double receivewater = Double.parseDouble(resulta);
                sql_date = date.getText().toString();
                if (receivewater >10) {
//                    count0 = 0;
//                   water0flag = -1;
                    if (waterA == 0) {
                        waterA = receivewater;
                    }
                    else {
                        waterB = receivewater;
                        waterD = waterA - waterB;
                        if (waterD <= 5) {
                            waterA = waterB;
                            waterB=0;
                        }
                        else {
                            waterA = waterB;
                            waterB=0;
                            int timeflag = juedetime();
                            yy.updatewater( sql_address, sql_date.toString(), timeflag, waterD);
                            double  water_re = yy.searchdata(sql_address, sql_date.toString(),6);
                            String water_xre = String.valueOf(water_re);
                            an_water.setText(water_xre+"ml");
                            finshstatus();
                        }
                    }
                }
//                else {
//                    if (water0flag == -1) {
//                        water0flag = 0;
//                        //count0 = 1;
//                    }
//                    else {
//                        count0++;
//                    }
//                    if (count0 == 20) {
//                        write("E20");
//                        count0 = 0;
//                    }
//                }
                String resualtb=econtent.substring(8,10);
                StringBuffer stemp = new StringBuffer();
                stemp.append(resualtb).append("℃");
                an_temp.setText(stemp.toString());
                String timea=an_temp.getText().toString().substring(0,an_temp.getText().toString().length()-1);
                double dtime=Double.parseDouble(timea);
                String timeb=standtemperture.getText().toString().substring(0,standtemperture.getText().toString().length()-1);
                double dtime1=Double.parseDouble(timeb);
                time_todrink=dtime1-dtime;
                if (time_todrink>0)
                {
                    string_time_todrink=String.valueOf(time_todrink);
                    an_time.setText(string_time_todrink+"min");
                }
                else{
                    an_time.setText("0"+"min");
                }
                write("S");
            }//接收的数据是阶段性的水里
            else if (econtent.substring(0, 1).equals("B")) {
                Notification notification = new NotificationCompat.Builder(Functiomain.this)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentTitle("到时间喝水啦~").setContentText("IntelientBottle").setSmallIcon(R.drawable.bottle)
                        .setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.image).setTicker("IntelientBottle发来一条新消息").build();
                mNManager1.notify(1, notification);
                String min=econtent.substring(1,econtent.length());
                an_time.setText(min+"min");
                //  write("S");
            }//接收的所需的时间

            else if (econtent.substring(0,1).equals("F")){
                Notification notification = new NotificationCompat.Builder(Functiomain.this)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentTitle("到时间喝水啦~").setContentText("IntelientBottle").setSmallIcon(R.drawable.bottle)
                        .setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.image).setTicker("IntelientBottle发来一条新消息").build();
                mNManager1.notify(1, notification);
                String min=econtent.substring(1,econtent.length());
                an_time.setText(min+"min");
                //  write("S");
            }
            else if ((econtent.substring(econtent.length()-1,econtent.length())).toString().equals("D")){
                Toast.makeText(Functiomain.this,"kk",Toast.LENGTH_SHORT).show();
                onoff.setChecked(false);
            }
         else {

            }

        }
    };

    //判断当前时间点是在哪个时间阶段
    private int juedetime() {
        int tflag = 0;
        Calendar c = Calendar.getInstance();
        int time = c.get(Calendar.HOUR_OF_DAY);
        System.out.println("tine="+time);
        if (time >= 0 && time < 6)
            tflag = 1;
        else if (time >= 6 && time < 12)
            tflag = 2;
        else if (time >= 12 && time < 18)
            tflag = 3;
        else tflag = 4;
        return tflag;
    }

    /**
     * 动态请求权限后，返回页面时的回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "权限已获取", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "权限未获取", Toast.LENGTH_SHORT).show();
            finish();//关闭页面
        }
    }

    /**
     * 数据回调方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
                getMyBondedDevices();//获取绑定的蓝牙设备
                adapter.notifyDataSetChanged();//刷新适配器
            } else {
                Toast.makeText(this, "蓝牙未开启", Toast.LENGTH_SHORT).show();
            }
        } else {

        }
    }

    /**
     * 获取已经绑定的蓝牙设置
     */
    private void getMyBondedDevices() {
        //获取所有已经绑定了的设备
        deviceNames.clear();//清除设备名称的集合
        devices.clear();//清除蓝牙设备对象的集合
        if (bTAdatper.getBondedDevices() != null) {//如果蓝牙适配器对象不为空时
            //获取里面的数据的对象
            List<BluetoothDevice> liset = new ArrayList<>(bTAdatper.getBondedDevices());
            devices.addAll(liset);
            //拿到适配器对象的名称数据
            for (BluetoothDevice device : liset) {
                deviceNames.add(device.getName());
            }

        }
    }

    /**
     * 开辟线程读任务
     */
    public class Readtask extends Thread {
        @Override
        public void run() {
            int bytes = 0;
            InputStream inputStream;   //建立输入流读取数据
            while (true) {
                try {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    inputStream = socket.getInputStream();
                    byte[] buffer = new byte[1024];

                    // 无线循环来接收数据
                    if ((bytes = inputStream.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        String s = new String(buf_data);
                        Message msg = new Message();

                        msg.obj = s;
                        System.out.println("s="+s);
                        //  发送数据
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
//                    Toast.makeText(Functiomain.this,"读取失败",Toast.LENGTH_LONG).show();
                    break;
                }
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("TAG", e.toString());
                }
            }
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //将获取的City赋值给定位的tlocation
            /**
             *1.国家:location.getCountry()
             * 2.城市:location.getCity()
             * 3.区域(例：天河区)：location.getDistrict()
             * 4.地点(例：风信路)：location.getStreet()
             * 5.详细地址：location.getAddrStr()
             */
            tlocation.setText(location.getAddrStr());
            Toast.makeText(Functiomain.this,"网络定位成功",Toast.LENGTH_LONG).show();
        }
        public void onReceivePoi(BDLocation arg0) {
        }
    }

    //执行onDestroy()方法，停止定位
    @Override
    public void onDestroy() {
        mLocationClient.stop();
        super.onDestroy();
    }

    //设置相关参数
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); //打开gps
        option.setAddrType("all");//返回定位结果包含地址信息
        option.setPriority(LocationClientOption.NetWorkFirst); // 设置网络优先
        option.setPriority(LocationClientOption.GpsFirst);       //gps
        option.disableCache(true);//禁止启用缓存定位
        mLocationClient.setLocOption(option);
    }
    //从数据库中调出该蓝牙相应的数据内容
    public void searchdataindb(){
        properid=yy.searchproperid(sql_address.toString());//将数据库中属于这个address的蓝牙的数据的ID调出来
        standardw=new int[properid.length];
        water=new int[properid.length];
        for(int i=0;i<properid.length;i++)
        {
            standardw[i]=(int)yy.searcharraystandardw(properid[i],sql_address.toString());
            water[i]=(int)yy.searcharraywatered(properid[i],sql_address.toString());
        }
    }
    //弹出相应的温馨提示
    public void dealwith(){
        searchdataindb();
        int length=standardw.length;
        String tip="hh";
        int count=0;
        if (length>=4){
            istand= new int[]{ standardw[length-4],  standardw[length-3],  standardw[length-2], standardw[length-1]};
            iwater=new int[]{water[length-4],water[length-3], water[length-2], water[length-1]};
        }
        else{
            if (length==1){
                istand= new int[]{0, 0, 0, standardw[0]};
                iwater=new int[]{0, 0, 0, water[0]};
            }
            else if (length==2){
                istand= new int[]{0, 0, standardw[0], standardw[1]};
                iwater=new int[]{0, 0, water[0], water[1]};
            }
            else if (length==3){
                istand= new int[]{0, standardw[0], standardw[1], standardw[2]};
                iwater=new int[]{0, water[0], water[1], water[2]};
            }
        }
        for (int i=0;i<4;i++){
            if (istand[i]>iwater[i])
                count++;
        }
        if (count==0){
            tip = "这几天的喝水量都很正常<img  src='yes'  />";
            tip +="，请继续保持呀！<img  src='fight' />";
        }
        else if (count==1){
            tip = "近四天中，主人你有一天的喝水量没有达到设定的标准值<img  src='sad'  />";
            tip +="，良好的饮水习惯有益于你的身心健康！<img  src='smile' />";
        }
        else if (count==2){
            tip = "近四天中，主人你有两天的喝水量没有达到设定的标准值<img  src='sad'  />";
            tip +="，良好的饮水习惯有益于你的身心健康！<img  src='smile' />";
        }
        else if (count==3){
            tip = "近四天中，主人你有三天的喝水量没有达到设定的标准值<img  src='sad'  />";
            tip +="，良好的饮水习惯有益于你的身心健康！<img  src='smile' />";
        }
        else{
            tip = "近四天中，主人你没有一天的喝水量没有达到设定的标准值<img  src='sad'  />";
            tip +="，良好的饮水习惯有益于你的身心健康！<img  src='smile' />";
        }

        CharSequence charSequence = Html.fromHtml(tip, new Html.ImageGetter()
        {
            public Drawable getDrawable(String source)
            {
                //转载图像资源
                Drawable drawable = getResources().getDrawable(getResourceId(source));
                //  if (source.equals("image"))
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());

                return drawable;
            }
        }, null);
        alte = new AlertDialog.Builder(this)
                .setTitle("温馨提示")
                .setMessage(charSequence)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alte.dismiss();
                    }//断开连接蓝牙
                }).create();
        alte.show();
    }//处理双状图的数据


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public int getResourceId(String name)//name参数标识R.drawable中的图像文件名
    {
        try
        {
            Field field = R.drawable.class.getField(name);//根据资源ID的变量(也就是资源的文件名)名获取Field对象
            return Integer.parseInt(field.get(null).toString());//取得并返回资源ID字段(静态变量)的值
        }
        catch (Exception e)
        {
        }
        return 0;
    }

    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return filter;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TAG", "onReceive---------");
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.e("TAG", "onReceive---------STATE_TURNING_ON");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            bluetoothStatus="on";
                          Log.e("TAG", "onReceive---------STATE_ON");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                           Log.e("TAG", "onReceive---------STATE_TURNING_OFF");
                            //Ble.toReset(mContext);
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            bluetoothStatus="off";
                             Log.e("TAG", "onReceive---------STATE_OFF");
                            flag = 0;
                            shutdownClient();
                            lianjie.setText("连接");
                            bluetoothname.setText("蓝牙名字");
                            bluetoothaddress.setText("蓝牙地址");
                            finshpercent.setText("已完成");
                            standardwater.setText("2000ml");
                            standtemperture.setText("35℃");
                            an_water.setText("0ml");
                            an_temp.setText("0℃");
                            an_time.setText("0min");
                            onoff.setChecked(false);
                            onofffla=0;
                            mNManager.cancel(1);
                            //Ble.toReset(mContext);
                            break;
                    }
                    break;
            }
        }
    };
}
