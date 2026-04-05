package org.webtrader.accountDynamicPL.quote;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import org.webtrader.accountDynamicPL.account.AccountDynamicPL;
import org.webtrader.accountDynamicPL.account.ListAccountDynamicPL;
import org.webtrader.accountDynamicPL.account.ListAccounts;
import org.webtrader.accountDynamicPL.log.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Objects;

public class QuoteListener implements QuoteInformerObserver
{
    final private static long INFORMER_CONNECT_RETRY_INTERVAL = 30_000;

    public void start()
    {
        this.connectQuotationInformer();
    }

    private void connectQuotationInformer()
    {
        Log.write( "trying to connect QuoteInformerClient..." );

        WebSocketClient quotationInformerClient = new QuoteInformerClient( this );
        quotationInformerClient.connect();
    }

    private void reconnectQuotationInformer()
    {
        Timer timer    = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                connectQuotationInformer();
            }
        };

        timer.schedule( task, INFORMER_CONNECT_RETRY_INTERVAL );
    }

    public void onQuotationInformerConnectionOpen
            ( ServerHandshake serverHandshake )
    {
        Log.write( "QuoteInformerClient connection OK" );
    }

    @Override
    public synchronized void onQuotationInformerMessageReceived( String message )
    {
        try
        {
            this.notificationsByQuote( this.JSONDecodeMessage( message ) );
        }
        catch( Exception e )
        {
            throw new RuntimeException( "unable to write quotation to database", e );
        }
    }

    @Override
    public void onQuotationInformerConnectionClose( int closeCode, String closeReason, boolean isClosedRemotely )
    {
        Log.write(
                this.createMessageWithReason(closeReason )
        );

        this.reconnectQuotationInformer();
    }

    @Override
    public void onQuotationInformerError( Exception error )
    {
        Log.write(
                this.createMessageWithReason(error.getMessage() )
        );
    }

    private String createMessageWithReason( String reason )
    {
        if ( reason.isBlank() ) {
            return "QuoteInformerClient connection CLOSED";
        }

        return "QuoteInformerClient connection CLOSED" + ": " + reason;
    }

    private JSONObject JSONDecodeMessage( String message )
    {
        return new JSONObject( message );
    }

    private void notificationsByQuote( JSONObject quote )
    {
        try {
            // 326 RUNEUSDT 3.656000 3.657000
            // THETAUSDT 340, bid = 1.18, ask = 1.181;
            // SOLUSDT 195, bid = 129.76, ask = 129.77
//            String symbol;
//            symbol = quote.optString("symbol");
//            if ( Objects.equals(symbol, "SOLUSDT") || Objects.equals(symbol, "COMPUSDT") ) {
//                System.out.println(
//                        quote.getString( "symbol" ) + " " +
//                                quote.getDouble("bid_price") + " " +
//                                quote.getDouble("ask_price")
//                );
//            }

            ListAccounts listAccounts = new ListAccounts();
            List<Integer> listAccountIDs = listAccounts.getListAccountIDs( quote.getString("symbol") );

            if ( listAccountIDs == null || listAccountIDs.isEmpty() ) {
                return;
            }

            ListAccountDynamicPL listAccountDynamicPL = new ListAccountDynamicPL();
            AccountDynamicPL accountDynamicPLs = listAccountDynamicPL.getAccountDynamicPL( listAccountIDs );

//            accountDynamicPLs.printAllRecords();

            accountDynamicPLs.sendNotificationsToAccounts();

//            for ( Integer listAccountID : listAccountIDs ) {
//                System.out.println( listAccountID );
//            }


        } catch ( Throwable e ) {
            Log.write( "Fatal error when searching for ticking orders: " + e.getMessage() );
            System.exit(123);
        }
    }
}
