package SuffixSetCreator;

import java.util.*;

public class State {
	public List<Trigger> triggers;
	private String state; 
	
	public State()
	{
		triggers = new ArrayList<Trigger>();
		state = "";
	}
	
	public State(String state)
	{
		triggers = new ArrayList<Trigger>();
		this.state = state;
	}
	
	public void AddTrigger(String str, State state)
	{
		triggers.add(new Trigger(str, state));
	}
	
	public void AddTrigger(Trigger trigger)
	{
		triggers.add(trigger);
	}
	
	public String toString()
	{
		return state;
	}

}
