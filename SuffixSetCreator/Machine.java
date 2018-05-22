package SuffixSetCreator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.*;

public class Machine {
	private Map<String, State> machine;
	private int treshold;
	private HashSet<String> list;
	
	public Machine()
	{
		list = new HashSet<String>();
		machine = new HashMap<String, State>();
	}
	
	public void CreateMachine(String pathState, String pathFlow) throws IOException
	{
		FileInputStream fstream=new FileInputStream(pathState);
		DataInputStream in=new DataInputStream(fstream);
		BufferedReader reader=new BufferedReader(new InputStreamReader(in));
		
		String line;
		while((line=reader.readLine())!=null){
			if(line=="") break;
			machine.put(line, new State(line));
			
		}
		
		reader.close();
		in.close();
		fstream.close();
		
		fstream=new FileInputStream(pathFlow);
		in=new DataInputStream(fstream);
		reader=new BufferedReader(new InputStreamReader(in));
		
		while((line=reader.readLine())!=null){
			if(line=="") break;
			String[] flow=line.split("\t"); // source trigger target
			State source = machine.get(flow[0]);
			State target = machine.get(flow[2]);
			Trigger t = new Trigger(flow[1], target);
			source.AddTrigger(t);		
		}
		
		reader.close();
		in.close();
		fstream.close();
	}
	
	public void CreateSuffixSet(int treshold, String path, String syllable, int rootType) throws IOException
	{
		FileOutputStream ofstream = new FileOutputStream(path);
		DataOutputStream out = new DataOutputStream(ofstream);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		
		this.treshold = treshold;
		
		State s = machine.get("root" + Integer.toString(rootType));//rootType: 0 for noun, 1 for verb
		String suffix = "";
		Traverse(0, s, suffix, writer, syllable);//Give the last syllable of root
		
		writer.close();
		out.close();
		ofstream.close();
	}
	
	public String Traverse(int step, State s, String str, BufferedWriter writer, String lastSyllable) throws IOException
	{
		if(step < treshold)
		{
			for(int i = 0; i < s.triggers.size(); i++)
			{
				Trigger t = s.triggers.get(i);
				State next = t.GetNextState();
				String current;
				String processedSuffix = "";
				boolean backTrace = false;
				boolean emptyTransaction = false; 
				
				if(t.GetTrigger().equals("0"))
				{
					current = str;
					emptyTransaction = true;
				}
				else
				{
					processedSuffix = ProcessSuffix(t.GetTrigger(), lastSyllable);
					
					if(!processedSuffix.equals(""))
					{
						String softening = checkConsonantSoftening(str, processedSuffix.charAt(0));
						current = softening + processedSuffix;
					}
					else
						current = str;
					
					backTrace = true;
				}
				if(!current.equals(""))
				{
					if(!list.contains(current))
					{
						writer.write(current);
						writer.newLine();
						list.add(current);
						System.out.println(current);
					}
				}
				if(backTrace)
				{
					backTrace = false;
					String lastProcessedSyllable = "";
					if(!processedSuffix.equals(""))
						lastProcessedSyllable = checkConsonantSoftening(lastSyllable, processedSuffix.charAt(0));
					if(emptyTransaction)
					{
						Traverse(step, next, current, writer, lastProcessedSyllable + processedSuffix);
						emptyTransaction = false;
					}
					else
						Traverse(step + 1, next, current, writer, lastProcessedSyllable + processedSuffix);
				}
				else
				{
					if(emptyTransaction)
					{
						Traverse(step, next, current, writer, lastSyllable);
						emptyTransaction = false;
					}
					else
						Traverse(step + 1, next, current, writer, lastSyllable);
				}
			}
		}
		return str;
	}
	
