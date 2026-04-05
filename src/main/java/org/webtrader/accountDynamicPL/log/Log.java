package org.webtrader.accountDynamicPL.log;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log
{
    final private static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    final private static String LOG_FILENAME = "account-dynamic-pl.log";

    final private SimpleDateFormat dateFormat;

    final private Path path;

    private static Log instance;

    public static Log getInstance()
    {
        if ( instance == null ) {
            instance = new Log();
        }

        return instance;
    }

    private Log()
    {
        this.dateFormat = new SimpleDateFormat( DATETIME_FORMAT );
        this.path = Paths.get( LOG_FILENAME );
    }

    public synchronized static void write( String message )
    {
        getInstance().writeMessage( message );
    }

    private void writeMessage( String message )
    {
        String messageFormatted = formatMessage( message );

        System.out.println( messageFormatted );

        try
        {
            Files.writeString(
                    path,
                    messageFormatted + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND
            );
        }
        catch( Throwable e )
        {
            throw new RuntimeException( "write file error", e );
        }
    }

    private String formatMessage( String message )
    {
        return
                "[ " +
                        getDateFormatted() +
                        " ] " +
                        message;
    }

    private String getDateFormatted()
    {
        return dateFormat.format( getDate() );
    }

    private Date getDate()
    {
        return new Date();
    }
}
