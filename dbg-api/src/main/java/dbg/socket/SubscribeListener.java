package dbg.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class SubscribeListener implements ApplicationListener<SessionSubscribeEvent> {

	@Autowired
	AbstractSubscribableChannel clientOutboundChannel;

	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		Message<byte[]> message = event.getMessage();
		StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);

		if (stompHeaderAccessor.getReceipt() != null) {
			StompHeaderAccessor receipt = StompHeaderAccessor.create(StompCommand.RECEIPT);
			receipt.setReceiptId(stompHeaderAccessor.getReceipt());
			receipt.setSessionId(stompHeaderAccessor.getSessionId());
			clientOutboundChannel.send(MessageBuilder.createMessage(new byte[0], receipt.getMessageHeaders()));
		}
	}

}