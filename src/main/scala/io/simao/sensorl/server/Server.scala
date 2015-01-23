package io.simao.sensorl.server

import com.typesafe.scalalogging.LazyLogging
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{ChannelHandlerContext, ChannelInitializer, SimpleChannelInboundHandler}
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.protobuf.ProtobufDecoder
import io.simao.sensorl.db.MeasurementDatabase
import io.simao.sensorl.message.Measurement
import io.simao.sensorl.{LoggingReceiver, Receiver}

class Server(val port: Int,
             receiverFn: Unit ⇒ Receiver = Unit ⇒ new LoggingReceiver) extends LazyLogging {

  def startServer(): Unit = {
    val bossGroup = new NioEventLoopGroup()
    val workerGroup = new NioEventLoopGroup()

    try {
      val b = new ServerBootstrap()
      b.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .childHandler(new ServerChannelInitializer(receiverFn))

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


class ServerChannelInitializer(receiverFn: Unit ⇒ Receiver) extends ChannelInitializer[SocketChannel] {
  override def initChannel(ch: SocketChannel) {
    ch.pipeline().addLast("frameDecoder",
      new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4))

    ch.pipeline().addLast("protobufDecoder",
      new ProtobufDecoder(Measurement.getDefaultInstance))

    val receiver = receiverFn.apply()

    ch.pipeline().addLast(new ServerHandler(receiver))
  }
}

class ServerHandler(receiver: Receiver) extends SimpleChannelInboundHandler[Measurement] with LazyLogging {
  override def channelRead0(ctx: ChannelHandlerContext, msg: Measurement): Unit = {
    receiver.receive(msg)

    val db = MeasurementDatabase
      .withConnection[Unit]("jdbc:sqlite:measurements.db")({ db: MeasurementDatabase ⇒
      db.setupTables()
      db.save(msg)
    })
  }

  override def exceptionCaught(ctx: ChannelHandlerContext , cause: Throwable ) {
    ctx.close()
    logger.error("Error: ", cause)
  }
}
