package com.concurrency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainThreads {

	public static void main(String[] args) {
		Map<Integer, Boolean> maps = new HashMap<>();

		for (int i = 0; i < 2000; i++) {
			maps.put(i, false);
		}

		ExecutorService service = Executors.newFixedThreadPool(3);
		Set<Future<Map<Integer, Boolean>>> futures = new HashSet<Future<Map<Integer, Boolean>>>();

		List<ChildThread> coll = new ArrayList<>();
		coll.add(new ChildThread(extract(maps, 0, 600), false,100));
		coll.add(new ChildThread(extract(maps, 0, 600), true,100));
		coll.add(new ChildThread(extract(maps, 0, 600), false,2));

		try {
			for (int i = 0; i < 3; i++) {

				futures.add(service.submit(coll.get(i)));
			}
			List<Future<Map<Integer, Boolean>>> returnInvokes = service.invokeAll(coll);
			for (Future<Map<Integer, Boolean>> future : returnInvokes) {
				try {
					Map<Integer, Boolean> f = future.get();
					System.out.println(true);
				} catch (ExecutionException e) {
					for (Future<Map<Integer, Boolean>> F : futures) {
						F.cancel(true);
					}
				}
				
			}
		service.shutdown();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static Map<Integer, Boolean> extract(Map<Integer, Boolean> bool, int start, int end) {
		Map<Integer, Boolean> maps = new HashMap<>();

		for (int i = start; i < end; i++) {
			maps.put(i, false);
		}
		return maps;
	}

}
