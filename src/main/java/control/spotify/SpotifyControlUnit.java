package control.spotify;

import com.wrapper.spotify.model_objects.miscellaneous.*;
import com.wrapper.spotify.model_objects.specification.*;

public interface SpotifyControlUnit {

    CurrentlyPlayingContext onGetCurrentlyPlayingContext();

    void onPausePlayback();
    void onResumePlayback();

    Paging<PlaylistSimplified> onGetUsersPlaylists();
    Paging<PlaylistTrack> onGetPlaylistTracks(String playlistID);
}
