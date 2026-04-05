package org.webtrader.accountDynamicPL.account;

import org.webtrader.accountDynamicPL.mariadb.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ListAccounts
{
    public List<Integer> getListAccountIDs( String symbol )
    {
        try {
            return this.fetchFromDB( symbol );
        } catch ( Exception e ) {
            throw new RuntimeException( "Error get the list of account IDs: ", e );
        }
    }

    private List<Integer> fetchFromDB( String symbol ) throws Exception
    {
        List<Integer> listAccountIDs = new ArrayList<>();
        PreparedStatement stmt = getPreparedStatement();

        stmt.setString( 1, symbol );

        ResultSet rs = stmt.executeQuery();
        while ( rs.next() ) {
            listAccountIDs.add( rs.getInt("accountID") );
        }

        return listAccountIDs;
    }

    private static PreparedStatement getPreparedStatement() throws Exception
    {
        String sql = """
                SELECT 
                    DISTINCT `orders`.`account_id` AS accountID
                FROM `orders`
                INNER JOIN `quote` ON `quote`.id = `orders`.`quote_id`
                WHERE `quote`.`symbol` = ?
                  AND `quote`.`is_deleted` = 0
                  AND `quote`.`is_trading` = 1
                  AND `orders`.`state` = 1
                  AND `orders`.`deleted_at` is null
                """;
        Connection connection = DBConnection.getInstance();

        return connection.prepareStatement( sql );
    }
}
