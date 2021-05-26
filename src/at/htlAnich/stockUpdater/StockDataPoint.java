package at.htlAnich.stockUpdater;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class StockDataPoint {
	public LocalDateTime mDateTime;
	private Map<StockValueType, Float> mValues;
	private Map<Long, Float> mAvgs;

	@Override
	public StockDataPoint clone(){
		return new StockDataPoint(this);
	}

	public StockDataPoint(StockDataPoint other){
		this.mDateTime = LocalDateTime.of(other.mDateTime.toLocalDate(), other.mDateTime.toLocalTime());
		this.mValues = new HashMap<StockValueType, Float>(other.mValues);
		this.mAvgs = new HashMap<Long, Float>(other.mAvgs);
	}

	public StockDataPoint(LocalDateTime dateTime){
		this.mDateTime = LocalDateTime.of(dateTime.toLocalDate(), dateTime.toLocalTime());
		this.mValues = new HashMap<StockValueType, Float>();
		this.mAvgs = new HashMap<Long, Float>();
	}

	/**
	 * Returns the value of <code>StockValueType</code>. If <code>StockValueType.avgValue</code> is used, the average
	 * over the last 200 days will be returned.
	 * @param type Which <code>StockValueType</code> do you want?
	 * @return the value of the <code>StockValueType</code>.
	 * @see StockValueType
	 */
	public float getValue(StockValueType type){
		return getValue(type, 200);
	}

	/**
	 * Returns the value of <code>StockValueType</code>. If <code>StockValueType.avgValue</code> is used, the average
	 * over the last <code>daysAvg</code> will be returned.
	 * @param type Which <code>StockValueType</code> do you want?
	 * @param daysAvg The amount of days on which the average was calculated.
	 * @return the value of the <code>StockValueType</code>.
	 * @see StockValueType
	 */
	public float getValue(StockValueType type, long daysAvg){
		if(type != StockValueType.avgValue){
			return (mValues.get(type)==null) ? 1.0f : mValues.get(type);
		}
		return mAvgs.get(daysAvg & ~0x8000_0000_0000_0000L);
	}

	/**
	 * Sets the value of <code>StockValueType</code>. If <code>StockValueType.avgValue</code> is used, the average
	 * over the last <code>daysAvg</code> will be set.
	 * @param type This <code>StockValueType</code> will be affected.
	 * @param value This value will be set.
	 * @param daysAvg The amount of days on which the average was calculated.
	 * @see StockValueType
	 */
	public void setValue(StockValueType type, float value, long daysAvg){
		if(type != StockValueType.avgValue){
			mValues.put(type, value);
			return;
		}
		mAvgs.put(daysAvg, value);
	}

	/**
	 * Sets the value of <code>StockValueType</code>. If <code>StockValueType.avgValue</code> is used, the average
	 * over the last 200 will be set.
	 * @param type This <code>StockValueType</code> will be affected.
	 * @param value This value will be set.
	 * @see StockValueType
	 */
	public void setValue(StockValueType type, float value){
		setValue(type, value, 200);
	}

	public Long[] getAverages(){
		return mAvgs.keySet().toArray(new Long[mAvgs.keySet().size()]);
	}
}
