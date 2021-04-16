package at.htlAnich.stockUpdater.api;

import at.htlAnich.stockUpdater.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ApiProcessor {

	public static StockResults processDailyAdjusted(@NotNull JSONObject json, @NotNull String symbol){
		var results = new StockResults(symbol);
		json = json.getJSONObject(json.keys().next());

		for(var key : json.keySet()){
			//yyyy-mm-dd
			var dateTime = LocalDate.parse(key).atStartOfDay();

			//1. open
			//2. high
			//3. low
			//4. close
			//5. adjusted close
			//6. volume
			//7. divided amount
			//8. split coefficient
			StockDataPoint dataPoint = new StockDataPoint(dateTime);
			var item = json.getJSONObject(key);
			dataPoint.setValue(StockValueType.open, item.getFloat("1. open"));
			dataPoint.setValue(StockValueType.high, item.getFloat("2. high"));
			dataPoint.setValue(StockValueType.low, item.getFloat("3. low"));
			dataPoint.setValue(StockValueType.close, item.getFloat("5. adjusted close"));
			dataPoint.setValue(StockValueType.volume, item.getFloat("6. volume"));
			dataPoint.setValue(StockValueType.avgValue, 0.0f);
			dataPoint.setValue(StockValueType.splitCoefficient, item.getFloat("8. split coefficient"));
			results.addDataPoint(dataPoint);
		}

		return results;
	}

	public static StockResults processDaily(@NotNull JSONObject json, @NotNull String symbol){
		var results = new StockResults(symbol);
		json = json.getJSONObject(json.keys().next());

		for(var key : json.keySet()){
			//yyyy-mm-dd
			var dateTime = LocalDate.parse(key).atStartOfDay();

			//1. open
			//2. high
			//3. low
			//4. close
			//5. volume
			StockDataPoint dataPoint = new StockDataPoint(dateTime);
			var item = json.getJSONObject(key);
			dataPoint.setValue(StockValueType.open, item.getFloat("1. open"));
			dataPoint.setValue(StockValueType.high, item.getFloat("2. high"));
			dataPoint.setValue(StockValueType.low, item.getFloat("3. low"));
			dataPoint.setValue(StockValueType.close, item.getFloat("4. close"));
			dataPoint.setValue(StockValueType.volume, item.getFloat("5. volume"));
			dataPoint.setValue(StockValueType.avgValue, 0.0f);
			dataPoint.setValue(StockValueType.splitCoefficient, item.getFloat("8. split coefficient"));
			results.addDataPoint(dataPoint);
		}

		return results;
	}

	public static StockResults processListingStatus(@NotNull String csv){
		// symbol,name,exchange,assetType,ipoDate,delistingDate,status
		//
		// String,String,StockExchangeType,StockAssetType,LocalDate,LocalDate,StockStatus
		var results = new StockResults("SYMBOL_LOADING");

		var choppedCsv = csv.split("\r\n");
		for(var line : choppedCsv){
			var params = line.split(",");

			results.addSymbolPoint(new StockSymbolPoint(
					params[0],
					params[1],
					StockExchangeType.valueOf(params[2]),
					StockAssetType.valueOf(params[3]),
					LocalDate.parse(params[4], DateTimeFormatter.ISO_DATE),
					LocalDate.parse(params[5], DateTimeFormatter.ISO_DATE),
					StockStatus.valueOf(params[6])
			));
		}

		return results;
	}
}
