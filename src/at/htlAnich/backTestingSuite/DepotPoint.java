package at.htlAnich.backTestingSuite;

import java.time.LocalDate;

import org.jetbrains.annotations.NotNull;

public class DepotPoint implements Comparable {

	public enum BuyFlag{
		BUY,
		SELL,
		UNCHANGED;
	}

	private LocalDate mDate	= null;
	private String mSymbol	= null;
	private BuyFlag mFlag	= null;
	private int mBuyAmount	= 0;
	private int mStocks	= 0;
	private float mWorth	= 0.0f;
	private float mAvg200	= 0.0f;
	private float mClose	= 0.0f;

	public DepotPoint(){
		this(LocalDate.now(), "", BuyFlag.UNCHANGED, 0, 0, 0.0f, 0.0f, 0.0f);
	}

	public DepotPoint(@NotNull LocalDate date, @NotNull String symbol, BuyFlag flag, int buyAmount, int totalStocks, float totalWorth, float avg200, float close){
		mDate = date;
		mSymbol = symbol;
		mFlag = (flag == null) ? BuyFlag.UNCHANGED : flag;
		mFlag = flag;
		// Let's stay positive
		mBuyAmount = Math.abs(buyAmount);
		mStocks = Math.abs(totalStocks);
		mWorth = Math.abs(totalWorth);
		mAvg200 = avg200;
		mClose = close;
	}

	@NotNull
	public LocalDate getDate() {
		return mDate;
	}

	@NotNull
	public String getSymbol() {
		return mSymbol;
	}

	@NotNull
	public BuyFlag getFlag() {
		return mFlag;
	}

	public int getBuyAmount() {
		return mBuyAmount;
	}

	public int getStocks() {
		return mStocks;
	}

	public float getWorth() {
		return mWorth;
	}

	public float getAvg200(){
		return mAvg200;
	}

	public float getClose(){
		return mClose;
	}

	/**
	 * <p>In the foregoing description, the notation
	 * {@code sgn(}<i>expression</i>{@code )} designates the mathematical
	 * <i>signum</i> function, which is defined to return one of {@code -1},
	 * {@code 0}, or {@code 1} according to whether the value of
	 * <i>expression</i> is negative, zero, or positive, respectively.
	 *
	 * @param o the object to be compared.
	 * @return a negative integer, zero, or a positive integer as this object
	 * is less than, equal to, or greater than the specified object.
	 * @throws NullPointerException if the specified object is null
	 * @throws ClassCastException   if the specified object's type prevents it
	 *                              from being compared to this object.
	 */
	@Override
	public int compareTo(@NotNull Object o) {
		if(o.getClass().equals(this.getClass())) {
			var temp = (DepotPoint)o;
			if(temp.mSymbol.equals(this.mSymbol)) {
				return this.mDate.compareTo(temp.mDate);
			}
		}
		return this.toString().compareTo(o.toString());
	}
}
