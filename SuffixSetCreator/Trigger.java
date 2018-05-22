package SuffixSetCreator;

public class Trigger {
	
	private String trigger;
	private State out;
	
	public Trigger(String trigger, State state)
	{
		this.trigger = trigger;
		out = state;
	}
	
	public Trigger()
	{
		trigger = "";
		out = new State();
	}
	
	public String GetTrigger()
	{
		return trigger;
	}
	
	public State GetNextState()
	{
		return out;
	}

}
