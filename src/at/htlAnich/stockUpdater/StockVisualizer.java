package at.htlAnich.stockUpdater;

import at.htlAnich.stockUpdater.api.ApiParser;
import javafx.application.Application;
import javafx.scene.chart.LineChart;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class StockVisualizer extends Application {
	private StockDatabase mDatabase = null;
	private ApiParser mApiParser = null;
	private StockResults mSymbols = null;
	private long mPtrToCurSymbol = 0;

	private Stage mStage = null;
	private LineChart mChart = null;

	private static int	WindowWidth = (int)(at.htlAnich.tools.Environment.getDesktopWidth_Multiple() * (1.0/2.5)),
						WindowHeight = (int)(at.htlAnich.tools.Environment.getDesktopHeight_Multiple() * (1.0/2.7));

	public StockVisualizer(StockDatabase db, ApiParser api, StockResults symbols){
		mDatabase = db;
		mApiParser = api;
		mSymbols = symbols;
	}

	@Override
	public void start(Stage stage) throws Exception {
		var params = getParameters().getRaw().toArray(new String[0]);
		WindowWidth = Integer.parseInt(params[0]);
		WindowHeight = Integer.parseInt(params[1]);
	}
}
