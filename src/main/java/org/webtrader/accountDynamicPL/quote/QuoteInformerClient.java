package org.webtrader.accountDynamicPL.quote;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.webtrader.accountDynamicPL.config.Config;

import java.net.URI;
import java.net.URISyntaxException;

public class QuoteInformerClient extends WebSocketClient
{
    final private static String QUOTATION_INFORMER_HOST = Config.getConfig().getProperty( "QUOTATION_INFORMER_HOST" );
    final private static String QUOTATION_INFORMER_PORT = Config.getConfig().getProperty( "QUOTATION_INFORMER_PORT" );

    final private QuoteInformerObserver quoteInformerObserver;

    QuoteInformerClient(QuoteInformerObserver quoteInformerObserver)
    {
        super( createURI() );

        this.quoteInformerObserver = quoteInformerObserver;
    }

    private static URI createURI()
    {
        try
        {
            return new URI( createURIString() );
        }
        catch( URISyntaxException e )
        {
            throw new RuntimeException( "unable to create URI instance", e );
        }
    }

    private static String createURIString()
    {
        return "ws://" + QUOTATION_INFORMER_HOST + ":" + QUOTATION_INFORMER_PORT + "/";
    }

    @Override
    public void onOpen( ServerHandshake serverHandshake )
    {
        this.quoteInformerObserver.onQuotationInformerConnectionOpen( serverHandshake );
    }

    @Override
    public void onMessage( String message )
    {
        this.quoteInformerObserver.onQuotationInformerMessageReceived( message );
    }

    @Override
    public void onClose( int closeCode, String closeReason, boolean isClosedRemotely )
    {
        this.quoteInformerObserver.onQuotationInformerConnectionClose( closeCode, closeReason, isClosedRemotely );
    }

    @Override
    public void onError( Exception error )
    {
        this.quoteInformerObserver.onQuotationInformerError( error );
    }
}
