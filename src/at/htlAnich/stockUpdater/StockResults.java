package at.htlAnich.stockUpdater;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class StockResults {
	private HashMap<StockValueType, Float>	LowerBounds,
											UpperBounds;
	private LocalDateTime	OldestDate,
							NewestDate;
	private List<StockDataPoint>	DataPoints;
	private List<StockSymbolPoint>	SymbolPoints;
	private String	Symbol,
					Name;

	public enum DatabaseNames_Data{
		stock_datetime,
		stock_symbol,
		open,
		close,
		high,
		low,
		volume,
		splitCoefficient,
		close_adjusted,
		avg;

		public String toString(long avgDays){
			if(this==avg){
				return this.name()+Long.toString(avgDays);
			}
			return this.name();
		}
	}

	public enum DatabaseNames_Symbol{

	}

	@Override
	public StockResults clone(){
		return new StockResults(this);
	}

	public StockResults(StockResults other){
		this.LowerBounds = new HashMap<StockValueType, Float>(other.LowerBounds);
		this.UpperBounds = new HashMap<StockValueType, Float>(other.UpperBounds);
		this.OldestDate = LocalDateTime.of(other.OldestDate.toLocalDate(), other.OldestDate.toLocalTime());
		this.NewestDate = LocalDateTime.of(other.NewestDate.toLocalDate(), other.NewestDate.toLocalTime());
		this.DataPoints = other.DataPoints.subList(0, other.DataPoints.size());
		this.SymbolPoints = other.SymbolPoints.subList(0, other.SymbolPoints.size());
		this.Symbol = other.Symbol.toString();
		this.Name = other.Name;
	}

	public StockResults(String symbol, String name){
		this.Symbol = symbol;
		this.Name = name;
		this.LowerBounds = new HashMap<>();
		this.UpperBounds = new HashMap<>();
		this.DataPoints = new LinkedList<>();
		this.SymbolPoints = new LinkedList<>();
		this.OldestDate = LocalDateTime.MAX;
		this.NewestDate = LocalDateTime.MIN;

		for(var x : StockValueType.values()){
			LowerBounds.put(x, Float.MAX_VALUE);
			UpperBounds.put(x, -Float.MAX_VALUE);
		}
	}

	public StockResults(String symbol){
		this(symbol, "");
	}

	public void addSymbolPoint(StockSymbolPoint point){
		SymbolPoints.add(point);
	}

	public void addDataPoint(StockDataPoint point){
		DataPoints.add(point);

		if(point.DateTime.isAfter(NewestDate)){
			NewestDate = point.DateTime;
		}
		if(point.DateTime.isBefore(OldestDate)){
			OldestDate = point.DateTime;
		}

		for(var x : StockValueType.values()){
			if(x == null || x == StockValueType.avgValue)
				continue;

			if (point.getValue(x) < LowerBounds.get(x)) {
				LowerBounds.put(x, point.getValue(x));
			}
			if (point.getValue(x) > UpperBounds.get(x)) {
				UpperBounds.put(x, point.getValue(x));
			}
		}
		return;
	}

	public LocalDateTime getOldestDate() {
		return OldestDate;
	}

	public LocalDateTime getNewestDate(){
		return NewestDate;
	}

	/**
	 * @return the lower bound of the given type, except <code>StockValueType.avgValue</code>: this will return 0.0f.
	 */
	public float getLowerBound(StockValueType t){
		if(t == StockValueType.avgValue) return 0.0f;
		return LowerBounds.get(t);
	}

	/**
	 * @return the upper bound of the given type, except <code>StockValueType.avgValue</code>: this will return 0.0f.
	 */
	public float getUpperBound(StockValueType t){
		if(t == StockValueType.avgValue) return 0.0f;
		return UpperBounds.get(t);
	}

	/**
	 * @return the range of the given type, except <code>StockValueType.avgValue</code>: this will return 0.0f.
	 */
	public float getRange(StockValueType t){
		if(t == StockValueType.avgValue) return 0.0f;
		return UpperBounds.get(t) - LowerBounds.get(t);
	}

	public List<StockDataPoint> getDataPoints(){
		return DataPoints;
	}

	public List<StockSymbolPoint> getSymbolPoints(){
		return SymbolPoints;
	}

	/**
	 * The short form of the stock.
	 */
	public String getSymbol(){
		return Symbol;
	}

	/**
	 * The full name of the stock.
	 */
	public String getName(){
		return Name;
	}
}
