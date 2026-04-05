package org.webtrader.accountDynamicPL;

import org.webtrader.accountDynamicPL.quote.QuoteListener;

public class Main
{
    public static void main(String[] args)
    {
        QuoteListener quoteListener = new QuoteListener();
        quoteListener.start();
    }
}
