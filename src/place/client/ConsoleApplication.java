package place.client;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public abstract class ConsoleApplication {
    private String[] cmdLineArgs;
    private Thread eventThread;

    public List< String > getArguments() {
        return Arrays.asList( this.cmdLineArgs );
    }

    public static void launch(
            Class< ? extends ConsoleApplication > ptuiClass
    ) {
        launch( ptuiClass, new String[ 0 ] );
    }

    public static void launch(
            Class< ? extends ConsoleApplication > ptuiClass,
            String[] args
    ) {
        try {
            ConsoleApplication ptuiApp = ptuiClass.newInstance();
            ptuiApp.cmdLineArgs = Arrays.copyOf( args, args.length );

            try {
                ptuiApp.init();
                ptuiApp.eventThread = new Thread( new Runner( ptuiApp ) );
                ptuiApp.eventThread.start();
                ptuiApp.eventThread.join();
            }
            catch( InterruptedException ie ) {
                System.err.println( "Console event thread interrupted" );
            }
            finally {
                ptuiApp.stop();
            }
        }
        catch( InstantiationException ie ) {
            System.err.println( "Can't instantiate Console App:" );
            System.err.println( ie.getMessage() );
        }
        catch( IllegalAccessException iae ) {
            System.err.println( iae.getMessage() );
        }
    }

    public void init(){}

    public void stop(){}

    private static class Runner implements Runnable {
        private final ConsoleApplication ptuiApp;

        public Runner( ConsoleApplication ptuiApp ) { this.ptuiApp = ptuiApp; }

        public void run() {
            // We don't put the PrintWriter in try-with-resources because
            // we don't want it to be closed. The Scanner can close.
            PrintWriter out = null;
            try ( Scanner consoleIn = new Scanner( System.in ) ) {
                do {
                    try {
                        out = new PrintWriter(
                                new OutputStreamWriter( System.out ), true );
                        ptuiApp.go( consoleIn, out );
                        out = null;
                    }
                    catch( Exception e ) {
                        e.printStackTrace();
                        if ( out != null ) {
                            out.println( "\nRESTARTING...\n" );
                        }
                    }
                } while ( out != null );
            }
        }
    }

    public abstract void go(Scanner consoleIn, PrintWriter consoleOut);
}
