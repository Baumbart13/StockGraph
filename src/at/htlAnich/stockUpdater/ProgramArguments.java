package at.htlAnich.stockUpdater;

public enum ProgramArguments {
	DEBUG,
	windowed, w,
	install,
	uninstall,
	autoupdate,
	inProduction;

	public static final String PREFIX = "--";

	@Override
	public String toString() {
		return PREFIX.concat(this.name());
	}
}
