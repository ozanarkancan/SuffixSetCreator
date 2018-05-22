package Test;

import java.io.IOException;

import SuffixSetCreator.*;

public class TestMachine {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Machine m = new Machine();
		m.CreateMachine("src/Data/EntireMachineStates", "src/Data/EntireMachineFlow");
		m.CreateSuffixSet(5, "src/Data/Deneme", "yür", 2);
	}

}
