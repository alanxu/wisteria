package me.alanx.wisteria.core.filter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


import me.alanx.wisteria.core.protocol.BasicProtocol;
import me.alanx.wisteria.core.protocol.Message;
import me.alanx.wisteria.core.protocol.Packet;
import me.alanx.wisteria.core.session.Session;

public class FilterChain {
	
	List<Filter> filterChain = new CopyOnWriteArrayList<Filter>();
	
	FilterTarget target = null;
	
	Iterator<Filter> iterator = null;
	
	boolean ready;
	
	FilterContext context = null;
	
	public FilterChain append(Filter filter) {
		
		if(ready) {
			throw new IllegalStateException("The chain has been initialized. ");
		}
		
		this.filterChain.add(filter);
		return this;
	}
	
	public FilterContext filter(FilterContext context) {
		
		if(!ready) {
			throw new IllegalStateException("The filter chain is not initialized properly. ");
		}
		
		if (iterator.hasNext()) {
			Filter current = iterator.next();
			current.filter(context, this);
		} else if (this.target != null) {
			this.target.run(context);
		}
		
		return this.context;
	}
	
	public Message[] filter(Packet p) {
		return filter(p.getSession(), p.getBytes());
	}
	
	public Message[] filter(Session session, ByteBuffer buf) {
		if (this.context.isOutbound()) {
			throw new IllegalStateException("The filter chain is current in Outbound mode. ");
		}
		
		if(!ready) {
			throw new IllegalStateException("The filter chain is not initialized properly. ");
		}
		//buf.flip();
		this.context.setSession(session);
		this.context.setValue(buf);
		
		return this.filter(context).tryGetValue(Message[].class);
	}
	
	public ByteBuffer filter(Session session, Message buf) {
		if(!ready) {
			throw new IllegalStateException("The filter chain is not initialized properly. ");
		}
		
		if (this.context.isInbound()) {
			throw new IllegalStateException("The filter chain is current in Inbound mode. ");
		}
		
		this.context.setSession(session);
		this.context.setValue(buf);
		
		return this.filter(context).tryGetValue(ByteBuffer.class);
	}
	
	public static FilterChain start() {
		return new FilterChain();
	}
	
	public FilterChain end() {
		this.ready = true;
		this.context = new FilterContext(this);
		this.context.setInbound();
		this.iterator = this.filterChain.iterator();
		return this;
	}
	
	public FilterChain endWith(FilterTarget target) {
		this.target = target;
		end();
		return this;
	}
	
	public FilterChain forInbound() {
		return end();
	}
	
	public FilterChain forOutbound() {
		this.ready = true;
		this.context = new FilterContext(this);
		this.context.setOutbound();
		List<Filter> fs = new ArrayList<>(this.filterChain);
		Collections.reverse(fs);
		this.iterator = fs.iterator();
		return this;
	}
	

	
	public static void main(String[] args) {
		/*Filter2 f = new TestFilter(1);
		Filter2 f2 = new TestFilter(2);
		Filter2 f3 = new TestFilter(3);
		Filter2 f4 = new TestFilter(4);
		Filter2 f5 = new TestFilter(5);
		Filter2 f6 = new TestFilter(6);
		Filter2 f7 = new TestFilter(7);
		Filter2 f8 = new TestFilter(8);
		Filter2 f9 = new TestFilter(9);
		
		FilterChain chain = FilterChain.start()
			.append(f)
			.append(f2)
			.append(f3)
			.append(f4)
			.append(f5)
			.append(f6)
			.append(f7)
			.append(f8)
			.append(f9)
			.endWith(new FilterTarget(){

				@Override
				public void run(FilterContext context) {
					System.out.println("End.");
				}
				
			})
			.forOutbound();*/
		
		//chain.filter(new FilterContext());
		
		
		
		FilterChain fChain = FilterChain.start()
				.append(new GzipFilter())
				.append(new ProtocolFilter(new BasicProtocol()))
				.append(new HeartbeatHandlingFilter())
				.endWith(new FilterTarget(){
					@Override
					public void run(FilterContext context) {
						if(context.isInbound()) {
							Message[] m = context.getValue();
							System.out.println("received: " + m[0]);
						}else{
							ByteBuffer b = context.getValue();
							System.out.println("received: bytes, size:" + b.limit());
							
							context.getChain().forInbound().filter(new Packet(null, b));
						}
						
					}
				}).forOutbound();
		
		Message m = new Message() {
			public String toString() {
				return "test filte message";
			}
		};
		
		fChain.filter(null, m);
		
		
		
	}
	
}

class TestFilter extends Filter {
	
	int i;
	
	public TestFilter(int i) {
		super();
		this.i = i;
	}

	@Override
	protected boolean filterInbound(FilterContext context) {
		System.out.println(i);
		return true;
	}

	@Override
	protected boolean filterOutbound(FilterContext context) {
		System.out.println(-i);
		return true;
	}

	
	
}
