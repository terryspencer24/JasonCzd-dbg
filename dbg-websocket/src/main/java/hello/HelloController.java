package hello;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

    @RequestMapping("/")
    public String index() {
		System.out.println("Updated6 request received at " + new java.util.Date());
		System.out.println("Version: " + getVersion());
        return "Greetings from Websockets!";
    }

private static int getVersion() {
    String version = System.getProperty("java.version");
    if(version.startsWith("1.")) {
        version = version.substring(2, 3);
    } else {
        int dot = version.indexOf(".");
        if(dot != -1) { version = version.substring(0, dot); }
    } return Integer.parseInt(version);
}

}