	public String ProcessSuffix(String suffix, String lastSyllable)
	{
		char[] processed = new char[100];
		String processedString;
		char[] arrayFromSuffix = suffix.toCharArray();
		char[] syllable = lastSyllable.toCharArray();
		boolean parenthesis = false;
		
		int j = 0;
		
		for(int i = 0; i < arrayFromSuffix.length; i++)
		{
			if(arrayFromSuffix[i] == '(')
			{
				//ekten önceki hecenin son harfi asil ünlüyse parantez içindeki kalýr
				//deðilse parantez içindeki harf gelmez
				if(arrayFromSuffix[i + 1] == 'y' || arrayFromSuffix[i + 1] == 'þ' || arrayFromSuffix[i + 1] == 's' || arrayFromSuffix[i + 1] == 'n')
				{
					if(isVowel(syllable[syllable.length - 1]))
					{
						i++;
						parenthesis = true;
					}
					else
					{
						i = i + 2;
						continue;
					}
				}
				else
				{
					//if(syllable[syllable.length - 1] == 'a' || syllable[syllable.length - 1] == 'e' || syllable[syllable.length - 1] == 'o' || syllable[syllable.length - 1] == 'ö')
					if(isVowel(syllable[syllable.length - 1]))
					{
						i = i + 2;
						continue;
					}
					else
					{
						i++;//parantez içindeki harf
						parenthesis = true;
					}
				}
			}
			try {
				char c = SubProcess(lastSyllable, arrayFromSuffix[i]);
				processed[j] = c;
				j++;
				if(parenthesis)
				{
					parenthesis = false;
					i++;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.print(e.getMessage());
			}
			
		}
		
		char[] temp = new char[j];
		for(int i = 0; i < j; i++)
			temp[i] = processed[i];
		processedString = new String(temp);
		return processedString;
	}
	
	public char SubProcess(String lastSyllable, char current) throws Exception
	{
		char processed = '\0';
		char vowel = findVowel(lastSyllable);
		char lastLetter = lastSyllable.charAt(lastSyllable.length() - 1);
		
		switch(current)
		{
			case 'A':
				if(vowel != '\0')
				{
					if(vowel == 'i' || vowel == 'ü' || vowel == 'e' || vowel == 'ö')
						processed = 'e';
					else
						processed = 'a';
				}
				else
					throw new Exception("There is no vowel");
				break;
			case 'C':				
				if(lastLetter == 'ç' || lastLetter == 'h' || lastLetter == 'f' || lastLetter == 'k' ||
						lastLetter == 'p' || lastLetter == 's' || lastLetter == 'þ' || lastLetter == 't')
					processed = 'ç';
				else
					processed = 'c';
				break;
			case 'D':
				if(lastLetter == 'ç' || lastLetter == 'h' || lastLetter == 'f' || lastLetter == 'k' ||
						lastLetter == 'p' || lastLetter == 's' || lastLetter == 'þ' || lastLetter == 't')
					processed = 't';
				else
					processed = 'd';
				break;
			case 'I':
				if(vowel == 'e' || vowel == 'i' || vowel == 'ü' || vowel == 'ö')
					processed = 'i';
				if(vowel == 'a' || vowel == 'ý' || vowel == 'u' || vowel == 'o')
					processed = 'ý';
				break;
			case 'H':
				if(vowel == 'e' || vowel == 'i')
					processed = 'i';
				if(vowel == 'a' || vowel == 'ý')
					processed = 'ý';
				if(vowel == 'ö' || vowel == 'ü')
					processed = 'ü';
				if(vowel == 'o' || vowel == 'u')
					processed = 'u';
				break;
			default:
				processed = current;				
		}
		
		if(processed == '\0')
			throw new Exception("Some vowel missing!\n");
		
		return processed;
	}
	
	public char findVowel(String syllable)
	{	
		char c = '\0';	
		for(int i = syllable.length() - 1; i >= 0; i--)
		{
			c = syllable.charAt(i);
			if(isVowel(c))
				break;
		}
		return c;
	}
	
	public String checkConsonantSoftening(String syllable, char firstChar)
	{
		if(syllable.length() == 0)
			return "";
		if(!isVowel(firstChar))
			return syllable;
		char c = syllable.charAt(syllable.length() - 1);
		if(c == 'p' || c == 'ç' || c == 'k' || c == 't')
		{
			char[] tmp = syllable.toCharArray();
			switch(c)
			{
				case 'p':
					tmp[syllable.length() - 1] = 'b';
					break;
				case 'ç':
					tmp[syllable.length() - 1] = 'c';
					break;
				case 'k':
					tmp[syllable.length() - 1] = 'ð';
					break;
				case 't':
					tmp[syllable.length() - 1] = 'd';
					break;
			}
			syllable = new String(tmp);
		}
		
		return syllable;
	}
	
	public boolean isVowel(char c)
	{
		boolean result = (c == 'a' || c == 'e' || c == 'ý' || c == 'i' || c == 'o' || c == 'ö' || c == 'u' || c == 'ü');
		return result;
	}
}
