package at.htlAnich.stockUpdater;

public enum ProgramArguments {
	DEBUG,
	windowed, w,
	install,
	unsinstall,
	autoupdate,
	inProduction;

	@Override
	public String toString() {
		return "--".concat(this.name());
	}
}
