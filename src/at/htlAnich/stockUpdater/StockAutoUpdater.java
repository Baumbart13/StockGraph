package at.htlAnich.stockUpdater;

import at.htlAnich.stockUpdater.api.ApiParser;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Queue;

import static at.htlAnich.tools.BaumbartLogger.errf;

public class StockAutoUpdater {
	private static final String QueuePath = "request.csv";
	public static final int QueueMaxLength = 10;

	protected boolean mUsesRandomSymbols;
	protected Queue<String> mAutoRequestQueue = new java.util.LinkedList<>();
	protected StockDatabase mDatabase;
	protected ApiParser mApiParser;


	public StockAutoUpdater(boolean useRandom, @NotNull StockDatabase db, @NotNull ApiParser api){
		this.mUsesRandomSymbols = useRandom;
		if(!useRandom){
			initQueue();
		}

		this.mDatabase = db;
		this.mApiParser = api;
	}

	/**
	 * Used for random requests.
	 * @param symbols a container with a few symbols to fill the queue.
	 * @param useAll if set to <code>true</code> the complete container will be added to the queue (not recommended
	 *               for bigger containers).
	 */
	public void initQueue(@NotNull List<String> symbols, boolean useAll){
		if(this.mAutoRequestQueue.size() > 0){
			errf("Can only be executed on an empty Queue.%n");
			return;
		}
		if(useAll){
			this.mAutoRequestQueue.addAll(symbols);
			return;
		}

		for(int i = 0; i < QueueMaxLength; ++i){
			this.mAutoRequestQueue.add(symbols.get(Stocks.Dice.nextInt(symbols.size())));
		}
	}

	/**
	 * Used for random requests loaded from file.
	 */
	public void initQueue(){
		if(this.mAutoRequestQueue.size() > 0){
			errf("Can only be executed on an empty Queue.%n");
			return;
		}
		if(this.mUsesRandomSymbols) {
			errf("Supposed to only do random request.%n");
			return;
		}

		BufferedReader reader = null;
		File file;
		if(Stocks.isInProduction()){
			file = new File("res\\".concat(QueuePath));
		}else{
			file = new File(QueuePath);
		}

		try{
			reader = new BufferedReader(new FileReader(file));
			var line = "";
			while((line = reader.readLine()) != null){
				this.mAutoRequestQueue.add(line);
			}

		}catch(FileNotFoundException e){
			Stocks.crucialFileMissing(file.getAbsolutePath());
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				reader.close();
			}catch (IOException e){
				System.err.printf("Couldn't close file\t\"%s\"%nNot that relevant, because of only read-access.%n",
					file.getName());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Retrieves the next <code>StockResult</code> and deletes the previous one.
	 * @return the retrieved <code>StockResult</code>.
	 */
	public StockResults next(){
		StockResults result = null;
		try {
			result = mApiParser.request(mAutoRequestQueue.remove(), ApiParser.Function.TIME_SERIES_DAILY_ADJUSTED);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Retrieves the next <code>StockResult</code> and does not delete the previous one.
	 * @return the retrieved <code>StockResult</code>.
	 */
	public StockResults hasNext(){
		StockResults result = null;
		try{
			result = mApiParser.request(mAutoRequestQueue.element(), ApiParser.Function.TIME_SERIES_DAILY_ADJUSTED);
		}catch (IOException e){
			e.printStackTrace();
		}
		return result;
	}

	public void updateDatabase(StockResults results){
		try {
			mDatabase.connect();
			mDatabase.insertOrUpdateStock(results);
		}catch (SQLException e){
			e.printStackTrace();
		}finally {
			try {
				mDatabase.disconnect();
			}catch (SQLException e){
				e.printStackTrace();
			}
		}
	}
}
