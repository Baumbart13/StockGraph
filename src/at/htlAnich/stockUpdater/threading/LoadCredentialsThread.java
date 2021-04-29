package at.htlAnich.stockUpdater.threading;

import at.htlAnich.stockUpdater.StockDatabase;
import at.htlAnich.stockUpdater.StockResults;
import at.htlAnich.stockUpdater.Stocks;
import at.htlAnich.stockUpdater.api.ApiParser;
import javafx.application.Platform;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static at.htlAnich.tools.BaumbartLogger.logf;
import static at.htlAnich.tools.BaumbartLogger.errf;

public class LoadCredentialsThread extends Thread {
	private Thread mThread			= null;
	private LoadType mThreadType		= null;
	private String mFile			= "";
	private StockResults mSymbols		= null;
	private ApiParser mApiParser		= null;
	private StockDatabase mDatabase		= null;
	private static CyclicBarrier cBarrier	= null;

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

	/**
	 * @param type Which Credentials do you want to load with this Thread?
	 * @param loadFromFile From which path do you want to load the credentials?
	 * @param parser Only needed for <code>LoadType.Symbols</code>
	 */
	public LoadCredentialsThread(LoadType type, String loadFromFile, ApiParser parser){
		mThreadType = type;
		mFile = loadFromFile;
		mApiParser = parser;
	}

	public LoadCredentialsThread(LoadType type, String loadFromFile, ApiParser parser, CyclicBarrier barrier){
		this(type, loadFromFile, parser);
		if(cBarrier==barrier)
			return;
		cBarrier = barrier;
	}

	@Override
	public void start(){
		if(mThread == null){
			mThread = new Thread(this, mThreadType.toString());
			mThread.start();

			Platform.runLater(() -> {
				switch(mThreadType){
					case Database -> Stocks.Database = this.mDatabase;
					case API -> Stocks.Parser = this.mApiParser;
					case Symbols -> Stocks.Symbols = this.mSymbols;
				}
			});
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
		try{
			cBarrier.await();
		}catch (InterruptedException e){
			errf("Thread jas been interrupted.");
			e.printStackTrace();
			return;
		}catch (BrokenBarrierException e){
			errf("LoadCredentials-Thread has broken barrier, what's the matter?");
			e.printStackTrace();
			return;
		}
	}
}
