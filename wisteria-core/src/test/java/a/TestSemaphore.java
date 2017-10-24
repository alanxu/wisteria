package a;

import java.util.concurrent.Semaphore;

public class TestSemaphore {
	public static void main(String[] args) {
		Semaphore p = new Semaphore(1);
		
		Thread t2 = new Thread(new Runnable(){

			@Override
			public void run() {
				p.release();
				
			}
			
		});
		
		Thread t1 = new Thread(new Runnable(){

			@Override
			public void run() {
				
				boolean r = p.tryAcquire();
				
				System.out.println("acq1 - " + r);
				
				t2.start();
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				r = p.tryAcquire();
				
				System.out.println("acq2 - " + r);
				
			}
			
		});
		
		
		t1.start();
		
		
		
		/*while (true) {
			
			
			
			boolean r = p.tryAcquire();
			
			System.out.println("get1? - " + r);
			
			r = p.tryAcquire();
			
			System.out.println("get2? - " + r);
			
			p.release();
			
			System.out.println("released");
			
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}
}
