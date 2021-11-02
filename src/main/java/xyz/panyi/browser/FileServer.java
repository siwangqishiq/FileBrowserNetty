package xyz.panyi.browser;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class FileServer {
    public static final String rootDir = "~";

    private int mPort = 8888;
    public FileServer(int port){
        mPort = port;
    }

    public void runServer() throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss , worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>(){
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("http-codec", new HttpServerCodec());
                        ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(512 * 1024));
                        ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                        ch.pipeline().addLast("fileServerHandler", new FileServerHandler(rootDir));
                    }
                });
        bootstrap.option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

        LogUtil.log("listen port : " + mPort);
        ChannelFuture future = bootstrap.bind(mPort).sync();
        future.channel().closeFuture().sync();

        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}
