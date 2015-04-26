package com.sushimaan.shiragiku;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.chanimagedownloader.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



public class ImageDownloaderActivity extends Activity {
	//private ArrayList<String> image_list = new ArrayList<String>();
	ProgressDialog mProgressDialog;
	static boolean allow_jpg;
	static boolean allow_png;
	static boolean allow_webm;
	static boolean allow_gif;
	static boolean scrape_404;
	
//	public void setList(ArrayList<String> list){
//		this.image_list = list;
//	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_downloader);
		Intent i = getIntent();
		String url_link = i.getStringExtra("url_link");
		ImageDownloaderActivity.allow_jpg = i.getExtras().getBoolean("jpg");
		ImageDownloaderActivity.allow_png = i.getExtras().getBoolean("png");
		ImageDownloaderActivity.allow_webm = i.getExtras().getBoolean("webm");
		ImageDownloaderActivity.scrape_404 = i.getExtras().getBoolean("scrape");
		ImageDownloaderActivity.allow_gif = i.getExtras().getBoolean("gif");
		
		mProgressDialog = new ProgressDialog(ImageDownloaderActivity.this);
		mProgressDialog.setMessage("Downloading");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
		
		
		final ParseHtml task = new ParseHtml(this);
		task.execute(url_link);
		
		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
			@Override
			public void onCancel(DialogInterface dialog){
				task.cancel(true);
			}
		});
	}
	
	@Override
   	protected void onStart(){
   		super.onStart();
   	}
   	
   	@Override
   	protected void onResume(){
   		super.onResume();
   	}
   	
   	@Override
   	protected void onRestart(){
   		super.onRestart();
   	}
   	private class ParseHtml extends AsyncTask<String, Integer, String>{
   		
   		
   		//private ImageDownloaderActivity activity;
   		String mfilename = null;
   		private Context context;
   		private PowerManager.WakeLock mWakeLock;
   		public ParseHtml(Context context){
   			this.context = context;
   		}
   		
   		@Override
   		protected String doInBackground(String... params){
   			//List<String> image_list = new ArrayList<String>();
   			try {
   				
   				RequestHandler rh = new RequestHandler();
		
   				String[] parts = params[0].split("/");
   				String board = parts[3];
   				
   				
   				String folderPath = Environment.getExternalStorageDirectory()+ "/threads";
   				File file = new File(folderPath);
   				if(!file.exists()){
   					file.mkdirs();
   				}
   				
   				String closed = "0";
   				JSONObject OP;
   				int resume = 0;
   				do{
   					String jsonString = rh.requestJSON(params[0]);
   	   				
   	   				JSONObject jsonObject = new JSONObject(jsonString);
   	   				JSONArray posts = jsonObject.getJSONArray("posts");
   	   				
   	   				OP = posts.getJSONObject(0);
   	   				if(OP.has("closed")){
   	   					closed = OP.getString("closed");
   	   				}
   					for(int i= resume; i < posts.length(); i++){
   						JSONObject post = posts.getJSONObject(i);
   						if( !post.has("ext")){
   							continue;
   						}
   						String ext = post.getString("ext");
 
   						if(ext.equals(".jpg") && ImageDownloaderActivity.allow_jpg){
   							String tim = post.getString("tim");
   							String filename = post.getString("filename");
   							String img_link = "http://i.4cdn.org/"+board+"/"+tim+ext;
   							filename = filename+ext;
   						
   						
   							String x = saveFile(img_link, filename, folderPath);
   							if (x == null){

   								break;
   							}
   							continue;
   						}
   						else if(ext.equals(".png") && ImageDownloaderActivity.allow_png){
   							String tim = post.getString("tim");
   							String filename = post.getString("filename");
   							String img_link = "http://i.4cdn.org/"+board+"/"+tim+ext;
   							filename = filename+ext;
   						
   							String x = saveFile(img_link, filename, folderPath);
   							if (x == null){
   								break;
   							}
   							continue;
   						}
   						else if(ext.equals(".webm") && ImageDownloaderActivity.allow_webm){
   							String tim = post.getString("tim");
   							String filename = post.getString("filename");
   							String img_link = "http://i.4cdn.org/"+board+"/"+tim+ext;
   							filename = filename+ext;
   						
   							String x = saveFile(img_link, filename, folderPath);
   							if (x == null){
   								break;
   							}
   							continue;
   						}
   						else if(ext.equals(".gif") && ImageDownloaderActivity.allow_gif){
   							String tim = post.getString("tim");
   							String filename = post.getString("filename");
   							String img_link = "http://i.4cdn.org/"+board+"/"+tim+ext;
   							filename = filename+ext;
   						
   							String x = saveFile(img_link, filename, folderPath);
   							if (x == null){
   								break;
   							}
   							continue;
   						}
   						else{continue;}				
   					}
   					resume =  posts.length();
   					Thread.sleep(20);
   				}while(ImageDownloaderActivity.scrape_404 && !closed.equals("1"));
   				
   				return "ok";
   			}catch(Exception e){
   				Log.e("error: ", e.toString());
   			return "ok";
   		}
     }
       
		
   		
   		@Override
   		protected void onPreExecute(){
   			super.onPreExecute();
   			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
   			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
   			mWakeLock.acquire();
   			mProgressDialog.show();
   		}
   		
   		@Override
   		protected void onProgressUpdate(Integer... progress){
   			super.onProgressUpdate(progress);
   			mProgressDialog.setMessage("Downloading: " + mfilename);
   			mProgressDialog.setIndeterminate(false);
   			mProgressDialog.setMax(100);
   			mProgressDialog.setProgress(progress[0]);
   		}
   		@Override
   		protected void onCancelled(){
   			onPostExecute("ok");
   		}
   		@Override
   		protected void onPostExecute(String result){
   		
   			mWakeLock.release();
   			mProgressDialog.dismiss();
   			Button scrape_another = (Button)findViewById(R.id.scrape_another);
   			scrape_another.setOnClickListener(new OnClickListener(){
   				public void onClick(View v){
   					Intent back = new Intent(getBaseContext(), UrlActivity.class);
   					startActivity(back);
   					finish();
   				}
   			});
   		}
	
   		private String saveFile(String image, String filename, String folderPath){
			
        	InputStream input = null;
			OutputStream output = null;
			HttpURLConnection conn = null;
				
			try{
					URL url = new URL(image);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linuxi686) Gecko/20071127 Firefox/2.0.0.11");
					conn.connect();
					if(conn.getResponseCode() != HttpURLConnection.HTTP_OK){
						return "Server returned HTTP" + conn.getResponseCode() + ""+ conn.getResponseMessage();
					}
					
	   			    mfilename = filename;
					int fileLength = conn.getContentLength();
					input = conn.getInputStream();
					output = new FileOutputStream(folderPath + "/" + filename);
					
					byte data[] = new byte[4096];
					long total = 0;
					int count;
					
					while((count = input.read(data)) != -1){
						if(isCancelled()){
							input.close();		
							return null;
						}
						total += count;
						if(fileLength > 0) publishProgress((int) (total * 100/fileLength));
						output.write(data, 0, count);
					}
				}catch (Exception e){
					return e.toString();
				}finally{
					try{
						if(output != null)
							output.close();
						if(input != null)
							input.close();
					}catch (IOException ignored){
					}
					
					if(conn != null)
						conn.disconnect();
				}
			return "ok";
			}
   	}
}
   				