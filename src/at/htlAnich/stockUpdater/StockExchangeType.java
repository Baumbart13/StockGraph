package at.htlAnich.stockUpdater;

public enum StockExchangeType {
	BATS,
	NASDAQ,
	NYSE,
	NYSE_ARCA,
	NYSE_MKT;

	@Override
	public String toString() {
		return this.name().replace('_',' ');
	}

	public static StockExchangeType valueOf(StockExchangeType t){
		return StockExchangeType.valueOf(t.name());
	}

	public static StockExchangeType valueOf(int i){
		for(var x : values()){
			if(x.ordinal() == i){
				return x;
			}
		}
		return null;
	}
}
