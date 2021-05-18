package at.htlAnich.backTestingSuite;

import java.util.Arrays;
import java.util.List;

import at.htlAnich.backTestingSuite.ProgramArguments;

import static at.htlAnich.tools.BaumbartLogger.logf;


public class BackTesting {
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
		System.out.println("Hello Backtesting suite");

		var trader = new Trader();

	}
}
