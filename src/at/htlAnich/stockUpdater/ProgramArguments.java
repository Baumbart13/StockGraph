package at.htlAnich.stockUpdater;

public enum ProgramArguments {
	DEBUG,
	windowed, w,
	install,
	uninstall,
	autoupdate,
	inProduction;

	@Override
	public String toString() {
		return "--".concat(this.name());
	}
}
