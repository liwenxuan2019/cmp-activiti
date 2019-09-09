package io.cmp;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@Slf4j
public class CmpApplication implements CommandLineRunner {
	private Logger logger = LoggerFactory.getLogger(getClass());
	public static void main(String[] args) {
		SpringApplication.run(CmpApplication.class, args);
	}

	@Autowired
	private SocketIOServer socketIOServer;

	@Override
	public void run(String... strings) {
		socketIOServer.start();
		logger.info("socket.io启动成功！");
	}
}