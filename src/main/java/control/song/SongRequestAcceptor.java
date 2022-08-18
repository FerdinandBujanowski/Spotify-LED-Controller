package control.song;

import com.wrapper.spotify.exceptions.detailed.NotFoundException;
import control.event.TimeMeasure;

import javax.swing.*;

public interface SongRequestAcceptor {

    void connectToSpotify();
    String[] searchSongByName(String name);
    String getLastConnectedUserName();
    String getDeviceNameList();
    void selectSongBySearchedList(int index);

    void onStartPlayback() throws NotFoundException;
    void onTogglePlayback();

    boolean isSpotifyConnected();
    boolean isSongSelected();
    boolean isSongPlaying();
    boolean isSongPaused();
    boolean isSongSynced();
    boolean isAnimationMode();
    int getAnimationTime();
    void setAnimationTime(int animationTime);

    ImageIcon getAlbumImage();

    int getCurrentSongMs();
    void setCurrentSongMs(int newMs);
    void onSkipTo(int ms);

    TimeMeasure[] generateTimeMeasures();
}
