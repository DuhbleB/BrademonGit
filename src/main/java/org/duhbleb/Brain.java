package org.duhbleb;

import java.util.Scanner;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class Brain {

	public static Brain singleton;
	
	public static Battle currentBattle;
	public static Brademon currentBrademonInEdit;
	
	public InputInterpreter interpreter;
	public JDA jda;
	
	private boolean isListening;
	private String userNameListeningFor;
	private ListenTarget listenTarget;
	private MessageReceiveMode messageReceiveMode;
	
	private boolean shouldContinue;
	
	
	public Brain(){
		
		if(singleton == null)
			singleton = this;
		else
			return;
	}
	
	public void doReadyEvent(ReadyEvent e){
		
		isListening = false;
		listenTarget = ListenTarget.idle;
		messageReceiveMode = MessageReceiveMode.utility;
		
		sendMessage(Main.homeChannel, "Connected and initialized!\n Brad-e-mon version: " + Main.versionNumber);
		sendMessage(Main.homeChannel, "My callsign is '$'.");
		sendMessage(Main.homeChannel, "Use 'commands' to get a cheat sheet.");
	}
	
	public void doMessageReceivedEvent(MessageReceivedEvent e){
		
		String input = e.getMessage().getRawContent();
		char callsign = input.charAt(0);
		String contents = input.substring(1);
		String senderName = e.getAuthor().getUsername();
		
		if(callsign != Main.callsign)
			return;
		
		switch(messageReceiveMode){
		
		case utility:
			
			doMessagesAsUtility(senderName, contents);
			break;
			
		case game:
			
			doMessagesAsGame(senderName, contents);
			break;
		}
	}
	
	public void doMessagesAsUtility(String senderName, String contents){
		
		if(isListening){
			
			if(senderName.equals(userNameListeningFor)){
				
				if(contents.equals("stoplistening")){
					
					stopListening();
				}else {
					
					switch(listenTarget){
				
					case channelChange:
					
						sendMessage(Main.homeChannel, "Attempting channel change... (If success, confirmation in new channel)");
						boolean success = changeChannel(contents);
						
						if(success){
							sendMessage(Main.homeChannel, "Success! Channel changed.");
							stopListening();
						}else {
							
							sendMessage(Main.homeChannel, "Failure! Channel not changed.");
							sendMessage(Main.homeChannel, "Still listening...");
						}
						break;
						
					case say:
						
						sendMessage(Main.homeChannel, contents);
						stopListening();
						break;
						
					case sayContinuous:
						
						sendMessage(Main.homeChannel, contents);
						break;
						
					default:
						break;
					}
				}
			}
		}else {
			
			switch(contents.toLowerCase()){
		
			case "clearchat":
			//TODO: Watch for this coming out soon.
				clearChat();
				break;
				
			case "switchmodes":
				
				if(isListening)
					stopListening();
				
				sendMessage(Main.homeChannel, "Switching modes...");
				messageReceiveMode = MessageReceiveMode.game;
				sendMessage(Main.homeChannel, "Mode is now game.");
				break;
			
			case "insultgeneralchat":
		
				sendMessage("general", "Hey nerds, bet you've never seen girls before.");
				break;
			
			case "alive":
			
				sendMessage(Main.homeChannel, "Brad-e-mon is functional.");
				break;
			
			case "diagnostics":
				
				printDiagnostics();
				break;
				
			case "This is a dumb game":
			
				sendMessage(Main.homeChannel, SpecialChatValues.angrySoldier);
				break;
				
			case "I need some cancer":
				
				sendMessage(Main.homeChannel, SpecialChatValues.souljaBoyCrankDatLyrics);
				break;
			
			case "changeoutputchannel":
				
				startListening(ListenTarget.channelChange, senderName);
				break;
			
			case "say":
				
				startListening(ListenTarget.say, senderName);
				break;
				
			case "sayuntilstoplistening":
				
				startListening(ListenTarget.sayContinuous, senderName);
				break;
				
			case "killself":
				
				sendMessage(Main.homeChannel, "Killing self...");
				goDead();
				break;
				
			case "commands":
			
				sendMessage(Main.homeChannel, "Commands are: \n 'switchmodes' \n 'insultgeneralchat' \n 'alive' \n 'clearchat' \n 'diagnostics' \n 'killself'");
				sendMessage(Main.homeChannel, "Listener commands are: \n 'changeoutputchannel' \n 'say' \n 'sayuntilstoplistening'");
				sendMessage(Main.homeChannel, "While listening, type 'stoplistening' to return to idle");
				break;	
				
			default:
				sendMessage(Main.homeChannel, "That isn't even a thing, you dusty pleb.");
				break;
			}
		}
	}
	
	public void doMessagesAsGame(String senderName, String contents){
		
		if(isListening){
			
			if(senderName.equals(userNameListeningFor)){
				
				if(contents.equals("stoplistening")){
					
					stopListening();
				}else {
					
					switch(listenTarget){
						
					case brademonGeneration:
						
						Brademon b = Brademon.generateNewBrademon(contents, senderName);
						
						if(b != null){
							
							sendMessage(Main.homeChannel, "Success! Here's your new Brad-e-mon.");
							b.printDescription();
						}else {
							
							sendMessage(Main.homeChannel, "It failed, and I have no idea why.");
						}
						
						stopListening();
						break;
						
					case brademonCreateMove:
						
						currentBrademonInEdit.addMove(contents);
						
						sendMessage(Main.homeChannel, "Named! Distribute " + currentBrademonInEdit.moveInEdit.pointsToSpend + " points between\n");
						sendMessage(Main.homeChannel, "Damage to health, damage to strength, damage to speed, damage to attack, damage to defense\n like this: '1,2,1,4,5'");
						
						startListening(ListenTarget.brademonPopulateMoveValues, userNameListeningFor);
						break;
						
					case brademonPopulateMoveValues:
						
						String[] values = contents.split(",");
						
						int damageToHealth = Integer.parseInt(values[0]);
						int damageToStrength = Integer.parseInt(values[1]);
						int damageToSpeed = Integer.parseInt(values[2]);
						int damageToAttack = Integer.parseInt(values[3]);
						int damageToDefense = Integer.parseInt(values[4]);
						
						int sum = damageToHealth + damageToStrength + damageToSpeed + damageToAttack + damageToDefense;
						BrademonMove move = currentBrademonInEdit.moveInEdit;
						
						if(sum <= move.pointsToSpend){
						
							move.damageToHealth = damageToHealth;
							move.damageToStrength = damageToStrength;
							move.damageToSpeed = damageToSpeed;
							move.damageToAttack = damageToAttack;
							move.damageToDefense = damageToDefense;
							
							currentBrademonInEdit.finalizeMove();
							
							sendMessage(Main.homeChannel, "Nice move!");
							stopListening();
							
							Brademon.serialize(currentBrademonInEdit);
							currentBrademonInEdit = null;
						}else {
							
							sendMessage(Main.homeChannel, "Too many points spent! You whore!");
						}
						break;
						
					case brademonBattleOpponentSelect:
						
						sendMessage(Main.homeChannel, "Let's begin!\n");
						
						currentBattle = new Battle(userNameListeningFor, contents);
						
						if(currentBattle.hasEnded){
							
							sendMessage(Main.homeChannel, "Battle failed! Someone doesn't have a Brad-e-mon.");
							stopListening();	
						}else{
							
							sendMessage(Main.homeChannel, "Put up your dukes, @" + contents + "!\n");
							sendMessage(Main.homeChannel, "Possible moves:\n" + currentBattle.getPossibleMoves());
							startListening(ListenTarget.brademonBattleNextMove, currentBattle.getTurn());
						}
						break;
						
					case brademonBattleNextMove:
						
						String result = currentBattle.sendCommand(userNameListeningFor, contents);
						
						if(!result.equals("error")){
								
							sendMessage(Main.homeChannel, result);
							
							if(currentBattle.hasEnded){
								
								currentBattle = null;
								stopListening();
							}else {
								
								currentBattle.switchTurn();

								sendMessage(Main.homeChannel, "Possible moves:\n" + currentBattle.getPossibleMoves());
								startListening(ListenTarget.brademonBattleNextMove, currentBattle.getTurn());
							}
						}else {
								
							sendMessage(Main.homeChannel, "That was probably not a command, or I'm programmed terribly");
						}
						break;
						
					default:
						break;
					}
				}
			}
		}else {
			
			switch(contents.toLowerCase()){
		
			case "switchmodes":
				
				if(isListening)
					stopListening();
				
				sendMessage(Main.homeChannel, "Switching modes...");
				messageReceiveMode = MessageReceiveMode.utility;
				sendMessage(Main.homeChannel, "Mode is now utility.");
				break;
				
			case "alive":
				
				sendMessage(Main.homeChannel, "Brad-e-mon is functional.");
				break;
				
			case "clearchat":
			//TODO: Watch for this coming out soon.
				clearChat();
				break;
				
			case "generatenewbrademon":
				
				sendMessage(Main.homeChannel, "If you had a Brad-e-mon already, it's received a bullet to the base of the skull!");
				sendMessage(Main.homeChannel, "Attempting to generate a new one!");
				sendMessage(Main.homeChannel, "Enter it's name:");
				
				startListening(ListenTarget.brademonGeneration, senderName);
				break;
				
			case "displaybrademon":
				
				sendMessage(Main.homeChannel, "Let me go get it...");
				Brademon toShowOff = Brademon.deserialize(senderName);
				
				if(toShowOff != null){
					
					sendMessage(Main.homeChannel, "Here it is!");
					toShowOff.printDescription();
				}else {
					
					sendMessage(Main.homeChannel, "You don't seem to have one yet");
				}
				break;
				
			case "createbrademonmove":
				
				sendMessage(Main.homeChannel, "What would you like to name the new move?");
				
				currentBrademonInEdit = Brademon.deserialize(senderName);
				startListening(ListenTarget.brademonCreateMove, senderName);
				break;
				
			case "challengesomeone":
				
				sendMessage(Main.homeChannel, "Uh oh, it's going down! \nWho will you be fighting with?");
				startListening(ListenTarget.brademonBattleOpponentSelect, senderName);
				break;
				
			case "This is a dumb game":
			
				sendMessage(Main.homeChannel, SpecialChatValues.angrySoldier);
				break;
				
			case "I need some cancer":
				
				sendMessage(Main.homeChannel, SpecialChatValues.souljaBoyCrankDatLyrics);
				break;
				
			case "diagnostics":
				
				printDiagnostics();
				break;
				
			case "killself":
				
				sendMessage(Main.homeChannel, "Killing self...");
				goDead();
				break;
				
			case "commands":
			
				sendMessage(Main.homeChannel, "Commands are: \n 'switchmodes' \n 'displaybrademon' \n 'clearchat' \n 'alive' \n 'diagnostics' \n 'killself'");
				sendMessage(Main.homeChannel, "Listener commands are: \n 'generatenewbrademon' \n 'createbrademonmove' \n 'challengesomeone'");
				sendMessage(Main.homeChannel, "While listening, type 'stoplistening' to return to idle");
				break;
				
				default:
					sendMessage(Main.homeChannel, "That isn't even a thing, you dusty pleb.");
					break;
			}
		}
	}
	
	public void sendMessage(String channelName, String content){
		
		Message message = new MessageBuilder().appendString(content).build();
		
		jda.getTextChannelsByName(channelName).get(0).sendMessage(message);
	}
	
	public void clearChat(){
		
		String clearer = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+ "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		
		sendMessage(Main.homeChannel, clearer);
	}
	
	public void startListening(ListenTarget target, String senderName){
		
		isListening = true;
		listenTarget = target;
		userNameListeningFor = senderName;
		
		sendMessage(Main.homeChannel, "LISTENING TO " + senderName + " (DESCRIPTION: " + listenTarget.description + ")...");
	}
	
	public void stopListening(){
		
		isListening = false;
		listenTarget = ListenTarget.idle;
		userNameListeningFor = "";
		
		sendMessage(Main.homeChannel, "Returning to idle.");
	}
	
	public boolean changeChannel(String newChannelName){
		
		TextChannel channel = jda.getTextChannelsByName(newChannelName).get(0);
		
		if(channel == null){
			
			return false;
		}else {
			
			Main.homeChannel = channel.getName();
			return true;
		}
	}
	
	public void printDiagnostics(){
		
		TextChannel channel = jda.getTextChannelsByName(Main.homeChannel).get(0);
		String diagnostics = "ListenTarget: " + listenTarget.name() + "\n"
								+ "Listening for: " + userNameListeningFor + "\n"
								+ "MessageReceiveMode: " + messageReceiveMode.name() + "\n"
								+ "Channel ID: " + channel.getId() + "\n"
								+ "Channel Name: " + channel.getName() + "\n"
								+ "Guild ID: " + channel.getGuild().getId() + "\n"
								+ "Guild Name: " + channel.getGuild().getName();
		
		sendMessage(Main.homeChannel, "I've been told to talk about my feelings.");
		sendMessage(Main.homeChannel, diagnostics);
		sendMessage(Main.homeChannel, "Session over.");
	}
	
	public void goLive(){
		
		shouldContinue = true;
		
		Scanner scanner = new Scanner(System.in);
		
		while(shouldContinue){
			
			String input = scanner.nextLine();
			char command = input.charAt(0);
			String contents = input.substring(1);
			
			switch(command){
			
			case 'k':
				
				System.out.println("commanded to kill");
				goDeadEasily();
				sendMessage(Main.homeChannel, "Commanded to shut down via remote control magic...");
				break;
				
			case 'c':
				
				System.out.println("commanded to chat");
				sendMessage(Main.homeChannel, contents);
				break;
				
			case 'd':
				
				System.out.println("commanded to print diagnostics");
				printDiagnostics();
				break;
				
			case 't':
				
				Brademon test = new Brademon();
				test.name = "Stupid";
				test.ownerName = "SomeMember";
				
				Brademon.serialize(test);
				System.out.println("Test file at: " + System.getProperty("user.dir"));
				break;
			}
		}
		
		jda.shutdown();
		scanner.close();
	}
	
	public void goDead(){
		
		shouldContinue = false;
		System.exit(0);
	}
	
	public void goDeadEasily(){
		
		shouldContinue = false;
	}
}
