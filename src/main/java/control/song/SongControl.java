package control.song;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import com.wrapper.spotify.requests.data.player.PauseUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import control.spotify.SpotifyWebHandler;
import logic.node.nodes.debug.DebugNode;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class SongControl {

    private SpotifyWebHandler spotifyWebHandler;

    private User lastConnectedUser;
    private String songId;
    private boolean songSelected;

    private Track[] lastSearchedSongList;
    private Track selectedSong;

    private Device[] currentAvailableDevices;

    public SongControl() {
        this.spotifyWebHandler = new SpotifyWebHandler();

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
        this.selectedSong = this.lastSearchedSongList[selectedListIndex];
        this.songId = this.selectedSong.getId();
        this.songSelected = true;
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
