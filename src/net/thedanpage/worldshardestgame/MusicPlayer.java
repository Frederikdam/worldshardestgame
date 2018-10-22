package net.thedanpage.worldshardestgame;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;

public class MusicPlayer {

    static Thread bgMusic = new Thread() {
        public void run() {
            TinySound.init();
            Music bgmusic = TinySound.loadMusic(ClassLoader.getSystemResource(
                    "net/thedanpage/worldshardestgame/resources/music.ogg"));
            bgmusic.play(true);
        }
    };

    public static void start() {
        bgMusic.start();
        bgMusic.run();
    }
}
