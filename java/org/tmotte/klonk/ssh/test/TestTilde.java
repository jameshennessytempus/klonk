package org.tmotte.klonk.ssh.test;
import org.tmotte.klonk.ssh.SSHCommandLine;
import org.tmotte.klonk.ssh.SSHConnections;
import org.tmotte.klonk.ssh.SSH;
import org.tmotte.common.text.ArgHandler;
import java.io.File;

public class TestTilde {
  public static void main(String[] args) throws Exception {
    SSHCommandLine cmd=new SSHCommandLine(args); 
    SSH ssh=cmd.ssh;
    System.out.println(ssh);
    System.out.println("BAM! ~ is: "+ssh.getTildeFix());
    ssh.close();
  }
  private static void mylog(String msg) {
    System.out.println("test.FilesListing: "+msg);
  }

}