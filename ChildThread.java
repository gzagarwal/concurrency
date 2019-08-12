package com.concurrency;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

public class ChildThread implements Callable<Map<Integer,Boolean>>{
	
	Map<Integer,Boolean> maps;
	boolean exception;
	long time;
	public ChildThread(Map<Integer,Boolean> maps,boolean exception,long time) {
		this.maps=maps;
		this.exception=exception;
		this.time=time;
	}
	@Override
	public Map<Integer,Boolean> call() throws Exception {
		if(exception) {
			throw new Exception();
		}
		try {
		//Thread.sleep(time);
		for(Map.Entry<Integer,Boolean> f : maps.entrySet()) {
			f.setValue(true);
		}
		}catch(CancellationException e) {
		e.printStackTrace();
		/*}catch(InterruptedException io) {
		
			io.printStackTrace();
			if(Thread.currentThread().isInterrupted()) {
				Thread.interrupted();
			}*/
		}
		return maps;
	}
	

	
}
