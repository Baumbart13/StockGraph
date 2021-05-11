package at.htlAnich.stockUpdater;

import at.htlAnich.stockUpdater.api.ApiParser;

import at.htlAnich.stockUpdater.threading.LoadCredentialsThread;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jdk.jshell.spi.ExecutionControl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import static at.htlAnich.tools.BaumbartLogger.errf;
import static at.htlAnich.tools.BaumbartLogger.logf;

public class StockVisualizer extends Application{
	private StockDatabase mDatabase	= null;
	private ApiParser mApiParser	= null;
	private StockResults mSymbols	= null;
	private int mPtrToCurSymbol	= 0;
	private String[] mParams	= null;

	private LineChart mChart	= null;
	private boolean mOnlyScreenshot	= false;
	private Stage mStage		= null;

	private int	mWindowWidth	= (int)(at.htlAnich.tools.Environment.getDesktopWidth_Multiple() * (1.0/2.599)),
			mWindowHeight	= (int)(at.htlAnich.tools.Environment.getDesktopHeight_Multiple() * (1.0/2.799));

	public StockVisualizer(StockDatabase db, ApiParser api, StockResults symbols, String ... params){
		mDatabase = new StockDatabase(db);
		mApiParser = new ApiParser(api);
		mSymbols = new StockResults(symbols);
		mParams = params.clone();
	}

	public StockVisualizer(){
		this(new StockDatabase(), new ApiParser(""), null);
	}

	public void init(String ... params){
		this.mParams = params;
	}

	@Override
	public void start(Stage stage) {
		this.mStage = stage;
		this.init(Integer.toString(Stocks.WindowWidth), Integer.toString(Stocks.WindowHeight));
		try {
			logf("Executing the original main%n");
			Stocks.main(this, getParameters().getRaw().toArray(new String[0]));
		} catch (ExecutionControl.NotImplementedException e) {
			e.printStackTrace();
		}

		try {
			var width = Integer.parseInt(this.mParams[0]);
			this.mWindowWidth = width;
			var height = Integer.parseInt(this.mParams[1]);
			this.mWindowHeight = height;
		}catch(NumberFormatException e){
			errf("Lol, window dimensions couldn't be parsed, for what ever reason");
		}
		logf("preparing the Layout%n");
		prepareLayout(this.mStage, this.mWindowWidth, this.mWindowHeight);
		this.mStage.setX(this.mWindowWidth - this.mStage.getWidth());
		this.mStage.setY(this.mWindowHeight - this.mStage.getHeight());

		logf("Starting up the UI%n");
		stage.show();
	}

	public void prepareLayout(int width, int height){
		prepareLayout(this.mStage, width, height);
	}

	public void prepareLayout(Stage stage, int width, int height){
		// Main Layout
		var vBoxMain = new VBox();
		vBoxMain.setSpacing(10);
		vBoxMain.setPadding(new Insets(8,8,8,8));

		// Head Layout
		var headBox = new HBox();
		headBox.setSpacing(10);

		// Symbols
		var ol = FXCollections.observableArrayList(new LinkedList<>(mSymbols.getSymbolPoints()));
		Collections.sort(ol);
		this.mPtrToCurSymbol = Stocks.Dice.nextInt(ol.size());

		final var comboBox = new ComboBox<StockSymbolPoint>(ol);
		comboBox.getSelectionModel().select(this.mPtrToCurSymbol);
		comboBox.setOnAction((e) -> {
			this.mPtrToCurSymbol = ol.indexOf(comboBox.valueProperty().getValue());
		});

		var bttnRequest = new Button();
		bttnRequest.setText("Load data");
		bttnRequest.setOnAction((e) -> {
			var t = ol.get(this.mPtrToCurSymbol).getSymbol();

			if(t.contains("-"))
				t = t.replace("-", "_");

			requestStockDataAsync(t);
		});

		var lblScreenshot = new Label();

		var bttnScreenshot = new Button();
		bttnScreenshot.setText("Take Screenshot");
		bttnScreenshot.setOnAction((e) -> {
			//new Thread(() -> {
				lblScreenshot.setText(String.format("Saved to: %s",
					takeScreenshot()));
				try {
					wait(5000);
				}catch(InterruptedException ex){
					ex.printStackTrace();
				}
			//}).start();
		});

		final var xAxis = new CategoryAxis();
		final var yAxis = new NumberAxis();
		xAxis.setLabel("Date");
		yAxis.setLabel("Value [$]");

		mChart = new LineChart(xAxis, yAxis);
		mChart.setCreateSymbols(false);
		mChart.setAnimated(false);
		VBox.setVgrow(mChart, Priority.ALWAYS);

		headBox.getChildren().addAll(comboBox, bttnRequest, bttnScreenshot);
		vBoxMain.getChildren().addAll(headBox, mChart, lblScreenshot);

		var scene = new Scene(vBoxMain, mWindowWidth, mWindowHeight);
		stage.setScene(scene);
	}

