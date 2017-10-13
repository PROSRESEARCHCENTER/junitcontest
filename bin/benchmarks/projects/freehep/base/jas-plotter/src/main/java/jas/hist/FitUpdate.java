package jas.hist;

public class FitUpdate
{
	FitUpdate(int state)
	{
		this.state = state;
		this.percent = state == Fitter.FIT ? 100 : 0;
	}
	FitUpdate(int state, int percent)
	{
		this.state = state;
		this.percent = percent;
	}
	FitUpdate(int state, FitFailed x)
	{
		this.state = state;
		this.percent = 0;
		this.reason = x;
	}
	public int getState()
	{
		return state;
	}
	public int getPercent()
	{
		return percent;
	}
	public FitFailed getReason()
	{
		return reason;
	}
	private int state;
	private int percent;
	private FitFailed reason;
}
