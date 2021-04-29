package at.htlAnich.stockUpdater;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class StockSymbolPoint implements Comparable{
	private String	mSymbol = "",
			mName = "";
	private StockExchangeType mExchange;
	private StockAssetType mAsset;
	private LocalDate	mIpoDate,
				mDelistingDate;
	private StockStatus mIsActive;

	@Override
	protected StockSymbolPoint clone() {
		return new StockSymbolPoint(this);
	}

	public StockSymbolPoint(StockSymbolPoint other){
		this(	other.mSymbol.toString(),
				other.mName.toString(),
				StockExchangeType.valueOf(other.mExchange),
				StockAssetType.valueOf(other.mAsset.name()),
				LocalDate.of(other.mIpoDate.getYear(), other.mIpoDate.getMonth(), other.mIpoDate.getDayOfMonth()),
				((other.mDelistingDate ==null) ? null : LocalDate.of(other.mDelistingDate.getYear(),
						other.mDelistingDate.getMonth(), other.mDelistingDate.getDayOfMonth())),
				StockStatus.valueOf(other.mIsActive.name())
		);
	}

	public StockSymbolPoint(@NotNull String symbol, String name, StockExchangeType exchange, StockAssetType asset,
							@NotNull LocalDate ipoDate, LocalDate delistingDate, StockStatus isActive){
		mSymbol = symbol;
		mName = (name == null || name.length() < symbol.length()) ? symbol : name;
		mExchange = exchange;
		mAsset = asset;
		mIpoDate = ipoDate;
		mDelistingDate = delistingDate;
		mIsActive = isActive;
	}

	public String getSymbol() {
		return mSymbol;
	}

	public String getName() {
		return mName;
	}

	public StockExchangeType getExchange() {
		return mExchange;
	}

	public StockAssetType getAsset() {
		return mAsset;
	}

	public LocalDate getIpoDate() {
		return mIpoDate;
	}

	public LocalDate getDelistingDate() {
		return mDelistingDate;
	}

	public StockStatus getIsActive() {
		return mIsActive;
	}

	public static StockSymbolPoint of(String line){
		var params = line.split(",");
		if(params.length != 7) return null;
		return new StockSymbolPoint(params[0], params[1], StockExchangeType.valueOf(params[2]),
				StockAssetType.valueOf(params[3]), LocalDate.parse(params[4]), LocalDate.parse(params[5]),
				StockStatus.valueOf(params[6]));
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
		if(o.getClass().equals(this.getClass()))
			return this.mSymbol.compareTo(((StockSymbolPoint)o).mSymbol);
		return this.toString().compareTo(o.toString());
	}
}