	public void setOnlyScreenshots(boolean b){
		this.mOnlyScreenshot = b;

		var temp = "lol";

		System.out.println("TEST: " + temp);
		System.exit(42069);
	}

	private void requestStockDataAsync(String symbol){

		final var database = new StockDatabase(this.mDatabase);
		var loadThread = new Thread(() -> {
			StockResults results = null;
			try{
				results = mApiParser.request(symbol, ApiParser.Function.TIME_SERIES_DAILY_ADJUSTED);
				updateDatabase(results, database);
			}catch(IOException e){
				errf("There was an error while receiving data from the API.");
				e.printStackTrace();
			}
		});
		loadThread.start();

	}

	public void updateDatabase(StockResults results){
		updateDatabase(results, this.mDatabase);
	}

	public void updateDatabase(StockResults results, StockDatabase stockDb){
		try{
			stockDb.connect();
			stockDb.insertOrUpdateStock(results);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	/**
	 * Takes Screenshot of applications current state.
	 * @return the absolute filepath of the output picture.
	 */
	public String takeScreenshot(){
		try{
			throw new ExecutionControl.NotImplementedException(String.format(
				"\"public String %s.takeScreenshot()\" not implemented yet.",
				StockVisualizer.class
			));
		}catch(ExecutionControl.NotImplementedException e){
			e.printStackTrace();
		}
		System.exit(-1);
		return null;
	}


	public static void main(String[] args) throws ExecutionControl.NotImplementedException {
		Stocks.argumentHandling(Arrays.asList(args));

		// We can't load the symbols, as long as the ApiParser has not been loaded completely, due to the fact
		// the symbols will be requested from the Api if the file >symbols.csv< is not present
		if(Stocks.Flag_Threading) {
			logf("Using Threads%n");
			var barrier = new CyclicBarrier(2, () -> logf("Thread has arrived at the barrier.%n"));
			var loadDb = new LoadCredentialsThread(LoadCredentialsThread.LoadType.Database, Stocks.DatabasePath, null);
			var loadApi = new LoadCredentialsThread(LoadCredentialsThread.LoadType.API, Stocks.ApiPath, null);
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
			logf("Using no Threads%n");
			Stocks.Database = Stocks.loadDb(Stocks.DatabasePath);
			Stocks.Parser = Stocks.loadApi(Stocks.ApiPath);
		}
		Stocks.Symbols = Stocks.loadSymbols(Stocks.SymbolsPath, Stocks.Parser);

		var gui = new StockVisualizer(Stocks.Database, Stocks.Parser, Stocks.Symbols);
		gui.init(Integer.toString(Stocks.WindowWidth), Integer.toString(Stocks.WindowHeight));

		//gui.setOnlyScreenshots(true);
		if(Stocks.UseGui){
			logf("Starting GUI%n");
			// TODO: Get the GUI at a later point working

		}else{ // autoupdate
			logf("Starting autoupdate%n");
			var autoUpdater = new StockAutoUpdater(Stocks.UseRandomSymbols, Stocks.Database, Stocks.Parser);

			if(!Stocks.UseRandomSymbols){
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
		launch();

		logf("Exiting main-method%n");
	}
}
