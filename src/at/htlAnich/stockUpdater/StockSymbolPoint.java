package at.htlAnich.stockUpdater;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class StockSymbolPoint {
	private String	Symbol,
					Name = "";
	private StockExchangeType Exchange;
	private StockAssetType Asset;
	private LocalDate	IpoDate,
						DelistingDate;
	private StockStatus IsActive;

	@Override
	protected StockSymbolPoint clone() throws CloneNotSupportedException {
		return new StockSymbolPoint(this);
	}

	public StockSymbolPoint(StockSymbolPoint other){
		this(	other.Symbol.toString(),
				other.Name.toString(),
				StockExchangeType.valueOf(other.Exchange),
				StockAssetType.valueOf(other.Asset.name()),
				LocalDate.of(other.IpoDate.getYear(), other.IpoDate.getMonth(), other.IpoDate.getDayOfMonth()),
				((other.DelistingDate==null) ? null : LocalDate.of(other.DelistingDate.getYear(),
						other.DelistingDate.getMonth(), other.DelistingDate.getDayOfMonth())),
				StockStatus.valueOf(other.IsActive.name())
		);
	}

	public StockSymbolPoint(@NotNull String symbol, String name, StockExchangeType exchange, StockAssetType asset,
							@NotNull LocalDate ipoDate, LocalDate delistingDate, StockStatus isActive){
		Symbol = symbol;
		Name = (name == null || name.length() < symbol.length()) ? symbol : name;
		Exchange = exchange;
		Asset = asset;
		IpoDate = ipoDate;
		DelistingDate = delistingDate;
		IsActive = isActive;
	}

	public static StockSymbolPoint of(String line){
		var params = line.split(",");
		if(params.length != 7) return null;
		return new StockSymbolPoint(params[0], params[1], StockExchangeType.valueOf(params[2]),
				StockAssetType.valueOf(params[3]), LocalDate.parse(params[4]), LocalDate.parse(params[5]),
				StockStatus.valueOf(params[6]));
	}

}

