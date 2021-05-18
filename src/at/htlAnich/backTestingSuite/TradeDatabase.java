package at.htlAnich.backTestingSuite;

import at.htlAnich.tools.database.Database;
import at.htlAnich.tools.database.MySQL;

import java.sql.SQLException;

public class TradeDatabase extends MySQL {
	public static final String _TABLE_NAME = "stock_trade";

	public TradeDatabase(String hostname, String user, String password, String database) {
		super(hostname, user, password, database);
	}

	public TradeDatabase(TradeDatabase tradeDb){
		this(tradeDb.mHostname, tradeDb.mUser, tradeDb.mPassword, tradeDb.mDatabase);
	}

	@Override
	public void createDatabase(String database) throws SQLException {
		var stmnt = mConnection.prepareStatement(String.format(
			"CREATE DATABASE IF NOT EXISTS %s;",
			database.trim()
		));
		stmnt.executeUpdate();
	}

	@Override
	public Database clone() {
		return new TradeDatabase(this);
	}
}
