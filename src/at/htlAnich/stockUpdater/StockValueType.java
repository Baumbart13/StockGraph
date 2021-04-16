package at.htlAnich.stockUpdater;

public enum StockValueType {
	high,
	low,
	open,
	close,
	volume,
	splitCoefficient,
	close_adjusted,
	close_adjusted_own,
	/**
	 * This is used, when an average based over the last X days is used. Default-value for this is 200.
	 * The <code>toString()</code> method will return a string containing <code>avg200</code>.
	 * The <code>avgValue.toString(316)</code> method will return a string containing <code>avg316</code>
	 */
	avgValue;

	/**
	 * Prefer this method oever the <code>name()</code> when in use with database, user, etc
	 * @param daysAvg The base for the average.
	 */
	public String toString(long daysAvg){
		if(this == avgValue){
			return "avg" + Long.toString(daysAvg & ~0x8000_0000_0000_0000L);
		}
		return name();
	}

	/**
	 * Prefer this method over the <code>name()</code>, when in use with database, User, etc
	 */
	public String toString(){
		return toString(200);
	}
}
