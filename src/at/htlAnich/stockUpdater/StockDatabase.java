package at.htlAnich.stockUpdater;

import at.htlAnich.tools.database.CanBeTable;
import at.htlAnich.tools.database.Database;
import jdk.jshell.spi.ExecutionControl;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static at.htlAnich.tools.BaumbartLogger.errf;

/**
 * @author Baumbart13
 */
public class StockDatabase extends Database implements CanBeTable {
	public static final String	_TABLE_NAME__DATA	= "stock_data",
					_TABLE_NAME__SYMBOLS	= "stock_symbol";

	public StockDatabase(){
		this("", "", "", "");
	}

	public StockDatabase(String hostname, String user, String password, String database){
		super(hostname, user, password, database);
	}

	public StockDatabase(StockDatabase stockDb){
		this(stockDb.mHostname, stockDb.mUser, stockDb.mPassword, stockDb.mDatabase);
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
		return;
	}

	@Override
	public void disconnect() throws SQLException {
		if(mConnection == null || mConnection.isClosed()){
			return;
		}

		mConnection.close();
		mConnection = null;
		return;
	}

	@Override
	public void createDatabase(String database) throws SQLException {
		var stmnt = mConnection.createStatement();
		stmnt.execute("CREATE DATABASE IF NOT EXISTS " + database.trim());
		return;
	}

	public void createDatabase() throws SQLException{
		createDatabase(mDatabase);
		return;
	}

	public void createTable(String tableName) throws SQLException{
		PreparedStatement stmnt;
		if(tableName.equals(_TABLE_NAME__DATA)){
			stmnt = mConnection.prepareStatement(String.format(
				"CREATE TABLE IF NOT EXISTS %s (" +
					"%s DATETIME NOT NULL," +	// data_datetime
					"%s VARCHAR(8) NOT NULL," +	// data_symbol
					"%s FLOAT," +			// data_open
					"%s FLOAT," +			// data_open
					"%s FLOAT," +			// data_close
					"%s FLOAT," +			// data_high
					"%s FLOAT," +			// data_low
					"%s FLOAT," +			// data_volume
					"%s FLOAT," +			// data_splitCoefficient
					"%s FLOAT," +			// data_close_adjusted
					"PRIMARY KEY(%s, %s);",		// data_datetime, data_symbol

				tableName,
				// PRIMARY KEYS
				StockResults.DatabaseNames_Data.data_datetime.toString(),
				StockResults.DatabaseNames_Data.data_symbol.toString(),
				// VALUES
				StockResults.DatabaseNames_Data.data_open.toString(),
				StockResults.DatabaseNames_Data.data_close.toString(),
				StockResults.DatabaseNames_Data.data_high.toString(),
				StockResults.DatabaseNames_Data.data_low.toString(),
				StockResults.DatabaseNames_Data.data_volume.toString(),
				//StockResults.DatabaseNames_Data.data_avg.toString(200), // removed due to different handling since v2
				StockResults.DatabaseNames_Data.data_splitCoefficient.toString(),
				StockResults.DatabaseNames_Data.data_close_adjusted.toString(),
				// PRIMARY KEY-Declaration
				StockResults.DatabaseNames_Data.data_datetime.toString(),
				StockResults.DatabaseNames_Data.data_symbol.toString()
			));
		}else if(tableName.equals(_TABLE_NAME__SYMBOLS)){
			stmnt = mConnection.prepareStatement(String.format(
				"CREATE TABLE IF NOT EXISTS %S (" +
					"%s VARCHAR(8) PRIMARY KEY NOT NULL," +	// symbol_symbol
					"%s VARCHAR(30) NOT NULL," +		// symbol_name
					"%s INT," +				// symbol_exchange
					"%s INT," +				// symbol_asset
					"%s DATETIME," +			// symbol_ipoDate
					"%s DATETIME," +			// symbol_delistingDate
					"%s INT);",				// symbol_status

				tableName,
				// PRIMARY KEY
				StockResults.DatabaseNames_Symbol.symbol_symbol.toString(),
				// INFORMATION
				StockResults.DatabaseNames_Symbol.symbol_name.toString(),
				StockResults.DatabaseNames_Symbol.symbol_exchange.toString(),
				StockResults.DatabaseNames_Symbol.symbol_asset.toString(),
				StockResults.DatabaseNames_Symbol.symbol_ipoDate.toString(),
				StockResults.DatabaseNames_Symbol.symbol_delistingDate.toString(),
				StockResults.DatabaseNames_Symbol.symbol_status.toString()
			));
		}else{
			stmnt = mConnection.prepareStatement(String.format(
				"SHOW TABLES FROM %s;",
				mDatabase
			));
		}

		stmnt.execute();
	}

