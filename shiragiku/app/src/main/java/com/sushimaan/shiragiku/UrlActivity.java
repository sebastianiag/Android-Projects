package com.sushimaan.shiragiku;


import com.example.chanimagedownloader.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;



public class UrlActivity extends Activity {
	private boolean jpg = false;
	private boolean png = false;
	private boolean webm = false;
	private boolean gif = false;
	private boolean scrape = false; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Unibody8Pro-Regular.ttf");
    	FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Unibody8Pro-Bold.ttf");
    	FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/Unibody8Pro-Regular.ttf");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url);
        
        //Setting the title
        String title = "<font color=#F8F8F2>Shira</font><font color=#A6E22E>giku</font>";
        TextView mTitle = (TextView)findViewById(R.id.title);
        mTitle.setText(Html.fromHtml(title));
        
        //Check buttons        
        String include_gif = "<font color=#F8F8F2>Include</font><font color=#A6E22E> .gif</font>";
        CheckBox option5 = (CheckBox)findViewById(R.id.include_gif);
        option5.setText(Html.fromHtml(include_gif));
        option5.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		gif = true;
        	}
        });
        
        String include_jpg = "<font color=#F8F8F2>Include</font><font color=#A6E22E> .jpg</font>";
        CheckBox option1 = (CheckBox)findViewById(R.id.include_jpg);
        option1.setText(Html.fromHtml(include_jpg));
        
        option1.setOnClickListener( new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		jpg = true;
        	}
        });
        
        String include_png = "<font color=#F8F8F2>Include</font><font color=#A6E22E> .png</font>";
        CheckBox option2 = (CheckBox)findViewById(R.id.include_png);
        option2.setText(Html.fromHtml(include_png));
        
        option2.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		png = true;
        	}
        });
        
        
        String include_webm = "<font color=#F8F8F2>Include</font><font color=#A6E22E> .webm</font>";
        CheckBox option3 = (CheckBox)findViewById(R.id.include_webm);
        option3.setText(Html.fromHtml(include_webm));
        
        option3.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		webm = true;
        	}
        });
        
        String scrape_404 = "<font color=#F8F8F2>Scrape images until</font><font color=#AE81FF> 404</font>";
        CheckBox option4 = (CheckBox)findViewById(R.id.scrape_404);
        option4.setText(Html.fromHtml(scrape_404));
        
        option4.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		scrape = true;
        	}
        });
        
        //Input and `download` button
        final EditText url = (EditText)findViewById(R.id.url_input);
        Button downButton = (Button)findViewById(R.id.download_button);
        
        
        downButton.setOnClickListener( new OnClickListener () {
        	
        	@Override
        	public void onClick(View v){
        		String url_link = url.getText().toString().trim();
        		Intent i = new Intent(getBaseContext(), ImageDownloaderActivity.class);
        		i.putExtra("url_link", url_link);
        		i.putExtra("gif", gif);
        		i.putExtra("jpg", jpg);
        		i.putExtra("png", png);
        		i.putExtra("webm", webm);
        		i.putExtra("scrape", scrape);
        		startActivity(i);
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
}
