package org.webtrader.accountDynamicPL.account;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.webtrader.accountDynamicPL.notifier.Notifier;

public class AccountDynamicPL
{
    // Используем Map, где ключ — это accountID, а значение — dynamicPL
    private final Map<Integer, Double> dynamicPLMap;

    private final Notifier notifier;

    public AccountDynamicPL()
    {
        this.dynamicPLMap = new HashMap<>();
        this.notifier = new Notifier();
    }

    public void addRecord( int accountID, double dynamicPL )
    {
        dynamicPLMap.put( accountID, dynamicPL );
    }

    public Double getDynamicPL( int accountID )
    {
        return dynamicPLMap.get( accountID );
    }

    public Map<Integer, Double> getAllRecords()
    {
        return dynamicPLMap;
    }

    public void printAllRecords()
    {
        for ( Map.Entry<Integer, Double> entry : dynamicPLMap.entrySet() )
        {
            System.out.println("account_id: " + entry.getKey() + ", profit_loss_opened_orders: " + entry.getValue());
        }
    }

    public void sendNotificationsToAccounts() throws Exception
    {
        this.notifier.sendNotifications( this );
    }

    public String toJson()
    {
        JSONObject jsonObject = new JSONObject();

        for ( Map.Entry<Integer, Double> entry : dynamicPLMap.entrySet() )
        {
            jsonObject.put( String.valueOf( entry.getKey() ), entry.getValue() );
        }

        return jsonObject.toString();
    }
}
