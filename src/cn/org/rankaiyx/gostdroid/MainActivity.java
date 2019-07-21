package cn.org.rankaiyx.gostdroid;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import cn.org.rankaiyx.gostdroid.R;
import android.content.Context;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;



/** 看不懂注释我就吃半斤狗粮 :-) */
public class MainActivity extends ActionBarActivity {

	private EditText et_cmd;
	private String app_path;
	private TextView tv_result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		
		/*初始化控件*/
		et_cmd = (EditText) findViewById(R.id.et_cmd);
		tv_result = (TextView) findViewById(R.id.tv_result);
		/* 获取app安装路径 */
		app_path = getApplicationContext().getFilesDir().getAbsolutePath();
		
		//复制gost程序文件
		varifyFile(getApplicationContext(), "gost");
		
		
		String initcmd =  "." + app_path + "/gost -L=:1080?dns=114.114.114.114:53/tcp";
		et_cmd.setText(initcmd);
		
		
		
	    initcmd =  "netstat -tnl";
		List<String> results = exe(initcmd,true);

		String result = "";
		/* 将结果转换成字符串, 输出到 TextView中 */
		for(String line : results){
			result += line + "\n";
		}
		tv_result.setText(result);	
	}


	/** 按钮点击事件 */
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {

		case R.id.exe: /* 执行Editext中的命令 */
			String cmd = et_cmd.getText().toString();
			/* 执行脚本命令 */
			
			exe(cmd,false);
			
			try{
				TimeUnit.MILLISECONDS.sleep(3000); //MILLISECONDS表示以毫秒为单位延时
			}
			catch (Exception e) {
                e.printStackTrace();
            }
			
			cmd = "netstat -tnl";
			List<String> results = exe(cmd,true);
			

			String result = "";
			/* 将结果转换成字符串, 输出到 TextView中 */
			for(String line : results){
				result += line + "\n";
			}
			tv_result.setText(result);
			break;

		default:
			break;
		}
	}

	/** 验证文件是否存在, 如果不存在就拷贝 */
	private void varifyFile(Context context, String fileName) {


        try {
        	/* 查看文件是否存在, 如果不存在就会走异常中的代码 */
        	context.openFileInput(fileName);
        } catch (FileNotFoundException notfoundE) {
            try {
            	/* 拷贝文件到app安装目录的files目录下 */
                copyFromAssets(context, fileName, fileName);
                /* 修改文件权限脚本 */
                String script = "chmod 700 " + app_path + "/" + fileName;
                /* 执行脚本 */
                exe(script,false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
	
	/** 将文件从assets目录中拷贝到app安装目录的files目录下 */
	private void copyFromAssets(Context context, String source,
			String destination) throws IOException {
		/* 获取assets目录下文件的输入流 */
		InputStream is = context.getAssets().open(source);
		/* 获取文件大小 */
		int size = is.available();
		/* 创建文件的缓冲区 */
		byte[] buffer = new byte[size];
		/* 将文件读取到缓冲区中 */
		is.read(buffer);
		/* 关闭输入流 */
		is.close();
		/* 打开app安装目录文件的输出流 */
		FileOutputStream output = context.openFileOutput(destination,
				Context.MODE_PRIVATE);
		/* 将文件从缓冲区中写出到内存中 */
		output.write(buffer);
		/* 关闭输出流 */
		output.close();
	}
	
	/** 执行 shell 脚本命令 */
	private List<String> exe(String cmd,boolean enable_results_show) {
		/* 获取执行工具 */
		Process process = null; 
		/* 存放脚本执行结果 */
        List<String> list = new ArrayList<String>();  
        try {  
        	/* 获取运行时环境 */
        	Runtime runtime = Runtime.getRuntime();
        	/* 执行脚本 */
            process = runtime.exec(cmd); 
            
            String line = null; 
            /* 获取脚本结果的输入流 */
            if(enable_results_show == true)
            {
            	InputStream is = process.getInputStream(); /*getErrorStream*/
            	BufferedReader br = new BufferedReader(new InputStreamReader(is));
            	//String line = null;  
            	/* 逐行读取脚本执行结果 */
            	while ((line = br.readLine()) != null) {  
            		list.add(line); 
            	}
            	br.close(); 
            }
        } catch (IOException e) {  
            e.printStackTrace();  
        } 
        return list;
	}
	
}
