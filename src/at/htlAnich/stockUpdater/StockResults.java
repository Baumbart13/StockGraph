package at.htlAnich.stockUpdater;

import at.htlAnich.tools.database.CanBeTable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is the interface between the API and Database. It supports <code>StockDataPoint</code> and
 * <code>StockSymbolPoint</code>.
 */
public class StockResults implements CanBeTable {
	private HashMap<StockValueType, Float>	mLowerBounds,
						mUpperBounds;
	private LocalDateTime	mOldestDate,
				mNewestDate;
	private List<StockDataPoint> mDataPoints;
	private List<StockSymbolPoint> mSymbolPoints;
	private String	mSymbol,
			mName;
	private Type mTableType;

	public enum Type{
		NOT_SET,
		DATA,
		SYMBOL;
	}

	public enum DatabaseNames_Data{
		data_datetime,
		data_symbol,
		data_open,
		data_close,
		data_high,
		data_low,
		data_volume,
		data_splitCoefficient,
		data_close_adjusted,
		data_avg;

		public String toString(long avgDays){
			if(this == data_avg){
				return this.toString()+Long.toString(avgDays);
			}
			return this.toString();
		}
	}

	public enum DatabaseNames_Symbol{
		symbol_symbol,	// primary key
		symbol_name,
		symbol_exchange,
		symbol_asset,
		symbol_ipoDate,
		symbol_delistingDate,
		symbol_status;
	}

	@Override
	public StockResults clone(){
		return new StockResults(this);
	}

	/**
	 * Copy constructor
	 * @param other Copies the values from <code>other</code> to a new instance.
	 */
	public StockResults(StockResults other){
		this.mTableType = Type.NOT_SET;
		this.mLowerBounds = new HashMap<StockValueType, Float>(other.mLowerBounds);
		this.mUpperBounds = new HashMap<StockValueType, Float>(other.mUpperBounds);
		this.mOldestDate = LocalDateTime.of(other.mOldestDate.toLocalDate(), other.mOldestDate.toLocalTime());
		this.mNewestDate = LocalDateTime.of(other.mNewestDate.toLocalDate(), other.mNewestDate.toLocalTime());
		this.mDataPoints = other.mDataPoints.subList(0, other.mDataPoints.size());
		this.mSymbolPoints = other.mSymbolPoints.subList(0, other.mSymbolPoints.size());
		this.mSymbol = other.mSymbol.toString();
		this.mName = other.mName;
	}

	public StockResults(String symbol, String name){
		this.mTableType = Type.NOT_SET;
		this.mSymbol = symbol;
		this.mName = name;
		this.mLowerBounds = new HashMap<>();
		this.mUpperBounds = new HashMap<>();
		this.mDataPoints = new LinkedList<>();
		this.mSymbolPoints = new LinkedList<>();
		this.mOldestDate = LocalDateTime.MAX;
		this.mNewestDate = LocalDateTime.MIN;

		for(var x : StockValueType.values()){
			mLowerBounds.put(x, Float.MAX_VALUE);
			mUpperBounds.put(x, -Float.MAX_VALUE);
		}
	}

	/**
	 *
	 * @param symbol
	 */
	public StockResults(String symbol){
		this(symbol, "");
	}

	public void addSymbolPoint(StockSymbolPoint point){
		if(mDataPoints.size() == 0 && mSymbolPoints.size() == 0)
			mTableType = Type.SYMBOL;

		if(mTableType != Type.SYMBOL)
			return;

		mSymbolPoints.add(point);
	}

	public void addDataPoint(StockDataPoint point){
		if(mDataPoints.size() == 0 && mSymbolPoints.size() == 0)
			mTableType = Type.DATA;

		if(mTableType != Type.DATA)
			return;

		mDataPoints.add(point);

		if(point.mDateTime.isAfter(mNewestDate)){
			mNewestDate = point.mDateTime;
		}
		if(point.mDateTime.isBefore(mOldestDate)){
			mOldestDate = point.mDateTime;
		}

		for(var x : StockValueType.values()){
			if(x == null || x == StockValueType.avgValue)
				continue;

			if (point.getValue(x) < mLowerBounds.get(x)) {
				mLowerBounds.put(x, point.getValue(x));
			}
			if (point.getValue(x) > mUpperBounds.get(x)) {
				mUpperBounds.put(x, point.getValue(x));
			}
		}
		return;
	}

	public LocalDateTime getOldestDate() {
		return mOldestDate;
	}

	public LocalDateTime getNewestDate(){
		return mNewestDate;
	}

	/**
	 * @return the lower bound of the given type, except <code>StockValueType.avgValue</code>: this will return 0.0f.
	 */
	public float getLowerBound(StockValueType t){
		if(t == StockValueType.avgValue) return 0.0f;
		return mLowerBounds.get(t);
	}

	/**
	 * @return the upper bound of the given type, except <code>StockValueType.avgValue</code>: this will return 0.0f.
	 */
	public float getUpperBound(StockValueType t){
		if(t == StockValueType.avgValue) return 0.0f;
		return mUpperBounds.get(t);
	}

	/**
	 * @return the range of the given type, except <code>StockValueType.avgValue</code>: this will return 0.0f.
	 */
	public float getRange(StockValueType t){
		if(t == StockValueType.avgValue) return 0.0f;
		return mUpperBounds.get(t) - mLowerBounds.get(t);
	}

	public List<StockDataPoint> getDataPoints(){
		return mDataPoints;
	}

	public List<StockSymbolPoint> getSymbolPoints(){
		return mSymbolPoints;
	}

	/**
	 * The short form of the stock.
	 */
	public String getSymbol(){
		return mSymbol;
	}

	/**
	 * The full name of the stock.
	 */
	public String getName(){
		return mName;
	}

	@Override
	/**
	 * @return A string, which tells if this object handles the data or the symbols.
	 */
	public String getTableName(){
		if(mDataPoints == null && mSymbolPoints != null){
			return StockDatabase._TABLE_NAME__SYMBOLS;
		}
		if(mDataPoints != null && mSymbolPoints == null){
			return StockDatabase._TABLE_NAME__DATA;
		}
		return "";
	}

	public Type getTableType(){
		return mTableType;
	}
}
