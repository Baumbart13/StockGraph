package at.htlAnich.tools;

public final class BaumbartLogger {
	public static void logf(String s, Object ... o){
		System.out.printf(s, o);
	}
	public static void errf(String s, Object ... o){
		System.err.printf(s, o);
	}
	public static void waitForKeyPress(){var s = new java.util.Scanner(System.in); s.nextLine();}
}
