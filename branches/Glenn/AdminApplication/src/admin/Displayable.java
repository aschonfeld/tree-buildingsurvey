package admin;

public abstract class Displayable {

	private boolean display;

	public Displayable() {
		display = false;
	}

	public Boolean getDisplay() {
		return display;
	}

	public void setDisplay(Boolean display) {
		this.display = display;
	}

	public void toggleDisplay() {
		this.display = !display;
	}

	public abstract String toString();
}