	/**
	 * Updates the database with the provided <code>StockResults</code>.
	 * @param results The data that shall be written to the database.
	 * @throws SQLException
	 */
	public void insertOrUpdateStock(StockResults results) throws SQLException{
		createTable(results.getTableName());

		switch(results.getTableType()){
			case DATA -> insertOrUpdateStock_DATA(results);
			case SYMBOL -> insertOrUpdateStock_SYMBOLS(results);
			case NOT_SET -> errf("WTF!? How could you parse an empty StockResults?!");
			default -> errf("That should not be possible to parse a StockResults without a TableType.");
		}
	}

	private void insertOrUpdateStock_DATA(StockResults results){
		final var stmntTextBase = String.format(
			"INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
			_TABLE_NAME__DATA,
			StockResults.DatabaseNames_Data.data_datetime,
			StockResults.DatabaseNames_Data.data_symbol,
			StockResults.DatabaseNames_Data.data_open,
			StockResults.DatabaseNames_Data.data_close,
			StockResults.DatabaseNames_Data.data_high,
			StockResults.DatabaseNames_Data.data_low,
			StockResults.DatabaseNames_Data.data_volume,
			StockResults.DatabaseNames_Data.data_splitCoefficient
			//StockResults.DatabaseNames_Data.data_close_adjusted	// removed due to different handling since v2
		);

		for(var dataPoint : results.getDataPoints()){

		}
	}

	private void insertOrUpdateStock_SYMBOLS(StockResults results){

	}

	private void updateAvgValues() throws SQLException{
		try{
			throw new ExecutionControl.NotImplementedException("Not implemented yet!");
		}catch(ExecutionControl.NotImplementedException e){
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void updateAvgs(List<StockDataPoint> dataPoints) throws SQLException{

		var avgsInResult = new LinkedList<Long>();
		for(var t : dataPoints){
			for(var avg : t.getAverages()){
				if(!avgsInResult.contains(avg)){
					avgsInResult.add(avg);
				}
			}
		}
		var avgsInDatabase = Arrays.asList(getAvgsOnDatabase());
		var avgsToAdd = new LinkedList<Long>();
		for(var t : avgsInResult){
			if(!avgsInDatabase.contains(t)) {
				avgsToAdd.add(t);
			}
		}

		// add new avgs to database
		for(var l : avgsToAdd) {
			var stmnt = mConnection.prepareStatement(String.format(
				"ALTER TABLE %s ADD COLUMN %s FLOAT;",
					_TABLE_NAME__DATA,
					StockResults.DatabaseNames_Data.data_avg.toString(l)
				));
			stmnt.execute();
		}
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
				"WHERE %s = ?" +
				"ORDER BY %s ASC" +
				"LIMIT 201;",
			_TABLE_NAME__DATA,
			StockResults.DatabaseNames_Data.data_symbol.toString(),
			StockResults.DatabaseNames_Data.data_datetime.toString()
		);

		var out = LocalDate.MIN.atStartOfDay();
		try{
			var stmnt = mConnection.prepareStatement(sql);
			stmnt.setString(1, symbol);
			var rs = stmnt.executeQuery();

			rs.first();
			var columnLabel = StockResults.DatabaseNames_Data.data_datetime.toString().toLowerCase();
			out = rs.getTimestamp(columnLabel).toLocalDateTime();

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
