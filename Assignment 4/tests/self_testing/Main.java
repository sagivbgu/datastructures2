/*
 * Decompiled with CFR 0.139.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class Main {
    private static String[] outTest1 = "0.3\n14\n111111_2,123456_1,donald_2,google_0,hellow_2,login_1,password_2,querty_1,starwars_2,welcome_2,zxcvbnm_2\n0.4654_0.5026\n111111_1,123456_0,donald_1,google_0,login_1,password_0,starwars_1,welcome_1,zxcvbnm_1".split("\n");

    public static void main(String[] args) {
        try {
            String workingDir = System.getProperty("user.dir");
            File outputTxt = new File("example_output.txt");
            outputTxt.delete();
            File dir = new File(workingDir);
            File[] files = dir.listFiles(new FilenameFilter(){

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".class");
                }
            });
            ArrayList<File> lstOfClassesFiles = new ArrayList<File>();
            if (files != null) {
                for (File fl : files) {
                    if (!fl.isFile()) continue;
                    lstOfClassesFiles.add(fl);
                }
            }
            for (int i = 0; i < lstOfClassesFiles.size(); ++i) {
                ((File)lstOfClassesFiles.get(i)).delete();
            }
            Process cmndPr = Runtime.getRuntime().exec("javac Runner.java");
            cmndPr.waitFor();
            Main.executeCommandLine("java Runner 32 32 2", 5000L);
            BufferedReader theirsRdr = new BufferedReader(new FileReader("example_output.txt"));
            String line = theirsRdr.readLine();
            boolean hasPassed = true;
            int lineInd = 0;
            while (line != null && hasPassed) {
                if (lineInd > 4) {
                    hasPassed = false;
                }
                if (lineInd != 3 && !outTest1[lineInd].equals(line.replace("\n", ""))) {
                    hasPassed = false;
                }
                ++lineInd;
                line = theirsRdr.readLine();
            }
            if (lineInd < 5) {
                hasPassed = false;
            }
            String toPrint = hasPassed ? "You have passed! :)" : "You have not passed";
            System.out.println(toPrint);
        }
        catch (Exception e) {
            System.out.println("something went wrong");
            e.printStackTrace();
        }
    }

    private static void executeCommandLine(String commandLine, long timeout) throws IOException, InterruptedException, TimeoutException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(commandLine);
        Worker worker = new Worker(process);
        worker.start();
        try {
            worker.join(timeout);
            if (worker.exit == null) {
                throw new TimeoutException();
            }
        }
        catch (InterruptedException ex) {
            worker.interrupt();
            Thread.currentThread().interrupt();
            throw ex;
        }
        finally {
            process.destroyForcibly();
        }
    }

    private static class Worker
    extends Thread {
        private final Process process;
        private Integer exit;

        private Worker(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            try {
                this.exit = this.process.waitFor();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
    }

}

