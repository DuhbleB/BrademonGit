package org.duhbleb;

import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.EventListener;

public class InputInterpreter implements EventListener {

	public void onEvent(Event e){
		
		if(e instanceof ReadyEvent)
			Brain.singleton.doReadyEvent((ReadyEvent) e);
		
		if(e instanceof MessageReceivedEvent)
			Brain.singleton.doMessageReceivedEvent((MessageReceivedEvent) e);
	}
}