package control.song;

import com.google.gson.JsonParser;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.NotFoundException;
import com.wrapper.spotify.model_objects.miscellaneous.AudioAnalysis;
import com.wrapper.spotify.model_objects.miscellaneous.AudioAnalysisMeasure;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import com.wrapper.spotify.requests.data.player.PauseUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.SeekToPositionInCurrentlyPlayingTrackRequest;
import com.wrapper.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioAnalysisForTrackRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioFeaturesForTrackRequest;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import control.event.TimeMeasure;
import control.save.EventSaveUnit;
import control.spotify.SpotifyWebHandler;
import gui.main_panels.player_panel.SpotifyPlayerPanel;
import logic.event.LogicTrack;
import org.apache.hc.core5.http.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class SongControl implements SongRequestAcceptor, SongEventCommunication {

    SpotifyWebHandler spotifyWebHandler;
    private boolean spotifyConnected;
    private User lastConnectedUser;
    private String songId;
    private ImageIcon albumImage;
    private boolean songSelected, songPlaying, songPaused;

    private boolean animationMode;
    private int animationTime;

    private int currentSongMs;
    private Track[] lastSearchedSongList;

    private AudioAnalysis selectedSongAnalysis;
    private AudioFeatures selectedSongFeatures;

    private Device[] currentAvailableDevices;

    public SongControl() {
        this.spotifyWebHandler = new SpotifyWebHandler();
        this.songId = "";
        this.albumImage = null;
        this.songSelected = false;
        this.songPlaying = false;
        this.songPaused = true;

        this.currentSongMs = 0;

        this.lastSearchedSongList = new Track[] {};

        this.selectedSongAnalysis = null;
        this.selectedSongFeatures = null;
        this.currentAvailableDevices = new Device[] {};
    }

    //TODO SongSaveUnit ?
    public void reinitialize() {
        this.songSelected = true;
    }

    public void selectSong(Track track) {
        if(!this.songId.equals(track.getId())) {
            this.songId = track.getId();
            this.songSelected = true;

            String imageURL = track.getAlbum().getImages()[0].getUrl();
            if(imageURL != null) {
                BufferedImage image = null;
                try {
                    image = ImageIO.read(new URL(imageURL));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                this.albumImage = new ImageIcon(SpotifyPlayerPanel.getScaledImage(image, 300, 300));
            }

            GetAudioAnalysisForTrackRequest getAudioAnalysisForTrackRequest =
                    this.spotifyWebHandler.getSpotifyApi().getAudioAnalysisForTrack(this.songId).build();
            try {
                this.selectedSongAnalysis = getAudioAnalysisForTrackRequest.execute();
                GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest =
                        this.spotifyWebHandler.getSpotifyApi().getAudioFeaturesForTrack(this.songId).build();
                this.selectedSongFeatures = getAudioFeaturesForTrackRequest.execute();

            } catch (IOException | SpotifyWebApiException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAnimationMode(boolean animationMode) {
        this.animationMode = animationMode;
    }

    public void updatePlayingState() {
        if(this.spotifyWebHandler.getSpotifyApi().getAccessToken() != null) {
            GetInformationAboutUsersCurrentPlaybackRequest currentPlaybackRequest =
                    this.spotifyWebHandler.getSpotifyApi().getInformationAboutUsersCurrentPlayback().build();
            try {
                CurrentlyPlayingContext currentlyPlayingContext = currentPlaybackRequest.execute();

                if(currentlyPlayingContext == null) {
                    this.songPlaying = false;
                } else {
                    this.songPlaying = currentlyPlayingContext.getItem().getId().equals(this.songId);
                }
                if(this.songPlaying) {
                    this.songPaused = !currentlyPlayingContext.getIs_playing();
                } else {
                    this.songPaused = true;
                }
            } catch (IOException | SpotifyWebApiException | ParseException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
    public int getUpdatedSongMs() {
        if(this.animationMode) {
            return this.currentSongMs;
        }
        if(this.spotifyWebHandler.getSpotifyApi().getAccessToken() != null) {
            GetInformationAboutUsersCurrentPlaybackRequest currentPlaybackRequest =
                    this.spotifyWebHandler.getSpotifyApi().getInformationAboutUsersCurrentPlayback().build();
            try {
                CurrentlyPlayingContext currentlyPlayingContext = currentPlaybackRequest.execute();
                return currentlyPlayingContext.getProgress_ms();

            } catch (IOException | SpotifyWebApiException | ParseException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public CurrentlyPlayingContext onGetCurrentPlayingTrack() {
        GetInformationAboutUsersCurrentPlaybackRequest currentPlaybackRequest =
                this.spotifyWebHandler.getSpotifyApi().getInformationAboutUsersCurrentPlayback().build();
        CurrentlyPlayingContext currentlyPlayingContext = null;
        try {
            currentlyPlayingContext = currentPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        return currentlyPlayingContext;
    }

    @Override
    public void connectToSpotify() {
        spotifyWebHandler.init();
        if(spotifyWebHandler.getSpotifyApi().getAccessToken() != null) {
            GetCurrentUsersProfileRequest currentUsersProfileRequest = spotifyWebHandler.getSpotifyApi().getCurrentUsersProfile().build();
            try {
                this.lastConnectedUser = currentUsersProfileRequest.execute();

                this.currentAvailableDevices = spotifyWebHandler.getSpotifyApi().getUsersAvailableDevices().build()
                        .execute();

                this.spotifyConnected = true;
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                this.spotifyConnected = false;
            }
        }
    }
    @Override
    public String[] searchSongByName(String input) {
        SearchTracksRequest searchTracksRequest = spotifyWebHandler.getSpotifyApi().searchTracks(input).build();
        try {
            Track[] tracks = searchTracksRequest.execute().getItems();
            this.lastSearchedSongList = tracks;
            return Arrays.copyOf(
                    Arrays.stream(tracks).map(e -> e.getName() + " by " + e.getArtists()[0].getName()).toArray(),
                    tracks.length,
                    String[].class
            );
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        return new String[] {};
    }
    @Override
    public String getLastConnectedUserName() {
        return lastConnectedUser.getDisplayName();
    }
    @Override
    public String getDeviceNameList() {
        String output = "";
        for(Device device : this.currentAvailableDevices) {
            output += ("<br/>" + device.getName());
        }
        if(output.equals("")) return "[no devices]";
        else return output;
    }
    @Override
    public void selectSongBySearchedList(int selectedListIndex) {
        this.selectSong(this.lastSearchedSongList[selectedListIndex]);
    }

    @Override
    public void onStartPlayback() {
        if(this.animationMode) return;

        StartResumeUsersPlaybackRequest startUsersPlayback = this.spotifyWebHandler.getSpotifyApi().startResumeUsersPlayback()
                .uris(JsonParser.parseString("[\"spotify:track:" + this.songId + "\"]").getAsJsonArray())
                .position_ms(0).build();
        try {
            startUsersPlayback.execute();
            this.updatePlayingState();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }
    private void onPausePlayback() {
        if(this.animationMode) {
            this.songPaused = true;
            return;
        }
        PauseUsersPlaybackRequest pauseUsersPlaybackRequest = this.spotifyWebHandler.getSpotifyApi().pauseUsersPlayback().build();
        try {
            pauseUsersPlaybackRequest.execute();
            this.songPaused = true;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }
    private void onResumePlayback() {
        if(this.animationMode) {
            this.songPaused = false;
            return;
        }
        StartResumeUsersPlaybackRequest resumeUsersPlaybackRequest = this.spotifyWebHandler.getSpotifyApi().startResumeUsersPlayback().build();
        try {
            resumeUsersPlaybackRequest.execute();
            this.songPaused = false;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onTogglePlayback() {
        if(this.isSongSelected() && this.isSongPlaying()) {
            if(this.songPaused) {
                this.onResumePlayback();
            } else {
                this.onPausePlayback();
            }
        }
    }

    @Override
    public boolean isSpotifyConnected() {
        return this.spotifyConnected;
    }
    @Override
    public boolean isSongSelected() {
        return this.songSelected || this.animationMode;
    }
    @Override
    public boolean isSongPlaying() {
        return this.songPlaying || this.animationMode;
    }
    @Override
    public boolean isSongPaused() {
        return this.songPaused;
    }
    @Override
    public boolean isSongSynced() {
        return false;
    }

    @Override
    public boolean isAnimationMode() {
        return this.animationMode;
    }

    @Override
    public int getAnimationTime() {
        return this.animationTime;
    }

    @Override
    public void setAnimationTime(int animationTime) {
        this.animationTime = animationTime;
    }

    @Override
    public int getCurrentSongMs() {
        return this.currentSongMs;
    }
    @Override
    public void setCurrentSongMs(int newMs) {
        this.currentSongMs = newMs;
    }

    @Override
    public void onSkipTo(int ms) {
        if(this.animationMode) {
            this.animationTime = ms;
            this.setCurrentSongMs(this.animationTime);
            return;
        }
        SeekToPositionInCurrentlyPlayingTrackRequest request =
                this.spotifyWebHandler.getSpotifyApi().seekToPositionInCurrentlyPlayingTrack(ms).build();
        try {
            request.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TimeMeasure[] generateTimeMeasures() {
        int startMs = (int)(this.selectedSongAnalysis.getBeats()[0].getStart() * 1000);
        TimeMeasure mainTimeMeasure = new TimeMeasure(
                this.selectedSongFeatures.getTimeSignature(),
                this.selectedSongFeatures.getTempo(),
                startMs,
                this.selectedSongAnalysis.getBeats().length
        );
        if(startMs != 0) {
            float startBpm = 60000.f / startMs;
            TimeMeasure startTimeMeasure = new TimeMeasure(1, startBpm, 0, 1);
            return new TimeMeasure[] { startTimeMeasure, mainTimeMeasure };
        }
        return new TimeMeasure[] { mainTimeMeasure };
    }

    @Override
    public ImageIcon getAlbumImage() {
        return this.albumImage;
    }
}
