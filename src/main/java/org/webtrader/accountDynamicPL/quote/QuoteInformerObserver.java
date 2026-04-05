package org.webtrader.accountDynamicPL.quote;

import org.java_websocket.handshake.ServerHandshake;

public interface QuoteInformerObserver
{
    void onQuotationInformerConnectionOpen( ServerHandshake serverHandshake );
    void onQuotationInformerMessageReceived( String message );
    void onQuotationInformerConnectionClose( int closeCode, String closeReason, boolean isClosedRemotely );
    void onQuotationInformerError( Exception error );
}
