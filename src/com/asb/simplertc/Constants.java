package com.asb.simplertc;

public class Constants {
	
	public static class CHANNEL {
		public static final String SERVER = "ws://211.188.198.45:8080/CallMe/callmews/rtcsignal";
		
		public static enum SIG_ACTION {
			DONE(10),
			CALL(10),
			RETURN(10),
			RCALL(10),
			ACK(10);
			
			private int value;
			
			private SIG_ACTION(int value) {
				this.value = value;
			}
			
			public int value() {
				return this.value;
			}
		};
		
		public static enum SIG_TYPE {
			NONE(100),
			GUM(100),
			REQUESTCALL(100),
			CONNECT(100),
			DISCONNECT(100),
			CLASSSTART(100),
			CLASSEND(100),
			FINISHCALL(100),
			REJECTCALL(100);
			
			private int value;
			
			private SIG_TYPE(int value) {
				this.value = value;
			}
			
			public int value() {
				return this.value;
			}
		};
	}
	
}
