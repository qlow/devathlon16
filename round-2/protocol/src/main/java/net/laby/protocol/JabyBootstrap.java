package net.laby.protocol;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import net.laby.protocol.packet.PacketLogin;
import net.laby.protocol.pipeline.PacketDecoder;
import net.laby.protocol.pipeline.PacketEncoder;
import net.laby.protocol.pipeline.PacketHandler;
import net.laby.protocol.pipeline.PacketLengthPrepender;
import net.laby.protocol.pipeline.PacketLengthSplitter;
import net.laby.protocol.utils.PipelineUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Class created by qlow | Jan
 */
public class JabyBootstrap {

    private static Map<Class<? extends Packet>, List<Method>> handlers = new HashMap<>();
    private static Map<Channel, JabyChannel> channels = new HashMap<>();
    private static boolean client;

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Registers the given handlers
     *
     * @param registerHandlers array of the handlers should be registered
     */
    public static void registerHandler( Class<?>... registerHandlers ) {
        int handlerCount = 0;

        // Iterating through given handlers
        for ( Class<?> handler : registerHandlers ) {

            // Iterating through methods in handlers
            for ( Method method : handler.getMethods() ) {
                // Checking if the method is static
                if ( !Modifier.isStatic( method.getModifiers() ) )
                    continue;

                // Checking for parameter-count
                if ( method.getParameterCount() != 2 )
                    continue;

                // Checking for parameter-types
                if ( !method.getParameterTypes()[0].isInstance( Packet.class )
                        || method.getParameterTypes()[1] != ChannelHandlerContext.class )
                    continue;

                Class<? extends Packet> packetClass = ( Class<? extends Packet> ) method.getParameterTypes()[0];

                // Adding an empty handlers-list to handlers if there is no handlers-list registered for the packet-class
                if ( !handlers.containsKey( packetClass ) ) {
                    handlers.put( packetClass, new ArrayList<>() );
                }

                // Adding handlers to packet-class' list
                handlers.get( packetClass ).add( method );

                // Counting handlerCount up
                handlerCount++;
            }
        }

        System.out.println( "[Jaby] Registered " + handlerCount + " handlers!" );
    }

    /**
     * Connects a client to the given ip & port
     *
     * @param ip                ip-address of the server
     * @param port              port of the server
     * @param password          password the client should connect with
     * @param clientType        type of the client that connects
     * @param bootstrapCallback callback of Bootstrap
     */
    public static void runClientBootstrap( String ip, int port, String password, PacketLogin.ClientType clientType,
                                           Consumer<Bootstrap> bootstrapCallback ) {
        // I know, that this is kinda ugly :/
        client = true;

        executorService.submit( new Runnable() {
            @Override
            public void run() {
                // Constructing Bootstrap
                final Bootstrap bootstrap = new Bootstrap()
                        .group( PipelineUtils.getEventLoopGroup( 8,
                                new ThreadFactoryBuilder().setNameFormat( "TeamLaby Thread #%d" ).build() ) )
                        .channel( PipelineUtils.getSocketChannelClass() )
                        .option( ChannelOption.CONNECT_TIMEOUT_MILLIS, 8000 )
                        .option( ChannelOption.SO_KEEPALIVE, true )
                        .handler( new ChannelInitializer<SocketChannel>() {

                            protected void initChannel( SocketChannel socketChannel ) throws Exception {
                                PacketHandler packetHandler = preparePipeline( socketChannel );

                                packetHandler.sendPacket( new PacketLogin( password, clientType ) );
                            }

                        } );

                // Connecting to given ip & port
                bootstrap.connect( ip, port );

                // "Callbacking" the Bootstrap
                bootstrapCallback.accept( bootstrap );

                // Adding shutdown hook
                Runtime.getRuntime().addShutdownHook( new Thread( new Runnable() {
                    public void run() {
                        bootstrap.group().shutdownGracefully();
                    }
                } ) );
            }
        } );
    }

    /**
     * Binds the server-bootstrap to the given port
     *
     * @param port                    port of the server
     * @param serverBootstrapConsumer callback of ServerBootstrap
     */
    public static void runServerBootstrap( int port, Consumer<ServerBootstrap> serverBootstrapConsumer ) {
        executorService.submit( new Runnable() {
            @Override
            public void run() {
                // Creating EventLoopGroups
                EventLoopGroup bossGroup = PipelineUtils.getEventLoopGroup( 8,
                        new ThreadFactoryBuilder().setNameFormat( "TeamLaby Boss Thread #%d" ).build() );
                EventLoopGroup workerGroup = PipelineUtils.getEventLoopGroup( 8,
                        new ThreadFactoryBuilder().setNameFormat( "TeamLaby Worker Thread #%d" ).build() );

                // Constructing ServerBootstrap
                ServerBootstrap serverBootstrap = new ServerBootstrap()
                        .group( bossGroup, workerGroup )
                        .channel( PipelineUtils.getServerSocketChannelClass() )
                        .childHandler( new ChannelInitializer<SocketChannel>() {

                            @Override
                            protected void initChannel( SocketChannel socketChannel ) throws Exception {
                                preparePipeline( socketChannel );
                            }

                        } )
                        .option( ChannelOption.TCP_NODELAY, true )
                        .childOption( ChannelOption.SO_KEEPALIVE, true );

                // Binding to given port
                serverBootstrap.bind( port );

                // "Callbacking" the ServerBootstrap
                serverBootstrapConsumer.accept( serverBootstrap );

                Runtime.getRuntime().addShutdownHook( new Thread( new Runnable() {
                    @Override
                    public void run() {
                        serverBootstrap.group().shutdownGracefully();
                    }
                } ) );
            }
        } );
    }

    /**
     * Prepares the pipeline for the given socket-channel
     *
     * @param socketChannel socket-channel that is initializing
     * @return packet-handler that was added to the pipeline
     */
    private static PacketHandler preparePipeline( SocketChannel socketChannel ) {
        PacketHandler packetHandler = new PacketHandler( socketChannel );
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast( new PacketLengthSplitter() );
        pipeline.addLast( new PacketDecoder() );

        pipeline.addLast( new PacketLengthPrepender() );
        pipeline.addLast( new PacketEncoder() );

        pipeline.addLast( packetHandler );

        return packetHandler;
    }

    /**
     * Map with the packet-handlers
     *
     * @return a hashmap with the packet-class as key and with a list of methods as value
     */
    public static Map<Class<? extends Packet>, List<Method>> getHandlers() {
        return handlers;
    }

    /**
     * Map with channels as key and JabyChannel as value
     *
     * @return HashMap with channels as key and JabyChannel as value
     */
    public static Map<Channel, JabyChannel> getChannels() {
        return channels;
    }

    /**
     * State whether the started Bootstrap is a ClientBootstrap
     *
     * @return true if a client was started
     */
    public static boolean isClient() {
        return client;
    }
}
