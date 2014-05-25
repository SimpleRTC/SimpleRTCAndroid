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
			
			public static SIG_ACTION fromValue(int value) {
				switch(value) {
				case 10:
					return SIG_ACTION.valueOf("DONE");
				case 11:
					return SIG_ACTION.valueOf("CALL");
				case 12:
					return SIG_ACTION.valueOf("RETURN");
				case 13:
					return SIG_ACTION.valueOf("RCALL");
				case 14:
					return SIG_ACTION.valueOf("ACK");
				default:
					return SIG_ACTION.valueOf("ACK");
				}
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
			
			public static SIG_TYPE fromValue(int value) {
				switch(value) {
				case 100:
					return SIG_TYPE.valueOf("NONE");
				case 101:
					return SIG_TYPE.valueOf("GUM");
				case 102:
					return SIG_TYPE.valueOf("REQUESTCALL");
				case 103:
					return SIG_TYPE.valueOf("CONNECT");
				case 104:
					return SIG_TYPE.valueOf("DISCONNECT");
				case 105:
					return SIG_TYPE.valueOf("CLASSSTART");
				case 106:
					return SIG_TYPE.valueOf("CLASSEND");
				case 107:
					return SIG_TYPE.valueOf("FINISHCALL");
				case 108:
					return SIG_TYPE.valueOf("REJECTCALL");
				default:
					return SIG_TYPE.valueOf("NONE");
				}
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
