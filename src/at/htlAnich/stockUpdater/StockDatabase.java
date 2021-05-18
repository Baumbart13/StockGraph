package at.htlAnich.stockUpdater;

import at.htlAnich.tools.database.CanBeTable;
import at.htlAnich.tools.database.Database;
import at.htlAnich.tools.database.MySQL;
import jdk.jshell.spi.ExecutionControl;

import java.sql.Date;
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
public class StockDatabase extends MySQL implements CanBeTable {
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
		return new StockDatabase(this);
	}

	@Override
	public void createDatabase(String database) throws SQLException {
		var stmnt = mConnection.createStatement();
		stmnt.execute("CREATE DATABASE IF NOT EXISTS " + database.trim());
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
			case DATA -> {
				updateAvgs(results.getDataPoints());
				insertOrUpdateStock_DATA(results);
			}
			case SYMBOL -> insertOrUpdateStock_SYMBOLS(results);
			case NOT_SET -> errf("WTF!? How could you parse an empty StockResults?!");
			default -> errf("That should not be possible to parse a StockResults without a TableType.");
		}
	}

	private void insertOrUpdateStock_DATA(StockResults results) throws SQLException{

		for(var dataPoint : results.getDataPoints()){
			//////////////////////////////////
			//                              //
			//          UPDATE-TEXT         //
			//                              //
			//////////////////////////////////

			// 9 parameters for the formatstring
			var stmntText = new StringBuilder(String.format(
				"INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s",
				_TABLE_NAME__DATA,
				StockResults.DatabaseNames_Data.data_datetime.toString(),
				StockResults.DatabaseNames_Data.data_symbol.toString(),
				StockResults.DatabaseNames_Data.data_open.toString(),
				StockResults.DatabaseNames_Data.data_close.toString(),
				StockResults.DatabaseNames_Data.data_high.toString(),
				StockResults.DatabaseNames_Data.data_low.toString(),
				StockResults.DatabaseNames_Data.data_volume.toString(),
				StockResults.DatabaseNames_Data.data_splitCoefficient.toString()
				//StockResults.DatabaseNames_Data.data_close_adjusted	// removed due to different handling since v2
			));
			var avgs = dataPoint.getAverages();
			int i;
			for(i = 0; i < avgs.length; ++i){
				stmntText.append(",");
				stmntText.append(StockResults.DatabaseNames_Data.data_avg.toString(avgs[i]));
			}

			// 6 values will be inserted .. starting at open
			stmntText.append(") VALUES (?, ?, ?, ?, ?, ?");
			for(i = 0; i < avgs.length; ++i){
				stmntText.append(", ?");
			}
			stmntText.append(String.format(
				") ON DUPLICATE KEY UPDATE %s=?, %s=?, %s=?, %s=?, %s=?, %s=?",
				StockResults.DatabaseNames_Data.data_open.toString(),
				StockResults.DatabaseNames_Data.data_close.toString(),
				StockResults.DatabaseNames_Data.data_high.toString(),
				StockResults.DatabaseNames_Data.data_low.toString(),
				StockResults.DatabaseNames_Data.data_volume.toString(),
				StockResults.DatabaseNames_Data.data_splitCoefficient.toString()
			));
			for(i = 0; i < avgs.length; ++i){
				stmntText.append(String.format(
					", %s=?",
					StockResults.DatabaseNames_Data.data_avg.toString(avgs[i])
				));
			}
			stmntText.append(';');

			//////////////////////////////////
			//                              //
			//       INSERTING VALUES       //
			//                              //
			//////////////////////////////////

			var stmnt = mConnection.prepareStatement(stmntText.toString());

			// INSERT INTO .. VALUES
			//  1   - data_datetime
			//  2   - data_symbol
			//  3   - data_open
			//  4   - data_close
			//  5   - data_high
			//  6   - data_low
			//  7   - data_volume
			//  8   - data_splitCoefficient
			//  9.. - data_avg..
			// ON DUPLICATE KEY UPDATE
			// 10   - data_open
			// 11   - data_close
			// 12   - data_high
			// 13   - data_low
			// 14   - data_volume
			// 15   - data_splitCoefficient
			// 16.. - data_avg..

			stmnt.setDate(1, Date.valueOf(dataPoint.mDateTime.toLocalDate()));
			stmnt.setString(2, results.getName());
			stmnt.setFloat(3, dataPoint.getValue(StockValueType.open));
			stmnt.setFloat(4, dataPoint.getValue(StockValueType.close));
			stmnt.setFloat(5, dataPoint.getValue(StockValueType.high));
			stmnt.setFloat(6, dataPoint.getValue(StockValueType.low));
			stmnt.setFloat(7, dataPoint.getValue(StockValueType.volume));
			stmnt.setFloat(8, dataPoint.getValue(StockValueType.splitCoefficient));
			for(i = 0; i < avgs.length; ++i){
				stmnt.setFloat(9+i, dataPoint.getValue(StockValueType.avgValue, avgs[i]));
			}
			i = 9+6+i;
			// ON DUPLICATE KEY UPDATE
			stmnt.setFloat(i++, dataPoint.getValue(StockValueType.open));
			stmnt.setFloat(i++, dataPoint.getValue(StockValueType.close));
			stmnt.setFloat(i++, dataPoint.getValue(StockValueType.high));
			stmnt.setFloat(i++, dataPoint.getValue(StockValueType.low));
			stmnt.setFloat(i++, dataPoint.getValue(StockValueType.volume));
			stmnt.setFloat(i++, dataPoint.getValue(StockValueType.splitCoefficient));
			for(int j = 0; j < avgs.length; ++j){
				stmnt.setFloat(i++, dataPoint.getValue(StockValueType.avgValue, avgs[j]));
			}

			// finally update that shit
			stmnt.executeUpdate();
		}

		updateAvgValues();
	}

	private void insertOrUpdateStock_SYMBOLS(StockResults results) throws SQLException{
		for(var symbolPoint : results.getSymbolPoints()){

			//////////////////////////////////
			//                              //
			//          UPDATE-TEXT         //
			//                              //
			//////////////////////////////////

			var stmntText = String.format(
				"INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s)" +
				"VALUES (?, ?, ?, ?, ?, ?, ?) " +
				"ON DUPLICATE KEY UPDATE %s=?, %s=?, %s=?, %s=?, %s=?, %s=?",

				// INSERT INTO
				_TABLE_NAME__SYMBOLS,
				StockResults.DatabaseNames_Symbol.symbol_symbol.toString(),
				StockResults.DatabaseNames_Symbol.symbol_name.toString(),
				StockResults.DatabaseNames_Symbol.symbol_exchange.toString(),
				StockResults.DatabaseNames_Symbol.symbol_asset.toString(),
				StockResults.DatabaseNames_Symbol.symbol_ipoDate.toString(),
				StockResults.DatabaseNames_Symbol.symbol_delistingDate.toString(),
				StockResults.DatabaseNames_Symbol.symbol_status.toString(),

				// VALUES

				//ON DUPLICATE KEY UPDATE
				StockResults.DatabaseNames_Symbol.symbol_name.toString(),
				StockResults.DatabaseNames_Symbol.symbol_exchange.toString(),
				StockResults.DatabaseNames_Symbol.symbol_asset.toString(),
				StockResults.DatabaseNames_Symbol.symbol_ipoDate.toString(),
				StockResults.DatabaseNames_Symbol.symbol_delistingDate.toString(),
				StockResults.DatabaseNames_Symbol.symbol_status.toString()
			);

			var stmnt = mConnection.prepareStatement(stmntText);

			// INSERT INTO ... VALUES
			//  1   - symbol_symbol
			//  2   - symbol_name
			//  3   - symbol_exchange
			//  4   - symbol_asset
			//  5   - symbol_ipoDate
			//  6   - symbol_delistingDate
			//  7   - symbol_status
			// ON DUPLICATE KEY UPDATE
			//  8   - symbol_name
			//  9   - symbol_exchange
			// 10   - symbol_asset
			// 11   - symbol_ipoDate
			// 12   - symbol_delistingDate
			// 13   - symbol_status

			stmnt.setString(1, symbolPoint.getSymbol());
			stmnt.setString(2, symbolPoint.getName());
			stmnt.setInt(3, symbolPoint.getExchange().ordinal());
			stmnt.setInt(4, symbolPoint.getAsset().ordinal());
			stmnt.setDate(5, Date.valueOf(symbolPoint.getIpoDate()));
			stmnt.setDate(6, Date.valueOf(symbolPoint.getDelistingDate()));
			stmnt.setInt(7, symbolPoint.getStatus().ordinal());
			// ON DUPLICATE KEY UPDATE
			stmnt.setString(8, symbolPoint.getName());
			stmnt.setInt(9, symbolPoint.getExchange().ordinal());
			stmnt.setInt(10, symbolPoint.getAsset().ordinal());
			stmnt.setDate(11, Date.valueOf(symbolPoint.getIpoDate()));
			stmnt.setDate(12, Date.valueOf(symbolPoint.getDelistingDate()));
			stmnt.setInt(13, symbolPoint.getStatus().ordinal());

			// finally update that shit
			stmnt.executeUpdate();
		}
	}

	/**
	 * Simply creates a new MySQL-Function, if it's not present and executes it.
	 * @throws SQLException
	 */
	private void updateAvgValues() throws SQLException{
		try{
			throw new ExecutionControl.NotImplementedException("Not implemented yet!");
		}catch(ExecutionControl.NotImplementedException e){
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Inserts new avg-columns into the db-table.
	 * @param dataPoints The object eventually containing new averages.
	 * @throws SQLException
	 */
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
	 * Returns the existing average-columns on the database.
	 * @return an array with every average-column existing on the database.
	 */
	public Long[] getAvgsOnDatabase(){
		var out = new LinkedList<Long>();
		final var sql = "SHOW COLUMNS FROM " + _TABLE_NAME__DATA + ";";

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
