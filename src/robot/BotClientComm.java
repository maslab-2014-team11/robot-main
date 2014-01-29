package robot;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import BotClient.BotClient;

public class BotClientComm implements Runnable {

	public class ImageMessage extends Message {

		private BufferedImage image;

		public ImageMessage(BufferedImage image) {
			this.image = image;
		}

		@Override
		public void send(BotClient client) {
			BufferedImage sendImage = image;
			if (image.getWidth() > 320 || image.getHeight() > 240) {
				int width = image.getWidth();
				int height = image.getHeight();
				if (width > 320) {
					height = height * 320 / width;
					width = 320;
				}
				if (height > 240) {
					width = width * 240 / height;
					height = 240;
				}
				sendImage = new BufferedImage(width, height, image.getType());
				Graphics2D g = sendImage.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.drawImage(image, 0, 0, width, height, 0, 0, image.getWidth(),
						image.getHeight(), null);
				g.dispose();
				image = sendImage;
			}
			client.sendImage(image);
		}
	}

	public class KeyValueMessage extends Message {

		private String name;
		private String value;

		public KeyValueMessage(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public void send(BotClient client) {
			client.send(FIELD, name, value);
		}
	}

	public abstract class Message {
		public abstract void send(BotClient client);
	}

	public class StopMessage extends Message {
		@Override
		public void send(BotClient client) {

		}
	}

	private static final String HOST_AND_PORT = "18.150.7.174:6667";
	private static final String TOKEN = "a0qzL4T9fq";
	private static final String FIELD = "a";

	private static BotClientComm comm;

	private final Thread thread;
	private final BotClient client;
	private final BlockingQueue<Message> outgoingMessages;

	private BotClientComm() {
		this.client = new BotClient(HOST_AND_PORT, TOKEN, true);
		this.thread = new Thread(this);
		outgoingMessages = new LinkedBlockingDeque<>();
	}

	public static BotClientComm get() {
		if (comm == null) {
			comm = new BotClientComm();
			comm.thread.start();
		}
		return comm;
	}

	public void send(String name, String value) {
		try {
			outgoingMessages.put(new KeyValueMessage(name, value));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void send(BufferedImage image) {
		while (true) {
			try {
				outgoingMessages.put(new ImageMessage(image));
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Message m = outgoingMessages.take();
				if (m instanceof StopMessage) {
					System.out.println("Stop received");
					client.close();
					return;
				}
				// Don't send old messages if there are too many newer ones
				if (outgoingMessages.size() < 5) {
					m.send(client);
					Thread.sleep(50);
				}
			} catch (InterruptedException e) {

			}
		}
	}

	public void stop() {
		while (true) {
			try {
				outgoingMessages.put(new StopMessage());
				return;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
