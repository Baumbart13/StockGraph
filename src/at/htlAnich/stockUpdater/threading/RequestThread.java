package at.htlAnich.stockUpdater.threading;

import at.htlAnich.stockUpdater.StockResults;

public class RequestThread extends Thread{
	private Thread mThread		= null;
	private StockResults results	= null;

	@Override
	public synchronized void start() {
		super.start();
	}

	@Override
	public void run() {
		super.run();
	}
}
