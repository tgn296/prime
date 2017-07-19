package com.mycompany.app;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		MyHandler handler = new MyHandler();
		server.createContext("/prime-server", handler);
		server.start();
	}

	static class MyHandler implements HttpHandler {
		Cache<String, String> cache;

		MyHandler() {
			cache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(10, TimeUnit.SECONDS).build();
		}
		

		public void handle(HttpExchange t) throws IOException {
			String att = t.getRequestURI().toString();
			String response= "";
			if(cache.asMap().keySet().contains(att)) {
				response += cache.asMap().get(att);
			}
			else {
				response += "Result: ";
				long var = getVariable(att);
				ArrayList<Long> primes = getPrime(var);
				for (long p : primes) {
					response += (" " + p);
				}
				cache.put(att, response);
			}
			
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();

		}

	}

	static long getVariable(String attribute) {
		int index = 0;
		while (attribute.charAt(index) != '=') {
			index++;
		}
		String sub = attribute.substring(index + 1);
		long value = Long.parseLong(sub);
		return value;
	}

	static ArrayList<Long> getPrime(long number) {
		ArrayList<Long> primes = new ArrayList<Long>();
		long run = 1;
		while (run <= number) {
			if (isPrime(run)) {
				primes.add(run);
			}
			run++;
		}
		return primes;
	}

	static boolean isPrime(long number) {
		if (number == 1) {
			return false;
		}
		double root = Math.sqrt(number);
		long run = 2;
		while (run <= root) {
			if (number % run == 0) {
				return false;
			} else {
				run++;
			}
		}
		return true;
	}
}
