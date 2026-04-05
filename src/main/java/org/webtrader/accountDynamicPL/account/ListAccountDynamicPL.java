package org.webtrader.accountDynamicPL.account;

import org.webtrader.accountDynamicPL.mariadb.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.StringJoiner;

public class ListAccountDynamicPL
{
    public AccountDynamicPL getAccountDynamicPL( List<Integer> listAccountIDs )
    {
        try {
            return this.fetchFromDB( listAccountIDs );
        } catch ( Exception e ) {
            throw new RuntimeException( "Fetch from DB table accounts error ", e );
        }
    }

    private AccountDynamicPL fetchFromDB( List<Integer> listAccountIDs ) throws Exception
    {
        AccountDynamicPL accountDynamicPL = new AccountDynamicPL();

        PreparedStatement stmt = getPreparedStatement( listAccountIDs );

        ResultSet rs = stmt.executeQuery();
        while ( rs.next() ) {
            accountDynamicPL.addRecord(
                    rs.getInt("account_id"),
                    rs.getDouble("profit_loss_opened_orders")
            );
        }

        return accountDynamicPL;
    }

    private static PreparedStatement getPreparedStatement( List<Integer> accountIDs ) throws Exception
    {
        // Создание плейсхолдеров для IN выражения в зависимости от количества accountIDs
        StringJoiner placeholders = new StringJoiner(", ");
        for ( int i = 0; i < accountIDs.size(); i++ ) {
            placeholders.add("?");
        }

        String sql = """
            SELECT
                o.account_id,
                SUM(
                    CASE
                        WHEN o.type = 0 THEN 
                            (q.bid_price * o.open_rate - o.open_price * o.open_rate) * o.lots * o.units - o.commission
                        ELSE
                            (o.open_price * o.open_rate - q.ask_price * o.open_rate) * o.lots * o.units - o.commission
                    END
                ) AS profit_loss_opened_orders
            FROM orders o
            INNER JOIN quote q ON o.quote_id = q.id
            INNER JOIN accounts a ON o.account_id = a.id
            WHERE a.id IN (""" + placeholders + """
              ) AND a.deleted_at IS NULL -- Аккаунты не мягко удаленные
              AND q.is_deleted = 0
              AND o.state = 1 -- Ордера в состоянии POSITION
              AND o.deleted_at IS NULL -- Ордера не мягко удаленные
            GROUP BY o.account_id;
            """;

        // Получаем соединение и создаем подготовленный запрос
        Connection connection = DBConnection.getInstance();
        PreparedStatement stmt = connection.prepareStatement(sql);

        // Заполняем плейсхолдеры значениями accountIDs
        for ( int i = 0; i < accountIDs.size(); i++ ) {
            stmt.setInt(i + 1, accountIDs.get(i));
        }

        return stmt;
    }

}
