package com.open.bomberman;

import java.io.IOException;
import java.util.Arrays;

public class Launcher {

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("usage: bomberman client|server");
			System.exit(1);
		}
		if (args[0].equals("server")) {
			new ServerLauncher().run();
		} else {
			new ClientLauncher().run(Arrays.copyOfRange(args, 1, args.length));
		}
	}
}
