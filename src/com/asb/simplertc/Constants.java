package com.asb.simplertc;

public class Constants {
	
	public static class CHANNEL {
		public static final String SERVER = "ws://211.188.198.45:8080/CallMe/callmews/rtcsignal";
		
		public static enum SIG_ACTION {
			DONE(10),
			CALL(11),
			RETURN(12),
			RCALL(13),
			ACK(14);
			
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
			GUM(101),
			REQUESTCALL(102),
			CONNECT(103),
			DISCONNECT(104),
			CLASSSTART(105),
			CLASSEND(106),
			FINISHCALL(107),
			REJECTCALL(108);
			
			private int value;
			
			private SIG_TYPE(int value) {
				this.value = value;
			}
			
			public int value() {
				return this.value;
			}
		};
	}
	
	public static class WEBRTC {
		public static final String SERVER = "";
		
		public static final int CALLER = 10;
		public static final int CALLEE = 11;
		
		public static enum ROLE {
			NONE(10),
			CALLER(11),
			CALLEE(12);
			
			private int value;
			
			private ROLE(int value) {
				this.value = value;
			}
			
			public int value() {
				return this.value;
			}
		};
		
		public static enum STEP {
			NONE(100),
			INIT(101),
			GUM_SUCCESS(102),
			GUM_FAILED(103),
			SIGNALING(104),
			CONNECTED(105),
			DISCONNECTED(106);
			
			private int value;
			
			private STEP(int value) {
				this.value = value;
			}
			
			public int value() {
				return this.value;
			}
		};
	}
	
}
