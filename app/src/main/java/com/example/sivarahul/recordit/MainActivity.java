package com.example.sivarahul.recordit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity {

    TextView status;
    String outputFile;
    MediaRecorder myRecorder;
    //MediaPlayer myPlayer;
    ImageButton starts;
    ImageButton stopsrec;
    SimpleDateFormat dateFormat;
    String currentTimeStamp;

    // ImageButton plays;
    //ImageButton stops;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = (TextView) findViewById(R.id.tv1);
        starts = (ImageButton) findViewById(R.id.startrec);
        stopsrec = (ImageButton) findViewById(R.id.stoprec);
        listings();
        // plays=(ImageButton)findViewById(R.id.play);
        //stops=(ImageButton)findViewById(R.id.stop);
        outputFile = Environment.getExternalStorageDirectory().
                getAbsolutePath() + "/rec" + getCurrentTimeStamp() + "records.mp3";
        myRecorder = new MediaRecorder();
        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myRecorder.setOutputFile(outputFile);

        ///
        starts.setOnClickListener(new OnClickListener() {
            // boolean mStartRecording = true;
            @Override
            public void onClick(View v) {

                startRecording(v);

            }
        });


        stopsrec.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording(v);

            }
        });
    }

    protected void stopRecording(View v) {
        try {
            myRecorder.stop();
            myRecorder.reset();
            myRecorder.release();

            myRecorder = null;

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            // no valid audio/video data has been received
            e.printStackTrace();
        }
        stopsrec.setEnabled(false);
        starts.setEnabled(true);
        //plays.setEnabled(true);
        status.setText("Recording Stopped");
        status.setTextColor(Color.RED);

        Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();
        refresh();
    }

    public void refresh() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    protected void startRecording(View v) {
        try {
            myRecorder.prepare();
            myRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //plays.setEnabled(false);
        stopsrec.setEnabled(true);
        starts.setEnabled(false);
        status.setText("Recording");
        status.setTextColor(Color.GREEN);
        Toast.makeText(getApplicationContext(), "Recording...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (myRecorder != null) {
            myRecorder.release();
            myRecorder = null;
        }

       /* if (myPlayer != null) {
            myPlayer.release();
            myPlayer = null;
        }*/
    }

    public String getCurrentTimeStamp() {
        try {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            currentTimeStamp = dateFormat.format(new Date()); // Find todays date
            return currentTimeStamp;

        } catch (Exception e) {
            e.printStackTrace();


        }
        return null;
    }
    public void listings(){
        final String[] items;
       // String path=Environment.getExternalStorageDirectory();
        final ListView lv=(ListView)findViewById(R.id.listView);
        final ArrayList<File> myrecords = GetFiles(Environment.getExternalStorageDirectory());
        items=new String[myrecords.size()];
        for(int i=0;i<myrecords.size();i++){
            items[i]=myrecords.get(i).getName().toString();
        }
        //  lv = (ListView)findViewById(R.id.filelist);
        final ArrayAdapter<String> adapt=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, items);
        lv.setAdapter(adapt);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // String filename =(String)lv.getItemAtPosition(position);

                String path=Environment.getExternalStorageDirectory().getAbsolutePath() +  "/"+(String)lv.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),path,Toast.LENGTH_SHORT).show();
                Uri audio = Uri.parse(path);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                File file = new File(String.valueOf(audio));
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                startActivity(intent);
            }
        });
    }
    public ArrayList<File> GetFiles(File DirectoryPath) {
        ArrayList<File> myfiles = new ArrayList<File>();
        File[] files = DirectoryPath.listFiles();
        for(File singlefile : files){
            if(singlefile.isDirectory() && !singlefile.isHidden()){
                myfiles.addAll(GetFiles(singlefile));

            }
            else {
                if (singlefile.getName().endsWith("records.mp3")) {
                    myfiles.add(singlefile);
                }
            }
        }

        return myfiles;
    }
}
