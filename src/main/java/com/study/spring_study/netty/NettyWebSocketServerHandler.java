package com.study.spring_study.netty;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private final ConcurrentHashMap<Channel,Integer> map = new ConcurrentHashMap(16);


    // 当web客户端连接后，触发该方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        map.put(channel,1);
        log.info("创建连接");

    }

    // 客户端离线
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        userOffLine(ctx);
    }

    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 可能出现业务判断离线后再次触发 channelInactive
        log.warn("触发 channelInactive 掉线![{}]", ctx.channel().id());
        userOffLine(ctx);
    }

    private void userOffLine(ChannelHandlerContext ctx) {
        map.remove(ctx.channel());
        ctx.channel().close();
    }

    /**
     * 心跳检查
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 读空闲
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 关闭用户的连接
                userOffLine(ctx);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    // 处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("异常发生，异常消息 ={}" + cause.getMessage());
        ctx.channel().close();
    }

    // 读取客户端发送的请求报文
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("msg:{}",msg);



        if(msg instanceof DefaultHttpRequest){

            DefaultHttpRequest request = (DefaultHttpRequest)msg;

            log.info("DefaultHttpRequest json:{}",request);

        }else if(msg instanceof DefaultLastHttpContent){
            DefaultLastHttpContent request = (DefaultLastHttpContent)msg;

            log.info("DefaultLastHttpContent json:{}",request);
            ByteBuf content = request.content();
            byte[] b =  new byte[content.writerIndex()];
            content.readBytes(b);
            log.info("DefaultLastHttpContent content:{}",new String(b));

            // 因为经过HttpServerCodec处理器的处理后消息被封装为FullHttpRequest对象
            // 获取请求的uri
            JSONObject json = new JSONObject();
            json.put("a","1");
            // 创建完整的响应对象
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK, Unpooled.copiedBuffer(json.toJSONString(), CharsetUtil.UTF_8));
            // 设置头信息
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
            // 响应写回给客户端,并在协会后断开这个连接
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

        }else {
            ByteBuf buf = (ByteBuf) msg;

            byte[] b =  new byte[buf.writerIndex()];
            buf.readBytes(b);
            log.info("json:{}",new String(b));
        }






    }
}


