package at.htlAnich.stockUpdater;

import at.htlAnich.tools.database.CanBeTable;
import at.htlAnich.tools.database.Database;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;

public class StockDatabase extends Database implements CanBeTable {
	private static final String _TABLE_NAME__DATA = "stock_data";
	private static final String _TABLE_NAME__SYMBOLS = "stock_symbol";

	public StockDatabase(String hostname, String user, String password, String database){
		super(hostname, user, password, database);
	}

	@Override
	public Database clone() {
		return new StockDatabase(mHostname, mUser, mPassword, mDatabase);
	}

	@Override
	public void connect() throws SQLException{

		if(mConnection != null){
			if(!mConnection.isClosed()){
				System.out.println("Connection already opened.");
				return;
			}
			return;
		}

		mConnection = DriverManager.getConnection(String.format(
				"jdbc:mysql://%s/%s?user=%s&password=%s?serverTimezone=UTC",
				mHostname, mDatabase, mUser, mPassword
		));
	}

	@Override
	public void disconnect() throws SQLException {
		if(mConnection == null || mConnection.isClosed()){
			return;
		}

		mConnection.close();
		mConnection = null;
	}

	@Override
	public void createDatabase(String database) throws SQLException {
		var stmnt = mConnection.createStatement();
		stmnt.execute("CREATE DATABASE IF NOT EXISTS " + database.trim());
	}

	public void createDatabase() throws SQLException{
		createDatabase(mDatabase);
	}

	/**
	 *
	 * @return
	 */
	public Long[] getAvgsOnDatabase(){
		var out = new LinkedList<Long>();
		var sql = "SHOW COLUMNS FROM " + _TABLE_NAME__DATA + ";";

		try{
			var stmnt = mConnection.prepareStatement(sql);
			var rs = stmnt.executeQuery();
			while(rs.next()){
				var line = rs.getString("Field");
				out.add(Long.valueOf(line.replace("avg", "")));
			}

		}catch(SQLException e){
			e.printStackTrace();
		}

		return out.toArray(new Long[out.size()]);
	}

	public LocalDateTime getNewestStockDataEntry(String symbol){
		var sql = String.format("SELECT * FROM %s" +
										"WHERE stock_symbol = ?" +
										"ORDER BY stock_datetime ASC" +
										"LIMIT 201;",
				_TABLE_NAME__DATA
		);

		var out = LocalDate.MIN.atStartOfDay();
		try{
			var stmnt = mConnection.prepareStatement(sql);
			stmnt.setString(1, symbol);
			var rs = stmnt.executeQuery();

			rs.first();
			out = rs.getTimestamp("stock_datetime").toLocalDateTime();

		}catch (SQLException e){
			e.printStackTrace();
		}
		return out;
	}

	/**
	 * Requests the data about the stock from the database.
	 * @param symbol The stock that shall be requested.
	 * @return the data of the provided symbol.
	 */
	public StockResults getStockData(String symbol){
		var out = new StockResults(symbol);
		var sql = String.format("SELECT * FROM %s" +
										"WHERE stock_symbol = ?" +
										"ORDER BY stock_datetime ASC;",

				_TABLE_NAME__DATA
		);

		try {
			var stmnt = mConnection.prepareStatement(sql);
			stmnt.setString(1, symbol);
			var rs = stmnt.executeQuery();

			while(rs.next()){
				var dataPoint = new StockDataPoint(rs.getTimestamp("stock_datetime").toLocalDateTime());
				var rsAvgs = getAvgsOnDatabase();

				// First insert the averages
				for(var l : rsAvgs){
					dataPoint.setValue(StockValueType.avgValue, rs.getFloat(StockValueType.avgValue.toString()), l);
				}

				// then insert the other values
				for(var t : StockValueType.values()){

					if(t == StockValueType.avgValue){
						continue;
					}
					dataPoint.setValue(t, rs.getFloat(t.name()));
				}
			}

		}catch(SQLException e){
			e.printStackTrace();
		}

		return out;
	}

	/**
	 * <strong>DataTables structure:</strong><br>
	 * 	<code>stock_datetime	DATETIME NOT NULL,<br>
	 * 	stock_symbol	VARCHAR(8) NOT NULL,<br>
	 * 	open FLOAT,<br>
	 * 	close FLOAT,<br>
	 * 	high FLOAT,<br>
	 * 	low FLOAT,<br>
	 * 	volume FLOAT,<br>
	 * 	splitCoefficient FLOAT,<br>
	 * 	close_adjusted FLOAT,<br>
	 * 	avgX FLOAT</code><br><br>
	 * "avgX" stands for the many averages, each based on a different amount of days.<br><br><br>
	 *
	 * <strong>SymbolTables structure:</strong><br>
	 * 	<code>symbol	STRING NOT NULL,<br>
	 * 	exchange	UINT64,<br>
	 * 	asset	UINT64,<br>
	 * 	ipoDate	DATETIME NOT NULL,<br>
	 * 	delistingDate	DATETIME NOT NULL,<br>
	 * 	status	UINT32</code><br><br>
	 *
	 * @return the name of the table.
	 */
	@Override
	public String getTableName() {
		return String.format("\"%s\" and \"%s\"",
				_TABLE_NAME__DATA, _TABLE_NAME__SYMBOLS);
	}
}
