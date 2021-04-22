package at.htlAnich.stockUpdater;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class StockSymbolPoint {
	private String	mSymbol,
					mName = "";
	private StockExchangeType	mExchange;
	private StockAssetType	mAsset;
	private LocalDate	mIpoDate,
						mDelistingDate;
	private StockStatus	mIsActive;

	@Override
	protected StockSymbolPoint clone() throws CloneNotSupportedException {
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

	public static StockSymbolPoint of(String line){
		var params = line.split(",");
		if(params.length != 7) return null;
		return new StockSymbolPoint(params[0], params[1], StockExchangeType.valueOf(params[2]),
				StockAssetType.valueOf(params[3]), LocalDate.parse(params[4]), LocalDate.parse(params[5]),
				StockStatus.valueOf(params[6]));
	}

}

