import com.typesafe.scalalogging.LazyLogging
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler, ChannelInitializer}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.protobuf.ProtobufDecoder
import io.netty.handler.codec.serialization.{ClassResolvers, ObjectDecoder, ObjectEncoder}


class Server(val port: Int) extends LazyLogging {
    def startServer() = {
      startServerLoop()
    }

    private def startServerLoop(): Unit = {
      val bossGroup = new NioEventLoopGroup()
      val workerGroup = new NioEventLoopGroup()

      try {
        val b = new ServerBootstrap()
        b.group(bossGroup, workerGroup)
          .channel(classOf[NioServerSocketChannel])
          .childHandler(new ServerChannelInitializer)

        logger.info("Binding on port {}", port.toString)
        b.bind(port).sync().channel().closeFuture().sync()

      } catch {
        case t: Throwable =>
          logger.error("Error on NodeServer", t)
      } finally {
        workerGroup.shutdownGracefully()
        bossGroup.shutdownGracefully()
      }
    }
}


class ServerChannelInitializer extends ChannelInitializer[SocketChannel] {
  override def initChannel(ch: SocketChannel) {
    ch.pipeline().addLast("frameDecoder",
      new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4))

    ch.pipeline().addLast("protobufDecoder",
      new ProtobufDecoder(Measurement.getDefaultInstance))

    ch.pipeline().addLast(new ServerHandler)
  }
}

class ServerHandler extends SimpleChannelInboundHandler[Measurement] with LazyLogging {
  override def channelRead0(ctx: ChannelHandlerContext, msg: Measurement): Unit = {
    logger.info(msg.toString)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext , cause: Throwable ) {
    ctx.close()
    logger.error("Error: ", cause)
  }
}
