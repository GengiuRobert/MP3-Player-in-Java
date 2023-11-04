package com.example.hope_it_works_now;

import java.io.File;
import java.net.URL;
import java.util.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

public class HelloController implements Initializable {

    @FXML
    Button myPausebutton, myPreviousbutton, myPlaybutton, myNextbutton, myResetbutton, myShufflebutton, myPlaylistbutton;
    @FXML
    ComboBox<String> myCombobox;
    @FXML
    Label myCurrentSonglabel, myNextSonglabel, myPlaylistlabel;
    @FXML
    Slider mySlider;
    @FXML
    ProgressBar myProgressbar;

    private Media media;
    private MediaPlayer mediaPlayer;

    private File directory;
    private File[] files;

    private ArrayList<File> songs;

    private int songNumber;
    private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};

    private Timer timer;
    private TimerTask task;

    private boolean running,isPaused;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songs = new ArrayList<File>();
        directory = new File("music");
        myPlaylistlabel.setText("Defaul Playlist: " + directory);
        files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                songs.add(file);
            }
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        myCurrentSonglabel.setText(songs.get(songNumber).getName().replaceAll("\\.\\w+$", ""));
        if (songNumber < songs.size() - 1) {
            myNextSonglabel.setText(songs.get(songNumber + 1).getName().replaceAll("\\.\\w+$", ""));
        } else {
            myNextSonglabel.setText(songs.get(0).getName().replaceAll("\\.\\w+$", ""));
        }
        for(int i = 0; i < speeds.length; i++) {

            myCombobox.getItems().add(Integer.toString(speeds[i])+"%");
        }
        myCombobox.setOnAction(this::changeSpeed);
        mySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(mySlider.getValue()*0.01);
            }
        });
    }

    private void changeSpeed(ActionEvent event) {
        if(myCombobox.getValue() == null)
        {
            mediaPlayer.setRate(1);
        } else {
            mediaPlayer.setRate(Integer.parseInt(myCombobox.getValue().substring(0, myCombobox.getValue().length() - 1)) * 0.01);
        }
    }

//    public void playMedia() {
//        changeSpeed(null);
//        mediaPlayer.setVolume(1);
//        mediaPlayer.setVolume(mySlider.getValue()*0.01);
//        beginTimer();
//        if (mediaPlayer != null) {
//            mediaPlayer.play();
//        }
//
//        mediaPlayer.setOnError(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("Error at playing media: " + mediaPlayer.getError());
//            }
//        });
//    }

    public void playMedia() {
        try {
            changeSpeed(null);
            mediaPlayer.setVolume(1);
            mediaPlayer.setVolume(mySlider.getValue() * 0.01);
            beginTimer();
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
        } catch (NullPointerException e) {
            System.out.println("Eroare la redarea media: " + e.getMessage());
        }
    }


    public void pauseMedia() {
        cancelTimer();
        mediaPlayer.pause();
        isPaused = true;
    }

    public void previousMedia() {
        if (songNumber > 0) {
            songNumber--;
        } else {
            songNumber = songs.size() - 1;
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer.stop();
        if(running)
        {
            cancelTimer();
        }
        mediaPlayer = new MediaPlayer(media);
        myCurrentSonglabel.setText(songs.get(songNumber).getName().replaceAll("\\.\\w+$", ""));

        if (songNumber < songs.size() - 1) {
            myNextSonglabel.setText(songs.get(songNumber + 1).getName().replaceAll("\\.\\w+$", ""));
        } else {
            myNextSonglabel.setText(songs.get(0).getName().replaceAll("\\.\\w+$", ""));
        }
        playMedia();
    }

    public void nextMedia() {
        if (songNumber < songs.size() - 1) {
            songNumber++;
        } else {
            songNumber = 0;
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer.stop();
        if(running)
        {
            cancelTimer();
        }
        mediaPlayer = new MediaPlayer(media);
        myCurrentSonglabel.setText(songs.get(songNumber).getName().replaceAll("\\.\\w+$", ""));

        if (songNumber < songs.size() - 1) {
            myNextSonglabel.setText(songs.get(songNumber + 1).getName().replaceAll("\\.\\w+$", ""));
        } else {
            myNextSonglabel.setText(songs.get(0).getName().replaceAll("\\.\\w+$", "")); // Afișați prima melodie din playlist
        }
        playMedia();
    }

    public void resetMedia() {
        if (isPaused == true)
            mediaPlayer.seek(Duration.ZERO);
        mediaPlayer.seek(Duration.ZERO);
        myProgressbar.setProgress(0);
    }

    public void shuffleMedia() {
        beginTimer();
        if (!songs.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(songs.size());

            songNumber = randomIndex;

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer.stop();
            if(running)
            {
                cancelTimer();
            }
            mediaPlayer = new MediaPlayer(media);
            myCurrentSonglabel.setText(songs.get(songNumber).getName().replaceAll("\\.\\w+$", ""));

            if (songNumber < songs.size() - 1) {
                myNextSonglabel.setText(songs.get(songNumber + 1).getName().replaceAll("\\.\\w+$", ""));
            } else {
                myNextSonglabel.setText(songs.get(0).getName().replaceAll("\\.\\w+$", ""));
            }
        }
        playMedia();
    }

    public void choose_playlist() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("music"));

        File selectedDirectory = directoryChooser.showDialog(myPlaylistbutton.getScene().getWindow());
        myPlaylistlabel.setText("New Playlist: " + selectedDirectory);

        if (selectedDirectory != null) {
            songs.clear();

            File[] files = selectedDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        songs.add(file);
                    }
                }
            }
        }
    }
    public void beginTimer() {

        timer = new Timer();

        task = new TimerTask() {

            public void run() {

                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                myProgressbar.setProgress(current/end);

                if(current/end == 1) {

                    cancelTimer();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }
    public void cancelTimer() {

        running = false;
        timer.cancel();
    }
}