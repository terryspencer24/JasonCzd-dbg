package dbg;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
public class HelloController extends DbgObject {

	//@RequestMapping("/")
	public Object index() {
		//logger.info("Request received at {} on Java version {}", new java.util.Date(), getVersion());
		XYZ xyz = new XYZ();
		xyz.msg = "Digital Game Board";
		return xyz;
	}
	
	class XYZ { String msg; public String getMsg() { return msg; } public void setMsg(String msg) { this.msg = msg; } }

	/*
	private static int getVersion() {
		String version = System.getProperty("java.version");
		if (version.startsWith("1.")) {
			version = version.substring(2, 3);
		} else {
			int dot = version.indexOf(".");
			if (dot != -1) {
				version = version.substring(0, dot);
			}
		}
		return Integer.parseInt(version);
	}
	*/

}
