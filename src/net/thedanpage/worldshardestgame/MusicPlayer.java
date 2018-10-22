package net.thedanpage.worldshardestgame;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;

public class MusicPlayer {
    public static void play(Sound sound) {
        TinySound.init();
        TinySound.loadSound(Player.class.getClassLoader()
                .getResource(sound.getUrl())).play();
    }
}

enum Sound {
    BACKGROUND("net/thedanpage/worldshardestgame/resources/music.ogg"),
    COIN("net/thedanpage/worldshardestgame/resources/ding.wav");

    private String url;

    Sound(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}