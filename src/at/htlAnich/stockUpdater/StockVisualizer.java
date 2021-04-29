package at.htlAnich.stockUpdater;

import at.htlAnich.stockUpdater.api.ApiParser;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import static at.htlAnich.tools.BaumbartLogger.errf;

public class StockVisualizer extends Application {
	private StockDatabase mDatabase	= null;
	private ApiParser mApiParser	= null;
	private StockResults mSymbols	= null;
	private int mPtrToCurSymbol	= 0;

	private Stage mStage		= null;
	private LineChart mChart	= null;

	private int	mWindowWidth	= (int)(at.htlAnich.tools.Environment.getDesktopWidth_Multiple() * (1.0/2.5)),
			mWindowHeight	= (int)(at.htlAnich.tools.Environment.getDesktopHeight_Multiple() * (1.0/2.7));

	public StockVisualizer(StockDatabase db, ApiParser api, StockResults symbols){
		mDatabase = db;
		mApiParser = api;
		mSymbols = symbols;
	}

	@Override
	public void start(Stage stage) throws Exception {
		var params = getParameters().getRaw().toArray(new String[0]);

		try {
			var width = Integer.parseInt(params[0]);
			mWindowWidth = width;
			var height = Integer.parseInt(params[1]);
			mWindowHeight = height;
		}catch(NumberFormatException e){
			errf("Lol, window dimensions couldn't be parsed, for what ever reason");
		}
		prepareLayout(stage, mWindowWidth, mWindowHeight);
		stage.setX(mWindowWidth - stage.getWidth());
		stage.setY(mWindowHeight - stage.getHeight());
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
		mPtrToCurSymbol = Stocks.Dice.nextInt(ol.size());

		final var comboBox = new ComboBox<StockSymbolPoint>(ol);
		comboBox.getSelectionModel().select(mPtrToCurSymbol);
		comboBox.setOnAction((e) -> {
			mPtrToCurSymbol = ol.indexOf(comboBox.valueProperty().getValue());
		});

		var bttnRequest = new Button();
		bttnRequest.setText("Load data");
		bttnRequest.setOnAction((e) -> {
			var t = ol.get(mPtrToCurSymbol).getSymbol();

			if(t.contains("-"))
				t = t.replace("-", "_");

			requestStockDataAsync(t);
		});

		var lblScreenshot = new Label();

		var bttnScreenshot = new Button();
		bttnScreenshot.setText("Take Screenshot");
		bttnScreenshot.setOnAction((e) -> {
			new Thread(() -> {
				lblScreenshot.setText(String.format("Saved to: %s",
					takeScreenshot()));
				try {
					wait(5000);
				}catch(InterruptedException ex){
					ex.printStackTrace();
				}
			}).start();
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

	private void requestStockDataAsync(String symbol){
		try {
			throw new ExecutionControl.NotImplementedException(String.format(
				"\"private void %s.requestStockDataAsync(String)\" not implemented yet.",
				this.getClass()
			));
		}catch(ExecutionControl.NotImplementedException e){
			e.printStackTrace();
		}
		System.exit(-1);
		return;
	}

	public String takeScreenshot(){
		try{
			throw new ExecutionControl.NotImplementedException(String.format(
				"\"public String %s.takeScreenshot()\" not implemented yet.",
				this.getClass()
			));
		}catch(ExecutionControl.NotImplementedException e){
			e.printStackTrace();
		}
		System.exit(-1);
		return null;
	}
}
