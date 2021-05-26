package at.htlAnich.backTestingSuite;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import at.htlAnich.backTestingSuite.ProgramArguments;

import static at.htlAnich.tools.BaumbartLogger.logf;


public class BackTesting {
	public static Scanner cliInput = new Scanner(System.in);

	public static void argumentHandling(List<String> args){
		if(args == null){
			System.exit(-1);
		}

		for(int i = 0; i < args.size(); ++i){
			if(!args.get(i).startsWith(ProgramArguments.PREFIX))
				continue;

			switch(ProgramArguments.valueOf(args.get(i).substring(2))){
				case inProduction:
					logf("We are in production now%n");
					break;
				case DEBUG:
					logf("DEBUG suite entered%n");
					break;
			}
		}
	}

	public static void main(String[] args) {
		argumentHandling(Arrays.asList(args));
		logf("Please enter your wanted stock: ");
		var symbol = cliInput.nextLine();

		Depot vals = null;
		try {
			var backDb = new BacktestingDatabase("localhost:3306", "root", "DuArschloch4", "baumbartstocks");
			backDb.connect();
			backDb.createDatabase(backDb.getDatabase());
			backDb.createTable(BacktestingDatabase._TABLE_NAME);
			vals = backDb.getValues(symbol);
			Trader.trade(vals);

			backDb.updateDepots(vals, symbol);
			backDb.disconnect();
		}catch (SQLException e){
			e.printStackTrace();
		}

	}
}
