package at.htlAnich.stockUpdater.threading;

import at.htlAnich.stockUpdater.StockDatabase;
import at.htlAnich.stockUpdater.StockResults;
import at.htlAnich.stockUpdater.Stocks;
import at.htlAnich.stockUpdater.api.ApiParser;
import static at.htlAnich.tools.BaumbartLogger.logf;

public class LoadCredentialsThread extends Thread {
	private Thread mThread = null;
	private LoadType mThreadType = null;
	private String mFile = "";
	private StockResults mSymbols = null;
	private ApiParser mApiParser = null;
	private StockDatabase mDatabase = null;

	public enum LoadType{
		API,
		Symbols,
		Database;

		@Override
		public String toString() {
			return String.format("Load %s-Thread", this.name());
		}
	}

	public boolean isBusy(){
		return !(getState() == State.TERMINATED || getState() == State.NEW);
	}

	public StockResults getLoadedSymbols(){
		if(mThreadType == LoadType.Symbols && !isBusy())
			return mSymbols;
		return null;
	}

	public ApiParser getLoadedApiParser(){
		if(mThreadType == LoadType.API && !isBusy())
			return mApiParser;
		return null;
	}

	public StockDatabase getLoadedDatabase(){
		if(mThreadType == LoadType.Database && !isBusy())
			return mDatabase;
		return null;
	}

	public LoadCredentialsThread(LoadType type, String loadFromFile, ApiParser parser){
		mThreadType = type;
		mFile = loadFromFile;
		mApiParser = parser;
	}

	@Override
	public void start(){
		if(mThread == null){
			mThread = new Thread(this, mThreadType.toString());
			mThread.start();
		}
	}

	@Override
	public void run(){
		logf("Loading %s now.", mThreadType.toString());
		switch(mThreadType){
			case API -> mApiParser = Stocks.loadApi(mFile);
			case Symbols -> mSymbols = Stocks.loadSymbols(mFile, mApiParser);
			case Database -> mDatabase = Stocks.loadDb(mFile);
		}
		logf("Ended loading %s.", mThreadType.toString());
	}
}
