package at.htlAnich.stockUpdater;

import at.htlAnich.stockUpdater.api.ApiParser;
import at.htlAnich.stockUpdater.threading.LoadCredentialsThread;
import at.htlAnich.tools.Environment;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import jdk.jshell.spi.ExecutionControl;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import static at.htlAnich.tools.BaumbartLogger.logf;

public class Stocks {
	public static Random	Dice			= new Random();
	public static boolean	UseGui			= true;
	public static boolean	UseRandomSymbols	= false;
	public static final boolean Flag_Threading	= false;
	public static String	DatabasePath		= "database.csv",
				ApiPath			= "api.csv",
				SymbolsPath		= "symbols.csv",
				AutoLoadPath		= "auto";
	public static StockResults Symbols		= null;
	public static StockDatabase Database		= null;
	public static ApiParser Parser			= null;
	public static final int		WindowWidth	= (int)(Environment.getDesktopWidth_Multiple() * (1.0/2.5)),
					WindowHeight	= (int)(Environment.getDesktopHeight_Multiple() * (1.0/2.7));

	/**
	 * Loads everything needed for the database to work. <code>database.csv</code> must be in the correct folder to
	 * work, else the program will abort.<br><br>
	 * <code>database.csv</code> example:<br>
	 *
	 * <code>hostname,user,password,database<br>
	 * localhost,root,pass1234,BaumbartStocks<br></code>
	 *
	 * @param loadFromFile The filepath of <code>database.csv</code>.
	 * @return the Database responsible for the stock-database.
	 * @see StockDatabase
	 */
	public static StockDatabase loadDb(String loadFromFile) {
		BufferedReader reader = null;
		String[] credentials = null;
		var file = new File(loadFromFile);

		try {
			reader = new BufferedReader(new FileReader(file));

			var line = "";
			while ((line = reader.readLine()) != null) {
				credentials = line.split(",");
			}

			return new StockDatabase(credentials[0], credentials[1], credentials[2], credentials[3]);
		}catch(FileNotFoundException e){
			Stocks.crucialFileMissing(file.getAbsolutePath());
		}catch (IOException e){
			e.printStackTrace();
		}finally {
			try {
				reader.close();
			} catch (IOException e) {
				System.err.printf("Couldn't close file\t\"%s\"%nNot that relevant, because of only read-access.",
						loadFromFile);
				e.printStackTrace();
			}
		}
		// Return empty, not working StockDatabase
		return new StockDatabase();
	}

	/**
	 * Loads everything needed for the api to work. In this case just the APIKey. <code>api.csv</code> must be in the
	 * correct folder to work, else the program will abort.<br><br>
	 * <code>api.csv</code> example:<br>
	 *
	 * <code>apiKey<br>
	 * DEMO<br></code>
	 *
	 * @param loadFromFile The filepath of <code>api.csv</code>.
	 * @return the Parser for the API of <code>www.alphavantage.co</code>.
	 * @see ApiParser
	 */
	public static ApiParser loadApi(String loadFromFile){
		BufferedReader reader = null;
		var apiKey = "";
		var file = new File(loadFromFile);

		try{
			reader = new BufferedReader(new FileReader(file));

			// check 1st line
			var line = reader.readLine();
			if(!line.equals("apiKey"))
				return new ApiParser("");
			while((line = reader.readLine()) != null){
				apiKey = line;
			}

		}catch (FileNotFoundException e){
			Stocks.crucialFileMissing(file.getAbsolutePath());
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				reader.close();
			}catch(IOException e){
				System.err.printf("Couldn't close file\t\"%s\"%nNot that relevant, because of only read-access.",
						loadFromFile);
				e.printStackTrace();
			}
		}

