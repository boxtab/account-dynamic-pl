package org.webtrader.accountDynamicPL.notifier;

import org.webtrader.accountDynamicPL.account.AccountDynamicPL;
import org.webtrader.accountDynamicPL.config.Config;
import org.webtrader.accountDynamicPL.log.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Notifier
{
    public void sendNotifications( AccountDynamicPL accountDynamicPL ) throws Exception
    {
        String urlString = this.createURL();
        String jsonPayload = accountDynamicPL.toJson();

        try {
            int responseCode = getResponseCode( urlString, jsonPayload );

            if ( responseCode != 200 ) {
                Log.write( jsonPayload );
                Log.write( "Error: The websocket server, at this address, " + urlString +
                        ", responded with HTTP code " + responseCode );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new Exception( "Error while sending notification", e );
        }
    }

    private static int getResponseCode( String urlString, String jsonPayload ) throws IOException
    {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // Записываем JSON в тело запроса
        try ( OutputStream os = connection.getOutputStream() ) {
            byte[] input = jsonPayload.getBytes( StandardCharsets.UTF_8 );
            os.write(input, 0, input.length);
        }

        return connection.getResponseCode();
    }

    private String createURL() throws Exception
    {
        Config config = Config.getConfig();

        try
        {
            return
                    "http://" +
                            config.getProperty( "LARAVEL_HOST" ) +
                            "/api/v1/demon/update-dynamic-pl/" +
                            config.getProperty( "DEMON_TOKEN" );
        }
        catch ( Exception e )
        {
            throw new Exception( "Unable to get config properties LARAVEL_HOST or DEMON_TOKEN: ", e );
        }
    }
}
