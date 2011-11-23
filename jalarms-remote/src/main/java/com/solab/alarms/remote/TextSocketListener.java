package com.solab.alarms.remote;

import java.io.*;
import java.net.*;

/** A very simple remote alarm listener. It reads a text line from a socket and sends an alarm with that message.
 * A line can contain an alarm source, by starting with the source name, separated from the message with a double colon,
 * for example "src::msg", or it can just be normal text, which will be sent as an alarm without specifying source.
 *
 * @author Enrique Zamudio
 */
public class TextSocketListener extends AbstractAlarmListener {

	private final int port;
    private int readTimeout = 2000;

	public TextSocketListener(int tcpPort) {
		port = tcpPort;
	}
    /** Sets the read timeout for each connection, in milliseconds. Default is 2000. */
    public void setReadTimeout(int value) {
        readTimeout = value;
    }

	public void run() {
		try {
			ServerSocket server = new ServerSocket(port);
			while (true) {
				Socket sock = server.accept();
                sock.setSoTimeout(readTimeout);
                tpool.execute(new SockReader(sock));
			}
		} catch (IOException ex) {
            log.error("Receiving connection", ex);
		}
	}

    private final class SockReader implements Runnable {
        private final Socket socket;
        private SockReader(Socket s) { socket = s; }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine();
                if (line != null) {
                    int sep = line.indexOf("::");
                    if (sep > 0) {
                        String src = line.substring(0, sep);
                        line = line.substring(sep + 2);
                        getAlarmSender().sendAlarm(line, src);
                    } else {
                        getAlarmSender().sendAlarm(line);
                    }
                }
            } catch (IOException e) {
                log.error("Reading text line from socket", e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    //Nothing left to do
                }
            }
        }
    }
}