package com.concurrency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * int submittedTasks = 5;
            for (int i=0;i< submittedTasks;i++) {
                taskCompletionService.submit(new CallableTask (
                        String.valueOf(i), 
                            (i * 10), 
                            ((i * 10) + 10  )
                        ));
               System.out.println("Task " + String.valueOf(i) + "subitted");
            }
            for (int tasksHandled=0;tasksHandled<submittedTasks;tasksHandled++) {
                try {
                    System.out.println("trying to take from Completion service");
                    Future<CalcResult> result = taskCompletionService.take();
                    System.out.println("result for a task availble in queue.Trying to get()");
                    // above call blocks till atleast one task is completed and results availble for it
                    // but we dont have to worry which one

                    // process the result here by doing result.get()
                    CalcResult l = result.get();
                    System.out.println("Task " + String.valueOf(tasksHandled) + "Completed - results obtained : " + String.valueOf(l.result));

                } catch (InterruptedException e) {
                    // Something went wrong with a task submitted
                    System.out.println("Error Interrupted exception");
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // Something went wrong with the result
                    e.printStackTrace();
                    System.out.println("Error get() threw exception");
                }
            }
        }
    }
shareimprove this answer
 * @author gaurav
 *
 */
public class CompletionMainThreads {

	public static void main(String[] args) {
		Map<Integer, Boolean> maps = new HashMap<>();

		for (int i = 0; i < 2000; i++) {
			maps.put(i, false);
		}

		ExecutorService service = Executors.newFixedThreadPool(3);
		ExecutorCompletionService<Map<Integer, Boolean>> completion  = new ExecutorCompletionService<>(service);
		
		Set<Future<Map<Integer, Boolean>>> futures = new HashSet<Future<Map<Integer, Boolean>>>();

		List<ChildThread> coll = new ArrayList<>();
		coll.add(new ChildThread(extract(maps, 0, 600), false,2000L));
		coll.add(new ChildThread(extract(maps, 0, 600), true,100L));
		coll.add(new ChildThread(extract(maps, 0, 600), false,2000L));

		
			for (int i = 0; i < 3; i++) {

				futures.add(completion.submit(coll.get(i)));
				System.out.println("Task " + String.valueOf(i) + "submitted");
			}
			for (int tasksHandled=0;tasksHandled<3;tasksHandled++) {
                try {
                    System.out.println("trying to take from Completion service");
                    Future<Map<Integer,Boolean>> result = completion.take();
                    futures.remove(result);
                    System.out.println("result for a task availble in queue.Trying to get()");
                    // above call blocks till atleast one task is completed and results availble for it
                    // but we dont have to worry which one

                    // process the result here by doing result.get()
                    Map<Integer,Boolean> l = result.get();
                    System.out.println("Task " + String.valueOf(tasksHandled) + "Completed - results obtained : " + l.entrySet().iterator().next().getValue());

                } catch (InterruptedException e) {
                	System.out.println("Error Interrupted exception");
                	for (Future<Map<Integer, Boolean>> ff : futures) {
						ff.cancel(true);
					}
                    // Something went wrong with a task submitted
                    System.out.println("Error Interrupted exception");
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // Something went wrong with the result
                	for (Future<Map<Integer, Boolean>> ff : futures) {
						ff.cancel(true);
					}
                    e.printStackTrace();
                    System.out.println("Error get() threw exception");
                }
                
            }
			service.shutdown();
	}

	private static Map<Integer, Boolean> extract(Map<Integer, Boolean> bool, int start, int end) {
		Map<Integer, Boolean> maps = new HashMap<>();

		for (int i = start; i < end; i++) {
			maps.put(i, false);
		}
		return maps;
	}

}
