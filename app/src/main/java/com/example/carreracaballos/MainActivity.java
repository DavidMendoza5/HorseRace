package com.example.carreracaballos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Observable;


public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar1, progressBar2, progressBar3, progressBar4;
    private Button button;
    private TextView winner;
    Thread[] threads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar1 = findViewById(R.id.progressBar2);
        progressBar2 = findViewById(R.id.progressBar3);

        winner = findViewById(R.id.textWinner);

        button = findViewById(R.id.button);

        threads = new Thread[2];

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRace(view);
            }
        });
    }

    public void startRace(View view) {
        button.setEnabled(false);

        for (int i = 0; i < threads.length; i++) {
            Horse horse = new Horse("Horse " + i);
            //horse.addObserver(this);
            horse.addObserver(this::update);
            threads[i] = new Thread(horse);
            threads[i].start();
        }

    }

    public void finish() {
        for (int i = 0; i < threads.length; i++) {
            threads[i].interrupt();
        }
    }

    //@Override
    public void update(Observable observable, Object object) {
        Horse h = (Horse) observable;
        int percent = (int) object;

        switch (h.getName()) {
            case "1":
                this.progressBar1.setProgress(percent);
                break;
            case "2":
                this.progressBar2.setProgress(percent);
                break;
        }

        if(percent >= 100) {
            finish();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    winner.setText("El ganador es: " + h.getName());
                    button.setEnabled(true);
                }
            });
        }
    }

    class Horse extends Observable implements Runnable {
        private String name;

        public Horse(String name) {
            this.name = name;
        }

        public String getName(){
            return name;
        }

        @Override
        public void run() {
            int percent = 0;
            int random_number;

            try {
                while (percent < 100) {
                    random_number = generateRandomNumber(1, 15);
                    System.out.println("Caballo " + name + " ha aumentado " + random_number + " lleva: " + percent);
                    percent += random_number;

                    this.setChanged();
                    this.notifyObservers(percent);
                    this.clearChanged();

                    if(percent >= 100) {
                        System.out.println("Ganador: " + name);
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Hilo interrumpido");
            }
        }

        public int generateRandomNumber(int min, int max) {
            int num = (int) Math.floor(Math.random() * (max - min + 1) + min);
            return  num;
        }
    }
}

