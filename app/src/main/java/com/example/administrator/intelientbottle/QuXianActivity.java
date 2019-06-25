package com.example.administrator.intelientbottle;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/10/31.
 */

public class QuXianActivity extends exitsystem {
    private LinearLayout customBarChart1, customBarChart2;
    TextView t_date;//传输过来的日期值
    private DBManager yy;//数据库的
    int[] tdata = {0,0,0,0};
    double temp=0.0;
    TextView desribe1;//描述单柱状图
    String redate;
    String address;
    int[] properid;
    int[] standard;
    int[] water;
    int[] istand;
    int[] iwater;
    TextView desribe2;//描述双柱状图
    TextView exit;
    String analyst;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quxian);
        init();
        initBarChart1();
         redate=getIntent().getStringExtra("date");
         address=getIntent().getStringExtra("address");

        t_date.setText(redate);
        for(int i=0;i<4;i++){
            temp=yy.receivewater(address,redate,i+1);
            tdata[i]=(int)temp;
        }
        texrfunction();
        searchdata();
        dealwith();
        initBarChart2();
    }
    @Override
    //重写onKeyDown方法,对按键(不一定是返回按键)监听
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//当返回按键被按下
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);//新建一个对话框
            dialog.setMessage("确定要退出柱状图分析吗?");//设置提示信息
            //设置确定按钮并监听
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();//结束当前Activity
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
    public void init(){
        customBarChart1 = (LinearLayout) findViewById(R.id.customBarChart1);
        customBarChart2 = (LinearLayout) findViewById(R.id.customBarChart2);
        t_date=(TextView)findViewById(R.id.T_date);
        yy = new DBManager(this);
        desribe1=(TextView)findViewById(R.id.describe);
        desribe2=(TextView)findViewById(R.id.describe1);
        exit=(TextView)findViewById(R.id.describe3);
    }
    /**
     * 初始化柱状图1数据
     */
    private void initBarChart1() {
        String[] xLabel = {"","凌晨", "上午", "下午", "晚上"};
        String[] yLabel = {"0", "600", "1200", "1800", "2400", "3000", "3600", "4200", "4800", "5600"};
        List<int[]> data = new ArrayList<>();
        data.add(tdata);
        List<Integer> color = new ArrayList<>();
        color.add(R.color.color12);
        color.add(R.color.color13);
        color.add(R.color.color16);
        customBarChart1.addView(new CustomBarChart(this, xLabel, yLabel, data, color));
    }
    /**
     * 初始化柱状图2数据
     */
    private void initBarChart2() {
        String[] xLabel = {"","前三天", "前两天", "前一天", "当天"};
        String[] yLabel = {"0", "600", "1200", "1800", "2400", "3000", "3600", "4200", "4800", "5600"};
        List<int[]> data = new ArrayList<>();
        data.add(iwater);
        data.add(istand);
        List<Integer> color = new ArrayList<>();
        color.add(R.color.color14);
        color.add(R.color.color15);
        color.add(R.color.color11);
        customBarChart2.addView(new CustomBarChart(this, xLabel, yLabel, data, color));
    }
public void texrfunction(){
    double watered,towater,percent;
    String swatered,stotalwater,spercent;
    watered=yy.searchdata(address,redate,6);
    swatered=String.valueOf(watered);
    towater=yy.searchdata(address,redate,5);
    stotalwater=String.valueOf(towater);
    DecimalFormat df = new DecimalFormat("#####0.00");
    percent=watered/towater*100;
    spercent=df.format(percent);
    desribe1.setText("      "+"今天设置的标准喝水量为"+stotalwater+"ml,"+"截止到现在为止，主人你今天已喝水"+swatered+"其中凌晨喝水"+String.valueOf(tdata[0])+"ml,"+"上午喝水"+String.valueOf(tdata[1]
    +"ml,"+"中午喝水"+String.valueOf(tdata[2]+"ml,"+"晚上喝水"+String.valueOf(tdata[3]))+"ml,"+"."+"按着今天的标准喝水量，主人你已完成了"+spercent+"%"
    ));

    exit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           finish();
        }
    });
}
public void searchdata(){
    int a1,a2;
    properid=yy.searchproperid(address);//将数据库中属于这个address的蓝牙的数据的ID调出来
    standard=new int[properid.length];
    water=new int[properid.length];
     for(int i=0;i<properid.length;i++)
     {
         standard[i]=(int)yy.searcharraystandardw(properid[i],address);
         water[i]=(int)yy.searcharraywatered(properid[i],address);
     }
}
public void dealwith(){

    int length=standard.length;
    int count=0;
    if (length>=4){
        istand= new int[]{ standard[length-4],  standard[length-3],  standard[length-2], standard[length-1]};
        iwater=new int[]{water[length-4],water[length-3], water[length-2], water[length-1]};
    }
    else{
        if (length==1){
            istand= new int[]{0, 0, 0, standard[0]};
            iwater=new int[]{0, 0, 0, water[0]};
        }
        else if (length==2){
            istand= new int[]{0, 0, standard[0], standard[1]};
            iwater=new int[]{0, 0, water[0], water[1]};
        }
        else if (length==3){
            istand= new int[]{0, standard[0], standard[1], standard[2]};
            iwater=new int[]{0, water[0], water[1], water[2]};
        }
    }
    for (int i=0;i<4;i++){
        if (istand[i]>iwater[i])
            count++;
    }
    if (count==0){
        analyst = "这几天的喝水量都很正常<img  src='yes'  />";
        analyst +="，请继续保持呀！<img  src='fight' />";
    }
    else if (count==1){
        analyst = "近四天中，主人你有一天的喝水量没有达到设定的标准值<img  src='sad'  />";
        analyst +="，良好的饮水习惯有益于你的身心健康！<img  src='smile' />";
    }
    else if (count==2){
        analyst = "近四天中，主人你有两天的喝水量没有达到设定的标准值<img  src='sad'  />";
        analyst +="，良好的饮水习惯有益于你的身心健康！<img  src='smile' />";
    }
    else if (count==3){
        analyst = "近四天中，主人你有三天的喝水量没有达到设定的标准值<img  src='sad'  />";
        analyst +="，良好的饮水习惯有益于你的身心健康！<img  src='smile' />";
    }
    else{
        analyst = "近四天中，主人你没有一天的喝水量没有达到设定的标准值<img  src='sad'  />";
        analyst +="，良好的饮水习惯有益于你的身心健康！<img  src='smile' />";
    }

    CharSequence charSequence = Html.fromHtml(analyst, new Html.ImageGetter()
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
    desribe2.setText(charSequence);
    desribe2.setMovementMethod(LinkMovementMethod.getInstance());

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


}
