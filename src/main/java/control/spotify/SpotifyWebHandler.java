package control.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.apache.hc.core5.http.ParseException;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class SpotifyWebHandler {
    private final String clientId = "b4024b28e3e140d2a648de961fa41de2";
    private  final String clientSecret = "596e84e310ad441eb033866fc15c81b6";
    private  final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8888/callbackWebpage");

    private String code;

    private final SpotifyApi spotifyApi;
    private AuthorizationCodeUriRequest authorizationCodeUriRequest;
    private AuthorizationCodeRequest authorizationCodeRequest;

    public SpotifyWebHandler() {

        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();


    }

    public void init() {
        this.authorizationCodeUriRequest = this.spotifyApi.authorizationCodeUri()
//          .state("x4xkmn9pu3j6ukrs8n")
                .scope("user-read-email, user-read-playback-state, user-modify-playback-state, playlist-read-private")
//          .show_dialog(true)
                .build();

        HTTPServer httpServer = new HTTPServer();
        httpServer.startHTTPServer();
        //authorizationCodeUri_Sync();
        this.authorizationCodeUri_Sync();
        while(httpServer.getCode() == null) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.code = httpServer.getCode();
        //System.out.println(httpServer.getCode());

        this.authorizationCodeRequest = spotifyApi.authorizationCode(this.code)
                .build();
        this.authorizationCode_Sync();
    }

    private void authorizationCodeUri_Sync() {
        final URI uri = this.authorizationCodeUriRequest.execute();
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("URI: " + uri.toString());
    }

    private void authorizationCodeUri_Async() {
        try {
            final CompletableFuture<URI> uriFuture = this.authorizationCodeUriRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final URI uri = uriFuture.join();

            //System.out.println("URI: " + uri.toString());
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);
            }
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authorizationCode_Sync() {
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = this.authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            this.spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            this.spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Token: " + authorizationCodeCredentials.getAccessToken());
            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void authorizationCode_Async() {
        try {
            final CompletableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = this.authorizationCodeRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeCredentialsFuture.join();

            // Set access and refresh token for further "spotifyApi" object usage
            this.spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            this.spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Token: " + authorizationCodeCredentials.getAccessToken());
            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }

    public SpotifyApi getSpotifyApi() {
        return this.spotifyApi;
    }
}