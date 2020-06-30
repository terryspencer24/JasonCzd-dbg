Maven plugin command to execute script (maybe for docker later)....

  <plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <!--<version>1.1.1</version>-->
    <executions>
      <execution>
        <id>some-execution</id>
        <phase>install</phase>
        <goals>
          <goal>exec</goal>
        </goals>
      </execution>
    </executions>
    <configuration>
      <executable>${basedir}/dd.sh</executable>
    </configuration>
  </plugin>