		return new ApiParser(apiKey);
	}

	/**
	 * Loads all symbols saved to <code>symbols.csv</code> into the program for usage of such. <code>symbols.csv</code>
	 * must be in the correct folder to work, else the program will receive the symbols through the database, else
	 * receive it through the API.
	 * @param loadFromFile The filepath of symbols.csv.
	 * @param parser In case of missing file, an <code>ApiParser</code> is being used to request the
	 * <code>symbols.csv</code> and load it.
	 * @return <code>StockResults</code> containing <code>StockSymbolPoint</code>.
	 * @see StockResults
	 * @see StockSymbolPoint
	 */
	public static StockResults loadSymbols(String loadFromFile, ApiParser parser){
		var symbols = new StockResults("");
		BufferedReader reader = null;
		var file = new File(loadFromFile);

		try{
			reader = new BufferedReader(new FileReader(file));

			var line = reader.readLine().trim();
			if(!line.equalsIgnoreCase("symbol,name,exchange,assetType,ipoDate,delistingDate,status")){
				return new StockResults("FATAL API ERROR");
			}

			while((line = reader.readLine()) != null){
				var splittedLine = line.trim().split(",");
				symbols.addSymbolPoint(new StockSymbolPoint(
						splittedLine[0],
						splittedLine[1],
						StockExchangeType.valueOf(splittedLine[2]),
						StockAssetType.valueOf(splittedLine[3]),
						LocalDate.parse(splittedLine[4], DateTimeFormatter.ISO_DATE_TIME),
						LocalDate.parse(splittedLine[5], DateTimeFormatter.ISO_DATE_TIME),
						StockStatus.valueOf(splittedLine[6])
				));
			}

		} catch (FileNotFoundException e) {
			System.err.printf("\"%s\" is missing, requesting it from the API.",
					loadFromFile);

			try {
				symbols = parser.request(
						"",
						ApiParser.Function.LISTING_STATUS,
						ApiParser.Outputsize.full,
						ApiParser.DataType.csv
				);
			}catch(IOException ex){
				ex.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				System.err.printf("Couldn't close file\t\"%s\"%nNot that relevant, because of only Read-Access.",
						loadFromFile);
				e.printStackTrace();
			}
		}
		return symbols;
	}

	public static void main(StockVisualizer gui, String[] args) throws ExecutionControl.NotImplementedException {
		argumentHandling(Arrays.asList(args));

		// We can't load the symbols, as long as the ApiParser has not been loaded completely, due to the fact
		// the symbols will be requested from the Api if the file >symbols.csv< is not present
		if(Flag_Threading) {
			logf("Using Threads");
			var barrier = new CyclicBarrier(2, () -> logf("Thread has arrived at the barrier.%n"));
			var loadDb = new LoadCredentialsThread(LoadCredentialsThread.LoadType.Database, DatabasePath, null);
			var loadApi = new LoadCredentialsThread(LoadCredentialsThread.LoadType.API, ApiPath, null);
			loadDb.start();
			loadApi.start();

			// TODO: implement threading on startup
			try {
				loadDb.join();
				loadApi.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			Database = loadDb(DatabasePath);
			Parser = loadApi(ApiPath);
		}
		Symbols = loadSymbols(SymbolsPath, Parser);

		//gui.setOnlyScreenshots(true);
		if(UseGui){
			logf("Starting GUI%n");
			// TODO: Get the GUI at a later point working

		}else{ // autoupdate
			logf("Starting autoupdate%n");
			var autoUpdater = new StockAutoUpdater(UseRandomSymbols, Database, Parser);

			if(!UseRandomSymbols){
				// TODO: implement random autoupdater
				throw new ExecutionControl.NotImplementedException("Random requesting of symbols not implemented yet");
			}else{
				autoUpdater.initQueue();
			}

			StockResults results = null;
			while((results = autoUpdater.next()) != null){
				autoUpdater.updateDatabase(results);


				// wait a moment to not overshoot the maximum requests of the API
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		logf("Exiting main-method%n");
	}

	public static void argumentHandling(final List<String> args) throws ExecutionControl.NotImplementedException {
		if(args == null){
			System.exit(-1);
		}


		for(int i = 0; i < args.size(); ++i){
			if(!args.get(i).startsWith(ProgramArguments.PREFIX))
				continue;

			switch(ProgramArguments.valueOf(args.get(i).substring(2))){
				case inProduction:
					ApiPath = String.format("res%s%s",
							File.separator, ApiPath);
					DatabasePath = String.format("res%s%s",
							File.separator, DatabasePath);
					SymbolsPath = String.format("res%s%s",
							File.separator, SymbolsPath);
					AutoLoadPath = String.format("res%s%s",
							File.separator, AutoLoadPath);
					break;
				case install:
					throw new ExecutionControl.NotImplementedException("StockInstaller.install not implemented yet.");
				case uninstall:
					throw new ExecutionControl.NotImplementedException("StockInstaller.uninstall not implemented yet.");
				case autoupdate:
					UseGui = false;
					if(args.size() <= i+1)
						break;
					// load a few random ones or from auto
					UseRandomSymbols = args.get(i+1).equalsIgnoreCase("rand");
					break;
				case DEBUG:
					logf("DEBUG mode has been chosen.");
					// DEBUG stuff
					break;
			}
		}
	}

	public static void crucialFileMissing(String nameOfFile){
		System.err.printf("\"%s\" is missing, exiting program.", nameOfFile);
		System.exit(-1);
	}

	public static void test(){
		// create sample content
		Rectangle rect = new Rectangle(100, 100, 200, 300);
		Pane root = new Pane(rect);
		root.setPrefSize(500, 500);

		Parent content = root;

// create scene containing the content
		Scene scene = new Scene(content);

		Stage window = new Stage();
		window.setScene(scene);

// make window visible
		window.show();
	}
}
