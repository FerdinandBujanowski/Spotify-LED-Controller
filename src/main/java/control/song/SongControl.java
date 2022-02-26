package control.song;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.*;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.player.*;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioAnalysisForTrackRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioFeaturesForTrackRequest;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import control.save.EventSaveUnit;
import control.spotify.SpotifyWebHandler;
import control.type_enums.CurveType;
import gui.main_panels.player_panel.SpotifyPlayerPanel;
import logic.song.LogicEvent;
import logic.song.LogicTrack;
import org.apache.hc.core5.http.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class SongControl implements TrackRequestAcceptor, Serializable {

    SpotifyWebHandler spotifyWebHandler;
    private EventGraphicUnit eventWindow;

    private boolean spotifyConnected;
    private User lastConnectedUser;
    private String songId;
    private ImageIcon albumImage;
    private boolean songSelected, songPlaying, songPaused;
    private int currentSongMs;
    private Track[] lastSearchedSongList;

    private AudioAnalysis selectedSongAnalysis;
    private AudioFeatures selectedSongFeatures;
    private ArrayList<TimeMeasure> timeMeasures;

    private Device[] currentAvailableDevices;

    private ArrayList<LogicTrack> logicTracks;

    public SongControl() {
        this.spotifyWebHandler = new SpotifyWebHandler();
        this.eventWindow = null;

        this.songId = "";
        this.albumImage = null;
        this.songSelected = false;
        this.songPlaying = false;
        this.songPaused = true;

        this.currentSongMs = 0;

        this.lastSearchedSongList = new Track[] {};

        this.selectedSongAnalysis = null;
        this.selectedSongFeatures = null;
        this.timeMeasures = new ArrayList<>();

        this.currentAvailableDevices = new Device[] {};

        this.logicTracks = new ArrayList<>();
    }

    public void reinitialize(EventSaveUnit eventSaveUnit) {
        this.songSelected = true;
        this.songId = eventSaveUnit.getSelectedSongId();
        this.albumImage = eventSaveUnit.getAlbumImage();
        this.logicTracks = eventSaveUnit.getLogicTracks();
        this.timeMeasures = eventSaveUnit.getTimeMeasures();
        this.eventWindow.syncTracks(this.getTrackTimes());
    }

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

    public String getLastConnectedUserName() {
        return lastConnectedUser.getDisplayName();
    }

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

    public void selectSongBySearchedList(int selectedListIndex) {
        this.selectSong(this.lastSearchedSongList[selectedListIndex]);
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

                this.logicTracks.add(new LogicTrack());
                for(AudioAnalysisMeasure bar : this.selectedSongAnalysis.getBars()) {
                    this.logicTracks.get(0).addEventToTrack(
                            (int)(bar.getStart() * 1000),
                            (int)((bar.getStart() + bar.getDuration()) * 1000),
                            CurveType.CONSTANT
                    );
                }

                float beatDurationSum = 0;
                this.logicTracks.add(new LogicTrack());
                for(AudioAnalysisMeasure beat : this.selectedSongAnalysis.getBeats()) {
                    beatDurationSum += beat.getDuration();
                    this.logicTracks.get(1).addEventToTrack(
                            (int)(beat.getStart() * 1000),
                            (int)((beat.getStart() + beat.getDuration()) * 1000),
                            CurveType.CONSTANT
                    );
                }
                GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest =
                        this.spotifyWebHandler.getSpotifyApi().getAudioFeaturesForTrack(this.songId).build();
                this.selectedSongFeatures = getAudioFeaturesForTrackRequest.execute();

                this.timeMeasures.add(new TimeMeasure(
                        this.selectedSongFeatures.getTimeSignature(),
                        this.selectedSongFeatures.getTempo(),
                        (int)(this.selectedSongAnalysis.getBeats()[0].getStart() * 1000),
                        this.selectedSongAnalysis.getBeats().length
                ));
                if(this.eventWindow != null) {
                    this.eventWindow.syncTracks(this.getTrackTimes());
                }

            } catch (IOException | SpotifyWebApiException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public int getTrackCount() {
        return this.logicTracks.size();
    }


    public ImageIcon getAlbumImage() {
        return this.albumImage;
    }

    public void updatePlayingState() {
        if(this.spotifyWebHandler.getSpotifyApi().getAccessToken() != null) {
            GetInformationAboutUsersCurrentPlaybackRequest currentPlaybackRequest =
                    this.spotifyWebHandler.getSpotifyApi().getInformationAboutUsersCurrentPlayback().build();
            try {
                CurrentlyPlayingContext currentlyPlayingContext = currentPlaybackRequest.execute();

                this.songPlaying = currentlyPlayingContext.getItem().getId().equals(this.songId);
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

    public void setCurrentSongMs(int newMs) {
        this.currentSongMs = newMs;
    }

    public boolean isSpotifyConnected() {
        return this.spotifyConnected;
    }

    public boolean isSongSelected() {
        return this.songSelected;
    }

    public boolean isSongPlaying() {
        return this.songPlaying;
    }

    public boolean isSongPaused() {
        return this.songPaused;
    }

    public boolean isSongSynced() {
        return false;
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

    public void onStartPlayback() {

    }

    public double[] getTrackIntensitiesAt(int ms) {
        double[] values = new double[this.logicTracks.size()];
        for(int i = 0; i < values.length; i++) {
            values[i] = this.getTrackIntensityAt(new Point(i, ms));
        }
        return values;
    }

    private void onPausePlayback() {
        PauseUsersPlaybackRequest pauseUsersPlaybackRequest = this.spotifyWebHandler.getSpotifyApi().pauseUsersPlayback().build();
        try {
            pauseUsersPlaybackRequest.execute();
            this.songPaused = true;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void onResumePlayback() {
        StartResumeUsersPlaybackRequest resumeUsersPlaybackRequest = this.spotifyWebHandler.getSpotifyApi().startResumeUsersPlayback().build();
        try {
            long msBefore = System.currentTimeMillis();
            resumeUsersPlaybackRequest.execute();
            long msAfter = System.currentTimeMillis();
            this.songPaused = false;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void onTogglePlayback() {
        if(this.songSelected && this.songPlaying) {
            if(this.songPaused) {
                this.onResumePlayback();
            } else {
                this.onPausePlayback();
            }
        }
    }

    public void onSkipTo(int ms) {
        SeekToPositionInCurrentlyPlayingTrackRequest request =
                this.spotifyWebHandler.getSpotifyApi().seekToPositionInCurrentlyPlayingTrack(ms).build();
        try {
            request.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setEventGraphicUnit(EventGraphicUnit eventGraphicUnit) {
        this.eventWindow = eventGraphicUnit;
    }

    @Override
    public Double getTrackIntensityAt(Point coordinates) {
        if(this.logicTracks.get(coordinates.x) == null) {
            return 0.d;
        } else {
            return this.logicTracks.get(coordinates.x).getIntensityAt(coordinates.y);
        }
    }

    @Override
    public ArrayList<TimeMeasure> getTimeMeasures() {
        return this.timeMeasures;
    }

    @Override
    public TimeMeasure getCorrespondingTimeMeasure(int ms) {
        for(TimeMeasure timeMeasure : this.timeMeasures) {
            int msEnd = timeMeasure.getMsStart() + (timeMeasure.getLengthOneBeat() * timeMeasure.getBeatsDuration());
            if(ms >= timeMeasure.getMsStart() && ms < msEnd) {
                return timeMeasure;
            }
        }
        return null;
    }

    @Override
    public int getCorrespondingEventIndex(int trackNumber, int ms) {
        return this.logicTracks.get(trackNumber).getEventIndex(ms);
    }

    @Override
    public TrackTime[] getTrackTimes() {

        TrackTime[] trackTimes = new TrackTime[this.logicTracks.size()];

        for(int i = 0; i < this.logicTracks.size(); i++) {
            trackTimes[i] = new TrackTime(this.logicTracks.get(i).getEventsCopyArray(), this.logicTracks.get(i).getCurveTypes());
        }
        return trackTimes;
    }

    @Override
    public int getCurrentSongMs() {
        return this.currentSongMs;
    }

    @Override
    public void tick(int ms) {
        eventWindow.tick(ms);
    }

    @Override
    public void onAddTrackRequest() {
        if(this.songSelected) {
            //TODO: hier alle möglichen Fehler abfangen
            this.logicTracks.add(new LogicTrack());
            this.eventWindow.addTrack();
        }
    }
    @Override
    public void onUpdateTrackRequest(int trackNumber, boolean deleted) {

    }

    @Override
    public void onAddEventToTrackRequest(int trackNumber, int msStart, int msDuration) {
        if(this.logicTracks.get(trackNumber) != null) {
            int oldLength = this.logicTracks.get(trackNumber).getEventsCopyArray().length;
            this.logicTracks.get(trackNumber).addEventToTrack(msStart, msStart + msDuration, CurveType.CONSTANT);

            //TODO: Prüfen nach Overlaps klappt anscheinend noch nicht
            LogicEvent[] events = this.logicTracks.get(trackNumber).getEventsCopyArray();
            if(events.length > oldLength) {
                LogicEvent newEvent = events[oldLength];
                this.eventWindow.addEventToTrack(trackNumber, newEvent.getMsStart(), newEvent.getMsDuration(), newEvent.getCurveType());
            }
        }
    }

    @Override
    public void onUpdateEventRequest(int trackNumber, int msStartOld, boolean deleted, CurveType curveType, int msStartNew, int msDurationNew) {
        int eventIndex = this.logicTracks.get(trackNumber).getEventIndex(msStartOld);
        if(eventIndex == -1) return;

        this.logicTracks.get(trackNumber).removeEventAtIndex(eventIndex);
        this.eventWindow.deleteEvent(trackNumber, eventIndex);

        if(!deleted) {
            this.logicTracks.get(trackNumber).addEventToTrack(msStartNew, msStartNew + msDurationNew, curveType);
            this.eventWindow.addEventToTrack(trackNumber, msStartNew, msDurationNew, curveType);
        }
    }

    public EventSaveUnit createEventSaveUnit() {
        return new EventSaveUnit(this.songId, this.albumImage, this.logicTracks, this.timeMeasures);
    }
}
