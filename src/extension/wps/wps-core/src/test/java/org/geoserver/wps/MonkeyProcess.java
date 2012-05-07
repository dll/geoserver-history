package org.geoserver.wps;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.geoserver.wps.jts.AnnotatedBeanProcessFactory;
import org.geoserver.wps.jts.DescribeParameter;
import org.geoserver.wps.jts.DescribeResult;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.ProcessException;
import org.geotools.process.ProcessFactory;
import org.geotools.util.SimpleInternationalString;
import org.opengis.util.ProgressListener;

@org.geoserver.wps.jts.DescribeProcess(title = "Monkey", description = "Process used to test asynch calls")
public class MonkeyProcess {

    enum CommandType {
        Exit, SetProgress, Exception
    }

    static Map<String, BlockingQueue<Command>> commands = new ConcurrentHashMap<String, BlockingQueue<MonkeyProcess.Command>>();

    private static class Command {
        CommandType type;

        Object value;

        public Command(CommandType type, Object value) {
            this.type = type;
            this.value = value;
        }

    }

    public static void exit(String id, SimpleFeatureCollection value, boolean wait) throws InterruptedException {
        getCommandQueue(id).offer(new Command(CommandType.Exit, value));
        if(wait) {
            while(getCommandQueue(id).size() > 0) {
                Thread.sleep(10);
            }
        }
    }

    private synchronized static BlockingQueue<Command> getCommandQueue(String id) {
        BlockingQueue<Command> queue = commands.get(id);
        if(queue == null) {
            queue = new LinkedBlockingQueue<MonkeyProcess.Command>();
            commands.put(id, queue);
        }
        
        return queue;
    }

    public static void progress(String id, float progress, boolean wait) throws InterruptedException {
        getCommandQueue(id).offer(new Command(CommandType.SetProgress, progress));
        if(wait) {
            while(getCommandQueue(id).size() > 0) {
                Thread.sleep(10);
            }
        }

    }

    public static void exception(String id, ProcessException exception, boolean wait) throws InterruptedException {
        getCommandQueue(id).offer(new Command(CommandType.Exception, exception));
        if(wait) {
            while(getCommandQueue(id).size() > 0) {
                Thread.sleep(10);
            }
        }
    }
    
    @DescribeResult(name="result")
    public SimpleFeatureCollection execute(@DescribeParameter(name = "id") String id, ProgressListener listener) throws Exception {
        while (true) {
            Command command = getCommandQueue(id).take();
            if (command.type == CommandType.Exit) {
                commands.remove(id);
                return (SimpleFeatureCollection) command.value;
            } else if (command.type == CommandType.SetProgress) {
                listener.progress(((Number) command.value).floatValue());
            } else {
                ProcessException exception = (ProcessException) command.value;
                listener.exceptionOccurred(exception);
                throw exception;
            }
        }
    }

    static final ProcessFactory getFactory() {
        return new AnnotatedBeanProcessFactory(new SimpleInternationalString("Monkey process"),
                "gs", MonkeyProcess.class);
    }
}
