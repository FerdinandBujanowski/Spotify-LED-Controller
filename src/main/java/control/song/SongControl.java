package control.song;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.AudioAnalysis;
import com.wrapper.spotify.model_objects.miscellaneous.AudioAnalysisMeasure;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import com.wrapper.spotify.requests.data.player.PauseUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioAnalysisForTrackRequest;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import control.spotify.SpotifyWebHandler;
import logic.event.LogicTrack;
import logic.node.nodes.debug.DebugNode;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class SongControl {

    private SpotifyWebHandler spotifyWebHandler;

    private User lastConnectedUser;
    private String songId;
    private boolean songSelected;

    private Track[] lastSearchedSongList;
    private Track selectedSong;

    private AudioAnalysis selectedSongAnalysis;

    private Device[] currentAvailableDevices;

    private ArrayList<LogicTrack> logicTracks;

    public SongControl() {
        this.spotifyWebHandler = new SpotifyWebHandler();
        this.logicTracks = new ArrayList<>();

        this.songId = "";
        this.songSelected = false;

        this.lastSearchedSongList = new Track[] {};
        this.selectedSong = null;

        this.currentAvailableDevices = new Device[] {};
    }


    public boolean connectToSpotify() {
        this.spotifyWebHandler.init();
        if(this.spotifyWebHandler.getSpotifyApi().getAccessToken() != null) {
            GetCurrentUsersProfileRequest currentUsersProfileRequest = this.spotifyWebHandler.getSpotifyApi().getCurrentUsersProfile().build();
            try {
                this.lastConnectedUser = currentUsersProfileRequest.execute();

                this.currentAvailableDevices = this.spotifyWebHandler.getSpotifyApi().getUsersAvailableDevices().build()
                        .execute();

                return true;
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getLastConnectedUserName() {
        return lastConnectedUser.getDisplayName();
    }

    public String[] searchSongByName(String input) {
        SearchTracksRequest searchTracksRequest = this.spotifyWebHandler.getSpotifyApi().searchTracks(input).build();
        try {
            Track[] tracks = searchTracksRequest.execute().getItems();
            this.lastSearchedSongList = tracks;
            return Arrays.copyOf(Arrays.stream(tracks).map(e -> e.getName() + " by " + e.getArtists()[0].getName()).toArray(), tracks.length, String[].class);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        return new String[] {};
    }

    public void selectSongBySearchedList(int selectedListIndex) {
        this.selectSong(this.lastSearchedSongList[selectedListIndex]);
    }

    public void selectSong(Track track) {
        this.selectedSong = track;
        this.songId = this.selectedSong.getId();
        this.songSelected = true;

        GetAudioAnalysisForTrackRequest getAudioAnalysisForTrackRequest =
                this.spotifyWebHandler.getSpotifyApi().getAudioAnalysisForTrack(this.songId).build();
        try {
            this.selectedSongAnalysis = getAudioAnalysisForTrackRequest.execute();

            this.logicTracks.add(new LogicTrack("Bars (generated)"));
            for(AudioAnalysisMeasure bar : this.selectedSongAnalysis.getBars()) {
                this.logicTracks.get(0).addEventToTrack(
                        (int)(bar.getStart() * 1000),
                        (int)((bar.getStart() + bar.getDuration()) * 1000)
                );
            }

            this.logicTracks.add(new LogicTrack("Beats (generated"));
            for(AudioAnalysisMeasure beat : this.selectedSongAnalysis.getBeats()) {
                this.logicTracks.get(1).addEventToTrack(
                        (int)(beat.getStart() * 1000),
                        (int)((beat.getStart() + beat.getDuration()) * 1000)
                );
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }

    public String getImageURL() {
        try {
            return this.selectedSong.getAlbum().getImages()[0].getUrl();
        } catch(NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String[] getDeviceNameList() {
        if(this.currentAvailableDevices.length == 0) {
            return null;
        }
        return Arrays.copyOf(
                Arrays.stream(this.currentAvailableDevices).map(Device::getName).toArray(),
                this.currentAvailableDevices.length,
                String[].class
        );
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

    public void onPausePlayback() {
        PauseUsersPlaybackRequest pauseUsersPlaybackRequest = this.spotifyWebHandler.getSpotifyApi().pauseUsersPlayback().build();
        try {
            pauseUsersPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void onResumePlayback() {
        StartResumeUsersPlaybackRequest resumeUsersPlaybackRequest = this.spotifyWebHandler.getSpotifyApi().startResumeUsersPlayback().build();
        try {
            resumeUsersPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }
}
